package edu.upenn.cis455.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.user.User;

public class XCrawlerAuthenticate extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//Check if Database contains the user and then check if the password matches
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = new DBWrapper(storeLocation);
		
		String userName = req.getParameter("username");
		String password = req.getParameter("password");
		User user = dbStore.getUser(userName);
		dbStore.close();
		if(userName!=null && userName.equals("admin") && password!=null && password.equals("password"))
		{
			HttpSession session = req.getSession();
			session.setAttribute("username", userName);
			resp.sendRedirect("admin");
		}
		else if(user!=null && user.getPassword().equals(password))
		{
			
			//Valid Login redirect to home page
			HttpSession session = req.getSession();
			session.setAttribute("username", userName);
			resp.sendRedirect("home");
		}
		else
		{
			resp.sendRedirect("invalid");
		}
	
	}
}
