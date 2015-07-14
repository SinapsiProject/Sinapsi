package com.sinapsi.engine.parameters;

import com.sinapsi.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for formal parameter list creation.
 */
public class FormalParamBuilder {

    public static final String FORMAL_PARAMETERS = "formal_parameters";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String BOOL_STYLE = "boolStyle";
    public static final String OPTIONAL = "optional";
    public static final String CHOICE_ENTRIES = "choiceEntries";

    public enum Types {
        CHOICE,
        STRING,
        INT,
        BOOLEAN,
        STRING_ADVANCED
    }

    public enum BoolStyles{
        TRUE_FALSE,
        ON_OFF,
        ACTIVATE_DEACTIVATE,
        YES_NO,
        ENABLE_DISABLE
    }

    public static final List<String[]> STYLED_BOOL_CONSTANTS = Arrays.asList(
            new String[]{"TRUE", "FALSE"},
            new String[]{"ON", "OFF"},
            new String[]{"ACTIVATE", "DEACTIVATE"},
            new String[]{"YES", "NO"},
            new String[]{"ENABLE", "DISABLE"}
    );

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
    public FormalParamBuilder put(String name, Types type, boolean optional) throws JSONException{
        arr.put(new JSONObject()
            .put(NAME, name)
            .put(TYPE, type.toString())
            .put(OPTIONAL, optional));
        return this;
    }

    /**
     * Puts a new parameter of type 'CHOICE' in the array.
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
            .put(NAME, name)
            .put(TYPE, Types.CHOICE.toString())
            .put(CHOICE_ENTRIES, choiceEntries)
            .put(OPTIONAL, optional));
        return this;
    }

    /**
     * Puts a parameter of type 'BOOLEAN' in the array
     * @param name the name chosen for this parameter
     * @param boolStyle the meaning of the boolean value
     * @param optional true for optional, false for required
     * @return the invocation object itself, to allow method
     *         chaining.
     * @throws JSONException
     */
    public FormalParamBuilder put(String name, BoolStyles boolStyle, boolean optional) throws JSONException{
        arr.put(new JSONObject()
                .put(NAME, name)
                .put(TYPE, Types.BOOLEAN.toString())
                .put(BOOL_STYLE, boolStyle.toString())
                .put(OPTIONAL, optional));
        return this;
    }


    /**
     * Puts a parameter of type 'STRING_ADVANCED' in the array.
     * A parameter of this type can give the user the option to
     * choose if the given value is has to match the whole string
     * completely, if it must be contained, or if it is a regex pattern.
     * @param name the name chosen for this parameter
     * @param optional true for optional, false for required
     * @return the invocation object itself, to allow method
     *         chaining.
     * @throws JSONException
     */
    public FormalParamBuilder putAdvancedString(String name, boolean optional) throws JSONException{
        arr.put(new JSONObject()
                .put(NAME, name)
                .put(TYPE, Types.STRING_ADVANCED.toString())
                .put(CHOICE_ENTRIES, JSONUtils.enumValuesToJSONArray(StringMatchingModeChoices.class))
                .put(OPTIONAL, optional));
        return this;
    }

    /**
     * Creates a new JSONObject containing infos about the parameters.
     * @return the JSONObject
     * @throws JSONException
     */
    public JSONObject create() throws JSONException {
        return obj.put(FORMAL_PARAMETERS, arr);
    }



}
