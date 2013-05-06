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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;
import org.openmidaas.app.session.attributeset.AttributeSetFactory;

public class Session {
	
	private final String CLIENT_ID = "client_id";
	
	private final String ATTRIBUTES = "attrs";
	
	private final String STATE = "state";
	
	private final String RETURN = "return";
	
	private final String RETURN_METHOD = "method";
	
	private final String TYPE = "type";
	
	private final String LABEL = "label";
	
	private final String ESSENTIAL = "essential";
	
	private final String VERIFIED = "verified";
	
	private String mClientId;
	
	private String mState;
	
	private List<AbstractAttributeSet> mAttributeSet;
	
	private ReturnStrategy mReturnStrategy = null;
	
	public Session() {
		mAttributeSet = new ArrayList<AbstractAttributeSet>();
	}
	
	/**
	 * Sets the data from the request
	 * Note: Make sure this method call is thread safe
	 * @param requestObject
	 * @throws JSONException
	 */
	public synchronized void setRequestData(JSONObject requestObject) throws JSONException, InvalidRequestException  {
		JSONObject attrRequest;
		if(requestObject == null) {
			throw new InvalidRequestException("Ther requestObject parameter is null. ");
		}
		// we need to the keys listed below to proceed. Check to see if they exist. 
		if((!(requestObject.has(CLIENT_ID))) || (!(requestObject.has(ATTRIBUTES))) || (!(requestObject.has(RETURN)))) {
			throw new InvalidRequestException("clientId, attrs, and/or return keys are missing in the request");
		}
			
		// we need values for the keys listed below to proceed. Check to see if they exist. 
		if(requestObject.isNull(CLIENT_ID) || requestObject.isNull(ATTRIBUTES) || requestObject.isNull(RETURN)) {
			throw new InvalidRequestException("clientId, attrs, and/or return values are missing in the request");
		}
		// check if "attrs" value is actually a JSONObject
		if (!(requestObject.get(ATTRIBUTES) instanceof JSONObject)) {
			throw new InvalidRequestException("The value for \"attrs\" is not of type JSONObject.");
		}
		attrRequest = requestObject.getJSONObject(ATTRIBUTES);
		mClientId = requestObject.getString(CLIENT_ID);
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
					throw new InvalidRequestException("The value for the key in \"attrs\" is not of type JSONObject.");
				}
			} else {
				throw new InvalidRequestException("The value for key: " + key + " is null");
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
	 * @throws InvalidRequestException
	 */
	private void createAttributeSet(String key, JSONObject attributeItem) throws JSONException, InvalidRequestException {
		String type = null;
		if(attributeItem.has(TYPE)) {
			if(!(attributeItem.isNull(TYPE))) {
				type = attributeItem.getString(TYPE);
			} else {
				throw new InvalidRequestException("Missing value for \"type\"");
			}
		} else {
			if(key != null) {
				type = key;
			} else {
				throw new InvalidRequestException("Key is null or not specified.");
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
}
