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

import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
}
