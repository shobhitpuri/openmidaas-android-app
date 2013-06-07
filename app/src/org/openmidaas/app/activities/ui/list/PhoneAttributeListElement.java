/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.app.activities.ui.list;


import org.openmidaas.app.R;
import org.openmidaas.app.common.AttributeRegistrationHelper;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Intents;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.PhoneAttribute.VERIFICATION_METHOD;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneAttributeListElement extends AbstractAttributeListElement {

	public PhoneAttributeListElement() {
	}
	
	@Override
	public void onTouch(Activity activity) {
		if(mAttribute.getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION) {
			DialogUtils.showCodeCollectionDialog(activity, mAttribute);
		}
	}

	@Override
	public void onLongTouch(final Activity activity) {
		
		final String message = "Name: " + mAttribute.getName() + "\n" +
				 "Value: " + mAttribute.toString(); 
		
		//Defined Layout for Dialog box 
		final LinearLayout llChooseMethod = new LinearLayout (activity);
		LinearLayout .LayoutParams lpChooseMethod = new LinearLayout.LayoutParams(LinearLayout .LayoutParams.WRAP_CONTENT, LinearLayout .LayoutParams.WRAP_CONTENT); 
		llChooseMethod.setLayoutParams(lpChooseMethod);
		llChooseMethod.setOrientation(LinearLayout.VERTICAL);
		
		//Adding heading for choosing method
		TextView tvMethodHeading = new TextView(activity);
		tvMethodHeading.setText("Choose method to re-verify:");
		tvMethodHeading.setTextAppearance(activity, android.R.attr.textAppearanceMedium);
		tvMethodHeading.setTextColor(Color.BLACK);
		tvMethodHeading.setPadding(30, 0, 0, 0);
		
		//Adding TextView to Layout 
		llChooseMethod.addView(tvMethodHeading);
		
		//Adding radio buttons to choose the method
		RadioButton rbPhoneCall = new RadioButton(activity);
		rbPhoneCall.setId(0);
		rbPhoneCall.setText(activity.getResources().getString(R.string.radioButtonPhoneVerifyText));
		RadioButton rbSms = new RadioButton(activity);
		rbSms.setId(1);
		rbSms.setText(activity.getResources().getString(R.string.radioButtonSMSVerifyText));		
		
		//Adding the radio buttons to button group
		final RadioGroup rgMethod = new RadioGroup(activity);
		rgMethod.addView(rbPhoneCall, 0);
		rgMethod.addView(rbSms, 1);
		rgMethod.check(1);
		rgMethod.setOrientation(0);
		rgMethod.setPadding(20, 0, 0, 0);
		
		//Adding RadioGroup to Layout
		llChooseMethod.addView(rgMethod);
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(activity)
			    .setTitle(activity.getResources().getString(R.string.title_phone_dialog_box))
			    .setMessage(message)
			    .setView(llChooseMethod)
			    .setNegativeButton(activity.getResources().getString(R.string.reVerifyButtonText), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(mAttribute.getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION || mAttribute.getState() == ATTRIBUTE_STATE.NOT_VERIFIED) {
							if (rgMethod.getCheckedRadioButtonId()==0){
								mAttribute.setVerificationMethod(VERIFICATION_METHOD.call.toString());
								AttributeRegistrationHelper.verifyAttribute(activity, "Starting "+mAttribute.getName() +" verification...", "You should receive a phone call soon at : "+getRenderedAttributeValue(), mAttribute);
								
							}else{
								mAttribute.setVerificationMethod(VERIFICATION_METHOD.sms.toString());
								AttributeRegistrationHelper.verifyAttribute(activity, "Starting "+mAttribute.getName() +" verification...", "Verification code sent to "+getRenderedAttributeValue()+" via SMS", mAttribute);
							}
						}else if(mAttribute.getState() == ATTRIBUTE_STATE.VERIFIED){
							Toast.makeText(activity, "Attribute already verified", Toast.LENGTH_LONG).show();
						}else if(mAttribute.getState() == ATTRIBUTE_STATE.NOT_VERIFIABLE){
							Toast.makeText(activity, "Attribute is non verifiable", Toast.LENGTH_LONG).show();
						}else if(mAttribute.getState() == ATTRIBUTE_STATE.UNKNOWN){
							Toast.makeText(activity, "Attribute state is unknown. Can't re-verify", Toast.LENGTH_LONG).show();
						}else if(mAttribute.getState() == ATTRIBUTE_STATE.ERROR_IN_SAVE){
							Toast.makeText(activity, "Attribute has not been saved. Error re-verifying", Toast.LENGTH_LONG).show();
						}
					} 
				})
			    .setPositiveButton(activity.getResources().getString(R.string.deleteButtonText),  new DialogInterface.OnClickListener() {
			    	
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							mAttribute.delete();
						
							activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
						} catch (MIDaaSException e) {
							
						}
					}
			    })	
			     .show();
			}
			
		});
		
	}
	
	@Override
	public String getRenderedAttributeValue() {
		return mAttribute.getValue().toString();
	}
	

}
