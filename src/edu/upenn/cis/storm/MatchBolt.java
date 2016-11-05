package edu.upenn.cis.storm;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.helpers.DefaultHandler;

import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.bolt.IRichBolt;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Tuple;
import edu.upenn.cis.stormlite.tuple.Values;
import edu.upenn.cis455.crawler.PageDownloader;
import edu.upenn.cis455.crawler.Processor;
import edu.upenn.cis455.crawler.RobotCache;
import edu.upenn.cis455.crawler.URLFrontierQueue;
import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class MatchBolt implements IRichBolt{
	static Logger log = Logger.getLogger(MatchBolt.class);
	
	Fields schema = new Fields("URLStream");
	
	String executorId = UUID.randomUUID().toString();
	
	private OutputCollector collector;
	
	private URLFrontierQueue urlQueue;
	
	private DBWrapper dbStore;
	
    public MatchBolt() {
    	log.debug("Starting MatchBolt");
    	this.dbStore = DBWrapper.getInstance(XPathCrawler.dbPath);
    }
    
    /**
     * Used to set DB instance
     * @param instance
     */
    public void setdbStore(DBWrapper instance) {
    	this.dbStore = instance;
    }
    
    /**
     * Used for debug purposes, shows our exeuctor/operator's unique ID
     */
	@Override
	public String getExecutorId() {
		return executorId;
	}
	
    /**
     * Lets the downstream operators know our schema
     */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(schema);
	}
	
    /**
     * Shutdown, just frees memory
     */
	@Override
	public void cleanup() {}
	
    /**
     * Process a tuple received from the stream, incrementing our
     * counter and outputting a result
     * @throws UnsupportedEncodingException 
     */
	@Override
	public void execute(Tuple input){
		String url = input.getStringByField("URL");
		String type = input.getStringByField("type");
		Document doc = (Document)input.getObjectByField("document");
		List<String> linklist = (List<String>) input.getObjectByField("URLStream");
		collector.emit(new Values<Object>(linklist));
		
		log.info("TYPE: " + type);
		if(!type.contains("xml")) return;
		List<Channel> channels = dbStore.getAllChannels();
		String[] xpaths = new String[channels.size()];
		for(int i = 0; i < channels.size(); i++) {
			xpaths[i] = channels.get(i).getXPath();
		}
		
		XPathEngineImpl xpathEngine = (XPathEngineImpl)XPathEngineFactory.getXPathEngine();
		DefaultHandler handler = XPathEngineFactory.getSAXHandler();
		xpathEngine.setXPaths(xpaths);
		InputStream xmlInput;
		try {
			xmlInput = new ByteArrayInputStream(doc.toString().getBytes("UTF-8"));
			boolean[] res = xpathEngine.evaluateSAX(xmlInput, handler);
			for(int i = 0; i < res.length; i++) {
				if(res[i]) {
					Channel c = channels.get(i);
					c.addMatchedURL(url);
					dbStore.updateChannel(c);
					log.info(url + "====>>> Matched with ====>>" + c.getName());
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	
    /**
     * Initialization, just saves the output stream destination
     */
	@Override
	public void prepare(Map<String, String> stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}
	
	/**
	 * Called during topology setup, sets the router to the next
	 * bolt
	 */
	@Override
	public void setRouter(IStreamRouter router) {
		this.collector.setRouter(router);
	}


	/**
	 * The fields (schema) of our output stream
	 */
	@Override
	public Fields getSchema() {
		return schema;
	}
}
