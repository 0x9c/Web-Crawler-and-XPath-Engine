package edu.upenn.cis.storm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.apache.log4j.Logger;

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

public class FilterBolt implements IRichBolt{
	static Logger log = Logger.getLogger(FilterBolt.class);
	
	Fields schema = new Fields();
	
	String executorId = UUID.randomUUID().toString();
	
	private OutputCollector collector;
	
	private URLFrontierQueue urlQueue;
	
    public FilterBolt() {
    	log.debug("Starting FilterBolt");
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
		List<String> links = (List<String>) input.getObjectByField("URLStream");
		Queue<String> linksToCheck = new LinkedList<>();
		for(String link : links) linksToCheck.offer(link);
		
		while(!linksToCheck.isEmpty()) {
			String link = linksToCheck.poll();
			if(!RobotCache.checkDelay(link)){
				linksToCheck.offer(link);
				continue;
			}

			if(RobotCache.isValid(link)) {
				RobotCache.setCurrentTime(link);  // HEAD REQUEST set the last visited time
				synchronized(urlQueue) {
					if(!urlQueue.filter(link)) continue;
					
					urlQueue.pushURL(link);
					urlQueue.visitedURLs.put(link, RobotCache.getLastVisited(link));
					log.info(link + " --> pushed into queue");
				}
			} 
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
