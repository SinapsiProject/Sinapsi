package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.engine.WebServiceGsonManager;
import com.sinapsi.webservice.system.WebServiceConsts;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Get/Set the available actions for a specific device
 */
@WebServlet("/available_actions")
public class AvailableActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();

        int idDevice = Integer.parseInt(request.getParameter("device"));

        try {
            String email = userManager.getUserEmail(idDevice);
            DeviceInterface device = deviceManager.getDevice(idDevice);
            
            Encrypt encrypter;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                encrypter = new Encrypt(keysManager.getUserPublicKey(email, device.getName(), device.getModel()),
                                        keysManager.getServerUncryptedSessionKey(email, device.getName(), device.getModel()));
            
            // get the available actions from the db
            List<ActionDescriptor> actions = engineManager.getAvailableActions(idDevice);
            
            // send the encrypted data
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
            	out.print(encrypter.encrypt(gson.toJson(actions)));
            else
            	out.print(gson.toJson(actions));
            
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");   
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();

        int idDevice = Integer.parseInt(request.getParameter("device"));
       
        // if the db fails to add the available actions, then set success to false, and vice-versa
        boolean success = false;
        // read the encrypted jsoned body
        String cryptedJsonBody = BodyReader.read(request);
        String cryptedString = gson.fromJson(cryptedJsonBody, new TypeToken<String>() {}.getType());
        
        try {
            DeviceInterface device = deviceManager.getDevice(idDevice);
            
            String email = userManager.getUserEmail(idDevice);
            
            Decrypt decrypter;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                decrypter = new Decrypt(keysManager.getServerPrivateKey(email, device.getName(), device.getModel()), 
                                        keysManager.getUserSessionKey(email, device.getName(), device.getModel()));
            // decrypt the jsoned body
            String jsonBody;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
            	jsonBody = decrypter.decrypt(cryptedString);
            else
            	jsonBody = cryptedJsonBody;
            
            // extract the list of actions from the jsoned triggers
            List<ActionDescriptor> actions = gson.fromJson(jsonBody, new TypeToken<List<ActionDescriptor>>() {}.getType());
            
            // add the list of actions in the db
            engineManager.addAvailableActions(idDevice, actions);
            success = true;

        } catch (Exception e) {
            // the db fails to add actions
            success = false;
            e.printStackTrace();
        }

        try {
            DeviceInterface device = deviceManager.getDevice(idDevice);
            String email = userManager.getUserEmail(idDevice);
            
            // return a crypted response to the client
            Encrypt encrypter;
            if(WebServiceConsts.ENCRYPTED_CONNECTION)
                encrypter = new Encrypt(keysManager.getUserPublicKey(email, device.getName(), device.getModel()));
            
            if (success)
            	 if(WebServiceConsts.ENCRYPTED_CONNECTION)
            		 out.print(encrypter.encrypt(gson.toJson("success!")));
            	 else
            		 out.print(gson.toJson("success!"));
            else
            	if(WebServiceConsts.ENCRYPTED_CONNECTION)
            		out.print(encrypter.encrypt(gson.toJson("Fail!")));
            	else
            		out.print(gson.toJson("Fail!"));

            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
