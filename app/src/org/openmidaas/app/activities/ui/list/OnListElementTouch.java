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
package org.openmidaas.app.activities.ui.list;

import android.app.Activity;

/**
 * Implement this interface to specify  what needs to be
 * done when a single element is touched in the list
 */
public interface OnListElementTouch {
	/**
	 * Method that is called when a single element is
	 * touched
	 * in the list
	 * @param activity the calling activity
	 */
	public void onTouch(Activity activity);
	
}
