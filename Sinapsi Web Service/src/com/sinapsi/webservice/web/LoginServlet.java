package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bgp.decryption.Decrypt;
import com.bgp.encryption.Encrypt;
import com.bgp.keymanager.SessionKeyManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.model.impl.User;
import com.sinapsi.utils.Pair;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.engine.WebServiceGsonManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Login System
 * 
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
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();
        
        // objects that manipulate data in the db
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");

        try {
            String email = request.getParameter("email");
            String deviceName = request.getParameter("name");
            String deviceModel = request.getParameter("model");
            
            String jsonBody = BodyReader.read(request);
           
            // return the string from the decrypted json string
            Pair<byte[], String> pwdSes = gson.fromJson(jsonBody, 
                                          new TypeToken<Pair<byte[], String>>() {}.getType());
            
            SecretKey clientSessionKey = SessionKeyManager.convertToKey(pwdSes.getFirst());

            
            // update session key of the client 
            keysManager.updateUserSessionKey(email, deviceName, deviceModel, SessionKeyManager.convertToString(clientSessionKey));
            
            // Create the encrypter using the session key saved in the request login servlet
            Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel), 
                                            keysManager.getServerUncryptedSessionKey(email, deviceName, deviceModel));
            
            // create the decrypter using local private key, and the client encrypted session key, then  decrypt the jsoned body
            Decrypt decrypter = new Decrypt(keysManager.getServerPrivateKey(email, deviceName, deviceModel), 
                                            keysManager.getUserSessionKey(email, deviceName, deviceModel));
            
            String password = decrypter.decrypt(pwdSes.getSecond());
            User user = (User) userManager.getUserByEmail(email);           
            
            if (userManager.checkUser(email, password)) {          
                    
                // send the encrypted data
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
                keysManager.updateUserPublicKey(email, deviceName, deviceModel, null);
                keysManager.updateServerKeys(email, deviceName, deviceModel, null, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
