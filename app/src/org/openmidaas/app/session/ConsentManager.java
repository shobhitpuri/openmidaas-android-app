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
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.content.Context;
import android.content.SharedPreferences;

public class ConsentManager {
	
	private static final String CONSENT_PERSISTENCE_STORE_NAME = "org.openmidaas.app.consent_persistence_store";
	
	/**
	 * Saves a list of attributes as consented attributes
	 * @param context
	 * @param clientId the clientId that maps to a list of consented attributes
	 * @param attributeSet the attribute set to save the consent. 
	 * @return true if the save was successful, false otherwise
	 */
	public static synchronized boolean saveAuthorizedAttributes(Context context, String clientId, List<AbstractAttributeSet> attributeSet) {
		if(context == null) {
			throw new IllegalArgumentException("Context cannot be null");
		}
		if(clientId == null) {
			throw new IllegalArgumentException("Client ID cannot be null");
		}
		if(attributeSet == null) {
			throw new IllegalArgumentException("Attribute list cannot be null");
		}
		JSONObject keyToAidMap = new JSONObject();
		for(AbstractAttributeSet element: attributeSet){
			AbstractAttribute<?> selectedAttribute = element.getSelectedAttribute();
			if(selectedAttribute != null) {
				if(selectedAttribute.getId() >= 0) {
					try {
						keyToAidMap.put(element.getKey(), String.valueOf(selectedAttribute.getId()));
					} catch (JSONException e) {
						Logger.debug(ConsentManager.class, e.getMessage());
					}
				}
			}
		}
		SharedPreferences prefs = context.getSharedPreferences(
				CONSENT_PERSISTENCE_STORE_NAME, Context.MODE_PRIVATE);
		if(prefs != null) {
			return (prefs.edit().putString(clientId, keyToAidMap.toString()).commit());
		}
		return false;
	}
	
	
	private static JSONObject loadAuthorizedAttributes(Context context, String clientId) {
		if(context == null) {
			throw new IllegalArgumentException("context cannot be null");
		}
		if (clientId == null) {
			throw new IllegalArgumentException("Client ID cannot be null");
		}
		SharedPreferences prefs = context.getSharedPreferences(
				CONSENT_PERSISTENCE_STORE_NAME, Context.MODE_PRIVATE);
		if(prefs != null) {
			JSONObject consentMap;
			try {
				String mappedData = prefs.getString(clientId, null);
				if(mappedData == null) {
					return null;
				}
				consentMap = new JSONObject(mappedData);
				if(consentMap != null) {
					return consentMap;
				}
			} catch (JSONException e) {
				Logger.debug(ConsentManager.class, e.getMessage());
			}
			
		}
		return null;
	}
	
	/**
	 * Returns the map of client id to a set of attribute ids
	 * @param context
	 * @return the list of all currently saved client ids that the user
	 * has consented to. 
	 */
	public static synchronized List<String> getAllConsents(Context context) {
		if(context == null) {
			throw new IllegalArgumentException("context cannot be null");
		}
		SharedPreferences prefs = context.getSharedPreferences(
				CONSENT_PERSISTENCE_STORE_NAME, Context.MODE_PRIVATE);
		List<String> tmpList = new ArrayList<String>();
		if(prefs != null) {
			Map<String, ?> map = prefs.getAll();
			if(prefs.getAll() != null) {
				for(Map.Entry<String,?> entry : map.entrySet()){
		            tmpList.add(entry.getKey());
				}
				return tmpList;
			}
		}
		return null;
	}
	
	/**
	 * Revokes a consent for the specified client id
	 * @param context
	 * @param clientId the client id to revoke the consent
	 * @return true if the consent was revoked successfully. 
	 */
	public static synchronized boolean revokeConsentForClient(Context context, String clientId) {
		SharedPreferences prefs = context.getSharedPreferences(
				CONSENT_PERSISTENCE_STORE_NAME, Context.MODE_PRIVATE);
		if(prefs != null) {
			return (prefs.edit().remove(clientId).commit());
		}
		return false;
	}

	
	public static AbstractAttribute<?> checkConsentWithSetKey(Context context, String key, String clientId, List<AbstractAttribute<?>> attributeList) {
		JSONObject consentSet = loadAuthorizedAttributes(context, clientId);
		if(attributeList == null) {
			return null;
		}
		if(consentSet == null) {
			return null;
		}
		try {
			long consentedAttributeId = Long.parseLong(consentSet.getString(key));
			for(AbstractAttribute<?> attribute: attributeList) {
				if(attribute != null) {
					if(attribute.getId() == consentedAttributeId) {
						return attribute;
					}
				}
			}
			return null;
		}catch(JSONException e) {
			Logger.debug(ConsentManager.class, e.getMessage());
		}
		return null;
	}
	
	public static boolean checkConsent(Context context, String clientId, AbstractAttribute<?> attribute) {
		JSONObject consentSet = loadAuthorizedAttributes(context, clientId);
		Iterator<?> keys = consentSet.keys();
		try {
			while( keys.hasNext() ){
	            String key = (String)keys.next();
	            long consentedAttributeId = Long.parseLong(consentSet.getString(key));
	            if(consentedAttributeId == attribute.getId()) {
	            	return true;
	            }
	        }
		} catch(JSONException e) {
			Logger.debug(ConsentManager.class, e.getMessage());
		}
		return false;
	}
}

