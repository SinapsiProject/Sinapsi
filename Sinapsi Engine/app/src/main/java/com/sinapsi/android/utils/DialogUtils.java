package com.sinapsi.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sinapsi.engine.R;

/**
 * Created by Giuseppe on 30/04/15.
 */
public class DialogUtils {
    public static void showOkDialog(Context context, String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
