package edu.upenn.cis455.xpathengine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XPathEngineImpl implements XPathEngine {
	  XQueryPool queryPool;
	  SAXParserFactory factory;
	  
	  public XPathEngineImpl() {
	    // Do NOT add arguments to the constructor!!
		  super();
		  factory = SAXParserFactory.newInstance();

	  }
		
	  public void setXPaths(String[] xpathExpressions) {
		  queryPool = new XQueryPool(xpathExpressions);
	  }
	
	  public boolean isValid(int i) {
		  XQuery[] querypool = queryPool.getPool();
		  if(querypool.length < i+1){
			  return false; 
		  } else{
			  return querypool[i].isValid();
		  }
	  }
		
	  public boolean[] evaluate(Document d) { 
	    /* TODO: Check whether the document matches the XPath expressions */
	    return null; 
	  }
	
	@Override
	public boolean isSAX() {
		return true;
	}
	
	@Override
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler) {
		try {
			SAXHandler saxhandler = (SAXHandler)handler;
			saxhandler.setPool(queryPool);
			
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(document, saxhandler);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		boolean[] res = new boolean[queryPool.getSize()];
		for(int i = 0; i < res.length; i++) {
			res[i] = isValid(i);
		}
		return res;
	}
     
	
	public static void main(String[] args) throws IOException {
		InputStream xmlInput = new FileInputStream("./res/Science.xml");
		XPathEngineImpl xpathEngine = (XPathEngineImpl)XPathEngineFactory.getXPathEngine();
		DefaultHandler handler = XPathEngineFactory.getSAXHandler();
		String[] expressions = new String[]{"/rss/channel/image[link[text()=\"http://www.nytimes.com/pages/science/index.html\"]]"};
		xpathEngine.setXPaths(expressions);
		boolean[] res = xpathEngine.evaluateSAX(xmlInput, handler);
		System.out.println(Arrays.toString(res));
	}
}
