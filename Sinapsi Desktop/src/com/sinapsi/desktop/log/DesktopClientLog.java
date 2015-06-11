package com.sinapsi.desktop.log;

import retrofit.*;

public class DesktopClientLog implements RestAdapter.Log{

	@Override
	public void log(String arg0) {
		System.out.println(arg0);		
	}
	
}
