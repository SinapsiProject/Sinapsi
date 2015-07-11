package com.sinapsi.engine.components;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.engine.system.DialogAdapter;
import com.sinapsi.utils.HashMapBuilder;
import com.sinapsi.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionStringInputDialog class. This Action will show a
 * dialog to allow the user to input a string on a text field,
 * which will be saved in a variable with the chosen name.
 */
public class ActionStringInputDialog extends Action{

    public static final String ACTION_STRING_INPUT_DIALOG = "ACTION_STRING_INPUT_DIALOG";

    @Override
    protected void onActivate(final ExecutionInterface ei) throws JSONException {
        DialogAdapter da = (DialogAdapter) ei.getSystemFacade().getSystemService(DialogAdapter.SERVICE_DIALOGS);
        JSONObject pjo = getParsedParams(ei.getLocalVars(), ei.getGlobalVars());
        String message = pjo.getString("dialog_message");
        String title = pjo.getString("dialog_title");
        String strscope = pjo.getString("var_scope");
        final String strname = pjo.getString("var_name");


        final VariableManager.Scopes scope = VariableManager.Scopes.valueOf(strscope);


        da.showStringInputDialog(title, message, new DialogAdapter.OnInputDialogChoiceListener() {
            @Override
            public void onDialogChoice(String inputvalue) {
                switch(scope){
                    case LOCAL:
                        ei.getLocalVars().putVar(strname, VariableManager.Types.STRING, inputvalue);
                        break;
                    case GLOBAL:
                        ei.getGlobalVars().putVar(strname, VariableManager.Types.STRING, inputvalue);
                        break;
                }
                ei.unpause();
            }
        }, new DialogAdapter.OnInputDialogChoiceListener() {
            @Override
            public void onDialogChoice(String inputvalue) {
                ei.cancel();
            }
        });

       ei.pause();
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("var_name", FormalParamBuilder.Types.STRING, false)
                .put("var_scope", JSONUtils.enumValuesToJSONArray(VariableManager.Scopes.class), false)
                .put("dialog_title", FormalParamBuilder.Types.STRING, false)
                .put("dialog_message", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public String getName() {
        return ACTION_STRING_INPUT_DIALOG;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(DialogAdapter.REQUIREMENT_INPUT_DIALOGS, 1)
                .create();
    }
}
