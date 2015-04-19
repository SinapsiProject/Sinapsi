package com.sinapsi.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Trigger interface. This interface must be implemented
 * by every class implementing every different type of trigger.
 * For example, classes like TriggerWifi and TriggerSMS should
 * implement this interface.
 *
 */
public abstract class Trigger implements Parameterized, DistributedComponent {

    protected DeviceInterface executionDevice;
    protected String params;
    protected MacroInterface macro;


    public Trigger(DeviceInterface executionDevice, String parameters, MacroInterface macro){
        this.executionDevice = executionDevice;
        this.params = parameters;
        this.macro = macro;
    }

    /**
     * Method to be overridden in order to be called when a
     * system event regarding the type of trigger occurs in order to
     * make additional actions.
     * @param e Event object containing infos about the system event
     *          that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     */
    public void onActivate(Event e, DeviceInterface di){
        //override this if you want to do something else on trigger activation
    }

    /**
     * Method to be called in order to activate this trigger.
     * @param e Event object containing infos about the system event
     *          that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     */
    public void activate(Event e, DeviceInterface di){
        if(checkParameters(e, di)) {
            onActivate(e,di);
            macro.execute(di);
        }

    }

    //TODO: add setEnabled(boolean,ActivationManager);

    @Override
    public DeviceInterface getExecutionDevice() {
        return executionDevice;
    }


    @Override
    public String getActualParameters() {
        return params;
    }

    @Override
    public void setActualParameters(String params) {
        this.params = params;
    }


    /**
     * Method called by Activate in order to check at execution phase
     * if the current state of the system or of the event meets the
     * parameters given at trigger instantiation.
     * @param e Event object containing infos about the system event
     *          that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     * @return true if each one of the actual parameters equals the
     *         extracted parameter from system/event's state.
     */
    protected boolean checkParameters(Event e, DeviceInterface di) {
        if(getActualParameters() == null) return true;

        JSONObject actualParameterObj;
        try {
            actualParameterObj = new JSONObject(getActualParameters());
        } catch (JSONException e1) {
            e1.printStackTrace();
            return false;
            //we don't rethrow just to not make the system crash for one trigger.
        }

        JSONObject pjo;
        try {
            pjo = actualParameterObj.getJSONObject("parameters");
        } catch (JSONException e1) {
            /*
            there is no parameters array, and, because all trigger parameters
            are optional, and are meant to filter the events, this means 'just
            activate the macro every time (i.e. either when wifi sets on and off)
            */
            e1.printStackTrace();
            macro.execute(di);
            return true;
        }

        JSONObject valuesObj = null;
        try {
            valuesObj = extractParameterValues(e, di);
        } catch (JSONException e1) {
            e1.printStackTrace();
            //This means that the extending class is bad implemented
        }
        if(valuesObj == null) return true;

        Iterator<String> pars = pjo.keys();

        while (pars.hasNext()){
            String key = pars.next();
            Object parvalue = pjo.opt(key);
            if(parvalue == null){
                continue;
            } else if(parvalue instanceof String){
                try {
                    if(!((String)parvalue).equals(valuesObj.getString(key)))
                        return false;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    return false;
                }
            } else if(parvalue instanceof Boolean){
                try {
                    if(!((Boolean)parvalue).equals(valuesObj.getBoolean(key)))
                        return false;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    return false;
                }
            } else if(parvalue instanceof Integer){
                try {
                    if(!((Integer)parvalue).equals(valuesObj.getInt(key)))
                        return false;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    return false;
                }
            } else {
                throw new UnsupportedOperationException(
                        "Check still not available for this type");
            }
        }

        //TODO: test, check, and probably remake with a JSONReader

        return true;
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
     * required parameters of the trigger.
     * @return the JSONObject containing the formal parameters
     */
    protected abstract JSONObject getFormalParametersJSON() throws JSONException;

    /**
     * Method the extending class has to implement in order to get
     * a JSON object containing all the necessary values for a check
     * of the given parameters at execution phase. These values are
     * usually extracted from the event that activated the trigger
     * or from the device/system on which the trigger is running.
     * @param e Event object containing infos about the system event
     *          that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     * @return just return null if no values are supposed to be set,
     *         or a JSONObject containing the parameters.
     */
    protected abstract JSONObject extractParameterValues(Event e, DeviceInterface di) throws JSONException;
}
