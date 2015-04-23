package com.sinapsi.utils;

import org.json.JSONArray;

import java.util.Arrays;

/**
 * Collection of static methods to convert Sinapsi-related
 * json strings to/from Sinapsi objects
 */
public class SinapsiJSONUtils {

    /**
     * Converts all enum values to a JSON array of strings
     * @param e the enum class
     * @return a JSON array object
     */
    public static JSONArray enumValuesToJSONArray(Class<? extends Enum<?>> e){
        String[] values = getEnumNames(e);
        JSONArray result = new JSONArray();
        for(String s:values){
            result.put(s);
        }
        return result;
    }

    /**
     * Converts all enum values names to an array of strings
     * @param e the enum class
     * @return a String array containing the enum values names
     */
    public static String[] getEnumNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

}
