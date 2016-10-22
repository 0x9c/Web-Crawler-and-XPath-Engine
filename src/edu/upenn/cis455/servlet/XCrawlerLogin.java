package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XCrawlerLogin extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		PrintWriter out = res.getWriter();
		
		out.println("<HTML>");
		out.println("<body>");
		out.println("<center>");
		out.println("<h3>LOGIN PAGE</h3><br><br><br>");
		out.println("<form action=\"register.jsp\" method=\"POST\">");
		out.println("<table>");
		out.println("<tr>");
		out.println("<td>USERNAME :</td><td><input type=\"text\" name=\"username\"></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>PASSWORD :</td><td><input type=\"password\" name=\"password\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<input type=\"submit\" value=\"Submit\">");
		out.println("</form><br><br>");
		out.println("</center>");
		out.println("</body>");
		out.println("</HTML>");
		
	}

}
