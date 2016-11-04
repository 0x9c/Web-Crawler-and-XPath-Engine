package edu.upenn.cis455.crawler;

import java.util.Calendar;
import java.util.Hashtable;

import edu.upenn.cis455.crawler.info.Robot;
import edu.upenn.cis455.crawler.info.URLInfo;

public class RobotCache {
	public static Hashtable<String, Robot> robots = new Hashtable<>();
	
	public static void addRobot(String url) {
		URLInfo urlinfo = new URLInfo(url);
		String hostName = urlinfo.getHostName();
		if(!robots.containsKey(hostName)){
			Robot robot = new Robot(url);
			robots.put(hostName, robot);
		}
	}
	
	public static boolean isValid(String url) {
		addRobot(url);
		URLInfo urlinfo = new URLInfo(url);
		String hostName = urlinfo.getHostName();
		Robot robot = robots.get(hostName);
		return robot.isURLValid(url);
	}
	
	public static boolean checkDelay(String url){
		URLInfo urlinfo = new URLInfo(url);
		String hostName = urlinfo.getHostName();
		if(robots.containsKey(hostName)) {    // not having hostName means even haven't crawled before 
			Robot robot = robots.get(hostName);
			if(robot.getCrawlDelay() == 0) return true;
			Calendar cal = Calendar.getInstance();
			long lastVisited = robot.getLastVisited();
			long currentVisiting = cal.getTime().getTime();
			if(currentVisiting - lastVisited >= robot.getCrawlDelay() * 1000 ) {  // crawl delay in seconds actually
				robot.setLastVisited();
				return true;
			} else {
				return false;
			}
		} else {
			addRobot(url);
			return true;
		}
	}
	
	public static void setCurrentTime(String url) {
		URLInfo urlinfo = new URLInfo(url);
		String hostname = urlinfo.getHostName();
		Robot robot = robots.get(hostname);
		if(robot == null) {
			addRobot(url);
			robot = robots.get(hostname);
		}
		robot.setLastVisited();
	}
	
	public static long getLastVisited(String url) {
		URLInfo urlinfo = new URLInfo(url);
		String hostname = urlinfo.getHostName();
		Robot robot = robots.get(hostname);
		return robot.getLastVisited();
	}
}
