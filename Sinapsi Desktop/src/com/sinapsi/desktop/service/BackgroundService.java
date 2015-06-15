package com.sinapsi.desktop.service;

import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.client.web.RetrofitWebServiceFacade;
import com.sinapsi.client.web.RetrofitWebServiceFacade.LoginStatusListener;
import com.sinapsi.client.web.SinapsiWebServiceFacade.WebServiceCallback;
import com.sinapsi.desktop.enginesystem.DesktopActivationManager;
import com.sinapsi.desktop.log.DesktopClientLog;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.components.TriggerEngineStart;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.LogMessage;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.log.SystemLogInterface;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.wsproto.WebSocketEventHandler;

public class BackgroundService implements Runnable, OnlineStatusProvider, WebSocketEventHandler, LoginStatusListener {
	
	private SinapsiLog sinapsiLog;
	private RetrofitWebServiceFacade web;
	private DeviceInterface device;
	private MacroEngine macroEngine;
	
	public BackgroundService() {
		sinapsiLog = new SinapsiLog();
		sinapsiLog.addLogInterface(new SystemLogInterface() {
			
			@Override
			public void printMessage(LogMessage lm) {
				System.out.println(lm.getTag() + ": " + lm.getMessage());				
			}
		});
		
		web = new RetrofitWebServiceFacade(new DesktopClientLog(), this, this, this);
	}
	
	public void initEngine() {
		WebExecutionInterface exe = new WebExecutionInterface() {
			
			@Override
			public void continueExecutionOnDevice(ExecutionInterface ei,
					DeviceInterface di) {
					web.continueMacroOnDevice(device, di, new RemoteExecutionDescriptor(ei.getMacro().getId(), ei.getLocalVars(), ei.getExecutionStackIndexes()), new WebServiceCallback<String>() {
						
						@Override
						public void success(String t, Object response) {
							sinapsiLog.log("EXECUTION_CONTINUE", t);
						}
						
						@Override
						public void failure(Throwable error) {
							sinapsiLog.log("EXECUTION_CONTINUE", "FAIL");
						}
					});			
			}
		};
		
		SystemFacade sf = new SystemFacade(); // TODO fill
		VariableManager globalVariables = new VariableManager();
		macroEngine = new MacroEngine(device, new DesktopActivationManager(new ExecutionInterface(sf, device, exe, globalVariables, sinapsiLog)), sinapsiLog, TriggerEngineStart.class, ActionSetVariable.class, ActionLog.class);
		
		macroEngine.startEngine();
	}
	
	@Override
	public void run() {
		
	}

	@Override
	public void onLogIn(UserInterface user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLogOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWebSocketOpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWebSocketMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWebSocketError(Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWebSocketClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOnline() {
		// TODO Auto-generated method stub
		return false;
	}

	public DeviceInterface getDevice() {
		return device;
	}

	public void setDevice(DeviceInterface device) {
		this.device = device;
	}

	public RetrofitWebServiceFacade getWeb() {
		return web;
	}
	
	
}
