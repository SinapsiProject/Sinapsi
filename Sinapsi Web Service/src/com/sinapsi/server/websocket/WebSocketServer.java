package com.sinapsi.server.websocket;

import java.io.IOException;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.MessageHandler;

public class WebSocketServer extends Endpoint {

    @Override
    public void onOpen(Session session, EndpointConfig endPointConfig) {
        RemoteEndpoint.Basic remoteEPbasic = session.getBasicRemote();
        session.addMessageHandler(new CommandHandler(remoteEPbasic));
    }
    
    private static class CommandHandler implements MessageHandler.Whole<String> {

        private final RemoteEndpoint.Basic remoteEndPointBasic;
        
        public CommandHandler(RemoteEndpoint.Basic remoteEPbasic) {
           this.remoteEndPointBasic = remoteEPbasic;
        }

        @Override
        public void onMessage(String message) {
            try {
                if(remoteEndPointBasic != null) {
                    remoteEndPointBasic.sendText(message);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
    }

}
