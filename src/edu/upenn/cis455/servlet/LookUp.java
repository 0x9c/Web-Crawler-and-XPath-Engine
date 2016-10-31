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
import edu.upenn.cis455.storage.Page;

/**
 * Servlet used to lookup documents by using URL
 * @author cis555
 *
 */
public class LookUp extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper db = new DBWrapper(storeLocation);
		String url = request.getParameter("url");
		Page webPage = db.getWebpage(url);
		PrintWriter out = response.getWriter();
		if(webPage == null) {
			out.println("<html>");
			out.println("404 Not Found");
			out.println("</html>");
		} else {
			String body = new String(webPage.getContent(), "UTF-8");
			out.println(body);
		}
	}
}
