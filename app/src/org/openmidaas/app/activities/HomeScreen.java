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
import org.openmidaas.app.R;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.Intents;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.common.UINotificationUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * Home screen for the application. 
 *
 */
public class HomeScreen extends AbstractActivity {
	
	private final int SCAN_REQUEST = 102938;
	
	private Activity mActivity;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		mActivity = this;
		findViewById(R.id.btnScanCode).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 showQRCodeScanner();
			}
		});
		
		findViewById(R.id.btnManageInfo).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showManageInfoScreen();
			}
		});
	}

	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  if (requestCode == SCAN_REQUEST) {
		  if (resultCode == Activity.RESULT_OK) {
			  if(intent.getStringExtra("SCAN_RESULT") != null) {
				  Logger.debug(getClass(), intent.getStringExtra("SCAN_RESULT"));
				  processScanResult(intent.getStringExtra("SCAN_RESULT"));
			  } else {
				  UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", "Error in scan");
			  }
		  } else if (resultCode == RESULT_CANCELED) {
			  Logger.debug(getClass(), "Scan cancelled");
		  }
	  	}
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
	
	private void processScanResult(String result) {
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
							UINotificationUtils.showNeutralButtonDialog(mActivity, "Debug", response);
					    }
						
						@Override
					    public void onFailure(Throwable e, String response) {
							if(mProgressDialog.isShowing()) {
								mProgressDialog.dismiss();
							}
					        UINotificationUtils.showNeutralButtonDialog(mActivity, "Error", e.getMessage());
					    }
					});
				} else {
					UINotificationUtils.showNeutralButtonDialog(mActivity, "Non http/https URI type", result);
				}
			} else {
				UINotificationUtils.showNeutralButtonDialog(mActivity, "Invalid URI", "Either relative URI or unknown format: " + result);
			}
		} catch (URISyntaxException e) {
			UINotificationUtils.showNeutralButtonDialog(mActivity, "Invalid URI", "Invalid URI: " + result);
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
		UINotificationUtils.showNeutralButtonDialog(mActivity, "Problem", getString(R.string.rear_camera_not_present_text));
	}
	 
	@Override
	protected String getTitlebarText() {
		return ("Home");
	}

	@Override
	protected int getLayoutResourceId() {
		return (R.layout.home_screen);
	}

}
