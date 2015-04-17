package com.sinapsi.model;

/**
 * DistributedComponent interface. Every component that can be
 * part of a distributed macro, should implement this interface.
 *
 */
public interface DistributedComponent extends MacroComponent {

    /**
     * Getter of the device where the component's job should
     * be done
     * @return the execution device
     */
    public DeviceInterface getExecutionDevice();
}
