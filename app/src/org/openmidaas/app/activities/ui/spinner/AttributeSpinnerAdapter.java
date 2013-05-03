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
package org.openmidaas.app.activities.ui.spinner;

import java.util.List;

import org.openmidaas.app.R;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class AttributeSpinnerAdapter extends ArrayAdapter<AbstractAttribute<?>> {
	
	private Context mContext;
	
	private List<AbstractAttribute<?>> mAttributeList;
	
	private int mSelectedIndex;
	
	public AttributeSpinnerAdapter(Context context, int textViewResourceId,
			List<AbstractAttribute<?>> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mAttributeList = objects;
	}
	
	public void setSelectedIndex(int index) {
		mSelectedIndex = index;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.attribute_spinner_custom_textview, null);
			 viewHolder = new ViewHolder();
			 viewHolder.tvAttributeSpinnerValue = (CheckedTextView)convertView.findViewById(R.id.tvAttributeSpinnerValue);
			 convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.tvAttributeSpinnerValue.setText("");
		if(mAttributeList.get(position).getLabel() != null && (!(mAttributeList.get(position).getLabel().isEmpty()))) {
			viewHolder.tvAttributeSpinnerValue.setText(mAttributeList.get(position).getLabel());
		} else {
			viewHolder.tvAttributeSpinnerValue.setText("");
		}
		switch (mAttributeList.get(position).getState()) {
		case PENDING_VERIFICATION:
			viewHolder.tvAttributeSpinnerValue.append(" (Pending Verification)\n");
			break;
		case VERIFIED:
			viewHolder.tvAttributeSpinnerValue.append(" (Verified)\n");
			break;
		case NOT_VERIFIED:
			viewHolder.tvAttributeSpinnerValue.append(" (Not Verified)\n");
		default:
			break;
		}
		viewHolder.tvAttributeSpinnerValue.append(mAttributeList.get(position).toString());
		if(position == mSelectedIndex) {
			viewHolder.tvAttributeSpinnerValue.setChecked(true);
		} else {
			viewHolder.tvAttributeSpinnerValue.setChecked(false);
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		private CheckedTextView tvAttributeSpinnerValue;
	}
}
