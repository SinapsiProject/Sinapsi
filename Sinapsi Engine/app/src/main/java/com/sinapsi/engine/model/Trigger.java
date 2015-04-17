package com.sinapsi.engine.model;

/**
 * Trigger interface
 * @author Ayoub
 *
 */
public interface Trigger extends Parameterized, DistributedComponent {
    public void onActivate(Event e, SystemFacade s);
}
