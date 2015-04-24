package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sinapsi.model.impl.User;
import com.sinapsi.webservice.db.DatabaseManager;

/**
 * Servlet that receives email and password from client and, after a check,
 * returns the user object
 *
 */
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("applications/json");
		PrintWriter out = resp.getWriter();
		DatabaseManager db = new DatabaseManager();
		
		try {
			String email = req.getParameter("email");
			String pwd = req.getParameter("password"); 
			if(db.checkUser(email, pwd)) {
				Gson gson = new Gson();
				User u = (User)db.getUserByEmail(email);
				out.print(gson.toJson(u)); 
			} else {
				Gson gson = new Gson();
				User user = (User) db.newUser(email, pwd);
				
				user.errorOccured(true);
				user.setErrorDescription("Login error");
				
				out.print(gson.toJson(user));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
