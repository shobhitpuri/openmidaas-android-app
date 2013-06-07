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

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmidaas.app.R;

public class AboutUsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		//Set the version number
        try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			TextView tvVersionName = (TextView)findViewById(R.id.tvVersion);
			tvVersionName.setText("Version: "+versionName);
		} catch (NameNotFoundException e) {
			
		}
        WebView view = new WebView(this);
        //view.setVerticalScrollBarEnabled(false);

        LinearLayout layoutAboutUs = ((LinearLayout)findViewById(R.id.llAboutUs));
        layoutAboutUs.addView(view);

        view.loadData(getString(R.string.about_us_text), "text/html", "utf-8");
	}
}
