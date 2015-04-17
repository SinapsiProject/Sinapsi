package com.sinapsi.model;

/**
 * Action interface
 * @author Ayoub
 *
 */
public interface Action extends Parameterized, DistributedComponent{
    public void active(SystemFacade s);
}
