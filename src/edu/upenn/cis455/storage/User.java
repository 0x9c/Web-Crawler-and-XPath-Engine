package edu.upenn.cis455.storage;

import java.util.HashSet;

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
	
	public User(){}
	
	public User(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
		
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
}
