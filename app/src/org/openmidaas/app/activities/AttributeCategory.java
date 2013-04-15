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

import org.openmidaas.library.model.core.AbstractAttribute;

public class AttributeCategory {
	
	private String mCategoryName;
	
	private List<AbstractAttribute<?>> mAttributeList;
	
	public AttributeCategory(String categoryName) {
		this.mCategoryName = categoryName;
		this.mAttributeList = new ArrayList<AbstractAttribute<?>>();
	}
	
	public List<AbstractAttribute<?>> getAttributesInCategory(){
		return mAttributeList;
	}
	
	public String getCategory() {
		return mCategoryName;
	}

}
