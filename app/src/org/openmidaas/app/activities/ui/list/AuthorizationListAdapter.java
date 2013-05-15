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
import java.util.List;

import org.openmidaas.app.R;
import org.openmidaas.app.activities.ui.spinner.AttributeSpinnerAdapter;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.attributeset.AbstractAttributeSet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AuthorizationListAdapter extends BaseAdapter {
	
	private Activity mActivity;
	
	private List<AbstractAttributeSet> mAttributeSet;
	
	public AuthorizationListAdapter(Activity activity) {
		mActivity = activity;
		mAttributeSet = new ArrayList<AbstractAttributeSet>();
	}
	
	public synchronized void setList(List<AbstractAttributeSet> attributeSet) {
		mAttributeSet = attributeSet;
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
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		final ViewHolder viewHolder;
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
		String attributeUserFriendlyName = null;
		final AbstractAttributeSet attributeSet = mAttributeSet.get(position);
		if(attributeSet.getLabel() == null || (attributeSet.getLabel().isEmpty())) {
			if(CategoryMap.get(attributeSet.getType()) == null) {
				attributeUserFriendlyName = attributeSet.getKey();
			} else {
				attributeUserFriendlyName = CategoryMap.get(attributeSet.getType()).getAttributeLabel();
			}
		} else {
			attributeUserFriendlyName = attributeSet.getLabel();
		}
		viewHolder.tvAttributeLabel.setText(attributeUserFriendlyName);
		viewHolder.tvAttributeLabel.setTypeface(null, Typeface.NORMAL);
		if(attributeSet.isEssentialRequested()) {
			viewHolder.tvAttributeLabel.setTypeface(null, Typeface.BOLD);
		}
		final AttributeSpinnerAdapter mSpinnerAdapter = new AttributeSpinnerAdapter(mActivity, android.R.layout.simple_spinner_item, attributeSet.getAttributeList());
		mSpinnerAdapter.setDropDownViewResource(R.layout.attribute_spinner_custom_textview);
		
		viewHolder.attributeSelector.setAdapter(mSpinnerAdapter);
		viewHolder.attributeSelector.setPrompt("Select " + attributeUserFriendlyName);
		viewHolder.attributeSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int selectedPositionInSpinner, long id) {
				Logger.debug(getClass(), "Setting selected value: " + attributeSet.getAttributeList().get(selectedPositionInSpinner).toString());
				attributeSet.setSelectedAttribute(attributeSet.getAttributeList().get(selectedPositionInSpinner));
				mSpinnerAdapter.setSelectedIndex(selectedPositionInSpinner);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		return convertView;
	}
	
	private static class ViewHolder {
		TextView tvAttributeLabel;
		Spinner attributeSelector;
	}
}
