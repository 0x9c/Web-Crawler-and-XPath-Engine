package edu.upenn.cis455.crawler;

import java.util.Calendar;
import java.util.List;

import edu.upenn.cis455.storage.DBWrapper;

public class XPathCrawler {
	private String startURL;
	private int maxSize;
	private URLFrontierQueue urlQueue;
	private DBWrapper db;
	private int maxFileNum = Integer.MAX_VALUE;
	
	public XPathCrawler(){}
	
	public XPathCrawler(String startURL, String dbStorePath, int maxSize){
		this.startURL = startURL;
		this.maxSize = maxSize;
		this.urlQueue = new URLFrontierQueue(maxSize);
		this.db = new DBWrapper(dbStorePath);
		PageDownloader.setup(db);
	}
	
	public XPathCrawler(String startURL, String dbStorePath, int maxSize, int maxFileNum){
		this(startURL, dbStorePath, maxSize);
		this.maxFileNum = maxFileNum;
	}
	
	public void crawl() {
		int size = 0;
		urlQueue.pushURL(startURL);
		while(!urlQueue.isEmpty()) {
			String curURL = urlQueue.popURL();
			System.out.println("Current URL : " + curURL);
			try{
				/* If the current URL doesn't match delay requirement, push it into the end of Queue */
				while(!RobotCache.checkDelay(curURL)) {
					//System.out.println("delay required");
					urlQueue.pushURL(curURL);
					curURL = urlQueue.popURL();
				}
				PageDownloader.download(curURL);
				size++;
				if(size >= maxFileNum) break;
				System.out.println(curURL + " : Downloading");
				List<String> links = Processor.extractLink(curURL);
				for(String url : links) {
					System.out.println("------> " + url + ": Extracted Link" );
					if(urlQueue.filter(url)){
						if(RobotCache.isValid(url)) {
							urlQueue.pushURL(url);
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				continue;   // skip this URL
			}
			System.out.println(size);
		}
		System.out.println(urlQueue.visitedURLs.size());
		
	}
	
	public void close(){
		db.close();
	}
	
	public static void main(String[] args){
		if(args.length == 0){
			System.out.println("You need to specify the arguments.");
			System.out.println("name: Tianxiang Dong");
			System.out.println("pennkey: dtianx");
			return;
		}
		else if(args.length == 3){
			String seedURL = args[0];
			String filepath = args[1];
			int maxSize = Integer.parseInt(args[2]);
			XPathCrawler crawler = new XPathCrawler(seedURL, filepath, maxSize);
			crawler.crawl();
			crawler.close();
		}
		else if(args.length == 4){
			String seedURL = args[0];
			String filepath = args[1];
			int maxSize = Integer.parseInt(args[2]);
			int fileno = Integer.parseInt(args[3]);
			XPathCrawler crawler = new XPathCrawler(seedURL, filepath, maxSize, fileno);
			crawler.crawl();
			crawler.close();
		}
		else{
			System.out.println("The number of arguments is wrong.");
		}
	}
}
