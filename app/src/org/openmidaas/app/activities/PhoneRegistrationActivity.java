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
package org.openmidaas.app.activities;

import org.openmidaas.app.R;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.PhoneAttribute;
import org.openmidaas.library.model.PhoneAttribute.VERIFICATION_METHOD;
import org.openmidaas.library.model.PhoneAttributeFactory;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.InitializeVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

public class PhoneRegistrationActivity extends AbstractActivity{
	private Activity mActivity;	
	private PhoneAttribute phoneAttribute;
	
	private EditText phoneCountryCode;
	private EditText phoneNumberLocal;
	private EditText phoneVerificationCode;
	
	private Spinner phoneNumberTypeLabel;
	private RadioGroup phChooseMethod;
	private RadioButton tempRadioButton;
	private Button phStartVerifyButton;
	private Button phCompleteVerifyButton;
	
	private ScrollView svPhoneRegister;
	
	private boolean isInitVerificationSuccess = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		svPhoneRegister = (ScrollView) findViewById(R.id.svPhoneVerification);
		phoneCountryCode = (EditText)findViewById(R.id.phCountryCode);
		phoneNumberLocal = (EditText)findViewById(R.id.phNumber);
		phoneVerificationCode = (EditText)findViewById(R.id.edPhVerifyCode);

