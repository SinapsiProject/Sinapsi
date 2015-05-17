package com.sinapsi.webservice.utility;

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
import com.sinapsi.model.MacroInterface;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;

/**
 * Servlet implementation class MacroServlet
 */
@WebServlet("/macro")
public class MacroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;     

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        Gson gson = new Gson();
        
        String email = request.getParameter("email");
        
        try {
            // create the encrypter
            Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
            // get the list of macro from the db
            List<MacroInterface> macros = engineManager.getUserMacro(userManager.getUserByEmail(email).getId());
            // send the encrypted data
            out.print(encrypter.encrypt(gson.toJson(macros)));
            out.flush();
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db");
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        Gson gson = new Gson();
        boolean success = false;
        
        String email = request.getParameter("email");
        String action = request.getParameter("action");
        
        // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        
        try {
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(email), keysManager.getClientSessionKey(email));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            // extract the list of actions from the jsoned triggers
            MacroInterface macro = gson.fromJson(jsonBody, new TypeToken<MacroInterface>() {}.getType());
            
        
            switch (action) {
                case "add": {
                    engineManager.addUserMacro(userManager.getUserByEmail(email).getId(), macro);
                } break;
                
                
                case "update": {
                    //TODO
                } break;
                
                
                case "delete": {
                    engineManager.deleteUserMacro(macro.getId());
                    
                } break;
        
            }
            success = true;
            
        } catch(Exception ex) {
            success = false;
            ex.printStackTrace();
        }
        
        try {
            if(success) {
                Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
                if (success)
                    out.print(encrypter.encrypt(gson.toJson("success!")));
                else
                    out.print(encrypter.encrypt(gson.toJson("Fail!")));
    
                out.flush();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
	}
}
