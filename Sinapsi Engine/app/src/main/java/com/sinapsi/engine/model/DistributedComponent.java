package com.sinapsi.engine.model;

/**
 * Distributed component interface
 * @author Ayoub
 *
 */
public interface DistributedComponent extends MacroComponent {
    public DeviceInterface getExecutionDevice();
}
