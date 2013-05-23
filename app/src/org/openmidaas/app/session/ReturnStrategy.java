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

import java.net.URI;
import java.net.URISyntaxException;

import org.openmidaas.app.session.Session.OnDoneCallback;

public abstract class ReturnStrategy {
	
	protected URI mReturnUrl;
	
	protected final String PARAMETER_VERIFIED_ATTRIBUTE = "vattr";
	
	protected final String PARAMETER_UNVERIFIED_ATTRIBUTE = "attr";
	
	protected final String PARAMETER_STATE = "state";
	
	public void setReturnUrl(String url) throws URISyntaxException {
		URI uri = new URI(url);
		if(uri.isAbsolute()) {
			this.mReturnUrl = uri;
		} else {
			throw new URISyntaxException(url, "Invalid return URL");
		}
	}
	
	public abstract void sendReturn(String verifiedAttributeBundle, String unverifiedAttributeBundle, String state, OnDoneCallback callback);
	
}
