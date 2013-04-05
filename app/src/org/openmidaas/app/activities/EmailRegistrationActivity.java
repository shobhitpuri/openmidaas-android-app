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
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.model.AttributeFactory;
import org.openmidaas.library.model.EmailAttribute;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.InitializeVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;


public class EmailRegistrationActivity extends AbstractAttributeRegistrationActivity {

	private Activity mActivity;
	
	private EmailAttribute emailAttribute;
	
	private boolean isInitVerificationSuccess = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
	}

	@Override
	protected void startAttributeVerification() {
		Logger.info(getClass(), "starting email verification");
			try {
				if(!isInitVerificationSuccess) {
					emailAttribute = AttributeFactory.getEmailAttributeFactory().createAttributeWithValue(mAttributeValue.getText().toString());
				}
				emailAttribute.startVerification(new InitializeVerificationCallback() {

					@Override
					public void onSuccess() {
						Logger.info(getClass(), "email verification started successfully");
						UINotificationUtils.showToast(mActivity, "An email has been sent to: "+mAttributeValue.getText().toString());
						isInitVerificationSuccess = true;
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mProgressDialog.cancel();
								mBtnCompleteAttributeVerification.setEnabled(true);
								mBtnStartAttributeVerification.setText("Re-send email");
								mAttributeVerificationCode.requestFocus();
							}
							
						});
					}

					@Override
					public void onError(MIDaaSException exception) {
						cancelCurrentProgressDialog();
						Logger.info(getClass(), "error in start email verification");
						Logger.info(getClass(), exception.getError().getErrorMessage());
						UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", exception.getError().getErrorMessage());
					}
				
				});
			} catch (final InvalidAttributeValueException e) {
				cancelCurrentProgressDialog();
				Logger.error(getClass(), e.getMessage());
				UINotificationUtils.showNeutralButtonDialog(mActivity, "Error" ,e.getMessage());
			} catch(MIDaaSException ex) {
				cancelCurrentProgressDialog();
				Logger.error(getClass(), ex.getError().getErrorMessage());
				UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", ex.getError().getErrorMessage());
			}
			
	}


	
	
	@Override
	protected void completeAttributeVerification() {
		if((!(mAttributeVerificationCode.getText().toString().isEmpty())) || (mAttributeVerificationCode.getText().toString() != null))
		emailAttribute.completeVerification(mAttributeVerificationCode.getText().toString(), new CompleteVerificationCallback() {

			@Override
			public void onSuccess() {
				UINotificationUtils.showToast(mActivity, emailAttribute.getName()+" "+getString(R.string.verification_success_tag));
				startActivity(new Intent(EmailRegistrationActivity.this, AttributeListActivity.class));
				EmailRegistrationActivity.this.finish();
			}

			@Override
			public void onError(MIDaaSException exception) {
				cancelCurrentProgressDialog();
				UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", exception.getError().getErrorMessage());
			}
		});
	}
	
	@Override
	protected String getProgressDialogMessage() {
		return (getString(R.string.sendingEmailText));
	}

	@Override
	protected boolean isAttributeValid() {
		if(mAttributeValue.getText().toString().isEmpty() || mAttributeValue.getText().toString() == null) {
			return false;
		}
		return true;
	}

	@Override
	public int getAttributeInputType() {
		// set the attribute value collection UI element to type email so that
		// a keyboard with email-specific characters is displayed. 
		return (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
	}

	@Override
	protected String getTitlebarText() {
		return ("Add an email");
	}
	
	protected boolean hasTitlebarButtonVisible() { 
		return false;
	}
	
	
}
