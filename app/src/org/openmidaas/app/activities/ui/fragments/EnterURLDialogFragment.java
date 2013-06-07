package org.openmidaas.app.activities.ui.fragments;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.openmidaas.app.R;
import org.openmidaas.app.activities.AuthorizationActivity;
import org.openmidaas.app.common.DialogUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class EnterURLDialogFragment extends DialogFragment {
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
				processUrl(merchantUrl);
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

	private void processUrl(String result) {
		try {
			URI uri = new URI(result);
			if(uri.isAbsolute()) {
				if(uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
					AsyncHttpClient client = new AsyncHttpClient();
					mProgressDialog.setMessage("Loading...");
					mProgressDialog.show();
					client.get(uri.toString(), new AsyncHttpResponseHandler() {
						@Override
					    public void onSuccess(String response) {
							if(mProgressDialog.isShowing()) {
								mProgressDialog.dismiss();
							}
							Intent intent = new Intent(mActivity, AuthorizationActivity.class);
							intent.putExtra(AuthorizationActivity.REQUEST_BUNDLE_KEY, response);
							startActivity(intent);
					    }
						
						@Override
					    public void onFailure(Throwable e, String response) {
							if(mProgressDialog.isShowing()) {
								mProgressDialog.dismiss();
							}
					        DialogUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
					    }
					});
				} else {
					DialogUtils.showNeutralButtonDialog(mActivity, "Non http/https URI type", result);
				}
			} else {
				DialogUtils.showNeutralButtonDialog(mActivity, "Invalid URI", "Either relative URI or unknown format: " + result);
			}
		} catch (URISyntaxException e) {
			DialogUtils.showNeutralButtonDialog(mActivity, "Invalid URI", "Invalid URI: " + result);
		}
	}
}
