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

public class Session {
	
	private final String CLIENT_ID = "clientId";
	
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
	
	public Session() {
		mAttributeSet = new ArrayList<AbstractAttributeSet>();
	}
	
	/**
	 * Sets the data from the request
	 * Note: Make sure this method call is thread safe
	 * @param requestObject
	 * @throws JSONException
	 */
	public void setRequestData(JSONObject requestObject) throws JSONException, MissingRequestValueException {
		JSONObject attrRequest;
		if(requestObject == null) {
			throw new MissingRequestValueException("");
		}
		if(requestObject.isNull(CLIENT_ID)) {
			// throw exception
		}
		if(requestObject.isNull(ATTRIBUTES)) {
			// throw exception			
		}
		if (requestObject.isNull(RETURN)) {
			// throw exception
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
					throw new MissingRequestValueException();
				}
			} else {
				throw new MissingRequestValueException();
			}
		}
		
	}
	
	/**
	 * Returns an attribute set for the request. 
	 * Note: Make sure this method is thread safe. 
	 * @return
	 */
	public List<AbstractAttributeSet> getAttributeSet() {
		for(AbstractAttributeSet attributeSet: mAttributeSet) {
			attributeSet.fetch();
		}
		return mAttributeSet;
	}
	
	public String getClientId() {
		return mClientId;
	}
	
	public String getState() {
		return mState;
	}
	
	private void createAttributeSet(String key, JSONObject attributeItem) throws JSONException {
		String type = null;
		if(attributeItem.has(TYPE)) {
			if(!(attributeItem.isNull(TYPE))) {
				type = attributeItem.getString(TYPE);
			}
		} else {
			if(key != null) {
				type = key;
			}
		}
		AbstractAttributeSet attributeSet = AttributeSetFactory.getAttributeSetForType(type);
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
}
