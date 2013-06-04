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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmidaas.app.R;
import org.openmidaas.app.activities.ui.ConsentedDetailsDialog;
import org.openmidaas.app.activities.ui.list.ConsentListAdapter;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.ConsentManager;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;
import org.openmidaas.library.persistence.core.AttributeDataCallback;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ManageConsentActivity extends AbstractActivity {
	
	private ListView mConsentListView;
	
	private TextView tvNoConsents;
	
	private ConsentListAdapter mListAdapter;
	
	private Activity mActivity;
	
	private Map<String, ArrayList<AbstractAttribute<?>>> mConsentMap;
	
	private String[] mKeys;
	
	private final int REFRESH_CONSENT_LIST = 1;
	
	private final int ERROR_CREATING_CONSENT_LIST = -1;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		mConsentListView = (ListView)findViewById(R.id.lvConsents);
		tvNoConsents = (TextView)findViewById(R.id.tvNoConsentsPresent);
		tvNoConsents.setVisibility(View.GONE);
		mListAdapter = new ConsentListAdapter(this);
		mActivity = this;
		mConsentMap = new HashMap<String, ArrayList<AbstractAttribute<?>>>();
		fetchAllAttributesAndDisplayConsent();
		final FragmentManager fm = getFragmentManager();
		mConsentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				
				ConsentedDetailsDialog dialog = new ConsentedDetailsDialog();
				dialog.setData(mKeys[position], mConsentMap.get(mKeys[position]));
				dialog.setRetainInstance(true);
				dialog.show(fm, "consent_dialog");
			}
			
		});
	}

	private void fetchAllAttributesAndDisplayConsent() {
		this.mConsentMap.clear();
		Logger.debug(getClass(), "Building consent list");
		new Thread(new Runnable() {

			@Override
			public void run() {
				AttributePersistenceCoordinator.getAllAttributes(new AttributeDataCallback() {

					@Override
					public void onError(MIDaaSException exception) {
						Message msg = Message.obtain();
						msg.what =ERROR_CREATING_CONSENT_LIST;
						mHandler.sendMessage(msg);
					}

					@Override
					public void onSuccess(List<AbstractAttribute<?>> attributeList) {
						displayConsentSummary(attributeList);
					}
					
				});
			}
			
		}).start();
		
	}
	
	private void displayConsentSummary(List<AbstractAttribute<?>> attributeList) {
		List<String> rpIds = ConsentManager.getAllConsents(mActivity);
		for(String rpId: rpIds) {
			for(AbstractAttribute<?> attribute: attributeList)
			if(attribute != null) {
				if(ConsentManager.checkConsent(mActivity, rpId, attribute)) {
					if(mConsentMap.containsKey(rpId)) {
						mConsentMap.get(rpId).add(attribute);
					} else {
						ArrayList<AbstractAttribute<?>> list = new ArrayList<AbstractAttribute<?>>();
						list.add(attribute);
						mConsentMap.put(rpId, list);
					}
				}
			}
		}
		this.mKeys = this.mConsentMap.keySet().toArray(new String[this.mConsentMap.size()]);
		Message msg = Message.obtain();
		msg.what =REFRESH_CONSENT_LIST;
		mHandler.sendMessage(msg);
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case REFRESH_CONSENT_LIST:
					if(!mConsentMap.isEmpty()) {
						tvNoConsents.setVisibility(View.GONE);
						mConsentListView.setVisibility(View.VISIBLE);
						mListAdapter.setConsentDataList(mConsentMap);
						mConsentListView.setAdapter(mListAdapter);
						mListAdapter.notifyDataSetChanged();
					} else {
						mConsentListView.setVisibility(View.GONE);
						tvNoConsents.setVisibility(View.VISIBLE);
					}
				break;
				case ERROR_CREATING_CONSENT_LIST:
					DialogUtils.showNeutralButtonDialog(mActivity, "Error", "Error creating consent list");
				break;
			}
			return true;
		}
		
	});
	
	@Override
	protected String getTitlebarText() {
		return (getString(R.string.consentActivityTitleText));
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.consent_summary);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(refreshConsentReceiver, new IntentFilter(Intents.REFRESH_CONSENT_LIST));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(refreshConsentReceiver);
	}
	
	private BroadcastReceiver refreshConsentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			fetchAllAttributesAndDisplayConsent();
		}
	};
}
