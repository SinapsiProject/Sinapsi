package com.sinapsi.webservice.engine;

import com.google.gson.Gson;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Event;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.LogMessage;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.log.SystemLogInterface;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.server.websocket.Message;
import com.sinapsi.server.websocket.WebSocketLocalClient;
import com.sinapsi.utils.HashMapBuilder;
import com.sinapsi.webservice.db.EngineDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Web Service engine
 *
 */
public class WebServiceEngine {
    private FactoryModelInterface factoryModel = new FactoryModel();
    private SinapsiLog sinapsiLog;
    private Map<Integer, MacroEngine> engines = new HashMap<>();
    private EngineDBManager engineDb = new EngineDBManager();

    public static final int DEFAULT_WEB_SERVICE_DEVICE_ID = 0; //TODO: change
    public static final String DEFAULT_WEB_SERVICE_DEVICE_NAME = "Cloud";
    public static final String DEFAULT_WEB_SERVICE_DEVICE_MODEL = "Sinapsi";
    public static final String DEFAULT_WEB_SERVICE_DEVICE_TYPE = "Web";

    public WebServiceEngine(){
        sinapsiLog = new SinapsiLog();
        sinapsiLog.addLogInterface(new SystemLogInterface() {
            @Override
            public void printMessage(LogMessage lm) {
                System.out.println(lm.getTag() + " : " + lm.getMessage());
            }
        });
    }

    /**
     * Init engines map
     * @param users list ud users saved in the db
     */
    public void initEngines(List<UserInterface> users){
        for(UserInterface user: users) {
            MacroEngine macroEngine = loadEngine(user);
            macroEngine.addMacros(loadSavedMacrosForUser(user));
            macroEngine.startEngine();
            // add to the list of engines, the id of the current user, and a macroEngine
            engines.put(user.getId(), macroEngine);
        }
    }

