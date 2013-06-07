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
