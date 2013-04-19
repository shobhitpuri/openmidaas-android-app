package org.openmidaas.app.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmidaas.app.common.CategoryMap;

public class CategoryManager {
	
	private static CategoryManager mInstance = null;
	
	private List<ListHeader> mCategoriesList;
	
	private Map<String, ListHeader> mListMap;
	
	private CategoryManager() {
		mCategoriesList = new ArrayList<ListHeader>();
		mListMap = new HashMap<String, ListHeader>();
	}
	
	public static synchronized CategoryManager getInstance() {
		if(mInstance == null) {
			mInstance = new CategoryManager();
		}
		return mInstance;
	}
	
	public List<ListHeader> getCategoriesList() {
		return mCategoriesList;
	}
	
	public Map<String, ListHeader> getMap() {
		return mListMap;
	}

}
