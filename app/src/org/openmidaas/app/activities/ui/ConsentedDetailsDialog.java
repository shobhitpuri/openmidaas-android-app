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
package org.openmidaas.app.activities.ui;

import java.util.List;
import org.openmidaas.app.R;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.ConsentManager;
import org.openmidaas.library.model.core.AbstractAttribute;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ConsentedDetailsDialog extends DialogFragment implements DialogInterface.OnClickListener {
	
	private ListView lvConsentedItems;
	
	private TextView tvInfo;
	
	private List<AbstractAttribute<?>> mAttributeList;
	
	private String mClientId;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);	
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater =getActivity().getLayoutInflater();
	    final View view = inflater.inflate(R.layout.consent_details, null);
	    lvConsentedItems = (ListView) view.findViewById(R.id.lvConsentedItems);
	    tvInfo = (TextView)view.findViewById(R.id.tvDialogInfoText);
	    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
	            .setNeutralButton(getActivity().getString(R.string.btnRevokeConsent), this);
	    alert.setView(view);
	    Logger.debug(getClass(), "Consent details dialog created");
	    return alert.create();
	}
	
	public void setData(String clientId, List<AbstractAttribute<?>> dataSource) {
		this.mClientId = clientId;
		this.mAttributeList = dataSource;
	}
	
	 @Override
	 public void onStart() {
		 super.onStart();
		 getDialog().setTitle(getActivity().getString(R.string.consentDetailsDialogTitle));
		 ConsentedDetailsDialogListAdapter adapter = new ConsentedDetailsDialogListAdapter();
		 adapter.setActivity(getActivity());
		 adapter.setDataSource(this.mAttributeList);
		 lvConsentedItems.setAdapter(adapter);
		 tvInfo.setText(getActivity().getString(R.string.consentDetailsDialogInfoText) + " "+ this.mClientId);
	 }

	@Override
	public void onClick(DialogInterface dialogInterface, int which) {
		if(!ConsentManager.revokeConsentForClient(getActivity(), mClientId)) {
			Logger.error(getClass(), "Error revoking consent");
			this.dismiss();
			DialogUtils.showNeutralButtonDialog(getActivity(), "Error", "Error revoking consent to: " + this.mClientId);
		} else {
			Logger.debug(getClass(), "Error revoked successfully");
			Logger.debug(getClass(), "Refreshing consent summary list");
			getActivity().sendBroadcast(new Intent().setAction(Intents.REFRESH_CONSENT_LIST));
		}
	}
}
