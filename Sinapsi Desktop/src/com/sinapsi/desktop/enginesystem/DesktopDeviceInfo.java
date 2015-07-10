package com.sinapsi.desktop.enginesystem;

import com.sinapsi.desktop.DesktopConsts;
import com.sinapsi.desktop.controller.RootAccess;
import com.sinapsi.engine.system.DeviceInfoAdapter;

public class DesktopDeviceInfo implements DeviceInfoAdapter {

	private String rootPsw;

	public DesktopDeviceInfo(String rootPsw) {
		this.rootPsw = rootPsw;
	}

	@Override
	public String getDeviceName() {
		// execute command with root privilege
		String out = null;
		try {
			Process p = RootAccess.runFromRoot("hdparm -I /dev/sd? | grep 'Serial\\ Number'", rootPsw);
			out = RootAccess.streamToString(p.getInputStream());
			out = out.substring(out.indexOf(":") + 1, out.indexOf("\n"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	@Override
	public String getDeviceModel() {
		// execute command with root privilege
		String out = null;
		try {
			Process p = RootAccess.runFromRoot("dmidecode | grep \"Manufacturer\"", rootPsw);
			out = RootAccess.streamToString(p.getInputStream());
			out = out.substring(out.indexOf(":") + 1, out.indexOf("\n"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	@Override
	public String getDeviceType() {
		return "PC Linux";
	}

	public int getVersion() {
		return DesktopConsts.CLIENT_VERSION;
	}
}