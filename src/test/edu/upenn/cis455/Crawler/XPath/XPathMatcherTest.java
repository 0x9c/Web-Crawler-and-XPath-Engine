package test.edu.upenn.cis455.Crawler.XPath;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import junit.framework.TestCase;

public class XPathMatcherTest extends TestCase{
	InputStream xmlInput;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		xmlInput = new FileInputStream("./res/Science.xml");
	}

	@Test
	public void test() {
		XPathEngineImpl xpathEngine = (XPathEngineImpl)XPathEngineFactory.getXPathEngine();
		DefaultHandler handler = XPathEngineFactory.getSAXHandler();
		String[] expressions = new String[]{"/rss", "/rss/channel", "/rss/channel/item[title[contains(text(), \"In Energy Work, One Hand Giveth and the Other Taketh\")]]"};
		xpathEngine.setXPaths(expressions);
		
		boolean[] res = xpathEngine.evaluateSAX(xmlInput, handler);
		boolean[] trues = new boolean[res.length];
		Arrays.fill(trues, true);
		assertTrue(Arrays.equals(res, trues));
	}

}
