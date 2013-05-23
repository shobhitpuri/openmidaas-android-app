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
import android.widget.TextView;

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
	public View getView(int position, View convertView, ViewGroup parent) {
		LabelViewHolder viewHolder;
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.authorization_spinner_label, null);
			viewHolder = new LabelViewHolder();
			viewHolder.tvSpinnerLabel = (TextView)convertView.findViewById(R.id.tvSpinnerLabel);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (LabelViewHolder) convertView.getTag();
		}
		viewHolder.tvSpinnerLabel.setText("");
		if(position == 0) {
			viewHolder.tvSpinnerLabel.setText(mContext.getString(R.string.noAttributeSelectedLabel));
		} else {
			viewHolder.tvSpinnerLabel.setText(mAttributeList.get(position).toString());
		}
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ValueViewHolder viewHolder;
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.attribute_spinner_custom_textview, null);
			 viewHolder = new ValueViewHolder();
			 viewHolder.tvAttributeSpinnerValue = (CheckedTextView)convertView.findViewById(R.id.tvAttributeSpinnerValue);
			 convertView.setTag(viewHolder);
		} else {
			viewHolder = (ValueViewHolder)convertView.getTag();
		}
		viewHolder.tvAttributeSpinnerValue.setText("");
		if(position == mSelectedIndex) {
			viewHolder.tvAttributeSpinnerValue.setChecked(true);
		} else {
			viewHolder.tvAttributeSpinnerValue.setChecked(false);
		}
		if(position == 0) {
			viewHolder.tvAttributeSpinnerValue.setText(mContext.getString(R.string.noAttributeSelectedLabel));
		}
		else{
			AbstractAttribute<?> attribute = mAttributeList.get(position);
			if(attribute.getLabel() != null && (!(attribute.getLabel().isEmpty()))) {
				viewHolder.tvAttributeSpinnerValue.setText(attribute.getLabel());
			} else {
				viewHolder.tvAttributeSpinnerValue.setText("");
			}
			switch (mAttributeList.get(position).getState()) {
			case PENDING_VERIFICATION:
				viewHolder.tvAttributeSpinnerValue.append("(Pending Verification)\n");
				break;
			case VERIFIED:
				viewHolder.tvAttributeSpinnerValue.append("(Verified)\n");
				break;
			case NOT_VERIFIED:
				viewHolder.tvAttributeSpinnerValue.append("(Not Verified)\n");
			default:
				break;
			}
			viewHolder.tvAttributeSpinnerValue.append(attribute.toString());
			
		}
		return convertView;
	}
	
	private static class ValueViewHolder {
		private CheckedTextView tvAttributeSpinnerValue;
	}
	
	private static class LabelViewHolder {
		private TextView tvSpinnerLabel;
	}
}
