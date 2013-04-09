package org.openmidaas.app.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CategoryLookupMap {
	
	/**
	 * Map of UI label -> attribute name
	 */
	FIRST_NAME("Personal", "first_name"),
	LAST_NAME("Personal", "last_name"),
	DOB("Personal", "date_of_birth"),
	EMAIL("Email", "email");
	
	private final String mCategoryDisplayLabel;
	
	private final String mAttributeName;
	
	private static final Map<String, CategoryLookupMap> lookup = new HashMap<String, CategoryLookupMap>();
	private static final List<String> list = new ArrayList<String>();
	
	static {
		for(CategoryLookupMap map: CategoryLookupMap.values()) {
			if(!(list.contains(map.getCategoryDisplayLabel()))) {
				list.add(map.getCategoryDisplayLabel());
			}
		}
	}
	
	static {
		for(CategoryLookupMap map: CategoryLookupMap.values())
			lookup.put(map.getAttributeName(), map);
	}
	
	private CategoryLookupMap(String label, String name) {
		this.mCategoryDisplayLabel = label;
		this.mAttributeName = name;
	}
	
	public String getAttributeName() {
		return mAttributeName;
	}
	
	public String getCategoryDisplayLabel() {
		return mCategoryDisplayLabel;
	}
	
	/**
	 * Reverse lookup based on attribute name to get the display label
	 * @param attributeName
	 * @return
	 */
	public static CategoryLookupMap get(String attributeName) {
		return lookup.get(attributeName);
	}
	
	public static List<String> getCategories() {
		return list;
	}

}
