package com.sinapsi.desktop.service;

import java.util.List;

import javax.print.attribute.standard.MediaSize.Engineering;

import com.google.gson.Gson;
import com.sinapsi.client.AppConsts;
import com.sinapsi.client.SyncManager;
import com.sinapsi.client.SyncManager.ConflictResolutionCallback;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.client.web.RetrofitWebServiceFacade;
import com.sinapsi.client.web.UserLoginStatusListener;
import com.sinapsi.client.web.SinapsiWebServiceFacade.WebServiceCallback;
import com.sinapsi.client.websocket.WSClient;
import com.sinapsi.desktop.enginesystem.DesktopActivationManager;
import com.sinapsi.desktop.log.DesktopClientLog;
import com.sinapsi.desktop.persistence.DesktopDiffDBManager;
import com.sinapsi.desktop.persistence.DesktopLocalDBManager;
import com.sinapsi.engine.ComponentFactory;
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
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.ComunicationInfo;
import com.sinapsi.webshared.ComponentFactoryProvider;
import com.sinapsi.webshared.wsproto.SinapsiMessageTypes;
import com.sinapsi.webshared.wsproto.WebSocketEventHandler;
import com.sinapsi.webshared.wsproto.WebSocketMessage;

public class BackgroundService implements Runnable, OnlineStatusProvider, WebSocketEventHandler, UserLoginStatusListener, ComponentFactoryProvider {

	private SinapsiLog sinapsiLog;
	private RetrofitWebServiceFacade web;
	private DeviceInterface device;
	private MacroEngine macroEngine;
	private SyncManager syncManager;

	public BackgroundService() {
		sinapsiLog = new SinapsiLog();
		sinapsiLog.addLogInterface(new SystemLogInterface() {

			@Override
			public void printMessage(LogMessage lm) {
				System.out.println(lm.getTag() + ": " + lm.getMessage());				
			}
		});

		web = new RetrofitWebServiceFacade(
				new DesktopClientLog(), 
				this, 
				this, 
				this, 
				this);
	}

	public void initEngine() {
		WebExecutionInterface exe = new WebExecutionInterface() {

			@Override
			public void continueExecutionOnDevice(ExecutionInterface ei,
					DeviceInterface di) {
				web.continueMacroOnDevice(device, di, new RemoteExecutionDescriptor(
						ei.getMacro().getId(), 
						ei.getLocalVars(), 
						ei.getExecutionStackIndexes()), 
						new WebServiceCallback<ComunicationInfo>() {

					@Override
					public void success(ComunicationInfo t, Object response) {
						sinapsiLog.log("EXECUTION_CONTINUE", t.getAdditionalInfo());
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
		macroEngine = new MacroEngine(device, 
				new DesktopActivationManager(
						new ExecutionInterface(
								sf, 
								device, 
								exe, 
								globalVariables, 
								sinapsiLog)), 
								sinapsiLog, 
								TriggerEngineStart.class, 
								ActionSetVariable.class, 
								ActionLog.class);


		syncManager = new SyncManager(web, 
				new DesktopLocalDBManager(), 
				new DesktopLocalDBManager(), 
				new DesktopDiffDBManager(), 
				device);

		if(AppConsts.DEBUG_CLEAR_DB_ON_START)
			syncManager.clearAll();



		macroEngine.startEngine();
	}

	public List<MacroInterface> loadSavedMacros() {
		return syncManager.getAllMacros();
	}

	public void syncAndLoadMacros(final boolean explicit) {
		if(isOnline()) {
			syncManager.sync(new SyncManager.MacroSyncCallback() {

				@Override
				public void onSyncSuccess(Integer pushed, Integer pulled,
						Integer noChanged, Integer resolvedConflicts) {
					macroEngine.clearMacros();
					macroEngine.addMacros(loadSavedMacros());					
				}

				@Override
				public void onSyncFailure(Throwable error) {
					// TODO Failure dialog
				}

				@Override
				public void onSyncConflicts(List<MacroSyncConflict> conflicts,
						ConflictResolutionCallback conflictCallback) {
					// TODO Sync Conflicts Dialog

				}
			});
		}
		else {
			macroEngine.clearMacros();
			macroEngine.addMacros(loadSavedMacros());
		}

	}

	public void handleWSMessage(String message, boolean firstCall) {
		Gson gson = new Gson();
		WebSocketMessage wsMessage = gson.fromJson(message, WebSocketMessage.class);
		
		switch(wsMessage.getMsgType()) {
			case SinapsiMessageTypes.REMOTE_EXECUTION_DESCRIPTOR: {
				RemoteExecutionDescriptor red = (RemoteExecutionDescriptor) wsMessage.getData();
				try {
					macroEngine.continueMacro(red);
				} catch(MacroEngine.MissingMacroException e) {
					if(firstCall) {
						syncAndLoadMacros(false);
						handleWSMessage(message, firstCall);
					} else 					
					e.printStackTrace();
				}
			} break;
			
			case SinapsiMessageTypes.MODEL_UPDATED_NOTIFICATION: {
				syncAndLoadMacros(false);
			} break;
			
			case SinapsiMessageTypes.NEW_CONNECTION: {
				
			} break;
			
			case SinapsiMessageTypes.CONNECTION_LOST: {
				
			} break;
		
		}
	}	
	
	public WSClient getWSClient() {
		return getWeb().getWebSocketClient();
	}

	@Override
	public void run() {
		while(true);
	}

	@Override
	public void onWebSocketOpen() {
		// TODO Log
	}

	@Override
	public void onWebSocketMessage(String message) {
		// TODO Log
		handleWSMessage(message, true);
	}

	@Override
	public void onWebSocketError(Exception ex) {
		// TODO Log
	}

	@Override
	public void onWebSocketClose(int code, String reason, boolean remote) {
		// TODO Log
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

	@Override
	public ComponentFactory getComponentFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onUserLogIn(UserInterface user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserLogOut() {
		// TODO Auto-generated method stub

	}


}
