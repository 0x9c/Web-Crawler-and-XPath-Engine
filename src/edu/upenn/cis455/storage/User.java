package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Entity Class used to store User information into Database
 * @author cis555
 *
 */
@Entity
public class User {
	//User class for registered users
	@PrimaryKey
	private String userName;
	private String password;
	private List<String> subscribe;
	
	public User(){}
	
	public User(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
		this.subscribe = new ArrayList<>();
	}
	
	public void setUser(String userName)
	{
		this.userName = userName;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getPassword()
	{
		return this.password;
	}

	public List<String> getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(List<String> subscribe) {
		this.subscribe = subscribe;
	}
	
	public void addSubscribe(String channelName){
		this.subscribe.add(channelName);
	}
	
	public void removeSubscribe(String channelName){
		this.subscribe.remove(channelName);
	}
}
