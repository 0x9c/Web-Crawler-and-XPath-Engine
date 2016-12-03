package test.edu.upenn.cis455.Crawler;

import org.apache.log4j.BasicConfigurator;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.upenn.cis455.storage.DBWrapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RunAllTests extends TestCase 
{
  public static Test suite() 
  {
    try {
      
        BasicConfigurator.configure();
        Class[]  testClasses = {
        /* TODO: Add the names of your unit test classes here */
    		  Class.forName("test.edu.upenn.cis455.Crawler.RobotTest"),
    		  Class.forName("test.edu.upenn.cis455.Crawler.DBWrapperTest"),
    		  Class.forName("test.edu.upenn.cis455.Crawler.ClientTest"),
    		  Class.forName("test.edu.upenn.cis455.Crawler.XPathCrawlerTest"),
    		  Class.forName("test.edu.upenn.cis455.Crawler.PageDownloaderTest"),
      };   
      
      return new TestSuite(testClasses);
    } catch(Exception e){
      e.printStackTrace();
    } 
    
    return null;
  }
}
