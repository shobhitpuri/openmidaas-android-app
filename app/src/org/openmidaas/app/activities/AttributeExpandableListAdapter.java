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

import org.openmidaas.app.R;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AttributeExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Activity mActivity;

	private ArrayList<ListHeader> mGroupHeaders;
	
	//private List<AttributeCategory> mCategories;
	
	public AttributeExpandableListAdapter(Activity activity, ArrayList<ListHeader> groupHeaders) {
		this.mActivity = activity;
		this.mGroupHeaders = groupHeaders;
	}
	
//	public AttributeExpandableListAdapter(Activity activity, List<AttributeCategory> categories) {
//		this.mActivity = activity;
//		this.mCategories = categories;
//	}

	public void clearExistingAttributeEntries() {
		//mCategories.clear();
		mGroupHeaders.clear();
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		//return mCategories.get(arg0).getAttributesInCategory().get(arg1);
		return mGroupHeaders.get(arg0).getList().get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		AbstractAttribute<?> attribute = (AbstractAttribute<?>)getChild(groupPosition, childPosition);
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_child_layout, null);
		}
		
		TextView tvAttributeName = (TextView)convertView.findViewById(R.id.tvAttributeName);
		TextView tvAttributeValue = (TextView)convertView.findViewById(R.id.etAttributeValue);
		tvAttributeName.setText("");
		tvAttributeValue.setText("");
		tvAttributeName.setText(CategoryMap.get(attribute.getName()).getAttributeDisplayLabel());
		if(attribute.getValue() != null) {
			tvAttributeValue.setText(attribute.getValue().toString());
		}
		tvAttributeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		switch (attribute.getState()) {
			case PENDING_VERIFICATION:
				tvAttributeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.warning, 0);
				break;
			case VERIFIED:
				tvAttributeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0);
				break;
			default:
				break;
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		//return mCategories.get(arg0).getAttributesInCategory().size();
		return mGroupHeaders.get(arg0).getList().size();
	}

	@Override
	public Object getGroup(int arg0) {
		//return mCategories.get(arg0);
		return mGroupHeaders.get(arg0);
	}

	@Override
	public int getGroupCount() {
		//return mCategories.size();
		return mGroupHeaders.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
		final ListHeader header = (ListHeader)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_layout, null);
        }
        
        TextView tv = (TextView) convertView.findViewById(R.id.tvGroupName);
        Button btnAdd = (Button) convertView.findViewById(R.id.btnAddMore);
        btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				header.getAddButtonHandler().onButtonClick(mActivity);
			}
		});
        tv.setText(header.getGroupName().trim());
        //tv.setText(mCategories.get(groupPosition).getCategory());
        return convertView;
    }

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

}
