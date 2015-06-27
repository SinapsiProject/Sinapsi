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
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.engine.WebServiceLog;
import com.sinapsi.webservice.system.WebServiceConsts;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet implementation class EncryptionTest
 */
@WebServlet("/encryption_test")
public class EncryptionTest extends HttpServlet {
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
	    KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        WebServiceLog log = new WebServiceLog(WebServiceLog.FILE_OUT);
	    response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        String email = request.getParameter("email");
        String deviceName = request.getParameter("name");
        String deviceModel = request.getParameter("model");
        
        // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        String test1 = gson.fromJson(encryptedJsonBody, new TypeToken<String>() {}.getType());
        System.out.println(test1);
        
        try {
            Encrypt encrypter = new Encrypt(keysManager.getUserPublicKey(email, deviceName, deviceModel),
                                            keysManager.getServerUncryptedSessionKey(email, deviceName, deviceModel));
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getServerPrivateKey(email, deviceName, deviceModel),    
                                            keysManager.getUserSessionKey(email, deviceName, deviceModel));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            String test = gson.fromJson(jsonBody, new TypeToken<String>() {}.getType());
            log.log(log.getTime(), test);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
      
	}

}
