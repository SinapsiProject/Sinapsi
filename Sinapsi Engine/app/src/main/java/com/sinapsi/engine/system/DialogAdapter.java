package com.sinapsi.engine.system;

/**
 * Interface used to adapt various system-dependent calls
 * to show various types of dialogs
 */
public interface DialogAdapter {

    /**
     * Simple listener interface, to be implemented, in
     * order to define what to do when the user makes a
     * choice in a simple (yes/no/cancel/ok/other) dialog.
     */
    public interface OnDialogChoiceListener{
        public void onDialogChoice();
    }

    /**
     * Shows a simple dialog to the user with a text message,
     * and two choices: Yes and No.
     * @param message the message
     * @param onYes listener for what to do when the user
     *              chooses yes
     * @param onNo listener for what to do when the user
     *              chooses no
     */
    public void showSimpleConfirmDialog(String message,
                                        OnDialogChoiceListener onYes,
                                        OnDialogChoiceListener onNo);

    //TODO: add more types of dialogs ( be creative!!! ;) )
}
