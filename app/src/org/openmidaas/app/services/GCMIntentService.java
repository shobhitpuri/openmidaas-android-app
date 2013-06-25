/**
  Copyright 2013 SecureKey Technologies Inc.
   
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
   
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package org.openmidaas.app.services;

import org.openmidaas.app.activities.MainTabActivity;
import org.openmidaas.app.activities.SplashActivity;
import org.openmidaas.app.common.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * This IntentService will get control when messages arrive for your app, or other
 * GCM-related events occur.
 */
	
public class GCMIntentService extends GCMBaseIntentService {
	public GCMIntentService() {
		super(SplashActivity.SENDER_ID);
	}
	  
	  
	/*called when a registration request that was kicked
	off with a call to register() on GCMRegistrar has completed. You are
	handed the registration ID.*/
	@Override
	protected void onRegistered(Context ctxt, String regId) {
		Logger.debug(getClass(), "onRegistered: " + regId);
		Log.d(TAG, "Device registered: regId= " + regId);
		// call a Web service here probably to send the Registration ID 
	}
	
	/*called sometime after you call unregister() on
	GCMRegistrar to indicate that your app no longer wishes to receive GCM
	messages from your server*/
	@Override
	protected void onUnregistered(Context ctxt, String regId) {
		Logger.debug(getClass(), "onUnregistered: " + regId);
	}
	
	/*called with a message that your server sent to your app.
	The message arrives in the form of an Intent object. The key/value pairs
	that your server declares to be the message will arrive as Intent extras on
	that Intent.*/

	
	protected void onMessage(Context context, Intent message) {
		Log.d(TAG,"Received push message");
		Bundle extras=message.getExtras();
	    for (String key : extras.keySet()) {
	    	if (key.equals("url")){
	    		Log.d(TAG,"Received key as url and value as "+extras.getString(key));
	    		Intent intent = new Intent(getBaseContext(), MainTabActivity.class);
	    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		intent.setAction(MainTabActivity.ACTION_MSG_CUSTOM);
	    		intent.addCategory(Intent.CATEGORY_DEFAULT);
	    		intent.putExtra("url", extras.getString(key));
	    		getApplication().startActivity(intent);
	    	}
	    }
	    
	}
	
	/*called if there is some unrecoverable error. You are passed
	a String that is the error message, that you can log somewhere, or put in a
	Notification, or whatever makes sense*/
	@Override
	protected void onError(Context ctxt, String errorMsg) {
		Logger.debug(getClass(), "onError: " + errorMsg);
		Log.d(TAG, errorMsg);
	}
	
	/*Called if there is some problem that GCM will 
	automatically handle (e.g., network connectivity  problem).*/
	@Override
	protected boolean onRecoverableError(Context ctxt, String errorMsg) {
		Logger.debug(getClass(), "onRecoverableError: " + errorMsg);
	    return(true);
	}
}
