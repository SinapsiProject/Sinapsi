package com.sinapsi.desktop.enginesystem;

import java.io.IOException;

import com.sinapsi.desktop.DesktopConsts;
import com.sinapsi.engine.system.DeviceInfoAdapter;

public class DesktopDeviceInfo implements DeviceInfoAdapter {

	@Override
	public String getDeviceName() {
		Runtime runTime = Runtime.getRuntime();
		try {
			Process p =	runTime.exec("hdparm -l /dev/sd? | grep 'Serial\\ Number'");
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		return null; 
	}

	@Override
	public String getDeviceModel() {
		// TODO Modello pc
		return null;
	}

	@Override
	public String getDeviceType() {
		return "PC Linux";
	}

	public int getVersion() {
		return DesktopConsts.CLIENT_VERSION;
	}
}
