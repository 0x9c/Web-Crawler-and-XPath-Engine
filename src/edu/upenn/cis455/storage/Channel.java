package edu.upenn.cis455.storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Channel {
	@PrimaryKey
	private String channelName;
	
	private String XPath;
	private String createdUser;
	private Set<String> matchedURL;
	
	public Channel(){}
	
	public Channel(String channelName, String XPath, String createdUser){
		this.channelName = channelName;
		this.setXPath(XPath);
		this.setCreatedUser(createdUser);
		this.matchedURL = new HashSet<>();
	}
	
	public String getName() {
		return this.channelName;
	}
	
	public void setName(String channelName) {
		this.channelName = channelName;
	}
	
	public String getXPath() {
		return XPath;
	}

	public void setXPath(String xPath) {
		XPath = xPath;
	}

	public Set<String> getMatchedURL() {
		return matchedURL;
	}

	public void addMatchedURL(String url) {
		this.matchedURL.add(url);
	}
	
	public void setMatchedURL(Set<String> matchedURL) {
		this.matchedURL = matchedURL;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
}
