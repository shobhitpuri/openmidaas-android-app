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
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.common.Utils;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.model.core.InitializationCallback;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends AbstractActivity {
	
	private TextView tvRegistrationStatus;
	private ProgressBar pb;
	Intent intent;
	private TextView tvVersionNumber;
	Boolean startedByPush;
	String url;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Logger.info(this.getClass(), "activity created");
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		tvRegistrationStatus = (TextView)findViewById(R.id.tvRegistering);
		startedByPush = false;
		url="";
		// register the app or check to see if already registered.
		MIDaaS.setLoggingLevel(Settings.LIBRARY_LOG_LEVEL);
		
		// Make sure the progress bar is visible
        pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        
        //Set the version number
		String versionNumber = Utils.getVersionNumber(getApplicationContext());
		tvVersionNumber = (TextView)findViewById(R.id.tvVersion);
		tvVersionNumber.setText("Version: "+versionNumber);
		
		//See if intent is coming from a service with message to process the URL
	    handleIntent(getIntent());
	    
	    try {
			MIDaaS.initialize(this, Settings.SERVER_URL,new InitializationCallbackImpl());
		} catch (URISyntaxException e) {
			DialogUtils.showNeutralButtonDialog(this, "Error", "Server URL appears to be invalid.");
		}
	}

	private void handleIntent(Intent intent)
	{
	    Bundle extras = intent.getExtras();
	    if (extras != null)
	    {
	    	if(intent.getAction().equals(Constants.IntentActionMessages.PROCESS_URL)){
	    		//Flag would be used pass extra parameters to process the URL in MainActivity
            	startedByPush = true;
            	url = extras.getString("url");
	    	}
	    }
	}
	private void registrationComplete() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tvRegistrationStatus.setText("");
				pb.setVisibility(View.GONE);
				Intent intent = new Intent(SplashActivity.this, MainTabActivity.class);
				if (startedByPush){
					//To detect if MainActivity would need to process the extra parameters
		    		intent.setAction(Constants.IntentActionMessages.PROCESS_URL);
		    		//Takes care of the scenario if activity is background. 
		    		//Instead of starting another on top of it, it starts a new one. 
		    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    		intent.addCategory(Intent.CATEGORY_DEFAULT);
		    		intent.putExtra("url", url);
		    		startActivity(intent);
				}else{
					startActivity(intent);	
				}
				SplashActivity.this.finish();
			}
		});
		
	}
	
	private void showError() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
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
			SplashActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvRegistrationStatus.setText("Registering...");
				}
			});
		}
	}
	
	@Override
	public int getLayoutResourceId() {
		return (R.layout.welcome_splash_screen);
	}

	@Override
	protected String getTitlebarText() {
		return ("");
	}
}
