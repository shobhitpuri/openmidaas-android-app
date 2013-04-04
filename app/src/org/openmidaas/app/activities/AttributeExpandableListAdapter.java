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

import org.openmidaas.app.R;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class AttributeExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Activity mActivity;

	private ArrayList<ATTRIBUTE_STATE> mGroups;
	
	private ArrayList<ArrayList<AbstractAttribute<?>>> mChildren;
	
	public AttributeExpandableListAdapter(Activity activity, ArrayList<ATTRIBUTE_STATE> mStateList, ArrayList<ArrayList<AbstractAttribute<?>>> attributeList) {
		this.mActivity = activity;
		this.mGroups = mStateList;
		this.mChildren = attributeList;
	}

	public void clearExistingAttributeEntries() {
		if(this.mGroups != null) {
			this.mGroups.clear();
		}
		if(this.mChildren != null) {
			this.mChildren.clear();
		}
	}
	
	public void addItem(AbstractAttribute<?> attribute) {
		int pos = 1;
		if(!(mGroups.contains(attribute.getState()))) {
			// reserve the pending state to the top of the list. 
			mGroups.add(attribute.getState());
		}
		int index = mGroups.indexOf(attribute.getState());
		if(mChildren.size() < index + 1) {
			mChildren.add(new ArrayList<AbstractAttribute<?>>());
		}
		mChildren.get(index).add(attribute);
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		return mChildren.get(arg0).get(arg1);
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
		TextView tvAttributeValue = (TextView)convertView.findViewById(R.id.tvAttributeValue);
		tvAttributeName.setText(attribute.getName());
		tvAttributeValue.setText(attribute.getValue().toString());
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return mChildren.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		return mGroups.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        String group = (String) getGroup(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_layout, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tvGroupName);
        if(getGroup(groupPosition).equals(ATTRIBUTE_STATE.PENDING_VERIFICATION)) {
        	tv.setText("Pending Verification");
        } else if (getGroup(groupPosition).equals(ATTRIBUTE_STATE.VERIFIED)) {
        	tv.setText("Verified");
        } else if(getGroup(groupPosition).equals(ATTRIBUTE_STATE.NOT_VERIFIABLE)) {
        	tv.setText("General");
        }
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
