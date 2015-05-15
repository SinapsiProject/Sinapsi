package com.sinapsi.server.websocket;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.json.JsonObject;


public class MessageDecoder implements Decoder.Text<Message> {

    /**
     * Message decoder destroy
     */
    @Override
    public void destroy() {
        //DEBUG
        System.out.println("destroy message decoder");
    }

    /**
     * Message decoder
     */
    @Override
    public void init(EndpointConfig arg0) {
        //DEBUG
        System.out.println("init message decoder"); 
    }
    
    /**
     * Decode string into a json object
     */
    @Override
    public Message decode(String string) throws DecodeException {
       JsonObject json = Json.createReader(new StringReader(string)).readObject();
        return new Message(json);
    }

    /**
     * Check if a string is "decodable into a json object"
     */
    @Override
    public boolean willDecode(String string) {
        try {
            Json.createReader(new StringReader(string)).read();
            return true;
            
        } catch(JsonException e) {
            //DEBUG
            System.out.println(string + " is not decodable");
            e.printStackTrace();
            return false;
        }
    }

}
