package com.sinapsi.engine.system;

import com.sinapsi.model.parameters.ConnectionStatusChoices;
import com.sinapsi.model.parameters.SwitchStatusChoices;

/**
 * Interface used to adapt various system calls
 * to get wifi infos or manage wifi status or connections.
 * TODO: documentation
 */
public interface WifiAdapter {

    public SwitchStatusChoices getStatus();
    public ConnectionStatusChoices getConnectionStatus();
    public String getSSID();
    public void connectToSSID(String id);
    public void setStatus(SwitchStatusChoices status);
    public void setConnectionStatus(ConnectionStatusChoices status);
}
