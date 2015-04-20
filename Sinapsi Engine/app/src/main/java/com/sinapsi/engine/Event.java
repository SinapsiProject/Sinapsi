package com.sinapsi.engine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Event class. An instance of this class is passed to a trigger
 * when it is activated from a system event, in order
 * to get eventually required infos about the event that
 * activated the trigger.
 *
 */
public class Event {
    private HashMap<String, Object> datamap = new HashMap<>();

    /**
     * Puts a new entry in the map
     * @param key the key
     * @param value the value
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public Event put(String key, Object value){
        datamap.put(key,value);
        return this;
    }

    /**
     * Gets the value for key
     * @param key the key
     * @return the value
     */
    public Object getObject(String key){
        return datamap.get(key);
    }

    /**
     * Gets the value for key
     * @param key the key
     * @return the value
     */
    public String getString(String key){
        return (String) datamap.get(key);
    }

    /**
     * Gets the value for key
     * @param key the key
     * @return the value
     */
    public Integer getInteger(String key){
        return (Integer) datamap.get(key);
    }

    /**
     * Gets the value for key
     * @param key the key
     * @return the value
     */
    public Boolean getBoolean(String key){
        return (Boolean) datamap.get(key);
    }

    /**
     * Directly extract all the Event map entries in a JSONObject,
     * formatted like the JSONObjects returned by Trigger.extractParameterValues()
     * @return a JSONObject containing all the Event entries
     * @throws JSONException
     */
    public JSONObject getJSONObject() throws JSONException{
        if(datamap.isEmpty()) return null;
        JSONObject jsonO = new JSONObject();
        for(Map.Entry<String, Object> e: datamap.entrySet()){
            Object o = e.getValue();
            String k = e.getKey();
            jsonO.put(k,o);
        }
        return jsonO;
    }
}
