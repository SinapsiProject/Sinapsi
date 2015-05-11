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
import com.sinapsi.model.MacroComponent;
import com.sinapsi.webservice.db.EngineManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet implementation class AvailableActionServlet
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
        EngineManager engineManager = new EngineManager();
        KeysDBManager keysManager = new KeysDBManager();
        UserManager userManager = new UserManager();
        Gson gson = new Gson();

        int idDevice = Integer.parseInt(request.getParameter("device"));

        try {
            String email = userManager.getUserEmail(idDevice);
            // create the encrypter
            Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
            // get the available actions from the db
            List<MacroComponent> actions = engineManager.getAvailableAction(idDevice);
            // send the encrypted data
            out.print(encrypter.encrypt(gson.toJson(actions)));
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
        EngineManager engineManager = new EngineManager();
        KeysDBManager keysManager = new KeysDBManager();
        UserManager userManager = new UserManager();
        Gson gson = new Gson();

        int idDevice = Integer.parseInt(request.getParameter("device"));
        
        // if the db fails to add the available actions, then set success to false, and vice-versa
        boolean success = false;
        // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);

        try {
            String email = userManager.getUserEmail(idDevice);
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(email), keysManager.getClientSessionKey(email));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            // extract the list of actions from the jsoned triggers
            List<MacroComponent> actions = gson.fromJson(jsonBody, new TypeToken<List<MacroComponent>>() {}.getType());
            // add the list of actions in the db
            engineManager.addAvailableActions(idDevice, actions);
            success = true;

        } catch (Exception e) {
            // the db fails to add actions
            success = false;
            e.printStackTrace();
        }

        try {
            String email = userManager.getUserEmail(idDevice);
            // return a crypted response to the client
            Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
            if (success)
                out.print(encrypter.encrypt(gson.toJson("success!")));
            else
                out.print(encrypter.encrypt(gson.toJson("Fail!")));

            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
