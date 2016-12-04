package edu.upenn.cis455.storage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class FrontierQueue {
	@PrimaryKey
	private String key = "FrontierQueue";
	private Queue<String> queue = new LinkedList<String>();
	
	public FrontierQueue() {}
	
	public synchronized void addQueue(String url) {
		this.queue.offer(url);
	}
	
	public synchronized String pollQueue() {
		if(queue.isEmpty()) return null;
		return this.queue.poll();
	}
	
	public synchronized int getSize(){
		return this.queue.size();
	}
}
