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

import org.openmidaas.app.Settings;
import org.openmidaas.app.activities.PushNotificationActivity;
import org.openmidaas.app.activities.SplashActivity;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.SessionManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * This IntentService will get control when messages arrive for your app, or other
 * GCM-related events occur.
 */

public class GCMIntentService extends GCMBaseIntentService {
	
	public static final String ACTION_MSG_FROM_GCM_BROADCAST = "org.openmidaas.app.action.gcm.somemessage";
	
	public GCMIntentService() {
		super(PushNotificationActivity.SENDER_ID);
	}
	
	/*called when a registration request that was kicked
	off with a call to register() on GCMRegistrar has completed. You are
	handed the registration ID.*/
	@Override
	protected void onRegistered(Context ctxt, String regId) {
		Logger.debug(getClass(), "onRegistered: " + regId);
		Log.d(TAG, "Device registered: regId= " + regId);
		// Get phone number from preferences
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("phone", MODE_PRIVATE); 
		String phone = prefs.getString("phoneNumberPush", "");
		Logger.debug(getClass(), "Phone Number which would be registered on Server: "+phone);
		// call a Web service here probably to send the Registration ID 
		if(!phone.isEmpty()){
			sendToServer(phone, regId);
		}
		
	}
	
	//Send the number and ID to server
	void sendToServer(String phone, String regId){
		final RequestParams param = new RequestParams();
		param.put("mobile_no", phone);
        param.put("gcm_id", regId);
        
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				AsyncHttpClient clientPost = new AsyncHttpClient();
        		clientPost.addHeader("Content-Type", "application/x-www-urlform-encoded");
        		
        		clientPost.post(Settings.PUSH_REGISTRATION_SERVER_URL, param, new AsyncHttpResponseHandler(){
        		    @Override
        		    public void onSuccess(int statusCode, String content) {
        		        super.onSuccess(statusCode, content);
        		        // Send a Local BroadCast 
        		        GCMIntentService.this.sendLocalBroadcastMessage("Registration Successsful");
        		        Logger.debug(getClass(), "Registration ID Successfully send to 3rd party server");
        		    }
        		      
        		    @Override
        		    public void onFailure(Throwable error, String content) {
        			    super.onFailure(error, content);
        			    GCMIntentService.this.sendLocalBroadcastMessage("Server Error: Error in sending Registration ID to 3rd party server");
        			    Log.e(TAG, error + ". Error in sending Registration ID to 3rd party server. ");
        			}

        		});
				
			}
		}).start();
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

	@Override
	protected void onMessage(Context context, Intent message) {
		Log.d(TAG,"Received push message");
		Bundle extras=message.getExtras();
	    for (String key : extras.keySet()) {
	    	Logger.debug(getClass(), "Received key: " +key +" and value as: "+extras.getString(key));
	    	if (key.equals("url")){
	    		if (!extras.getString(key).isEmpty() && extras.getString(key)!=null){
	    			Log.d(TAG,"Received key as 'url' and value as "+extras.getString(key));
	    			Intent intent = new Intent(getBaseContext(), SplashActivity.class);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    		intent.setAction(SplashActivity.ACTION_MSG_CUSTOM);
		    		intent.addCategory(Intent.CATEGORY_DEFAULT);
		    		intent.putExtra("url", extras.getString(key));
		    		//Check for the lock before continuing
			    	if (SessionManager.getBusyness() == false){
			    		getApplication().startActivity(intent);
			    	}else{
			    		Log.d(TAG,"Received push message but won't process the URL as session is locked.");
			    	}
		    		
	    		}else{
	    			Log.d(TAG,"Received push message but vaue of key:\"url\" is empty or null. ");
	    		}
	    	}
	    }
	    
	}
	
	/*called if there is some unrecoverable error. You are passed
	a String that is the error message, that you can log somewhere, or put in a
	Notification.*/
	@Override
	protected void onError(Context ctxt, String errorMsg) {
		Logger.error(getClass(), "onError: " + errorMsg);
		Log.e(TAG, errorMsg);
		//Send a Local Broadcast to tell about the error 
		sendLocalBroadcastMessage(errorMsg);
	}
	
	/*Called if there is some problem that GCM will automatically handle (e.g., network connectivity  problem).
	 *If onRecoverableError() returns true, it means Android is welcome to retry the operation as it sees fit. 
	 */
	@Override
	protected boolean onRecoverableError(Context ctxt, String errorMsg) {
		Logger.error(getClass(), "onRecoverableError: " + errorMsg);
		Log.e(TAG, "onRecoverableError: " + errorMsg);
		//Send a Local Broadcast to tell about the error
		sendLocalBroadcastMessage(errorMsg);
	    return(true);
	}
	
	// Send an Intent with an action named "custom-event-name". The Intent sent should 
	// be received by the ReceiverActivity.
	private void sendLocalBroadcastMessage(String message) {
	  Logger.debug(getClass(), " Broadcasting message: "+message);
	  Intent intent = new Intent(ACTION_MSG_FROM_GCM_BROADCAST );
	  intent.putExtra("message", message);
	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
}
