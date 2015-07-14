package com.sinapsi.desktop.service;

import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.MediaSize.Engineering;

import com.google.gson.Gson;
import com.sinapsi.client.AppConsts;
import com.sinapsi.client.SafeSyncManager;
import com.sinapsi.client.SyncManager;
import com.sinapsi.client.SyncManager.ConflictResolutionCallback;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.client.web.RetrofitWebServiceFacade;
import com.sinapsi.client.web.UserLoginStatusListener;
import com.sinapsi.client.web.SinapsiWebServiceFacade.WebServiceCallback;
import com.sinapsi.client.websocket.WSClient;
import com.sinapsi.desktop.enginesystem.DesktopActivationManager;
import com.sinapsi.desktop.enginesystem.DesktopDeviceInfo;
import com.sinapsi.desktop.enginesystem.DesktopNotificationAdapter;
import com.sinapsi.desktop.log.DesktopClientLog;
import com.sinapsi.desktop.persistence.DesktopDiffDBManager;
import com.sinapsi.desktop.persistence.DesktopLocalDBManager;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.components.ActionSimpleNotification;
import com.sinapsi.engine.components.TriggerEngineStart;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.LogMessage;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.log.SystemLogInterface;
import com.sinapsi.engine.system.NotificationAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.CommunicationInfo;
import com.sinapsi.utils.Pair;
import com.sinapsi.webshared.ComponentFactoryProvider;
import com.sinapsi.webshared.wsproto.SinapsiMessageTypes;
import com.sinapsi.webshared.wsproto.WebSocketEventHandler;
import com.sinapsi.webshared.wsproto.WebSocketMessage;

public class BackgroundService implements Runnable, OnlineStatusProvider, WebSocketEventHandler, UserLoginStatusListener, ComponentFactoryProvider {

	private SinapsiLog sinapsiLog;
	private RetrofitWebServiceFacade web;
	private DeviceInterface device;
	private MacroEngine macroEngine;
	private SafeSyncManager safeSyncManager;
	private DesktopDeviceInfo deviceInfo;
	private String rootPasswd;
	
	public MacroEngine getEngine() {
		return this.macroEngine;
	}

