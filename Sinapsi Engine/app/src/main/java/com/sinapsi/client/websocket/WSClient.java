package com.sinapsi.client.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import com.sinapsi.client.AppConsts;
/**
 * Web Socket Client
 */
public class WSClient extends WebSocketClient {
    private String username;
    
    /**
     * Default Ctor
     */
    public WSClient() throws URISyntaxException {
        super(new URI(AppConsts.SINAPSI_WS_URL));
    }
    
    /**
     * Secondary Ctor.
     * @throws URISyntaxException
     */
    public WSClient(String username) throws URISyntaxException {
        super(new URI(AppConsts.SINAPSI_WS_URL), username);
        this.username = username;
    }

    /**
     * Secondary Ctor
     * @param serverURI uri of the
     */
    public WSClient(URI serverURI, String username) {
        super(serverURI, username);
        this.username = username;
    }


    /**
     * Method called when the connection to web socket server is established
     * @param handshakedata
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection open :" + this.getURI());
    }

    /**
     * Method called whene messagge is recived
     */
    @Override
    public void onMessage(String message) {
        //Override this to handle messages
    }

    /**
     * Method called when the connetion closed
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason);
    }

    /**
     * Method called on comunication error
     */
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Establish connection with the server websocket
     */
    public void establishConnection() {
        this.connect();
    }

    /**
     * Close connection with the server websocket
     */
    public void closeConnection() {
        this.close();
    }

    /**
     * Return the username of the client
     * @return
     */
    public String getusername() {
        return this.username;
    }

    @Override
    public void setDeviceId(int id) {
        super.setDeviceId(id);
    }
}
