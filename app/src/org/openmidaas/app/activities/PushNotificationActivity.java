/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.openmidaas.app.activities;

import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.services.GCMIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PushNotificationActivity extends AbstractActivity {
	public static final String SENDER_ID= Settings.GCM_SENDER_ID;
	private Button btnPositive;
	private Button btnClear;
	String phoneNumber;
	public static final String ACTION_MSG = "org.openmidaas.app.action.receivedMessageGCM";
	
	PhoneNumberUtil phoneUtil;
	PhoneNumber phoneParsedNumber;
	Boolean isPhoneValid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_register);
		isPhoneValid = false;
		
		//Get number from the SIM card if present
		phoneNumber = getPhoneNumberFromSIM(); 
		//Set it to EditText if not null
		if (phoneNumber!=null){
			phoneNumber = phoneNumber.replace("+", "");
			View tv = findViewById(R.id.edPushActivity);
			((EditText)tv).setText(phoneNumber);
		}
		
		btnPositive = (Button)findViewById(R.id.btnOkayPushPhone);
		btnPositive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isPhoneValid = false;
				//Get the Number on click from EditText
				View tv = findViewById(R.id.edPushActivity);
				phoneNumber = ((EditText)tv).getText().toString();
				
				if ((phoneNumber==null) || (phoneNumber.isEmpty())){
					DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Error", "Phone Number cannot be empty.");
				}else{
					//Hide the keyboard
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					
					//parse the number to check the validity
					phoneUtil = PhoneNumberUtil.getInstance();
					try {
						String phoneTest;
						if (phoneNumber.contains("+")){
							phoneTest = phoneNumber;
						}else{
							phoneTest = "+"+phoneNumber;
						}
						phoneParsedNumber = phoneUtil.parse(phoneTest, null);
						isPhoneValid = phoneUtil.isValidNumber(phoneParsedNumber); // returns true or false
					} catch (NumberParseException e) {
						Logger.debug(getClass(), "NumberParseException was thrown: " + e.toString());
					}
					
					if(isPhoneValid == true){ //valid phone
					
						// save phone number in shared preference
						SharedPreferences.Editor editor = getSharedPreferences("phone", MODE_PRIVATE).edit();
						editor.putString("phoneNumberPush", phoneNumber);
						editor.commit();
						
						try{
							/*Help to make sure the device is ready for using GCM,
							including whether or not it has the Google Services Framework*/
							GCMRegistrar.checkDevice(PushNotificationActivity.this);
							// Check internet connection
							if (isNetworkAvailable() == false){
						    	Logger.debug(getClass(), "Not connected to internet. Failed Registration.");
						    	DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Registration Failed", "Active internet connection is required for registering.");
						    	
						    }else{

						    	// Register Local Broadcast Listener to receive messages.
						    	// We are registering an observer (mMessageReceiver) to receive Intents
						    	// with actions named "custom-event-name".
						    	Logger.debug(getClass(), "Registering the receiver");
						    	LocalBroadcastManager.getInstance(PushNotificationActivity.this).registerReceiver(mMessageReceiver,
					    	      new IntentFilter(GCMIntentService.ACTION_MSG_FROM_GCM_BROADCAST));
						    	
						    	//Takes the sender ID and registers app to be able to receive messages sent by that sender. ID received in a callback in BroadCastReceiver
						    	GCMRegistrar.register(PushNotificationActivity.this, SENDER_ID);
						    	
						    	//Dialog started here and would be dismissed when its registered on server or there is an error.
						    	mProgressDialog.setTitle("Please Wait");
						    	mProgressDialog.setMessage("Registering your number for push notification service...");
						    	mProgressDialog.setCancelable(false);
						    	mProgressDialog.show();
						    	
						    }
					    }catch(RuntimeException e){
					    	//Device incompatibility message
					    	Logger.debug(getClass(), e + " Device is incompatible for using GCM services.");
					    	DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Registration Failed", "Unable to register to push message service. Device is incompatible for using GCM services.");			   
					    }
						
					}else{
						//Invalid phone number message
						Logger.debug(getClass(), "Invalid phone number entered for push registration.");
						DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Invalid Phone Number", "Please enter a valid international phone number. Make sure you've entered the country code followed by phone number.");
					}
				    
				}
			}
		});
        
		btnClear = (Button)findViewById(R.id.btnClearPushPhone);
		btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Clear the text to set it to default
				View ed = findViewById(R.id.edPushActivity);
		        ((EditText)ed).setText("");
			}
		});
        
		
	}
	
	// Handler for received Intents. This will be called whenever an Intent
	// with specified action names is broadcasted.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  Bundle extras = intent.getExtras();
		  if (extras != null){
			  if(intent.getAction().equals(GCMIntentService.ACTION_MSG_FROM_GCM_BROADCAST)){
				  //Dismiss the dialog if showing
				  if (mProgressDialog.isShowing())
					  mProgressDialog.dismiss();
				  //Unregister the receiver
				  Logger.debug(getClass(), "Unregistering the receiver for local broadcast");
				  LocalBroadcastManager.getInstance(PushNotificationActivity.this).unregisterReceiver(mMessageReceiver);
				  Logger.debug(getClass(), "Got message through Local Broadcast : " + extras.getString("message"));
				  //Show TOAST with message
				  DialogUtils.showToast(PushNotificationActivity.this, extras.getString("message"));
			  }
		  }
	  }
	};
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private String getPhoneNumberFromSIM(){
		TelephonyManager mTelephonyMgr;  
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);   
		String yourNumber = mTelephonyMgr.getLine1Number(); 
		return yourNumber;
	}
	
	@Override
	protected String getTitlebarText() {
		return ("Push Notification Registration");
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.push_register);
	}
	
}
