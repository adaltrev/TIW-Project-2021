package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import utils.*;
import dao.*;
import beans.*;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session= request.getSession(true);
		String username = null;
		String password = null;
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("userName"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		// query to database to check the credentials
		UserDao userDao = new UserDao(connection);
		User user = null;
		try {
			user = userDao.checkCredentials(username, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server unavailable, not possible to check credentials");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (user == null) {
			session.setAttribute("errorMsg","Incorrect Username or Password, try again" );
			path = getServletContext().getContextPath() + "/FailedLogin";
			response.sendRedirect(path);
		} else {
			session.setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			session.setAttribute("cart", new HashMap<Integer,List<CartProduct>>());//the hashMap is never null if the user is logged 
			response.sendRedirect(path);
		}

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}