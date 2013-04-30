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
import java.util.Collections;
import java.util.List;
import org.openmidaas.app.R;
import org.openmidaas.app.session.AbstractAttributeSet;
import org.openmidaas.library.model.core.AbstractAttribute;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AuthorizationListAdapter extends BaseAdapter {
	
	private Activity mActivity;
	
	private List<AbstractAttributeSet> mAttributeSet;
	
	protected AuthorizationListAdapter(Activity activity) {
		mActivity = activity;
		mAttributeSet = new ArrayList<AbstractAttributeSet>();
	}
	
	public synchronized void add(AbstractAttributeSet attributeSet) {
		//mAttributeSet.addall.add(attributeSet);
	}
	
	@Override
	public int getCount() {
		return mAttributeSet.size();
	}

	@Override
	public Object getItem(int position) {
		return mAttributeSet.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = infalInflater.inflate(R.layout.authorization_view_row, null);
			 viewHolder = new ViewHolder();
			 viewHolder.tvAttributeLabel = (TextView)convertView.findViewById(R.id.tvAttributeLabel);
			 viewHolder.attributeSelector = (Spinner)convertView.findViewById(R.id.attributeSelector);
			 convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.tvAttributeLabel.setText("");
		viewHolder.tvAttributeLabel.setText(mAttributeSet.get(position).getLabel());
		ArrayAdapter<AbstractAttribute<?>> dataAdapter = new ArrayAdapter<AbstractAttribute<?>>(mActivity,
	                android.R.layout.simple_spinner_item, mAttributeSet.get(position).getAttributeList());
		viewHolder.attributeSelector.setAdapter(dataAdapter); 
		return convertView;
	}
	
	private static class ViewHolder {
		TextView tvAttributeLabel;
		Spinner attributeSelector;
	}
}
