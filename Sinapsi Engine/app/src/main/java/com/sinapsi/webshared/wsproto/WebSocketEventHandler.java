package com.sinapsi.webshared.wsproto;

/**
 * Created by Giuseppe on 11/06/15.
 */
public interface WebSocketEventHandler {

    public void onWebSocketOpen();
    public void onWebSocketMessage(String message);
    public void onWebSocketError(Exception ex);
    public void onWebSocketClose(int code, String reason, boolean remote);

}
