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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.common.AttributeRegistrationHelper;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.GenericAttributeParcel;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.AttributeFactory;
import org.openmidaas.library.model.EmailAttribute;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;
import org.openmidaas.library.persistence.core.AttributeDataCallback;
import org.openmidaas.library.persistence.core.EmailDataCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AttributeListActivity extends AbstractActivity{

	private ExpandableListView mAttributeListView;
	
	private TextView mAttributeInfoText;
	
	private static AttributeListActivity mActivity;
	
	private AttributeExpandableListAdapter mAdapter;
	
	private List<AbstractAttribute<?>> mAttributeList;
	
	private LinkedHashMap<String, ListHeader> mCategoriesWithChildren = new LinkedHashMap<String, ListHeader>();
	
	private ArrayList<ListHeader> mCategoriesList = new ArrayList<ListHeader>();
	
	private List<AttributeCategory> mCategoryList = new ArrayList<AttributeCategory>();
	
	private BroadcastReceiver attributeEvent = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshAttributeList();
		}
		
	};
	
	private Handler mHandler = new Handler() {
		 @Override
         public void handleMessage(Message msg)
         {
             mAdapter.notifyDataSetChanged();
             for(int i=0; i<mAdapter.getGroupCount(); i++) {
            	 mAttributeListView.expandGroup(i);
             }
             super.handleMessage(msg);
         }
		
	};
	
	private void initAttributeCategories() {
		for(String s:CategoryMap.getCategories()) {
			ListHeader header = new ListHeader();
			
			header.setGroupName(s);
			if(s.equalsIgnoreCase("Personal")) {
				header.setAddButtonHandler(new AddButtonClickDelegate() {

					@Override
					public void onButtonClick(Activity activity) {
						Logger.debug(getClass(), "nothing to implement");
					}
					
				});
				header.getList().add(AttributeFactory.getGenericAttributeFactory().setAttributeName(Constants.AttributeNames.FIRST_NAME).createAttribute());
				header.getList().add(AttributeFactory.getGenericAttributeFactory().setAttributeName(Constants.AttributeNames.LAST_NAME).createAttribute());
			} else if(s.equalsIgnoreCase("Email")) {
				header.setAddButtonHandler(new AddButtonClickDelegate() {

					@Override
					public void onButtonClick(Activity activity) {
						activity.startActivity(new Intent(activity, EmailRegistrationActivity.class));
						activity.finish();
					}
				
				});
			}
			mCategoriesWithChildren.put(s, header);
			mCategoriesList.add(header);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mAttributeListView = (ExpandableListView)findViewById(R.id.listViewAttributes);
		mAttributeInfoText = (TextView)findViewById(R.id.tvAttributeListInfo);
		mAttributeListView.setClickable(true);
		mAttributeListView.setItemsCanFocus(true);
		mActivity = this;
		mAttributeListView.setGroupIndicator(null);
		mAttributeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		mAdapter = new AttributeExpandableListAdapter(mActivity, mCategoriesList);
		//mAdapter = new AttributeExpandableListAdapter(mActivity, mCategoryList);
		mAttributeListView.setAdapter(mAdapter);
		
		refreshAttributeList();
		
		
		mAttributeListView.setOnChildClickListener(new OnChildClickListener() {

			 @Override
	            public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPosition, int childPosition, long id) {
				// when the cell is clicked, check to see if the attribute state is pending verification.
					AbstractAttribute<?> attribute = (AbstractAttribute<?>) mAdapter.getChild(groupPosition, childPosition);
					if(attribute.getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION) {
						showCodeCollectionDialog(attribute);
					} else {
						UINotificationUtils.showAttributeModificationDialog(mActivity, attribute);
					}
				
				return false;
			}
		});
		
		
		mAttributeListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
					if(Settings.ATTRIBUTE_DIAGNOSTICS_ENABLED) { 
						showAttributeDetails(position, (AbstractAttribute<?>) mAdapter.getChild(ExpandableListView.getPackedPositionGroup(id), ExpandableListView.getPackedPositionChild(id)));
					}
					return true;
				}
				return false;
			}
		});
	}
	
	
	
	
	private int addAttributeToCategory(AbstractAttribute<?> attribute) {
		Logger.debug(getClass(), CategoryMap.get(attribute.getName()).getCategoryDisplayLabel());
		ListHeader header = mCategoriesWithChildren.get(CategoryMap.get(attribute.getName()).getCategoryDisplayLabel());
		if(header == null) {
			header = new ListHeader();
			header.setGroupName(CategoryMap.get(attribute.getName()).getCategoryDisplayLabel());
			
			mCategoriesWithChildren.put(CategoryMap.get(attribute.getName()).getCategoryDisplayLabel(), header);
			mCategoriesList.add(header);
		}
		
		ArrayList<AbstractAttribute<?>> attributeChildrenList = header.getList();
		if(attributeChildrenList.isEmpty()) {
			attributeChildrenList.add(attribute);
		} else {
			int i=0;
			for(AbstractAttribute<?> a:attributeChildrenList) {
				if(a.getName().equalsIgnoreCase(attribute.getName())) {
					attributeChildrenList.set(i, attribute);	
				} 
				i++;
			}
		}
		//attributeChildrenList.add(attribute);
		header.setList(attributeChildrenList);
		
		
		return (mCategoriesList.indexOf(header));
	}
	
	
	private void showAttributeDetails(int position, AbstractAttribute<?> attribute) {
		String message = "Name: " + attribute.getName() + "\n" +
				 "Value: " + attribute.getValue() + "\n"; 
		String[] jwsParams = null;
		JSONObject object = null;
		if(attribute.getSignedToken() != null) {
			jwsParams = attribute.getSignedToken().split("\\."); 
			try {
				object = new JSONObject(new String(Base64.decode(jwsParams[1], Base64.NO_WRAP), "UTF-8"));
				if(object != null) {
					message += "Audience: " + object.getString("aud") + "\n";
					message += "Issuer: " + object.getString("iss") + "\n";
					message += "Subject: " + object.getString("sub") + "\n";
					message += "Signature: " + jwsParams[2];
				}
			} catch(Exception e) {
			}
		}
		UINotificationUtils.showDeleteAttributeDialog(mActivity,attribute, message);
	}

	private void showCodeCollectionDialog(final AbstractAttribute<?> attribute) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Verify " + attribute.getName());
		alert.setMessage("Enter the PIN you received below ");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			completeAttributeVerification(value.toString(), attribute);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    	
		  }
		});

		alert.show();
	}
	
	private void completeAttributeVerification(String code, AbstractAttribute<?> attribute) {
		attribute.completeVerification(code, new CompleteVerificationCallback() {

			@Override
			public void onSuccess() {
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						refreshAttributeList();
					}
					
				});
				
			}

			@Override
			public void onError(MIDaaSException exception) {
				UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", exception.getError().getErrorMessage());
			}
			
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(attributeEvent, new IntentFilter(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
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
	
	private void addItemsToList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mAdapter.clearExistingAttributeEntries();
				mCategoriesList.clear();
				mCategoriesWithChildren.clear();
				initAttributeCategories();
//				initAttributes();
				for(AbstractAttribute<?> attribute:mAttributeList) {
					//addAttributes(attribute);
					addAttributeToCategory(attribute);
				}
				mHandler.sendEmptyMessage(1);
			}
			
		}).start();
	}

	/**
	 * Helper method that gets/refreshes the attribute list 
	 */
	private void refreshAttributeList() {
		AttributePersistenceCoordinator.getAllAttributes(new AttributeDataCallback () {

			@Override
			public void onSuccess(final List<AbstractAttribute<?>> list) {
				mAttributeList = list;
				addItemsToList();
			}

			@Override
			public void onError(MIDaaSException exception) {
				UINotificationUtils.showNeutralButtonDialog(AttributeListActivity.this, "Error", exception.getError().getErrorMessage());
			}
			
		});
		
	}


	@Override
	protected String getTitlebarText() {
		return ("Your Info");
	}
	
	protected boolean hasTitlebarButtonVisible() { 
		return true;
	}
}
