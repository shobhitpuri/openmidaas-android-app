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

package org.openmidaas.app;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {
	public static final String	REGISTERED_KEY_NAME = "org.openmidaas.app.REGISTERED";
	public static final String REGISTRATION_STATUS = "org.openmidaas.app.REGISTRATION_STATUS";
	private static App mInstance;
	
	public static final int REGISTRATION_SUCCESS = 1;
	public static final int REGISTRATION_ERROR = 0;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}
	
	/**
	 * Return a single instance of the application
	 * @return
	 */
	public static synchronized App getInstance() {
		return mInstance;
	}
	
	/**
	 * Check to see if app is registered
	 * @return
	 */
	public boolean isRegistered() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isRegistered = true;
		if (prefs.contains(REGISTERED_KEY_NAME)) {
			isRegistered = true;
		}
		else {
			isRegistered = false;
		}
		return isRegistered;
	}
	
	/**
	 * Register's the app and sends a broadcast intent to denote completion
	 */
	public void register() {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean(REGISTERED_KEY_NAME, true);
		editor.commit();
		this.getApplicationContext().sendBroadcast(new Intent().setAction(Intents.DEVICE_REGISTRATION_COMPLETE).putExtra(REGISTRATION_STATUS, REGISTRATION_SUCCESS));
	}
}
