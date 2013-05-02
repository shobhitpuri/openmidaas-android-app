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
