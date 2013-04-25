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

import java.util.List;
import org.openmidaas.app.R;
import org.openmidaas.app.activities.listui.AbstractAttributeListElement;
import org.openmidaas.app.activities.listui.AbstractListCategory;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.library.model.core.AbstractAttribute;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AttributeExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Activity mActivity;

	private List<AbstractListCategory> mGroupHeaders;
	
	public AttributeExpandableListAdapter(Activity activity, List<AbstractListCategory> groupHeaders) {
		this.mActivity = activity;
		this.mGroupHeaders = groupHeaders;
	}

	public void clearExistingAttributeEntries() {
		mGroupHeaders.clear();
	}
	
	public void setHeaders(List<AbstractListCategory> groupHeaders) {
		this.mGroupHeaders = groupHeaders;
	}
	
	/**
	 * 
	 * View holder implementation for the group and 
	 * child view to support smooth scrolling so as 
	 * to avoid finding the view element every time
	 * a pass in made through the data set. 
	 * For addition info, see:
	 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	 */
	static class GroupViewHolder {
		TextView tvTitle;
		Button btnAdd;
	}
	
	static class ChildViewHolder {
		TextView tvAttributeLabel;
		TextView tvAttributeValue;
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		return mGroupHeaders.get(arg0).getList().get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildViewHolder childViewHolder;
		AbstractAttributeListElement listElement = (AbstractAttributeListElement)getChild(groupPosition, childPosition);
		AbstractAttribute<?> attribute = listElement.getAttribute();
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_child_layout, null);
           childViewHolder = new ChildViewHolder();
           childViewHolder.tvAttributeLabel = (TextView)convertView.findViewById(R.id.tvAttributeName);
           childViewHolder.tvAttributeValue = (TextView)convertView.findViewById(R.id.etAttributeValue);
           convertView.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) convertView.getTag();
		}
			
		childViewHolder.tvAttributeLabel.setText("");
		childViewHolder.tvAttributeValue.setText("");
		if(attribute.getLabel() == null || attribute.getLabel().isEmpty()) {	
			childViewHolder.tvAttributeLabel.setText(CategoryMap.get(attribute.getName()).getAttributeLabel());
		} else {
			childViewHolder.tvAttributeLabel.setText(attribute.getLabel());
		}
		if(attribute.getValue() != null) {
			childViewHolder.tvAttributeValue.setText(listElement.getRenderedAttributeValue());
		}
		childViewHolder.tvAttributeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		switch (attribute.getState()) {
			case PENDING_VERIFICATION:
				childViewHolder.tvAttributeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.warning, 0);
				break;
			case VERIFIED:
				childViewHolder.tvAttributeValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0);
				break;
			default:
				break;
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return mGroupHeaders.get(arg0).getList().size();
	}

	@Override
	public Object getGroup(int arg0) {
		return mGroupHeaders.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return mGroupHeaders.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
		GroupViewHolder groupViewHolder;
		final AbstractListCategory category = (AbstractListCategory)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_layout, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvGroupName);
            groupViewHolder.btnAdd = (Button) convertView.findViewById(R.id.btnAddMore);
            convertView.setTag(groupViewHolder);
        } else {
        	groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        
        groupViewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				category.onAddButtonTouch(mActivity);
			}
		});
        groupViewHolder.tvTitle.setText(category.getGroupName().trim());
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
