package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bgp.decryption.Decrypt;
import com.bgp.encryption.Encrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.impl.User;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet that Sign in the user
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // empty body
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        // objects that manipulate data in the db
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");

        try {
            String email = request.getParameter("email");
            String encryptedJsonBody = BodyReader.read(request);

            // Create the encrypter 
            Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
            
            // create the decrypter using local private key, and the client encrypted session key, then  decrypt the jsoned body
            Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(email),keysManager.getClientSessionKey(email));
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            
            // return the string from the decrypted json string
            String pwd = gson.fromJson(jsonBody, new TypeToken<String>() {}.getType());

            User user = (User) userManager.getUserByEmail(email);           
            
            if (user != null) {
                // the user is ok
                if (userManager.checkUser(email, pwd)) {          
                    // set the connected devices
                    user.setDevices(deviceManager.getUserDevices(email));
                    
                    // and send the encrypted data
                    out.print(encrypter.encrypt(gson.toJson(user)));
                    out.flush();
                
                // login error, (email incorrect or password incorrect)
                } else {
                    // set error description
                    user.errorOccured(true);
                    user.setErrorDescription("Login error");
                    // send encrypted data            
                    out.print(encrypter.encrypt(gson.toJson(user)));
                    out.flush();
                }
            
            // the user doesn't exist in the db
            } else {
                FactoryModelInterface factory = new FactoryModel();
                user = (User) factory.newUser(0, email, pwd, null);
                user.errorOccured(true);
                user.setErrorDescription("User doesnt exist");
                // send encrypted data
                out.print(encrypter.encrypt(gson.toJson(user)));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
