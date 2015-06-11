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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.sinapsi.android.enginesystem.AndroidDeviceInfo;
import com.sinapsi.android.view.MainActivity;
import com.sinapsi.android.web.AndroidBase64DecodingMethod;
import com.sinapsi.android.web.AndroidBase64EncodingMethod;
import com.sinapsi.client.AppConsts;
import com.sinapsi.android.Lol;
import com.sinapsi.android.persistence.AndroidUserSettingsFacade;
import com.sinapsi.android.enginesystem.AndroidNotificationAdapter;
import com.sinapsi.client.SyncManager;
import com.sinapsi.client.persistence.UserSettingsFacade;
import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.client.web.RetrofitWebServiceFacade;
import com.sinapsi.android.enginesystem.AndroidActivationManager;
import com.sinapsi.android.enginesystem.AndroidDialogAdapter;
import com.sinapsi.android.enginesystem.AndroidSMSAdapter;
import com.sinapsi.android.enginesystem.AndroidWifiAdapter;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.client.websocket.WSClient;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.R;
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
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.wsproto.SinapsiMessageTypes;
import com.sinapsi.wsproto.WebSocketMessage;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.android.AndroidLog;

/**
 * Sinapsi background service on android platform.
 * This should be started in foreground notification mode
 * in order to remain running on the system. The engine is initialized
 * here and
 */
public class SinapsiBackgroundService extends Service implements OnlineStatusProvider {
    private WSClient wsClient;
    private RetrofitWebServiceFacade web;
    private SyncManager syncManager = new SyncManager();
    private UserSettingsFacade settings;

    private WebExecutionInterface defaultWebExecutionInterface;
    private MacroEngine engine;
    private SinapsiLog sinapsiLog;
    private DeviceInterface device;

    private FactoryModel fm = new FactoryModel();

    private Map<String, WebServiceConnectionListener> connectionListeners = new HashMap<>();

    private boolean started = false;
    private boolean onlineMode = false;




    @Override
    public void onCreate() {
        super.onCreate();

        // loading settings from shared preferences -----------------
        settings = new AndroidUserSettingsFacade(AppConsts.PREFS_FILE_NAME, this);
        //loadSettings(settings);

        if (device == null) {
            AndroidDeviceInfo adi = new AndroidDeviceInfo();
            device = fm.newDevice(-1, adi.getDeviceName(), adi.getDeviceModel(), adi.getDeviceType(), null, 1); //TODO: remove this
        }

        new WebExecutionInterface() {
            @Override
            public void continueExecutionOnDevice(ExecutionInterface ei, DeviceInterface di) {
                web.continueMacroOnDevice(
                        device,
                        di,
                        new RemoteExecutionDescriptor(
                                ei.getMacro().getId(),
                                ei.getLocalVars(),
                                ei.getExecutionStackIndexes()),
                        new SinapsiWebServiceFacade.WebServiceCallback<String>() {

                            @Override
                            public void success(String s, Object response) {
                                sinapsiLog.log("EXECUTION_CONTINUE", s);
                            }

                            @Override
                            public void failure(Throwable error) {
                                sinapsiLog.log("EXECUTION_CONTINUE", "FAIL");
                            }
                        });
            }
        };


        // web service and web socket initialization ----------------
        try{
            wsClient = new WSClient() {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    super.onOpen(handshakedata);
                }

                @Override
                public void onMessage(String message) {
                    super.onMessage(message);
                    handleWsMessage(message, true);
                }

                @Override
                public void onError(Exception ex) {
                    super.onError(ex);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    super.onClose(code, reason, remote);
                }

                private void handleWsMessage(String message, boolean firstcall){
                    Gson gson = new Gson();
                    WebSocketMessage wsMsg = gson.fromJson(message, WebSocketMessage.class);
                    switch (wsMsg.getMsgType()){
                        case SinapsiMessageTypes.REMOTE_EXECUTION_DESCRIPTOR:
                        {
                            RemoteExecutionDescriptor red = (RemoteExecutionDescriptor) wsMsg.getData();
                            try {
                                engine.continueMacro(red);
                            } catch (MacroEngine.MissingMacroException e) {
                                if(firstcall){
                                    //retries after a sync
                                    syncManager.sync();
                                    handleWsMessage(message, false);
                                }else{
                                    e.printStackTrace();
                                    //TODO: the server is trying to tell the client to execute a macro that doesn't exist (neither in the server)
                                }

                            }
                        }
                        break;
                        case SinapsiMessageTypes.MODEL_UPDATED_NOTIFICATION:
                        {
                            //TODO: impl (after alpha)
                        }
                        break;
                    }
                }

            };
        } catch (URISyntaxException e){
            e.printStackTrace();
        }

        web = new RetrofitWebServiceFacade(
                new AndroidLog("RETROFIT"),
                this,
                wsClient,
                new AndroidBase64EncodingMethod(),
                new AndroidBase64DecodingMethod());


