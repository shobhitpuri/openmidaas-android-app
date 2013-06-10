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
package org.openmidaas.app.common;

import org.json.JSONObject;
import org.openmidaas.app.R;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.GenericAttributeFactory;
import org.openmidaas.library.model.InvalidAttributeNameException;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

public final class Utils {
	
	/**
	 * Formats the value typically from a date-picker widget and returns it as a easy to read string
	 * @param dayOfMonth
	 * @param monthOfYear
	 * @param year
	 * @return easy to read date as string
	 */
	public static String getFormattedDate(int dayOfMonth, int monthOfYear, int year) {
		StringBuilder builder = new StringBuilder();
		return (builder.append(dayOfMonth).append("-").append(monthOfYear+1).append("-").append(year).append(" ").toString());
	}
	
	/**
	 * Returns a user-friendly label for the attribute. 
	 * If a mapping exists, it returns the mapped label, otherwise it checks to see if the 
	 * attribute has a label, if not, the attribute's name is returned. 
	 * @param attribute
	 * @return
	 */
	public static String getAttributeDisplayLabel(AbstractAttribute<?> attribute) {
		String label = "";
		if(attribute != null) {
			CategoryMap map = CategoryMap.get(attribute.getName());
			if(attribute.getLabel() != null && !attribute.getLabel().isEmpty()) {
				label = attribute.getLabel();
			}else if(map!=null){
				label = map.getAttributeLabel();
			}else{
				label = attribute.getName();
			}
		} 
		return label;
	}
	
	/**
	 * Creates a generic attribute and tries to save it
	 * @param activity
	 * @param name
	 * @param value
	 * @param label
	 */
	public static void createGenericAttribute(Activity activity, String name, String value, String label) {
		try {
			GenericAttribute attribute = GenericAttributeFactory.createAttribute(name);
			if(label != null) {
				attribute.setLabel(label);
			}
			setValueAndPersist(activity, attribute, value);
		} catch (InvalidAttributeNameException e) {
			DialogUtils.showNeutralButtonDialog(activity, activity.getString(R.string.defaultErrorDialogTitle), "Invalid name" );
		} 
	}
	
	/**
	 * Sets the attribute with the provided value and tries to save it
	 * @param activity
	 * @param attribute
	 * @param value
	 */
	public static void modifyGenericAttribute(Activity activity, GenericAttribute attribute, String value) {
		setValueAndPersist(activity, attribute, value);
	}
	public static String getAttributeDetailsLabel(AbstractAttribute<?> attribute){
		String message = "Name: " + attribute.getName() + "\n" +
				 "Value: " + attribute.toString() + "\n"; 
		String[] jwsParams = null;
		JSONObject object = null;
		String audience = "";
		String issuer = "";
		String subject = "";
		if(attribute.getSignedToken() != null) {
			jwsParams = attribute.getSignedToken().split("\\."); 
			try {
				object = new JSONObject(new String(Base64.decode(jwsParams[1], Base64.NO_WRAP), "UTF-8"));
				if(object != null) {
					if (object.getString("aud")!=null)
						audience = object.getString("aud");
					message += "Audience: " + audience + "\n";
					
					if (object.getString("iss")!=null)
						issuer = object.getString("iss");
					message += "Issuer: " + issuer + "\n";
					
					if (object.getString("sub")!=null)
						subject = object.getString("sub");
					message += "Subject: " + subject + "\n";
					
					message += "Signature: " + jwsParams[2];
				}
			} catch(Exception e) {
			}
		}
		return message;
	}
	
	private static void setValueAndPersist(Activity activity, GenericAttribute attribute, String value) {
		try {
			attribute.setValue(value.toString());
			attribute.save();
			activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
		} catch (InvalidAttributeValueException e) {
			DialogUtils.showNeutralButtonDialog(activity, activity.getString(R.string.defaultErrorDialogTitle), "Invalid value ");
		} catch (MIDaaSException e) {
			DialogUtils.showNeutralButtonDialog(activity, activity.getString(R.string.defaultErrorDialogTitle), e.getError().getErrorMessage());
		}
	}
	
	/**
	 * To get the version number from Manifest
	 */
	public static String getVersionNumber(Context context ){
		
		String versionName;
        try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			return versionName;
		} catch (Exception e) {
			Logger.error(Utils.class , e);
			Logger.error(Utils.class, "Version number unknown");
		}
        return "Unknown";
	}
	
	
}
