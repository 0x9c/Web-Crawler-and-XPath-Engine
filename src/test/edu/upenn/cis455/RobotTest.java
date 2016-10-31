package test.edu.upenn.cis455;
import edu.upenn.cis455.crawler.RobotCache;
import edu.upenn.cis455.crawler.info.Client;
import edu.upenn.cis455.crawler.info.Robot;
import junit.framework.TestCase;

import org.junit.*;
import static org.junit.Assert.*;



public class RobotTest extends TestCase{
	@Before
	public void setUp() {
		RobotCache.addRobot("http://crawltest.cis.upenn.edu");
	}
	@Test
	public void testAllowed() {
		
		assertTrue(RobotCache.isValid("http://crawltest.cis.upenn.edu"));
		assertFalse(RobotCache.isValid("http://crawltest.cis.upenn.edu/marie/private/page"));		
	}
	
	@Test
	public void testDelay() {
		RobotCache.setCurrentTime("http://crawltest.cis.upenn.edu");
		assertFalse(RobotCache.checkDelay("http://crawltest.cis.upenn.edu"));
	}
}
