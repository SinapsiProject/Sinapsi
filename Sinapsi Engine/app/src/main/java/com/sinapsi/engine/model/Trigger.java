package com.sinapsi.engine.model;

/**
 * Trigger interface
 * @author Ayoub
 *
 */
public interface Trigger {
    public void onActivate(Event e, SystemFacade s);
}
