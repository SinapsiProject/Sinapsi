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
import com.sinapsi.webservice.db.DeviceManager;
import com.sinapsi.webservice.db.KeysManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet implementation class DeviceConnectedServlet
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
        DeviceManager deviceManager = new DeviceManager();
        String action = request.getParameter("action");
        Gson gson = new Gson();

        // get connected device request
        if (action.equals("get")) {
            String email = request.getParameter("email");

            try {
                // create the keys manger and the encrypter
                KeysManager keysManager = new KeysManager();
                Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
                
                User user = (User) deviceManager.getUserByEmail(email);
                List<DeviceInterface> devices;

                if (user != null) {
                    devices = deviceManager.getUserDevices(email);
                    out.print(encrypter.encrypt(gson.toJson(devices)));
                    out.flush();
                
                // user doesn't exist, return empty array of json
                } else {
                    devices = new ArrayList<DeviceInterface>();
                    out.print(encrypter.encrypt(gson.toJson(devices)));
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
        DeviceManager deviceManager = new DeviceManager();
        KeysManager keysManager = new KeysManager();
        Gson gson = new Gson();

        // add device request
        if (action.equals("add")) {
            // get the parameter from the request
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String model = request.getParameter("model");
            String type = request.getParameter("type");
            int version = Integer.parseInt(request.getParameter("version"));
            
            // read the encrypted body
            String encryptedJsonbody = BodyReader.read(request);

            try {
                // create the encrypter
                Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
                // create the decrypter
                Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(email), keysManager.getClientSessionKey(email));
                // decrypt the jsoned body
                String jsonBody = decrypter.decrypt(encryptedJsonbody);
                // get the id string from the decrypter jsoned body
                String id = gson.fromJson(jsonBody, new TypeToken<String>() {}.getType());
                int idUser = Integer.parseInt(id);

                // if the device is new then added to the db
                if (!deviceManager.checkDevice(name, model, idUser)) {
                    Device device = (Device) deviceManager.newDevice(name,model, type, idUser, version);
                    out.print(encrypter.encrypt(gson.toJson(device)));
                    out.flush();

                // device already exist, return it
                } else {
                    Device device = (Device) deviceManager.getDevice(name, model, idUser);
                    // and set error description
                    device.errorOccured(true);
                    device.setErrorDescription("device already exist");
                    out.print(encrypter.encrypt(gson.toJson(device)));
                    out.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
