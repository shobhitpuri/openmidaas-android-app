package org.openmidaas.app.activities;

public class Category {
	
	private String mName;
	
	private String mLabel;

	public Category(String name, String label) {
		this.mName = name;
		this.mLabel = label;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String label) {
		this.mLabel = label;
	}
}
