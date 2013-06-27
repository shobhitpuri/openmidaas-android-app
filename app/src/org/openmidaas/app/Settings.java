/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.app;

import org.openmidaas.library.MIDaaS;

public final class Settings {
	public static final boolean SHOULD_LOG = true;
	public static final boolean ATTRIBUTE_DIAGNOSTICS_ENABLED = true;
	public static final int LIBRARY_LOG_LEVEL = MIDaaS.LOG_LEVEL_DEBUG;
	public static final boolean IS_HOCKEY_APP_ENABLED = false;
	public static final String HOCKEY_APP_ID = "";
	public static final String SERVER_URL = "https://midaas-avp.securekeylabs.com";
	public static final String GCM_SENDER_ID = "";
	public static final String PUSH_REGISTRATION_SERVER_URL = "";
}
