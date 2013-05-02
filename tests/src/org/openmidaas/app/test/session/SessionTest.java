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
package org.openmidaas.app.test.session;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.app.session.InvalidRequestException;
import org.openmidaas.app.session.Session;

import junit.framework.Assert;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class SessionTest extends InstrumentationTestCase {
	
	
	private String mClientId = "https://edbc.ca";
	
	private String mState = "1234";
	
	private String validRequest = "{\"clientId\": \"https://edbc.ca\",\"acr\": \"1\",\"attrs\": {"+ 
			"\"email\": {\"essential\": true,\"label\": \"work email\",\"verified\": true}},\"state\": \"1234\","+ 
			"\"return\": {\"method\": \"postback\",\"url\": \"https://edbc.ca/sess/fhyxy8209jskso\"}}\"";
	
	
	private String requestWithoutClientId = "{\"acr\": \"1\",\"attrs\": {"+ 
					"\"email\": {\"essential\": true,\"label\": \"work email\",\"verified\": true}},\"state\": \"1234\","+ 
					"\"return\": {\"method\": \"postback\",\"url\": \"https://edbc.ca/sess/fhyxy8209jskso\"}}\"";
	
	private String requestWithoutState = "{\"clientId\": \"https://edbc.ca\",\"acr\": \"1\",\"attrs\": {"+ 
			"\"email\": {\"essential\": true,\"label\": \"work email\",\"verified\": true}},"+ 
			"\"return\": {\"method\": \"postback\",\"url\": \"https://edbc.ca/sess/fhyxy8209jskso\"}}\"";
	
	private Session mSession;
	
	protected void setUp() throws Exception {
		mSession = new Session();
	}
	
	@SmallTest
	public void testSetValidRequest() {
		try {
			JSONObject object = new JSONObject(validRequest);
			mSession.setRequestData(object);
			Assert.assertEquals(mClientId, mSession.getClientId());
			Assert.assertEquals(mState, mSession.getState());
		} catch (JSONException e) {
			Assert.fail();
		} catch (InvalidRequestException e) {
			Assert.fail();
		}
	}
	
	@SmallTest
	public void testSetInvalidRequest() {
		try {
			JSONObject object = new JSONObject(requestWithoutClientId);
			mSession.setRequestData(object);
			Assert.fail();
		} catch (JSONException e) {
			Assert.fail();
		} catch (InvalidRequestException e) {
		}
	}
	
	@SmallTest
	public void testSetWithoutState() {
		try {
			JSONObject object = new JSONObject(requestWithoutState);
			mSession.setRequestData(object);
		} catch (JSONException e) {
			Assert.fail();
		} catch (InvalidRequestException e) {
			Assert.fail();
		}
		
	}
	
	protected void tearDown() throws Exception {
		mSession = null;
	}

}
