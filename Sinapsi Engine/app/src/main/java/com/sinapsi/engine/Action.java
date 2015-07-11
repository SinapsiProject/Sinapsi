package com.sinapsi.engine;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.DistributedComponent;
import com.sinapsi.model.Parameterized;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action interface. This interface must be implemented
 * by every class implementing every different type of action.
 * For example, classes like ActionNotification and ActionSMS should
 * implement this interface.
 */
public abstract class Action implements Parameterized, DistributedComponent {


    protected DeviceInterface executionDevice;
    protected String params;
    protected String inStringVariablePattern = "@\\{([_a-zA-Z][_a-zA-Z0-9]*)\\}"; // @{var_identifier}


    /**
     * Default ctor, needed by ComponentLoader to create an instance
     * with java reflection.
     * DO NOT DIRECTLY CALL THIS: THIS SHOULD BE CALLED ONLY BY
     * ComponentLoader. USE ComponentFactory TO CREATE A NEW INSTANCE
     * INSTEAD.
     */
    public Action() {
        executionDevice = null;
        params = null;
    }

    /**
     * Initializes the new Action instance.
     *
     * @param executionDevice the device on which this action is going to be executed
     * @param params          the JSON string containing the actual parameters
     */
    public void init(DeviceInterface executionDevice, String params) {
        this.executionDevice = executionDevice;
        this.params = params;
    }

    /**
     * Method to be implemented in order to be called by
     * the engine when an action instance should do his work
     * inside a macro.
     *
     * @param ei passed by the MacroEngine, used to give access
     *           to eventual system-dependant calls needed by
     *           the action
     */
    protected abstract void onActivate(final ExecutionInterface ei) throws JSONException;


    /**
     * Call this method to activate the action.
     *
     * @param ei the execution interface instance
     */
    public void activate(final ExecutionInterface ei) {
        try {
            ei.getLog().log(getName(), "Executing action");
            onActivate(ei);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getActualParameters() {
        return params;
    }

    @Override
    public void setActualParameters(String params) {
        this.params = params;
    }

    @Override
    public ComponentTypes getComponentType() {
        return ComponentTypes.ACTION;
    }

    @Override
    public DeviceInterface getExecutionDevice() {
        return executionDevice;
    }


    /**
     * Helper method to parse a JSONObject inside a string and returning
     * the 'parameters' entry contained in it.
     *
     * @param actualParameters the string to be parsed
     * @return a JSONObject where names and values are actualParameters
     */
    public static JSONObject getParamsObj(String actualParameters) throws JSONException {
        JSONObject o = new JSONObject(actualParameters);
        return o.getJSONObject("parameters");
    }


    /**
     * Parses the actual parameters JSONObject in search for variable occurrences,
     * and replaces them with their respective values.
     *
     * @param variableManagers the list of variable managers used for variable queries.
     *                         the order must be from the most local scope to the most
     *                         global one.
     *
     * @return a JSONObject with only values, no variables.
     * @throws JSONException
     */
    public JSONObject getParsedParams(VariableManager... variableManagers) throws JSONException{
        JSONObject actParentObj = getParamsObj(params);
        JSONObject formParentObj = getFormalParametersJSON();
        JSONArray formArr = formParentObj.getJSONArray("formal_parameters");

        JSONObject result = new JSONObject();

        for(int i = 0; i < formArr.length(); ++i){
            JSONObject formObj = formArr.getJSONObject(i);
            String formObjName = formObj.getString("name");
            String formObjType = formObj.getString("type");
            Boolean formObjOptional = formObj.getBoolean("optional");

            if(formObjOptional && !actParentObj.has(formObjName)) continue;

            FormalParamBuilder.Types formObjType_e = FormalParamBuilder.Types.valueOf(formObjType);

            switch (formObjType_e) {
                case CHOICE:
                    result.put(formObjName, actParentObj.getString(formObjName)); //for now, there are no vars of type choice
                    break;
                case STRING:{
                    //searches for @{variable_names} inside the string
                    String original = actParentObj.getString(formObjName);
                    Pattern pattern = Pattern.compile(inStringVariablePattern);
                    Matcher matcher = pattern.matcher(original);
                    String[] splits = original.split(inStringVariablePattern, -1);

                    ArrayList<String> identifiers = new ArrayList<>();

                    while(matcher.find()) {
                        identifiers.add(matcher.group(1));
                    }


                    String stresult = splits[0];
                    for(int j = 1; j < splits.length; ++j){
                        String varval = "NULL";
                        for(VariableManager v: variableManagers)
                        {
                            if(v.containsVariable(identifiers.get(j-1))) {
                                varval = v.getStringRepresentationOfValue(identifiers.get(j - 1));
                                break;
                            }
                        }
                        stresult += varval + splits[j];
                    }

                    result.put(formObjName, stresult);

                }
                    break;
                case INT:{
                    if(actParentObj.get(formObjName) instanceof Integer){
                        result.put(formObjName, actParentObj.getInt(formObjName));
                    }else{
                        String tmp = actParentObj.getString(formObjName);
                        int varval = 0; //DEFAULT VALUE FOR UNDEFINED INTEGER VARIABLE IS 0

                        for(VariableManager v: variableManagers)
                        {
                            if(v.containsVariable(tmp)) {
                                varval =(Integer) v.getVarValue(tmp);
                                break;
                            }
                        }
                        result.put(formObjName, varval);

                    }

                }
                    break;
                case BOOLEAN:{
                    if(actParentObj.get(formObjName) instanceof Boolean) {
                        result.put(formObjName, actParentObj.getBoolean(formObjName));
                    }else{
                        String tmp = actParentObj.getString(formObjName);
                        boolean varval = false; //DEFAULT VALUE FOR UNDEFINED BOOLEAN VARIABLE IS FALSE

                        for(VariableManager v: variableManagers)
                        {
                            if(v.containsVariable(tmp)){
                                varval = (Boolean) v.getVarValue(tmp);
                                break;
                            }
                        }
                        result.put(formObjName, varval);
                    }

                }
                    break;
                case STRING_ADVANCED:
                    result.put(formObjName, actParentObj.getJSONObject(formObjName));//TODO: impl string_advanced variable parsing
                    break;
            }
        }
        return result;
    }

    @Override
    public String getFormalParameters() {
        JSONObject result = null;
        try {
            result = getFormalParametersJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result != null ? result.toString() : null;
    }

    /**
     * Method the extending class has to implement in order to get
     * a JSON object containing all the necessary infos about the
     * required parameters of the action.
     *
     * @return the JSONObject containing the formal parameters
     */
    public abstract JSONObject getFormalParametersJSON() throws JSONException;
}
