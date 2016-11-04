package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler{ 
	int level = 0;
	List<Node> textChecklist = new ArrayList<Node>();
	XQueryPool queryPool;
	
	public SAXHandler(){}
	
	public SAXHandler(XQueryPool queryPool) {
		setPool(queryPool);
	}
	
	public void setPool(XQueryPool queryPool){
		this.queryPool = queryPool;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		HashMap<String, List<Node>> candidateList = queryPool.candidateList;
		if(candidateList.containsKey(qName)){
			List<Node> nodelist = candidateList.get(qName);
			for(Node node:nodelist){
				//check level
				if(node.level == level){
					//check attribute
					HashMap<String, String> attributeList = node.attributeList;
					int attributeFlag = 0;
					for(Map.Entry<String, String> entry: attributeList.entrySet()){
						if(attributes.getValue(entry.getKey()).equals(entry.getValue())){
							continue;
						}
						else{
							//do not process any more
							attributeFlag = 1;
						}
					}
					if(attributeFlag == 0){
						//need to check text
						textChecklist.add(node);	
					}
				}
			}
		}
		level++;		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		level--;
		HashMap<String, List<Node>> candidateList = queryPool.candidateList;
		if(candidateList.containsKey(qName)){
			List<Node> nodelist = candidateList.get(qName);
			for(Node node:nodelist){
				if(node.level == level){
					queryPool.removeChildren(node);
					if(!node.valid) node.setInComplete();
				}
			}
		}
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		
		for(Node node: textChecklist){
			boolean textfailure = false;
			List<String> containsList = node.containsList;
			List<String> textList = node.textList;
			
			for(String s: containsList){
				if(text.contains(s)) continue;
				else textfailure = true;
			}
			for(String s: textList){
				//text = text.trim();
				if(text.equals(s)) continue;
				else textfailure = true;
			}
			if(!textfailure){
				if(node.children.size() == 0){
					node.valid = true;
					if(node.ancestor!=null) node.ancestor.setComplete();
				}
				else queryPool.insertChildren(node);
			}
		}
		textChecklist.clear();
	}
}
