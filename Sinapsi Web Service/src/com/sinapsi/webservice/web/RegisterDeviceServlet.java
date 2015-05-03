package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sinapsi.model.impl.Device;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.webservice.db.DatabaseManager;

/**
 * Servlet implementation class RegisterDeviceServlet
 */
@WebServlet("/register_device")
public class RegisterDeviceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            if(!db.checkDevice(name, model, idUser)) {
                Device device = (Device) db.newDevice(name, model, type, idUser, version);
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
