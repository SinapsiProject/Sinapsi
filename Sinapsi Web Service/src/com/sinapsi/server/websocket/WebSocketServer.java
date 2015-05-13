package com.sinapsi.server.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.OnMessage;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/websocket", encoders={MessageEncoder.class}, decoders={MessageDecoder.class})
public class WebSocketServer {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    
    /**
     * Send broadcast message to all connected clients
     * @param message message to send
     */
    public static void send(Message message) {
        for(Session client : clients) {
            if(client.isOpen()) {
                try {
                    client.getBasicRemote().sendObject(message);
                } catch (IOException| EncodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Send message to a specific device
     * @param message message to send
     * @param idDevice id of device target
     */
    public static void send(Message message, int idDevice) {
        for(Session client : clients) {
            if(client.getUserProperties().get("id_device").equals(idDevice)) {
                try {
                    client.getBasicRemote().sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Intercept message sent from connected clients
     * @param message Message object
     * @param session Session object
     */
    @OnMessage
    public void onMessage(Message message, Session session) {
        // parse recived message
        JsonObject jsonMsg = message.getJson();
        String idDeviceTarget = jsonMsg.getString("to");
        boolean messageSent = false;
        
        // send the remote execution descritpion 
            // Iterate over the connected sessions
            for(Session client : clients) {
                // the message contain a remote macro execution 
                if(client.isOpen() && message.getType().equals(Message.REMOTE_MACRO_TYPE)) {
                    
                    if(client.getId().equals(idDeviceTarget)) {
                        try {
                            // send the remote macro execution object to the target device
                            client.getBasicRemote().sendObject(message);
                            messageSent = true;
                        } catch(IOException | EncodeException e) {
                            e.printStackTrace();
                            System.out.println("Failed to sent message");
                        }
                    }
                }
            }  
            //DEBUG
            System.out.println("Remote execution from: " + jsonMsg.getString("from") + " to: " + jsonMsg.getString("to")); 
        
        //DEBUG
        if(messageSent == false) {
            System.out.println("Message not sent to device target");
        }
    }
    
    /**
     * Allows us to intercept the creation of a new session.
     * Session class allows us to send data to users
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id_device") String idDevice) {
        // Add session to the connected sessions set
        session.getUserProperties().put("id_device", idDevice);
        clients.add(session);
        
        
        Message message = new Message(Json.createObjectBuilder()
                                .add("type", Message.TEXT_TYPE)
                                .add("data", session.getId() + " has joined Sinapsi")
                                .add("id", session.getId())
                                .build());
      
        //Broadcast send: notify to all client connected that has joined a new device
        for (Session s : clients) {
            try {
                s.getBasicRemote().sendObject(message);
                
            } catch(IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
        
        //internal debug
        System.out.println(session.getId() + " has joined");
      
    }

    @OnClose
    public void onClose (Session session) {
      // Remove session from the connected sessions set
      clients.remove(session);
    }
}