package com.sinapsi.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sinapsi.engine.R;

/**
 * Created by Giuseppe on 30/04/15.
 */
public class DialogUtils {

    /**
     * Shows a simple text message dialog.
     *
     * @param context the context
     * @param title the title
     * @param message the message
     */
    public static void showOkDialog(Context context,
                                    String title,
                                    String message){
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


    /**
     * Shows a simple text message dialog with yes/no buttons
     * @param context the context
     * @param title the title
     * @param message the message
     * @param onYes what to do on yes
     * @param onNo what to do on no
     */
    public static void showYesNoDialog(Context context,
                                       String title,
                                       String message,
                                       DialogInterface.OnClickListener onYes,
                                       DialogInterface.OnClickListener onNo) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes), onYes);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no), onNo);
        alertDialog.show();
    }
}
