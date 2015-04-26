package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sinapsi.model.ActionInterface;
import com.sinapsi.model.TriggerInterface;
import com.sinapsi.webservice.db.DatabaseManager;

/**
 * Servlet implementation class AvailableTriggerServlet
 */
@WebServlet("/available_triggers")
public class AvailableTriggerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		DatabaseManager db = new DatabaseManager();
		response.setContentType("application/json");
		int idDevice = Integer.parseInt(request.getParameter("device"));
		
		try {
			List<TriggerInterface> triggers = db.getAvailableTrigger(idDevice);
			Gson gson = new Gson();
			out.print(gson.toJson(triggers)); 
			out.flush(); 
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
