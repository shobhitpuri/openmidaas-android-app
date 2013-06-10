package org.openmidaas.app.activities;

import org.openmidaas.app.R;
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
import android.support.v4.app.Fragment;

public class ScanFragment extends Fragment {
	private final int SCAN_REQUEST = 65534;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showQRCodeScanner();
	}
	
	private void showQRCodeScanner() {
		// check to see if a rear-camera is available
		if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
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
	
	private void showNoQRCodeScannersPresentDialog() {
		AlertDialog.Builder dlBuilder = new AlertDialog.Builder(getActivity());
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
		DialogUtils.showNeutralButtonDialog(getActivity(), "Problem", getString(R.string.rear_camera_not_present_text));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  if (requestCode == SCAN_REQUEST) {
		  if (resultCode == Activity.RESULT_OK) {
			  if(intent.getStringExtra("SCAN_RESULT") != null) {
				  Logger.debug(getClass(), intent.getStringExtra("SCAN_RESULT"));
				  ((MainTabActivity)getActivity()).processUrl(intent.getStringExtra("SCAN_RESULT"));
			  } else {
				  DialogUtils.showNeutralButtonDialog(getActivity(), "Error", "Error in scan");
			  }
		  } else if (resultCode == Activity.RESULT_CANCELED) {
			  Logger.debug(getClass(), "Scan cancelled");
		  }
		  ((MainTabActivity)getActivity()).mTabHost.setCurrentTabByTag(((MainTabActivity)getActivity()).currentTab);
	   }
	}
}
