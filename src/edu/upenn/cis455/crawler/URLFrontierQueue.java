package edu.upenn.cis455.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import edu.upenn.cis.storm.CrawlerBolt;
import edu.upenn.cis455.crawler.info.Client;
import edu.upenn.cis455.storage.DBWrapper;

/**
 * The URL Frontier class, used for storing URL Queue, as well as visited URLs.
 * @author cis555
 *
 */

public class URLFrontierQueue {
	
	//private ConcurrentHashMap<String, Long> visitedURLs = new ConcurrentHashMap<String, Long>();
	//private Queue<String> queue = new LinkedBlockingQueue<String>();
	
	
	private int maxSize = Integer.MAX_VALUE;
	private volatile static int URLexecuted = 0;
	private static Logger log = Logger.getLogger(URLFrontierQueue.class);
	private DBWrapper db = DBWrapper.getInstance(XPathCrawler.dbPath);
	
	public URLFrontierQueue(){};
	
	public URLFrontierQueue(int maxSize){
		this.maxSize = maxSize;
	}
	
	public void putIntoVisitedURL(String url, Long visitedTime) {
		db.putVisitedURL(url, visitedTime);
	}
	
	public long getVisitedURLSize(){
		return db.getVisitedSize();
	}
	
	public boolean filter(String url){
		Client client = new Client(url);
		if(!client.isValid(maxSize)) {
			log.debug(url + ": Not Downloading");
			return false;
		}
		long currentLastModified = client.getLast_modified();
		if(db.visitedURLcontains(url)){
			long crawled_LastModified = db.getVisitedTime(url);	
			if(currentLastModified > crawled_LastModified){
				db.putVisitedURL(url, currentLastModified);
				return true;
			}
			else{
				log.debug(url + ": Not Modified");
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty(){
		return db.isEmptyFrontierQueue();
	}
	
	public int getSize(){
		return db.getFrontierQueueSize();
	}
	
	public String popURL(){
		return db.pollFromFrontierQueue();
	}
	
	public synchronized int addExecutedSize(){
		return URLexecuted++;
	}
	
	public synchronized int getExecutedSize(){
		return URLexecuted;
	}
	
	public void pushURL(String url){
		db.addIntoFrontierQueue(url);
		//Client client = new Client(url);
		//long lastModified = client.getLast_modified();
		//visitedURLs.put(url, lastModified);
	}
	
	public void setLastModifiedWhenDownloading(String url){
		Client client = new Client(url);
		long lastModified = client.getLast_modified();
		db.putVisitedURL(url, lastModified);
	}
}
