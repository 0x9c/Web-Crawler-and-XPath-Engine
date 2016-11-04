package edu.upenn.cis455.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class to extract link from html body.
 * @author cis555
 *
 */
public class Processor {

	public static List<String> extractLink(String url){
		Document doc;
		List<String> linklist = new ArrayList<String>();
		try {
			doc = Jsoup.connect(url).get();
			
			/*
			Connection con = Jsoup.connect(url);
			Map<String, String> headers = new HashMap<>();
			con = con.headers(headers);
			System.out.println(headers.toString());
			System.out.println(doc.toString());
			*/
			
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				linklist.add(link.attr("abs:href"));
			}
			return linklist;
		} catch (IOException e) {
			e.printStackTrace();
			return linklist;
		}
	}
	
	public static void main(String[] args) {
		Processor.extractLink("http://crawltest.cis.upenn.edu/marie/tpc/");
	}
	
}
