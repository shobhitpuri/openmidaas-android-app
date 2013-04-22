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

import org.openmidaas.library.model.core.AbstractAttribute;

/**
 * 
 * ADT for a single element in the list
 *
 */
public abstract class AbstractAttributeListElement implements ListElementTouchDelegate{
	
	protected AbstractAttribute<?> mAttribute;
	
	protected ListElementTouchDelegate mOnTouchDelegate;

	/**
	 * Returns the attribute for that list element
	 * @return the attribute for that list element
	 */
	public AbstractAttribute<?> getAttribute() {
		return mAttribute;
	}

	/**
	 * Sets the attribute for that list element
	 * @param attribute the attribute for that list element
	 */
	public void setAttribute(AbstractAttribute<?> attribute) {
		this.mAttribute = attribute;
	}

	public ListElementTouchDelegate getOnTouchDelegate() {
		return mOnTouchDelegate;
	}

	/**
	 * Override this to return the display format of the attribute. 
	 * @return a user readable format for the attribute
	 */
	public abstract String getRenderedAttributeValue();
	
}
