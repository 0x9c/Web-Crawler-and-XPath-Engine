package test.edu.upenn.cis455.Crawler;

import java.util.List;

import edu.upenn.cis455.crawler.Processor;
import edu.upenn.cis455.crawler.RobotCache;
import edu.upenn.cis455.crawler.URLFrontierQueue;
import junit.framework.TestCase;

public class XPathCrawlerTest extends TestCase {
	String url;
	URLFrontierQueue urlFrontier;
	
	protected void setUp() throws Exception {
		super.setUp();
		url = "http://crawltest.cis.upenn.edu/";
		urlFrontier = new URLFrontierQueue(10);
	}
	
	public void testProcessor(){
		List<String> links = Processor.extractLink(url);
		assertEquals(links.contains("http://crawltest.cis.upenn.edu/nytimes/"), true);
		assertEquals(links.contains("http://crawltest.cis.upenn.edu/bbc/"), true);
		assertEquals(links.contains("http://crawltest.cis.upenn.edu/cnn/"), true);
		assertEquals(links.contains("http://crawltest.cis.upenn.edu/international/"), true);
	}
	
	public void testRobotManager(){
		RobotCache.addRobot(url);
		assertEquals(RobotCache.isValid("http://crawltest.cis.upenn.edu/marie/private/"), true);
		assertEquals(RobotCache.isValid("http://crawltest.cis.upenn.edu/foo/abc"), false);
		assertEquals(RobotCache.isValid("http://crawltest.cis.upenn.edu/marie/private/abc"), false);
		assertEquals(RobotCache.isValid("http://crawltest.cis.upenn.edu"), true);		
	}
	
	public void testURLFrontier(){
		urlFrontier.pushURL(url);
		assertEquals(urlFrontier.filter("http://crawltest.cis.upenn.edu/marie/private"), true);
	}
}
