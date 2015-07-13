package com.sinapsi.webservice.web.dashboard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sinapsi.model.UserInterface;
import com.sinapsi.utils.Pair;
import com.sinapsi.webservice.db.UserDBManager;

/**
 * Servlet implementation class WebCharts
 */
@WebServlet("/web_charts")
public class WebCharts extends HttpServlet {
   private static final long serialVersionUID = 1L;

   /**
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
    *      response)
    */
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      // getting number of log files
      File folder = new File("/var/log/tomcat7");
      File[] listOfFiles = folder.listFiles();
      HttpSession session = request.getSession();
      UserDBManager userManager = (UserDBManager) getServletContext().getAttribute("users_db");

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

         if (user.getRole().equals("user")) {
            session.setAttribute("role", "user");
            request.getRequestDispatcher("charts.jsp").forward(request,
                  response);
            return;
         }
         if (user.getRole().equals("admin"))
            session.setAttribute("role", "admin");
      } catch (SQLException e1) {
         e1.printStackTrace();
      }

      Vector<Pair<String, String>> files = new Vector<Pair<String, String>>(listOfFiles.length);
      for (int i = 0; i < listOfFiles.length; ++i) {
         if (listOfFiles[i].isFile()) {
            if (listOfFiles[i].getName().substring(0, 9).equals("localhost")) {
               // count number of line
               BufferedInputStream is = new BufferedInputStream(
                     new FileInputStream(listOfFiles[i]));
               int count = 0;
               try {
                  byte[] c = new byte[1024];
                  int readChars = 0;
                  boolean empty = true;
                  while ((readChars = is.read(c)) != -1) {
                     empty = false;
                     for (int ii = 0; ii < readChars; ++ii) {
                        if (c[ii] == '\n') {
                           ++count;
                        }
                     }
                  }
                  if (count == 0 && !empty)
                     count = 1;

               } finally {
                  is.close();
               }

               // get date
               String filename = listOfFiles[i].getName();
               String date = filename.substring(filename.indexOf('.') + 1,
                     filename.length() - 4);

               // get date in milliseconds
               DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
               Date d;

               try {
                  d = dateFormat.parse(date);
                  Long dateM = d.getTime();
                  files.add(new Pair<String, String>(dateM.toString(), Integer.toString(count))); 
               } catch (ParseException e) {
                  e.printStackTrace();
               }

            }
         }
      }
      
      // order data
      Collections.sort(files, new Comparator<Pair<String, String>>() {
         public int compare(Pair<String, String> result1,
               Pair<String, String> result2) {
            return result1.getFirst().compareTo(result2.getFirst());
         }
      });
      
      // remove duplicate
      for(int i = 1; i < files.size(); ++i) {
         if(files.get(i-1).getFirst().equals(files.get(i).getFirst())) {
            files.remove(i-1);
         }
      }
      
      session.setAttribute("server_load", files);
      request.getRequestDispatcher("charts.jsp").forward(request, response);
   }

   /**
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
    *      response)
    */
   protected void doPost(HttpServletRequest request,
         HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}
