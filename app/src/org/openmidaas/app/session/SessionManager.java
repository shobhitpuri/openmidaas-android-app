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

public class SessionManager{
	private static boolean busy = false;
	private static final Object OBJ_LOCK = new Object();
	
	public static Session createSession() throws SessionCreationException {
		synchronized (OBJ_LOCK) {
			if (!busy){
				Session session = new Session();
				return session;
			}else{
				throw new SessionCreationException("Cannot create a new request. A request is already in progress.");
			}	
		}	
	}
	
	public static boolean getBusyness(){
		synchronized (OBJ_LOCK) {
			return busy;
		}
	}
	
	public static void setBusyness(boolean status){
		synchronized (OBJ_LOCK) {
			busy = status;	
		}
	}
}
