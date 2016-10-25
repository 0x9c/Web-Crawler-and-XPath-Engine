package test.edu.upenn.cis455;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.User;
import junit.framework.TestCase;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;

public class DBWrapperTest extends TestCase{
	private DBWrapper db;
	private static String envDir = "./testDB";
	private User user = new User("Zach", "password");
	
	@BeforeClass
	public void setUp(){
	    db = new DBWrapper(envDir);
   }

   @Test
   public void testAddUser(){
	  db.addData(user);
	  assertTrue(db.getUser("Zach").getUserName().equals("Zach"));
   }
   
   @Test
   public void testLogin(){
	  assertTrue(db.getUser("Zach").getPassword().equals("password"));
   }
   
   @Test
   public void testAddWebPage(){
      db.putPage("http://crawltest.cis.upenn.edu/", new byte[0], "text/html");
      assertTrue(db.getWebpage("http://crawltest.cis.upenn.edu/").getContent().length == 0);
   }
   
   @AfterClass
   public void clean() {
	   File file = new File(envDir);
	   file.deleteOnExit();
   }
}
