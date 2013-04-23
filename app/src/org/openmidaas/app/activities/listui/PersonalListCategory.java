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

import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Constants;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;

/**
 * 
 * Personal category
 *
 */
public class PersonalListCategory extends AbstractListCategory {
	
	public PersonalListCategory() {
		mGroupName = Constants.ATTRIBUTE_CATEGORY_PERSONAL;
	}

	@Override
	public void onAddButtonTouch(Activity activity) {
		
	}

	@Override
	public void addAttribute(AbstractAttribute<?> attribute) {
		if(attribute.getName().equals(CategoryMap.FIRST_NAME.getAttributeName())) {
			if(mList.get(0).getAttribute().getName().equals(CategoryMap.FIRST_NAME.getAttributeName())) {
				mList.get(0).setAttribute(attribute);
			}
		} else if(attribute.getName().equals(CategoryMap.LAST_NAME.getAttributeName())) {
			if(mList.get(1).getAttribute().getName().equals(CategoryMap.LAST_NAME.getAttributeName())) {
				mList.get(1).setAttribute(attribute);
			}
		}
	}
}
