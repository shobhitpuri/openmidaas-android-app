/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.app.session;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;
import org.openmidaas.app.session.attributeset.AttributeSetFactory;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.MIDaaS.VerifiedAttributeBundleCallback;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;

import android.net.ParseException;

public class Session implements VerifiedAttributeBundleCallback{
	
	private final String ACR = "acr";
	
	private final String CLIENT_ID = "client_id";
	
	private final String ATTRIBUTES = "attrs";
	
	private final String STATE = "state";
	
	private final String RETURN = "return";
	
	private final String RETURN_METHOD = "method";
	
	private final String RETURN_URL = "url";
	
	private final String TYPE = "type";
	
	private final String LABEL = "label";
	
	private final String ESSENTIAL = "essential";
	
	private final String VERIFIED = "verified";
	
	private int mAcrLevel = 1;
	
	private String mClientId;
	
	private String mState = null;
	
	private String mVerifiedResponse = null;
	
	private String mUnverifiedResponse = null;
	
	private List<AbstractAttributeSet> mAttributeListSet;
	
	private Map<String, AbstractAttribute<?>> mVerifiedAttributeMap;
	
	private Map<String, AbstractAttribute<?>> mUnverifiedAttributeMap;
	
	private ReturnStrategy mReturnStrategy = null;
	
	private OnDoneCallback mOnDoneCallback = null;
	
	public Session() {
		mAttributeListSet = new ArrayList<AbstractAttributeSet>();
		mVerifiedAttributeMap = new HashMap<String, AbstractAttribute<?>>();
		mUnverifiedAttributeMap = new HashMap<String, AbstractAttribute<?>>();
	}
	
	/**
	 * Sets the data from the request
	 * Note: Make sure this method call is thread safe
	 * @param requestObject
	 * @throws JSONException
	 */
	public synchronized void setRequestData(JSONObject requestObject) throws JSONException, AttributeRequestObjectException  {
		JSONObject attrRequest;
		if(requestObject == null) {
			
			Logger.error(getClass(), "The requestObject parameter is null. ");
			throw new AttributeRequestObjectException("The requestObject parameter is null.");
		}
		// we need to the keys listed below to proceed. Check to see if they exist. 
		if((!(requestObject.has(CLIENT_ID))) || (!(requestObject.has(ATTRIBUTES))) || (!(requestObject.has(RETURN)))) {
			Logger.error(getClass(), "clientId, attrs, and/or return keys are missing in the request");
			throw new AttributeRequestObjectException("clientId, attrs, and/or return keys are missing in the request");
		}
			
		// we need values for the keys listed below to proceed. Check to see if they exist. 
		if(requestObject.isNull(CLIENT_ID) || requestObject.isNull(ATTRIBUTES) || requestObject.isNull(RETURN)) {
			Logger.error(getClass(), "clientId, attrs, and/or return values are missing in the request");
			throw new AttributeRequestObjectException("clientId, attrs, and/or return values are missing in the request");
		}
		if(!(requestObject.isNull(ACR))) {
			mAcrLevel = requestObject.getInt(ACR);
		}
		// we need to check whether the return object has the correct keys
		if(!(requestObject.getJSONObject(RETURN).has(RETURN_METHOD))) {
			Logger.error(getClass(), "Return object has no return method.");
			throw new AttributeRequestObjectException("Return object has no return method.");
		}
		if(!(requestObject.getJSONObject(RETURN).has(RETURN_URL))) {
			Logger.error(getClass(), "Return object has no return url.");
			throw new AttributeRequestObjectException("Return object has no return url.");
		}
		
		// check if "attrs" value is actually a JSONObject
		if (!(requestObject.get(ATTRIBUTES) instanceof JSONObject)) {
			Logger.error(getClass(), "The value for \"attrs\" is not of type JSONObject.");
			throw new AttributeRequestObjectException("The value for \"attrs\" is not of type JSONObject.");
		}
		attrRequest = requestObject.getJSONObject(ATTRIBUTES);
		mClientId = requestObject.getString(CLIENT_ID);
		JSONObject returnObject = requestObject.getJSONObject(RETURN);
		mReturnStrategy = ReturnStrategyFactory.getStrategyForMethodName(returnObject.getString(RETURN_METHOD));
		if(mReturnStrategy == null) {
			Logger.error(getClass(), "There is no return method for " + returnObject.getString(RETURN_METHOD));
			throw new AttributeRequestObjectException("There is no return method for " + returnObject.getString(RETURN_METHOD));
		}
		try {
			mReturnStrategy.setReturnUrl(returnObject.getString(RETURN_URL));
		} catch (URISyntaxException e) {
			Logger.error(getClass(), "The return url appears to be an invalid url");
			throw new AttributeRequestObjectException("The return url appears to be an invalid url");
		}
		
		if(!(requestObject.isNull(STATE))) {
			mState = requestObject.getString(STATE);
		}
		
		Iterator<?> keys = requestObject.getJSONObject(ATTRIBUTES).keys();
		// parsing through the "attrs" field now.
		mAttributeListSet.clear();
		while(keys.hasNext()) {
			String key = (String)keys.next();
			if(attrRequest.get(key) != null) {
				if(attrRequest.get(key) instanceof JSONObject) {
					createAttributeSet(key, attrRequest.getJSONObject(key));
				} else {
					Logger.error(getClass(), "The value for the key in \"attrs\" is not of type JSONObject.");
					throw new AttributeRequestObjectException("The value for the key in \"attrs\" is not of type JSONObject.");
				}
			} else {
				Logger.error(getClass(), "The value for key: " + key + " is null");
				throw new AttributeRequestObjectException("The value for key: " + key + " is null");
			}
		}
	}
	
	
	/**
	 * Returns an attribute set for the request. 
	 * Note: Make sure this method is thread safe. 
	 * @return
	 * @throws AttributeFetchException 
	 */
	public synchronized List<AbstractAttributeSet> getAttributeSet() throws AttributeFetchException {
		Logger.debug(getClass(), "Fetching attributes from library");
		for(AbstractAttributeSet attributeSet: mAttributeListSet) {
			attributeSet.fetch();
		}
		return mAttributeListSet;
	}
	
