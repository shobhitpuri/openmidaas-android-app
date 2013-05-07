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
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;
import org.openmidaas.app.session.attributeset.AttributeSetFactory;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.MIDaaS.VerifiedAttributeBundleCallback;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;

public class Session {
	
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
	
	private String mClientId;
	
	private String mState;
	
	private List<AbstractAttributeSet> mAttributeSet;
	
	private Map<String, AbstractAttribute<?>> mVerifiedAttributeMap;
	
	private Map<String, AbstractAttribute<?>> mUnverifiedAttributeMap;
	
	private ReturnStrategy mReturnStrategy = null;
	
	private String mVerifiedResponse = null;
	
	private String mUnverifiedResponse = null;
	
	public Session() {
		mAttributeSet = new ArrayList<AbstractAttributeSet>();
		mVerifiedAttributeMap = new HashMap<String, AbstractAttribute<?>>();
		mUnverifiedAttributeMap = new HashMap<String, AbstractAttribute<?>>();
	}
	
	/**
	 * Sets the data from the request
	 * Note: Make sure this method call is thread safe
	 * @param requestObject
	 * @throws JSONException
	 */
	public synchronized void setRequestData(JSONObject requestObject) throws JSONException, ParseException  {
		JSONObject attrRequest;
		if(requestObject == null) {
			throw new ParseException("Ther requestObject parameter is null. ");
		}
		// we need to the keys listed below to proceed. Check to see if they exist. 
		if((!(requestObject.has(CLIENT_ID))) || (!(requestObject.has(ATTRIBUTES))) || (!(requestObject.has(RETURN)))) {
			throw new ParseException("clientId, attrs, and/or return keys are missing in the request");
		}
			
		// we need values for the keys listed below to proceed. Check to see if they exist. 
		if(requestObject.isNull(CLIENT_ID) || requestObject.isNull(ATTRIBUTES) || requestObject.isNull(RETURN)) {
			throw new ParseException("clientId, attrs, and/or return values are missing in the request");
		}
		// we need to check whether the return object has the correct keys
		if(!(requestObject.getJSONObject(RETURN).has(RETURN_METHOD))) {
			throw new ParseException("Return object has no return method.");
		}
		if(!(requestObject.getJSONObject(RETURN).has(RETURN_URL))) {
			throw new ParseException("Return object has no return url.");
		}
		
		// check if "attrs" value is actually a JSONObject
		if (!(requestObject.get(ATTRIBUTES) instanceof JSONObject)) {
			throw new ParseException("The value for \"attrs\" is not of type JSONObject.");
		}
		attrRequest = requestObject.getJSONObject(ATTRIBUTES);
		mClientId = requestObject.getString(CLIENT_ID);
		
		mReturnStrategy = ReturnStrategyFactory.getStrategyForMethodName(requestObject.getJSONObject(RETURN).getString(RETURN_METHOD));
		
		try {
			mReturnStrategy.setReturnUrl(requestObject.getJSONObject(RETURN).getString(RETURN_URL));
		} catch (URISyntaxException e) {
			throw new ParseException("The return url appears to be an invalid url");
		}
		
		if(!(requestObject.isNull(STATE))) {
			mState = requestObject.getString(STATE);
		}
		
		Iterator<?> keys = requestObject.getJSONObject(ATTRIBUTES).keys();
		// parsing through the "attrs" field now.
		while(keys.hasNext()) {
			String key = (String)keys.next();
			if(attrRequest.get(key) != null) {
				if(attrRequest.get(key) instanceof JSONObject) {
					createAttributeSet(key, attrRequest.getJSONObject(key));
				} else {
					throw new ParseException("The value for the key in \"attrs\" is not of type JSONObject.");
				}
			} else {
				throw new ParseException("The value for key: " + key + " is null");
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
		for(AbstractAttributeSet attributeSet: mAttributeSet) {
			attributeSet.fetch();
		}
		return mAttributeSet;
	}
	
	/**
	 * Helper method to create an attribute set
	 * @param key
	 * @param attributeItem
	 * @throws JSONException
	 * @throws ParseException
	 */
	private void createAttributeSet(String key, JSONObject attributeItem) throws JSONException, ParseException {
		String type = null;
		if(attributeItem.has(TYPE)) {
			if(!(attributeItem.isNull(TYPE))) {
				type = attributeItem.getString(TYPE);
			} else {
				throw new ParseException("Missing value for \"type\"");
			}
		} else {
			if(key != null) {
				type = key;
			} else {
				throw new ParseException("Key is null or not specified.");
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
		}
		mAttributeSet.add(attributeSet);
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
		this.mAttributeSet = null;
		this.mReturnStrategy = null;
		super.finalize();
	}
	
	private void putAttributeInMap(AbstractAttributeSet attributeSet, AbstractAttribute<?> userSelectedAttribute) {
		// if is verified was requested for the selected attribute and the selected attribute state is actually verified
		// put it in the verifiedAttributeMap. 
		if(attributeSet.isVerifiedRequested() && userSelectedAttribute.getState().equals(ATTRIBUTE_STATE.VERIFIED)) {
			this.mVerifiedAttributeMap.put(attributeSet.getKey(), userSelectedAttribute);
		// if is verified was requested by the selected attribute is not verified, put the selected attribute in the unverified map. 
		// this is on a best effort basis. 
		} else if(attributeSet.isVerifiedRequested() && !(userSelectedAttribute.getState().equals(ATTRIBUTE_STATE.VERIFIED))) {
			 this.mUnverifiedAttributeMap.put(attributeSet.getKey(), userSelectedAttribute);
		// if verified was not request, irrespective of the attribute state, the attribute is added to the unverified attribute map. 
		} else if(!(attributeSet.isVerifiedRequested())) {
			this.mUnverifiedAttributeMap.put(attributeSet.getKey(), userSelectedAttribute);
		}
	}
	
	/**
	 * Authorizes the release of attributes to the RP for this session. 
	 * @param onDoneCallback the callback handler to get the status of the authorization
	 * @throws EssentialAttributeMissingException if no attribute is provided for a required attribute. 
	 */
	public synchronized void authorizeRequest(final OnDoneCallback onDoneCallback) throws EssentialAttributeMissingException {
		
		for(AbstractAttributeSet attributeSet: this.mAttributeSet){
			// if essential is requested and nothing was selected, throw an exception
			if(attributeSet.isEssentialRequested() && attributeSet.getSelectedAttribute() == null) {
				throw new EssentialAttributeMissingException(attributeSet.getLabel() + " is essential. Please select one.");
			}
			// if essential is requested and selected attribute is not null or  if is essential is not set but we have a selected attribute
			else if((attributeSet.isEssentialRequested() && attributeSet.getSelectedAttribute() != null) || 
					(!(attributeSet.isEssentialRequested()) && attributeSet.getSelectedAttribute() != null)) {
				putAttributeInMap(attributeSet, attributeSet.getSelectedAttribute());
			}
		}
		// if there is at least one verified attribute in the map. 
		if(this.mVerifiedAttributeMap.size() >0) {
			MIDaaS.getVerifiedAttributeBundle(mClientId, mState, mVerifiedAttributeMap, new VerifiedAttributeBundleCallback() {

				@Override
				public void onError(MIDaaSException arg0) {
					onDoneCallback.onError(new Exception(arg0.getError().getErrorMessage()));
				}

				@Override
				public void onSuccess(String response) {
					if(response == null || response.isEmpty()) {
						onDoneCallback.onError(new Exception("Response from server is empty"));
					} else {
						// set the verified/signed attribute bundle. 
						mVerifiedResponse = response;
						// if there are data elements in the unverified attribute map
						if(mUnverifiedAttributeMap.size() > 0) {
							// get the unverified attribute bundle. 
							mUnverifiedResponse = MIDaaS.getAttributeBundle(mClientId, mState, mUnverifiedAttributeMap);
							returnDataToRp(mUnverifiedResponse,mUnverifiedResponse, onDoneCallback);
							if(mUnverifiedResponse == null) {
								onDoneCallback.onDone("Unverified attributes could not be generated");
							}
						} else {
							returnDataToRp(mVerifiedResponse,null, onDoneCallback);
						}
					}
				}
				
			});
		} else if (this.mUnverifiedAttributeMap.size() > 0) { 
			mUnverifiedResponse = MIDaaS.getAttributeBundle(mClientId, mState, mUnverifiedAttributeMap);
			returnDataToRp(null,mUnverifiedResponse, onDoneCallback);
		}
	}
	
	private void returnDataToRp(String verifiedAttributeBundle, String unverifiedAttributeBundle, OnDoneCallback callback) {
		Logger.debug(getClass(), "Returning data to RP");
		Logger.debug(getClass(), "verified bundle: " + verifiedAttributeBundle);
		Logger.debug(getClass(), "unverified bundle: " + unverifiedAttributeBundle);
		mReturnStrategy.sendReturn(verifiedAttributeBundle, unverifiedAttributeBundle, callback);
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
