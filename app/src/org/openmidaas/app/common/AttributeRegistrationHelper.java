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
package org.openmidaas.app.common;

import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.InitializeVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class AttributeRegistrationHelper {
	
	public static void verifyAttribute(final Activity activity, final String dialogText, final String toastText, final AbstractAttribute<?> attribute) {
		final ProgressDialog mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage(dialogText);
		mProgressDialog.show();
		attribute.startVerification(new InitializeVerificationCallback() {

			@Override
			public void onSuccess() {
				Logger.info(getClass(), toastText);
				DialogUtils.showToast(activity, toastText);
				try {
					attribute.save();
				} catch (MIDaaSException e) {
					DialogUtils.showNeutralButtonDialog(activity, "Error", e.getError().getErrorMessage());
				} catch (InvalidAttributeValueException e) {
					DialogUtils.showNeutralButtonDialog(activity, "Error", e.getMessage());
				}
				activity.sendBroadcast(new Intent().setAction(Intents.ATTRIBUTE_LIST_CHANGE_EVENT));
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialog.dismiss();
						
					}
					
				});
			}

			@Override
			public void onError(MIDaaSException exception) {
				mProgressDialog.dismiss();
				Logger.info(getClass(), exception.getError().getErrorMessage());
				DialogUtils.showNeutralButtonDialog(activity, "Error", exception.getError().getErrorMessage());
			}
			
		});
	}

}
