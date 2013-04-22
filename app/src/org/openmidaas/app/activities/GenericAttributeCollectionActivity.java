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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openmidaas.app.R;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.common.GenericAttributeParcel;
import org.openmidaas.app.common.UINotificationUtils;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.GenericAttributeFactory;
import org.openmidaas.library.model.InvalidAttributeNameException;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.MIDaaSException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GenericAttributeCollectionActivity extends AbstractActivity {
	
	private TextView mHelperText;
	
	private Button mBtnDone;
	
	private GenericAttributeParcel mParcel;
	
	private List<EditText> mAttributeValuesList = new ArrayList<EditText>();
	
	private TableLayout mAttributesTableLayout;
	
	private Activity mActivity;
	
	private String mAttributeName;
	
	private String mAttributeValue = "";
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Logger.info(this.getClass(), "activity created");
		mAttributesTableLayout = (TableLayout)findViewById(R.id.tlAttribbutes);
		mBtnDone = (Button)findViewById(R.id.btnDone);
		mHelperText = (TextView)findViewById(R.id.tvGenericAttrHelperText);
		mActivity = this;
		mParcel = this.getIntent().getParcelableExtra("uiparcel");
		createUIElementsFromParcel();
		mBtnDone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				for(int i=0; i<mAttributeValuesList.size(); i++) {
					
						mAttributeValue += mAttributeValuesList.get(i).getText().toString()+";";
					
				}
				try {
					//GenericAttribute attribute = AttributeFactory.getGenericAttributeFactory().createAttribute(mAttributeName, mAttributeValue);
					GenericAttribute attribute = GenericAttributeFactory.createAttribute(mAttributeName);
					attribute.setValue(mAttributeValue);
					attribute.save();
				} catch (IllegalArgumentException e) {
					
				} catch (InvalidAttributeValueException e) {
					
				} catch (MIDaaSException e) {
					UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", e.getError().getErrorMessage());
				} catch (InvalidAttributeNameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(new Intent(mActivity, AttributeListActivity.class));
				mActivity.finish();
			}
		});
	}
	
	/**
	 * Dynamically create the number of rows depending on what
	 * was set in the initial map. 
	 */
	private void createUIElementsFromParcel() {
		mHelperText.setText(mParcel.getHelperText());
		Map<String, Integer> map = mParcel.getLabelToInputTypeMap();
		mAttributeValuesList.clear();
		mAttributeValue = mParcel.getAttributeValue();
		mAttributeName = mParcel.getAttributeName();
		
		for(Map.Entry<String, Integer> entry:map.entrySet()) {
			TableRow row = new TableRow(mActivity);
			TextView label = new TextView(mActivity);
			EditText attributeValue = new EditText(mActivity);
			label.setText(entry.getKey() + ": ");
			attributeValue.setInputType(entry.getValue());
			label.setTextColor(Color.BLACK);
			attributeValue.setEms(10);
			mAttributeValuesList.add(attributeValue);
			row.addView(label);
			row.addView(attributeValue);
			mAttributesTableLayout.addView(row, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		if(!(mAttributeValue.isEmpty())) {
			String[] array = mAttributeValue.split("\\;");
			for(int i=0; i<array.length; i++) {
				mAttributeValuesList.get(i).setText(array[i]);
			}
		}
	}

	@Override
	protected String getTitlebarText() {
		return "General";
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.generic_attribute);
	}

}
