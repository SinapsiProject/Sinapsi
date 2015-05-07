package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.impl.User;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.webservice.db.UserManager;

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
        PrintWriter out = response.getWriter();
        UserManager userManager = new UserManager();
        response.setContentType("application/json");
        try {
        	//TODO: decrypt (using private key of the user saved in the db)data from client, (email, password must be crypted)
            String email = request.getParameter("email");
            String pwd = request.getParameter("password");
            User user = (User) userManager.getUserByEmail(email);
            Gson gson = new Gson();

            if (user != null) {
                // the user is ok
                if (userManager.checkUser(email, pwd)) {
                	//TODO: send crypted user object
                    out.print(gson.toJson(user));
                    out.flush();
                    // login error, (email incorrect or password incorrect)
                } else {
                    user.errorOccured(true);
                    user.setErrorDescription("Login error");
                    out.print(gson.toJson(user));
                    out.flush();
                }
                // the user doesn't exist in the db
            } else {
                FactoryModelInterface factory = new FactoryModel();
                user = (User) factory.newUser(0, email, pwd);
                user.errorOccured(true);
                user.setErrorDescription("User doesnt exist");
                out.print(gson.toJson(user));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
