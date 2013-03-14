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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EmailRegistrationActivity extends AbstractAttributeRegistrationActivity {

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtnCompleteAttributeVerification = (Button)findViewById(R.id.btnCompleteEmailVerification);
		mBtnCompleteAttributeVerification.setEnabled(false);
		mBtnStartAttributeVerification.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAttributeVerification(new Object());
			}
		});
		
		mBtnCompleteAttributeVerification.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				completAttributeVerification(new Object());
			}
		});
	}

	@Override
	protected int getUIElementForAttributeValue() {
		return (R.id.etEmailAddress);
	}

	@Override
	protected int getUIElementForAttributeVerification() {
		return (R.id.etVerificationCode);
	}

	@Override
	protected boolean isAttributeValid() {
		if(mAttributeValue.getText().toString().isEmpty() || mAttributeValue.getText().toString() == null) {
			return false;
		}
		return true;
	}

	@Override
	protected int getUIElementForStartAttributeVerification() {
		return (R.id.btnStartEmailVerification);
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.attribute_view);
	}
	
	@Override
	protected void completAttributeVerification(Object object) {
		
	}
}
