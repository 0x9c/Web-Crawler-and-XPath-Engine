package edu.upenn.cis455.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

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
	
	/* TODO: write object store wrapper for BerkeleyDB */
	private DBWrapper(String envDirectory){
		//Initialize myEnv
		this.envDirectory = envDirectory;
		try{
			EnvironmentConfig envConfig = new EnvironmentConfig();
			//Create new myEnv if it does not exist
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
	
//	public static void main(String[] args){
//		DBWrapper db = DBWrapper.getInstance("./dtianx");
//		db.putChannel("sports", "/rss/channel", "dtx");
//		db = DBWrapper.getInstance("./dtianx");
//		Channel c = db.getChannel("sports");
//		c.setXPath("channel");
//		db.updateChannel(c);
//		
//		System.out.println(db.getAllChannels().get(0).getXPath());
//		
//	}
}
