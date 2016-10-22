package edu.upenn.cis455.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.user.User;

public class XCrawlerRegister extends HttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//Check if Database contains the user and then check if the password matches
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = new DBWrapper(storeLocation);
		
		String userName = req.getParameter("username");
		String password = req.getParameter("password");
		User user = dbStore.getUser(userName);
		if( user != null && !user.getPassword().equals(password) ) {
			resp.sendRedirect("invalid");
		} else if( user != null && user.getPassword().equals(password)) {
			HttpSession session = req.getSession();
			session.setAttribute("username", userName);
			resp.sendRedirect("home");
			System.out.println("User logged in successfully");
		} else {
			// user == null 
			if(userName.isEmpty() || password.isEmpty())
				/* We can't create User using invalid username or password */
				resp.sendRedirect("invalid");
			else {
				// If the User doesn't exist, create a new one immediately. 
				HttpSession session = req.getSession();
				user = new User(userName, password);
				dbStore.addData(user);
				session.setAttribute("username", userName);
				resp.sendRedirect("home");		
				System.out.println("New User created");
			}
		}
		dbStore.close();
	}
}
