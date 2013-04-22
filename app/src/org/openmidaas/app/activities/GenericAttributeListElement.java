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

import org.openmidaas.app.activities.ui.AbstractAttributeListElement;
import org.openmidaas.app.activities.ui.ListElementTouchDelegate;
import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.model.GenericAttribute;

import android.app.Activity;

public class GenericAttributeListElement extends AbstractAttributeListElement implements ListElementTouchDelegate {

	public GenericAttributeListElement(GenericAttribute attribute) {
		mAttribute = attribute;
		mOnTouchDelegate = this;
	}

	@Override
	public void onTouch(Activity activity) {
		UINotificationUtils.showGenericAttributeModificationDialog(activity, mAttribute);
	}

	@Override
	public String getRenderedAttributeValue() {
		return (mAttribute.getValue().toString());
	}
}
