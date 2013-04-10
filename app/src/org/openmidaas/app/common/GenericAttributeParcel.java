package org.openmidaas.app.common;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class GenericAttributeParcel implements Parcelable {
	
	private String mHelperText;
	
	private String mAttributeName;
	
	private String mAttributeValue = "";
	
	private long mAttributeId;
	
	private Map<String, Integer> mLabelToInputTypeMap; 
	
	private final String DEFAULT_HELPER_TEXT = "Please provide the following information";
	
	public GenericAttributeParcel(String attributeName) {
		this.mLabelToInputTypeMap = new HashMap<String, Integer>();
		mHelperText = DEFAULT_HELPER_TEXT;
		mAttributeName = attributeName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		final int N = mLabelToInputTypeMap.size();
		dest.writeInt(N);
		if ( N > 0 ) {
			for(Map.Entry<String, Integer> entry:mLabelToInputTypeMap.entrySet()) {
				dest.writeString(entry.getKey());
				Integer i = entry.getValue();
				dest.writeInt(i);	
			}
		}
		dest.writeString(mHelperText);
		dest.writeString(mAttributeName);
		dest.writeString(mAttributeValue);
	}
	
	public static final Parcelable.Creator<GenericAttributeParcel> CREATOR = new Parcelable.Creator<GenericAttributeParcel>() {

		@Override
		public GenericAttributeParcel createFromParcel(Parcel source) {
			return new GenericAttributeParcel(source);
		}

		@Override
		public GenericAttributeParcel[] newArray(int size) {
			return new GenericAttributeParcel[size];
		}
	};
	
	private GenericAttributeParcel(Parcel source) {
		this.mLabelToInputTypeMap = new HashMap<String, Integer>();
		final int N = source.readInt();
		for(int i=0; i<N; i++) {
			String key = source.readString();
			Integer it = source.readInt();
			mLabelToInputTypeMap.put(key, it);
		}
		mHelperText = source.readString();
		mAttributeName = source.readString();
		mAttributeValue = source.readString();
	}

	public void addToList(String labelText, Integer intputType) {
		this.mLabelToInputTypeMap.put(labelText, intputType);
	}
	
	public void setHelperText(String helperText) {
		this.mHelperText = helperText;
	}
	
	public String getHelperText() {
		return mHelperText;
	}
	
	public Map<String, Integer> getLabelToInputTypeMap() {
		return mLabelToInputTypeMap;
	}
	
	public void setAttributeName(String name) {
		this.mAttributeName = name;
	}
	
	public String getAttributeName() {
		return mAttributeName;
	}
	
	public void setAttributeValue(String value) {
		this.mAttributeValue = value;
	}
	
	public String getAttributeValue() {
		return mAttributeValue;
	}
	
	public void setAttributeId(long id) {
		this.mAttributeId = id;
	}
	
	public long getAttributeId() {
		return mAttributeId;
	}
}
