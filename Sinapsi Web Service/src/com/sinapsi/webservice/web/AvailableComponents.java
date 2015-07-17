package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.AvailabilityMap;
import com.sinapsi.model.impl.CommunicationInfo;
import com.sinapsi.model.impl.TriggerDescriptor;
import com.sinapsi.utils.Pair;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.engine.WebServiceGsonManager;
import com.sinapsi.webservice.system.WebServiceConsts;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Get/Set available components for a specific device
 */
@WebServlet("/available_components")
public class AvailableComponents extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
	    EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
	    KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
	    Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();
	    
	    
	        
	    String email = request.getParameter("email");
	    String name = request.getParameter("name");
	    String model = request.getParameter("model");
	    
	    try {
	        Encrypt encrypter;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                encrypter = new Encrypt(keysManager.getUserPublicKey(email, name, model),
                                        keysManager.getServerUncryptedSessionKey(email, name, model));
            
            AvailabilityMap data = new AvailabilityMap();
            
            try {
                List<DeviceInterface> devicesUser = deviceManager.getUserDevices(email);
                
                for(DeviceInterface device : devicesUser) {
                	device.getUser().setPassword(""); //hiding salted password
                    data.put(
	                            device,
	                            engineManager.getAvailableTriggers(device.getId()),
	                            engineManager.getAvailableActions(device.getId())
                            );
                }
                
                if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    out.print(encrypter.encrypt(gson.toJson(data)));
                else
                    out.print(gson.toJson(data));
                out.flush();
                
            } catch(SQLException e) {
                data.clear();
                data.errorOccured(true);
                data.setErrorDescription("Error during getting available components");
                
                
                if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    out.print(encrypter.encrypt(gson.toJson(data)));
                else
                    out.print(gson.toJson(data));
            }
           
	    } catch(Exception e) {	        
	        e.printStackTrace();
	    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();
        
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String model = request.getParameter("model");

        // read the encrypted body
        String cryptedJsonbody = BodyReader.read(request);
        String cryptedString = gson.fromJson(cryptedJsonbody, new TypeToken<String>() {}.getType());
        
        try {
            Encrypt encrypter;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                encrypter = new Encrypt(keysManager.getUserPublicKey(email, name, model),
                                        keysManager.getServerUncryptedSessionKey(email, name, model));            
            
            // create the decrypter
            Decrypt decrypter;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                decrypter = new Decrypt(keysManager.getServerPrivateKey(email, name, model), 
                                        keysManager.getUserSessionKey(email, name, model));
            // decrypt the jsoned body
            String jsonBody;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                 jsonBody = decrypter.decrypt(cryptedString);
            else
                 jsonBody = cryptedJsonbody;
            
            Pair<List<TriggerDescriptor>,List<ActionDescriptor>> newData = gson.fromJson(jsonBody, 
                    new TypeToken<Pair<List<TriggerDescriptor>, List<ActionDescriptor>>>() {}.getType());
            
            List<TriggerDescriptor> availableTriggers = newData.getFirst();
            List<ActionDescriptor> avalabbleActions = newData.getSecond();
            
            String errorDescription = "";
            boolean errorOccured = false;
            try {
                engineManager.addAvailableTriggers(deviceManager.getDevice(name, model).getId(), availableTriggers);
            } catch(SQLException e) {
                errorOccured = true;
                errorDescription = "Error during add available triggers";
            }
            
            try {
                engineManager.addAvailableActions(deviceManager.getDevice(name, model).getId(), avalabbleActions);
            } catch(SQLException e) {
                errorOccured = true;
                errorDescription = "Error during add available actions";
            }
            
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                if(errorOccured)
                    out.print(encrypter.encrypt(gson.toJson(new CommunicationInfo(errorDescription, true))));
                else
                    out.print(encrypter.encrypt(gson.toJson(new CommunicationInfo())));
            
            else
                if(errorOccured)
                    out.print(gson.toJson(new CommunicationInfo(errorDescription, true)));
                else
                    out.print(gson.toJson(new CommunicationInfo()));
            
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
