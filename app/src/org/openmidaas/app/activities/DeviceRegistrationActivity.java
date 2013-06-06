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

/**
 * This activity handles device registration
 */

import java.net.URISyntaxException;

import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.model.core.InitializationCallback;
import org.openmidaas.library.model.core.MIDaaSException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DeviceRegistrationActivity extends AbstractActivity {
	
	private TextView tvRegistrationStatus;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Logger.info(this.getClass(), "activity created");
		tvRegistrationStatus = (TextView)findViewById(R.id.tvRegistrationStatus);
		// register the app or check to see if already registered.
		MIDaaS.setLoggingLevel(Settings.LIBRARY_LOG_LEVEL);
		mProgressDialog.show();
		try {
			MIDaaS.initialize(this, Settings.SERVER_URL,new InitializationCallbackImpl());
		} catch (URISyntaxException e) {
			DialogUtils.showNeutralButtonDialog(this, "Error", "Server URL appears to be invalid.");
		}
	}
	
	

	private void registrationComplete() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				startActivity(new Intent(DeviceRegistrationActivity.this, HomeScreen.class));
				DeviceRegistrationActivity.this.finish();
			}
		});
		
	}
	
	private void showError() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				tvRegistrationStatus.setText(getString(R.string.registration_error_text));
			}
			
		});
	}
	
	private class InitializationCallbackImpl implements InitializationCallback {

		@Override
		public void onError(MIDaaSException arg0) {
			showError();
		}

		@Override
		public void onSuccess() {
			registrationComplete();
		}

		@Override
		public void onRegistering() {
			DeviceRegistrationActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mProgressDialog.setMessage("Registering...");
				}
			});
		}
	}
	
	@Override
	public int getLayoutResourceId() {
		return (R.layout.device_registration_view);
	}

	@Override
	protected String getTitlebarText() {
		return ("Home");
	}
}
