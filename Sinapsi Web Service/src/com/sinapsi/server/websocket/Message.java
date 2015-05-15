package com.sinapsi.server.websocket;

import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;


/**
 * JSon wrapper class, needed by OnMessage annotation method to accept the object
 * as a method 
 *
 */
public class Message {
    public static final String TEXT_TYPE = "text";
    public static final String REMOTE_MACRO_TYPE = "remote macro";
    public static final String MACRO_TYPE = "macro";
    public static final String BINARY_TYPE = "binary";
    private JsonObject json;
    private String type;
    
    /**
     * Default ctor
     * @param json
     */
    public Message(JsonObject json) {
        this.json = json;
        this.type = json.getString("type");
    }

    /**
     * Return the json object
     * @return
     */
    public JsonObject getJson() {
        return json;
    }
    
    /**
     * Setter json object
     * @param json json object
     */
    public void setJson(JsonObject json) {
        this.json = json;
    }
    
    /**
     * To string override method to return a string rappresentation of the json object
     */
    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        Json.createWriter(writer).write(json);
        return writer.toString();
    }
    
    /**
     * Return the type of message
     * @return
     */
    public String getType() {
        return type;
    }
    
    /**
     * Setter for message type
     * @param type type of the message
     */
    public void setType(String type) {
        this.type = type;
    }
}