	/**
	 * Helper method to create an attribute set
	 * @param key
	 * @param attributeItem
	 * @throws JSONException
	 * @throws ParseException
	 */
	private void createAttributeSet(String key, JSONObject attributeItem) throws JSONException, AttributeRequestObjectException {
		Logger.debug(getClass(), "Creating attribute set");
		String type = null;
		if(attributeItem.has(TYPE)) {
			if(!(attributeItem.isNull(TYPE))) {
				type = attributeItem.getString(TYPE);
			} else {
				Logger.error(getClass(), "Missing value for \"type\"");
				throw new AttributeRequestObjectException("Missing value for \"type\"");
			}
		} else {
			if(key != null) {
				type = key;
			} else {
				Logger.error(getClass(), "Key is null or not specified.");
				throw new AttributeRequestObjectException("Key is null or not specified.");
			}
		}
		AbstractAttributeSet attributeSet = AttributeSetFactory.getAttributeSetForType(type);
		attributeSet.setKey(key);
		if (attributeItem.has(ESSENTIAL) && !(attributeItem.isNull(ESSENTIAL))) {
			attributeSet.setEssentialRequested(attributeItem.getBoolean(ESSENTIAL));
		}
		if (attributeItem.has(VERIFIED) && !(attributeItem.isNull(VERIFIED))) {
			attributeSet.setVerifiedRequested(attributeItem.getBoolean(VERIFIED));
		}
		if (attributeItem.has(LABEL) && !(attributeItem.isNull(LABEL))) {
			attributeSet.setLabel(attributeItem.getString(LABEL));
		} else {
			// if we have no mapping of the attribute type,
			if(CategoryMap.get(attributeSet.getType()) == null) {
				attributeSet.setLabel(attributeSet.getKey());
			} else {
				// get the mapped label and set it. 
				attributeSet.setLabel(CategoryMap.get(attributeSet.getType()).getAttributeLabel());
			}
		}
		mAttributeListSet.add(attributeSet);
	}
	
	/**
	 * Returns the client id for the current session
	 * @return
	 */
	public synchronized String getClientId() {
		return mClientId;
	}
	
	/**
	 * Returns the state for the current session
	 * @return
	 */
	public synchronized String getState() {
		return mState;
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.mClientId = null;
		this.mState = null;
		this.mAttributeListSet = null;
		this.mReturnStrategy = null;
		super.finalize();
	}
	
	/**
	 * Compares the attribute set to the user-selected attribute and puts the user-selected
	 * attribute in the appropriate map.
	 * @param attributeSet
	 * @param userSelectedAttribute
	 */
	private void putAttributeInMap(AbstractAttributeSet attributeSet) {
		 AbstractAttribute<?> userSelectedAttribute = attributeSet.getSelectedAttribute();
		Logger.debug(getClass(), "User-selected attribute: " + userSelectedAttribute + " has state: " + userSelectedAttribute.getState().toString());
		// if verified was requested for the attribute.
		if(attributeSet.isVerifiedRequested()) {
			// if the selected attribute is verified, put it in the verifiedAttributeMap. 
			if(userSelectedAttribute.getState().equals(ATTRIBUTE_STATE.VERIFIED)) {
				Logger.debug(getClass(), "Adding user-selected attribute: " + userSelectedAttribute + " to the verified attribute map");
				this.mVerifiedAttributeMap.put(attributeSet.getKey(), userSelectedAttribute);
			} else {
				// if the selected attribute is not verified, put the selected attribute in the unverified map. 
				// this is on a best effort basis.
				Logger.debug(getClass(), "Adding user-selected attribute: " + userSelectedAttribute + " to the unverified attribute map");
				this.mUnverifiedAttributeMap.put(attributeSet.getKey(), userSelectedAttribute);
			} 
		// if verified was not request, irrespective of the attribute state, the attribute is added to the unverified attribute map. 
		} else if(!(attributeSet.isVerifiedRequested())) {
			Logger.debug(getClass(), "Adding user-selected attribute: " + userSelectedAttribute + " to the unverified attribute map");
			this.mUnverifiedAttributeMap.put(attributeSet.getKey(), userSelectedAttribute);
		}
	}
	
