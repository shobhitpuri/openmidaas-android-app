package org.openmidaas.app.activities;


import org.openmidaas.app.R;
import org.openmidaas.app.common.DialogUtils;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public abstract class AbstractAttributeRegistrationActivity extends AbstractActivity {
	
	protected EditText mAttributeValue;
	
	protected EditText mAttributeVerificationCode;
	
	protected Button mBtnStartAttributeVerification;
	
	protected Button mBtnCompleteAttributeVerification;
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAttributeValue = (EditText)findViewById(getUIElementForAttributeValue());
		mAttributeVerificationCode = (EditText)findViewById(getUIElementForAttributeVerification());
		mBtnStartAttributeVerification = (Button)findViewById(getUIElementForStartAttributeVerification());
		mBtnCompleteAttributeVerification = (Button)findViewById(R.id.btnCompleteEmailVerification);
		mBtnCompleteAttributeVerification.setEnabled(false);
	} 
	
	/***
	 * Returns the UI element that is responsible for 
	 * collecting the attribute value.
	 * Override this method to return the EditText resource ID. 
	 * @return - The resource ID for the UI element. 
	 */
	protected abstract int getUIElementForAttributeValue();
	
	/**
	 * Returns the UI element that is responsible for
	 * collecting the verification code for the attribute value.
	 * Override this method to return the EditText resource ID.
	 * @return - The resource ID for the UI element.
	 */
	protected abstract int getUIElementForAttributeVerification();
	
	/**
	 * Returns the UI element that is responsible for starting
	 * the attribute verification.
	 * Override this method to return the Button resource ID. 
	 * @return - The resource ID for the UI element.
	 */
	protected abstract int getUIElementForStartAttributeVerification();
	
	/**
	 * Starts the attribute verification process by getting the value
	 * from the EditText UI element and sending it to the server via the
	 * MIDaaS library.
	 * @param object -  the attribute to verify
	 */
	protected void startAttributeVerification(Object object) {
		if(!(isAttributeValid())) {
			DialogUtils.showErrorDialog(this, "Attribute value is incorrect.");
		} else {
			mBtnStartAttributeVerification.setText(getString(R.string.resendVerificationText));
			mBtnCompleteAttributeVerification.setEnabled(true);
			//TODO: Get the attribute value and send it to the server using the library.
		}
	}
	
	/**
	 * Validates the attribute. E.g., whether an email address matches 
	 * a valid email address format. 
	 * Override this method to validate the attribute value collected via
	 * the EditText UI element. 
	 * @return
	 */
	protected abstract boolean isAttributeValid();
	
	/**
	 * Completes the attribute verification process.
	 * Override this method to implement the process. 
	 */
	protected void completAttributeVerification(Object object) {
		throw new UnsupportedOperationException();
	}
}
