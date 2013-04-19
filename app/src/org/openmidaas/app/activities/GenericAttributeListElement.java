package org.openmidaas.app.activities;

import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.model.GenericAttribute;

import android.app.Activity;

public class GenericAttributeListElement extends AbstractAttributeListElement implements OnTouchDelegate {

	public GenericAttributeListElement(GenericAttribute attribute) {
		mAttribute = attribute;
		mOnTouchDelegate = this;
	}

	@Override
	public void onTouch(Activity activity) {
		UINotificationUtils.showAttributeModificationDialog(activity, mAttribute);
	}

	@Override
	public String getRenderedAttributeValue() {
		return (mAttribute.getValue().toString());
	}
}
