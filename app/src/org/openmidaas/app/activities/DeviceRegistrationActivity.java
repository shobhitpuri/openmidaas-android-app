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

import org.openmidaas.app.App;
import org.openmidaas.app.Intents;
import org.openmidaas.app.R;
import org.openmidaas.app.common.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class DeviceRegistrationActivity extends AbstractActivity {
	
	private TextView tvRegistrationStatus;
	
	private BroadcastReceiver mRegistrationCompleteReceiver =  new BroadcastReceiver() {

		@Override
		public void onReceive(Context content, Intent intent) {
			if(intent.getIntExtra(App.REGISTRATION_STATUS, App.REGISTRATION_ERROR) == App.REGISTRATION_ERROR) {
				Logger.debug(DeviceRegistrationActivity.class, "Registration unsuccessful!");
				tvRegistrationStatus.setText(getString(R.string.registration_error_text));
			} else {
				Logger.debug(DeviceRegistrationActivity.class, "Registration successful!");
				tvRegistrationStatus.setText(getString(R.string.registration_success_text));
			}
		}
	};
	
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Logger.info(this.getClass(), "activity created");
		tvRegistrationStatus = (TextView)findViewById(R.id.tvRegistrationStatus);
		if(App.getInstance().isRegistered()) {
			// show registration done!
			// TODO: Navigate to home screen after this.
			tvRegistrationStatus.setText(getString(R.string.registration_already_present_text));
		} else {
			tvRegistrationStatus.setText(getString(R.string.registering_text));
			registerOnDeviceRegistrationReceiver();
			App.getInstance().register();
			
		}	
	}
	
	/**
	 * Helper method to register the broadcast receiver to handle the registration complete
	 * intent.
	 */
	private void registerOnDeviceRegistrationReceiver() {
		IntentFilter registrationCompleteIntentFilter = new IntentFilter( Intents.DEVICE_REGISTRATION_COMPLETE );
		this.registerReceiver( mRegistrationCompleteReceiver , registrationCompleteIntentFilter );
	}
	
	@Override
	public void onPause() {
		this.unregisterReceiver(mRegistrationCompleteReceiver);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		registerOnDeviceRegistrationReceiver();
		super.onResume();
	}

	@Override
	public int getLayoutResourceId() {
		return (R.layout.registration_view);
	}
}
