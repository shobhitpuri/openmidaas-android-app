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
import java.util.Map;

import org.openmidaas.app.R;
import org.openmidaas.app.activities.ui.list.AbstractListCategory;
import org.openmidaas.app.activities.ui.list.AddressCategory;
import org.openmidaas.app.activities.ui.list.AttributeExpandableListAdapter;
import org.openmidaas.app.activities.ui.list.CreditCardCategory;
import org.openmidaas.app.activities.ui.list.EmailCategory;
import org.openmidaas.app.activities.ui.list.GeneralCategory;
import org.openmidaas.app.activities.ui.list.GenericAttributeListElement;
import org.openmidaas.app.activities.ui.list.OnListElementLongTouch;
import org.openmidaas.app.activities.ui.list.OnListElementTouch;
import org.openmidaas.app.activities.ui.list.PersonalListCategory;
import org.openmidaas.app.activities.ui.list.PhoneCategory;
import org.openmidaas.app.common.CategoryManager;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Intents;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AttributeListFragment extends Fragment {

	private ExpandableListView mAttributeListView;
	
	private RelativeLayout mRelativeLayout;
	
	private AttributeExpandableListAdapter mAdapter;
	
	private FragmentActivity mFragmentActiviy;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mFragmentActiviy = (FragmentActivity)super.getActivity();
		mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.attribute_list_view, container, false);
		mAttributeListView = (ExpandableListView)mRelativeLayout.findViewById(R.id.listViewAttributes);
		mAttributeListView.setClickable(true);
		mAttributeListView.setItemsCanFocus(true);
		mAttributeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mAdapter = new AttributeExpandableListAdapter(mFragmentActiviy);
		mAttributeListView.setAdapter(mAdapter);
		
		mAttributeListView.setOnChildClickListener(new OnChildClickListener() {
			 @Override
	            public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPosition, int childPosition, long id) {
				 OnListElementTouch element = (OnListElementTouch) mAdapter.getChild(groupPosition, childPosition);
				 if(element != null) {
					 element.onTouch(mFragmentActiviy);
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
							element.onLongTouch(mFragmentActiviy);
							
						}
					return true;
				}
				return false;
			}
		});
		return mRelativeLayout;
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(attributeEvent, new IntentFilter(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));	
		refreshAttributeList();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(attributeEvent);
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
				DialogUtils.showNeutralButtonDialog(mFragmentActiviy, "Error", exception.getError().getErrorMessage());
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
					CategoryMap map = CategoryMap.get(attribute.getName());
					// if we have a map definition
					if(map != null) {
						CategoryManager.getMap().get(map.getCategoryName()).addAttribute(attribute);
					} else {
						CategoryManager.getMap().get(Constants.ATTRIBUTE_CATEGORY_GENERAL).addAttribute(attribute);
					}
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
			Map<String, AbstractListCategory> map = CategoryManager.getMap();
			PersonalListCategory personalCategory = new PersonalListCategory();
			personalCategory.getList().add(new GenericAttributeListElement(GenericAttributeFactory.createAttribute(Constants.AttributeNames.GIVEN_NAME)));
			personalCategory.getList().add(new GenericAttributeListElement(GenericAttributeFactory.createAttribute(Constants.AttributeNames.MIDDLE_NAME)));
			personalCategory.getList().add(new GenericAttributeListElement(GenericAttributeFactory.createAttribute(Constants.AttributeNames.FAMILY_NAME)));
			map.put(Constants.ATTRIBUTE_CATEGORY_PERSONAL, personalCategory);
			EmailCategory emailHeader = new EmailCategory();
			map.put(CategoryMap.EMAIL.getCategoryName(), emailHeader);
			PhoneCategory phoneHeader = new PhoneCategory();
			map.put(CategoryMap.PHONE.getCategoryName(), phoneHeader);
			AddressCategory addressCategory = new AddressCategory();
			map.put(Constants.ATTRIBUTE_CATEGORY_ADDRESS, addressCategory);
			CreditCardCategory creditCardCategory = new CreditCardCategory();
			map.put(Constants.ATTRIBUTE_CATEGORY_CREDIT_CARD, creditCardCategory);
			GeneralCategory generalCategory = new GeneralCategory();
			map.put(Constants.ATTRIBUTE_CATEGORY_GENERAL, generalCategory);
		} catch (InvalidAttributeNameException e) {
			DialogUtils.showNeutralButtonDialog(mFragmentActiviy, "Error", e.getMessage());
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
	
}
