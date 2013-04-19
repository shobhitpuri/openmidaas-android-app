package org.openmidaas.app.activities;

import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.EmailAttributeFactory;

import android.app.Activity;

public class EmailAttributeListElement extends AbstractAttributeListElement implements OnTouchDelegate {

	public EmailAttributeListElement() {
		//mAttribute = EmailAttributeFactory.createAttribute();
		mOnTouchDelegate = this;
	}

	@Override
	public void onTouch(Activity activity) {
		if(mAttribute.getState() == ATTRIBUTE_STATE.PENDING_VERIFICATION) {
			UINotificationUtils.showCodeCollectionDialog(activity, mAttribute);
	}
}

	@Override
	public String getRenderedAttributeValue() {
		return mAttribute.getValue().toString();
	}
}
