package com.sinapsi.webservice.dashboard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.websocket.Server;

/**
 * Servlet implementation class DashboardServlet
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession();
	    
	    // number of registered users
	    UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");
	    try {
	        session.setAttribute("registered_users", Integer.toString(userManager.getUsers().size()));
        } catch (SQLException e) {
            session.setAttribute("log_buffer", "0");
            e.printStackTrace();
        }
	    
	    // clients connected
	    Server wsserver = (Server) getServletContext().getAttribute("wsserver");
	    session.setAttribute("clients_connected", Integer.toString(wsserver.connections().size()));
	    
	    // server requestes
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dayliLog = "/var/log/tomcat7/localhost_access_log." + dateFormat.format(date) + ".txt";
        
        // count n of line in the dayli log (fastest way to count the number of line. thanks: stackoverflow")
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(new File(dayliLog)));
        int count = 0;
        try {
            byte[] c = new byte[1024];
            int readChars = 0;
            boolean empty = true;
            while((readChars = is.read(c)) != -1) {
                empty = false;
                for(int i = 0; i < readChars; ++i) {
                    if(c[i] == '\n') {
                        ++count;
                    }
                }
            }
            if(count == 0 && !empty) 
                count = 1;

        } finally {
            is.close();
        }
        session.setAttribute("server_requestes", Integer.toString(count));
	    request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
