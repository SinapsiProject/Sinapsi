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
			if(db.checkUser(email, req.getParameter("password"))) {
				//out.print(1);
				
				/*L'user viene convertito in una stringa JSON,
					inviato al client dove retrofit (che al suo
					interno ha GSON) lo riconverte in un oggetto
				 	User. Vedere, sul client android, la chiamata
					'public User login(String email, String password)'
					all'interno di RetrofitInterface.java*/
				Gson gson = new Gson();
				User u = (User)db.getUserByEmail(email);
				out.print(gson.toJson(u)); 
			} else {
				out.print(0);
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
