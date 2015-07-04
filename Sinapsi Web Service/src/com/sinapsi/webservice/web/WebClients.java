package com.sinapsi.webservice.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.sinapsi.model.UserInterface;
import com.sinapsi.webservice.db.UserDBManager;

/**
 * Servlet implementation class WebClients
 */
@WebServlet("/web_clients")
public class WebClients extends HttpServlet {
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
	    UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
	    HttpSession session = request.getSession();
	    
	    String email = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("user")) 
                    email = cookie.getValue();
            }
        }
        
        try {
            UserInterface user = userManager.getUserByEmail(email);
            // user don't have the permission to see this page
            if(user.getRole().equals("user"))
                request.getRequestDispatcher("clients.jsp").forward(request, response);
            
            List<UserInterface> administrators = userManager.getAdmins();
            List<UserInterface> users = userManager.getUsers();
            List<UserInterface> pendingUsers = userManager.getPendingUsers();
            
            session.setAttribute("admins", administrators);
            session.setAttribute("users", users);
            session.setAttribute("pending_users", pendingUsers);
            
            request.getRequestDispatcher("clients.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }   
	}
}
