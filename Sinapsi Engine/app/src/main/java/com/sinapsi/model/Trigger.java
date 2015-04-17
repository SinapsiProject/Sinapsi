package com.sinapsi.model;

/**
 * Trigger interface
 * @author Ayoub
 *
 */
public interface Trigger extends Parameterized, DistributedComponent {
    public void onActivate(Event e, SystemFacade s);
}
