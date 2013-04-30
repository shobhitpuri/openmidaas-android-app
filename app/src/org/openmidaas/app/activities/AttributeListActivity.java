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
import org.openmidaas.app.activities.listui.AddressCategory;
import org.openmidaas.app.activities.listui.EmailCategory;
import org.openmidaas.app.activities.listui.GenericAttributeListElement;
import org.openmidaas.app.activities.listui.OnListElementLongTouch;
import org.openmidaas.app.activities.listui.OnListElementTouch;
import org.openmidaas.app.activities.listui.PersonalListCategory;
import org.openmidaas.app.common.CategoryManager;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.library.model.GenericAttributeFactory;
import org.openmidaas.library.model.InvalidAttributeNameException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;
import org.openmidaas.library.persistence.core.AttributeDataCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

public class AttributeListActivity extends AbstractActivity {

	private ExpandableListView mAttributeListView;
	
	private static AttributeListActivity mActivity;
	
	private AttributeExpandableListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mAttributeListView = (ExpandableListView)findViewById(R.id.listViewAttributes);
		mAttributeListView.setClickable(true);
		mAttributeListView.setItemsCanFocus(true);
		mActivity = this;
		mAttributeListView.setGroupIndicator(null);
		mAttributeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new AttributeExpandableListAdapter(mActivity);
		mAttributeListView.setAdapter(mAdapter);
		
		mAttributeListView.setOnChildClickListener(new OnChildClickListener() {
			 @Override
	            public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPosition, int childPosition, long id) {
				 OnListElementTouch element = (OnListElementTouch) mAdapter.getChild(groupPosition, childPosition);
				 if(element != null) {
					 element.onTouch(mActivity);
				 }
				
				return false;
			}
		});
		
		
		mAttributeListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
						OnListElementLongTouch element =  (OnListElementLongTouch) 
								mAdapter.getChild(ExpandableListView.getPackedPositionGroup(id), ExpandableListView.getPackedPositionChild(id));
						if(element != null) {
							element.onLongTouch(mActivity);
							
						}
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(attributeEvent, new IntentFilter(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));	
		refreshAttributeList();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(attributeEvent);
	}
	
	@Override
	protected int getLayoutResourceId() {
		return (R.layout.list_view);
	}

	@Override
	protected String getTitlebarText() {
		return ("Your Information");
	}
	
	/**
	 * Helper method that gets/refreshes the attribute list 
	 */
	private void refreshAttributeList() {
		AttributePersistenceCoordinator.getAllAttributes(new AttributeDataCallback () {

			@Override
			public void onSuccess(final List<AbstractAttribute<?>> list) {
				addItemsToList(list);
			}

			@Override
			public void onError(MIDaaSException exception) {
				DialogUtils.showNeutralButtonDialog(AttributeListActivity.this, "Error", exception.getError().getErrorMessage());
			}
			
		});
		
	}
	
	/**
	 * Adds data to the map in a thread-safe way
	 * @param attributeList the attribute list from the library.  
	 */
	private void addItemsToList(final List<AbstractAttribute<?>> attributeList) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				createEmptyAttributeList();
				for(AbstractAttribute<?> attribute:attributeList) {
					// reverse lookup: attribute name -> category name -> category list
					CategoryManager.getMap().get(CategoryMap.get(attribute.getName()).getCategoryName()).addAttribute(attribute);
				} 
				mHandler.sendEmptyMessage(1);
			}
			
		}).start();
	}
	
	/**
	 * Bootstraps an empty list of attributes in a specific order.  
	 * E.g., Personal Info (first name, last name), Email
	 * Add your category to the end of the list. 
	 * Since the map is using the LinkedHashMap implementation, the order
	 * of insertion is guaranteed. 
	 */
	private void createEmptyAttributeList() {
		try {
			PersonalListCategory personalCategory = new PersonalListCategory();
			personalCategory.getList().add(new GenericAttributeListElement(GenericAttributeFactory.createAttribute(Constants.AttributeNames.GIVEN_NAME)));
			personalCategory.getList().add(new GenericAttributeListElement(GenericAttributeFactory.createAttribute(Constants.AttributeNames.MIDDLE_NAME)));
			personalCategory.getList().add(new GenericAttributeListElement(GenericAttributeFactory.createAttribute(Constants.AttributeNames.FAMILY_NAME)));
			CategoryManager.getMap().put(Constants.ATTRIBUTE_CATEGORY_PERSONAL, personalCategory);
			EmailCategory emailHeader = new EmailCategory();
			CategoryManager.getMap().put(CategoryMap.EMAIL.getCategoryName(), emailHeader);
			AddressCategory addressCategory = new AddressCategory();
			CategoryManager.getMap().put(Constants.ATTRIBUTE_CATEGORY_ADDRESS, addressCategory);
		} catch (InvalidAttributeNameException e) {
			DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
		}
		
	}
	
	/**
	 * Broadcast receiver that refreshes the attribute list. 
	 */
	private BroadcastReceiver attributeEvent = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshAttributeList();
		}
		
	};
	
	/**
	 * Notify the UI thread to refresh the list with new data. 
	 */
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			mAdapter.notifyDataSetChanged();
			for(int i=0; i<mAdapter.getGroupCount(); i++) {
				mAttributeListView.expandGroup(i);
			}
			return true;
		}
	});
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(mActivity, HomeScreen.class));
		finish();
	}
}
