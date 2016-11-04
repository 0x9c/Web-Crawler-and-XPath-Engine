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
	private Set<String> matchedURL;
	
	public Channel(){}
	
	public Channel(String channelName, String XPath){
		this.channelName = channelName;
		this.setXPath(XPath);
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

	public void setMatchedURL(Set<String> matchedURL) {
		this.matchedURL = matchedURL;
	}
}
