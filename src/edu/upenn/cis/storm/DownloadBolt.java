package edu.upenn.cis.storm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
 * Bolt Component used to store document into database
 * @author cis555
 *
 */
public class DownloadBolt implements IRichBolt{
	static Logger log = Logger.getLogger(DownloadBolt.class);
	
	Fields schema = new Fields("URL", "type","document", "URLStream");
	
	String executorId = UUID.randomUUID().toString();
	
	private OutputCollector collector;
	
	private URLFrontierQueue urlQueue;
	
    public DownloadBolt() {
    	log.debug("Starting DownloadBolt");
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
		Document doc = (Document)input.getObjectByField("document");
		String type = input.getStringByField("type");
		String url = input.getStringByField("url");
		
		RobotCache.setCurrentTime(url);
		/* The Downloader itself updated the last visited time */
		PageDownloader.download(url, doc, type);
		
		synchronized(urlQueue) {
			urlQueue.visitedURLs.put(url, RobotCache.getLastVisited(url));
			urlQueue.URLexecuted += 1;
			log.info(executorId + "----> " + url + ": Downloading");
			log.info(urlQueue.URLexecuted);
		}
		
		List<String> linklist = new ArrayList<String>();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			linklist.add(link.attr("abs:href"));
		}
		
		collector.emit(new Values<Object>(url, type, doc, linklist));
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
