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
