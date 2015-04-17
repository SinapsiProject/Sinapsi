package com.sinapsi.model;

/**
 * Distributed component interface
 *
 */
public interface DistributedComponent extends MacroComponent {
    public DeviceInterface getExecutionDevice();
}
