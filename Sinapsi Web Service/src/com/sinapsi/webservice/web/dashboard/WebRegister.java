package com.sinapsi.webservice.web.dashboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sinapsi.model.impl.User;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.UserDBManager;

/**
 * Servlet implementation class WebRegister
 */
@WebServlet("/web_register")
public class WebRegister extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        
        try {
            User user = (User) userManager.getUserByEmail(email);           
            if (user == null) {
                User newUser = (User)userManager.newUser(email, password);
                
                // register the web service as a new device
                DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
                deviceManager.newDevice("Cloud", "Sinapsi", "Web", newUser.getId(), 1);
                request.getRequestDispatcher("registered.html").forward(request, response);
            
            } else {
                request.getRequestDispatcher("error.html").forward(request, response);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
