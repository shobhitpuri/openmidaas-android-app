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

import org.openmidaas.app.R;
import org.openmidaas.app.common.CategoryManager;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.library.model.AddressAttribute;
import org.openmidaas.library.model.AddressAttributeFactory;
import org.openmidaas.library.model.AddressValue;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.MIDaaSException;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddressActivity extends AbstractActivity{
	
	private EditText mStreetAddress;
	private EditText mCity;
	private EditText mZip;
	private Spinner mState;
	private Spinner mCountry;
	private Spinner mAddressLabel;
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Button mBtnSaveAndQuit = (Button)findViewById(R.id.btnSaveAndQuit);
		mStreetAddress = (EditText)findViewById(R.id.edStreet);
		mCity = (EditText)findViewById(R.id.edCity);
		mZip = (EditText)findViewById(R.id.edZip);
		mState = (Spinner)findViewById(R.id.statePicker);
		mCountry = (Spinner)findViewById(R.id.countryPicker);
		mAddressLabel = (Spinner)findViewById(R.id.addressLabelPicker);
		mStreetAddress.setInputType(InputType.TYPE_CLASS_TEXT);
		mCity.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		mZip.setInputType(InputType.TYPE_CLASS_TEXT);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);		
		mBtnSaveAndQuit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					saveAndQuit();
				} catch (IllegalArgumentException e) {
					DialogUtils.showNeutralButtonDialog(AddressActivity.this, "Error", e.getMessage());
				} catch (InvalidAttributeValueException e) {
					DialogUtils.showNeutralButtonDialog(AddressActivity.this, "Error", e.getMessage());
				} catch (MIDaaSException e) {
					DialogUtils.showNeutralButtonDialog(AddressActivity.this, "Error", e.getError().getErrorMessage());
				}
			}
		});
	}

	@Override
	protected String getTitlebarText() {
		return "Add an address";
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.address_collection);
	}
	
	private void saveAndQuit() throws IllegalArgumentException, InvalidAttributeValueException, MIDaaSException {
		AddressAttribute address = AddressAttributeFactory.createAttribute();
		
		
		if(mStreetAddress.getText().toString() == null || mStreetAddress.getText().toString().isEmpty()) {
			throw new IllegalArgumentException ("Street address cannot be empty");
		}
		if(mCity.getText().toString() == null || mCity.getText().toString().isEmpty()) {
			throw new IllegalArgumentException ("City/Town cannot be empty");
		}
		if(mZip.getText().toString() == null || mZip.getText().toString().isEmpty()) {
			throw new IllegalArgumentException ("Zip/Postal Code cannot be empty");
		}
		if(mState.getSelectedItem().toString() == null || mState.getSelectedItem().toString().isEmpty()) {
			throw new IllegalArgumentException ("Invalid state/province selected");
		}
		if(mCountry.getSelectedItem().toString() == null || mCountry.getSelectedItem().toString().isEmpty()) {
			throw new IllegalArgumentException ("Invalid country selected");
		}
		AddressValue value = new AddressValue(mStreetAddress.getText().toString(), mCity.getText().toString(), 
				mState.getSelectedItem().toString(), mZip.getText().toString(), mCountry.getSelectedItem().toString());
		address.setLabel(mAddressLabel.getSelectedItem().toString());
		address.setValue(value);
		address.save();
		AddressActivity.this.finish();
	}
}
