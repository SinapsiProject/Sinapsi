package com.sinapsi.engine.components;

import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionSetVariable class. This Action will assign a value to
 * the specified value.
 * This action must be parametrized to know at execution phase
 * the variable name, the type and the value.
 * Notice that this action is completely platform-independent:
 * it relies on other facades/adapters in ExecutionInterface.
 */
public class ActionSetVariable extends Action {

    public static final String ACTION_SET_VARIABLE = "ACTION_SET_VARIABLE";


    @Override
    public void onActivate(final ExecutionInterface di) throws JSONException{
        JSONObject pjo = getParsedParams(di.getLocalVars(),di.getGlobalVars());
        String strname = null;
        String strscope = null;
        String strtype = null;
        String strvalue = null;

        strname = pjo.getString("var_name");
        strscope = pjo.getString("var_scope");
        strtype = pjo.getString("var_type");
        strvalue = pjo.getString("var_value");

        VariableManager.Scopes scope = VariableManager.Scopes.valueOf(strscope);
        VariableManager.Types type = VariableManager.Types.valueOf(strtype);

        switch (scope){
            case LOCAL:
                di.getLocalVars().putVar(strname, type, strvalue);
                break;
            case GLOBAL:
                di.getGlobalVars().putVar(strname, type, strvalue);
                break;
        }

    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("var_name", FormalParamBuilder.Types.STRING, false)
                .put("var_scope", JSONUtils.enumValuesToJSONArray(VariableManager.Scopes.class), false)
                .put("var_type", JSONUtils.enumValuesToJSONArray(VariableManager.Types.class), false)
                .put("var_value", FormalParamBuilder.Types.STRING, false)//TODO: the editor needs to know that this string should be parsed to check type
                .create();
    }

    @Override
    public String getName() {
        return ACTION_SET_VARIABLE;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null; //NO REQUIREMENTS NEEDED. Integrated in engine.
                     //This means this action is always available, on
                     // every device.
    }
}
