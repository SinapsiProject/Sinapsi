package com.sinapsi.server.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.OnMessage;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint(value = "/websocket/{idDevice}", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class WebSocketServer {
    private static final Set<Session> clientsSet = Collections.synchronizedSet(new HashSet<Session>());
    private static final Map<String, Session> clients = Collections.synchronizedMap(new HashMap<String, Session>());
    
    /**
     * Send broadcast message to all connected clients
     * @param message message to send
     */
    public void broadcastMessage(Message message) {
        for (Session client : clientsSet) {
            synchronized (client) {
                if (client.isOpen()) {
                    try {
                        client.getBasicRemote().sendObject(message);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    } 
                }
            }
        }
    }

    /**
     * Send message to a specific device, target device added in the json message
     * @param message message to send
     * @param idDevice id of the device target
     */
    public void send(Message message, String idDevice) {
        if(clients.get(idDevice).isOpen()) {
            try {
                clients.get(idDevice).getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Intercept message sent from connected clients
     * @param message Message object
     * @param session Session object
     */
    @OnMessage
    public void onMessage(Session session, Message message) {
        // parse recived message
        String idDeviceTarget = null;
        
        if(message.getJson().containsKey("to"))
            idDeviceTarget = message.getJson().getString("to");
        
        if(idDeviceTarget != null  && clients.get(idDeviceTarget).isOpen()) {
            send(message, idDeviceTarget);
            
        } else {
            //DEBUG
            System.out.println("Message not sent to device target, probabily is not connected");
        }
        
        //broadcast message that contain macro
        if(message.getJson().containsKey("data")) {
            if(message.getJson().getString("type").equals(Message.MACRO_TYPE))
                broadcastMessage(message);
            
        }

               
        // DEBUG
        if(idDeviceTarget != null) {
            System.out.println("Remote execution from:"+ idDeviceTarget);
            System.out.println("to: " + message.getJson().getString("from"));
        }
        System.out.println("data: " + message.getJson().getString("data"));
    }

    /**
     * Allows us to intercept the creation of a new session. Session class
     * allows us to send data to users
     * 
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("idDevice") String idDevice) {
 
        Message message = new Message(Json.createObjectBuilder()
                .add("type", Message.TEXT_TYPE)
                .add("data", idDevice + " joined Sinapsi")
                .add("id", idDevice).build());

        // Broadcast send: notify to all client connected that has joined a new device
        broadcastMessage(message);

        // save in a map the current user 
        session.getUserProperties().put("device", idDevice);
        clientsSet.add(session);
        clients.put(idDevice, session);
        
        // internal debug
        System.out.println(idDevice + " has joined");

    }

    @OnClose
    public void onClose(Session session) {
        // Remove session from the connected sessions set
        clientsSet.remove(session);
        clients.remove(session.getUserProperties().get("device"));
        
        // Broadcast send: notify to all client connected that has closed a
        // connection from a device
        for (Session s : clientsSet) {
            try {
                Message message = new Message(Json
                        .createObjectBuilder()
                        .add("type", Message.TEXT_TYPE)
                        .add("data", "Connection with " + session.getUserProperties().get("device") + " has been closed")
                        .add("id", (String) session.getUserProperties().get("device")).build());

                s.getBasicRemote().sendObject(message);

            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Error handler
     * @param session
     * @param t
     */
    @OnError
    public void error(Session session, Throwable t) {
       System.out.println("Shit happened");
       t.printStackTrace();
    }
}