		phoneNumberTypeLabel = (Spinner)findViewById(R.id.spPhoneType);
		phChooseMethod = (RadioGroup)findViewById(R.id.rgVerifyTypeRadio);
		phChooseMethod.check(R.id.rBtnVerifySms);
		phStartVerifyButton = (Button)findViewById(R.id.btnPhoneStartVerify);
		phCompleteVerifyButton = (Button)findViewById(R.id.btnPhoneCompleteVerify);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		phStartVerifyButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				try {
					startVerifyingAttruibutes();
				} catch (IllegalArgumentException e) {
					DialogUtils.showNeutralButtonDialog(PhoneRegistrationActivity.this, "Error", e.getMessage());
				} catch (InvalidAttributeValueException e) {
					DialogUtils.showNeutralButtonDialog(PhoneRegistrationActivity.this, "The entered data is not valid.", e.getMessage());
				} catch (MIDaaSException e) {
					DialogUtils.showNeutralButtonDialog(PhoneRegistrationActivity.this, "Error", e.getError().getErrorMessage());
				}
			}
		});
		
		phCompleteVerifyButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Logger.info(getClass(), "completing phone verification...");
				mProgressDialog.setMessage("Verifying...");
				mProgressDialog.show();
				completeVerifyingAttruibutes();
				
			}
		});
	}
	
	private void startVerifyingAttruibutes() throws IllegalArgumentException, InvalidAttributeValueException, MIDaaSException {
		String phoneNumberValue;
		Logger.info(getClass(), "starting phone verification");
		try {
			if(!isInitVerificationSuccess) {
				phoneAttribute = PhoneAttributeFactory.createAttribute();
				
				//Check County Code is NULL and Empty
				if(phoneCountryCode.getText().toString() == null || phoneCountryCode.getText().toString().isEmpty()) {
					throw new IllegalArgumentException ("Country Code cannot be empty");
				}
				Logger.debug(getClass(), "CountryCode not empty");
				//Check given number NULL and Empty
				if(phoneNumberLocal.getText().toString() == null || phoneNumberLocal.getText().toString().isEmpty()) {
					throw new IllegalArgumentException ("Phone Number cannot be empty");
				}
				Logger.debug(getClass(), "Given Number not empty");
				//Check the label. Set it if Not 'None'
				if(phoneNumberTypeLabel.getSelectedItemPosition() != 0 && (!(phoneNumberTypeLabel.getSelectedItem().equals("None")))) {
					phoneAttribute.setLabel(phoneNumberTypeLabel.getSelectedItem().toString());
					Logger.debug(getClass(), "Label is: "+phoneNumberTypeLabel.getSelectedItem().toString());
				}
				
				//Check method of verification
				tempRadioButton = (RadioButton)findViewById(phChooseMethod.getCheckedRadioButtonId());
				if(tempRadioButton.getText().toString().compareToIgnoreCase("Phone Call") == 0){ // set "Call" as method 
					phoneAttribute.setVerificationMethod(VERIFICATION_METHOD.call.toString());
					Logger.debug(getClass(), "Method of verification : "+VERIFICATION_METHOD.call.toString());
				}else{ //set "sms" as method (default)
					phoneAttribute.setVerificationMethod(VERIFICATION_METHOD.sms.toString());
					Logger.debug(getClass(), "Method of verification : "+VERIFICATION_METHOD.sms.toString());
				}
				
				//Construct a phone number in E-164 standard format
				phoneNumberValue = "+"+phoneCountryCode.getText().toString()+phoneNumberLocal.getText().toString();
				//Save and Check for validity of the number(else will throw Invalid Attribute Exception)
				phoneAttribute.setValue(phoneNumberValue);
				Logger.debug(getClass(), "Setting value of Phone Number : "+phoneNumberValue);
			}
			mProgressDialog.show();
			phoneAttribute.startVerification(new InitializeVerificationCallback() {
			
				@Override
				public void onSuccess() {
					Logger.info(getClass(), "phone verification started successfully");
					if(phoneAttribute.getVerificationMethod().compareToIgnoreCase(VERIFICATION_METHOD.call.toString()) == 0){
						DialogUtils.showToast(mActivity, "You should receive a phone call soon at : "+phoneAttribute.getValue().toString());
						Logger.debug(getClass(), "Call sent");
					}else{
						DialogUtils.showToast(mActivity, "A SMS has been sent to: "+phoneAttribute.getValue().toString());
						Logger.debug(getClass(), "SMS Sent");
					}
					
					isInitVerificationSuccess = true;
					try {
						phoneAttribute.save();
						Logger.debug(getClass(), "Attribute Saved");
					} catch (MIDaaSException e) {
						DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getError().getErrorMessage());
						Logger.debug(getClass(), "Middas Error: "+e.getError().getErrorMessage() );
					} catch (InvalidAttributeValueException ex) {
						DialogUtils.showNeutralButtonDialog(mActivity, "Error", ex.getMessage());
						Logger.debug(getClass(), "Invalid Attribute Error: "+ex.getMessage() );
					}
					
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (mProgressDialog.isShowing()) {
								Logger.debug(getClass(), "Dismissing Dialog: ");
								mProgressDialog.dismiss();
							}
							phCompleteVerifyButton.setEnabled(true);
							phStartVerifyButton.setText("Re-start Verification");
							svPhoneRegister.scrollTo(0, svPhoneRegister.getBottom());
							phoneVerificationCode.requestFocus();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
							phoneVerificationCode.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
						}
						
					});
				}

				@Override
				public void onError(MIDaaSException exception) {
					cancelCurrentProgressDialog();
					Logger.info(getClass(), "error in start phone verification");
					Logger.info(getClass(), exception.getError().getErrorMessage());
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", exception.getError().getErrorMessage());
				}
			
			});
		} catch (final InvalidAttributeValueException e) {
			Logger.debug(getClass(), "Invalid Attribute Error: "+e.getMessage() );
			cancelCurrentProgressDialog();
			DialogUtils.showNeutralButtonDialog(mActivity, "Error" ,"The phone you entered is invalid");
		} 
	}

	private void completeVerifyingAttruibutes(){
		
		if((!(phoneVerificationCode.getText().toString().isEmpty())) || (phoneVerificationCode.getText().toString() != null))
			phoneAttribute.completeVerification(phoneVerificationCode.getText().toString(), new CompleteVerificationCallback() {
			
			@Override
			public void onSuccess() {
				DialogUtils.showToast(mActivity, phoneAttribute.getName()+" "+getString(R.string.verification_success_tag));
				PhoneRegistrationActivity.this.finish();
			}

			@Override
			public void onError(MIDaaSException exception) {
				cancelCurrentProgressDialog();
				DialogUtils.showNeutralButtonDialog(mActivity, "Error", exception.getError().getErrorMessage());
			}
		});
	}
	
	
	@Override
	protected String getTitlebarText() {
		return ("Add a phone number");
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.phone_number_registration);
	}

	private void cancelCurrentProgressDialog() {
		this.runOnUiThread(new Runnable() {
			

			@Override
			public void run() {
				if (mProgressDialog != null) {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				}
			}
		});
	}

}
