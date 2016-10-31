package edu.upenn.cis455.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import edu.upenn.cis455.crawler.info.Client;

/**
 * The URL Frontier class, used for storing URL Queue, as well as visited URLs.
 * @author cis555
 *
 */
public class URLFrontierQueue {
	HashMap<String, Long> visitedURLs = new HashMap<String, Long>();
	Queue<String> queue = new LinkedList<String>();
	int maxSize;
	
	public URLFrontierQueue(int maxSize){
		this.maxSize = maxSize;
	}
	
	public boolean filter(String url){
		Client client = new Client(url);
		if(!client.isValid(maxSize)) {
			System.out.println(url + ": Not Downloading");
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
				System.out.println(url + ": Not Modified");
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	public String popURL(){
		return queue.poll();
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
