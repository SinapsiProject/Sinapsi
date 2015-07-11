package com.sinapsi.webservice.web.dashboard;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sinapsi.model.UserInterface;
import com.sinapsi.webservice.db.UserDBManager;

/**
 * Servlet implementation class WebMacroEditor
 */
@WebServlet("/web_macro_editor")
public class WebMacroEditor extends HttpServlet {
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
            request.getRequestDispatcher("macro_editor.jsp").forward(request,response);
            return;
         }
         if (user.getRole().equals("admin")) {
            session.setAttribute("role", "admin");
            request.getRequestDispatcher("macro_editor.jsp").forward(request,response);
            return;
         }
      } catch (SQLException e1) {
         e1.printStackTrace();
      }
	}

}
