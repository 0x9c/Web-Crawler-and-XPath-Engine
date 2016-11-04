package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class XQuery {
	public String query;
	Node head;
	Node tail;
	int queryID;
	
	public XQuery(String query, int queryID, int level){
//		System.out.println(query);
		this.queryID = queryID;
		this.query = query;
		int flag = 0;
		int startindex = (query.charAt(0) == '/')? 1:0;
		List<String> nodenames = new ArrayList<String>();
		for(int i=startindex;i<query.length();i++){
			if(flag == 0 && query.charAt(i) == '/'){
				nodenames.add(query.substring(startindex, i));
				startindex = i+1;
			}
			else if(query.charAt(i) == '[') flag++;
			else if(query.charAt(i) == ']') flag--;
		}
		nodenames.add(query.substring(startindex, query.length()));
		for(String nodename:nodenames){
			if(!nodename.equals("")){
//				System.out.println(nodename + " " + level);
				Node node = new Node(nodename, queryID, level++);
				if(head == null){
					head = node;
					tail = node;
				}
				else{
					tail.children.add(node);
					tail = node;
				}
				
			}
		}
		determineAncestor();
	}
	
	public Node getHead(){
		return head;
	}
	
	public boolean isValid(){
		return head.valid;
	}
	
	private void determineAncestor(){
		LinkedList<Node> pq = new LinkedList<Node>();
		pq.add(head);
		while(!pq.isEmpty()){
			Node p = pq.poll();
			//System.out.println(p.level+" "+p.pathname);
			for(Node child: p.children){
				pq.add(child);
				child.ancestor = p;
			}
		}
	}
}
