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

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.session.SessionManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainTabActivity extends FragmentActivity {
	
	/* Tab identifiers */
	private static String tabProfile;
	private static String tabScan;
	private static String tabInputURL;
	String currentTab;
	
	TabHost mTabHost;
	
	AttributeListFragment mAttributeListFragment;
	EnterURLDialogFragment mUrlInputFragment;
	ScanFragment mScanFragment;
		
	Intent intentScan;
	private Activity mActivity;
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main_tab);
		
		//Get the text
		tabProfile = getResources().getString(R.string.profileTabtext);
		tabScan = getResources().getString(R.string.scanTabtext);
		tabInputURL = getResources().getString(R.string.urlTabtext);
		
		//Enabling action bar
		ActionBar actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST|ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.show();
        getOverflowMenu();
        
        mActivity = this;
        mProgressDialog = new ProgressDialog(mActivity);
        
		mAttributeListFragment = new AttributeListFragment();
		mUrlInputFragment = new EnterURLDialogFragment();
		mScanFragment = new ScanFragment();
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        //initializing tabs
        initializeTab();
        //Check for updates
        checkForUpdates();
        //See if its being called to process URL from push message. 
        //MainAcivity not being killed to be consistent with what happens when the URL taken via QR code etc.
        if((getIntent().getExtras()!=null) && !getIntent().getExtras().isEmpty()){
        	if(getIntent().getAction().equals(SplashActivity.ACTION_MSG_CUSTOM)){
        		processUrl(getIntent().getExtras().getString("url"));
        	}
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_tab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
		
			case android.R.id.home:
				break;
				
			case R.id.revoke_concent:
				intent = new Intent(this, ManageConsentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        this.startActivity(intent);
		        break;
		        
			case R.id.register_push:
				intent = new Intent(this, PushNotificationActivity.class);
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        startActivity(intent);
				break;
				
			case R.id.about_us:
				intent = new Intent(this, AboutUsActivity.class);
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        startActivity(intent);
				break;
		}
		return true;
	}
	
	/*
	 * To show the overflow button on the Action Bar
	 */
	private void getOverflowMenu() {

	     try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/*
	 * Initialize the tabs and set views and identifiers for the tabs
	 */
	private void initializeTab() {
		
        TabHost.TabSpec spec    =   mTabHost.newTabSpec(tabProfile);
        mTabHost.setCurrentTab(0);
        currentTab = tabProfile;
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(tabProfile, R.drawable.profiletab_i));
        mTabHost.addTab(spec);

        spec =   mTabHost.newTabSpec(tabScan);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(tabScan, R.drawable.qrcodetab));
        mTabHost.addTab(spec);
        
        spec =   mTabHost.newTabSpec(tabInputURL);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(tabInputURL, R.drawable.linktab_i));
        mTabHost.addTab(spec);
	}
	
	/*
	 * returns the tab view i.e. the tab icon and text
	 */
	private View createTabView(final String text, final int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageDrawable(getResources().getDrawable(id));
        ((TextView) view.findViewById(R.id.tab_text)).setText(text);
        return view;
    }
	
	/*
	 * TabChangeListener for changing the tab when one of the tabs is pressed
	 */
	TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {

		public void onTabChanged(String tabId) {
			//Hide he keyboard while changing tabs
	        InputMethodManager imgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imgr.hideSoftInputFromWindow(mTabHost.getApplicationWindowToken(), 0);
	        
			/*Set current tab..*/
	        if(tabId.equals(tabProfile)){
	        	currentTab = tabProfile; 
	        	pushFragments(tabId, mAttributeListFragment);
	        }else if(tabId.equals(tabScan)){
	        	pushFragments(tabId, mScanFragment);
	        }else if(tabId.equals(tabInputURL)){
	        	currentTab = tabInputURL; 
	        	pushFragments(tabId, mUrlInputFragment);
		      }
	      }
	    };
	    
	/*
	 * adds the fragment to the FrameLayout
	 */
	private void pushFragments(String tag, Fragment fragment){
	    
	    FragmentManager manager = getSupportFragmentManager();
	    FragmentTransaction ft = manager.beginTransaction();
	    ft.replace(android.R.id.tabcontent, fragment);
	    ft.commit();
	}

	void processUrl(String result) {
		try {
			URI uri = new URI(result);
			if(uri.isAbsolute()) {
				if(uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
					//Check for the lock before starting to process
					if (SessionManager.getBusyness() == false){
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
					}
				} else {
					DialogUtils.showNeutralButtonDialog(mActivity, getResources().getString(R.string.invalidURIType), result);
				}
			} else {
				DialogUtils.showNeutralButtonDialog(mActivity, getResources().getString(R.string.invalidURI), getResources().getString(R.string.unknownURIFormat) + " " + result);
			}
		} catch (URISyntaxException e) {
			DialogUtils.showNeutralButtonDialog(mActivity, getResources().getString(R.string.invalidURI),  getResources().getString(R.string.invalidURI) + " " + result);
		}
	}
	
	private void checkForCrashes() {
		if(Settings.IS_HOCKEY_APP_ENABLED) {
			CrashManager.register(this, Settings.HOCKEY_APP_ID);
		}
	}
	
	private void checkForUpdates() {  
		if(Settings.IS_HOCKEY_APP_ENABLED) {
			UpdateManager.register(this, Settings.HOCKEY_APP_ID);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkForCrashes();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//Hide he keyboard while changing tabs
        InputMethodManager imgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.hideSoftInputFromWindow(mTabHost.getApplicationWindowToken(), 0);
	}
}
