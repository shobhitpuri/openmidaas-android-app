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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;

public class PushNotificationActivity extends AbstractActivity {
	public static final String SENDER_ID= Settings.GCM_SENDER_ID;
	private Button btnPositive;
	private Button btnClear;
	String phoneNumber;
	public static ProgressDialog dialog;
	public static final String ACTION_MSG = "org.openmidaas.app.action.receivedMessageGCM";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_register);
		
        //Get number from the SIM card if present
        phoneNumber = getPhoneNumberFromSIM(); 
        //Set it to EditText if not null
		if (phoneNumber!=null){
			View tv = findViewById(R.id.edPushActivity);
			((EditText)tv).setText(phoneNumber);
		}
		
		btnPositive = (Button)findViewById(R.id.btnOkayPushPhone);
        btnPositive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Get the Number on click from EditText
				View tv = findViewById(R.id.edPushActivity);
				phoneNumber = ((EditText)tv).getText().toString();
				
				if ((phoneNumber==null) || (phoneNumber.isEmpty())){
					DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Error", "Phone Number cannot be empty.");
				}else{
					// save phone number in shared preference
					SharedPreferences.Editor editor = getSharedPreferences("phone", MODE_PRIVATE).edit();
					editor.putString("phoneNumberPush", phoneNumber);
					editor.commit();

					//Hide the keyboard
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					
					//Dialog started here and would be dismissed when its registered on server 
					dialog = new ProgressDialog(PushNotificationActivity.this);
					dialog.setTitle("Please Wait");
					dialog.setMessage("Registering your number for GCM Push...");
			        dialog.show();
			        
			        /*Help to make sure the device is ready for using GCM,
				    including whether or not it has the Google Services Framework*/
				    try{
				    	GCMRegistrar.checkDevice(PushNotificationActivity.this);
				    }catch(RuntimeException e){
				    	//Device incompatibility message
					    Logger.debug(getClass(), e + " Device is incompatible for using GCM services.");
					    DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Registration Failed", "Unable to register to push message service. Device is incompatible for using GSM services.");
					   
				    }
				    
				    // Check internet connection
				    if (isNetworkAvailable() == false){
				    	Logger.debug(getClass(), "Not connected to internet. Failed Registration.");
				    	DialogUtils.showNeutralButtonDialog(PushNotificationActivity.this, "Registration Failed", "Active internet connection is required for registering.");
				    	
				    }
				    //Takes the sender ID and registers app to be able to receive messages sent by that sender. ID received in a callback in BroadCastReceiver
				    GCMRegistrar.register(PushNotificationActivity.this, SENDER_ID);
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