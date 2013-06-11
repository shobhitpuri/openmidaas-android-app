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
import org.openmidaas.app.common.Utils;

import android.app.ActionBar;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutUsActivity extends AbstractActivity {
	TextView tvVersionName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		tvVersionName = (TextView)findViewById(R.id.tvVersion);
        //Showing back button on Action Bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        
		//Get the version number
		String versionNumber = Utils.getVersionNumber(getApplicationContext());
		tvVersionName.setText("Version: "+versionNumber);
        
		WebView view = new WebView(this);

        LinearLayout layoutAboutUs = ((LinearLayout)findViewById(R.id.llAboutUs));
        layoutAboutUs.addView(view);
        view.loadData(getString(R.string.aboutUsText), "text/html", "utf-8");
	}

	@Override
	protected String getTitlebarText() {
		return (getResources().getString(R.string.aboutUs));
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.about_us);
	}
}
