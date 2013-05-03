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

import org.openmidaas.app.activities.CreditCardActivity;
import org.openmidaas.app.common.Constants;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.content.Intent;

public class CreditCardCategory extends AbstractListCategory {

	public CreditCardCategory() {
		mGroupName = Constants.ATTRIBUTE_CATEGORY_CREDIT_CARD;
	}
	
	@Override
	public void onAddButtonTouch(Activity activity) {
		activity.startActivity(new Intent(activity, CreditCardActivity.class));
	}

	@Override
	public void addAttribute(AbstractAttribute<?> attribute) {
		CreditCardListElement element = new CreditCardListElement();
		element.setAttribute(attribute);
		mList.add(element);
	}

}
