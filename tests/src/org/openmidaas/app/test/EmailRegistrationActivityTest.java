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

import org.openmidaas.app.activities.DeviceRegistrationActivity;
import org.openmidaas.app.activities.EmailRegistrationActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.test.ActivityUnitTestCase;
import android.test.ViewAsserts;
import android.widget.Button;

public class EmailRegistrationActivityTest extends ActivityUnitTestCase<EmailRegistrationActivity>{
	
	private EmailRegistrationActivity mActivity; 
	
	private Button startVerification; 
	
	public EmailRegistrationActivityTest(String name) {
		super(EmailRegistrationActivity.class);	
		setName(name);
	}
	
	public void setUp() throws Exception {
		 super.setUp();
		 startActivity(new Intent(getInstrumentation().getTargetContext(),
		    		DeviceRegistrationActivity.class), null, null);
		 mActivity = getActivity();
		 startVerification = (Button) mActivity.findViewById(org.openmidaas.app.R.id.btnStartAttributeVerification);
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testLayout() {
		assertNotNull(mActivity.findViewById(org.openmidaas.app.R.id.btnStartAttributeVerification));
		assertNotNull(mActivity.findViewById(org.openmidaas.app.R.id.btnCompleteAttributeVerification));
		assertNotNull(mActivity.findViewById(org.openmidaas.app.R.id.tvStartVerificationInfo));
	}
	
	public void testStartAttributeVerificationButtonText() {
		assertEquals(startVerification.getText().toString(), 
				getInstrumentation().getContext().getString(org.openmidaas.app.test.R.string.btnStartVerification));
	}
	
	
	protected Resources getResources(String packageName) throws NameNotFoundException {
	    PackageManager pm = getInstrumentation().getTargetContext().getPackageManager();
	    return pm.getResourcesForApplication(packageName);
	}
	
}
