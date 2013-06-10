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
package org.openmidaas.app.activities;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.app.R;
import org.openmidaas.app.activities.ui.list.AuthorizationListAdapter;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.AttributeFetchException;
import org.openmidaas.app.session.AttributeRequestObjectException;
import org.openmidaas.app.session.ConsentManager;
import org.openmidaas.app.session.EssentialAttributeMissingException;
import org.openmidaas.app.session.Session;
import org.openmidaas.app.session.Session.OnDoneCallback;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class AuthorizationActivity extends AbstractActivity{
	
	public static final String REQUEST_BUNDLE_KEY = "authorization_package_data";
	
	public static final int AUTHORIZATION_ACTIVITY_REQUEST_CODE = 1; 
	
	public static final int AUTHORIZATION_ACTIVITY_RESULT_OK = 0;
	
	public static final int AUTHORIZATION_ACTIVITY_RESULT_ERROR = -1;
	
	private ListView mAuthorizationList;
	
	private TextView tvRpInfo;
	
	private TextView tvAuthInfo;
	
	private CheckBox cbUserConsent;
	
	private Button btnAuthorize;
	
	private List<AbstractAttributeSet> mAttributeSet;
	
	private AuthorizationListAdapter mAuthorizationListAdapter;
	
	private final int ATTRIBUTE_SET_PARSE_SUCCESS = 1;
	
	private final int AUTO_AUTHORIZE = 2;
	
	private final int ATTRIBUTE_SET_PARSE_ERROR = -1;
	
	private final int ATTRIBUTE_SET_INVALID_REQUEST= -2;
	
	private Activity mActivity;
	
	private Session mSession;
	
	private JSONObject mCurrentRequestData = null;
	
	private String mConsentedAttributeNames = "";
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mActivity = this;
		mAuthorizationList = (ListView)findViewById(R.id.lvAuthorizationItems);
		tvRpInfo = (TextView)findViewById(R.id.tvRpInfo);
		tvAuthInfo = (TextView)findViewById(R.id.tvAuthorizationInfo);
		cbUserConsent = (CheckBox)findViewById(R.id.cbUserConsent);
		btnAuthorize = (Button)findViewById(R.id.bthAuthorize);
		mAuthorizationListAdapter = new AuthorizationListAdapter(mActivity);
		hideUI();
		if(this.getIntent().getStringExtra(REQUEST_BUNDLE_KEY) != null) {
			try {
				mCurrentRequestData = new JSONObject(this.getIntent().getStringExtra(REQUEST_BUNDLE_KEY));
				mProgressDialog.show();
				displayAuthorizationList(mCurrentRequestData);
			} catch(JSONException e) {
				DialogUtils.showNeutralButtonDialog(this, "Error", "There was an error processing the request.");
			}
		}
		btnAuthorize.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				performAuthorization(false);
			}
		});
	}

	private void performAuthorization(boolean savedConsentPresent) {
		if(cbUserConsent.isChecked()) {
			ConsentManager.saveAuthorizedAttributes(mActivity, mSession.getClientId(), this.mAttributeSet);
		}
		String message = "";
		if(savedConsentPresent) {
			message = mActivity.getString(R.string.authorizingAlreadyPresentConsent)+": " + mConsentedAttributeNames;
			
		} else {
			message = mActivity.getString(R.string.authorizingRequest);
		}
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
		checkEssentialAndAuthorize();
	}
	
	/**
	 * Checks if any essential attributes are missing. If not, authorization if performed on 
	 * the current session. If essential attributes are missing, a dialog box is show informing 
	 * the user of the missing essential attributes. 
	 */
	private void checkEssentialAndAuthorize() {
		try {
			checkEssentialAttributesInSet();
			authorizeSession();
		} catch (EssentialAttributeMissingException e) {
			// if the users presses proceed, authorize the set. 
			dismissDialog();
			DialogUtils.showEssentialAttributeMissingDialog(mActivity, e.getMessage(), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					mProgressDialog.setMessage(mActivity.getString(R.string.authorizingRequest));
					mProgressDialog.show();
					authorizeSession();
				}
			});
		}
	}
	
	/**
	 * Checks if essential attributes are set by the user and if not, throws an exception.
	 * @throws EssentialAttributeMissingException exception. The message is the CSV list of the 
	 * missing attributes. 
	 */
	private void checkEssentialAttributesInSet() throws EssentialAttributeMissingException {
		boolean essentialAttributesMissing = false;
		StringBuilder builder = new StringBuilder();
		String prefix = "";
		for(AbstractAttributeSet attributeSet: this.mAttributeSet){
			// if essential is requested and nothing was selected, throw an exception
			if(attributeSet.isEssentialRequested() && attributeSet.getSelectedAttribute() == null) {
				essentialAttributesMissing = true;
				builder.append(prefix);
				builder.append(attributeSet.getLabel());
				prefix = ", ";
			}
		}
		if(essentialAttributesMissing) {
			throw new EssentialAttributeMissingException(builder.toString());
		}
	}
	
	/**
	 * Authorizes the current session 
	 */
	private void authorizeSession() {
		mSession.authorizeRequest(new OnDoneCallback() {

			@Override
			public void onDone(String message) {
				dismissDialog();
				DialogUtils.showToast(mActivity, getResources().getString(R.string.attrSentSuccess));
				mActivity.finish();
			}

			@Override
			public void onError(Exception e) {
				dismissDialog();
				DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
			}
			
		});
	}
	
	
	@Override
	protected String getTitlebarText() {
		return (getString(R.string.authorization_title_text));
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.authorization_list_view);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == AUTHORIZATION_ACTIVITY_REQUEST_CODE) {
			if (resultCode == AUTHORIZATION_ACTIVITY_RESULT_OK) {
				displayAuthorizationList(mCurrentRequestData);
			} else {
				Logger.debug(getClass(), "Error adding attributes on the fly");
				DialogUtils.showNeutralButtonDialog(mActivity, "Error", getString(R.string.attributeAddOnFlyError));
			}
		}  else {
			Logger.debug(getClass(), "Request code for onActivityResult does not match");
		}
	}
	
	/**
	 * Creates UI elements based on the requestData. 
	 * @param requestData
	 */
	private void displayAuthorizationList(final JSONObject requestData) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mSession = new Session();
				Message message = Message.obtain();
				try {
					fetchAttributeSet(message, requestData);
				} catch (AttributeFetchException e) {
					Logger.error(getClass(), e.getMessage());
					// re-try fetching the attribute set from the persistence store again. 
					try {
						fetchAttributeSet(message, requestData);
					} catch (AttributeFetchException e1) {
						Logger.error(getClass(), e1.getMessage());
					} 
				}
			}
		}).start();
	}
	
	/**
	 * Populates the attribute set data structure with the request. 
	 * @param message
	 * @param requestData
	 * @throws AttributeFetchException
	 */
	private void fetchAttributeSet(Message message, JSONObject requestData) throws AttributeFetchException {
		StringBuilder builder = new StringBuilder();
		String prefix = "";
		try {
			boolean noConsentPresent = false;
			//1 set the request data
			mSession.setRequestData(requestData);
			//2 populate the attribute set from the db (only for the RP-requested attributes)
			mAttributeSet = mSession.getAttributeSet();
			//3 check for consents
			for(AbstractAttributeSet set: mAttributeSet) {
				List<AbstractAttribute<?>> mAttributeList = set.getAttributeList();
				// if essential is requested
				if(set.isEssentialRequested()) {
					// if "no" element exists, i.e., only the null (zero-th) element (for display purposes)
					if(mAttributeList.size() == 1 && mAttributeList.get(0) == null) {
						noConsentPresent = true;
						ConsentManager.revokeConsentForClient(mActivity, mSession.getClientId());
						break;
					} else {
						AbstractAttribute<?> consentedAttribute = ConsentManager.checkConsentWithSetKey(mActivity, set.getKey(), mSession.getClientId(), mAttributeList);
						if(consentedAttribute != null) {
							builder.append(prefix);
							prefix = ", ";
							builder.append(set.getLabel());
							set.setSelectedAttribute(consentedAttribute);
						} else {
							noConsentPresent = true;
							ConsentManager.revokeConsentForClient(mActivity, mSession.getClientId());
							break;
						}
					}
				} else {
					AbstractAttribute<?> consentedAttribute = ConsentManager.checkConsentWithSetKey(mActivity, set.getKey(), mSession.getClientId(), mAttributeList);
					if(consentedAttribute != null) {
						builder.append(prefix);
						prefix = ", ";
						builder.append(set.getLabel());
					} 
					set.setSelectedAttribute(consentedAttribute);
				}
				
				if(noConsentPresent) {
					break;
				}
			}
			mConsentedAttributeNames = builder.toString();
			if(noConsentPresent) {
				mAuthorizationListAdapter.setList(mAttributeSet);
				message.what = ATTRIBUTE_SET_PARSE_SUCCESS;
				mHandler.sendMessage(message);
			} else {
				message.what = AUTO_AUTHORIZE;
				mHandler.sendMessage(message);
			}
		}catch (AttributeRequestObjectException e) {
			Logger.error(getClass(), e.getMessage());
			message.what = ATTRIBUTE_SET_INVALID_REQUEST;
			mHandler.sendMessage(message);
		} catch (JSONException e) {
			Logger.error(getClass(), e.getMessage());
			message.what = ATTRIBUTE_SET_PARSE_ERROR;
			mHandler.sendMessage(message);
		}
	}
	
	/**
	 * Handler to the UI thread. 
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			mProgressDialog.dismiss();
			switch(msg.what) {
				case ATTRIBUTE_SET_PARSE_SUCCESS:
					if(mSession.getClientId()!= null) {
						tvRpInfo.setText(mSession.getClientId() + " " + mActivity.getString(R.string.rpInfoText));
					}
					// refresh the authorization list
					showUI();
					mAuthorizationList.setAdapter(mAuthorizationListAdapter);
					mAuthorizationListAdapter.notifyDataSetChanged();
				break;
				case AUTO_AUTHORIZE:
					performAuthorization(true);
				break;
				case ATTRIBUTE_SET_INVALID_REQUEST:
					// a more specific error message. We can recover from this. 
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", mActivity.getString(R.string.invalidAttributeRequestMessage));
				break;
				case ATTRIBUTE_SET_PARSE_ERROR:
					// if there was a JSON parsing exception (runtime exception
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", mActivity.getString(R.string.attributeSetParseErrorMessage));
				break;
				default:
					// this should never occur. 
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", mActivity.getString(R.string.fatalErrorMessage));
				break;
			}
			return true;
		}
	});
	
	private void hideUI() {
		mAuthorizationList.setVisibility(View.GONE);
		tvRpInfo.setVisibility(View.GONE);
		cbUserConsent.setVisibility(View.GONE);
		btnAuthorize.setVisibility(View.GONE);
		tvAuthInfo.setVisibility(View.GONE);
	}
	
	private void showUI() {
		mAuthorizationList.setVisibility(View.VISIBLE);
		tvRpInfo.setVisibility(View.VISIBLE);
		cbUserConsent.setVisibility(View.VISIBLE);
		btnAuthorize.setVisibility(View.VISIBLE);
		tvAuthInfo.setVisibility(View.VISIBLE);
	}
}
