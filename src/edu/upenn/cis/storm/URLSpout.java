package edu.upenn.cis.storm;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.spout.IRichSpout;
import edu.upenn.cis.stormlite.spout.SpoutOutputCollector;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Values;
import edu.upenn.cis455.crawler.RobotCache;
import edu.upenn.cis455.crawler.URLFrontierQueue;
import edu.upenn.cis455.crawler.XPathCrawler;

/**
 * Main spout that checks delay and dequeue URLs from URLFrontier.
 * @author cis555
 *
 */
public class URLSpout implements IRichSpout{
	static Logger log = Logger.getLogger(URLSpout.class);
	URLFrontierQueue urlQueue;
	SpoutOutputCollector collector;
	int size = 0;
	
    /**
     * To make it easier to debug: we have a unique ID for each
     * instance of the WordSpout, aka each "executor"
     */
    String executorId = UUID.randomUUID().toString();
    
	public URLSpout(){
		log.debug("Starting Spout");
		this.urlQueue = XPathCrawler.urlQueue;
	}
	
	@Override
	public String getExecutorId() {
		return executorId;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("URL"));
	}

	@Override
	public void open(Map<String, String> config, TopologyContext topo, SpoutOutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void close() {}

	@Override
	public void nextTuple() {
		synchronized(urlQueue) {
			if(!urlQueue.isEmpty()) {
				String curURL = urlQueue.popURL();
				if(!RobotCache.checkDelay(curURL)) {
					urlQueue.pushURL(curURL);
					//log.info("Delay Required");
				} else {
					//log.info("URL Emitted");
					this.collector.emit(new Values<Object>(curURL));
				}
			}
		}
		Thread.yield();
	}

	@Override
	public void setRouter(IStreamRouter router) {
		this.collector.setRouter(router);
	}

}
