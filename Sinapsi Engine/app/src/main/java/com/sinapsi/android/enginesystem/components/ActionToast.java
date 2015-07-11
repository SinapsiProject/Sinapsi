package com.sinapsi.android.enginesystem.components;

import com.sinapsi.android.enginesystem.ToastAdapter;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Action toast. This is android-exclusive. Prints a "toast" message on the screen.
 */
public class ActionToast extends Action {

    public static final String ACTION_TOAST = "ACTION_TOAST";

    @Override
    protected void onActivate(ExecutionInterface ei) throws JSONException {
        ToastAdapter ta = (ToastAdapter) ei.getSystemFacade().getSystemService(ToastAdapter.SERVICE_TOAST);
        JSONObject pjo = getParsedParams(ei.getLocalVars(), ei.getGlobalVars());
        ta.printMessage(pjo.getString("message"));
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("message", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public String getName() {
        return ACTION_TOAST;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(ToastAdapter.REQUIREMENT_TOAST, 1)
                .create();
    }
}
