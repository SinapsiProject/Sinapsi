package com.sinapsi.model;

/**
 * Action interface
 *
 */
public interface Action extends Parameterized, DistributedComponent{
    public void active(SystemFacade s);
}
