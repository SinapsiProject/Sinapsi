package com.sinapsi.server.websocket;


import java.io.IOException;
import java.net.URI;
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
public class WebSocketClient {
    private String name;
    private Session userSession = null;
    
    /**
     * Default ctor
     * @param idDevice id of the device
     * @param name name of the client
     * @param endpointURI server endpoint uri
     */
    public WebSocketClient(String name, final URI endpointURI) {
        this.name = name;
        
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
    public void onOpen(final Session session) throws Exception {
        this.userSession = session;
        
        try {
            userSession.getUserProperties().put("name", name);
            
            session.getBasicRemote().sendText(name + " connected");
            //DEBUG
            System.out.println(name + " connected");
            
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
        session.getUserProperties().put("data", message.getJson().get("data"));
        session.getUserProperties().put("data_type", message.getType());
        
        try {
            session.getBasicRemote().sendText("Message recived");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //DEBUG
        System.out.println("Message recived");
    }
    
    /**
     * On close connection
     * @param reason
     */
    @OnClose
    public void onClose (final CloseReason reason) {
        this.userSession = null;
        this.name = null;
    } 
    
    /**
     * Return the user session
     * @return
     */
    public Session getSession() {
        return this.userSession;
    }
}
