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

import org.openmidaas.app.R;
import org.openmidaas.app.activities.ui.list.AbstractAttributeListElement;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
			    .setNeutralButton(activity.getString(R.string.okButtonText),  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
			    })
			     .show();
			}
		});
	}
	
	/**
	 * Displays a list of essential attributes that are missing from an authorization. 
	 * @param activity
	 * @param message
	 * @param proceedButtonListener
	 */
	public static void showEssentialAttributeMissingDialog(final Activity activity, final String message, final DialogInterface.OnClickListener proceedButtonListener) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				AlertDialog.Builder alert = new AlertDialog.Builder(activity);
				alert.setTitle("Missing information");
				alert.setMessage(message);
			    alert.setNegativeButton("Proceed",  proceedButtonListener);
			    alert.setPositiveButton(activity.getString(R.string.backButtonText),  new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {}
			    });
			    alert.show();
			}
		});
	}
	
	/**
	 * Displays a name-value collection dialog for general attributes
	 * @param activity
	 */
	public static void showNameValueCollectionDialog(final Activity activity) {
		 final AlertDialog.Builder alert = new AlertDialog.Builder(activity);  
		 LinearLayout lila1= new LinearLayout(activity);
	     lila1.setOrientation(1); //1 is for vertical orientation
	     final EditText etName = new EditText(activity); 
	     etName.setHint(activity.getString(R.string.generalAttributeNamePrompt));
	     final EditText etValue = new EditText(activity);
	     etValue.setHint(activity.getString(R.string.generalAttributeValuePrompt));
	     lila1.addView(etName);
	     lila1.addView(etValue);
	     alert.setView(lila1);
	     alert.setTitle(activity.getString(R.string.generalAttributeDialogCollectionTitle));
	     alert.setNeutralButton(activity.getString(R.string.saveButtonText), new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				final String name = etName.getText().toString();
				final String value = etValue.getText().toString();
				Utils.createGenericAttribute(activity, name, value, null);
			}
	    	 
	     });
	     final AlertDialog alertDialog = alert.create();
	     etName.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View arg0, boolean hasFocus) {
					if (hasFocus) {
						alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
				}
				
			});
			alertDialog.show();
	}
	
	/**
	 * Displays the birthday dialog. The onDateSet function is being called twice for some reason. So a flag is set to ensure that
	 * the attribute is created and saved only once. 
	 * @param activity
	 * @param attribute
	 */
	public static void showBirthdayDatePickerDialog(final Activity activity, final AbstractAttribute<?> attribute) {
		DatePickerDialog.OnDateSetListener dateLisenter = new DatePickerDialog.OnDateSetListener() {
			boolean hasAlreadyFired = false;
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				if(hasAlreadyFired) {
					return;
				} else {
					if(attribute != null) {
						Utils.modifyGenericAttribute(activity, (GenericAttribute)attribute,  Utils.getFormattedDate(dayOfMonth, monthOfYear, year));
					} else {
						Utils.createGenericAttribute(activity, Constants.AttributeNames.BIRTHDAY, Utils.getFormattedDate(dayOfMonth, monthOfYear, year), null);
					}
					hasAlreadyFired = true;
				}
			}
		};
		DatePickerDialog datePicker = new DatePickerDialog(activity, dateLisenter, 01, 01, 2000);
		datePicker.setTitle(activity.getString(R.string.birthdayDialogTitle));
		datePicker.show();	
		
	}
	
	/**
	 * Displays a dialog containing a list of radio items as defined by the itemsToDisplay parameter
	 * @param activity
	 * @param message
	 * @param itemsToDisplay
	 * @param listener
	 */
	public static void showRadioButtonDialog(final Activity activity, final String message, final String[] itemsToDisplay, final DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle(message);
	    builder.setSingleChoiceItems(itemsToDisplay, -1, listener);
	    builder.create().show();  
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
		showDeleteAttributeDialog(activity,listElement, Utils.getAttributeDetailsLabel(attribute));
	}
	
	/**
	 * Displays a dialog to modify an attribute
	 * @param activity
	 * @param attribute
	 */
	public static void showGenericAttributeModificationDialog(final Activity activity, final AbstractAttribute<?> attribute) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(Utils.getAttributeDisplayLabel(attribute));
		alert.setMessage(activity.getString(R.string.modifyAttributeText));

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		alert.setView(input);
		
		alert.setPositiveButton(activity.getString(R.string.saveButtonText), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			Utils.modifyGenericAttribute(activity, (GenericAttribute)attribute, value.toString());
		  }
		});

		alert.setNegativeButton(activity.getString(R.string.cancelButtonText), new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {}
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
	
	/**
	 * Displays a dialog that enables the user to enter the verification code they obtained. 
	 * @param activity
	 * @param attribute
	 */
	public static void showCodeCollectionDialog(final Activity activity, final AbstractAttribute<?> attribute) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Verify " + attribute.getName());
		alert.setMessage(activity.getString(R.string.enterPinText));

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		alert.setView(input);

		alert.setPositiveButton(activity.getString(R.string.okButtonText), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			final ProgressDialog progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage("Verifying...");
			progressDialog.show();
			attribute.completeVerification(value.toString(), new CompleteVerificationCallback() {

				@Override
				public void onSuccess() {
					try {
						attribute.save();
					} catch (MIDaaSException e) {
						DialogUtils.showNeutralButtonDialog(activity, activity.getString(R.string.defaultErrorDialogTitle), e.getError().getErrorMessage());
					} catch (InvalidAttributeValueException e) {
						DialogUtils.showNeutralButtonDialog(activity, activity.getString(R.string.defaultErrorDialogTitle), e.getMessage());
					}
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if(progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
						}
					});
					
				}

				@Override
				public void onError(MIDaaSException exception) {
					if(progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					DialogUtils.showNeutralButtonDialog(activity, activity.getString(R.string.defaultErrorDialogTitle), exception.getError().getErrorMessage());
				}
				
			});
		  }
		});

		alert.setNegativeButton(activity.getString(R.string.cancelButtonText), new DialogInterface.OnClickListener() {
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
	
	/**
	 * Dialog that asks the user whether they would like to delete an attribute 
	 * @param activity
	 * @param listElement
	 * @param message
	 */
	public static void showDeleteAttributeDialog(final Activity activity,  final AbstractAttributeListElement listElement, final String message) {
		final AbstractAttribute<?> attribute = listElement.getAttribute();
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				
			    builder.setTitle(activity.getString(R.string.deleteButtonText));
			    builder.setMessage(message);
			    if(attribute.getState() != ATTRIBUTE_STATE.VERIFIED){
				
				    builder.setNegativeButton("Re-verify", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(attribute.getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION || attribute.getState() == ATTRIBUTE_STATE.NOT_VERIFIED) {
								if ( attribute.getVerificationMethod()!=null)
									AttributeRegistrationHelper.verifyAttribute(activity, "Starting "+attribute.getName() +" verification...", "You should receive a "+attribute.getVerificationMethod()+" soon at : "+listElement.getRenderedAttributeValue(), attribute);
								else
									AttributeRegistrationHelper.verifyAttribute(activity, "Starting "+attribute.getName() +" verification...", "Verification code sent to "+listElement.getRenderedAttributeValue(), attribute);
							}else if(attribute.getState() == ATTRIBUTE_STATE.VERIFIED){
								Toast.makeText(activity, activity.getString(R.string.verifiedAttributeText), Toast.LENGTH_LONG).show();
							}else if(attribute.getState() == ATTRIBUTE_STATE.NOT_VERIFIABLE){
								Toast.makeText(activity, activity.getString(R.string.unverifiableAttributeText), Toast.LENGTH_LONG).show();
							}else if(attribute.getState() == ATTRIBUTE_STATE.UNKNOWN){
								Toast.makeText(activity, activity.getString(R.string.unknownAttributeStateText), Toast.LENGTH_LONG).show();
							}else if(attribute.getState() == ATTRIBUTE_STATE.ERROR_IN_SAVE){
								Toast.makeText(activity, activity.getString(R.string.errorInAttributeSaveText), Toast.LENGTH_LONG).show();
							}
						} 
					});
			    }
			    builder.setPositiveButton(activity.getString(R.string.deleteButtonText),  new DialogInterface.OnClickListener() {
			    	
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							attribute.delete();
						
							activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
						} catch (MIDaaSException e) {
							
						}
					}
			    });	
			    AlertDialog dialog = builder.create();
			    dialog.show();
			}
			
		});
	}
	
	/**
	 * Dialog that collects an attribute value when a list element is pressed 
	 * @param activity
	 * @param attributeName
	 * @param label
	 */
	public static void showAttributeValueCollectionDialog( final Activity activity, final String attributeName, String label) {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		alert.setTitle("Adding " + label);
		alert.setMessage("Enter your " + label);

		// Set an EditText view to get user input 
		final EditText input = new EditText(activity);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		alert.setView(input);

		alert.setPositiveButton(activity.getString(R.string.okButtonText), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			Utils.createGenericAttribute(activity, attributeName, value.toString(), null);
			activity.startActivity(activity.getIntent());
			activity.finish();
			activity.overridePendingTransition(0, 0);
		  }
		});

		alert.setNegativeButton(activity.getString(R.string.cancelButtonText), new DialogInterface.OnClickListener() {
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
