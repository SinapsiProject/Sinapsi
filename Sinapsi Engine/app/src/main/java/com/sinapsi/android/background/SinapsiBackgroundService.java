package com.sinapsi.android.background;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.sinapsi.android.AndroidAppConsts;
import com.sinapsi.android.SinapsiAndroidApplication;
import com.sinapsi.android.enginesystem.ToastAdapter;
import com.sinapsi.android.enginesystem.components.ActionToast;
import com.sinapsi.android.persistence.AndroidDiffDBManager;
import com.sinapsi.android.persistence.AndroidLocalDBManager;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.android.view.MainActivity;
import com.sinapsi.android.web.AndroidBase64DecodingMethod;
import com.sinapsi.android.web.AndroidBase64EncodingMethod;
import com.sinapsi.client.AppConsts;
import com.sinapsi.android.Lol;
import com.sinapsi.android.persistence.AndroidUserSettingsFacade;
import com.sinapsi.android.enginesystem.AndroidNotificationAdapter;
import com.sinapsi.client.SafeSyncManager;
import com.sinapsi.client.SyncManager;
import com.sinapsi.client.persistence.InconsistentMacroChangeException;
import com.sinapsi.client.persistence.UserSettingsFacade;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.utils.Triplet;
import com.sinapsi.webshared.ComponentFactoryProvider;
import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.client.web.RetrofitWebServiceFacade;
import com.sinapsi.android.enginesystem.AndroidActivationManager;
import com.sinapsi.android.enginesystem.AndroidDialogAdapter;
import com.sinapsi.android.enginesystem.AndroidSMSAdapter;
import com.sinapsi.android.enginesystem.AndroidWifiAdapter;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.client.web.UserLoginStatusListener;
import com.sinapsi.client.websocket.WSClient;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.android.R;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.components.ActionContinueConfirmDialog;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSendSMS;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.components.ActionSimpleNotification;
import com.sinapsi.engine.components.ActionStringInputDialog;
import com.sinapsi.engine.components.ActionWifiState;
import com.sinapsi.engine.components.TriggerACPower;
import com.sinapsi.engine.components.TriggerEngineStart;
import com.sinapsi.engine.components.TriggerSMS;
import com.sinapsi.engine.components.TriggerScreenPower;
import com.sinapsi.engine.components.TriggerWifi;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.LogMessage;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.log.SystemLogInterface;
import com.sinapsi.engine.parameters.ConnectionStatusChoices;
import com.sinapsi.engine.system.CommonDeviceConsts;
import com.sinapsi.engine.system.DialogAdapter;
import com.sinapsi.engine.system.NotificationAdapter;
import com.sinapsi.engine.system.SMSAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.CommunicationInfo;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.webshared.wsproto.SinapsiMessageTypes;
import com.sinapsi.webshared.wsproto.WebSocketEventHandler;
import com.sinapsi.webshared.wsproto.WebSocketMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.android.AndroidLog;

/**
 * Sinapsi background service on android platform.
 * This should be started in foreground notification mode
 * in order to remain running on the system. The engine is initialized
 * here and
 */
