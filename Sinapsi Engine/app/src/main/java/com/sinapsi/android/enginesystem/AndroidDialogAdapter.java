package com.sinapsi.android.enginesystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

import com.sinapsi.android.R;
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
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    @Override
    public void showStringInputDialog(String title, String message, final OnInputDialogChoiceListener onDo, final OnInputDialogChoiceListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               onDo.onDialogChoice(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancel.onDialogChoice(input.getText().toString());
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
}
