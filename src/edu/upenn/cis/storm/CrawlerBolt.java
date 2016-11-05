package edu.upenn.cis.storm;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

/**
 * Bolt Component used to connect URL and get the document
 * @author cis555
 *
 */
public class CrawlerBolt implements IRichBolt{
	static Logger log = Logger.getLogger(CrawlerBolt.class);
	
	Fields schema = new Fields("url", "document", "type");
	
	String executorId = UUID.randomUUID().toString();
	
	private OutputCollector collector;
	
	private URLFrontierQueue urlQueue;
	
    public CrawlerBolt() {
    	log.debug("Starting CrawlerBolt");
    	this.urlQueue = XPathCrawler.urlQueue;
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
     */
	@Override
	public void execute(Tuple input) {
		String curURL = input.getStringByField("URL");
		try {
			Response rep = Jsoup.connect(curURL).header("User-Agent", "cis455crawler").execute();
			Document doc = rep.parse();
			String contentType = rep.contentType();
			collector.emit(new Values<Object>(curURL, doc, contentType));
			
		} catch (IOException e) {
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
