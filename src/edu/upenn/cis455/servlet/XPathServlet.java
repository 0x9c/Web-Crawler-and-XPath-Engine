package edu.upenn.cis455.servlet;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.Channel;
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
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			res.sendRedirect("/login");
			return;
		}
		
		String userName = (String)session.getAttribute("username");
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = DBWrapper.getInstance(storeLocation);
		List<Channel> channels = dbStore.getAllChannels();
		User user = dbStore.getUser(userName);
		
		PrintWriter out = res.getWriter();
		
		out.println("<HTML>");
		out.println("<body>");
		out.println("<center>");
		out.println("<h3>Welcome " + userName + "</h3><br><br>");
		
		out.println("<form action=\"/create\" method=\"GET\">");
		out.println("<table>");
		out.println("<tr>");
		out.println("<td>Channel :</td><td><input type=\"text\" name=\"ChannelName\" id=\"ChannelName\"></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>XPath :</td><td><input type=\"text\" name=\"XPath\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<input type=\"submit\" value=\"Create\">");
		out.println("</form><br><br>");
		
		out.println("<h3><b>All Channels Available<b></h3><br>");
		out.println("<table>");
		for(Channel channel : channels) {
			out.println("<tr>");
			out.println("<td>Channel: </td>" + "<td>"+ channel.getName() + "</td>");
			if(user.getSubscribe().contains(channel.getName())) {
				out.println("<td>");
 				out.println("<a href = \"/show?channel=" + channel.getName() + "\">View</a>");
 				out.println("</td>");
 			} else {
 				out.println("<td>");
 				out.println("<a href = \"/subscribe?channel=" + channel.getName() + "\">Subscribe</a>");
 				out.println("</td>");
 			}
			out.println("</tr>");
		}
		out.println("</table>");
		out.println("</center>");
		out.println("</body>");
		out.println("</HTML>");
	}

}









