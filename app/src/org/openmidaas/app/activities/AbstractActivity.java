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

package org.openmidaas.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * Abstract Activity class that sets the layout. All
 * activities must inherit from this class. 
 *
 */
public abstract class AbstractActivity extends Activity{

	protected TextView mTitlebarText;
	
	protected ProgressDialog mProgressDialog;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(getLayoutResourceId());
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.setCanceledOnTouchOutside(false);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getTitlebarText());
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * Override to return your custom titlebar test.
	 * @return - custom titlebar text
	 */
	protected abstract String getTitlebarText();
	
	/**
	 * Override in inheriting class to set the resource ID
	 * of the view. 
	 * @return
	 */
	protected abstract int getLayoutResourceId();
	
	protected void dismissDialog() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
