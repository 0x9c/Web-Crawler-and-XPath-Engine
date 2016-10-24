package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;

public class XCrawlerUser extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		String username = (String)session.getAttribute("username");
		
		PrintWriter out = res.getWriter();

		out.println("<HTML>");
		out.println("<body>");
		out.println("<center>");
		out.println("<p><h3>Welcome</h3></p>");
		out.println("<p><h3>" + username + "</h3></p>");
		out.println("<form action=\"logout\" method=\"GET\">");
		out.println("<input type=\"submit\" value=\"Logout\">");
		out.println("</form>");
		out.println("</center>");
		out.println("</body>");
		out.println("</HTML>");
		
	}

}
