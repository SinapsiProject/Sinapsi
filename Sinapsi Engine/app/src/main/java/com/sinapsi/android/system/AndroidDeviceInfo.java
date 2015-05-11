package com.sinapsi.android.system;

import android.os.Build;

import com.sinapsi.engine.system.DeviceInfoAdapter;

/**
 * Android dependent class, give model and name of the current device
 */
public class AndroidDeviceInfo implements DeviceInfoAdapter {
    /**
     * Return os version
     *
     * @return
     */
    public int getVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Return the name od the device
     *
     * @return
     */
    @Override
    public String getDeviceName() {
        return Build.PRODUCT;
    }

    /**
     * Return the model of the device
     *
     * @return
     */
    @Override
    public String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * Return the type od the device
     *
     * @return
     */
    @Override
    public String getDeviceType() {
        return "Android";
    }
}
