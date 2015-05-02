package com.sinapsi.engine.execution;

import com.sinapsi.model.DeviceInterface;

/**
 * Interface to be implemented by every device in order to send requests to continue
 * execution of a macro on other devices.
 */
public interface WebExecutionInterface {
    public void continueExecutionOnDevice(ExecutionInterface ei, DeviceInterface di);
}
