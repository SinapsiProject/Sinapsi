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
import com.sinapsi.webservice.db.KeysManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet implementation class AvailableActionServlet
 */
@WebServlet("/available_actions")
public class AvailableActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = response.getWriter();
		EngineManager engineManager = new EngineManager();
		KeysManager keysManager = new KeysManager();
		Gson gson = new Gson();
		response.setContentType("application/json");

		String email = request.getParameter("email");
		int idDevice = Integer.parseInt(request.getParameter("device"));

		try {
			Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
			List<MacroComponent> actions = engineManager.getAvailableAction(idDevice);
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
    	PrintWriter out = response.getWriter();
		EngineManager engineManager = new EngineManager();
		KeysManager keysManager = new KeysManager();
		Gson gson = new Gson();
		response.setContentType("application/json");
		
		String email = request.getParameter("email");
		int idDevice = Integer.parseInt(request.getParameter("device"));
		boolean success = false;

		String encryptedJsonBody = BodyReader.read(request);
		
		try {
			Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(email), keysManager.getClientSessionKey(email));
	        String jsonBody = decrypter.decrypt(encryptedJsonBody);
	        
	        List<MacroComponent> actions = gson.fromJson(jsonBody, new TypeToken<List<MacroComponent>>(){}.getType());
	        engineManager.addAvailableActions(idDevice, actions);
	        success = true;
	        
		} catch(Exception e) {
			success = true;
			e.printStackTrace();
		}
		
		try {
			Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
			if(success) 	
				out.print(encrypter.encrypt(gson.toJson("success!")));
			else 
				out.print(encrypter.encrypt(gson.toJson("Fail!")));
			
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
