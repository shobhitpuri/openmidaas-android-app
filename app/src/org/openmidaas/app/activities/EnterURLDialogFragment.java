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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class EnterURLDialogFragment extends Fragment {
	private Button btnPositive;
	private Button btnClear;
	private Activity mActivity;
	private EditText edUrl;
	InputMethodManager imgr;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        mActivity = getActivity();
        
        imgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        edUrl = (EditText)view.findViewById(R.id.edDialogFragment);
        edUrl.requestFocus();
        
        btnPositive = (Button)view.findViewById(R.id.btnOkayDialogFragment);
        btnPositive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Get the URL
				View tv = getActivity().findViewById(R.id.edDialogFragment);
				String merchantUrl = ((EditText)tv).getText().toString();
				((MainTabActivity)getActivity()).processUrl(merchantUrl);
			}
		});
        
        btnClear = (Button)view.findViewById(R.id.btnClearDialogFragment);
        btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Clear the text
				View ed = getActivity().findViewById(R.id.edDialogFragment);
		        ((EditText)ed).setText("");
			}
		});
        return view;
        
        
    }
}
