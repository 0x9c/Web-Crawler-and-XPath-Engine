package edu.upenn.cis455.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

import edu.upenn.cis455.storage.user.User;

public class DBWrapper {
	
	private static String envDirectory = null;
	
	private static Environment myEnv;
	private static EntityStore store;
	
	/* TODO: write object store wrapper for BerkeleyDB */
	public DBWrapper(String envDirectory){
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
	
	public Environment getEnvironment()
	{
		return myEnv;
	}
	
	public EntityStore getStoreUser()
	{
		return store;
	}
	
	public EntityStore getStoreCrawler()
	{
		return store;
	}
	
	//Close method
	public void close()
	{
		
		//Close store first as recommended
		if(store!=null)
		{
			try{
				store.close();
			}
			catch(DatabaseException e)
			{
				e.printStackTrace();
			}
		}
		
		
		if(myEnv!=null)
		{
			try{
				myEnv.close();
			}
			catch(DatabaseException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	 
	public void addData(User user)
	{
		PrimaryIndex<String,User> userPIndex = store.getPrimaryIndex(String.class, User.class);
		userPIndex.put(user);
	}
	
//	public void addData(URL url)
//	{
//		PrimaryIndex<String,URL> urlPIndex = store.getPrimaryIndex(String.class, URL.class);
//		urlPIndex.put(url);
//	}
//	
//	public void addData(XPath xpath)
//	{	
//		PrimaryIndex<String,XPath> xpathPIndex = store.getPrimaryIndex(String.class, XPath.class);
//		xpathPIndex.put(xpath);
//	}
//	
//	public void addData(Channel channel)
//	{	
//		PrimaryIndex<String,Channel> channelPIndex = store.getPrimaryIndex(String.class, Channel.class);
//		channelPIndex.put(channel);
//	}
//	
//	
	public User getUser(String key)
	{
		PrimaryIndex<String, User> userPIndex = store.getPrimaryIndex(String.class, User.class);
		User u = userPIndex.get(key);
		return u;
	}
//	
//	public URL getURL(String key)
//	{
//		PrimaryIndex<String,URL> urlPIndex = store.getPrimaryIndex(String.class, URL.class);
//		URL u = urlPIndex.get(key);
//		return u;
//	}
//
//	public XPath getXPath(String xpath) {
//		
//		PrimaryIndex<String,XPath> xpathPIndex = store.getPrimaryIndex(String.class, XPath.class);
//		XPath u = xpathPIndex.get(xpath);
//		return u;
//	}
//
//	public Channel getChannel(String channelKey) {
//		
//		PrimaryIndex<String,Channel> channelPIndex = store.getPrimaryIndex(String.class, Channel.class);
//		Channel u = channelPIndex.get(channelKey);
//		return u;
//		
//	}
//	
//	
//	public void deleteURL(String key)
//	{
//		PrimaryIndex<String,URL> urlPIndex = store.getPrimaryIndex(String.class, URL.class);
//		urlPIndex.delete(key);
//	}
//	
//	public void deleteUser(String key)
//	{
//		PrimaryIndex<String,User> userPIndex = store.getPrimaryIndex(String.class, User.class);
//		userPIndex.delete(key);
//	}
//	
//	public void deleteChannel(String key)
//	{
//		PrimaryIndex<String,Channel> channelPIndex = store.getPrimaryIndex(String.class, Channel.class);
//		channelPIndex.delete(key);
//	}
//	
//	public void deleteXPath(String key)
//	{
//		PrimaryIndex<String,XPath> xpathPIndex = store.getPrimaryIndex(String.class, XPath.class);
//		xpathPIndex.delete(key);
//	}
//	
//	
//	public TreeMap<String, ArrayList<String>> allChannels()
//	{
//		PrimaryIndex<String,Channel> channelPIndex = store.getPrimaryIndex(String.class, Channel.class);
//		TreeMap<String, ArrayList<String>> map = new TreeMap<String,ArrayList<String>>();
//		EntityCursor<String> cursor = channelPIndex.keys();
//		for(String key : cursor)
//		{
//			String channelName = key.split(" ",2)[0];
//			String userName = key.split(" ",2)[1];
//			
//			if(map.containsKey(userName))
//			{
//				map.get(userName).add(channelName);
//			}
//			else
//			{
//				ArrayList<String> channelList = new ArrayList<String>();
//				channelList.add(channelName);
//				map.put(userName, channelList);
//			}
//		}
//		
//		cursor.close();
//		
//		return map;
//	}
//
//	public HashMap<String,XPath> allXPaths()
//	{
//		PrimaryIndex<String,XPath> xpathPIndex = store.getPrimaryIndex(String.class, XPath.class);
//		HashMap<String, XPath> map = new HashMap<String, XPath>();
//		EntityCursor<String> cursor = xpathPIndex.keys();
//		for(String key : cursor)
//		{
//			map.put(key,xpathPIndex.get(key));
//		}
//		
//		cursor.close();
//		return map;
//	}
//
//	public HashMap<String, URL> allDocuments() {
//		
//		PrimaryIndex<String,URL> urlPIndex = store.getPrimaryIndex(String.class, URL.class);
//		HashMap<String, URL> map = new HashMap<String, URL>();
//		EntityCursor<String> cursor = urlPIndex.keys();
//		for(String key : cursor)
//		{
//			map.put(key, urlPIndex.get(key));
//		}
//		cursor.close();
//		return map;
//	}
}
