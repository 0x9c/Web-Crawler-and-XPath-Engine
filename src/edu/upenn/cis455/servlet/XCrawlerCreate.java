package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;

/**
 * Servlet used to create new channels
 * @author cis555
 *
 */
public class XCrawlerCreate extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			res.sendRedirect("/login");
			return;
		}
		
		System.out.println(req.getQueryString());
		String query = req.getQueryString();
		
		
		String channelName = req.getParameter("ChannelName");
		String xpath = req.getParameter("XPath");
		
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = DBWrapper.getInstance(storeLocation);
		User user = dbStore.getUser((String)session.getAttribute("username"));
		
		System.out.println("channelName: " + channelName);
		System.out.println("xpath: " + xpath);
		
		Channel newChannel = new Channel(channelName, xpath, user.getUserName());
		user.addSubscribe(channelName);
		dbStore.updateChannel(newChannel);
		dbStore.updateUser(user);
		
		res.sendRedirect("/");
	}
}
