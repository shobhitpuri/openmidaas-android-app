package org.openmidaas.app.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CategoryLookupMap {
	
	/**
	 * Map of UI label -> attribute name
	 * category_label, attribute_name, attribute_label
	 */
	FIRST_NAME("Personal", "first_name", "First Name"),
	LAST_NAME("Personal", "last_name", "Last Name"),
	DOB("Personal", "dob", "Date of Birth"),
	EMAIL("Email", "email", "Personal Email");
	
	private final String mCategoryDisplayLabel;
	
	private final String mAttributeName;
	
	private final String mAttributeDisplayLabel;
	
	private static final Map<String, CategoryLookupMap> lookup = new HashMap<String, CategoryLookupMap>();
	private static final List<String> list = new ArrayList<String>();
	private static Map<String, ArrayList<CategoryLookupMap>> enumForCateory = new HashMap<String, ArrayList<CategoryLookupMap>>();
	private static final List<String> attributeLabels = new ArrayList<String>();
	static {
		for(CategoryLookupMap map: CategoryLookupMap.values()) {
			if(!(list.contains(map.getCategoryDisplayLabel()))) {
				list.add(map.getCategoryDisplayLabel());
			}
		}
	
		for(CategoryLookupMap map: CategoryLookupMap.values())
			lookup.put(map.getAttributeName(), map);
	
	
		for(CategoryLookupMap map: CategoryLookupMap.values()) {
			ArrayList<CategoryLookupMap> list = enumForCateory.get(map.getCategoryDisplayLabel());
			if(list ==  null) {
				list = new ArrayList<CategoryLookupMap>();
				list.add(map);
			} else {
				list.add(map);
			}
			enumForCateory.put(map.getCategoryDisplayLabel(),list);
		}
	}
	
	
	private CategoryLookupMap(String label, String name, String attributeLabel) {
		this.mCategoryDisplayLabel = label;
		this.mAttributeName = name;
		this.mAttributeDisplayLabel = attributeLabel;
	}
	
	public String getAttributeName() {
		return mAttributeName;
	}
	
	public String getCategoryDisplayLabel() {
		return mCategoryDisplayLabel;
	}
	
	public String getAttributeDisplayLabel() {
		return mAttributeDisplayLabel;
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
	
	public static ArrayList<CategoryLookupMap> getEnumsForCategory(String category) {
		return enumForCateory.get(category);
	}
	
	public static ArrayList<String> getLabelsForCategory(String category) {
		ArrayList<CategoryLookupMap> list = enumForCateory.get(category);
		ArrayList<String> labelsList = new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			labelsList.add(list.get(i).getAttributeDisplayLabel());
		}
		return labelsList;
	}

}
