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
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.SyncOperation;
import com.sinapsi.utils.Pair;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet implementation class MacroServlet
 */
@WebServlet("/macro")
public class MacroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private  Gson gson = new Gson();
	private KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
	private EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");     
    private UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
    private DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");  
    PrintWriter out; 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
                    
        String email = request.getParameter("email");
        String deviceName = request.getParameter("name");
        String deviceModel = request.getParameter("model");
        
        try {
            // create the encrypter
            Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel));
            // get the list of macro from the db
            List<MacroInterface> macros = engineManager.getUserMacro(userManager.getUserByEmail(email).getId());
            
            // sync macro for the current device
            deviceManager.macroNotSynced(deviceName, deviceModel, false);
            // send the encrypted data
            out.print(encrypter.encrypt(gson.toJson(new Pair<Boolean, List<MacroInterface>>(false, macros))));
            out.flush();
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    out = response.getWriter();
        
               
        String email = request.getParameter("email");
        String deviceName = request.getParameter("name");
        String deviceModel = request.getParameter("model");
        String action = request.getParameter("action");
        
        // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        
        try {
            Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel));
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getServerPrivateKey(email, deviceName, deviceModel),    
                                            keysManager.getUserSessionKey(email, deviceName, deviceModel));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            
            // switch the action to do 
            switch (action) {
                //  push a list of changes (adds/updates/deletes) to do 
                case "push": {
                    
                   // extract the list of actions from the jsoned triggers
                    List<Pair<SyncOperation, MacroInterface>> changes = gson.fromJson(jsonBody, new TypeToken<List<Pair<SyncOperation, MacroInterface>>>() {}.getType());
                    List<Pair<SyncOperation, Integer>> result = new ArrayList<Pair<SyncOperation,Integer>>();
                    
                    // iterate the stack of operation to do 
                    for(Pair<SyncOperation, MacroInterface> change : changes) {
                        // operations
                        switch(change.getFirst()) {
                            case UPDATE:
                            case ADD:
                                result.add(new Pair<SyncOperation, Integer>(SyncOperation.ADD, add(jsonBody, email)));
                                break;
                                
                            case DELETE:
                                deleteMacro(jsonBody);
                                result.add(new Pair<SyncOperation, Integer>(SyncOperation.DELETE, -1));
                                break;                                 
                        }
                    }
                    
                    // return the result
                    out.print(encrypter.encrypt(gson.toJson(result)));
                    out.flush();
                } break;
                
                case "add": {
                    // add macro
                    int idMacro = add(jsonBody, email);
                    
                    // send idMacro
                    out.print(encrypter.encrypt(gson.toJson(idMacro)));
                    out.flush();                
                } break;
                
                case "add_macros": {
                    // add a list of macro
                    List<Integer> ids = addMacros(jsonBody, email);
                    
                    // send the macro id
                    out.print(encrypter.encrypt(gson.toJson(ids)));
                    out.flush();                   
                } break;
                
                case "delete": {
                    // delete macro
                    deleteMacro(jsonBody);
                    
                    //send -1 id
                    out.print(encrypter.encrypt(gson.toJson(-1)));
                    out.flush();
                } break;
        
            }
      
            // async macro on all devices of current user 
            // TODO: async all devices except current device
            deviceManager.macroNotSynced(email, true);
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
	 */
	private int add(String jsonBody, String email) {
	    // extract the list of actions from the jsoned triggers
        MacroInterface macro = gson.fromJson(jsonBody, new TypeToken<MacroInterface>() {}.getType());
        
        // add macro and return the id if macro already exist, update 
        try {
            int idMacro = engineManager.addUserMacro(userManager.getUserByEmail(email).getId(), macro);
   
            // save the macro id
            return idMacro;
            
        } catch (Exception e) {
            e.printStackTrace();
        }     
        return -1;
	}
	
	/**
	 * Add a list of macro
	 * @param jsonBody json body containing macro interfaces
	 * @param email email of the user
	 * @param deviceName device name of the user
	 * @param deviceModel device model of the user
	 */
	private List<Integer> addMacros(String jsonBody, String email) {
	    // extract the list of actions from the jsoned triggers
        List<MacroInterface> macros = gson.fromJson(jsonBody, new TypeToken<List<MacroInterface>>() {}.getType());
        
        try {
            return engineManager.addUserMacros(userManager.getUserByEmail(email).getId(), macros);
   
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return new ArrayList<Integer>();
	}
	
	/**
	 * Delete macro
	 * @param jsonBody json body
	 */
	private void deleteMacro(String jsonBody) {
	    // extract the list of actions from the jsoned triggers
        MacroInterface macro = gson.fromJson(jsonBody, new TypeToken<MacroInterface>() {}.getType());
        
        // delete macro
        try {
            engineManager.deleteUserMacro(macro.getId());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
	}
}
