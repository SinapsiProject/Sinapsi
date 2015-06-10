package com.sinapsi.webservice.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WebLog
 */
@WebServlet("/web_log")
public class WebLog extends HttpServlet {
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
		String type = request.getParameter("type");
		
		switch (type) {
            case "tomcat":
                request.getRequestDispatcher("tomcat_log.jsp").forward(request, response);
                break;

            case "db":
                request.getRequestDispatcher("db_log.jsp").forward(request, response);
                break;
                
            case "ws":
                request.getRequestDispatcher("ws_log.jsp").forward(request, response);
                break;
        }
	}

}
