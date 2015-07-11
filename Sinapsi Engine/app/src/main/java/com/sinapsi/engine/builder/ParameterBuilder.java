package com.sinapsi.engine.builder;

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
                    if(val == null){
                        if(!isOptional()){
                            //TODO: inconsistent actual parameter error
                        }
                    }else{
                        strValue = val;
                    }
                }
                    break;

                case INT:{
                    Integer val;
                    try{
                        val =actualValuesObj.getInt(name);
                    }catch (JSONException je){
                        val = null;
                    }
                    if(val == null){
                        if(!isOptional()){
                            //TODO: inconsistent actual parameter error
                        }
                    }else{
                        intValue = val;
                    }
                }
                    break;

                case BOOLEAN:{
                    Boolean val;
                    try{
                        val =actualValuesObj.getBoolean(name);
                    }catch (JSONException je){
                        val = null;
                    }
                    if(val == null){
                        if(!isOptional()){
                            //TODO: inconsistent actual parameter error
                        }
                    }else{
                        boolValue = val;
                    }
                }
                    break;

                case STRING_ADVANCED:{
                    JSONObject objval = actualValuesObj.optJSONObject(name);
                    if(objval == null){
                        if(!isOptional()){
                            //TODO: inconsistent actual parameter error
                        }
                    }else{
                        String val = null;
                        String matchingMode = null;
                        try{
                            val = objval.getString("value");
                            matchingMode = objval.getString("matchingMode");
                        }catch (JSONException je){
                            //does nothing
                        }
                        if(matchingMode == null){
                            //TODO: inconsistent actual parameter error
                        } else if(val == null ){
                            if(!isOptional()){
                                //TODO: inconsistent actual parameter error
                            }
                        } else {
                            this.stringMatchingMode = StringMatchingModeChoices.valueOf(matchingMode);
                            strValue = val;
                        }
                    }
                }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String buildActualString(List<ParameterBuilder> parameterBuilders){
        return null; //TODO: impl
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
