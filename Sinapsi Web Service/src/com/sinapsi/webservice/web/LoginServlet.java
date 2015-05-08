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
import com.sinapsi.webservice.db.KeysManager;
import com.sinapsi.webservice.db.UserManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet that Sign in the user
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
        UserManager userManager = new UserManager();
        Gson gson = new Gson();
        KeysManager keysManager = new KeysManager();
        response.setContentType("application/json");
        

        try {
            String email = request.getParameter("email");
            String jsonBody = BodyReader.read(request);
            String encryptedData = gson.fromJson(jsonBody, new TypeToken<String>(){}.getType());
            
            Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(email), keysManager.getClientSessionKey(email));
            String pwd = decrypter.decrypt(encryptedData);
            
            User user = (User) userManager.getUserByEmail(email);
            
            if (user != null) {
                // the user is ok
                if (userManager.checkUser(email, pwd)) {
                	// send encrypted data
                	Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
                    out.print(encrypter.encrypt(gson.toJson(user)));
                    out.flush();
                    // login error, (email incorrect or password incorrect)
                } else {
                    user.errorOccured(true);
                    user.setErrorDescription("Login error");
                    // send encrypted data
                	Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
                    out.print(encrypter.encrypt(gson.toJson(user)));

                    out.flush();
                }
                // the user doesn't exist in the db
            } else {
                FactoryModelInterface factory = new FactoryModel();
                user = (User) factory.newUser(0, email, pwd);
                user.errorOccured(true);
                user.setErrorDescription("User doesnt exist");
                // send encrypted data
            	Encrypt encrypter = new Encrypt(keysManager.getClientPublicKey(email));
                out.print(encrypter.encrypt(gson.toJson(user)));

                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
