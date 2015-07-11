package com.sinapsi.webservice.web.dashboard;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sinapsi.engine.Action;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.engine.WebServiceEngine;

/**
 * Servlet implementation class WebMacroManager
 */
@WebServlet("/web_macro_manager")
public class WebMacroManager extends HttpServlet {
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
        EngineDBManager engineManager = (EngineDBManager) getServletContext().getAttribute("engines_db"); 
        WebServiceEngine engine = (WebServiceEngine) getServletContext().getAttribute("engine");
        UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
        HttpSession session = request.getSession();
	    
        String email = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("user")) 
                    email = cookie.getValue();
            }
        }
        if(email == null) {
            request.getRequestDispatcher("login.html").forward(request, response);
            return;
        }
        
        try {
            UserInterface user = userManager.getUserByEmail(email);
            
            if(user == null) {
                request.getRequestDispatcher("login.html").forward(request, response);
                return;
            }
            
            String actionPar = request.getParameter("action");
            String idMacro = request.getParameter("macro");
            
            if(idMacro != null && actionPar.equals("delete")) {
                // delete macro
                try {
                    engineManager.deleteUserMacro(Integer.parseInt(idMacro));
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
            
            // get the list of macro from the db
            List<MacroInterface> macros = engineManager.getUserMacro(user.getId(), engine.getComponentFactoryForUser(user.getId()));
            Map<Integer, String> devices = new HashMap<Integer, String>();
            Map<Integer, String> devicesTriggered = new HashMap<Integer, String>();
            
            for(MacroInterface macro : macros) {
                int deviceTrigger = macro.getTrigger().getExecutionDevice().getId();
                devicesTriggered.put(deviceTrigger, deviceManager.getDevice(deviceTrigger).getModel());
                
                for(Action action : macro.getActions()) {
                    int idDevice = action.getExecutionDevice().getId();
                    devices.put(idDevice, deviceManager.getDevice(idDevice).getModel());
                }
            }
           
            if (user.getRole().equals("user")) 
               session.setAttribute("role", "user");

            if (user.getRole().equals("admin")) 
               session.setAttribute("role", "admin");
            
            session.setAttribute("macros", macros);
            session.setAttribute("triggeredDevice", devicesTriggered);
            session.setAttribute("devices", devices);
            
        } catch(SQLException e) {
            e.printStackTrace();
        }
	    request.getRequestDispatcher("macro_manager.jsp").forward(request, response);
	}

}
