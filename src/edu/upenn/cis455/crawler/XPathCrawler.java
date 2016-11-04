package edu.upenn.cis455.crawler;

import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.BasicConfigurator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.upenn.cis.storm.CrawlerBolt;
import edu.upenn.cis.storm.DownloadBolt;
import edu.upenn.cis.storm.FilterBolt;
import edu.upenn.cis.storm.URLSpout;
import edu.upenn.cis.stormlite.Config;
import edu.upenn.cis.stormlite.LocalCluster;
import edu.upenn.cis.stormlite.Topology;
import edu.upenn.cis.stormlite.TopologyBuilder;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis455.storage.DBWrapper;

/**
 * Main class for Crawler, used to start crawl from provided start URL
 * @author cis555
 *
 */
public class XPathCrawler {
	private String startURL;
	private int maxSize;
	private DBWrapper db;
	private int maxFileNum = Integer.MAX_VALUE;
	public static URLFrontierQueue urlQueue;
	
	public XPathCrawler(){}
	
	public XPathCrawler(String startURL, String dbStorePath, int maxSize){
		this.startURL = startURL;
		this.maxSize = maxSize;
		this.urlQueue = new URLFrontierQueue(maxSize);
		this.db = DBWrapper.getInstance(dbStorePath);
		PageDownloader.setup(db);
	}
	
	public XPathCrawler(String startURL, String dbStorePath, int maxSize, int maxFileNum){
		this(startURL, dbStorePath, maxSize);
		this.maxFileNum = maxFileNum;
	}
	
	/**
	 * Main method of crawling entry.
	 */
	public void crawl() {
		int size = 0;
		urlQueue.pushURL(startURL);
		while(!urlQueue.isEmpty()) {
			String curURL = urlQueue.popURL();
			//System.out.println("Current URL : " + curURL);
			try{
				/* If the current URL doesn't match delay requirement, push it into the end of Queue */
				while(!RobotCache.checkDelay(curURL)) {
					//System.out.println("delay required");
					urlQueue.pushURL(curURL);
					curURL = urlQueue.popURL();
				}
				if(!urlQueue.filter(curURL)) continue;
				PageDownloader.download(curURL);
				urlQueue.visitedURLs.put(curURL, RobotCache.getLastVisited(curURL));
				size++;
				if(size >= maxFileNum) break;
				System.out.println(curURL + ": Downloading");
				List<String> links = Processor.extractLink(curURL);
				for(String url : links) {
					//System.out.println("------> " + url + ": Extracted Link" );
					if(RobotCache.isValid(url)) {
						urlQueue.pushURL(url);
					} 
				}
			} catch (Exception e) {
				System.out.println("Error URL: " + curURL);
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
	
	public void stormCRAWL() {
		String URL_SPOUT = "URL_SPOUT";
	    String CRAWLER_BOLT = "CRAWLER_BOLT";
	    String DOWNLOAD_BOLT = "DOWNLOAD_BOLT";
	    String FILTER_BOLT = "FILTER_BOLT";
	    
        Config config = new Config();
      
        this.urlQueue.pushURL(startURL);
        
        URLSpout spout = new URLSpout();
        CrawlerBolt boltA = new CrawlerBolt();
        DownloadBolt boltB = new DownloadBolt();
        FilterBolt boltC = new FilterBolt();
        
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(URL_SPOUT, spout, 1);
        builder.setBolt(CRAWLER_BOLT, boltA, 6).fieldsGrouping(URL_SPOUT, new Fields("URL"));
        
        // A single printer bolt (and officially we round-robin)
        builder.setBolt(DOWNLOAD_BOLT, boltB, 6).shuffleGrouping(CRAWLER_BOLT);
        builder.setBolt(FILTER_BOLT, boltC, 6).shuffleGrouping(DOWNLOAD_BOLT);

        LocalCluster cluster = new LocalCluster();
        Topology topo = builder.createTopology();

        ObjectMapper mapper = new ObjectMapper();
		try {
			String str = mapper.writeValueAsString(topo);
			
			System.out.println("The StormLite topology is:\n" + str);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        cluster.submitTopology("crawler", config, 
        		builder.createTopology());
        
        
        while(urlQueue.URLexecuted < maxFileNum) {
        	// keep waiting...
            if(urlQueue.isEmpty()) {
            	try{
            		Thread.sleep(30000);      // if queue is empty, wait five more seconds to see whether more links are coming
            	} catch (InterruptedException e){
            		e.printStackTrace();
            	}
            	System.out.println("Queue size: " + urlQueue.getSize());
            	if(urlQueue.isEmpty()) break;
            }
        }
        
        cluster.killTopology("crawler");
        cluster.shutdown();
        System.exit(0);
    }
	
	public static void main(String[] args){
		BasicConfigurator.configure();
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
			crawler.stormCRAWL();
			crawler.close();
		}
		else if(args.length == 4){
			String seedURL = args[0];
			String filepath = args[1];
			int maxSize = Integer.parseInt(args[2]);
			int fileno = Integer.parseInt(args[3]);
			XPathCrawler crawler = new XPathCrawler(seedURL, filepath, maxSize, fileno);
			//crawler.crawl();
			crawler.stormCRAWL();
			crawler.close();
		}
		else{
			System.out.println("The number of arguments is wrong.");
		}
	}
}
