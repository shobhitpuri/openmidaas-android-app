package org.openmidaas.app.activities.ui.fragments;
import org.openmidaas.app.R;
import org.openmidaas.app.activities.MainTabActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class EnterURLDialogFragment extends Fragment {
	private Button btnPositive;
	private Button btnClear;
	private Activity mActivity;
	ProgressDialog mProgressDialog;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment, container, false);
        mActivity = getActivity();
        
		mProgressDialog = new ProgressDialog(mActivity);
        btnPositive = (Button)v.findViewById(R.id.btnOkayDialogFragment);
        btnPositive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Get the URL
				View tv = getActivity().findViewById(R.id.edDialogFragment);
				String merchantUrl = ((EditText)tv).getText().toString();
				((MainTabActivity)getActivity()).processUrl(merchantUrl);
			}
		});
        
        btnClear = (Button)v.findViewById(R.id.btnClearDialogFragment);
        btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Clear the text
				View ed = getActivity().findViewById(R.id.edDialogFragment);
		        ((EditText)ed).setText("");
			}
		});
        return v;
        
        
    }
}
