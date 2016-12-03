package edu.upenn.cis455.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import edu.upenn.cis.storm.CrawlerBolt;
import edu.upenn.cis455.crawler.info.Client;

/**
 * The URL Frontier class, used for storing URL Queue, as well as visited URLs.
 * @author cis555
 *
 */
public class URLFrontierQueue {
	public ConcurrentHashMap<String, Long> visitedURLs = new ConcurrentHashMap<String, Long>();
//	private Queue<String> queue = new LinkedList<String>();
	private Queue<String> queue = new LinkedBlockingQueue<String>();
	private int maxSize = Integer.MAX_VALUE;
	public volatile static int URLexecuted = 0;
	static Logger log = Logger.getLogger(URLFrontierQueue.class);
	
	public URLFrontierQueue(){};
	
	public URLFrontierQueue(int maxSize){
		this.maxSize = maxSize;
	}
	
	public boolean filter(String url){
		Client client = new Client(url);
		if(!client.isValid(maxSize)) {
			log.debug(url + ": Not Downloading");
			return false;
		}
		long currentLastModified = client.getLast_modified();
		if(visitedURLs.containsKey(url)){
			long crawled_LastModified = visitedURLs.get(url);	
			if(currentLastModified > crawled_LastModified){
				visitedURLs.put(url, currentLastModified);
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
		return queue.isEmpty();
	}
	
	public int getSize(){
		return queue.size();
	}
	
	public String popURL(){
		return queue.poll();
	}
	
	public synchronized int addExecutedSize(){
		return URLexecuted++;
	}
	
	public synchronized int getExecutedSize(){
		return URLexecuted;
	}
	
	public void pushURL(String url){
		queue.add(url);
		Client client = new Client(url);
		//long lastModified = client.getLast_modified();
		//visitedURLs.put(url, lastModified);
	}
	
	public void setLastModifiedWhenDownloading(String url){
		Client client = new Client(url);
		long lastModified = client.getLast_modified();
		visitedURLs.put(url, lastModified);
	}
}
