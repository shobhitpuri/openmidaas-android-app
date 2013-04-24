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
package org.openmidaas.app.activities.listui;

import java.util.ArrayList;
import java.util.List;
import org.openmidaas.library.model.core.AbstractAttribute;
import android.app.Activity;

/**
 * 
 * ADT for a list header. 
 *
 */
public abstract class AbstractListCategory {
	
	protected String mGroupName;
	
	protected List<AbstractAttributeListElement> mList = new ArrayList<AbstractAttributeListElement>();
	
	/**
	 * Returns the group name
	 * @return the group name 
	 */
	public String getGroupName() {
		return mGroupName;
	}
	
	/**
	 * Returns attribute list element array
	 * @return attribute list element array
	 */
	public List<AbstractAttributeListElement> getList() {
		return mList;
	}
	
	/**
	 * Implement this to specify what happens when the add button 
	 * is touched by the user for a specific category
	 * @param activity activity from which this method is 
	 * being called
	 */
	public abstract void onAddButtonTouch(Activity activity);
	
	/**
	 * Implement this to add the attribute to the list. 
	 * @param attribute attribute to add
	 */
	public abstract void addAttribute(AbstractAttribute<?> attribute);
	
}
