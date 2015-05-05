package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.impl.Device;
import com.sinapsi.model.impl.User;
import com.sinapsi.webservice.db.DatabaseManager;

/**
 * Servlet implementation class DeviceConnectedServlet
 */ 
@WebServlet("/devices")
public class DeviceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
String action = request.getParameter("action");
    	
    	switch(action) {
	    	case "add": {
	    		PrintWriter out = response.getWriter();
	            DatabaseManager db = new DatabaseManager();
	            response.setContentType("application/json");
	            String name = request.getParameter("name");
	            String model = request.getParameter("model");
	            String type = request.getParameter("type");
	            int version = Integer.parseInt(request.getParameter("model"));
	            int idUser = Integer.parseInt(request.getParameter("user"));

	            try {
	                // if the device is new then added to the db
	                if (!db.checkDevice(name, model, idUser)) {
	                    Device device = (Device) db.newDevice(name, model, type,
	                            idUser, version);
	                    Gson gson = new Gson();
	                    out.print(gson.toJson(device));
	                    out.flush();

	                } else {
	                    Device device = (Device) db.getDevice(name, model, idUser);
	                    device.errorOccured(true);
	                    device.setErrorDescription("device already exist");
	                    Gson gson = new Gson();
	                    out.print(gson.toJson(device));
	                    out.flush();
	                }

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    		
	    	case "get": {
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

	    	        } catch (Exception ex) {

	    	        }
	    	}
    	} // switch

    }


	/**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    } // doPost
}
