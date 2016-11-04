package test.edu.upenn.cis455.Crawler;
import edu.upenn.cis455.crawler.Processor;
import edu.upenn.cis455.crawler.info.Client;
import junit.framework.TestCase;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;

public class ClientTest extends TestCase{
	private Client client;
	
	@Before
	public void setUp() {
		client = new Client("http://crawltest.cis.upenn.edu");
	}
	
	@Test
	public void testGETRequest() {
		List<String> links = Processor.extractLink(client.getUrl());
		assertEquals(13, links.size());
	}
	
	@Test
	public void testHeadRequest() {
		client.executeGET(false);
		assertTrue(client.getContentType().equalsIgnoreCase("text/html"));
	}

}
