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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmidaas.app.common.CategoryMap;

public class CategoryManager {
	
	private static CategoryManager mInstance = null;
	
	private List<AbstractListHeader> mCategoriesList;
	
	private Map<String, AbstractListHeader> mListMap;
	
	private CategoryManager() {
		mCategoriesList = new ArrayList<AbstractListHeader>();
		mListMap = new HashMap<String, AbstractListHeader>();
	}
	
	public static synchronized CategoryManager getInstance() {
		if(mInstance == null) {
			mInstance = new CategoryManager();
		}
		return mInstance;
	}
	
	public List<AbstractListHeader> getCategoriesList() {
		return mCategoriesList;
	}
	
	public Map<String, AbstractListHeader> getMap() {
		return mListMap;
	}

}
