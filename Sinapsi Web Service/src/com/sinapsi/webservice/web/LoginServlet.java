package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.sinapsi.webservice.db.DatabaseManager;

/**
 * Servlet that recive email and password from client and check if the user is in the db
 *
 */
public class LoginServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("applications/json");
		PrintWriter out = resp.getWriter();
		DatabaseManager db = new DatabaseManager();
		try {
			if(db.checkUser(req.getParameter("email"), req.getParameter("password"))) {
				out.print(1);
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
