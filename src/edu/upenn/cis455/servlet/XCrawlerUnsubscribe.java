package edu.upenn.cis455.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;

/**
 * Servlet used to unsubscribe channels from users
 * @author cis555
 *
 */
public class XCrawlerUnsubscribe extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			res.sendRedirect("/login");
			return;
		}
		
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = DBWrapper.getInstance(storeLocation);
		
		String userName = (String)session.getAttribute("username");
		User user = dbStore.getUser(userName);
		String unsubscribe = req.getParameter("channel");
		user.removeSubscribe(unsubscribe);
		dbStore.updateUser(user);
		res.sendRedirect("/user");
	}
}
