package com.sinapsi.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class for formal parameter list creation.
 */
public class FormalParamBuilder {

    public static final String TYPE_CHOICE = "choice";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_INTEGER = "int";
    public static final String TYPE_BOOLEAN = "boolean";


    private JSONArray arr = new JSONArray();
    private JSONObject obj = new JSONObject();

    /**
     * Default ctor.
     */
    public FormalParamBuilder(){}

    /**
     * Ctor with pre-constructed JSONObject. Using put()
     * and create() on a builder created with this ctor
     * will actually append the parameter array to the
     * object o.
     * @param o the JSONObject
     */
    public FormalParamBuilder(JSONObject o){
        this.obj = o;
    }

    /**
     * Puts a new parameter in the array. For choice
     * types use put(String,JSONArray,boolean).
     * @param name the name chosen for this parameter
     * @param type the type of this parameter
     * @param optional true for optional, false for required
     * @return the invocation object itself, to allow method
     *         chaining.
     * @throws JSONException
     */
    public FormalParamBuilder put(String name, String type, boolean optional) throws JSONException{
        arr.put(new JSONObject()
            .put("name", name)
            .put("type", type)
            .put("optional", optional));
        return this;
    }

    /**
     * Puts a new parameter of type 'choice' in the array.
     * @param name the name chosen for this parameter
     * @param choiceEntries a JSONArray containing all the possible
     *                      entries for this choice parameter
     * @param optional true for optional, false for required
     * @return the invocation object itself, to allow method
     *         chaining.
     * @throws JSONException
     */
    public FormalParamBuilder put(String name, JSONArray choiceEntries, boolean optional) throws JSONException{
        arr.put(new JSONObject()
            .put("name", name)
            .put("type", "choice")
            .put("choiceEntries", choiceEntries)
            .put("optional", optional));
        return this;
    }

    /**
     * Creates a new JSONObject containg infos about the parameters.
     * @return the JSONObject
     * @throws JSONException
     */
    public JSONObject create() throws JSONException {
        return obj.put("formal_parameters", arr);
    }

}
