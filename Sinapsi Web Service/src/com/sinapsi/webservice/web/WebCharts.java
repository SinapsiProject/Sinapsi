package com.sinapsi.webservice.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sinapsi.utils.Pair;

/**
 * Servlet implementation class WebCharts
 */
@WebServlet("/web_charts")
public class WebCharts extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// getting number of log files
	    File folder = new File("/var/log/tomcat7");
	    File[] listOfFiles = folder.listFiles();
	    HttpSession session = request.getSession();
	    Vector<Pair<Long, Integer>> files = new Vector<Pair<Long, Integer>>();
	    
	    for(int i = 0; i < listOfFiles.length; ++i) {
	        if(listOfFiles[i].isFile()) { 
	            if(listOfFiles[i].getName().substring(0, 9).equals("localhost")) {
	             // count number of line
	                BufferedInputStream is = new BufferedInputStream(new FileInputStream(listOfFiles[i]));
	                int count = 0;
	                try {
	                    byte[] c = new byte[1024];
	                    int readChars = 0;
	                    boolean empty = true;
	                    while((readChars = is.read(c)) != -1) {
	                        empty = false;
	                        for(int ii = 0; ii < readChars; ++ii) {
	                            if(c[ii] == '\n') {
	                                ++count;
	                            }
	                        }
	                    }
	                    if(count == 0 && !empty) 
	                        count = 1;

	                } finally {
	                    is.close();
	                }
	                
	                // get date
	                String filename = listOfFiles[i].getName();
                    String date = filename.substring(filename.indexOf('.') + 1, filename.length() - 4);
                    
                    // get date in milliseconds
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date d;

                        try {
                            d =  dateFormat.parse(date);
                            files.add(new Pair<Long, Integer>(d.getTime(), count));  
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        
                                     
	            }    	            
	        }
	    }
	    session.setAttribute("server_load", files);
        request.getRequestDispatcher("charts.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doGet(request, response);
	}

}
