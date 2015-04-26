package com.sinapsi.engine.components;

import com.sinapsi.engine.ExecutionInterface;
import com.sinapsi.model.Action;
import com.sinapsi.model.parameters.FormalParamBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;

/**
 * TODO: doku
 */
public class ActionLuaScript extends Action{

    public static final String ACTION_LUA_SCRIPT = "ACTION_LUA_SCRIPT";
    public static final int ACTION_LUA_SCRIPT_ID = 3;

    @Override
    public void activate(ExecutionInterface di) {
        String scriptText;
        JSONObject pjo = getParamsObj(params);
        try{
            scriptText = pjo.getString("lua_script");
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }
        try {
            Globals globals =JsePlatform.standardGlobals();
            LuaValue chunk = globals.load(scriptText);

            LuaValue system = toLua(di.getSystemFacade());
            //TODO: add vars

            chunk.call(system);
        }
        catch (LuaError e) {
            //probably an error in script
            e.printStackTrace();
            return;
        }

    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder().
                put("lua_script", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public int getId() {
        return ACTION_LUA_SCRIPT_ID;
    }

    @Override
    public String getName() {
        return ACTION_LUA_SCRIPT;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null;
    }


    private LuaValue toLua(Object javaValue) {
        return javaValue == null? LuaValue.NIL:
                javaValue instanceof LuaValue? (LuaValue) javaValue:
                        CoerceJavaToLua.coerce(javaValue);
    }
}
