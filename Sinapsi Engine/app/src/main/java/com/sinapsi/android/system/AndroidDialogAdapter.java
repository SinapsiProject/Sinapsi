package com.sinapsi.android.system;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sinapsi.engine.R;
import com.sinapsi.engine.system.DialogAdapter;

/**
 * DialogAdapter implementation for Android.
 */
public class AndroidDialogAdapter implements DialogAdapter {

    private Context context;

    public AndroidDialogAdapter(Context c){
        this.context = c;
    }

    @Override
    public void showSimpleConfirmDialog(String title, String message, final OnDialogChoiceListener onYes, final OnDialogChoiceListener onNo) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onYes.onDialogChoice();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onNo.onDialogChoice();
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
