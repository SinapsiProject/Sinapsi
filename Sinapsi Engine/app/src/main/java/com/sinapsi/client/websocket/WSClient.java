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

    /**
     * Defaut Ctor.
     * @throws URISyntaxException
     */
    public WSClient() throws URISyntaxException {
        super(new URI(AppConsts.SINAPSI_WS_URL));
    }

    /**
     * Secondary Ctor
     * @param serverURI uri of the
     */
    public WSClient(URI serverURI) {
        super(serverURI);
    }

    /**
     * Method called when the connection to web socket server is established
     * @param handshakedata
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection open :" + this.getURI());
    }

    @Override
    public void onMessage(String message) {
        //TODO: ?
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void establishConnection() {
        this.connect();
    }

    public void closeConnection() {
        this.close();
    }

}
