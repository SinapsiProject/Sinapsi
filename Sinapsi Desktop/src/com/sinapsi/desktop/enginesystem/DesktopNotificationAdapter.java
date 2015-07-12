package com.sinapsi.desktop.enginesystem;

import java.io.IOException;
import com.sinapsi.engine.system.NotificationAdapter;

public class DesktopNotificationAdapter implements NotificationAdapter {

	@Override
	public void showSimpleNotification(String title, String message) {
		try {
		Process p = Runtime.getRuntime().exec(new String[] {"notify-send",title,message});
		p.waitFor();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
