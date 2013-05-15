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

import org.json.JSONObject;
import org.openmidaas.app.activities.ui.list.AbstractAttributeListElement;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.GenericAttributeFactory;
import org.openmidaas.library.model.InvalidAttributeNameException;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * Helper class to display UI dialogs. 
 *
 */
public final class DialogUtils {
	
	
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
	
	/**
	 * Displays the attribute details
	 * @param activity
	 * @param attribute
	 */
	public static void showAttributeDetails(Activity activity, AbstractAttributeListElement listElement) {
		AbstractAttribute<?> attribute = listElement.getAttribute();
		String message = "Name: " + attribute.getName() + "\n" +
				 "Value: " + attribute.toString() + "\n"; 
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
		showDeleteAttributeDialog(activity,listElement, message);
	}
	
	
	public static void showGenericAttributeModificationDialog(final Activity activity, final AbstractAttribute<?> attribute) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Enter " + CategoryMap.get(attribute.getName()).getAttributeLabel());
		alert.setMessage("Enter a new value and tap save");

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		alert.setView(input);
		
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			try {
				GenericAttribute generic = (GenericAttribute) attribute;
				generic.setValue(value.toString());
				generic.save();
				activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
			} catch (IllegalArgumentException e) {
				showNeutralButtonDialog(activity, "Error", e.getMessage());
			} catch (InvalidAttributeValueException e) {
				showNeutralButtonDialog(activity, "Error", e.getMessage());
			} catch (MIDaaSException e) {
				showNeutralButtonDialog(activity, "Error", e.getError().getErrorMessage());
			}
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    	
		  }
		});

		final AlertDialog alertDialog = alert.create();
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
			}
			
		});
		alertDialog.show();
	}
	
	public static void showCodeCollectionDialog(final Activity activity, final AbstractAttribute<?> attribute) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Verify " + attribute.getName());
		alert.setMessage("Enter the PIN you received below ");

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			attribute.completeVerification(value.toString(), new CompleteVerificationCallback() {

				@Override
				public void onSuccess() {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
						}
					});
					
				}

				@Override
				public void onError(MIDaaSException exception) {
					DialogUtils.showNeutralButtonDialog(activity, "Error", exception.getError().getErrorMessage());
				}
				
			});
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    	
		  }
		});
		final AlertDialog alertDialog = alert.create();
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
			}
			
		});
		alertDialog.show();
	}
	
	public static void showDeleteAttributeDialog(final Activity mActivity,  final AbstractAttributeListElement listElement, final String message) {
		final AbstractAttribute<?> attribute = listElement.getAttribute();
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(mActivity)
			    .setTitle("Delete")
			    .setMessage(message)
			    .setNegativeButton("Re-verify", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(attribute.getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION) {
							AttributeRegistrationHelper.verifyAttribute(mActivity, "sending email", "email sent", attribute);
						} else {
							Toast.makeText(mActivity, "Attribute already verified", Toast.LENGTH_LONG).show();
						}
					} 
				})
			    .setPositiveButton("Delete",  new DialogInterface.OnClickListener() {
			    	
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							attribute.delete();
						
							mActivity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
						} catch (MIDaaSException e) {
							
						}
					}
			    })	
			     .show();
			}
			
		});
	}
	
	
	public static void showAttributeValueCollectionDialog( final Activity activity, final String attributeName, String label) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Adding " + label);
		alert.setMessage("Enter your " + label);

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			try {
				GenericAttribute attribute = GenericAttributeFactory.createAttribute(attributeName);
				attribute.setValue(value.toString());
				attribute.save();
				activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
			} catch (InvalidAttributeValueException e) {
				showNeutralButtonDialog(activity, "Error", e.getMessage());
			} catch (MIDaaSException e) {
				showNeutralButtonDialog(activity, "Error", e.getError().getErrorMessage());
			} catch (InvalidAttributeNameException e) {
				showNeutralButtonDialog(activity, "Error", e.getMessage());
			}
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    	
		  }
		});

		final AlertDialog alertDialog = alert.create();
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
			}
			
		});
		alertDialog.show();
	}
}
