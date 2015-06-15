package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        
        Gson gson = new Gson();
        
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
        PrintWriter out = response.getWriter();
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        
        Gson gson = new Gson();
        
        String email = request.getParameter("email");
        String deviceName = request.getParameter("name");
        String deviceModel = request.getParameter("model");
        String action = request.getParameter("action");
        
        // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        
        try {
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getServerPrivateKey(email, deviceName, deviceModel),    
                                            keysManager.getUserSessionKey(email, deviceName, deviceModel));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            
           
        
            switch (action) {
                case "add": {
                    // extract the list of actions from the jsoned triggers
                    MacroInterface macro = gson.fromJson(jsonBody, new TypeToken<MacroInterface>() {}.getType());
                    
                    // add macro and return the id if macro already exist, update 
                    int idMacro = engineManager.addUserMacro(userManager.getUserByEmail(email).getId(), macro);
                    List<Integer> ids = new ArrayList<Integer>();
                    ids.add(idMacro);
                    
                    // async macro on all devices of current user
                    deviceManager.macroNotSynced(email, true);
                    
                    // send the macro id
                    Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel));
                    
                    out.print(encrypter.encrypt(gson.toJson(ids)));
                    out.flush();
                    
                } break;
                
                case "add_macros": {
                    // extract the list of actions from the jsoned triggers
                    List<MacroInterface> macros = gson.fromJson(jsonBody, new TypeToken<List<MacroInterface>>() {}.getType());
                    
                    List<Integer> ids= engineManager.addUserMacros(userManager.getUserByEmail(email).getId(), macros);
                    
                    // send the macro id
                    Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel));
                    out.print(encrypter.encrypt(gson.toJson(ids)));
                    out.flush();
                    
                } break;
                
                case "delete": {
                    // extract the list of actions from the jsoned triggers
                    MacroInterface macro = gson.fromJson(jsonBody, new TypeToken<MacroInterface>() {}.getType());
                    
                    // delete macro
                    engineManager.deleteUserMacro(macro.getId());
                    
                } break;
        
            }
      
        } catch(Exception ex) {
            ex.printStackTrace();
        }
	}
}
