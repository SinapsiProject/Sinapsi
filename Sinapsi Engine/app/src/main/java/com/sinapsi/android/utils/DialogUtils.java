package com.sinapsi.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;

import com.sinapsi.android.R;

import retrofit.RetrofitError;

/**
 * Created by Giuseppe on 30/04/15.
 */
public class DialogUtils {

    /**
     * Shows a simple text message dialog.
     *
     * @param context the context
     * @param title   the title
     * @param message the message
     * @param system  if the dialog window type must be set to TYPE_SYSTEM_ALERT
     */
    public static void showOkDialog(Context context,
                                    String title,
                                    String message,
                                    boolean system) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (system) alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }


    /**
     * Shows a simple text message dialog with yes/no buttons
     *
     * @param context the context
     * @param title   the title
     * @param message the message
     * @param system  if the dialog window type must be set to TYPE_SYSTEM_ALERT
     * @param onYes   what to do on yes
     * @param onNo    what to do on no
     */
    public static void showYesNoDialog(Context context,
                                       String title,
                                       String message,
                                       boolean system,
                                       DialogInterface.OnClickListener onYes,
                                       DialogInterface.OnClickListener onNo) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes), onYes);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no), onNo);
        if (system) alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    /**
     * Handles a RetrofitError by showing it to the user with a dialog
     *
     * @param t      the error (must extend RetrofitError)
     * @param system if the dialog window type must be set to TYPE_SYSTEM_ALERT
     */
    public static void handleRetrofitError(Throwable t, Context context, boolean system) {
        RetrofitError error = (RetrofitError) t;
        error.printStackTrace();
        String errstring = "An error occurred while communicating with the server.\n";

        String errtitle = "Error: " + error.getKind().toString();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        switch (error.getKind()) {
            case NETWORK:
                errstring += "Network error";
                break;
            case CONVERSION:
                errstring += "Conversion error";

                break;
            case HTTP:
                if(error.getResponse().getStatus() == 404){
                    errstring += "404: Unreachable server";
                }else errstring += "HTTP Error " + error.getResponse().getStatus();
                break;
            case UNEXPECTED:
                errstring += "An unexpected error occurred";
                break;
        }

        if (ni == null || !ni.isConnected())
            errstring += "\nMissing internet connection.";
        showOkDialog(
                context,
                errtitle,
                errstring,
                system);
    }
}
