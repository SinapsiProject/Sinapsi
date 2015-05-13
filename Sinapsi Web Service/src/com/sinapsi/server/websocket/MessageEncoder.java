package com.sinapsi.server.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    /**
     * Message encoder destroy
     */
    @Override
    public void destroy() {
        //DEBUG
        System.out.println("message encoder destroy");
        
    }

    /**
     * Message encoder init
     */
    @Override
    public void init(EndpointConfig arg0) {
        //DEBUG
        System.out.println("message encoder init");
    }

    /**
     * Message encoder
     */
    @Override
    public String encode(Message message) throws EncodeException {
        return message.getJson().toString();
    }

}
