package com.sinapsi.engine.system;

import com.sinapsi.model.parameters.ConnectionStatusChoices;
import com.sinapsi.model.parameters.SwitchStatusChoices;

/**
 * Interface used to adapt various system calls
 * to get wifi infos or manage wifi status or connections.
 *
 */
public interface WifiAdapter {

    /**
     * Getter of the main wifi adapter's status.
     * @return ENABLED, DISABLED, ENABLING or DISABLING
     */
    public SwitchStatusChoices getStatus();

    /**
     * Getter of the main wifi connection's status.
     * @return CONNECTED, DISCONNECTED, DISCONNECTING or CONNECTING
     */
    public ConnectionStatusChoices getConnectionStatus();

    /**
     * Gets the SSID of the connected wifi network
     * @return the SSID
     */
    public String getSSID();

    /**
     * Attempts to connect to the given SSID
     * @param id the SSID
     */
    public void connectToSSID(String id);

    /**
     * Sets the main wifi adapter's status
     * @param status true for enable, false for disable.
     */
    public void setStatus(boolean status);

    /**
     * Sets the main wifi connection's status.
     * @param status true for connect, false for disconnect.
     */
    public void setConnectionStatus(boolean status);
}
