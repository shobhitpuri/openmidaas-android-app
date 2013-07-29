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


public final class Constants {
	public static final class AttributeNames {
		public static final String GIVEN_NAME = "given_name";
		public static final String MIDDLE_NAME = "middle_name";
		public static final String FAMILY_NAME = "family_name";
		public static final String BIRTHDAY = "birthday";
		public static final String GENDER = "gender";
		public static final String EMAIL = "email";
		public static final String PHONE = "phone_number";
		public static final String ADDRESS = "address";
		public static final String CREDIT_CARD = "credit_card";
	}
	public static final String ATTRIBUTE_CATEGORY_PERSONAL = "Personal";
	public static final String ATTRIBUTE_CATEGORY_EMAIL = "Email";
	public static final String ATTRIBUTE_CATEGORY_PHONE = "Phone";
	public static final String ATTRIBUTE_CATEGORY_ADDRESS = "Address";
	public static final String ATTRIBUTE_CATEGORY_CREDIT_CARD = "Credit Card";
	public static final String ATTRIBUTE_CATEGORY_GENERAL = "General";
	public static final String ZXING_MARKET =
			    "market://search?q=pname:com.google.zxing.client.android";
	public static final String ZXING_DIRECT =
			    "https://zxing.googlecode.com/files/BarcodeScanner3.1.apk";
	
	public static final class IntentActionMessages{
		public static final String LOCAL_BROADCAST_GCM_MESSAGE = "org.openmidaas.app.action.gcm.somemessage";
		public static final String PROCESS_URL = "org.openmidaas.app.action.processURL";
		
	}
	
	public static final class SharedPreferenceNames{
		public static final String PHONE_NUMBER_PUSH_SERVICE = "org.openmidaas.app.preference.phone";
	}
}
