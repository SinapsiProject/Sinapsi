package com.sinapsi.client.web.gson;

import com.google.gson.InstanceCreator;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;

import java.lang.reflect.Type;

/**
 * Created by Giuseppe on 25/05/15.
 */
public class DeviceInterfaceInstanceCreator implements InstanceCreator<DeviceInterface> {

    private final FactoryModelInterface factory;

    public DeviceInterfaceInstanceCreator(FactoryModelInterface factory){
        this.factory = factory;
    }

    @Override
    public DeviceInterface createInstance(Type type) {
        return factory.newDevice(-2, null, null, null, null, -1);
    }
}
