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

public final class Intents {
	
	private static final String BASE_INTENT_NAME = "org.openmidaas.app";
	
	public static final String ATTRIBUTE_LIST_CHANGE_EVENT = BASE_INTENT_NAME + ".ATTRIBUTE_LIST_CHANGE_EVENT";
	
	public static final String QR_CODE_INIT_INTENT = "com.google.zxing.client.android.SCAN";
	
	public static final String REFRESH_CONSENT_LIST = BASE_INTENT_NAME + ".REFRESH_CONSENT_LIST";
	
}
