package com.sinapsi.webservice.websocket;

import java.io.IOException;
import java.net.UnknownHostException;

public class WSServerThread implements Runnable {
    private volatile boolean running = true;
    private Server wsserver;

    /**
     * Default ctor
     * @param port
     * @throws UnknownHostException 
     */
    public WSServerThread(int port) throws UnknownHostException {
        wsserver = new Server(port);
    }
    
    /**
     * Terminate thread
     */
    public void terminate() {
        running = false;
    }
    
    /**
     * Run thread
     */
    @Override
    public void run() {
        while (running) {
            try {
                // init the server
                wsserver.init();
            } catch (InterruptedException | IOException  e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
    
    /**
     * Return the server object
     * @return
     */
    public Server getServer() {
        return wsserver;
    }

}
