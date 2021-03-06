package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;

/**
 * User Page if user login successfully.
 * @author cis555
 *
 */
public class XCrawlerUser extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			res.sendRedirect("/login");
			return;
		}
		
		String username = (String)session.getAttribute("username");
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = DBWrapper.getInstance(storeLocation);
		List<Channel> channels = dbStore.getAllChannels();
		User user = dbStore.getUser(username);
		
		PrintWriter out = res.getWriter();

		out.println("<HTML>");
		out.println("<body>");
		out.println("<center>");
		out.println("<p><h3>Welcome</h3></p>");
		out.println("<p><h3>" + username + "</h3></p>");
		out.println("<form action=\"/\" method=\"GET\">");
		out.println("<input type=\"submit\" value=\"View All Channels\">");
		out.println("</form>");
		out.println("<br>");
		out.println("<h3><b>My Subscription<b></h3><br>");
		out.println("<table>");
		for(Channel channel : channels) {
			if(user.getSubscribe().contains(channel.getName())) {
				out.println("<tr>");
				out.println("<td>Channel: </td>" + "<td>"+ channel.getName() + "</td>");
				out.println("<td>");
 				out.println("<a href = \"/show?channel=" + channel.getName() + "\">View</a>");
 				out.println("</td>");
 				out.println("<td>");
 				out.println("<a href = \"/unsubscribe?channel=" + channel.getName() + "\">Unsubscribe</a>");
 				out.println("</td>");
 				out.println("</tr>");
 			} 
		}
		out.println("</table>");
		
		out.println("<form action=\"logout\" method=\"GET\">");
		out.println("<input type=\"submit\" value=\"Logout\">");
		out.println("</form>");
		
		out.println("</center>");
		out.println("</body>");
		out.println("</HTML>");
		
	}

}
