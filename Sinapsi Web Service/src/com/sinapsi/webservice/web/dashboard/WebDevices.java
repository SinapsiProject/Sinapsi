package com.sinapsi.webservice.web.dashboard;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.websocket.Server;

/**
 * Servlet implementation class WebDevices
 */
@WebServlet("/web_devices")
public class WebDevices extends HttpServlet {
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
	   HttpSession session = request.getSession();
      UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
      DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
      Server wsserver = (Server) getServletContext().getAttribute("wsserver");
      
      String email = null;
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
         for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user"))
               email = cookie.getValue();
         }
      }
      if (email == null) {
         request.getRequestDispatcher("login.html").forward(request, response);
         return;
      }

      try {
         UserInterface user = userManager.getUserByEmail(email);

         if (user == null) {
            request.getRequestDispatcher("login.html").forward(request,
                  response);
            return;
         }

         if (user.getRole().equals("user")) 
            session.setAttribute("role", "user");

         if (user.getRole().equals("admin")) 
            session.setAttribute("role", "admin");

         Map<DeviceInterface, Boolean> devicesConn = new HashMap<DeviceInterface, Boolean>();
         
         for(DeviceInterface device :  deviceManager.getUserDevices(email)) {
            devicesConn.put(device, wsserver.isDeviceOnline(device.getId()));
         }
         
         session.setAttribute("devices", devicesConn);
         request.getRequestDispatcher("devices.jsp").forward(request, response);

      } catch (SQLException e1) {
         e1.printStackTrace();
      }
	}

}
