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
package org.openmidaas.app.activities.ui;

import java.util.ArrayList;

/**
 * 
 * ADT for a list header. 
 *
 */
public abstract class AbstractListHeader implements AddButtonTouchDelegate{
	
	protected String mGroupName;
	
	protected ArrayList<AbstractAttributeListElement> mList = new ArrayList<AbstractAttributeListElement>();

	protected AddButtonTouchDelegate addButtonDelegate;
	
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
	public ArrayList<AbstractAttributeListElement> getList() {
		return mList;
	}
	
	/**
	 * Returns the delegate for the add button
	 * @return delegate for the add button 
	 */
	public AddButtonTouchDelegate getAddButtonHandler() {
		return addButtonDelegate;
	}
}
