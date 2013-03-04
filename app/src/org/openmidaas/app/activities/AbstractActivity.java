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

import android.app.Activity;
import android.os.Bundle;


public abstract class AbstractActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(getLayoutResourceId());
	}
	
	/**
	 * Override in inheriting class to set the resource ID
	 * of the view. 
	 * @return
	 */
	public abstract int getLayoutResourceId();
	
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
