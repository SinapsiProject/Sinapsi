package com.sinapsi.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

/**
 * Collection of static methods to convert Sinapsi-related
 * json strings to/from Sinapsi objects
 */
public class JSONUtils {

    /**
     * Converts all enum values to a JSON array of strings
     *
     * @param e the enum class
     * @return a JSON array object
     */
    public static JSONArray enumValuesToJSONArray(Class<? extends Enum<?>> e) {
        String[] values = getEnumNames(e);
        JSONArray result = new JSONArray();
        for (String s : values) {
            result.put(s);
        }
        return result;
    }

    /**
     * Converts all enum values names to an array of strings
     *
     * @param e the enum class
     * @return a String array containing the enum values names
     */
    public static String[] getEnumNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    /**
     * Converts a json array of strings to a java array of strings
     *
     * @param a the json array (of string values)
     * @return an array of strings
     */
    public static String[] jsonArrayToStringArray(JSONArray a) {
        String[] result = new String[a.length()];
        try {
            for (int i = 0; i < a.length(); ++i) {
                result[i] = a.getString(i);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the String array arr contains the String s
     *
     * @param arr the input array
     * @param str the string
     * @return true if arr contains s, false otherwise
     */
    public static boolean contains(String[] arr, String str) {
        boolean c = false;
        for(String s: arr){
            if(s.equals(str)){
                c = true;
                break;
            }
        }
        return c;
    }

}