        // initializing sinapsi log ---------------------------------
        sinapsiLog = new SinapsiLog();
        sinapsiLog.addLogInterface(new SystemLogInterface() {
            @Override
            public void printMessage(LogMessage lm) {
                Lol.d(lm.getTag(), lm.getMessage());
            }
        });


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
                ActionStringInputDialog.class);
        // here ends engine initialization      ---------------------

        // loads macros from local db/web service -------------------
        syncAndUpdateMacros();


        if(AppConsts.DEBUG_MACROS)createLocalMacroExamples();


        // starts the engine (and the TriggerOnEngineStart activates)
        engine.startEngine();


    }

    private void loadSettings(UserSettingsFacade settings) {
        device = settings.getSavedDevice();
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


        PackageManager pm = getPackageManager();

        sf.setRequirementSpec(CommonDeviceConsts.REQUIREMENT_LUA, true);
        sf.setRequirementSpec(DialogAdapter.REQUIREMENT_SIMPLE_DIALOGS, true);
        sf.setRequirementSpec(NotificationAdapter.REQUIREMENT_SIMPLE_NOTIFICATIONS, true);
        sf.setRequirementSpec(CommonDeviceConsts.REQUIREMENT_INTERCEPT_SCREEN_POWER, true);
        sf.setRequirementSpec(CommonDeviceConsts.REQUIREMENT_AC_CHARGER, true);
        sf.setRequirementSpec(DialogAdapter.REQUIREMENT_INPUT_DIALOGS, true);
        if (pm.hasSystemFeature(PackageManager.FEATURE_WIFI))
            sf.setRequirementSpec(WifiAdapter.REQUIREMENT_WIFI, true);
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            sf.setRequirementSpec(SMSAdapter.REQUIREMENT_SMS_READ, true);


        return sf;
    }

    /**
     * Loads all saved macros from a local db and/or from the web service
     *
     * @return the saved macros
     */
    public List<MacroInterface> loadSavedMacros() {
        //TODO: implement
        return new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        foregroundMode();
        started = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean isStarted() {
        return started;
    }

    public RetrofitWebServiceFacade getWeb() {
        return web;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new SinapsiServiceBinder();
    }

    public void syncAndUpdateMacros(){
        if(isOnline()) syncManager.sync();
        engine.addMacros(loadSavedMacros());

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
    public ComponentFactory getComponentFactory() {
        return engine.getComponentFactory();
    }

    /**
     * model factory getter
     *
     * @return the component factory
     */
    public FactoryModel getFactoryModel() {
        return fm;
    }

    /**
     * web service facade getter
     *
     * @return the web service facade
     */
    public SinapsiWebServiceFacade getWebServiceFacade() {
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
     * Return the WSClient object
     *
     * @return WSClient
     */
    public WSClient getWSClient() {
        return wsClient;
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
                device));

        myMacro.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Wifi enabled")
                        .create().toString(),
                device
        ));

        myMacro.addAction(getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Yeah!")
                        .put("notification_message", "Connected to @{wifi_ssid}.")
                        .create().toString(),
                device
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
                device
        ));

        myMacro2.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Lo schermo e' @{screen_power}")
                        .create().toString(),
                device
        ));

        myMacro2.addAction(getComponentFactory().newAction(
                ActionSetVariable.ACTION_SET_VARIABLE,
                new ActualParamBuilder()
                        .put("var_name", "screen_power")
                        .put("var_scope", VariableManager.Scopes.LOCAL.toString())
                        .put("var_type", VariableManager.Types.STRING.toString())
                        .put("var_value", "@{screen_power} @{screen_power}")
                        .create().toString(),
                device
        ));

        myMacro2.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Lo schermo e' @{screen_power}")
                        .create().toString(),
                device
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
                device));

        myMacro3.addAction(getComponentFactory().newAction(
                ActionContinueConfirmDialog.ACTION_CONTINUE_CONFIRM_DIALOG,
                new ActualParamBuilder()
                        .put("dialog_title", "Continuare?")
                        .put("dialog_message", "Sicuro di voler disattivare il wifi?")
                        .create().toString(),
                device
        ));

        myMacro3.addAction(getComponentFactory().newAction(
                ActionWifiState.ACTION_WIFI_STATE,
                new ActualParamBuilder()
                        .put("wifi_switch", false)
                        .create().toString(),
                device
        ));

        myMacro3.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Il wifi e' disattivato")
                        .create().toString(),
                device
        ));

        myMacro3.setMacroColor("#AA3333");
        myMacro3.setIconName("ic_macro_default");
        //engine.addMacro(myMacro3);
    }


    private void foregroundMode() {
        //TODO: useful toggles instead of classic content pending intent

        Intent i1 = new Intent(this, MainActivity.class);
        PendingIntent maini = PendingIntent.getActivity(this,0,i1,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(getString(R.string.app_name))
                .setContentText("Sinapsi Engine service is running")
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setContentIntent(maini);
        Notification forenotif = builder.build();
        startForeground(1, forenotif);
    }


}
