package com.sinapsi.android.system;

import android.os.Build;
import com.sinapsi.engine.system.DeviceInfoAdapter;

/**
 * Android dipendent class, give model and name of the current device
 */
public class AndroidDeviceInfo implements DeviceInfoAdapter {
    private String model;
    private String product;
    private String version;
    private String type;

    /**
     * Default ctor
     */
    public AndroidDeviceInfo() {
        model = Build.MODEL;
        product = Build.PRODUCT;
        version = System.getProperty("os.version");
        type = "Android";
    }

    /**
     * Return os version
     * @return
     */
    public int getVersion() {
        return Integer.parseInt(version);
    }

    /**
     * Return the name od the device
     *
     * @return
     */
    @Override
    public String getDeviceName() {
        return product;
    }

    /**
     * Return the model of the device
     *
     * @return
     */
    @Override
    public String getDeviceModel() {
        return model;
    }

    /**
     * Return the type od the device
     *
     * @return
     */
    @Override
    public String getDeviceType() {
        return type;
    }
}
