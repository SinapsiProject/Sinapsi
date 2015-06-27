package com.sinapsi.webshared.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sinapsi.engine.Action;
import com.sinapsi.model.MacroInterface;

import java.io.IOException;

/**
 * Default Gson's type adapter for Macro objects.
 */
public class MacroTypeAdapter extends TypeAdapter<MacroInterface> {

    public static final String MACRO_ID = "id";
    public static final String MACRO_NAME = "name";
    public static final String MACRO_ICON_NAME = "icon_name";
    public static final String MACRO_ICON_COLOR = "icon_color";
    public static final String MACRO_VALID = "valid";
    public static final String MACRO_FAILURE_POLICY = "failure_policy";
    public static final String MACRO_ENABLED = "enabled";
    public static final String MACRO_TRIGGER_DEVICE_ID = "trigger_device_id";
    public static final String MACRO_TRIGGER_NAME = "trigger_name";
    public static final String MACRO_TRIGGER_JSON = "trigger_json";
    public static final String MACRO_ACTIONS = "macro_actions";

    public static final String ACTION_ORDER = "action_order";
    public static final String ACTION_DEVICE_ID = "action_device_id";
    public static final String ACTION_NAME = "action_name";
    public static final String ACTION_JSON = "action_json";
    @Override
    public void write(JsonWriter out, MacroInterface m) throws IOException {
        out.beginObject();
        out.name(MACRO_NAME).value(m.getName());
        out.name(MACRO_ID).value(m.getId());
        out.name(MACRO_ICON_NAME).value(m.getIconName());
        out.name(MACRO_ICON_COLOR).value(m.getMacroColor());
        out.name(MACRO_VALID).value(m.isValid());
        out.name(MACRO_FAILURE_POLICY).value(m.getExecutionFailurePolicy());
        out.name(MACRO_ENABLED).value(m.isEnabled());
        if(m.getTrigger()!=null) {
            out.name(MACRO_TRIGGER_DEVICE_ID).value(m.getTrigger().getExecutionDevice().getId());
            out.name(MACRO_TRIGGER_NAME).value(m.getTrigger().getName());
            out.name(MACRO_TRIGGER_JSON).value(m.getTrigger().getActualParameters());
        }else{
            out.name(MACRO_TRIGGER_DEVICE_ID).value(-1);
            out.name(MACRO_TRIGGER_NAME).nullValue();
            out.name(MACRO_TRIGGER_JSON).nullValue();
        }
        out.name(MACRO_ACTIONS).beginArray();
        for(int i = 0; i < m.getActions().size(); ++i){
            Action a = m.getActions().get(i);
            out.name(ACTION_NAME).value(a.getName());
            out.name(ACTION_ORDER).value(i);
            out.name(ACTION_DEVICE_ID).value(a.getExecutionDevice().getId());
            out.name(ACTION_JSON).value(a.getActualParameters());
        }
        out.endArray();
        out.endObject();
    }

    @Override
    public MacroInterface read(JsonReader in) throws IOException {
        return null;
    }
}
