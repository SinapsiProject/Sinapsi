package com.sinapsi.webservice.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.sinapsi.utils.Pair;

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
		String dateFilter = request.getParameter("filter_text");
		
		switch (type) {
            case "tomcat": 
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String dayliLog = "/var/log/tomcat7/localhost_access_log." + dateFormat.format(date) + ".txt";
                    
                    // filter enabled
                    if(dateFilter != "" && dateFilter != null) {
                        // check if exist the file
                        File f = new File("/var/log/tomcat7/localhost_access_log." + dateFilter + ".txt");
                        
                        if(f.exists()) {
                            dayliLog = "/var/log/tomcat7/localhost_access_log." + dateFilter + ".txt";
                        } else {
                            dayliLog = "/var/log/sinapsi/empty_file.log";
                        }
                        
                    }
                    
                    FileInputStream fstram = new FileInputStream(new File(dayliLog));
                    HttpSession session = request.getSession();
                    BufferedReader brr = (BufferedReader) session.getAttribute("log_buffer");
                    
                    if(brr == null)
                        brr = new BufferedReader(new InputStreamReader(fstram));
                    
                    session.setAttribute("log_buffer", new Pair<BufferedReader, String>(brr, "tomcat"));
                    request.getRequestDispatcher("log.jsp").forward(request, response);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;

            case "catalina":
                try {
                    FileInputStream fstram = new FileInputStream(new File("/var/log/tomcat7/catalina.out"));
                    HttpSession session = request.getSession();
                    BufferedReader brr = (BufferedReader) session.getAttribute("log_buffer");
                    
                    if(brr == null)
                        brr = new BufferedReader(new InputStreamReader(fstram));
                    
                    session.setAttribute("log_buffer", new Pair<BufferedReader, String>(brr, "catalina"));
                    request.getRequestDispatcher("log.jsp").forward(request, response);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
                
            case "db":
                try {
                    FileInputStream fstram = new FileInputStream(new File("/var/log/postgresql/postgresql-9.1-main.log"));
                    HttpSession session = request.getSession();
                    BufferedReader brr = (BufferedReader) session.getAttribute("log_buffer");
                    
                    if(brr == null)
                        brr = new BufferedReader(new InputStreamReader(fstram));
                    
                    session.setAttribute("log_buffer", new Pair<BufferedReader, String>(brr, "db"));
                    request.getRequestDispatcher("log.jsp").forward(request, response);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
                
            case "ws":
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String dayliLog = "/var/log/sinapsi/web_socket." + dateFormat.format(date) + ".log";
                    
                    // filter enabled
                    if(dateFilter != "") {
                        // check if exist the file
                        File f = new File("/var/log/sinapsi/web_socket." + dateFilter + ".log");
                        
                        if(f.exists()) {
                            dayliLog = "/var/log/sinapsi/web_socket." + dateFilter + ".log";
                        } else {
                            dayliLog = "/var/log/sinapsi/empty_file.log";
                        }
                    }
                    
                    FileInputStream fstram = new FileInputStream(new File(dayliLog));
                    HttpSession session = request.getSession();
                    BufferedReader brr = (BufferedReader) session.getAttribute("log_buffer");
                    
                    if(brr == null)
                        brr = new BufferedReader(new InputStreamReader(fstram));
                    
                    session.setAttribute("log_buffer", new Pair<BufferedReader, String>(brr, "ws"));
                    request.getRequestDispatcher("log.jsp").forward(request, response);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            
            case "webs":
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String dayliLog = "/var/log/sinapsi/web_service." + dateFormat.format(date) + ".log";
                    
                    // filter enabled
                    if(dateFilter != "") {
                        // check if exist the file
                        File f = new File("/var/log/sinapsi/web_service." + dateFilter + ".log");
                        
                        if(f.exists()) {
                            dayliLog = "/var/log/sinapsi/web_service." + dateFilter + ".log";
                        } else {
                            dayliLog = "/var/log/sinapsi/empty_file.log";
                        }
                    }
                    
                    FileInputStream fstram = new FileInputStream(new File(dayliLog));
                    HttpSession session = request.getSession();
                    BufferedReader brr = (BufferedReader) session.getAttribute("log_buffer");
                    
                    if(brr == null)
                        brr = new BufferedReader(new InputStreamReader(fstram));
                    
                    session.setAttribute("log_buffer", new Pair<BufferedReader, String>(brr, "webs"));
                    request.getRequestDispatcher("log.jsp").forward(request, response);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
                
        }
	}

}
