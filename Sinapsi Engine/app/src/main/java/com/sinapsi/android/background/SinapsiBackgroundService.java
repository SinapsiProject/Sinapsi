package com.sinapsi.android.background;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import com.sinapsi.android.Lol;
import com.sinapsi.client.RetrofitWebServiceFacade;
import com.sinapsi.android.system.AndroidActivationManager;
import com.sinapsi.android.system.AndroidDialogAdapter;
import com.sinapsi.android.system.AndroidSMSAdapter;
import com.sinapsi.android.system.AndroidWifiAdapter;
import com.sinapsi.client.SinapsiWebServiceFacade;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSendSMS;
import com.sinapsi.engine.components.TriggerWifi;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.LogMessage;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.log.SystemLogInterface;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.parameters.ActualParamBuilder;
import com.sinapsi.model.parameters.SwitchStatusChoices;

import java.util.ArrayList;
import java.util.List;

/**
 * Sinapsi background service on android platform.
 * This should be started in foreground notification mode
 * in order to remain running on the system. The engine is initialized
 * here and
 */
public class SinapsiBackgroundService extends Service {

    private MacroEngine engine;
    private FactoryModel fm = new FactoryModel();
    private SinapsiLog sinapsiLog;

    private RetrofitWebServiceFacade web = new RetrofitWebServiceFacade();

    WebExecutionInterface defaultWebExecutionInterface = new WebExecutionInterface() {
        @Override
        public void continueExecutionOnDevice(ExecutionInterface ei, DeviceInterface di) {
            web.continueMacroOnDevice(
                    di,
                    new RemoteExecutionDescriptor(
                            ei.getLocalVars(),
                            ei.getExecutionStackIndexes()),
                            new SinapsiWebServiceFacade.WebServiceCallback<String>(){

                                @Override
                                public void success(String s, Object response) {

                                }

                                @Override
                                public void failure(Throwable error) {

                                }
                            });
        }
    };

    private DeviceInterface device = fm.newDevice(
            0,
            "my_phone",
            "phone_model",
            "android_smartphone",
            fm.newUser(0,
                    "my@email.com",
                    "secretpassw"),
            1); //TODO: initialize this elsewhere (perhaps load from settings or db) with user and device info

    /**
     * Default ctor. This initializes and starts the engine and the logging system.
     */
    public SinapsiBackgroundService() {
        sinapsiLog = new SinapsiLog();
        sinapsiLog.addLogInterface(new SystemLogInterface() {
            @Override
            public void printMessage(LogMessage lm) {
                Lol.d(lm.getTag(),lm.getMessage());
            }
        });

        SystemFacade sf = createAndroidSystemFacade();

        engine = new MacroEngine(device, new AndroidActivationManager(this), defaultWebExecutionInterface, sf, sinapsiLog);
        engine.addMacros(loadSavedMacros());
        engine.startEngine();
    }

    /**
     * Initializes a SystemFacade instance for the Android platform
     * @return a new SystemFacade instance
     */
    private SystemFacade createAndroidSystemFacade(){
        SystemFacade sf = new SystemFacade();

        sf.addSystemService(SystemFacade.SERVICE_DIALOGS, new AndroidDialogAdapter(this));
        sf.addSystemService(SystemFacade.SERVICE_SMS, new AndroidSMSAdapter(this));
        sf.addSystemService(SystemFacade.SERVICE_WIFI, new AndroidWifiAdapter(this));

        PackageManager pm = getPackageManager();

        sf.setRequirementSpec(SystemFacade.REQUIREMENT_LUA, true);
        sf.setRequirementSpec(SystemFacade.REQUIREMENT_SIMPLE_DIALOGS, true);

        if(pm.hasSystemFeature(PackageManager.FEATURE_WIFI))
            sf.setRequirementSpec(SystemFacade.REQUIREMENT_WIFI, true);
        if(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            sf.setRequirementSpec(SystemFacade.REQUIREMENT_SMS_READ, true);

        return sf;
    }

    /**
     * Loads all saved macros from a local db and/or from the web service
     * @return the saved macros
     */
    public List<MacroInterface> loadSavedMacros(){
        //TODO: implement
        return new ArrayList<>();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return new SinapsiServiceBinder();
    }

    /**
     * Binder class received by activities in order to access
     * this service's methods.
     */
    public class SinapsiServiceBinder extends Binder{
        public SinapsiBackgroundService getService() {
            return SinapsiBackgroundService.this;
        }
    }

    /**
     * engine getter
     * @return the engine
     */
    public MacroEngine getEngine() {
        return engine;
    }

    /**
     * sinapsi log getter
     * @return the sinapsi log
     */
    public SinapsiLog getSinapsiLog() {
        return sinapsiLog;
    }

    /**
     * component factory getter
     * @return the component factory
     */
    public ComponentFactory getComponentFactory(){
        return engine.getComponentFactory();
    }


    /**
     * QUESTO E' UN PRIMO ESEMPIO DI UNA MACRO LOCALE.
     * Viene creata una macro che si attiva ogni volta che il wifi
     * viene attivato, e che quindi stampa un messaggio nel log e
     * successivamente invia un sms a un numero inventato.
     *
     * Il lavoro che fa questo metodo e' in sostanza quello
     * che dovra' essere fatto dal macro editor. Le uniche differenze
     * sono che il macro editor comunichera' con una GUI, e che controllera'
     * anche quali component sono disponibili prima di aggiungerli o mostrarli
     * all'utente.
     */
    public void createLocalMacroExample(){
        MacroInterface myMacro = fm.newMacro("ExampleLocal", 1);
        myMacro.setTrigger(getComponentFactory().newTrigger(
                TriggerWifi.TRIGGER_WIFI,
                new ActualParamBuilder()
                        .put("wifi_status", SwitchStatusChoices.ENABLED.toString())
                        .create().toString(),
                myMacro));

        myMacro.addAction(getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Wifi enabled")
                        .create().toString()
        ));

        myMacro.addAction(getComponentFactory().newAction(
                ActionSendSMS.ACTION_SEND_SMS,
                new ActualParamBuilder()
                        .put("number", "1234567890")
                        .put("msg", "Wifi enabled on the phone")
                        .create().toString()
        ));

        engine.addMacro(myMacro);
    }

    //TODO: foreground notification mode

    //TODO: connection against web service


}
