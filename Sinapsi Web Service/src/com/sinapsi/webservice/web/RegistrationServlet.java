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
import com.sinapsi.webservice.db.DatabaseManager;

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
		PrintWriter out = response.getWriter();
		DatabaseManager db = new DatabaseManager();
		response.setContentType("application/json");
		String email = request.getParameter("email");
		String pwd = request.getParameter("password"); 
		
		try {
			// user already exist
			if(db.checkUser(email, pwd)) {
				User user = (User) db.getUserByEmail(email);
				user.errorOccured(true);
				user.setErrorDescription("User already exist");
				Gson gson = new Gson();
				out.print(gson.toJson(user)); 
				out.flush(); 
			} else {
				User user = (User) db.newUser(email, pwd);
				Gson gson = new Gson();
				
				out.print(gson.toJson(user)); 
				out.flush(); 
			}
			
		} catch(Exception e) {
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
