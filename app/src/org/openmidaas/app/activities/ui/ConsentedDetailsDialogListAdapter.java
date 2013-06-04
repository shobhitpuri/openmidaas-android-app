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
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Logger;
import org.openmidaas.library.model.core.AbstractAttribute;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConsentedDetailsDialogListAdapter extends BaseAdapter {
	
	private List<AbstractAttribute<?>> attributeList;

	private Activity mActivity;
	
	public void setActivity(Activity activity) {
		this.mActivity = activity;
	}
	
	public synchronized void setDataSource(List<AbstractAttribute<?>> list) {
		this.attributeList = list;
	}
	
	@Override
	public int getCount() {
		return this.attributeList.size();
	}

	@Override
	public AbstractAttribute<?> getItem(int position) {
		return this.attributeList.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = infalInflater.inflate(R.layout.conset_details_row, null);
			 viewHolder = new ViewHolder();
			 viewHolder.tvAttributeName = (TextView) convertView.findViewById(R.id.tvAttributeName);
			 viewHolder.tvAttributeValue = (TextView) convertView.findViewById(R.id.tvAttributeValue);
			 convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.tvAttributeName.setText("");
		viewHolder.tvAttributeValue.setText("");
		AbstractAttribute<?> attribute = getItem(position);
		if(attribute != null) {
			Logger.debug(getClass(), "attribute: " + attribute.getName() + "-OK. Setting TextViews");
			String attributeName = attribute.getName();
			CategoryMap map = CategoryMap.get(attributeName);
			if(map != null) {
				viewHolder.tvAttributeName.setText(map.getAttributeLabel());
			} else {
				viewHolder.tvAttributeName.setText(attributeName);
			}
			viewHolder.tvAttributeValue.setText(attribute.toString());
		} else {
			Logger.error(getClass(), "Attribute is null - not expected");
		}
		return convertView;
	}

	private static class ViewHolder {
		TextView tvAttributeName;
		TextView tvAttributeValue;
	}
}
