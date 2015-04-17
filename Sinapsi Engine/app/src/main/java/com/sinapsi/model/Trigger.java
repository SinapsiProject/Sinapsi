package com.sinapsi.model;

/**
 * Trigger interface
 *
 */
public interface Trigger extends Parameterized, DistributedComponent {
    public void onActivate(Event e, SystemFacade s);
}
