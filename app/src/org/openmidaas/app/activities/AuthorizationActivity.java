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
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.AbstractAttributeSet;
import org.openmidaas.app.session.MissingRequestValueException;
import org.openmidaas.app.session.Session;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

public class AuthorizationActivity extends AbstractActivity{
	
	public static final String BUNDLE_KEY = "authorization_package_data";
	
	private ListView mAuthorizationList;
	
	private AuthorizationListAdapter mAuthorizationListAdapter;
	
	private List<AbstractAttributeSet> mAttributeSet = new ArrayList<AbstractAttributeSet>();
	
	private final int ATTRIBUTE_SET_PARSE_SUCCESS = 1;
	
	private final int ATTRIBUTE_SET_PARSE_ERROR = -1;
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mAuthorizationList = (ListView)findViewById(R.id.lvAuthorizationItems);
		mAuthorizationListAdapter = new AuthorizationListAdapter(this);
		mAuthorizationList.setAdapter(mAuthorizationListAdapter);
		if(this.getIntent().getStringExtra(BUNDLE_KEY) != null) {
			try {
				JSONObject requestData = new JSONObject(this.getIntent().getStringExtra(BUNDLE_KEY));
				startSession(requestData);
			} catch(JSONException e) {
				DialogUtils.showNeutralButtonDialog(this, "Error", "There was an error reading the QR code");
			}
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
				Session session = new Session();
				Message message = Message.obtain();
				try {
					session.setRequestData(requestData);
					mAttributeSet.clear();
					mAttributeSet = session.getAttributeSet();
					//mAuthorizationListAdapter.addAll
					message.what = ATTRIBUTE_SET_PARSE_SUCCESS;
					mHandler.sendMessage(message);
				} catch (JSONException e) {
					Logger.error(getClass(), e.getMessage());
					message.what = ATTRIBUTE_SET_PARSE_ERROR;
					mHandler.sendMessage(message);
				} catch (MissingRequestValueException e) {
					Logger.error(getClass(), e.getMessage());
					message.what = ATTRIBUTE_SET_PARSE_ERROR;
					mHandler.sendMessage(message);
				}
			}
			
		}).start();
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case ATTRIBUTE_SET_PARSE_SUCCESS:
					mAuthorizationListAdapter.notifyDataSetChanged();
				break;
				case ATTRIBUTE_SET_PARSE_ERROR:
				
				break;
				default:
				break;
			}
			
			return true;
		}
	});
}
