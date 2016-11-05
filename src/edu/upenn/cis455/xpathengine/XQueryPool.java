package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Query Pool which includes xpaths from all channels.
 * @author cis555
 *
 */
public class XQueryPool {
	String[] queries;
	XQuery[] querypool;
	HashMap<String, List<Node>> candidateList;
	
	public XQueryPool(String[] queries){
		this.queries = queries;
		candidateList = new HashMap<String, List<Node>>();
		querypool = new XQuery[queries.length];
		for(int i=0;i<queries.length;i++){
			querypool[i] = new XQuery(queries[i], i, 0);
			insertCandidate(querypool[i]);
		}
	}
	
	public XQuery[] getPool(){
		return this.querypool;
	}
	
	public int getSize(){
		return this.querypool.length;
	}
	
	private void insertCandidate(XQuery query){
		Node head = query.head;
		if(!candidateList.containsKey(head.pathname)){
			candidateList.put(head.pathname, new ArrayList<Node>());
		}
		candidateList.get(head.pathname).add(head);
	}
	
	public void insertChildren(Node node){
		for(Node child: node.children){
			if(!candidateList.containsKey(child.pathname)){
				candidateList.put(child.pathname, new ArrayList<Node>());
			}
			candidateList.get(child.pathname).add(child);
		}
	}
	
	public void removeChildren(Node node){
		for(Node child: node.children){
			if(candidateList.containsKey(child.pathname)){
				candidateList.get(child.pathname).remove(child);
			}
		}
	}
}
