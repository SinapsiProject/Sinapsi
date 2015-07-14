package com.sinapsi.engine.builder;

import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.engine.parameters.StringMatchingModeChoices;
import com.sinapsi.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * TODO: doku
 */
public class ParameterBuilder {

    //formal part
    private String name;
    private FormalParamBuilder.Types type;
    private boolean optional;
    private String[] choiceEntries;
    private FormalParamBuilder.BoolStyles boolStyle;

    //actual part
    private String strValue;
    private Integer intValue;
    private Boolean boolValue;
    private StringMatchingModeChoices stringMatchingMode;


    public ParameterBuilder(JSONObject formalObj, JSONObject actualValuesObj) {
        debuildParameter(formalObj, actualValuesObj);
    }

    private void debuildParameter(JSONObject formalObj, JSONObject actualValuesObj) {
        try {
            this.name = formalObj.getString(FormalParamBuilder.NAME);
            this.type = FormalParamBuilder.Types.valueOf(formalObj.getString(FormalParamBuilder.TYPE));
            this.optional = formalObj.getBoolean(FormalParamBuilder.OPTIONAL);

            switch (type){
                case CHOICE:
                    choiceEntries = JSONUtils.jsonArrayToStringArray(formalObj.getJSONArray(FormalParamBuilder.CHOICE_ENTRIES));
                    //break statement here is intentionally deleted
                case STRING:{
                    String val;
                    try{
                        val = actualValuesObj.getString(name);
                    } catch (JSONException je){
                        val = null;
                    }
                    strValue = val;
                }
                    break;

                case INT:{
                    Integer val;
                    try{
                        val =actualValuesObj.getInt(name);
                    }catch (JSONException je){
                        val = null;
                    }
                    intValue = val;

                }
                    break;

                case BOOLEAN:{
                    boolStyle = FormalParamBuilder.BoolStyles.valueOf(formalObj.getString(FormalParamBuilder.BOOL_STYLE));
                    Boolean val;
                    try{
                        val =actualValuesObj.getBoolean(name);
                    }catch (JSONException je){
                        val = null;
                    }
                    boolValue = val;

                }
                    break;

                case STRING_ADVANCED:{
                    JSONObject objval = actualValuesObj.optJSONObject(name);
                    String val = null;
                    String matchingMode = null;
                    try{
                        val = objval.getString("value");
                        matchingMode = objval.getString("matchingMode");
                    }catch (JSONException je){
                        val = null;
                        matchingMode = null;
                    }

                    this.stringMatchingMode = StringMatchingModeChoices.valueOf(matchingMode);
                    strValue = val;
                }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String buildActualString(List<ParameterBuilder> parameterBuilders){
        ActualParamBuilder apb = new ActualParamBuilder();
        for(ParameterBuilder pb: parameterBuilders){
            switch (pb.getType()){
                case CHOICE:
                case STRING:
                    if(pb.getStrValue() != null)
                        apb.put(pb.getName(), pb.getStrValue());
                    break;
                case INT:
                    if(pb.getIntValue() != null)
                        apb.put(pb.getName(), pb.getIntValue());
                    break;
                case BOOLEAN:
                    if(pb.getBoolValue() != null)
                        apb.put(pb.getName(), pb.getBoolValue());
                    break;
                case STRING_ADVANCED:
                    if(pb.getBoolValue() != null)
                        apb.put(pb.getName(), pb.getStrValue(), pb.getStringMatchingMode());
                    break;
            }
        }
        return apb.create().toString();
    }

    //FORMAL PART
    public String getName() {
        return name;
    }

    public FormalParamBuilder.Types getType() {
        return type;
    }

    public void setType(FormalParamBuilder.Types type) {
        this.type = type;
    }

    public boolean isOptional() {
        return optional;
    }


    public String[] getChoiceEntries() {
        return choiceEntries;
    }

    public FormalParamBuilder.BoolStyles getBoolStyle() {
        return boolStyle;
    }


    //ACTUAL PART
    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public StringMatchingModeChoices getStringMatchingMode() {
        return stringMatchingMode;
    }

    public void setStringMatchingMode(StringMatchingModeChoices stringMatchingMode) {
        this.stringMatchingMode = stringMatchingMode;
    }


}
