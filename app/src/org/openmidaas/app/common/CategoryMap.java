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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CategoryMap {
	
	/**
	 * Map of UI label -> attribute name
	 * category_label, attribute_name, attribute_label
	 */
	FIRST_NAME(Constants.ATTRIBUTE_CATEGORY_PERSONAL, "first_name", "First Name"),
	LAST_NAME(Constants.ATTRIBUTE_CATEGORY_PERSONAL, "last_name", "Last Name"),
	EMAIL(Constants.ATTRIBUTE_CATEGORY_EMAIL, "email", "Email");
	
	private final String mCategoryDisplayLabel;
	
	private final String mAttributeName;
	
	private final String mAttributeDisplayLabel;
	
	private static final Map<String, CategoryMap> lookup = new HashMap<String, CategoryMap>();
	private static final List<String> list = new ArrayList<String>();
	private static Map<String, ArrayList<CategoryMap>> enumForCateory = new HashMap<String, ArrayList<CategoryMap>>();
	private static final List<CategoryMap> categories = new ArrayList<CategoryMap>();
	static {
		for(CategoryMap map: CategoryMap.values()) {
			if(!(list.contains(map.getCategoryName()))) {
				list.add(map.getCategoryName());
			}
		}
	
		for(CategoryMap map: CategoryMap.values())
			lookup.put(map.getAttributeName(), map);
	
	
		for(CategoryMap map: CategoryMap.values()) {
			ArrayList<CategoryMap> list = enumForCateory.get(map.getCategoryName());
			if(list ==  null) {
				list = new ArrayList<CategoryMap>();
				list.add(map);
			} else {
				list.add(map);
			}
			enumForCateory.put(map.getCategoryName(),list);
		}
		
		for(CategoryMap map: CategoryMap.values()) {
			categories.add(map);
		}
	}
	
	
	private CategoryMap(String label, String name, String attributeLabel) {
		this.mCategoryDisplayLabel = label;
		this.mAttributeName = name;
		this.mAttributeDisplayLabel = attributeLabel;
	}
	
	public String getAttributeName() {
		return mAttributeName;
	}
	
	public String getCategoryName() {
		return mCategoryDisplayLabel;
	}
	
	public String getAttributeLabel() {
		return mAttributeDisplayLabel;
	}
	
	/**
	 * Reverse lookup based on attribute name to get the display label
	 * @param attributeName
	 * @return
	 */
	public static CategoryMap get(String attributeName) {
		return lookup.get(attributeName);
	}
	
	public static List<String> getCategories() {
		return list;
	}
	
	public static ArrayList<CategoryMap> getEnumsForCategory(String category) {
		return enumForCateory.get(category);
	}
	
	public static ArrayList<String> getLabelsForCategory(String category) {
		ArrayList<CategoryMap> list = enumForCateory.get(category);
		ArrayList<String> labelsList = new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			labelsList.add(list.get(i).getAttributeLabel());
		}
		return labelsList;
	}
	
	public static List<CategoryMap> getEnums() {
		return categories;
	}

}
