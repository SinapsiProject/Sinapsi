package com.sinapsi.webservice.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * WebSocketServer implementation
 */
public class Server extends WebSocketServer {
    private Map<String, WebSocket> clients = Collections.synchronizedMap(new HashMap<String, WebSocket>());
    private Map<WebSocket, String> clientsWS = Collections.synchronizedMap(new HashMap<WebSocket, String>());
    
    /**
     * Default ctor
     * @param port port of server
     * @throws UnknownHostException
     */
    public Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    /**
     * Secondary ctor
     * @param address
     */
    public Server(InetSocketAddress address) {
        super(address);
    }

    /**
     * Method called when connection is established with a client
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.put(handshake.getFieldValue("Username"), conn);
        clientsWS.put(conn, handshake.getFieldValue("Username"));
        
        broadcast("new connection: " + handshake.getFieldValue("Username"));
        System.out.println(handshake.getFieldValue("Username") + " connected!");
    }

    /**
     * Method called when connection is closed
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(clientsWS.get(conn) + " disconnected!");
        System.out.println(clientsWS.get(conn) + " disconnected!");
        
        clients.remove(clientsWS.get(conn));
        clientsWS.remove(conn);      
    }

    /**
     * Method called on message recived
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        this.broadcast(message);
        System.out.println(conn + ": " + message );
    }

    /**
     * Method called on fragment recived
     */
    @Override
    public void onFragment(WebSocket conn, Framedata fragment) {
        System.out.println("received fragment: " + fragment);
    }

    /**
     * Start the server
     * @param port server port
     * @throws InterruptedException
     * @throws IOException
     */
    public void init() throws InterruptedException , IOException {
        WebSocketImpl.DEBUG = true;

        this.start();
        System.out.println("websocket server started on port: " + getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            String in = sysin.readLine();
            this.broadcast(in);
            if(in.equals("exit")) {
                this.stop();
                break;
            } else if( in.equals("restart")) {
                this.stop();
                this.start();
                break;
            }
        }
    }

    /**
     * On connection error
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if(conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.
     * 
     * @param text The String to send across the network.
     * @throws InterruptedException
     *            
     */
    public void broadcast(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for(WebSocket c : con) {
                c.send(text);
            }
        }
    }
    
    /**
     * Return the websocket client from the username
     * @param username username of the client
     * @return WebSocket
     */
    public WebSocket getClient(String username) {
        return clients.get(username);
    }
    
    /**
     * Send <var>msg</var> to a specific connected websocket client
     * @param c websocket client
     * @param msg message
     */
    public void send(WebSocket c, String msg) {
        c.send(msg);
    }
}