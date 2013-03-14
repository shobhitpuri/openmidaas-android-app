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
package org.openmidaas.app.test;

import org.openmidaas.app.App;
import org.openmidaas.app.activities.DeviceRegistrationActivity;
import org.openmidaas.app.common.Logger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

public class DeviceRegistrationActivityTest extends ActivityUnitTestCase<DeviceRegistrationActivity>{

	public DeviceRegistrationActivityTest(String name) {
		super(DeviceRegistrationActivity.class);	
		setName(name);
	}
	
	 public void setUp() throws Exception {
		 super.setUp();
		 startActivity(new Intent(getInstrumentation().getTargetContext(),
		    		DeviceRegistrationActivity.class), null, null);
		 
	}
	
	 public void tearDown() throws Exception {
		 super.tearDown();
	 }
	 
	public void testLayout() {
		DeviceRegistrationActivity activity = getActivity();
		assertNotNull(activity.findViewById(org.openmidaas.app.R.id.tvRegistrationStatus));
	}
}