    /**
     * Load the macro engine for the user
     * @param user user saved in the db
     * @return macro engine
     */
    private MacroEngine loadEngine(UserInterface user){
        SystemFacade systemFacade = new SystemFacade();
        DeviceInterface webServiceDevice = getWebServiceDevice(user);

        //Interfaces called when the user want to continue macro in other device
        WebExecutionInterface webExecutionInterfaceExample = new WebExecutionInterface() {
            @Override
            public void continueExecutionOnDevice(ExecutionInterface ei, DeviceInterface dev) {
                RemoteExecutionDescriptor red = new RemoteExecutionDescriptor(
                        ei.getMacro().getId(),
                        ei.getLocalVars(),
                        ei.getExecutionStackIndexes());

                //call the websocket server passing the red object, device target and sender device
                Gson gson = new Gson();
                String url = "ws://localhost:8181/sinapsi/websocket/" + ei.getDevice().getId();
                WebSocketLocalClient clientEndpoint = null;
                try {
                    clientEndpoint = new WebSocketLocalClient(new URI(url));
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                JsonObject message = Json.createObjectBuilder()
                                          .add("data", gson.toJson(red))
                                          .add("to", Integer.toString(dev.getId()))
                                          .add("type", Message.REMOTE_MACRO_TYPE).build();

                WebSocketLocalClient.send(clientEndpoint.getSession(), new Message(message));
            }
        };

        //TODO:
        VariableManager globalVar = new VariableManager();

        WebServiceActivationManager activationManager = new WebServiceActivationManager(
                new ExecutionInterface(
                        systemFacade,
                        webServiceDevice,
                        webExecutionInterfaceExample,
                        globalVar,
                        sinapsiLog));

        MacroEngine result = new MacroEngine(
                webServiceDevice,
                activationManager,
                sinapsiLog,
                ActionLog.class,
                ActionSetVariable.class,
                // questi sono due component di esempio definiti in basso
                TriggerAsino.class,
                ActionSpam.class
        );

        return result;
    }

    public ComponentFactory getComponentFactoryForUser(UserInterface user){
        return getEngineForUser(user).getComponentFactory();

    }

    public List<MacroInterface> loadSavedMacrosForUser(UserInterface u) {
        List<MacroInterface> macrosOfuser = null;
        try {
            macrosOfuser =  engineDb.getUserMacro(u.getId());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return macrosOfuser;
    }

    public DeviceInterface getWebServiceDevice(UserInterface user) {
        return factoryModel.newDevice(
                DEFAULT_WEB_SERVICE_DEVICE_ID,
                DEFAULT_WEB_SERVICE_DEVICE_NAME,
                DEFAULT_WEB_SERVICE_DEVICE_MODEL,
                DEFAULT_WEB_SERVICE_DEVICE_TYPE,
                user,
                SinapsiVersions.ANTARES.ordinal()
        );
    }

    public MacroEngine getEngineForUser(UserInterface u) {
        return engines.get(u.getId());
    }


    //////////// ESEMPIO TRIGGER /////////////////////////////////////////////
    //TODO: questo e' un esempio di trigger, che si attiva quando qualcuno da' dell'asino all'utente
    public class TriggerAsino extends Trigger{

        public static final String TRIGGER_ASINO = "TRIGGER_ASINO";

        @Override
        protected JSONObject getFormalParametersJSON() throws JSONException {
            return new FormalParamBuilder()
                    .put("nome_finto_parametro", FormalParamBuilder.Types.STRING, true)
                    .create();
        }

        @Override
        protected JSONObject extractParameterValues(Event e, ExecutionInterface di) throws JSONException {
            String stringa_inutile = "asino"; //ovviamente qui si dovrebbe ottenere dati da systemfacade, event ecc...
            return new JSONObject()
                    .put("nome_finto_parametro", stringa_inutile); //la classe base trigger usa questi dati
                                                                    // per capire se i parametri attuali
                                                                    // dati in fase di editing della macro
                                                                    // corrispondono ai valori necessari
                                                                    // ad esempio questo trigger si attiva solo
                                                                    // se l'utente ha scelto "asino" per il
                                                                    // parametro "nome_finto_parametro"
        }

        @Override
        public String getName() {
            return TRIGGER_ASINO;
        }

        @Override
        public int getMinVersion() {
            return SinapsiVersions.ANTARES.ordinal();
        }

        @Override
        public HashMap<String, Integer> getSystemRequirementKeys() {
            return new HashMapBuilder<String, Integer>()
                    .put("MICROFONI_IN_GIRO_PER_IL_MONDO", 1000000000)
                    .create();
                    //Se non ci sono almeno 1000000000 microfoni in giro per il mondo
                    // e collegati alla rete di sinapsi, probabilmente non potremo
                    // ascoltare l'utente mentre viene insultato.
                    // PS: tenere aggiornato quanti microfoni ci sono in giro per il mondo
                    // chiamando
                    // systemFacade.setRequirementSpec("MICROFONI_IN_GIRO_PER_IL_MONDO", xxx);
        }
    }

    //////////// ESEMPIO ACTION /////////////////////////////////////////////
    //TODO: esempio di action, che manda illimitate pubblicita' di viagra alla mail dell'utente
    public class ActionSpam extends Action{

        public static final String ACTION_SPAM = "ACTION_SPAM";

        @Override
        protected void onActivate(ExecutionInterface ei) throws JSONException {
            // ... utilizza un oggetto della system facade per iniziare a mandare le mail ...
        }

        @Override
        protected JSONObject getFormalParametersJSON() throws JSONException {
            return new FormalParamBuilder()
                    .put("marca_viagra", FormalParamBuilder.Types.STRING, false)
                    .create();
        }

        @Override
        public String getName() {
            return ACTION_SPAM;
        }

        @Override
        public int getMinVersion() {
            return SinapsiVersions.ANTARES.ordinal();
        }

        @Override
        public HashMap<String, Integer> getSystemRequirementKeys() {
            return new HashMapBuilder<>()
                    .put("EMAIL_IMPOSTATA", 1)
                    .create();
        }
    }



}
