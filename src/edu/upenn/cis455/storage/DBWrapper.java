package edu.upenn.cis455.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

import edu.upenn.cis.storm.RecordBolt;
import edu.upenn.cis455.crawler.RobotCache;
import edu.upenn.cis455.storage.User;
/**
 * Basic class to connect Berkeley DB, including add and get User, Page, etc. from Database.
 * @author cis555
 *
 */
public class DBWrapper {
	
	private static String envDirectory = null;
	
	private static Environment myEnv;
	private static EntityStore store;
	
	private static DBWrapper DBinstance = null;
	
	PrimaryIndex<String, OutLinks> outLinksIndex;
	PrimaryIndex<String, VisitedURL> visitedURLIndex;
	PrimaryIndex<String, FrontierQueue> frontierQueueIndex;
	PrimaryIndex<String, RobotMap> RobotMapIndex;
	static Logger log = Logger.getLogger(DBWrapper.class);
	
	/* TODO: write object store wrapper for BerkeleyDB */
	private DBWrapper(String envDirectory){
		//Initialize myEnv
		this.envDirectory = envDirectory;
		try{
			EnvironmentConfig envConfig = new EnvironmentConfig();
			//Create new myEnv if it does not exist
			envConfig.setLockTimeout(500, TimeUnit.MILLISECONDS);
			envConfig.setAllowCreate(true);
			//Allow transactions in new myEnv
			envConfig.setTransactional(true);
			//Create new myEnv
			File dir = new File(envDirectory);
			if(!dir.exists())
			{
				dir.mkdir();
				dir.setReadable(true);
				dir.setWritable(true);
			}
			myEnv = new Environment(dir,envConfig);
			
			//Create new entity store object
			StoreConfig storeConfig = new StoreConfig();
			storeConfig.setAllowCreate(true);
			storeConfig.setTransactional(true);
			store = new EntityStore(myEnv,"DBEntityStore",storeConfig);
			
			outLinksIndex = store.getPrimaryIndex(String.class, OutLinks.class);
			visitedURLIndex = store.getPrimaryIndex(String.class, VisitedURL.class);
			frontierQueueIndex = store.getPrimaryIndex(String.class, FrontierQueue.class);
			RobotMapIndex = store.getPrimaryIndex(String.class, RobotMap.class);
			
		}
		catch(DatabaseException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized static DBWrapper getInstance(String envDirectory) {
		if(DBinstance == null) {
			close();
			DBinstance = new DBWrapper(envDirectory);
		}
		return DBinstance;
	}
	
	public void sync() {
		if(store != null) store.sync();
		if(myEnv != null) myEnv.sync();
	}
	
	public Environment getEnvironment() {
		return myEnv;
	}
	
	public EntityStore getStoreUser() {
		return store;
	}
	
	public EntityStore getStoreCrawler() {
		return store;
	}
	
	//Close method
	public synchronized static void close() {
		
		//Close store first as recommended
		if(store!=null) {
			try{
				store.close();
			}
			catch(DatabaseException e)
			{
				e.printStackTrace();
			}
		}
		
		
		if(myEnv!=null) {
			try{
				myEnv.close();
			}
			catch(DatabaseException e)
			{
				e.printStackTrace();
			}
		}
		DBinstance = null;
	}
	
	 
	public void addData(User user)
	{
		PrimaryIndex<String,User> userPIndex = store.getPrimaryIndex(String.class, User.class);
		userPIndex.put(user);
	}
	
	public void putPage(String url, byte[] content, String type) {
		Page webpage = new Page();
		webpage.setContent(content);
		Calendar cal = Calendar.getInstance();
    	long current_time = cal.getTime().getTime();
		webpage.setCrawlTime(current_time);
		if(type.startsWith("text/html")) webpage.setType("html");
		else webpage.setType("xml");
		webpage.setURL(url);
		PrimaryIndex<String, Page> WebpageIndex = store.getPrimaryIndex(String.class, Page.class);
		WebpageIndex.put(webpage);
	}

	public User getUser(String key) {
		PrimaryIndex<String, User> userPIndex = store.getPrimaryIndex(String.class, User.class);
		User u = userPIndex.get(key);
		return u;
	}
	
	public Page getWebpage(String url) {
		PrimaryIndex<String, Page> WebpageIndex = store.getPrimaryIndex(String.class, Page.class);
		return WebpageIndex.get(url);
	}
	
	public Channel getChannel(String channelName) {
		PrimaryIndex<String, Channel> channelIndex = store.getPrimaryIndex(String.class, Channel.class);
		return channelIndex.get(channelName);
	}
	
	public void putChannel(String channelName, String XPath, String username) {
		Channel channel = new Channel(channelName, XPath, username);
		PrimaryIndex<String, Channel> channelIndex = store.getPrimaryIndex(String.class, Channel.class);
		channelIndex.put(channel);
	}
	
	public void updateChannel(Channel channel) {
		PrimaryIndex<String, Channel> channelIndex = store.getPrimaryIndex(String.class, Channel.class);
		channelIndex.put(channel);
	}
	
	public void updateUser(User user) {
		PrimaryIndex<String,User> userPIndex = store.getPrimaryIndex(String.class, User.class);
		userPIndex.put(user);
	}
	
	public List<Channel> getAllChannels() {
		PrimaryIndex<String, Channel> channelIndex = store.getPrimaryIndex(String.class, Channel.class);
		EntityCursor<Channel> cursor = channelIndex.entities();
		List<Channel> res = new ArrayList<>();
		try{
			for(Channel c : cursor) res.add(c);
		} finally {
			cursor.close();
		}
		return res;
	}
	
	/* Related Method for OutLinks */
	
	public synchronized OutLinks getOutLinks(String url) {
		synchronized(outLinksIndex) {
			return outLinksIndex.get(url);
		}
	}
	
	public synchronized void putOutLinks(OutLinks outlinks) {
		synchronized(outLinksIndex) {
			outLinksIndex.put(outlinks);
		}
		sync();
	}
	
	public List<String> getOutLinksList(String url){
		OutLinks outlinks = getOutLinks(url);
		return outlinks.getLinks();
	}
	
	public void addOutLinks(String url, String link) {
		OutLinks outlinks = getOutLinks(url);
		if(outlinks == null) {
			outlinks = new OutLinks(url);
		}
		outlinks.addLinks(link);
		putOutLinks(outlinks);
	}
	
	public void setOutLinks(String url, Queue<String> links) {
		OutLinks outlinks = getOutLinks(url);
		if(outlinks == null) {
			outlinks = new OutLinks(url);
			for(int i = 0; i < links.size(); i++) {
				String link = links.poll();
				outlinks.getLinks().add(link);
				links.offer(link);
			}
			putOutLinks(outlinks);
		}
	}
	
	/* Related Method for VisitedURLs */
	
	public VisitedURL getVisitedURL(String url) {
		synchronized(visitedURLIndex) {
			return visitedURLIndex.get(url);
		}
	}
	
	public synchronized void putVisitedURL(VisitedURL visitedURL) {
		synchronized(visitedURLIndex) {
			visitedURLIndex.put(visitedURL);
		}
		sync();
	}
	
	public Long getVisitedTime(String url) {
		VisitedURL v = getVisitedURL(url);
		if(v == null) return Calendar.getInstance().getTimeInMillis();
		return v.getLastVisited();
	}
	


	public void putVisitedURL(String url, Long lastVisited) {
		VisitedURL v = getVisitedURL(url);
		if(v == null) {
			v = new VisitedURL(url, lastVisited);
		} else {
			v.setLastVisited(lastVisited);
		}
		putVisitedURL(v);
	}
	
	public long getVisitedSize(){
		synchronized(visitedURLIndex) {
			return visitedURLIndex.count();
		}
	}
	
	public boolean visitedURLcontains(String url) {
		synchronized(visitedURLIndex) {
			return visitedURLIndex.contains(url);
		}
	}
	
	
	/* Related Method for FrontierQueue */
	public FrontierQueue getFrontierQueue() {
		synchronized(frontierQueueIndex) {
			FrontierQueue queue = frontierQueueIndex.get("FrontierQueue");
			if(queue == null) {
				queue = new FrontierQueue();
				putFrontierQueue(queue);
			}
			return queue;
		}
	}
	
	public void putFrontierQueue(FrontierQueue queue){
		synchronized(frontierQueueIndex) {
			frontierQueueIndex.put(queue);
		}
		sync();
	}
	
	public synchronized void pollFromFrontierQueue(int num, Queue<String> memoryQueue){
		FrontierQueue queue = getFrontierQueue();
		int count = 0;
		while(!queue.isEmpty() && count < num) {
			String url = queue.pollQueue();
			memoryQueue.offer(url);
			count++;
		}
		putFrontierQueue(queue);
	}
	
	public synchronized void addIntoFrontierQueue(LinkedBlockingQueue<String> nextQueue){
		FrontierQueue queue = getFrontierQueue();
		while(!nextQueue.isEmpty()) {
			queue.addQueue(nextQueue.poll());
		}
		putFrontierQueue(queue);
	}
	
	public int getFrontierQueueSize() {
		FrontierQueue queue = getFrontierQueue();
		int size = queue.getSize();
		return size;
	}
	
	public boolean isEmptyFrontierQueue() {
		return getFrontierQueueSize() == 0;
	}
	
	/* Related Method for RobotMap */
	public RobotMap getRobotMap(String hostname) {
		synchronized(RobotMapIndex) {
			return RobotMapIndex.get(hostname);
		}

	}
	
	public void putRobotMap(RobotMap RobotMap) {
		synchronized(RobotMapIndex) {
			RobotMapIndex.put(RobotMap);
		}
		sync();
	}
	
	public void putRobotMap(String hostname, String url) {
		RobotMap v = getRobotMap(hostname);
		if(v == null) {
			v = new RobotMap(hostname, url);
			putRobotMap(v);
		} 
	}

	public int getRobotCrawlDelay(String hostname) {
		return getRobotMap(hostname).getCrawlDelay();
	}
	
	public boolean getRobotIsURLValid(String hostname, String url) {
		return getRobotMap(hostname).isURLValid(url);
	}
	
	public long getRobotLastVisited(String hostname){
		return getRobotMap(hostname).getLastVisited();
	}
	
	public void setRobotLastVisited(String hostname){
		RobotMap v = getRobotMap(hostname);
		v.setLastVisited();
		putRobotMap(v);
	}
	
	public long getRobotMapSize(){
		synchronized(RobotMapIndex) {
			return RobotMapIndex.count();
		}
	}
	
	public boolean RobotMapContains(String hostName) {
		synchronized(RobotMapIndex) {
			return RobotMapIndex.contains(hostName);
		}
	}
	
	
	
	
	public static void main(String[] args) throws IOException{
		DBWrapper db = DBWrapper.getInstance("./dtianx");
//		System.out.println(System.getenv().get("AWS_KEY"));
//		Map<String, String> env = System.getenv();
//        for (String envName : env.keySet()) {
//            System.out.format("%s=%s%n",
//                              envName,
//                              env.get(envName));
//        }
//        Properties prop = new Properties();
//        String propFileName = "config.properties";
//		InputStream inputStream = DBWrapper.class.getClassLoader().getResourceAsStream(propFileName);
//		if (inputStream != null) {
//			prop.load(inputStream);
//		} else {
//			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
//		}
//		System.out.println(prop.getProperty("KEY"));

		System.out.println(db.getVisitedSize());
//		System.out.println(RobotCache.isValid("https://www.facebook.com"));
//		System.out.println(System.setProperty("AWS_ID", "aaa"));
//		System.out.println(System.getProperty("AWS_ID"));
		

	}
}
