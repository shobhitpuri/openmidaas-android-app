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
package org.openmidaas.app.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openmidaas.library.model.core.AbstractAttribute;

public abstract class AbstractAttributeSet{
	
	protected final int TIMEOUT = 500;
	
	protected final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
	
	private String mKey;
	
	protected String mType;
	
	private boolean isEssentialRequested = false;
	
	private boolean isVerifiedRequested = false;
	
	private String mLabel;
	
	private AbstractAttribute<?> mSelectedAttribute;
	
	protected List<AbstractAttribute<?>> mAttributeList = new ArrayList<AbstractAttribute<?>>();
	
	public abstract void fetch();
	
	public abstract void onModify();

	public String getKey() {
		return mKey;
	}

	public String getType() {
		return mType;
	}

	public boolean isEssentialRequested() {
		return isEssentialRequested;
	}

	public boolean isVerifiedRequested() {
		return isVerifiedRequested;
	}

	public String getLabel() {
		return mLabel;
	}

	public AbstractAttribute<?> getSelectedAttribute() {
		return mSelectedAttribute;
	}

	public List<AbstractAttribute<?>> getAttributeList() {
		return mAttributeList;
	}

	public void setKey(String key) {
		this.mKey = key;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public void setEssentialRequested(boolean isEssentialRequested) {
		this.isEssentialRequested = isEssentialRequested;
	}

	public void setVerifiedRequested(boolean isVerifiedRequested) {
		this.isVerifiedRequested = isVerifiedRequested;
	}

	public void setLabel(String mLabel) {
		this.mLabel = mLabel;
	}

	public void setSelectedAttribute(AbstractAttribute<?> selectedAttribute) {
		this.mSelectedAttribute = selectedAttribute;
	}

	public void setAttributeList(List<AbstractAttribute<?>> attributeList) {
		this.mAttributeList = attributeList;
	}

}
