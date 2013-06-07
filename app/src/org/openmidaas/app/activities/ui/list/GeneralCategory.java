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
package org.openmidaas.app.activities.ui.list;

import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;

public class GeneralCategory extends AbstractListCategory {
	
	public GeneralCategory() {
		mGroupName = Constants.ATTRIBUTE_CATEGORY_GENERAL;
	}

	@Override
	public void onAddButtonTouch(Activity activity) {
		// show general name-value collection dialog
		DialogUtils.showNameValueCollectionDialog(activity);
	}

	@Override
	public void addAttribute(AbstractAttribute<?> attribute) {
		GenericAttributeListElement element = new GenericAttributeListElement((GenericAttribute) attribute);
		mList.add(element);
	}
}
