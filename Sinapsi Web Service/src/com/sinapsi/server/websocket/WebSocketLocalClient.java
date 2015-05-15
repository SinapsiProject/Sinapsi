package com.sinapsi.server.websocket;


import java.io.IOException;
import java.net.URI;

import javax.json.Json;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint(encoders={MessageEncoder.class}, decoders={MessageDecoder.class})
public class WebSocketLocalClient {
    private Session userSession;
    
    /**
     * Default ctor
     * @param idDevice id of the device
     * @param name name of the client
     * @param endpointURI server endpoint uri
     */
    public WebSocketLocalClient(final URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }  
    
    /**
     * When connection is established
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) throws Exception {
        this.userSession = session;
        
        try {
            Message message = new Message(Json.createObjectBuilder()
                    .add("type", Message.TEXT_TYPE)
                    .add("data", "joined to Sinapsi").build());
                    
            session.getBasicRemote().sendObject(message);
            
            //DEBUG
            System.out.println("connected");
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Static send method
     * @param session session object
     * @param message message object
     */
    public static void send(Session session, Message message) {
        try {
            if(session.isOpen())
                session.getBasicRemote().sendObject(message);
            
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Intercept message sent from server endpoint
     * @param message message recived
     */
    @OnMessage
    public void onMessage(final Message message, Session session) {
        // if client recive a macro execution interface 
        if(message.getType().equals(Message.REMOTE_MACRO_TYPE)) {
            //DEBUG
            System.out.println("remote macro recived");
        }
        
        if(message.getType().equals(Message.TEXT_TYPE)) {
            //DEBUG
            System.out.println(message.getJson().getString("data"));
        }
        
        //DEBUG
        System.out.println("**Message recived**");
    }
    
    /**
     * On close connection
     * @param reason
     */
    @OnClose
    public void onClose (final CloseReason reason) {
        this.userSession = null;
    } 
    
    /**
     * Return the user session
     * @return
     */
    public Session getSession() {
        return this.userSession;
    }
}
