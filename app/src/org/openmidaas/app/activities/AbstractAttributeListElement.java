package org.openmidaas.app.activities;

import org.openmidaas.library.model.core.AbstractAttribute;

public abstract class AbstractAttributeListElement {
	
	protected AbstractAttribute<?> mAttribute;
	
	protected OnTouchDelegate mOnTouchDelegate;

	public AbstractAttribute<?> getAttribute() {
		return mAttribute;
	}

	public void setAttribute(AbstractAttribute<?> attribute) {
		this.mAttribute = attribute;
	}

	public OnTouchDelegate getOnTouchDelegate() {
		return mOnTouchDelegate;
	}

	public abstract String getRenderedAttributeValue();
	
}
