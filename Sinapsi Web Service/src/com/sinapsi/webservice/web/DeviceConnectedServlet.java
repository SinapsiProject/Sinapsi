package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.impl.User;
import com.sinapsi.webservice.db.DatabaseManager;

/**
 * Servlet implementation class DeviceConnectedServlet
 */ 
@WebServlet("/devices_connected")
public class DeviceConnectedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		DatabaseManager db = new DatabaseManager();
		response.setContentType("application/json");
		String email = request.getParameter("email");
		
		try {
			User user = (User) db.getUserByEmail(email);
			Gson gson = new Gson();
			List<DeviceInterface> devices;
			
			if (user != null) {
				devices = db.getUserDevices(email);
				
				out.print(gson.toJson(devices)); 
				out.flush(); 
			} else {
				// user doesn't exist, return empty array of json
				devices = new ArrayList<DeviceInterface>();
				out.print(gson.toJson(devices));
				out.flush();
			}
			
		} catch(Exception ex) {
			
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
