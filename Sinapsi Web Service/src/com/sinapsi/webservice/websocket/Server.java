package com.sinapsi.webservice.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.sinapsi.webservice.engine.WebServiceGsonManager;
import com.sinapsi.webservice.engine.WebServiceLog;
import com.sinapsi.webshared.wsproto.SinapsiMessageTypes;
import com.sinapsi.webshared.wsproto.WebSocketMessage;

/**
 * WebSocketServer implementation
 */
public class Server extends WebSocketServer {
    private Map<String, WebSocket> clients = Collections.synchronizedMap(new HashMap<String, WebSocket>());
    private Map<WebSocket, String> clientsWS = Collections.synchronizedMap(new HashMap<WebSocket, String>());
    
    private Map<Integer, WebSocket> devices = Collections.synchronizedMap(new HashMap<Integer, WebSocket>());
    private Map<WebSocket, Integer> devicesWS = Collections.synchronizedMap(new HashMap<WebSocket, Integer>());
    
    private Map<String, Integer> clientDevices = Collections.synchronizedMap(new HashMap<String, Integer>());
    
    private WebServiceLog wslog = new WebServiceLog(WebServiceLog.WEBSOCKET_FILE_OUT);
    
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
        
        devices.put(Integer.parseInt(handshake.getFieldValue("device")), conn);
        devicesWS.put(conn, Integer.parseInt(handshake.getFieldValue("device")));
        
        clientDevices.put(handshake.getFieldValue("Username"), Integer.parseInt(handshake.getFieldValue("device")));
        
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();
        broadcast(gson.toJson(new WebSocketMessage(SinapsiMessageTypes.NEW_CONNECTION, "New connection: " + handshake.getFieldValue("Username"))));
        wslog.log(wslog.getTime(), handshake.getFieldValue("Username") + " connected!");
    }

    /**
     * Method called when connection is closed
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();
        broadcast(gson.toJson((new WebSocketMessage(SinapsiMessageTypes.CONNECTION_LOST, clientsWS.get(conn) + " disconnected!"))));
        wslog.log(wslog.getTime(), clientsWS.get(conn) + " disconnected!");
        
        clientDevices.remove(clientsWS.get(conn));
        
        devices.remove(devicesWS.get(conn));
        devicesWS.remove(conn);   
        
        clients.remove(clientsWS.get(conn));
        clientsWS.remove(conn);      
    }

    /**
     * Method called on message recived
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        this.broadcast(message);
        wslog.log(wslog.getTime(), conn + ": " + message );
    }

    /**
     * Method called on fragment recived
     */
    @Override
    public void onFragment(WebSocket conn, Framedata fragment) {
        wslog.log(wslog.getTime(), "received fragment: " + fragment);
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
        wslog.log(wslog.getTime(), "websocket server started on port: " + getPort());
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
        wslog.log(wslog.getTime(), "broadcast : " + text);
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for(WebSocket c : con) {
                if(c.isOpen())
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
     * Return the connected devices
     * @param username email of the user
     * @return List of devices id
     */
    public List<Integer> getDevicesOnline(String username) {
       List<Integer> devicesConnected = new ArrayList<Integer>();
       synchronized (devicesConnected) {
          for(Map.Entry<String, Integer> entry : clientDevices.entrySet()) {
             if(entry.getKey() == username)  
                devicesConnected.add(entry.getValue());
          }
       } 
       return devicesConnected;
    }
    
    /**
     * Return the connectivity of a device
     * @param id id of the device
     * @return boolean
     */
    public boolean isDeviceOnline(Integer id) {
       WebSocket device = devices.get(id);
       
       if(device != null)
          return devices.get(id).isOpen();
       else
          return false;
    }
    
    /**
     * Return a specific device
     * @param idDevice id of device
     * @return websocket connection object
     */
    public WebSocket getDevice(Integer idDevice) {
       return devices.get(idDevice);
    }
    
    /**
     * Send message to a device
     * @param idDevice id of the device
     * @param msg message
     */
    public void send(Integer idDevice, String msg) {
       if(devices.get(idDevice).isOpen()) {
          wslog.log(wslog.getTime(), "To : " + idDevice  + " MSG : " + msg);
          devices.get(idDevice).send(msg);
       }
    }
}