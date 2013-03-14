package org.openmidaas.app.activities;

import org.openmidaas.app.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EmailRegistrationActivity extends AbstractAttributeRegistrationActivity {

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtnCompleteAttributeVerification = (Button)findViewById(R.id.btnCompleteEmailVerification);
		mBtnCompleteAttributeVerification.setEnabled(false);
		mBtnStartAttributeVerification.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAttributeVerification(new Object());
			}
		});
		
		mBtnCompleteAttributeVerification.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				completAttributeVerification(new Object());
			}
		});
	}

	@Override
	protected int getUIElementForAttributeValue() {
		return (R.id.etEmailAddress);
	}

	@Override
	protected int getUIElementForAttributeVerification() {
		return (R.id.etVerificationCode);
	}

	@Override
	protected boolean isAttributeValid() {
		if(mAttributeValue.getText().toString().isEmpty() || mAttributeValue.getText().toString() == null) {
			return false;
		}
		return true;
	}

	@Override
	protected int getUIElementForStartAttributeVerification() {
		return (R.id.btnStartEmailVerification);
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.attribute_view);
	}
	
	@Override
	protected void completAttributeVerification(Object object) {
		
	}
}
