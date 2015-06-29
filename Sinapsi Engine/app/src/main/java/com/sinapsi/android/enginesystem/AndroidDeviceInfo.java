package com.sinapsi.android.enginesystem;

import android.content.Context;
import android.os.Build;

import com.sinapsi.android.AndroidAppConsts;
import com.sinapsi.engine.system.DeviceInfoAdapter;

/**
 * Android dependent class, give model and name of the current device
 */
public class AndroidDeviceInfo implements DeviceInfoAdapter {

    private Context context;

    public AndroidDeviceInfo(Context c){
        this.context = c;
    }

    /**
     * Return client version
     *
     * @return
     */
    public int getVersion() {
        return AndroidAppConsts.CLIENT_VERSION;
    }

    /**
     * Return the name od the device
     *
     * @return
     */
    @Override
    public String getDeviceName() {
        return InstallationUUIDManager.id(context);
    }

    /**
     * Return the model of the device
     *
     * @return
     */
    @Override
    public String getDeviceModel() {
        return Build.MODEL + " " + Build.PRODUCT;
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
