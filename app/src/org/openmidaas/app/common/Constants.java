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
	public enum NAME { first_name, last_name, email, address };
	public static final class AttributeNames {
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
	}
	public static final String ATTRIBUTE_CATEGORY_PERSONAL = "Personal";
	public static final String ATTRIBUTE_CATEGORY_EMAIL = "Email";
	public static final String[] ATTRIBUTE_CATEGORY_LABELS = {ATTRIBUTE_CATEGORY_PERSONAL, ATTRIBUTE_CATEGORY_EMAIL};
	 public static final String ZXING_MARKET =
			    "market://search?q=pname:com.google.zxing.client.android";
			  public static final String ZXING_DIRECT =
			    "https://zxing.googlecode.com/files/BarcodeScanner3.1.apk";
}
