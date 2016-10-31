package edu.upenn.cis455.crawler.info;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class Client {
	private String url;
	private String hostName;
	private String path;
	private int portNumber;
	private int contentLength;
	private String contentType = "text/html";
	private long last_modified;
	
	public Client(String url){
		this.url = url;
		URLInfo urlinfo = new URLInfo(url);
		this.hostName = urlinfo.getHostName();
		this.path = urlinfo.getFilePath();
		this.portNumber = urlinfo.getPortNo();
		executeGET(false);
	}

	/**
	 * This is the method excuting GET or HEAD. Basically HEAD is called in the Client's Constructor.
	 * @param isGET true means excuting GET request; false means excuting HEAD request.
	 * @return InputStream returned by server for the respond's body ONLY.
	 */
	public InputStream executeGET(boolean isGET){
		String method = isGET ? "GET" : "HEAD";
		
		if(url.startsWith("https")) {
			try{
				URL httpsURL = new URL(url);
				HttpsURLConnection c = (HttpsURLConnection)httpsURL.openConnection();
				c.connect();
				contentLength = c.getContentLength();
				contentType = c.getContentType();
				last_modified = c.getLastModified();
				if(isGET) return c.getInputStream();
				else return null;
			} catch(MalformedURLException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
		} else if(url.startsWith("http")){
			try{
				/* Sending HEAD or GET Request */
				Socket socket = new Socket(InetAddress.getByName(hostName), portNumber);
				PrintWriter output = new PrintWriter(socket.getOutputStream());
				output.println(method + " " + path + " HTTP/1.1");
				output.println("Host: "+ hostName);
				output.println("User-Agent: cis455crawler");  /* Header must be added to avoid serious crawling problem */
				output.println("Connection: close");  // connection close header to close socket immediately when reader completed, otherwise bufferedReader might be waiting for timeout
				output.println("");
				output.flush();
				
				/* Reading incoming Response */
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String s;
				s = br.readLine();
				if(processInitialLine(s)){
					// To check if the response valid 
					while((s = br.readLine()) != null) {
						if(s.equals("")) break;  // The header lines end.
						Pattern r = Pattern.compile("(.*?): (.*)");
						Matcher m = r.matcher(s);
						if(m.find()){
							if(m.group(1).toLowerCase().equals("content-length")){
								contentLength = Integer.parseInt(m.group(2));
							}
							if(m.group(1).toLowerCase().equals("content-type")){
								contentType = m.group(2);
							}
							if(m.group(1).toLowerCase().equals("last-modified")){
								String date = m.group(2);
								SimpleDateFormat f = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
								Date d = f.parse(date);
								last_modified = d.getTime();
							}
						}
					}
					if(isGET){
						StringBuilder sb = new StringBuilder();
						while((s = br.readLine()) != null){
							sb.append(s);
							sb.append("\n");
						}
						String responseBody = sb.toString();
						socket.close();
						return new ByteArrayInputStream(responseBody.getBytes());
					} else {
						socket.close();
						return null;
					}
				}
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} 
		} 
		
		return null;
	}
	
	public boolean processInitialLine(String s){
		Pattern p = Pattern.compile("HTTP/1.1 (\\d{3}) .*");
		Matcher m = p.matcher(s);
		if(m.find()){
			int status_code = Integer.parseInt(m.group(1));
			if(status_code < 400) return true;
		}
		return false;
	}
	
	public boolean isValid(int maxSize){
		return isValidType() && isValidLength(maxSize);
	}
	
	public boolean isValidType(){
		if(contentType.startsWith("text/html")) return true;
		if(contentType.startsWith("application/xml")) return true;
		if(contentType.startsWith("text/xml")) return true;
		if(contentType.endsWith("+xml")) return true;
		return false;
	}
	
	public boolean isValidLength(int maxSize){
		if(contentLength > maxSize*1024*1024) return false;
		return true;
	}
	
	public long getLast_modified() {
		return last_modified;
	}
	public void setLast_modified(long last_modified) {
		this.last_modified = last_modified;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public int getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public static void main(String[] args) throws IOException {
		String url = "http://crawltest.cis.upenn.edu/marie/tpc";
		Client client = new Client(url);
		System.out.println("URLInfo:" + client.getPath());
		InputStream inputStream = client.executeGET(true);
		if(inputStream != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int next;
			try{
				while((next = inputStream.read())!=-1){
					bos.write(next);
				}
				bos.flush();
				byte[] content = bos.toByteArray();
				System.out.println(new String(content, "UTF-8"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(client.getContentType());
		System.out.println(client.getContentLength());
		System.out.println(client.getLast_modified());
	}
}
