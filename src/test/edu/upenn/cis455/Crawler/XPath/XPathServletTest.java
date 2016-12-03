package test.edu.upenn.cis455.Crawler.XPath;

import junit.framework.TestCase;

import junit.framework.TestCase;
import static org.mockito.Matchers.*;

import static org.powermock.api.mockito.PowerMockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.powermock.api.mockito.PowerMockito.*;
import org.mockito.Mockito;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import edu.upenn.cis455.servlet.*;
import edu.upenn.cis455.storage.*;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBWrapper.class })
public class XPathServletTest extends TestCase{
	private HttpServletResponse response;
	private HttpServletRequest request;
	private DBWrapper db;
	private PrintWriter out;
	private XCrawlerShow show;
	private HttpSession session;
	private User user;
	private Channel channel;
	private Page webPage;
	private static final String USERNAME = "username";
	private static final String CHANNEL = "channel";
	private static final String URL = "URL";
	
	@Test
	public void testUsernameIsNUll() throws Exception{
		//given
		show = spy(new XCrawlerShow());
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		out = mock(PrintWriter.class);
		session = mock(HttpSession.class);
		//when
		doReturn(null).when(session).getAttribute("username");
		doReturn(session).when(request).getSession();
		doNothing().when(out).println(anyString());
		doReturn(out).when(response).getWriter();
		//call
		Whitebox.invokeMethod(show, "doGet", request, response);
		//then
		Mockito.verify(response).sendRedirect("/login");;    
	}
	
	@Test
	public void testShowNotContains() throws Exception{
		//given
		user = spy(new User("name", "password"));
		//user.setSubscribe(new ArrayList<String>());
		show = spy(new XCrawlerShow());
		db = mock(DBWrapper.class);
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		out = mock(PrintWriter.class);
		session = mock(HttpSession.class);
		ServletContext context = mock(ServletContext.class);
		mockStatic(DBWrapper.class);
		List<String> list = mock(ArrayList.class);
		
		//when
		doReturn(context).when(show).getServletContext();
		doReturn("storeLocation").when(context).getInitParameter("BDBstore");
		doReturn(user).when(db).getUser("name");
		Mockito.when(DBWrapper.getInstance(anyString())).thenReturn(db);
		doReturn(USERNAME).when(session).getAttribute(USERNAME);
		
		doReturn(CHANNEL).when(request).getParameter(CHANNEL);
		doReturn(session).when(request).getSession();
		doReturn(list).when(user).getSubscribe();
		doReturn(false).when(list).contains(CHANNEL);
		
		doNothing().when(out).println(anyString());
		doReturn(out).when(response).getWriter();
	
		Whitebox.invokeMethod(show, "doGet", request, response);
		//then
		Mockito.verify(response).sendRedirect("/login");    
		
	}
}
