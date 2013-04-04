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
import org.openmidaas.app.common.UINotificationUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public abstract class AbstractAttributeRegistrationActivity extends AbstractActivity {
	
	protected EditText mAttributeValue;
	
	protected EditText mAttributeVerificationCode;
	
	protected Button mBtnStartAttributeVerification;
	
	protected Button mBtnCompleteAttributeVerification;
	
	private TextView mInitVerificationHelperText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAttributeValue = (EditText)findViewById(R.id.etAttributeValue);
		mAttributeVerificationCode = (EditText)findViewById(R.id.etVerificationCode);
		mBtnStartAttributeVerification = (Button)findViewById(R.id.btnStartAttributeVerification);
		mBtnCompleteAttributeVerification = (Button)findViewById(R.id.btnCompleteAttributeVerification);
		mInitVerificationHelperText = (TextView)findViewById(R.id.tvStartVerificationInfo);
		mAttributeValue.setInputType(getAttributeInputType());
		mInitVerificationHelperText.setText(getInitVerificationHelpText());
		mBtnCompleteAttributeVerification.setEnabled(false);
		mBtnStartAttributeVerification.setOnClickListener(new View.OnClickListener() {
			
			@Override	
			public void onClick(View v) {
				if(isAttributeValid()) {
					startAttributeVerification();
				} else {
					UINotificationUtils.showNeutralButtonDialog(AbstractAttributeRegistrationActivity.this, "Error", getString(R.string.missing_attribute_value_text));
				}
			}
		});
		
		mBtnCompleteAttributeVerification.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				completeAttributeVerification();
			}
		});
	} 
	
	@Override
	protected int getLayoutResourceId() {
		return (R.layout.attribute_registration_view);
	}
	
	public abstract int getAttributeInputType();
	
	/**
	 * Override this to return your custom help text to display in the initialize
	 * attribute verification stage. 
	 * @return custom help text
	 */
	protected String getInitVerificationHelpText() {
		return (getString(R.string.tvDefaultStartVerificationInfoText));
	}
	
	/**
	 * Override this to return a custom label displayed to the user telling them what
	 * attribute they need to enter. e.g, email, phone number. 
	 * @return
	 */
	protected String getInitVerificationAttributeLabelText() {
		return (getString(R.string.tvDefaultAttributeName));
	}
	
	/**
	 * Returns true if the attribute is valid. For example, an email 
	 * should be of the format; foo@bar.com
	 * @return - true if the attribute is valid, false otherwise. 
	 */
	protected abstract boolean isAttributeValid();
	
	/**
	 * Method that creates the attribute and starts the attribute 
	 * verification process.
	 */
	protected abstract void startAttributeVerification();
	
	/**
	 * Method that completes the attribute verification.
	 */
	protected abstract void completeAttributeVerification();
}
