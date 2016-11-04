package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node {
	String path;
	String pathname;
	int queryID;
	int level;
	List<Node> children = new ArrayList<Node>();
	List<String> testList = new ArrayList<String>();
	HashMap<String, String> attributeList = new HashMap<String, String>();
	List<String> containsList = new ArrayList<String>();
	List<String> textList = new ArrayList<String>();
	
	boolean valid = false;
	int completeChildren = 0;
	Node ancestor;
	
	public Node(String path, int queryID, int level){
		this.path = path;
		this.queryID = queryID;
		this.level = level;
		if(path!=null){
			this.pathname = extractPathName();
			extract();
			analyze();		
		}
	}
	
	public String getPathName(){
		return pathname;
	}
	
	public List<Node> getChildren(){
		return children;
	}
	
	public List<String> getTextList(){
		return textList;
	}
	
	public List<String> getContainsList(){
		return containsList;
	}
	
	public HashMap<String, String> getAttributeList(){
		return attributeList;
	}
		
	public void setComplete(){
		if(!valid){
			completeChildren++;
			if(completeChildren == children.size()){
				valid = true;
				if(ancestor!=null) ancestor.setComplete();
			}
		}
	}
	
	public void setInComplete(){
		valid = false;
		completeChildren = 0;
		for(Node child : children){
			child.setInComplete();
		}
	}
	
	private String extractPathName() {
		Pattern pattern = Pattern.compile("([^\\[]+)(\\[.+?\\])*");
		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			return  matcher.group(1);
		}
		return null;
	}

	private void extract() {
		int flag = 0;
		int startindex = 0;
		int endindex = 0;
		for(int i = 0; i < path.length();i++){
			if(path.charAt(i)=='['){
				flag++;
				if(flag == 1) 
					startindex = i; 
			}
			else if(path.charAt(i)==']'){
				flag--;
				if(flag == 0) {
					endindex = i;
					testList.add(path.substring(startindex+1, endindex));
				}
			}
		}
	}
	
	private void analyze() {
		for(String test:testList){
			
			if(isAttribute(test)||isContains(test)||isText(test)){
				continue;
			}
			//path
		
			else {
				XQuery testQuery = new XQuery(test, queryID, level + 1);
				children.add(testQuery.head);
			}
		}	
	}
	
	private boolean isContains(String test){
		test = test.trim();
		Pattern pattern = Pattern.compile("^contains\\s*\\(\\s*text\\s*\\(\\s*\\)\\s*,\\s*\"(.*)\"\\s*\\)");
		Matcher matcher = pattern.matcher(test);
		while(matcher.find()){
			containsList.add(matcher.group(1));
			//System.out.println("contains "+matcher.group(1));
			return true;
		}
		return false;
	}
	
	private boolean isText(String test){
		test = test.trim();
		Pattern pattern = Pattern.compile("^text\\s*\\(\\)\\s*=\\s*\"(.*)\"");
		Matcher matcher = pattern.matcher(test);
		while(matcher.find()){
			textList.add(matcher.group(1));
//			System.out.println("text " + matcher.group(1));
			return true;
		}
		return false;
	}
	
	private boolean isAttribute(String test){
		test = test.trim();
		Pattern pattern = Pattern.compile("^@([^=\\s]+)\\s*=\\s*\"(.*)\"");
		Matcher matcher = pattern.matcher(test);
		while(matcher.find()){
			attributeList.put(matcher.group(1), matcher.group(2));
			//System.out.println("attribute "+matcher.group(1)+" "+matcher.group(2));
			return true;
		}
		return false;
	}
}
