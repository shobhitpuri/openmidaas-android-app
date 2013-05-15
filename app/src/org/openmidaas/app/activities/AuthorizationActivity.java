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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.app.R;
import org.openmidaas.app.activities.ui.list.AuthorizationListAdapter;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.AttributeFetchException;
import org.openmidaas.app.session.AttributeRequestObjectException;
import org.openmidaas.app.session.EssentialAttributeMissingException;
import org.openmidaas.app.session.Session;
import org.openmidaas.app.session.Session.OnDoneCallback;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class AuthorizationActivity extends AbstractActivity{
	
	public static final String REQUEST_BUNDLE_KEY = "authorization_package_data";
	
	private ListView mAuthorizationList;
	
	private TextView tvRpInfo;
	
	private AuthorizationListAdapter mAuthorizationListAdapter;
	
	private List<AbstractAttributeSet> mAttributeSet = new ArrayList<AbstractAttributeSet>();
	
	private final int ATTRIBUTE_SET_PARSE_SUCCESS = 1;
	
	private final int ATTRIBUTE_SET_PARSE_ERROR = -1;
	
	private final int ATTRIBUTE_SET_INVALID_REQUEST= -2;
	
	private Activity mActivity;
	
	private Session mSession;
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mActivity = this;
		mAuthorizationList = (ListView)findViewById(R.id.lvAuthorizationItems);
		tvRpInfo = (TextView)findViewById(R.id.tvRpInfo);
		mAuthorizationListAdapter = new AuthorizationListAdapter(mActivity);
		mAuthorizationList.setAdapter(mAuthorizationListAdapter);
		if(this.getIntent().getStringExtra(REQUEST_BUNDLE_KEY) != null) {
			try {
				JSONObject requestData = new JSONObject(this.getIntent().getStringExtra(REQUEST_BUNDLE_KEY));
				mProgressDialog.show();
				startSession(requestData);
			} catch(JSONException e) {
				DialogUtils.showNeutralButtonDialog(this, "Error", "There was an error processing the request.");
			}
		}
		findViewById(R.id.bthAuthorize).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				performAuthorization();
			}
		});
	}

	private void performAuthorization() {
		mProgressDialog.setMessage("Authorizing...");
		mProgressDialog.show(); 
		try {
			mSession.authorizeRequest(new OnDoneCallback() {
	
				@Override
				public void onDone(String message) {
					dismissDialog();
					mActivity.startActivity(new Intent(mActivity, HomeScreen.class).putExtra(HomeScreen.ANIMATE_DONE, true));
					mActivity.finish();
				}
	
				@Override
				public void onError(Exception e) {
					dismissDialog();
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
				}
				
			});
		} catch(EssentialAttributeMissingException e) {
			dismissDialog();
			DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
		}
	}
	
	@Override
	protected String getTitlebarText() {
		return (getString(R.string.authorization_title_text));
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.authorization_list_view);
	}
	
	private void startSession(final JSONObject requestData) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mSession = new Session();
				Message message = Message.obtain();
				try {
					mSession.setRequestData(requestData);
					mAttributeSet.clear();
					mAttributeSet = mSession.getAttributeSet();
					mAuthorizationListAdapter.setList(mAttributeSet);
					message.what = ATTRIBUTE_SET_PARSE_SUCCESS;
					mHandler.sendMessage(message);
				} catch (AttributeRequestObjectException e) {
					Logger.error(getClass(), e.getMessage());
					message.what = ATTRIBUTE_SET_INVALID_REQUEST;
					mHandler.sendMessage(message);
				} catch (JSONException e) {
					Logger.error(getClass(), e.getMessage());
					message.what = ATTRIBUTE_SET_PARSE_ERROR;
					mHandler.sendMessage(message);
				} catch (AttributeFetchException e) {
					Logger.error(getClass(), e.getMessage());
					// re-try fetching the attribute set from the persistence store again. 
					try {
						mAttributeSet.clear();
						mSession.getAttributeSet().clear();
						mAttributeSet = mSession.getAttributeSet();
						mAuthorizationListAdapter.setList(mAttributeSet);
						message.what = ATTRIBUTE_SET_PARSE_SUCCESS;
						mHandler.sendMessage(message);
					} catch (AttributeFetchException e1) {
						Logger.error(getClass(), e.getMessage());
						mHandler.sendMessage(message);
					}
				}
			}
			
		}).start();
	}
	
	/**
	 * Create a handler to the UI thread. 
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			if(mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			switch(msg.what) {
				case ATTRIBUTE_SET_PARSE_SUCCESS:
					if(mSession.getClientId()!= null) {
						tvRpInfo.setText(mSession.getClientId() + " " + mActivity.getString(R.string.rpInfoText));
					}
					// refresh the authorization list
					mAuthorizationListAdapter.notifyDataSetChanged();
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
}
