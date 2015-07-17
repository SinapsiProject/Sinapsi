package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sinapsi.model.impl.User;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.engine.WebServiceGsonManager;

/**
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // body empty
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        response.setContentType("application/json");
        
        Gson gson = WebServiceGsonManager.defaultSinapsiGsonBuilder().create();
        String email = request.getParameter("email");
        String pwd = request.getParameter("password");

        try {
            // user already exist
            if (userManager.checkUser(email, pwd)) {
                User user = (User) userManager.getUserByEmail(email);
                // set a error description
                user.errorOccured(true);
                user.setErrorDescription("User already exist");
                
                out.print(gson.toJson(user));
                out.flush();
            } else {
                User user = (User) userManager.newUser(email, pwd);
                
                // register the web service as a new device
                DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
                deviceManager.newDevice("Cloud", "Sinapsi", "Web", user.getId(), 1);
                
                out.print(gson.toJson(user));
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
