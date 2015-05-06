package com.sinapsi.engine;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.DistributedComponent;
import com.sinapsi.model.Parameterized;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Action interface. This interface must be implemented
 * by every class implementing every different type of action.
 * For example, classes like ActionNotification and ActionSMS should
 * implement this interface.
 *
 */
public abstract class Action implements Parameterized, DistributedComponent {


    protected DeviceInterface executionDevice;
    protected String params;


    /**
     * Default ctor, needed by ComponentLoader to create an instance
     * with java reflection.
     * DO NOT DIRECTLY CALL THIS: THIS SHOULD BE CALLED ONLY BY
     * ComponentLoader. USE ComponentFactory TO CREATE A NEW INSTANCE
     * INSTEAD.
     */
    public Action(){
        executionDevice = null;
        params = null;
    }

    /**
     * Initializes the new Action instance.
     * @param executionDevice the device on which this action is going to be executed
     * @param params the JSON string containing the actual parameters
     */
    public void init(DeviceInterface executionDevice, String params){
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
     * @param ei the execution interface instance
     */
    public void activate(final ExecutionInterface ei){
        try{
            onActivate(ei);
        }catch(JSONException e){
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
     * @param actualParameters the string to be parsed
     * @return a JSONObject where names and values are actualParameters
     */
    public static JSONObject getParamsObj(String actualParameters){
        JSONObject o = null;
        try {
            o = new JSONObject(actualParameters);
        } catch (JSONException e1) {
            //actual parameters string is not well-formed
            e1.printStackTrace();
            return null;
        }
        JSONObject pjo;
        try {
            pjo = o.getJSONObject("parameters");
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
        return pjo;
    }

    @Override
    public String getFormalParameters() {
        JSONObject result = null;
        try {
            result = getFormalParametersJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result!=null ? result.toString() : null;
    }

    /**
     * Method the extending class has to implement in order to get
     * a JSON object containing all the necessary infos about the
     * required parameters of the action.
     * @return the JSONObject containing the formal parameters
     */
    protected abstract JSONObject getFormalParametersJSON() throws JSONException;
}
