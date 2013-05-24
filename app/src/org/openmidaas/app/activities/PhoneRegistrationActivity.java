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
	
	private EditText etPhoneCountryCode;
	private EditText etPhoneNumberLocal;
	private EditText etPhoneVerificationCode;
	
	private Spinner spPhoneNumberTypeLabel;
	private RadioGroup rgChooseVerifyMethod;
	private RadioButton tempRadioButton;
	private Button btnPhoneStartVerification;
	private Button btnPhoneCompleteVerification;
	
	private ScrollView svPhoneRegister;
	
	private boolean isInitVerificationSuccess = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		svPhoneRegister = (ScrollView) findViewById(R.id.svPhoneVerification);
		etPhoneCountryCode = (EditText)findViewById(R.id.phCountryCode);
		etPhoneNumberLocal = (EditText)findViewById(R.id.phNumber);
		etPhoneVerificationCode = (EditText)findViewById(R.id.edPhVerifyCode);

		spPhoneNumberTypeLabel = (Spinner)findViewById(R.id.spPhoneType);
		rgChooseVerifyMethod = (RadioGroup)findViewById(R.id.rgVerifyTypeRadio);
		rgChooseVerifyMethod.check(R.id.rBtnVerifySms);
		
		btnPhoneStartVerification = (Button)findViewById(R.id.btnPhoneStartVerify);
		btnPhoneCompleteVerification = (Button)findViewById(R.id.btnPhoneCompleteVerify);
		btnPhoneCompleteVerification.setEnabled(false);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		btnPhoneStartVerification.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				try {
					startVerifyingAttruibutes();
				} catch (IllegalArgumentException e) {
					DialogUtils.showNeutralButtonDialog(PhoneRegistrationActivity.this, "Error", e.getMessage());
				}
			}
		});
		
		btnPhoneCompleteVerification.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Logger.info(getClass(), "completing phone verification...");
				mProgressDialog.setMessage("Verifying...");
				mProgressDialog.show();
				completeVerifyingAttruibutes();
				
			}
		});
	}
	
	private void startVerifyingAttruibutes() throws IllegalArgumentException {
		String phoneNumberValue;
		Logger.info(getClass(), "starting phone verification");
		try {
			/*
			 *  If user asks to re-verify, delete the existing attributes and restart the process.
			 *  (as user may change things like verification method and other values when on same activity)
			 *  Throws MIDaas exception
			 */
			 
			if(isInitVerificationSuccess) {
				phoneAttribute.delete(); 
			}
			
			phoneAttribute = PhoneAttributeFactory.createAttribute();
			
			//Check County Code is NULL and Empty
			if(etPhoneCountryCode.getText().toString() == null || etPhoneCountryCode.getText().toString().isEmpty()) {
				throw new IllegalArgumentException ("Country Code cannot be empty");
			}
			Logger.debug(getClass(), "CountryCode not empty");
			
			//Check given number NULL and Empty
			if(etPhoneNumberLocal.getText().toString() == null || etPhoneNumberLocal.getText().toString().isEmpty()) {
				throw new IllegalArgumentException ("Phone Number cannot be empty");
			}
			Logger.debug(getClass(), "Given Number not empty");
			
			//Check the label. Set it if Not 'None'
			if(spPhoneNumberTypeLabel.getSelectedItemPosition() != 0 && (!(spPhoneNumberTypeLabel.getSelectedItem().equals("None")))) {
				phoneAttribute.setLabel(spPhoneNumberTypeLabel.getSelectedItem().toString());
				Logger.debug(getClass(), "Label is: "+spPhoneNumberTypeLabel.getSelectedItem().toString());
			}
			
			//Check method of verification and set accordingly
			tempRadioButton = (RadioButton)findViewById(rgChooseVerifyMethod.getCheckedRadioButtonId());
			if(tempRadioButton.getText().toString().compareToIgnoreCase("Phone Call") == 0){ 
				phoneAttribute.setVerificationMethod(VERIFICATION_METHOD.call.toString());
				Logger.debug(getClass(), "Method of verification : "+VERIFICATION_METHOD.call.toString());
			}
			//Else set "sms" as default method 
			else{ 
				phoneAttribute.setVerificationMethod(VERIFICATION_METHOD.sms.toString());
				Logger.debug(getClass(), "Method of verification : "+VERIFICATION_METHOD.sms.toString());
			}
			
			//Construct a phone number in E-164 standard format
			phoneNumberValue = "+"+etPhoneCountryCode.getText().toString()+etPhoneNumberLocal.getText().toString();

			//Set the value of attribute
			phoneAttribute.setValue(phoneNumberValue); //throws InvalidAttribException
			Logger.debug(getClass(), "Setting value of Phone Number : "+phoneNumberValue);

			//saving the attribute before starting verification
			//throws MIDaasException & InvalidAttributeException
			phoneAttribute.save(); 
			
			//Start waiting dialog	
			mProgressDialog.show();
			
			//Start verification of Attribute with AVS
			phoneAttribute.startVerification(new InitializeVerificationCallback() {
				@Override
				public void onSuccess() {
					// Make the flag as true on success
					isInitVerificationSuccess = true;
					Logger.info(getClass(), "phone verification started successfully");
					
					if(phoneAttribute.getVerificationMethod().compareToIgnoreCase(VERIFICATION_METHOD.call.toString()) == 0){
						DialogUtils.showToast(mActivity, "You should receive a phone call soon at : "+phoneAttribute.getValue().toString());
						Logger.debug(getClass(), "Call sent");
					}else{
						DialogUtils.showToast(mActivity, "A SMS has been sent to: "+phoneAttribute.getValue().toString());
						Logger.debug(getClass(), "SMS Sent");
					}
					
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
							btnPhoneCompleteVerification.setEnabled(true);
							btnPhoneStartVerification.setText("Re-start Verification");
							svPhoneRegister.scrollTo(0, svPhoneRegister.getBottom());
							etPhoneVerificationCode.requestFocus();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); 
							etPhoneVerificationCode.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS  //Hiding the dictionary suggestions as not needed 
																| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
																| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
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
		}catch (final InvalidAttributeValueException e) {
			Logger.debug(getClass(), "Invalid Attribute Error: "+e.getMessage() );
			cancelCurrentProgressDialog();
			DialogUtils.showNeutralButtonDialog(mActivity, "Error" ,"The phone number you entered is invalid.");
		}catch (MIDaaSException e) {
			Logger.debug(getClass(), "Middas Error: "+e.getError().getErrorMessage() );
			cancelCurrentProgressDialog();
			DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getError().getErrorMessage());
		}
		
	}

	private void completeVerifyingAttruibutes(){
		
		if((!(etPhoneVerificationCode.getText().toString().isEmpty())) || (etPhoneVerificationCode.getText().toString() != null))
			phoneAttribute.completeVerification(etPhoneVerificationCode.getText().toString(), new CompleteVerificationCallback() {
			
			@Override
			public void onSuccess() {
				cancelCurrentProgressDialog();
				DialogUtils.showToast(mActivity, phoneAttribute.getName()+" "+getString(R.string.verification_success_tag));
				
				try {
					phoneAttribute.save();
				} catch (MIDaaSException e) {
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getError().getErrorMessage());
				} catch (InvalidAttributeValueException e) {
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
				}
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
