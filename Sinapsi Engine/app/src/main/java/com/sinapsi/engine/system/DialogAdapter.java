package com.sinapsi.engine.system;

/**
 * Interface used to adapt various system-dependent calls
 * to show various types of dialogs
 */
public interface DialogAdapter {

    public static final String SERVICE_DIALOGS = "SERVICE_DIALOGS";
    public static final String REQUIREMENT_SIMPLE_DIALOGS = "REQUIREMENT_SIMPLE_DIALOGS";
    public static final String REQUIREMENT_INPUT_DIALOGS = "REQUIREMENT_INPUT_DIALOGS";

    /**
     * Simple listener interface, to be implemented, in
     * order to define what to do when the user makes a
     * choice in a simple (yes/no/cancel/ok/other) dialog.
     */
    public interface OnDialogChoiceListener {
        public void onDialogChoice();
    }

    public interface OnInputDialogChoiceListener {
        public void onDialogChoice(String inputvalue);
    }

    /**
     * Shows a simple dialog to the user with a text message,
     * and two choices: Yes and No.
     *
     * @param message the message
     * @param onYes   listener for what to do when the user
     *                chooses yes
     * @param onNo    listener for what to do when the user
     *                chooses no
     */
    public void showSimpleConfirmDialog(String title,
                                        String message,
                                        final OnDialogChoiceListener onYes,
                                        final OnDialogChoiceListener onNo);


    /**
     * Shows a simple input dialog to the user with a text message,
     * a title, a input text field and two choices: Yes and No.
     *
     * @param message  the message
     * @param onDo     listener for what to do when the user
     *                 chooses yes
     * @param onCancel listener for what to do when the user
     *                 chooses no
     */
    public void showStringInputDialog(String title,
                                      String message,
                                      final OnInputDialogChoiceListener onDo,
                                      final OnInputDialogChoiceListener onCancel);

}
