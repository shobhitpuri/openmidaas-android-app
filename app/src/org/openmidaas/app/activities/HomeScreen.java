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

import java.net.URI;
import java.net.URISyntaxException;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.openmidaas.app.R;
import org.openmidaas.app.Settings;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 
 * Home screen for the application. 
 *
 */
public class HomeScreen extends AbstractActivity {
	
	protected static final String ANIMATE_DONE = "org.openmidaas.app.activities.animate_done";

	private final int SCAN_REQUEST = 102938;
	
	private final int SLIDE_UP_NOTIFICATION = 1;
	
	private Activity mActivity;
	
	private RelativeLayout rlDone;
	
	private final int SLIDE_ANIMATION_DURATION = 1500;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		mActivity = this;
		rlDone = (RelativeLayout)findViewById(R.id.rlInfoSlider);
		if(getIntent().getBooleanExtra(ANIMATE_DONE, false) == true) {
			animate();
		} else {
			rlDone.setVisibility(View.GONE);
		}
		findViewById(R.id.btnScanCode).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showQRCodeScanner();
				String text = "{\"client_id\": \"https://edbc.ca\",\"acr\": \"1\",\"attrs\": {"+ "\"given_name\" : { \"essential\": true },"+
						"\"home_address\": {\"type\": \"address\", \"essential\": true, \"label\": \"Home address\", \"verified\": true}," +
								"\"credit_card\":{\"label\": \"Credit Card\"}," + 
								"\"email\": {\"essential\": true,\"label\": \"Work email\",\"verified\": true}},\"state\": \"1234\","+ 
								"\"return\": {\"method\": \"postback\",\"url\": \"https://edbc.ca/sess/fhyxy8209jskso\"}}\"";
						//processScanResult(text);
						Intent intent = new Intent(mActivity, AuthorizationActivity.class);
						intent.putExtra(AuthorizationActivity.REQUEST_BUNDLE_KEY, text);
						startActivity(intent);
			}
		});
		
		findViewById(R.id.btnManageInfo).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showManageInfoScreen();
			}
		});
		
		findViewById(R.id.btnEnterUrl).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showUrlCollectionDialog();
			}
		});
		
		checkForUpdates();
	}

	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  if (requestCode == SCAN_REQUEST) {
		  if (resultCode == Activity.RESULT_OK) {
			  if(intent.getStringExtra("SCAN_RESULT") != null) {
				  Logger.debug(getClass(), intent.getStringExtra("SCAN_RESULT"));
				  processUrl(intent.getStringExtra("SCAN_RESULT"));
			  } else {
				  DialogUtils.showNeutralButtonDialog(mActivity, "Error", "Error in scan");
			  }
		  } else if (resultCode == RESULT_CANCELED) {
			  Logger.debug(getClass(), "Scan cancelled");
		  }
	   }
	}
	
	private void showUrlCollectionDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);

		alert.setMessage(getString(R.string.enterUrlMessageBoxText));

		// Set an EditText view to get user input 
		final EditText input = new EditText(HomeScreen.this);
		input.setText("https://");
		input.setSelection(input.getText().toString().length());
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Editable value = input.getText();
			processUrl(value.toString());
		  }
		});
		final AlertDialog alertDialog = alert.create();
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
			}
			
		});
		alertDialog.show();
	}
	
	private void animate() {
		slideDown(rlDone);
	    new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized(this) {
					try {
						wait(3000);
					} catch (InterruptedException e) {
						Logger.error(getClass(), e.getMessage());
						this.notify();
					}
				}
				Message message = new Message();
				message.what = SLIDE_UP_NOTIFICATION;
				mHandler.sendMessage(message);
			}
	    	
	    }).start();
	}
	
	private void showManageInfoScreen() {
		startActivity(new Intent(mActivity, AttributeListActivity.class));
		mActivity.finish();
	}
	
	private void showQRCodeScanner() {
		// check to see if a rear-camera is available
		if(mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Intent intentScan = new Intent(Intents.QR_CODE_INIT_INTENT);
			intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intentScan.putExtra("SAVE_HISTORY", false);
		    try {
		      startActivityForResult(intentScan, SCAN_REQUEST);
		    } catch (ActivityNotFoundException error) {
		    	showNoQRCodeScannersPresentDialog();
		    }
		} else {
			showNoBackCameraPresentDialog();
		}
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
							mActivity.finish();
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
	
	private void slideDown(View view) {
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(SLIDE_ANIMATION_DURATION);
		animation.setFillAfter(true);
		view.clearAnimation();
		view.setAnimation(animation);
	}
	
	private void slideUp(View view) {
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f);
		animation.setDuration(SLIDE_ANIMATION_DURATION);
		animation.setFillAfter(true);
		view.clearAnimation();
		view.setAnimation(animation);
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case SLIDE_UP_NOTIFICATION:
					slideUp(rlDone);
				break;
				default:
				break;
			}
			return true;
		}
		
	});
	
	@Override
	protected String getTitlebarText() {
		return ("Home");
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.home_screen);
	}

	@Override
	public void onResume() {
		super.onResume();
		checkForCrashes();
	}
}
