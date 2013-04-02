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

import org.json.JSONObject;
import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.EmailAttribute;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;
import org.openmidaas.library.persistence.core.AttributeDataCallback;
import org.openmidaas.library.persistence.core.EmailDataCallback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AttributeListActivity extends AbstractActivity{

	private ListView mAttributeListView;
	
	private Button addEmail;
	
	private AttributeListActivity mActivity;
	
	private List<AbstractAttribute<?>> mAttributeList;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mAttributeListView = (ListView)findViewById(R.id.listViewAttributes);
		mAttributeListView.setClickable(true);
		mAttributeListView.setItemsCanFocus(true);
		mAttributeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		addEmail = (Button)findViewById(R.id.button1);
		mActivity = this;
		refreshAttributeList();
		
		mAttributeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Logger.info(getClass(), "list clicked...");
				// when the cell is clicked, check to see if the attribute state is pending verification. 
				if(mAttributeList.get(arg2).getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION) {
					showPinCollectionDialog(mAttributeList.get(arg2));
				}
			}
		});
		
		
		mAttributeListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(Settings.ATTRIBUTE_DIAGNOSTICS_ENABLED) {
					showAttributeDetails(arg2);
				}
				return true;
			}
		});
	}
	
	
	private void showAttributeDetails(int pos) {
		AbstractAttribute<?> attribute = mAttributeList.get(pos);
		String message = "Name: " + attribute.getName() + "\n" +
				 "Value: " + attribute.getValue() + "\n"; 
		String str = null;
		String[] jwsParams = null;
		JSONObject object = null;
		if(attribute.getSignedToken() != null) {
			jwsParams = attribute.getSignedToken().split("\\."); 
			try {
				str = new String(Base64.decode(jwsParams[1], Base64.NO_WRAP), "UTF-8");
				object = new JSONObject(str);
				if(object != null) {
					message += "Subject ID: " + object.getString("subjectID");
					message += "Signature: " + jwsParams[2];
				}
			} catch(Exception e) {
				str = null;
			}
		}
		new AlertDialog.Builder(mActivity)
	    .setTitle("Attribute Diagnostics")
	    .setMessage(message)
	    .setNeutralButton("OK",  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
	    })
	     .show();
	}

	private void showPinCollectionDialog(final AbstractAttribute<?> attribute) {
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
	protected int getLayoutResourceId() {
		return (R.layout.list_view);
	}

	/**
	 * Helper method that gets/refreshes the attribute list 
	 */
	private void refreshAttributeList() {
		AttributePersistenceCoordinator.getAllAttributes(new AttributeDataCallback () {

			@Override
			public void onSuccess(final List<AbstractAttribute<?>> list) {
				mAttributeList = list;
				if(list.isEmpty()) {
					startActivity(new Intent(AttributeListActivity.this, EmailRegistrationActivity.class));
					AttributeListActivity.this.finish();
				} else {
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mAttributeListView.setAdapter(new AttributeListAdapter(AttributeListActivity.this, list));
							
						}
						
					});
					
				}
			}

			@Override
			public void onError(MIDaaSException exception) {
				UINotificationUtils.showNeutralButtonDialog(AttributeListActivity.this, "Error", exception.getError().getErrorMessage());
			}
			
		});
		
		
		
		
		addEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AttributeListActivity.this, EmailRegistrationActivity.class));
				AttributeListActivity.this.finish();
			}
		});
	}
}
