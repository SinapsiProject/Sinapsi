package com.sinapsi.webshared.wsproto;

/**
 * Class used to wrap data into a message containing other metadata (like the type of message),
 * in order to be serialized (with gson) and sent across a web socket connection.
 */
public class WebSocketMessage {

    private String msgType;
    private Object data;

    public WebSocketMessage(String msgType, Object data) {
        this.msgType = msgType;
        this.data = data;
    }

    public String getMsgType() {
        return msgType;
    }

    public Object getData() {
        return data;
    }
}
