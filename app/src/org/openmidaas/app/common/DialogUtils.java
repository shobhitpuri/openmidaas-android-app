package org.openmidaas.app.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {
	
	public static void showErrorDialog(Context context, String message) {
		new AlertDialog.Builder(context)
	    .setTitle("Error")
	    .setMessage(message)
	    .setNeutralButton("OK",  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
	    })
	     .show();
	}

}