	@Override
	public void onError(MIDaaSException arg0) {
		Logger.error(getClass(), arg0.getError().getErrorMessage());
		mOnDoneCallback.onError(new Exception(arg0.getError().getErrorMessage()));
	}

	@Override
	public void onSuccess(String response) {
		if(response == null || response.isEmpty()) {
			Logger.error(getClass(), "Response from server is empty.");
			mOnDoneCallback.onError(new Exception("Response from server is empty"));
		} else {
			Logger.debug(getClass(), "Server response: " + response);
			// set the verified/signed attribute bundle. 
			mVerifiedResponse = response;
			// if there are data elements in the unverified attribute map
			if(mUnverifiedAttributeMap.size() > 0) {
				// get the unverified attribute bundle. 
				getUnverifiedBundleAndReturnToRP();
			} else {
				returnDataToRp(mVerifiedResponse,null, mOnDoneCallback);
			}
		}
	}
	
	/**
	 * Authorizes the release of attributes to the RP for this session. First tries to get the verified bundle, then the 
	 * unverified bundle and then returns the data to the RP. 
	 * @param onDoneCallback the callback handler to get the status of the authorization
	 */
	public synchronized void authorizeRequest(final OnDoneCallback onDoneCallback) {
		if(onDoneCallback == null) {
			throw new IllegalArgumentException("OnDoneCallback required.");
		}
		mOnDoneCallback = onDoneCallback;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Logger.error(getClass(), e.getMessage());
				}
				for(AbstractAttributeSet attributeSet: mAttributeListSet){
					if(attributeSet.getSelectedAttribute() != null) {
						putAttributeInMap(attributeSet);
					}
				}
				if(mAcrLevel == 1) {
					if(mVerifiedAttributeMap.size() >0) {
						MIDaaS.getVerifiedAttributeBundle(mClientId, mState, mVerifiedAttributeMap, Session.this);
					} else {
						MIDaaS.getVerifiedAttributeBundle(mClientId, mState, null, Session.this);
					}
				}
//				// if there is at least one verified attribute in the map, get the signed bundle from the server. 
//				if(mVerifiedAttributeMap.size() >0) {
//					Logger.debug(getClass(), "Bundling attibutes with AVS");
//					MIDaaS.getVerifiedAttributeBundle(mClientId, mState, mVerifiedAttributeMap, Session.this);
//				} else if (mUnverifiedAttributeMap.size() > 0) { 
//					getUnverifiedBundleAndReturnToRP();
//				} else {
//					returnDataToRp(mVerifiedResponse,mUnverifiedResponse, mOnDoneCallback);
//				}
			}
			
		}).start();
	}
	
	private void getUnverifiedBundleAndReturnToRP() {
		mUnverifiedResponse = MIDaaS.getAttributeBundle(mClientId, mState, mUnverifiedAttributeMap);
		if(mUnverifiedResponse == null) {
			Logger.error(getClass(), "Unverified attributes could not be generated");
			mOnDoneCallback.onError(new Exception("Unverified attributes could not be generated"));
		} else {
			returnDataToRp(mVerifiedResponse,mUnverifiedResponse, mOnDoneCallback);
		}
	}
	
	private void returnDataToRp(String verifiedAttributeBundle, String unverifiedAttributeBundle, OnDoneCallback callback) {
		Logger.debug(getClass(), "Returning data to RP");
		Logger.debug(getClass(), "verified bundle: " + verifiedAttributeBundle);
		Logger.debug(getClass(), "unverified bundle: " + unverifiedAttributeBundle);
		if(this.mState != null) {
			Logger.debug(getClass(), "state: " + this.mState);
		}
		mReturnStrategy.sendReturn(verifiedAttributeBundle, unverifiedAttributeBundle, this.mState, callback);
	}
	
	/**
	 * 
	 * Methods listed below are called when the authorization 
	 * was successful and the data was returned to the RP or 
	 * an error is returned if something went wrong with the 
	 * authorization.
	 *
	 */
	public static interface OnDoneCallback {
		
		/**
		 * Called when the authorization operation was successful
		 * @param message server message if any
		 */
		public void onDone(String message);
		
		/**
		 * Called when something goes wrong while authorizing. 
		 * @param e the exception.
		 */
		public void onError(Exception e);
	}
}
