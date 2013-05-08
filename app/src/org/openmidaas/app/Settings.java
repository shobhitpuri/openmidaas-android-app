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

public class Settings {
	public static boolean SHOULD_LOG = true;
	public static boolean ATTRIBUTE_DIAGNOSTICS_ENABLED = true;
	public static int LIBRARY_LOG_LEVEL = MIDaaS.LOG_LEVEL_ERROR;
	public static boolean IS_HOCKEY_APP_ENABLED = false;
}
