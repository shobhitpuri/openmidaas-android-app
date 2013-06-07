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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.Session.OnDoneCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PostbackReturnStrategy extends ReturnStrategy {
	
	protected PostbackReturnStrategy(){}

	@Override
	public void sendReturn(String verifiedAttributeBundle,
			String unverifiedAttributeBundle, String state, final OnDoneCallback callback) throws NullPointerException {
		AsyncHttpClient client = new AsyncHttpClient();
		if(mReturnUrl == null) {
			Logger.error(getClass(), "URL is missing");
			throw new NullPointerException("Return url is missing. ");
		}
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(this.getAsUrlParameter(PARAMETER_VERIFIED_ATTRIBUTE, verifiedAttributeBundle));
		nameValuePairs.add(this.getAsUrlParameter(PARAMETER_UNVERIFIED_ATTRIBUTE, unverifiedAttributeBundle));
		nameValuePairs.add(this.getAsUrlParameter(PARAMETER_STATE, state));
		UrlEncodedFormEntity urlParams = null;
		try {
			Logger.debug(getClass(), "Making POST request to: " + mReturnUrl.toString());
			urlParams = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			callback.onError(new Exception("Error while encoding data"));
		} 
			Logger.debug(getClass(), urlParams.toString());
			client.post(null, mReturnUrl.toString(), urlParams, "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String response) {
					Logger.debug(getClass(), "done posting to: " + mReturnUrl.toString());
					callback.onDone(response);
				}
				@Override
				public void onFailure(Throwable e, String response){
					Logger.error(getClass(), e.getMessage() + response);
					callback.onError(new Exception ("Error sending response to server. "));
				}
			});
		
	}
	
	private BasicNameValuePair getAsUrlParameter(String key, String value) {
		if(value == null || value.isEmpty()) {
			return new BasicNameValuePair(key, "");
		}
		return new BasicNameValuePair(key, value);
	}
}
