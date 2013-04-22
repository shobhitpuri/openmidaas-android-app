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
import java.util.List;
import org.openmidaas.app.activities.ui.AbstractListHeader;

/**
 * 
 * Class that manages the list headers 
 *
 */
public class CategoryManager {
	
	private static CategoryManager mInstance = null;
	
	private List<AbstractListHeader> mCategoriesList;
	
	private CategoryManager() {
		mCategoriesList = new ArrayList<AbstractListHeader>();
		
	}
	
	public static synchronized CategoryManager getInstance() {
		if(mInstance == null) {
			mInstance = new CategoryManager();
		}
		return mInstance;
	}
	
	/**
	 * Returns the category list
	 * @return
	 */
	public List<AbstractListHeader> getCategoriesList() {
		return mCategoriesList;
	}

}
