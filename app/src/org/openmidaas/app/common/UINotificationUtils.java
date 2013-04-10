/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.app.common;

import java.util.ArrayList;

import org.openmidaas.app.R;
import org.openmidaas.app.activities.EmailRegistrationActivity;
import org.openmidaas.app.activities.GenericAttributeCollectionActivity;
import org.openmidaas.app.activities.ListHeader;
import org.openmidaas.library.model.AttributeFactory;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * Helper class to display UI notifications. 
 *
 */
public final class UINotificationUtils {
	
	private static ProgressDialog mProgressDialog = null;
	
	/**
	 * Displays a "OK" dialog box.
	 * @param activity the activity displaying the notification
	 * @param title the dialog box title
	 * @param message the message
	 */
	public static void showNeutralButtonDialog(final Activity activity, final String title, final String message) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(activity)
			    .setTitle(title)
			    .setMessage(message)
			    .setNeutralButton("OK",  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
			    })
			     .show();
			}
		});
	}
	
	/**
	 * Displays a toast notification. 
	 * @param activity the activity displaying the notification 
	 * @param message the message to be displayed
	 */
	public static void showToast(final Activity activity, final String message) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static void showDeleteAttributeDialog(final Activity activity, final AbstractAttribute<?> attribute, final String message) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(activity)
			    .setTitle("Delete")
			    .setMessage(message)
			    .setNeutralButton("Delete",  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							attribute.delete();
						} catch (MIDaaSException e) {
							
						}
					}
			    })
			     .show();
			}
			
		});
	}
	
	public static void showIndeterministicProgressDialog(final Activity activity, final String message) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog = new ProgressDialog(activity);
				mProgressDialog.setMessage(message);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.show();
			}
		});
	}
	
	public static void dismissIndeterministicProgressDialog(final Activity activity) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(mProgressDialog != null) {
					if(mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
				}
			}
			
		});
		
	}
	
	public static void showAttributeChoiceDialog(final ListHeader header, final Activity activity) {
		if (header.getGroupName().equalsIgnoreCase("Personal")) {
			//final CharSequence[] items = {"First Name", "Last Name"};
			ArrayList<String> list = CategoryLookupMap.getLabelsForCategory("Personal");
			final CharSequence[] items = list.toArray(new CharSequence[list.size()]);
			new AlertDialog.Builder(activity)
			.setTitle("Please select the value you wish to enter")
	        .setSingleChoiceItems(items, 0, null)
	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                dialog.dismiss();
	                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
	                showAttributeValueCollectionDialog(activity, CategoryLookupMap.getEnumsForCategory("Personal").get(selectedPosition).getAttributeName(), items[selectedPosition].toString());
	            }
	        })
	        .show();
		} else {
			activity.startActivity(new Intent(activity, EmailRegistrationActivity.class));
			activity.finish();
		}
	}
	
	public static void showAttributeModificationDialog(final Activity activity, final AbstractAttribute<?> attribute) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Modifying " + attribute.getName());
		alert.setMessage("Enter a new value and tap save");

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		alert.setView(input);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			try {
				GenericAttribute generic = (GenericAttribute) attribute;
				generic.setValue(value.toString());
				AttributePersistenceCoordinator.saveAttribute(attribute);
				activity.sendBroadcast(new Intent().setAction("attribute_event"));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAttributeValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MIDaaSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    	
		  }
		});

		alert.show();
	}
	
	public static void showAttributeValueCollectionDialog( final Activity activity, final String attributeName, String label) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Adding " + label);
		alert.setMessage("Enter your " + label);

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			try {
				GenericAttribute attribute = AttributeFactory.getGenericAttributeFactory().createAttribute(attributeName, value.toString());
				activity.sendBroadcast(new Intent().setAction("attribute_event"));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAttributeValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MIDaaSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    	
		  }
		});

		alert.show();
	}
	
}
