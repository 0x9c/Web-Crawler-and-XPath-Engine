package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that will be redirected when the user has already exists.
 * @author dongtianxiang
 *
 */
public class XCrawlerAccountExists extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PrintWriter out = resp.getWriter();
				
		out.println("<HTML>");
		out.println("<body>");
		out.println("<center>");
		out.println("<h3 style=\"color:red\">ACCOUNT ALREADY EXISTS</h3><br><br><br>");
		out.println("<form action=\"createaccount\" method=\"POST\">");
		out.println("<table>");
		out.println("<tr>");
		out.println("<td>USERNAME :</td><td><input type=\"text\" name=\"username\"></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>PASSWORD :</td><td><input type=\"text\" name=\"password\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<input type=\"submit\" value=\"CREATE ACCOUNT\">");
		out.println("</form><br><br>");
		out.println("<a href=\"login\">Back to login</a>");
		out.println("</center>");
		out.println("</body>");
		out.println("</HTML>");
	}
}
