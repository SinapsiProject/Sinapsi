package com.sinapsi.webshared.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Trigger;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.FactoryModel;

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

    public static final String MACRO_ACTIONS = "macro_actions";

    public static final String TRIGGER_DEVICE_ID = "trigger_device_id";
    public static final String TRIGGER_NAME = "trigger_name";
    public static final String TRIGGER_JSON = "trigger_json";

    public static final String ACTION_DEVICE_ID = "action_device_id";
    public static final String ACTION_NAME = "action_name";
    public static final String ACTION_JSON = "action_json";

    public static final String COMMINFO_ERROR = "comminfo_error";
    public static final String COMMINFO_ERROR_DESC = "comminfo_error_desc";
    public static final String COMMINFO_ADDITIONAL = "comminfo_additional";

    private final ComponentFactory componentFactory;
    private final FactoryModel factoryModel = new FactoryModel();

    public MacroTypeAdapter(ComponentFactory componentFactory){
        this.componentFactory = componentFactory;
    }

    @Override
    public void write(JsonWriter out, MacroInterface m) throws IOException {
        out.beginObject();

        //CommunicationInfo
        out.name(COMMINFO_ERROR).value(m.isErrorOccured());
        out.name(COMMINFO_ERROR_DESC).value(m.getErrorDescription());
        out.name(COMMINFO_ADDITIONAL).value(m.getAdditionalInfo());

        //Macro's metadata
        out.name(MACRO_NAME).value(m.getName());
        out.name(MACRO_ID).value(m.getId());
        out.name(MACRO_ICON_NAME).value(m.getIconName());
        out.name(MACRO_ICON_COLOR).value(m.getMacroColor());
        out.name(MACRO_VALID).value(m.isValid());
        out.name(MACRO_FAILURE_POLICY).value(m.getExecutionFailurePolicy());
        out.name(MACRO_ENABLED).value(m.isEnabled());

        //Trigger
        if(m.getTrigger()!=null) {
            out.name(TRIGGER_DEVICE_ID).value(m.getTrigger().getExecutionDevice().getId());
            out.name(TRIGGER_NAME).value(m.getTrigger().getName());
            out.name(TRIGGER_JSON).value(m.getTrigger().getActualParameters());
        }else{
            out.name(TRIGGER_DEVICE_ID).value(-1);
            out.name(TRIGGER_NAME).nullValue();
            out.name(TRIGGER_JSON).nullValue();
        }

        //Actions
        out.name(MACRO_ACTIONS).beginArray();
        for(int i = 0; i < m.getActions().size(); ++i){
            Action a = m.getActions().get(i);
            out.name(ACTION_NAME).value(a.getName());
            out.name(ACTION_DEVICE_ID).value(a.getExecutionDevice().getId());
            out.name(ACTION_JSON).value(a.getActualParameters());
        }
        out.endArray();


        out.endObject();
    }

    @Override
    public MacroInterface read(JsonReader in) throws IOException {
        MacroInterface result = factoryModel.newMacro("",-1);

        in.beginObject();

        //Communication info
        in.nextName();//COMMINFO_ERROR
        boolean commError = in.nextBoolean();
        in.nextName();//COMMINFO_ERROR_DESC
        String commErrorDesc = in.nextString();
        in.nextName();//COMMINFO_ADDITIONAL
        String commAdditional = in.nextString();

        result.errorOccured(commError);
        result.setErrorDescription(commErrorDesc);
        result.setAdditionalInfo(commAdditional);


        //Macro's metadata
        in.nextName();//MACRO_NAME
        String macroName = in.nextString();
        in.nextName();//MACRO_ID
        int id = in.nextInt();
        in.nextName();//MACRO_ICON_NAME
        String iconName = in.nextString();
        in.nextName();//MACRO_ICON_COLOR
        String iconColor = in.nextString();
        in.nextName();//MACRO_VALID
        boolean valid = in.nextBoolean();
        in.nextName();//MACRO_FAILURE_POLICY
        String failurePolicy = in.nextString();
        in.nextName();//MACRO_ENABLED
        boolean enabled = in.nextBoolean();


        //Trigger
        in.nextName();//TRIGGER_DEVICE_ID
        int triggerDeviceId = in.nextInt();
        in.nextName();//TRIGGER_NAME
        String triggerName = null;
        if(in.peek() == JsonToken.NULL){
            in.nextNull();
        }else{
            triggerName = in.nextString();
        }
        in.nextName();//TRIGGER_JSON
        String triggerJson = null;
        if(in.peek() == JsonToken.NULL){
            in.nextNull();
        }else {
            triggerJson = in.nextString();
        }


        result.errorOccured(commError);
        result.setErrorDescription(commErrorDesc);
        result.setAdditionalInfo(commAdditional);

        result.setName(macroName);
        result.setId(id);
        result.setIconName(iconName);
        result.setMacroColor(iconColor);
        result.setValid(valid);
        result.setExecutionFailurePolicy(failurePolicy);

        Trigger trigger = null;
        if(triggerName == null){
            trigger = componentFactory.newEmptyTrigger(result);
        }else{
            trigger = componentFactory.newTrigger(
                    triggerName,
                    triggerJson,
                    result,
                    factoryModel.newDevice( //TODO: find a way to not insert a device here, but only the device id
                            triggerDeviceId,
                            "",
                            "",
                            "",
                            null,
                            -1
                    )
            );
        }

        result.setTrigger(trigger);
        result.setEnabled(enabled);

        //Actions
        in.nextName();//MACRO_ACTIONS
        in.beginArray();
        while(in.hasNext()){
            in.nextName();//ACTION_NAME
            String actionName = in.nextString();
            in.nextName();//ACTION_DEVICE_ID
            int actionDeviceId = in.nextInt();
            in.nextName();//ACTION_JSON
            String actionJson = in.nextString();

            Action action = componentFactory.newAction(
                    actionName,
                    actionJson,
                    factoryModel.newDevice(
                            actionDeviceId,
                            "",
                            "",
                            "",
                            null,
                            -1
                    )
            );

            result.addAction(action);

        }
        in.endArray();
        in.endObject();


        return result;
    }
}
