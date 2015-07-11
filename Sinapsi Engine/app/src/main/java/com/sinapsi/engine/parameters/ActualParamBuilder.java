package com.sinapsi.engine.parameters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class for formal parameter list creation.
 * This comes handy during macro editing.
 */
public class ActualParamBuilder {
    public static final String PARAMETERS = "parameters";
    private JSONObject obj = new JSONObject();
    private JSONObject par = new JSONObject();

    /**
     * Default ctor.
     */
    public ActualParamBuilder(){}

    /**
     * Ctor with pre-constructed JSONObject. Using put()
     * and create() on a builder created with this ctor
     * will actually append the parameter entry to the
     * object o.
     * @param o the JSONObject
     */
    public ActualParamBuilder(JSONObject o){
        this.obj = o;
    }

    /**
     * Puts a new actual parameter entry
     * @param name the name of the parameter
     * @param value the value of the parameter
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public ActualParamBuilder put(String name, String value){
        try {
            par.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts a new actual parameter entry
     * @param name the name of the parameter
     * @param value the value of the parameter
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public ActualParamBuilder put(String name, Boolean value){
        try {
            par.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts a new actual parameter entry
     * @param name the name of the parameter
     * @param value the value of the parameter
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public ActualParamBuilder put(String name, Integer value){
        try {
            par.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Puts a new actual parameter entry of type STRING_ADVANCED
     * @param name the name of the parameter
     * @param value the value of the parameter
     * @param matchingMode the matching mode of this string
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public ActualParamBuilder put(String name,
                                  String value,
                                  StringMatchingModeChoices matchingMode){
        try {
            par.put(name, new JSONObject()
                    .put("advancedType", FormalParamBuilder.Types.STRING_ADVANCED.toString())
                    .put("value", value)
                    .put("matchingMode", matchingMode.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Creates a new JSONObject containing infos about the parameters.
     * @return the JSONObject
     *
     */
    public JSONObject create(){
        try {
            obj.put("parameters", par);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
