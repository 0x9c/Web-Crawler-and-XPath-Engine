package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.Page;

public class XCrawlerShow extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			res.sendRedirect("/login");
			return;
		}
		
		String channelName = req.getParameter("channel");
		String storeLocation = getServletContext().getInitParameter("BDBstore");
		DBWrapper dbStore = DBWrapper.getInstance(storeLocation);
		Channel channel = dbStore.getChannel(channelName);
		Set<String> matchedURL = (Set<String>) channel.getMatchedURL();
		List<Page> matchPage = new ArrayList<>();
		for(String url: matchedURL) {
			matchPage.add(dbStore.getWebpage(url));
		}
		
		PrintWriter out = res.getWriter();
		
		out.println("<HTML>");
		out.println("<body>");
		out.println("<div class=\"channelheader\">");
		out.println("<b><h3>Channel Name: " + channel.getName() + "<br><h3></b>");
		out.println("<b><h3>Created By: " + channel.getCreatedUser() + "<br><h3></b>");
		out.println("</div>");
		out.println("<br>");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if(matchPage.isEmpty()) {
			out.println("<b><h3>"+ "No Matched Files for this Channel" +"<br><h3></b>");
		}
		for(Page page: matchPage) {
			Date date = new Date(page.getCrawlTime());
			out.println("<div class=\"matchedXML\">");
			out.println("<b><h3>Crawled on: " + format.format(date) + "</b></h3>");
			out.println("<b><h3>Location: " + page.getURL() + "</b></h3>");
			out.println("<div class=\"document\">");
			String content = new String(page.getContent(), "UTF-8");
			out.println(content);
			out.println("</div>");
			out.println("</div>");
			out.println("<br>");
		}
		out.println("<body>");
		out.println("<HTML>");
	}
}
