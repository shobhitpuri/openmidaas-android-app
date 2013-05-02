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
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.library.model.CreditCardAttribute;
import org.openmidaas.library.model.CreditCardAttributeFactory;
import org.openmidaas.library.model.CreditCardValue;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.MIDaaSException;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class CreditCardActivity extends AbstractActivity {

	private EditText mCreditCardNumber;
	private EditText mCCV;
	private EditText mCardHolderName;
	private Spinner mExpiryMonth;
	private Spinner mExpiryYear;
	
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		Button mBtnSaveAndQuit = (Button)findViewById(R.id.btnSaveAndQuit);
		mCreditCardNumber = (EditText)findViewById(R.id.edCCNumber);
		mCCV = (EditText)findViewById(R.id.edCCV);
		mCardHolderName = (EditText)findViewById(R.id.edCardHolderName);
		mExpiryMonth = (Spinner)findViewById(R.id.spnMonth);
		mExpiryYear = (Spinner)findViewById(R.id.spnYear);
		mExpiryYear.setAdapter( genExpiryYearAdapter() );
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mBtnSaveAndQuit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					saveAndQuit();
				} catch (IllegalArgumentException e) {
					DialogUtils.showNeutralButtonDialog(CreditCardActivity.this, "Error", e.getMessage());
				} catch (InvalidAttributeValueException e) {
					DialogUtils.showNeutralButtonDialog(CreditCardActivity.this, "The entered data is not valid.", e.getMessage());
				} catch (MIDaaSException e) {
					DialogUtils.showNeutralButtonDialog(CreditCardActivity.this, "Error", e.getError().getErrorMessage());
				}
			}
		});

	}
	
	private void saveAndQuit() throws IllegalArgumentException, InvalidAttributeValueException, MIDaaSException {
		CreditCardAttribute creditCard = CreditCardAttributeFactory.createAttribute();
		
		String ccNum = mCreditCardNumber.getText().toString();
		String ccv = mCCV.getText().toString();
		String ccHolderName = mCardHolderName.getText().toString();
		String expiryMonth = mExpiryMonth.getSelectedItem().toString();
		String expiryYear = mExpiryYear.getSelectedItem().toString();
		
		if( ccNum == null || ccNum.isEmpty()) {
			throw new IllegalArgumentException ("Credit card# cannot be empty");
		}
		//ccv is optional and can be empty
		if( ccHolderName == null || ccHolderName.isEmpty()) {
			throw new IllegalArgumentException ("Cardholder name cannot be empty");
		}
		if( expiryMonth == null || expiryMonth.isEmpty()) {
			throw new IllegalArgumentException ("Expiry month cannot be empty");
		}
		if( expiryYear == null || expiryYear.isEmpty()) {
			throw new IllegalArgumentException ("Expiry year cannot be empty");
		}
		CreditCardValue value = new CreditCardValue( ccNum, ccv,
				expiryMonth, expiryYear, ccHolderName);

		creditCard.setValue(value);
		
		creditCard.setLabel(creditCard.getValue().getCardType());
		
		creditCard.save();
		
		CreditCardActivity.this.finish();
	}
	
	@Override
	protected String getTitlebarText() {
		return "Add a credit card";
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.credit_card_collection);
	}

	//create a spinner adapter and populate with expiry year values
	private ArrayAdapter<String> genExpiryYearAdapter() {
		int numYears = 10;
		List<String> array = new ArrayList<String>(numYears);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i=0; i<numYears; i++) {
			array.add( String.valueOf(i + year) );
		}
		return adapter;
	}
}
