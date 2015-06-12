package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bgp.encryption.Encrypt;
import com.bgp.generator.KeyGenerator;
import com.bgp.keymanager.PrivateKeyManager;
import com.bgp.keymanager.PublicKeyManager;
import com.bgp.keymanager.SessionKeyManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.model.impl.User;
import com.sinapsi.utils.Pair;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet called when a user want to login
 */
@WebServlet("/request_login")
public class RequestLoginSevlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // empty method
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        Gson gson = new Gson();
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        // generate local public/private keys
        KeyGenerator generator = new KeyGenerator(1024, "RSA");

        try {    
            String email = request.getParameter("email");
            User user = (User) userManager.getUserByEmail(email); 
            
            //User doesn't exist
            if (user == null) {   
                // send data
                Pair<byte[], byte[]> pair = new Pair<byte[], byte[]>(null, null);
                pair.errorOccured(true);
                pair.setErrorDescription("User doesnt exist");
                out.print(gson.toJson(pair));
                out.flush();
            }
            
            byte[] byteKey = gson.fromJson(BodyReader.read(request), new TypeToken<byte[]>(){}.getType());
            PublicKey clientPublicKey = PublicKeyManager.convertToKey(byteKey);
            
            // create the encrypter using the client public key
            Encrypt encrypt = new Encrypt(clientPublicKey);
            
            // save local private key, generated by the key generator
            String localPrivateKey = PrivateKeyManager.convertToString(generator.getPrivateKey());
            
            // save local public key, generated by the key generator
            String localPublicKey = PublicKeyManager.convertToString(generator.getPublicKey());
            
            // save local encrypted session key, generated by the encrypter
            String localSessionKey = SessionKeyManager.convertToString(encrypt.getEncryptedSessionKey());
            
            // save local unencrypted session key, generated by the encrypter
            String localUncryptedSessionKey = SessionKeyManager.convertToString(encrypt.getSessionKey());
            
            // update user keys in the db
            keysManager.updateRemotePublicKey(email, PublicKeyManager.convertToString(clientPublicKey));
            
            // update local keys in the db
            keysManager.updateLocalKeys(email, localPublicKey, localPrivateKey, localSessionKey, localUncryptedSessionKey);

            // send local public key and session key to the client
            out.print(gson.toJson(new Pair<byte[], byte[]>(PublicKeyManager.convertToByte(generator.getPublicKey()), 
                                                           SessionKeyManager.convertToByte(encrypt.getEncryptedSessionKey()))));
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
