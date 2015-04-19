package com.sinapsi.model;

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

    public Action(DeviceInterface executionDevice, String params){
        this.executionDevice = executionDevice;
        this.params = params;
    }

    /**
     * Method to be implemented in order to be called by
     * the engine when an action instance should do his work
     * inside a macro.
     *
     * @param di passed by the MacroEngine, used to give access
     *           to eventual system-dependant calls needed by
     *           the action
     */
    public abstract void activate(DeviceInterface di);

    @Override
    public String getActualParameters() {
        return params;
    }

    @Override
    public void setActualParameters(String params) {
        this.params = params;
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
