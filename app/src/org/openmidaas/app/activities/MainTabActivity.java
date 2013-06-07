package org.openmidaas.app.activities;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.activities.ui.fragments.EnterURLDialogFragment;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.Logger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MainTabActivity extends FragmentActivity {
	
	/* Tab identifiers */
	static String TAB_A = "Profile";
	static String TAB_B = "Scan";
	static String TAB_C = "Enter URL";
	String currentTab;
	
	TabHost mTabHost;
	
	AttributeListActivity fragment1;
	EnterURLDialogFragment fragment3;
		
	Intent intentScan;
	private final int SCAN_REQUEST = 65534;
	private Activity mActivity;
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main_tab);
		
		//Enabling action bar
		ActionBar actionBar = getActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST|ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.show();
        getOverflowMenu();
        
        mActivity = this;
        mProgressDialog = new ProgressDialog(mActivity);
        
		fragment1 = new AttributeListActivity();
		fragment3 = new EnterURLDialogFragment();
		
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();
        
        //initializing tabs
        initializeTab();
        checkForUpdates();
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
		        
			case R.id.help:
				intent = new Intent(this, HelpActivity.class);
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
	public void initializeTab() {
		
        TabHost.TabSpec spec    =   mTabHost.newTabSpec(TAB_A);
        mTabHost.setCurrentTab(0);
        currentTab = TAB_A;
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(TAB_A, R.drawable.profiletab));
        mTabHost.addTab(spec);


        spec =   mTabHost.newTabSpec(TAB_B);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(TAB_B, R.drawable.qrcodetab));
        mTabHost.addTab(spec);
        
        spec =   mTabHost.newTabSpec(TAB_C);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(TAB_C, R.drawable.linktab));
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
			/*Set current tab..*/
	        if(tabId.equals(TAB_A)){
	        	currentTab = TAB_A; 
	        	pushFragments(tabId, fragment1);
	        
	        }else if(tabId.equals(TAB_B)){
	        	showQRCodeScanner();
	        
	        }else if(tabId.equals(TAB_C)){
	        	currentTab = TAB_C; 
	        	pushFragments(tabId, fragment3);
		      }
	      }
	    };
	    
	/*
	 * adds the fragment to the FrameLayout
	 */
	public void pushFragments(String tag, Fragment fragment){
	    
	    FragmentManager manager = getSupportFragmentManager();
	    FragmentTransaction ft = manager.beginTransaction();
	    ft.replace(android.R.id.tabcontent, fragment);
	    ft.commit();
	}


	private void showQRCodeScanner() {
		// check to see if a rear-camera is available
		if(mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Intent intentScan = new Intent(Intents.QR_CODE_INIT_INTENT);
			intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intentScan.putExtra("SAVE_HISTORY", false);
		    try {
		      startActivityForResult(intentScan, SCAN_REQUEST);
		      mTabHost.setCurrentTabByTag(currentTab);
		    } catch (ActivityNotFoundException error) {
		    	showNoQRCodeScannersPresentDialog();
		    }
		} else {
			showNoBackCameraPresentDialog();
		}
	}
	
	private void showNoQRCodeScannersPresentDialog() {
		AlertDialog.Builder dlBuilder = new AlertDialog.Builder(mActivity);
        dlBuilder.setTitle(R.string.no_qrcode_dialog_title);
        dlBuilder.setMessage(R.string.no_qrcode_dialog_message);
        dlBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        dlBuilder.setPositiveButton(R.string.install_button,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                           Uri.parse(Constants.ZXING_MARKET));
                try {
                  startActivity(intent);
                }
                catch (ActivityNotFoundException e) { 
                  intent = new Intent(Intent.ACTION_VIEW,
                                      Uri.parse(Constants.ZXING_DIRECT));
                  startActivity(intent);
                }
              }
            }
        );
        dlBuilder.setNegativeButton(R.string.cancel, null);
        dlBuilder.show();
	}
	
	private void showNoBackCameraPresentDialog() {
		DialogUtils.showNeutralButtonDialog(mActivity, "Problem", getString(R.string.rear_camera_not_present_text));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  if (requestCode == SCAN_REQUEST) {
		  if (resultCode == Activity.RESULT_OK) {
			  if(intent.getStringExtra("SCAN_RESULT") != null) {
				  Logger.debug(getClass(), intent.getStringExtra("SCAN_RESULT"));
				//  EnterURLDialogFragment fragment = getFragmentManager().findFragmentById(android.R.id.tabcontent);
				  processUrl(intent.getStringExtra("SCAN_RESULT"));
			  } else {
				  DialogUtils.showNeutralButtonDialog(mActivity, "Error", "Error in scan");
			  }
		  } else if (resultCode == Activity.RESULT_CANCELED) {
			  Logger.debug(getClass(), "Scan cancelled");
		  }
	   }
	}
	
	public void processUrl(String result) {
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
}