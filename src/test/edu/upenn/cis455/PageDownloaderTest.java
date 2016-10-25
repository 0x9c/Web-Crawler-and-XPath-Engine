package test.edu.upenn.cis455;

import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.crawler.PageDownloader;
import edu.upenn.cis455.storage.DBWrapper;
import junit.framework.TestCase;

public class PageDownloaderTest extends TestCase{
	DBWrapper db = new DBWrapper("./dbtest");
	PageDownloader pg = new PageDownloader();
	
	@Before
	public void setUp(){
		pg.setup(db);
	}
	
	@Test
	public void testA(){
		pg.download("http://crawltest.cis.upenn.edu/");
		assertTrue(db.getWebpage("http://crawltest.cis.upenn.edu/").getContent() != null);
	}
	
	@Test
	public void testB(){
		pg.download("http://crawltest.cis.upenn.edu/bbc/");
		assertTrue(db.getWebpage("http://crawltest.cis.upenn.edu/").getType().equals("html"));
	}
}