	public BackgroundService(String rootPasswd) {
		this.rootPasswd = rootPasswd;
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

		deviceInfo = new DesktopDeviceInfo(rootPasswd);
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
						new WebServiceCallback<CommunicationInfo>() {

					@Override
					public void success(CommunicationInfo t, Object response) {
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
		sf.addSystemService(NotificationAdapter.SERVICE_NOTIFICATION, new DesktopNotificationAdapter());
		sf.setRequirementSpec(NotificationAdapter.REQUIREMENT_SIMPLE_NOTIFICATIONS, true);
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
								ActionLog.class,
								ActionSimpleNotification.class);


		SyncManager syncManager = new SyncManager(web, 
				new DesktopLocalDBManager(), 
				new DesktopLocalDBManager(), 
				new DesktopDiffDBManager(), 
				device);

		safeSyncManager = new SafeSyncManager(syncManager, this);

		if(AppConsts.DEBUG_CLEAR_DB_ON_START)
			syncManager.clearAll();



		macroEngine.startEngine();
		
		//TODO: eliminare questo dopo aver fatto i db manager
		getWeb().getAllMacros(device, new WebServiceCallback<Pair<Boolean,List<MacroInterface>>>() {

			@Override
			public void success(Pair<Boolean, List<MacroInterface>> t,
					Object response) {
				macroEngine.clearMacros();
				macroEngine.addMacros(t.getSecond());
				
			}

			@Override
			public void failure(Throwable error) {
				System.out.println("Error");				
			}
		});
	}

	public List<MacroInterface> getMacros() {
		return new ArrayList<>(macroEngine.getMacros().values());
	}

	public void handleWSMessage(String message, boolean firstCall) {
		Gson gson = new Gson();
		WebSocketMessage wsMessage = gson.fromJson(message, WebSocketMessage.class);

		switch(wsMessage.getMsgType()) {
		case SinapsiMessageTypes.REMOTE_EXECUTION_DESCRIPTOR: {
			RemoteExecutionDescriptor red = gson.fromJson(wsMessage.getData(), RemoteExecutionDescriptor.class);
			try {
				macroEngine.continueMacro(red);
			} catch(MacroEngine.MissingMacroException e) {
				if(firstCall) {
					syncMacros(new BackgroundSyncCallback() {

						@Override
						public void onBackgroundSyncSuccess(List<MacroInterface> currentMacros) {
							handleWSMessage(message, false);							
						}

						@Override
						public void onBackgroundSyncFail(Throwable error) {
							// TODO Auto-generated method stub
						}

					}, false);

				} else 					
					e.printStackTrace();
			}
		} break;

		case SinapsiMessageTypes.MODEL_UPDATED_NOTIFICATION: {
			//TODO: eliminare questo dopo aver fatto i db manager
			getWeb().getAllMacros(device, new WebServiceCallback<Pair<Boolean,List<MacroInterface>>>() {

				@Override
				public void success(Pair<Boolean, List<MacroInterface>> t,
						Object response) {
					macroEngine.clearMacros();
					macroEngine.addMacros(t.getSecond());
					
				}

				@Override
				public void failure(Throwable error) {
					System.out.println("Error");				
				}
			});
			/*syncMacros(new BackgroundSyncCallback() {

				@Override
				public void onBackgroundSyncSuccess(List<MacroInterface> currentMacros) {
					// Do nothing					
				}

				@Override
				public void onBackgroundSyncFail(Throwable error) {
					//Do nothing					
				}
			},false);*/
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
		// TODO device is online
		return true;
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
		return macroEngine.getComponentFactory();
	}

	@Override
	public void onUserLogIn(UserInterface user) {
		// TODO Auto-generated method stub

	}

	public static interface BackgroundSyncCallback {
		public void onBackgroundSyncSuccess(List<MacroInterface> currentMacros);

		public void onBackgroundSyncFail(Throwable error);
	}

	private class BackgroundServiceInternalSyncCallback implements SyncManager.MacroSyncCallback {

		private final BackgroundSyncCallback callback;
		private final boolean userIntention;

		public BackgroundServiceInternalSyncCallback(BackgroundSyncCallback callback, boolean userIntention) {
			this.callback = callback;
			this.userIntention = userIntention;
		}

		@Override
		public void onSyncSuccess(List<MacroInterface> currentMacros) {
			macroEngine.clearMacros();
			macroEngine.addMacros(currentMacros);
			callback.onBackgroundSyncSuccess(currentMacros);
		}

		@Override
		public void onSyncConflicts(List<MacroSyncConflict> conflicts, SyncManager.ConflictResolutionCallback conflictCallback) {
			handleConflicts(conflicts, conflictCallback);
		}

		@Override
		public void onSyncFailure(Throwable error) {
			handleSyncFailure(error, userIntention);
			callback.onBackgroundSyncFail(error);
		}
	}

	public void handleConflicts(List<MacroSyncConflict> conflicts, SyncManager.ConflictResolutionCallback callback) {

	}

	public void handleSyncFailure(Throwable e, boolean showError) {

	}

	@Override
	public void onUserLogOut() {
		// TODO Auto-generated method stub

	}

	public void syncMacros(final BackgroundSyncCallback callback, final boolean userIntention) {
		safeSyncManager.getMacros(new BackgroundServiceInternalSyncCallback(callback, userIntention));
	}

	public void removeMacro(int id, final BackgroundSyncCallback callback, final boolean userIntention) {
		safeSyncManager.removeMacro(id, new BackgroundServiceInternalSyncCallback(callback, userIntention));
	}

	public void updateMacro(MacroInterface macro, final BackgroundSyncCallback callback, final boolean userIntention) {
		safeSyncManager.updateMacro(macro, new BackgroundServiceInternalSyncCallback(callback, userIntention));
	}

	public void addMacro(MacroInterface macro, final BackgroundSyncCallback callback, final boolean userIntention) {
		safeSyncManager.addMacro(macro, new BackgroundServiceInternalSyncCallback(callback, userIntention));
	}

	public DesktopDeviceInfo getDeviceInfo() {

		return deviceInfo;
	}


}
