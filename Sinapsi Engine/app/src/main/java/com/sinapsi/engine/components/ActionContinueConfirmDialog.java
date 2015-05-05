package com.sinapsi.engine.components;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.DialogAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.Action;
import com.sinapsi.model.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionContinueConfirmDialog class. This Action will show a
 * dialog to ask the user to confirm the execution of the
 * rest of the macro.
 * This is a good example of how the ExecutionInterface class can
 * be used by a component. When the action is activated, the dialog
 * is showed and the execution of the macro is paused. When the user
 * chooses "Yes" on the dialog, the execution of the macro is continues.
 * If the user chooses "No" then the execution of the macro is cancelled.
 * This action must be parametrized in order to know at execution phase
 * the text message in the dialog.
 * Notice that this action is completely platform-independent:
 * it relies on ExecutionInterface and DialogAdapter.
 */
public class ActionContinueConfirmDialog extends Action {

    public static final String ACTION_CONTINUE_CONFIRM_DIALOG = "ACTION_CONTINUE_CONFIRM_DIALOG";


    @Override
    public void activate(final ExecutionInterface di) {
        DialogAdapter da = (DialogAdapter) di.getSystemFacade().getSystemService(SystemFacade.SERVICE_DIALOGS);
        JSONObject pjo = getParamsObj(params);
        String message = "";
        try{
            message = pjo.getString("dialog_message");
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }

        da.showSimpleConfirmDialog(message, new DialogAdapter.OnDialogChoiceListener() {
            @Override
            public void onDialogChoice() {
                di.unpause();
            }
        }, new DialogAdapter.OnDialogChoiceListener() {
            @Override
            public void onDialogChoice() {
                di.cancel();
            }
        });

        di.pause();
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("dialog_message", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public String getName() {
        return ACTION_CONTINUE_CONFIRM_DIALOG;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_SIMPLE_DIALOGS, 1)
                .create();
    }
}
