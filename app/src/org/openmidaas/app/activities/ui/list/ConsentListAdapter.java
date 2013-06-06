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
package org.openmidaas.app.activities.ui.list;

import java.util.ArrayList;
import java.util.Map;

import org.openmidaas.app.R;
import org.openmidaas.app.common.Utils;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConsentListAdapter extends BaseAdapter {

	private Map<String, ArrayList<AbstractAttribute<?>>> mList;
	
	private String[] mKeys;
	
	private Activity mActivity;
	
	public ConsentListAdapter(Activity activity) {
		this.mActivity = activity;
	}
	
	public synchronized void setConsentDataList(Map<String, ArrayList<AbstractAttribute<?>>> data) {
		this.mList = data;
		mKeys = this.mList.keySet().toArray(new String[this.mList.size()]);
	}
	
	@Override
	public int getCount() {
		return this.mList.size();
	}

	@Override
	public ArrayList<AbstractAttribute<?>> getItem(int location) {
		return this.mList.get(mKeys[location]);
	}

	@Override
	public long getItemId(int itemId) {
		return itemId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = infalInflater.inflate(R.layout.consent_summary_row, null);
			 viewHolder = new ViewHolder();
			 viewHolder.tvRpId = (TextView)convertView.findViewById(R.id.tvRpId);
			 viewHolder.tvConsentNames = (TextView)convertView.findViewById(R.id.tvConsentNames);
			 convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.tvRpId.setText("");
		viewHolder.tvConsentNames.setText("");
		viewHolder.tvRpId.setText(mKeys[position]);
		String consentNamesAsCSVs = buildConsentNames(position);
		viewHolder.tvConsentNames.setText(consentNamesAsCSVs);
		return convertView;
	}
	
	private static class ViewHolder {
		TextView tvRpId;
		TextView tvConsentNames;
	}
	
	private String buildConsentNames(int position) {
		StringBuilder builder = new StringBuilder();
		String prefix = "";
		builder.append("Consented to release: ");
		ArrayList<AbstractAttribute<?>> attributeList = (ArrayList<AbstractAttribute<?>>) this.getItem(position);
		for(AbstractAttribute<?> attribute: attributeList) {
			builder.append(prefix);
			prefix = ", ";
			builder.append(Utils.getAttributeDisplayLabel(attribute));
		}
		return builder.toString();
	}
}
