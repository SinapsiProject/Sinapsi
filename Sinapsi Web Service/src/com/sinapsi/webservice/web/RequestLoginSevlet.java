package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
import com.sinapsi.webservice.db.KeysManager;

/**
 * Servlet called when a user want to login
 */
@WebServlet("/request_login")
public class RequestLoginSevlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        Gson gson = new Gson();
        KeysManager keysManager = new KeysManager();
        // generate local public/private keys
        KeyGenerator generator = new KeyGenerator();
        
        try {
        	 String email = request.getParameter("email");
        	 String clientPublicKey = request.getParameter("pub");
        	 String clientSessionKey = request.getParameter("skey");
        	 
        	 Encrypt encrypt = new Encrypt(PublicKeyManager.convertToKey(clientPublicKey));
        	 String localPrivateKey = PrivateKeyManager.convertToString(generator.getPrivateKey());
        	 String localPublicKey = PublicKeyManager.convertToString(generator.getPublicKey());
        	 String localSessionKey = SessionKeyManager.convertToString(encrypt.getEncryptedSessionKey());
        	 
        	 // update user keys in the db
        	 keysManager.updateRemoteKeys(email, clientPublicKey, clientSessionKey);
        	 // update local keys 
        	 keysManager.updateLocalKeys(email, localPublicKey, localPrivateKey, localSessionKey);
        	 
        	 //send local public key and session key to the client
        	 out.print(gson.toJson(new HashMap.SimpleEntry<String, String>(localPublicKey, localSessionKey)));
             out.flush();
             
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}

}
