package edu.upenn.cis455.crawler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.jsoup.nodes.Document;

import edu.upenn.cis455.crawler.info.Client;
import edu.upenn.cis455.storage.DBWrapper;

/**
 * Download the webpage and store to the database
 * @author dongtianxiang
 *
 */
public class PageDownloader {
	public static DBWrapper db;
	
	public static void setup(DBWrapper instance){
		db = instance;
	}
	
	public static void download(String url){
		long c0 = Calendar.getInstance().getTime().getTime();
		Client client = new Client(url);
		InputStream inputStream = client.executeGET(true);
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		int next = -1;
		try{
			while((next = inputStream.read())!=-1){
				byteOutput.write(next);
			}
			byteOutput.flush();
			byte[] content = byteOutput.toByteArray();
			db.putPage(url, content, client.getContentType());	
		}
		catch(IOException e){
			e.printStackTrace();
		}
		RobotCache.setCurrentTime(url);
		db.sync();
	}
	
	public static void download(String url, Document doc, String type){
		String body = doc.toString();
		byte[] content = body.getBytes();
		db.putPage(url, content, type);	
		RobotCache.setCurrentTime(url);
		db.sync();
	}
}
