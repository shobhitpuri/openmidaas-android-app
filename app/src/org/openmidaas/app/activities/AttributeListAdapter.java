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
import org.openmidaas.library.model.EmailAttribute;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AttributeListAdapter extends BaseAdapter {
	
	private Activity mActivity;

	private List<AbstractAttribute<?>> mAttributeList;
	
	private LayoutInflater inflater = null;
	
	public AttributeListAdapter(Activity activity, List<AbstractAttribute<?>> attributeList) {
		mActivity = activity;
		mAttributeList = attributeList;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mAttributeList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return (mAttributeList.get(arg0));
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View vi = view;
		if(vi == null) {
			vi = inflater.inflate(R.layout.list_view_cell, null);
		}
		//TextView attributeName = (TextView)vi.findViewById(R.id.tvAttributeName);
		TextView attributeValue = (TextView)vi.findViewById(R.id.tvAttributeValue);
		TextView attributeState = (TextView)vi.findViewById(R.id.tvAttributeState);
		Button btnDeleteAttribute = (Button)vi.findViewById(R.id.btnDeleteAttribute);
		final AbstractAttribute<?> attribute = mAttributeList.get(position);
		//attributeName.setText(attribute.getName());
		attributeValue.setText(attribute.getValue().toString());
		attributeState.setText(attribute.getState().toString());
		btnDeleteAttribute.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAttributeList.remove(attribute);
				AttributeListAdapter.this.notifyDataSetChanged();
				attribute.delete();
			}
		});
		return vi;
	}

}
