package org.openmidaas.app.activities;

import java.util.ArrayList;
import org.openmidaas.library.model.core.AbstractAttribute;

public class ListHeader {
	
	private String mGroupName;
	
	private ArrayList<AbstractAttribute<?>> mList = new ArrayList<AbstractAttribute<?>>();
	
	private String mGroupLabel;
	
	public ListHeader(){}
	
	public ListHeader(String groupName) {
		this.mGroupName = groupName;
	}
	
	public ListHeader(String groupName, String groupLabel) {
		this.mGroupName = groupName;
		this.mGroupLabel = groupLabel;
	}
	
	public void setGroupName(String name) {
		this.mGroupName = name;
	}
	
	public void setGroupLabel(String label) {
		this.mGroupLabel = label;
	}
	
	public String getGroupLabel() {
		return mGroupLabel;
	}
	
	public String getGroupName() {
		return mGroupName;
	}
	
	public void setList(ArrayList<AbstractAttribute<?>> list) {
		this.mList = list;
	}
	
	public ArrayList<AbstractAttribute<?>> getList() {
		return mList;
	}
}
