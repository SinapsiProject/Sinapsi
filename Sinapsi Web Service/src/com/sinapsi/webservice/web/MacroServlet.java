package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bgp.decryption.Decrypt;
import com.bgp.encryption.Encrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.SyncOperation;
import com.sinapsi.utils.Pair;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.engine.WebServiceEngine;
import com.sinapsi.webservice.engine.WebServiceGsonManager;
import com.sinapsi.webservice.engine.WebServiceLog;
import com.sinapsi.webservice.system.WebServiceConsts;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Macro Managment
 * 
 */
@WebServlet("/macro")
public class MacroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private  WebServiceGsonManager gsonManager; 
    PrintWriter out; 

	/**
	 * Get the list of macro from the server and return a list of macro and a boolean that tell the client 
	 * that the current device is synced with the last changes
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
		EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db"); 
		WebServiceEngine engine = (WebServiceEngine) getServletContext().getAttribute("engine");
	    UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
	    DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");  
	    WebServiceEngine webServiceEngine = (WebServiceEngine) getServletContext().getAttribute("engine");
	    
	    gsonManager = new WebServiceGsonManager(webServiceEngine);
	    
	    response.setContentType("application/json");
	    out = response.getWriter();
                    
        String email = request.getParameter("email");
        String deviceName = request.getParameter("name");
        String deviceModel = request.getParameter("model");        
        
        try {
            // create the encrypter
        	Encrypt encrypter;
        	if(WebServiceConsts.ENCRYPTED_CONNECTION)
        		encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel),
        		                        keysManager.getServerUncryptedSessionKey(email, deviceName, deviceModel));
        	
        	UserInterface user = userManager.getUserByEmail(email);
        	
            // get the list of macro from the db
            List<MacroInterface> macros = engineManager.getUserMacro(user.getId(), engine.getComponentFactoryForUser(user.getId()));
            
            WebServiceLog log = new WebServiceLog(WebServiceLog.FILE_OUT);
            
            // get the value of sync before the update
            boolean oldSyncValue = deviceManager.getMacroSyncValue(deviceName, deviceModel);
            // sync macro for the current device
            deviceManager.macroNotSynced(deviceName, deviceModel, false);
            
            String data= gsonManager.getGsonForUser(user.getId())
                                   .toJson(new Pair<Boolean, List<MacroInterface>>(oldSyncValue, macros));
            log.log(data);
            
            // send the encrypted data
            if(WebServiceConsts.ENCRYPTED_CONNECTION) 
            	out.print(encrypter.encrypt(data));
            else
            	out.print(data);
            out.flush();
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
	}

	/**
	 * Push the last macro changes in the db. Client tell the server what kind of change to do.
	 * Changes can be a push of changes to do, add/update a macro and delete a macro
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
		EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db"); 
		DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");  
		UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
		WebServiceEngine webServiceEngine = (WebServiceEngine) getServletContext().getAttribute("engine");
	        
	    gsonManager = new WebServiceGsonManager(webServiceEngine);
	    response.setContentType("application/json");
	    out = response.getWriter();
                      
        String email = request.getParameter("email");
        String deviceName = request.getParameter("name");
        String deviceModel = request.getParameter("model");
        String action = request.getParameter("action");
        
        // read the encrypted jsoned body
        String cryptedJsonBody = BodyReader.read(request);
        
        try {
            UserInterface user = userManager.getUserByEmail(email);
            
            Gson userGson = gsonManager.getGsonForUser(user.getId());
            
            String cryptedString = userGson.fromJson(cryptedJsonBody, new TypeToken<String>() {}.getType());
                    
            Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel),
                                            keysManager.getServerUncryptedSessionKey(email, deviceName, deviceModel));
            
            Decrypt decrypter = new Decrypt(keysManager.getServerPrivateKey(email, deviceName, deviceModel),    
                                            keysManager.getUserSessionKey(email, deviceName, deviceModel));
            // decrypt the jsoned body
            String jsonBody;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
            	jsonBody = decrypter.decrypt(cryptedString);
            else
            	jsonBody = cryptedJsonBody;
                    
            //DEBUG
            WebServiceLog log = new WebServiceLog(WebServiceLog.FILE_OUT);
            log.log("Received json ########\n" + jsonBody + "\n ################# \n");
            
            // action to do: push a batch of changes, add a macro, update a macro and delete a macro
            switch (action) {
                
                //  push a list of changes (adds/updates/deletes) to do 
                case "push": {
                    
                    // extract the list of cahnges to do from the json
                    List<Pair<SyncOperation, MacroInterface>> changes = userGson.fromJson(jsonBody, new TypeToken<List<Pair<SyncOperation, MacroInterface>>>() {}.getType());
                    
                    // the result of computation is a list of pairs containing the sync operation and the id of macro
                    List<Pair<SyncOperation, Integer>> result = new ArrayList<Pair<SyncOperation,Integer>>();
                    
                    // iterate the list of operation to do 
                    for(Pair<SyncOperation, MacroInterface> change : changes) {
                        // operations
                        switch(change.getFirst()) {
                            case UPDATE: // update is do it by the add in case the id of macro already exixst in the db
                            case ADD:
                                // add the macro in the db and add the returned id (of the macro) to the list result
                                result.add(new Pair<SyncOperation, Integer>(SyncOperation.ADD, add(change.getSecond(), user)));
                                break;
                                
                            case DELETE:
                                // delete macro from the db and add a -1 id to the list of result
                                deleteMacro(change.getSecond().getId(), user);
                                result.add(new Pair<SyncOperation, Integer>(SyncOperation.DELETE, -1));
                                break;                                 
                        }
                    }
                    
                    // return the result
                    if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    	out.print(encrypter.encrypt(userGson.toJson(result)));
                    else
                    	out.print(userGson.toJson(result));
                    out.flush();
                    
                    // update the macros in the engine
                    MacroEngine macroEngine =  webServiceEngine.getEngineForUser(user);
                    macroEngine.clearMacros();
                    macroEngine.addMacros(engineManager.getUserMacro(user.getId(), webServiceEngine.getComponentFactoryForUser(user.getId())));
                    
                } break;
                
                case "add": {
                	
                	
            		MacroInterface m = userGson.fromJson(jsonBody, MacroInterface.class);
                	
                    // add macro and send the id of the macro
                	if(WebServiceConsts.ENCRYPTED_CONNECTION){
                		
                		out.print(encrypter.encrypt(userGson.toJson(add(m, user))));
                	} else {
                		out.print(userGson.toJson(add(m, user)));
                	}
                		
                    out.flush();                
                } break;
                
                case "add_macros": {
                	
                	List<MacroInterface> macros = userGson.fromJson(jsonBody, new TypeToken<List<MacroInterface>>(){}.getType());
                	
                    // add a list of macro and send the macro ids
                	if(WebServiceConsts.ENCRYPTED_CONNECTION)
                		out.print(encrypter.encrypt(userGson.toJson(addMacros(macros, user))));
                	else
                		out.print(userGson.toJson(addMacros(macros, user)));
                    out.flush();                   
                } break;
                
                case "delete": {
                    // delete macro
                	MacroInterface m = userGson.fromJson(jsonBody, MacroInterface.class);
                	
                    deleteMacro(m.getId(), user);
                    
                    //send -1 id
                    if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    	out.print(encrypter.encrypt(userGson.toJson(-1)));
                    else
                    	out.print(userGson.toJson(-1));
                    out.flush();
                } break;
        
            }
      
            // async macro on all devices of current user 
            deviceManager.macroNotSynced(email, deviceName, deviceModel, true);
        } catch(Exception ex) {
            ex.printStackTrace();
        }  
	}
	
	/**
	 * Add a macro to the db
	 * @param jsonBody json body containing macro interface
	 * @param email email of the user
	 * @param deviceName device name of the user
	 * @param deviceModel device model of the user
	 * @throws Exception 
	 */
	private int add(MacroInterface macro, UserInterface user) throws Exception {
		EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");     
        
        // add macro and return the id if macro already exist, update 
        try {
            int idMacro = engineManager.addUserMacro(user.getId(), macro);
   
            // save the macro id
            return idMacro;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }     
	}
	
	/**
	 * Add a list of macro
	 * @param jsonBody json body containing macro interfaces
	 * @param email email of the user
	 * @param deviceName device name of the user
	 * @param deviceModel device model of the user
	 */
	private List<Integer> addMacros(List<MacroInterface> macros, UserInterface user) {
		EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db"); 
	    
        try {
            return engineManager.addUserMacros(user.getId(), macros);
   
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return new ArrayList<Integer>();
	}
	
	/**
	 * Delete macro
	 * @param jsonBody json body
	 */
	private void deleteMacro(int id, UserInterface user) {
		EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");  
		
        // delete macro
        try {
            engineManager.deleteUserMacro(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } 
	}
}
