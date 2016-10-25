package edu.upenn.cis455.servlet;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;

/**
 * Servlet to match both "/" and "/xpath" pattern.
 * @author cis555
 *
 */
@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	
	/* TODO: Implement user interface for XPath engine here */
	
	/* You may want to override one or both of the following methods */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		/* TODO: Implement user interface for XPath engine here */

		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.sendRedirect("login");
	}

}









