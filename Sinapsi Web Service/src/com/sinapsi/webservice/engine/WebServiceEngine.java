package com.sinapsi.webservice.engine;

import com.google.gson.Gson;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.LogMessage;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.log.SystemLogInterface;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.engine.components.ActionSendEmail;
import com.sinapsi.webservice.engine.components.TriggerEmailReceived;
import com.sinapsi.webservice.engine.system.EmailAdapter;
import com.sinapsi.webservice.websocket.Server;
import com.sinapsi.webshared.wsproto.SinapsiMessageTypes;
import com.sinapsi.webshared.wsproto.WebSocketMessage;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service engine
 *
 */
public class WebServiceEngine {
    private FactoryModelInterface factoryModel = new FactoryModel();
    private SinapsiLog sinapsiLog;
    private Map<Integer, MacroEngine> engines = new HashMap<>();
    private EngineDBManager engineDb = new EngineDBManager();
    private DeviceDBManager deviceDB = new DeviceDBManager();
    private Server wsserver;
    private WebServiceLog actionlog = new WebServiceLog(WebServiceLog.ACTION_LOG_FILE);
    
    public static final String DEFAULT_WEB_SERVICE_DEVICE_NAME = "Cloud";
    public static final String DEFAULT_WEB_SERVICE_DEVICE_MODEL = "Sinapsi";
    public static final String DEFAULT_WEB_SERVICE_DEVICE_TYPE = "Web";

    /**
     * Default ctor
     */
    public WebServiceEngine(){
        sinapsiLog = new SinapsiLog();
        sinapsiLog.addLogInterface(new SystemLogInterface() {
            @Override
            public void printMessage(LogMessage lm) {
                actionlog.log(actionlog.getTime(), lm.getTag() + " : " + lm.getMessage());
            }
        });
    }
    

    /**
     * Init engines map
     * 
     * @param users list ud users saved in the db
     */
    public void initEngines(List<UserInterface> users){
        for(UserInterface user: users) {
            MacroEngine macroEngine = loadEngine(user);
            // add to the list of engines, the id of the current user, and a macroEngine
            engines.put(user.getId(), macroEngine);
            macroEngine.addMacros(loadSavedMacrosForUser(user));
            macroEngine.startEngine();  
            
            try {
               engineDb.addAvailableTriggers(macroEngine.getDevice().getId(), macroEngine.getAvailableTriggerDescriptors());
               engineDb.addAvailableActions(macroEngine.getDevice().getId(), macroEngine.getAvailableActionDescriptors());
            } catch (SQLException e) {
               e.printStackTrace();
            }
        }
    }
    
    /**
     * Add to the engine a websocket server
     * 
     * @param wsserver Web Socket server
     */
    public void addWSServer(Server wsserver) {
        this.wsserver = wsserver;
    }

    /**
     * Load the macro engine for the user
     * 
     * @param user user saved in the db
     * @return macro engine
     */
    private MacroEngine loadEngine(UserInterface user){
        SystemFacade systemFacade = new SystemFacade();
        
        //HERE ARE ALL THE ADPTERS
        systemFacade.addSystemService(EmailAdapter.SERVICE_EMAIL, new EmailAdapter(user));
        
        // at engine start up, det dynamcly the triggers requirments
        // systemFacade.setRequirementSpec(key, value);
        
        DeviceInterface webServiceDevice = getWebServiceDevice(user);

        //Interfaces called when the user want to continue macro in other device
        WebExecutionInterface webExecutionInterface = new WebExecutionInterface() {
            @Override
            public void continueExecutionOnDevice(ExecutionInterface ei, DeviceInterface dev) {
            	 
                RemoteExecutionDescriptor red = new RemoteExecutionDescriptor(
                        ei.getMacro().getId(),
                        ei.getLocalVars(),
                        ei.getExecutionStackIndexes());

                //call the websocket server passing the red object, device target and sender device
                Gson gson = new Gson();
                
                String redMessage = gson.toJson(red);
                wsserver.send(dev.getId(), gson.toJson(new WebSocketMessage(SinapsiMessageTypes.REMOTE_EXECUTION_DESCRIPTOR, redMessage)));                            
            }
        };

        VariableManager globalVar = new VariableManager();
        
        ExecutionInterface executionInterface = new ExecutionInterface(
                    systemFacade, 
                    webServiceDevice, 
                    webExecutionInterface, 
                    globalVar, 
                    sinapsiLog);
        
        WebServiceActivationManager activationManager = new WebServiceActivationManager(executionInterface);

        // add list of trigger and actions
        MacroEngine macroEngine = new MacroEngine(
                webServiceDevice,
                activationManager,
                sinapsiLog,
                ActionLog.class,
                ActionSendEmail.class,
                ActionSetVariable.class,
                TriggerEmailReceived.class);

        return macroEngine;
    }

    /**
     * Return the component factory for a specific user
     * 
     * @param userid the id of the user
     * @return
     */
    public ComponentFactory getComponentFactoryForUser(int userid){
        return engines.get(userid).getComponentFactory();

    }

    /**
     * Load saved macro from the db
     * 
     * @param u user
     * @return
     */
    public List<MacroInterface> loadSavedMacrosForUser(UserInterface u) {
        List<MacroInterface> macrosOfuser = null;
        try {
            macrosOfuser =  engineDb.getUserMacro(u.getId(), getComponentFactoryForUser(u.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return macrosOfuser;
    }

    /**
     * Return the device Web Service for a specific user
     * 
     * @param user
     * @return
     */
    public DeviceInterface getWebServiceDevice(UserInterface user) {

        try {
            int webServiceDeviceId = deviceDB.getIdWebDevice(user.getId());
            
            return factoryModel.newDevice(
                    webServiceDeviceId,
                    DEFAULT_WEB_SERVICE_DEVICE_NAME,
                    DEFAULT_WEB_SERVICE_DEVICE_MODEL,
                    DEFAULT_WEB_SERVICE_DEVICE_TYPE,
                    user,
                    SinapsiVersions.ANTARES.ordinal()
            );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
       return null;
    }

    /**
     * Return the Engine for a specific user
     * 
     * @param u user
     * @return
     */
    public MacroEngine getEngineForUser(UserInterface u) {
        return engines.get(u.getId());
    }
}
