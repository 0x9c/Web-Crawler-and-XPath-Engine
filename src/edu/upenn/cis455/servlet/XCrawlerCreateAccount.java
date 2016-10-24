package edu.upenn.cis455.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;

public class XCrawlerCreateAccount extends HttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		//Check if user already exists
		
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = new DBWrapper(storeLocation);
		
		//Check if user already exists
		String userName = req.getParameter("username");
		String password = req.getParameter("password");
		
		if(userName.isEmpty() || password.isEmpty() || userName.equals("admin"))
				res.sendRedirect("login");
		else
		{
			User user = dbStore.getUser(userName);
			
			if(user==null)
			{
				//New account, create database entry
				HttpSession session = req.getSession();
				user = new User(userName, password);
				dbStore.addData(user);
				dbStore.close();
				session.setAttribute("username", userName);
				res.sendRedirect("login");
				
			}
			else
			{
				//Send user already exists
				res.sendRedirect("accountexists");
			}
		
		}
	}
}
