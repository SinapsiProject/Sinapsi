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
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.impl.Device;
import com.sinapsi.model.impl.User;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.engine.WebServiceGsonManager;
import com.sinapsi.webservice.system.WebServiceConsts;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Manage user devices
 * 
 */
@WebServlet("/devices")
public class DeviceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        String action = request.getParameter("action");
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();

        // get connected device request
        if (action.equals("get")) {
            String email = request.getParameter("email");
            String deviceName = request.getParameter("name");
            String deviceModel = request.getParameter("model");

            try {
                // create the keys manger and the encrypter
                KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
                
                Encrypt encrypter;
                if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel),
                                            keysManager.getServerUncryptedSessionKey(email, deviceName, deviceModel));
                
                User user = (User) deviceManager.getUserByEmail(email);
                List<DeviceInterface> devices;

                if (user != null) {
                    devices = deviceManager.getUserDevices(email);
                    if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    	out.print(encrypter.encrypt(gson.toJson(devices)));
                    else
                    	out.print(gson.toJson(devices));
                    out.flush();
                
                // user doesn't exist, return empty array of json
                } else {
                    devices = new ArrayList<DeviceInterface>();
                    if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    	out.print(encrypter.encrypt(gson.toJson(devices)));
                    else
                    	out.print(gson.toJson(devices));
                    
                    out.flush();
                }

            } catch (Exception ex) {

            }
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();

        // add device request
        if (action.equals("add")) {
            // get the parameter from the request
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String model = request.getParameter("model");
            String type = request.getParameter("type");
            int version = Integer.parseInt(request.getParameter("version"));
            
            // read the encrypted body
            String cryptedJsonbody = BodyReader.read(request);
            String cryptedString = gson.fromJson(cryptedJsonbody, new TypeToken<String>() {}.getType());
            
            try {
                // create the encrypter
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
                //String jsonBody = decrypter.decrypt(encryptedJsonbody);
                String jsonBody;
                if(WebServiceConsts.ENCRYPTED_CONNECTION)
                	 jsonBody = decrypter.decrypt(cryptedString);
                else
                	 jsonBody = cryptedJsonbody;
                
                
                // get the id string
                String id = gson.fromJson(jsonBody, new TypeToken<String>() {}.getType());
                int idUser = Integer.parseInt(id);

                // if the device is new then added to the db
                if (!deviceManager.checkDevice(name, model, idUser)) {
                    Device device = (Device) deviceManager.newDevice(name,model, type, idUser, version);
                    if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    	out.print(encrypter.encrypt(gson.toJson(device)));
                    else 
                    	out.print(gson.toJson(device));

                    out.flush();
                    deviceManager.macroNotSynced(email, name, model, true);

                // device already exist, return it
                } else {
                    DeviceInterface device = deviceManager.getDevice(name, model, idUser);
                    if(WebServiceConsts.ENCRYPTED_CONNECTION)
                    	out.print(encrypter.encrypt(gson.toJson(device)));
                    else
                    	out.print(gson.toJson(device));
                    out.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