public class SinapsiBackgroundService extends Service
        implements
        OnlineStatusProvider,
        WebSocketEventHandler,
        UserLoginStatusListener,
        ComponentFactoryProvider {

    private RetrofitWebServiceFacade web;
    private SafeSyncManager safeSyncManager;
    private UserSettingsFacade settings;

    private MacroEngine engine;
    private SinapsiLog sinapsiLog;
    private DeviceInterface device;

    private static FactoryModel fm = new FactoryModel();

    private Map<String, WebServiceConnectionListener> connectionListeners = new HashMap<>();

    private boolean started = false;
    private boolean onlineMode = false;
    private static final UserInterface logoutUser = fm.newUser(-1, "Not logged in yet.", "", false, "user");
    private UserInterface loggedUser = logoutUser;

    private REDHandler REDHandler;



    @Override
    public void onCreate() {
        super.onCreate();

        REDHandler = new REDHandler(Looper.getMainLooper());

        // loading settings from shared preferences -----------------
        settings = new AndroidUserSettingsFacade(AppConsts.PREFS_FILE_NAME, this);
        //loadSettings(settings);


        // initializing sinapsi log ---------------------------------
        sinapsiLog = new SinapsiLog();
        sinapsiLog.addLogInterface(new SystemLogInterface() {
            @Override
            public void printMessage(LogMessage lm) {
                Lol.d(lm.getTag(), lm.getMessage());
            }
        });


        // web service initialization -------------------------------
        web = new RetrofitWebServiceFacade(
                new AndroidLog("RETROFIT"),
                this,
                this,
                this,
                this,
                new AndroidBase64EncodingMethod(),
                new AndroidBase64DecodingMethod());

        // inits the engine with mock data for debug
        if (AppConsts.DEBUG_BYPASS_LOGIN) mockLogin();

    }

    public void initEngine() {
        // initializing web execution interface ---------------------
        WebExecutionInterface defaultWebExecutionInterface = new WebExecutionInterface() {
            @Override
            public void continueExecutionOnDevice(ExecutionInterface ei, DeviceInterface di) {
                web.continueMacroOnDevice(
                        device,
                        di,
                        new RemoteExecutionDescriptor(
                                ei.getMacro().getId(),
                                ei.getLocalVars(),
                                ei.getExecutionStackIndexes()),
                        new SinapsiWebServiceFacade.WebServiceCallback<CommunicationInfo>() {

                            @Override
                            public void success(CommunicationInfo s, Object response) {
                                sinapsiLog.log("EXECUTION_CONTINUE", s.getAdditionalInfo());
                            }

                            @Override
                            public void failure(Throwable error) {
                                sinapsiLog.log("EXECUTION_CONTINUE", "FAIL");
                            }
                        });
            }
        };

        // here starts engine initialization    ---------------------
        SystemFacade sf = createAndroidSystemFacade();
        VariableManager globalVarables = new VariableManager();


        engine = new MacroEngine(
                device,
                new AndroidActivationManager(
                        new ExecutionInterface(
                                sf,
                                device,
                                defaultWebExecutionInterface,
                                globalVarables,
                                sinapsiLog),
                        this,
                        sf),
                sinapsiLog,
                TriggerSMS.class,
                TriggerWifi.class,
                TriggerEngineStart.class,
                TriggerScreenPower.class,
                TriggerACPower.class,

                ActionWifiState.class,
                ActionSendSMS.class,
                ActionSetVariable.class,
                ActionContinueConfirmDialog.class,
                ActionLog.class,
                ActionSimpleNotification.class,
                ActionStringInputDialog.class,
                ActionToast.class);
        // here ends engine initialization      ---------------------

        // sync manager initialization ------------------------------

        AndroidLocalDBManager aldbm_old;
        AndroidLocalDBManager aldbm_curr;
        AndroidDiffDBManager addbm;

        if (loggedUser.getId() == logoutUser.getId()) {
            aldbm_old = new AndroidLocalDBManager(getApplicationContext(), "logout_user-lastSync.db", engine.getComponentFactory());
            aldbm_curr = new AndroidLocalDBManager(getApplicationContext(), "logout_user-current.db", engine.getComponentFactory());
            addbm = new AndroidDiffDBManager(getApplicationContext(), "logout_user-diff.db");
            Log.d("DBLOCATION", getApplicationContext().getDatabasePath("logout_user-lastSync.db").getAbsolutePath());
            Log.d("DBLOCATION", getApplicationContext().getDatabasePath("logout_user-lastSync.db").getAbsolutePath());
            Log.d("DBLOCATION", getApplicationContext().getDatabasePath("logout_user-lastSync.db").getAbsolutePath());
            Log.d("DBLOCATION", getApplicationContext().getDatabasePath("logout_user-lastSync.db").getAbsolutePath());
            Log.d("DBLOCATION", getApplicationContext().getDatabasePath("logout_user-lastSync.db").getAbsolutePath());
        } else {
            aldbm_old = new AndroidLocalDBManager(getApplicationContext(), loggedUser.getEmail().replace('@', '_').replace('.', '_') + "-lastSync.db", engine.getComponentFactory());
            aldbm_curr = new AndroidLocalDBManager(getApplicationContext(), loggedUser.getEmail().replace('@', '_').replace('.', '_') + "-current.db", engine.getComponentFactory());
            addbm = new AndroidDiffDBManager(getApplicationContext(), loggedUser.getEmail().replace('@', '_').replace('.', '_') + "-diff.db");
        }


        SyncManager syncManager = new SyncManager(
                web,
                aldbm_old,
                aldbm_curr,
                addbm,
                device

        );

        safeSyncManager = new SafeSyncManager(syncManager, this);

        if (AppConsts.DEBUG_CLEAR_DB_ON_START) syncManager.clearAll();

        if (AppConsts.DEBUG_TEST_MACROS) createLocalMacroExamples();


    }

    public void syncMacrosAndStartEngine(){
        // loads macros from local db/web service -------------------
        syncMacros(new BackgroundSyncCallback() {
            @Override
            public void onBackgroundSyncSuccess(List<MacroInterface> currentMacros) {
                engine.startEngine();
            }

            @Override
            public void onBackgroundSyncFail(Throwable error) {
                //try to start the engine anyway
                engine.startEngine();
            }
        }, false);


        startForegroundMode();
    }

    private void loadSettings(UserSettingsFacade settings) {
        //TODO: impl
    }

    /**
     * Initializes a SystemFacade instance for the Android platform
     *
     * @return a new SystemFacade instance
     */
    private SystemFacade createAndroidSystemFacade() {
        SystemFacade sf = new SystemFacade();

        sf.addSystemService(DialogAdapter.SERVICE_DIALOGS, new AndroidDialogAdapter(this));
        sf.addSystemService(SMSAdapter.SERVICE_SMS, new AndroidSMSAdapter(this));
        sf.addSystemService(WifiAdapter.SERVICE_WIFI, new AndroidWifiAdapter(this));
        sf.addSystemService(NotificationAdapter.SERVICE_NOTIFICATION, new AndroidNotificationAdapter(getApplicationContext()));
        sf.addSystemService(ToastAdapter.SERVICE_TOAST, new ToastAdapter(this));

        PackageManager pm = getPackageManager();

        sf.setRequirementSpec(DialogAdapter.REQUIREMENT_SIMPLE_DIALOGS, true);
        sf.setRequirementSpec(NotificationAdapter.REQUIREMENT_SIMPLE_NOTIFICATIONS, true);
        sf.setRequirementSpec(CommonDeviceConsts.REQUIREMENT_INTERCEPT_SCREEN_POWER, true);
        sf.setRequirementSpec(CommonDeviceConsts.REQUIREMENT_AC_CHARGER, true);
        sf.setRequirementSpec(DialogAdapter.REQUIREMENT_INPUT_DIALOGS, true);

        sf.setRequirementSpec(WifiAdapter.REQUIREMENT_WIFI, pm.hasSystemFeature(PackageManager.FEATURE_WIFI));
        sf.setRequirementSpec(SMSAdapter.REQUIREMENT_SMS_READ, pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY));

        sf.setRequirementSpec(ToastAdapter.REQUIREMENT_TOAST, true);


        return sf;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        started = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SinapsiServiceBinder();
    }


    public List<MacroInterface> getMacros() {
        return new ArrayList<>(engine.getMacros().values());
    }

    /**
     * Adds a web service connection listener to the notification set.
     * From now on, when the online/offline mode changes, the specified listener
     * is notified.
     *
     * @param wscl the connection listener
     */
    public void addWebServiceConnectionListener(WebServiceConnectionListener wscl) {
        connectionListeners.put(wscl.getClass().getName(), wscl);
    }

    /**
     * Removes a web service connection listener to from the notification set.
     *
     * @param wscl the connection listener
     */
    public void removeWebServiceConnectionListener(WebServiceConnectionListener wscl) {
        connectionListeners.remove(wscl.getClass().getName());
    }

    private void notifyWebServiceConnectionListeners(boolean online) {
        for (WebServiceConnectionListener wscl : connectionListeners.values()) {
            if (online) wscl.onOnlineMode();
            else wscl.onOfflineMode();
        }
    }


    /**
     * Online status getter.
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean tmpOnlineVal = netInfo != null && netInfo.isConnectedOrConnecting();
        if (onlineMode != tmpOnlineVal) notifyWebServiceConnectionListeners(tmpOnlineVal);
        onlineMode = tmpOnlineVal;
        return tmpOnlineVal;
    }

    @Override
    public void onWebSocketOpen() {
        Lol.d("WEB_SOCKET", "WebSocket Open");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Lol.d("WEB_SOCKET", "WebSocket message received: '" + message + "'");
        handleWsMessage(message, true);

    }

    @Override
    public void onWebSocketError(Exception ex) {
        Lol.d("WEB_SOCKET", "WebSocket Error: " + ex.getMessage());
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Lol.d("WEB_SOCKET", "WebSocket Close: " + code + ":" + reason);
    }

    private void handleWsMessage(final String message, boolean firstcall) {

        Gson gson = new Gson();
        WebSocketMessage wsMsg = gson.fromJson(message, WebSocketMessage.class);
        switch (wsMsg.getMsgType()) {
            case SinapsiMessageTypes.REMOTE_EXECUTION_DESCRIPTOR: {
                final RemoteExecutionDescriptor red = gson.fromJson(wsMsg.getData(), RemoteExecutionDescriptor.class);
                handleWsRed(red);
            }
            break;
            case SinapsiMessageTypes.MODEL_UPDATED_NOTIFICATION: {
                syncMacros(new BackgroundSyncCallback() {
                    @Override
                    public void onBackgroundSyncSuccess(List<MacroInterface> currentMacros) {
                        //do nothing
                    }

                    @Override
                    public void onBackgroundSyncFail(Throwable error) {
                        //do nothing
                    }
                }, false);
            }
            break;
            case SinapsiMessageTypes.NEW_CONNECTION: {

            }
            break;
            case SinapsiMessageTypes.CONNECTION_LOST: {

            }
        }
    }

    public UserInterface getLoggedUser() {
        return loggedUser;
    }

    @Override
    public void onUserLogIn(UserInterface user) {
        this.loggedUser = user;
        SinapsiAndroidApplication app = (SinapsiAndroidApplication) getApplication();
        app.setLoggedIn(true);
    }

    @Override
    public void onUserLogOut() {
        this.loggedUser = logoutUser;
        SinapsiAndroidApplication app = (SinapsiAndroidApplication) getApplication();
        app.setLoggedIn(false);
        pauseEngine();
        if(getWSClient().isOpen()) getWSClient().closeConnection();
        stopForegroundMode();
    }

    /**
     * Loads all saved macros from a local db.
     */
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

    public void handleConflicts(List<MacroSyncConflict> conflicts, SyncManager.ConflictResolutionCallback callback) {
        //TODO (show them to the user, only if sinapsi gui is open, otherwise show notification, duplicate and disable macros)
        //if(sinapsiGuiIsOpen){
        for (MacroSyncConflict conflict : conflicts) {
            //TODO: show dialog to the user
        }
        //}else{
        //  showConflictNotification(conflicts.size());
        //  engine.pause();
        //  setResolveConflictsOnNextGuiOpen(true, conflictCallback);
        //}
    }

    public void handleSyncFailure(Throwable e, boolean showError) {

        Lol.d(SinapsiBackgroundService.class, "Sync failed: " + e.getMessage());
        e.printStackTrace();

        if (showError) {
            if (e instanceof RetrofitError) {
                DialogUtils.handleRetrofitError(e, SinapsiBackgroundService.this, true);
            } else if (e instanceof InconsistentMacroChangeException) {
                DialogUtils.showOkDialog(this,
                        "Error",
                        "A data consistency error occurred while syncing data.\n" +
                                "Retry. If this fails again, contact developers of Sinapsi\n" +
                                "Error description:" + e.getMessage(),
                        true);
            } else {
                DialogUtils.showOkDialog(SinapsiBackgroundService.this, "Sync failed", e.getMessage(), true);
            }
        }
    }


    public void mockLogin() {
        onUserLogIn(logoutUser);
        setDevice(new FactoryModel().newDevice(-1, "Test device", "Sinapsi debug", "AndroidSmartphone", loggedUser, AndroidAppConsts.CLIENT_VERSION));
        initEngine();
    }


    /**
     * Binder class received by activities in order to access
     * this service's methods.
     */
    public class SinapsiServiceBinder extends Binder {
        public SinapsiBackgroundService getService() {
            return SinapsiBackgroundService.this;
        }
    }

    /**
     * engine getter
     *
     * @return the engine
     */
    public MacroEngine getEngine() {
        return engine;
    }

    /**
     * sinapsi log getter
     *
     * @return the sinapsi log
     */
    public SinapsiLog getSinapsiLog() {
        return sinapsiLog;
    }

    /**
     * component factory getter
     *
     * @return the component factory
     */
    @Override
    public ComponentFactory getComponentFactory() {
        return engine.getComponentFactory();
    }

    /**
     * model factory getter
     *
     * @return the model factory
     */
    public FactoryModel getFactoryModel() {
        return fm;
    }

    /**
     * web service facade getter
     *
     * @return the web service facade
     */
    public RetrofitWebServiceFacade getWeb() {
        return web;
    }

    /**
     * User settings facade getter
     *
     * @return the user settings facade
     */
    public UserSettingsFacade getSettings() {
        return settings;
    }

    /**
     * getter of the device on which this client is running onto.
     *
     * @return the device
     */
    public DeviceInterface getDevice() {
        return device;
    }

    /**
     * setter of the device on which this client is running onto.
     *
     * @param device the device
     */
    public void setDevice(DeviceInterface device) {
        this.device = device;
    }

    /**
     * Return the WSClient object
     *
     * @return WSClient
     */
    public WSClient getWSClient() {
        return web.getWebSocketClient();
    }

    /**
     * QUESTO E' UN PRIMO ESEMPIO DI UNA MACRO LOCALE.
     * Viene creata una macro che si attiva ogni volta che il wifi
     * si connette ad una rete, e che quindi stampa un messaggio
     * nel log e successivamente mostra una notifica.
     * <p/>
     * Il lavoro che fa questo metodo e' in sostanza quello
     * che dovra' essere fatto dal macro editor. Le uniche differenze
     * sono che il macro editor comunichera' con una GUI, e che controllera'
     * anche quali component sono disponibili prima di aggiungerli o mostrarli
     * all'utente.
     */
    public void createLocalMacroExamples() {
        MacroInterface myMacro = fm.newMacro("Wifi connection", 1);
        myMacro.setTrigger(getComponentFactory().newTrigger(
                TriggerWifi.TRIGGER_WIFI,
                new ActualParamBuilder()
                        .put("wifi_connection_status", ConnectionStatusChoices.CONNECTED.toString())
                        .create().toString(),
                myMacro,
                device.getId()));

        myMacro.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Wifi enabled")
                        .create().toString(),
                device.getId()
        ));

        myMacro.addAction(getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Yeah!")
                        .put("notification_message", "Connected to @{wifi_ssid}.")
                        .create().toString(),
                device.getId()
        ));
        myMacro.setMacroColor("#3333AA");
        myMacro.setIconName("ic_macro_default");
        engine.addMacro(myMacro);


        //MACRO 2
        MacroInterface myMacro2 = fm.newMacro("Screen Log", 2);
        myMacro2.setTrigger(getComponentFactory().newTrigger(
                TriggerScreenPower.TRIGGER_SCREEN_POWER,
                null,
                myMacro2,
                device.getId()
        ));

        myMacro2.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Lo schermo e' @{screen_power}")
                        .create().toString(),
                device.getId()
        ));

        myMacro2.addAction(getComponentFactory().newAction(
                ActionSetVariable.ACTION_SET_VARIABLE,
                new ActualParamBuilder()
                        .put("var_name", "screen_power")
                        .put("var_scope", VariableManager.Scopes.LOCAL.toString())
                        .put("var_type", VariableManager.Types.STRING.toString())
                        .put("var_value", "@{screen_power} @{screen_power}")
                        .create().toString(),
                device.getId()
        ));

        myMacro2.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Lo schermo e' @{screen_power}")
                        .create().toString(),
                device.getId()
        ));

        myMacro2.setMacroColor("#33AA33");
        myMacro2.setIconName("ic_macro_default");
        engine.addMacro(myMacro2);

        //MACRO3
        MacroInterface myMacro3 = fm.newMacro("Ex. POWER->CONFIRM->WIFIOFF->LOG", 3);
        /*myMacro3.setTrigger(getComponentFactory().newTrigger(
                TriggerACPower.TRIGGER_AC_POWER,
                new ActualParamBuilder()
                        .put("ac_power", false)
                        .create().toString(),
                myMacro3
        ));*/

        /*myMacro3.setTrigger(getComponentFactory().newTrigger(
                TriggerEngineStart.TRIGGER_ENGINE_START,
                null,
                myMacro3
        ));*/

        myMacro3.setTrigger(getComponentFactory().newTrigger(
                TriggerWifi.TRIGGER_WIFI,
                new ActualParamBuilder()
                        .put("wifi_connection_status", ConnectionStatusChoices.CONNECTED.toString())
                        .create().toString(),
                myMacro3,
                device.getId()));

        myMacro3.addAction(getComponentFactory().newAction(
                ActionContinueConfirmDialog.ACTION_CONTINUE_CONFIRM_DIALOG,
                new ActualParamBuilder()
                        .put("dialog_title", "Continuare?")
                        .put("dialog_message", "Sicuro di voler disattivare il wifi?")
                        .create().toString(),
                device.getId()
        ));

        myMacro3.addAction(getComponentFactory().newAction(
                ActionWifiState.ACTION_WIFI_STATE,
                new ActualParamBuilder()
                        .put("wifi_switch", false)
                        .create().toString(),
                device.getId()
        ));

        myMacro3.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Il wifi e' disattivato")
                        .create().toString(),
                device.getId()
        ));

        myMacro3.setMacroColor("#AA3333");
        myMacro3.setIconName("ic_macro_default");
        //engine.addMacro(myMacro3);
    }

    public void pauseEngine() {
        engine.pauseEngine();
        stopForegroundMode();
    }

    public void resumeEngine() {
        engine.resumeEngine();
        startForegroundMode();
    }

    public MacroInterface newEmptyMacro() {
        int id = safeSyncManager.getMinId() - 1;
        return engine.newEmptyMacro(id);
    }

    private void handleWsRed(RemoteExecutionDescriptor red){
        Message msg = REDHandler.obtainMessage();
        msg.obj = new Triplet<RemoteExecutionDescriptor, MacroEngine, Boolean>(red, engine, true);
        msg.sendToTarget();
    }


    private void startForegroundMode() {
        //HINT: useful toggles instead of classic content pending intent

        Intent i1 = new Intent(this, MainActivity.class);
        PendingIntent maini = PendingIntent.getActivity(this, 0, i1, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText("Sinapsi Engine service is running")
                .setSmallIcon(R.drawable.ic_stat_1436506281_cog)
                .setContentIntent(maini);
        Notification forenotif = builder.build();
        startForeground(1, forenotif);
    }

    private void stopForegroundMode() {
        stopForeground(true);
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
            Lol.d(this, "Sync succeedeed: refreshing macros");
            engine.clearMacros();
            engine.addMacros(currentMacros);
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

    private final class REDHandler extends Handler{

        public REDHandler(Looper looper) {
            super(looper); //SUPER LOOPER!
        }

        private void handleRecursive(RemoteExecutionDescriptor red, MacroEngine engine, boolean firstcall){
            Message msg = obtainMessage();
            msg.obj = new Triplet<RemoteExecutionDescriptor, MacroEngine, Boolean>(red, engine, firstcall);
            handleMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final Triplet<RemoteExecutionDescriptor, MacroEngine, Boolean> triplet = (Triplet<RemoteExecutionDescriptor, MacroEngine, Boolean>) msg.obj;

            try {
                engine.continueMacro(triplet.getFirst());
            } catch (MacroEngine.MissingMacroException e) {
                if (triplet.getThird()) {
                    //retries after a sync

                    syncMacros(new BackgroundSyncCallback() {
                        @Override
                        public void onBackgroundSyncSuccess(List<MacroInterface> currentMacros) {
                            handleRecursive(triplet.getFirst(), triplet.getSecond(), false);
                        }

                        @Override
                        public void onBackgroundSyncFail(Throwable error) {
                            //TODO: a remote execution descriptor arrived,
                            //----:     the macro is not present on this
                            //----:     client, and an attempt to sync failed.
                            //----:     what to do?
                        }
                    }, false);

                } else {
                    e.printStackTrace();
                    //the server is trying to tell the client to execute a macro that doesn't exist (neither in the server)
                    //just prints the stack trace and ignores the message
                }
            }
        }



    }
}
