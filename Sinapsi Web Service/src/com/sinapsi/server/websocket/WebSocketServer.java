package com.sinapsi.server.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/remote_macro")
public class WebSocketServer {

    /**
     * Intercept new connection
     * @param session allow to send data to client
     */
    @OnOpen
    public void onOpen(Session session) {
        
    }
    
    /**
     * Handle message from client
     * @param message message recived
     * @param session allow to send data to client
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        
    }
    
    /**
     * Intercept close connection
     * @param session allow to send data to client
     */
    @OnClose
    public void onClose(Session session) {
        
    }
}
