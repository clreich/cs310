package cs310;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.util.*;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class HashPrac {
	
	
	
	
	
    public static void main(String[] args) throws PcapNativeException, NotOpenException {
		
		//silence Logger
		Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.toLevel("ERROR"));
		
		//open pcap
	    PcapHandle handle = Pcaps.openOffline(args[0]);
	    
	    //create map to store lists containing all outgoing
	    //communication from node
	    HashMap<String, LinkedList<String>> hmap = new HashMap<String, LinkedList<String>>();
	    LinkedList<String> val;
	    
	    
	    // move through PCAP file
	    while (true) {
	      try {
	        Packet packet = handle.getNextPacketEx();

	        // If packet has IP addresses, add the destination
	        // node to list of source
	        if (packet.contains(IpV4Packet.class)){
	        	String src = packet.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
	        	String dst = packet.get(IpV4Packet.class).getHeader().getDstAddr().getHostAddress();
	        	
	        	if (hmap.containsKey(src)) {
	        		val = hmap.get(src);
	        		val.add(dst);
	        		hmap.put(src, val);
	        	}
	        	else {
	        		val = new LinkedList<String>();
	        		val.add(dst);
	        		hmap.put(src, val);
	        	}
	        	
	        	
	        }

	      } catch (TimeoutException e) {
	      } catch (EOFException e) {
	        System.out.println("Reached end of file.");
	        break;
	      }
	    }
	    
	    
	    Graph graphStream = new SingleGraph("");
	    graphStream.addAttribute("ui.stylesheet", "graph {text-mode: normal;}");
	   
	    /*Add nodes and edges to the graph:
	    //move through the list of nodes, via their names in
	    //the map and find nodes that communicate back
	    //and create edges between them
	    */
	    
        //set of all possible nodes from data
	    Iterator<String> strset = hmap.keySet().iterator();
	    
	    LinkedList<String> str;
	    Iterator<String> iter;
        while (strset.hasNext()) {
	      	
	    	String s = strset.next();
	    	
	    	str = hmap.get(s);
	    	
	    	iter = str.descendingIterator();
	    	
	    	//move through the list of communication
	    	//
	    	while (iter.hasNext()) {
	    		
	    	   String k = iter.next();
	           LinkedList<String> str2 = hmap.get(k);
	    	   
	           if (str2 != null) {	
	             Iterator<String> iter2 = str2.descendingIterator();
	    		
	            //check to see if reverse communication
	    		//add nodes if not already in set
	    		//and create bi-directional edge
	    		 while (iter2.hasNext()) {
	    			
	    			String s2 = iter2.next();
	    			
	    			if (s2.equals(s)) {
	    				Node n;
	    				//check if nodes already exist
	    				if (graphStream.getNode(s) == null) {
	    					n = graphStream.addNode(s);
	    			    	n.addAttribute("ui.label", s);
	    			    	n.addAttribute("ui.style", "fill-color: rgb(0,100,255);");
	    			    	
	    				}
	    				if (graphStream.getNode(k) == null) {
	    					n = graphStream.addNode(k);
	    					n.addAttribute("ui.label", k);
	    					n.addAttribute("ui.style",  "fill-color: rgb(0,100,255);");
	    				}
	    				//check that edge was not already created from opposite node
	    				if (graphStream.getEdge(s + "-" + k) == null && graphStream.getEdge(k + "-" +s) == null) {
	    					graphStream.addEdge(s + "-" + k, s, k);
	    					}
	    				
	    				break;}
	    			}
	    	
	    	     }
	    	}
	    	
	    }
        
      
        
        
        //find the weight for each edge
        for (Edge d : graphStream.getEachEdge()) {
        	String x = d.getNode0().getAttribute("ui.label");
        	String y = d.getNode1().getAttribute("ui.label");
        	
        	int cntr = 0;
        	str = hmap.get(x);
        	iter = str.descendingIterator();
        	String c;
        	
        	while (iter.hasNext()) {
        		c = iter.next();
        		if (c.equals(y))
        			cntr++;
        		
        	}
        	
        	str = hmap.get(y);
        	iter = str.descendingIterator();
        	
        	while (iter.hasNext()) {
        		c = iter.next();
        		if (c.equals(x))
        			cntr++;
        	}
        	
        	d.addAttribute("weight", cntr);
        	d.addAttribute("ui.label", cntr);
        	
        	
        	
        }
	    
	    //create object that inputs a graph
        //and outputs the connected components
	    CC connexion = new CC(graphStream);
	    
	    //number of connected components
	    int compCount = connexion.count();
	    
	    //create an array of Graph objects
	    //with each graph representing 
	    //a connected component
	    Graph[] connexGraphs = new Graph[compCount];
	    int q;
	    for(q = 0; q < compCount; q++) {
	    	connexGraphs[q] = connexion.connexGraph(q);
	    }
	    
	    
	    
	    
	    
	    //sort the connected components by number of components
	    
	    Arrays.sort(connexGraphs, new Comparator<Graph>(){
	    	public int compare(Graph x, Graph y) {
	    		int x1 = x.getAttribute("num");
	    		int y1 = y.getAttribute("num");
	    		
	    		if (x1 > y1) return -1;
	    		else if (x1 < y1) return 1;
	    		else return 0;
	    	}
	    });
	    
	    graphStream.display(true);
	    
	    //create array of objects that
	    //inputs a connected component represented by a Graph object
	    //and outputs a maximum spanning tree represented by a Graph
        MaxST[] maxArr = new MaxST[compCount];
	    
	    
	    //get the maximum spanning tree of each
        //connected component
	    for (q = 0; q < compCount; q++) {
	    	Graph grph = connexGraphs[q];
	    	maxArr[q] = new MaxST(grph);
	    	
	    }
	   
	    
        PriorityQueue<Node> nodepq;
	    
	    
	    
	    
	    
	    //find the highest degree nodes in each component
        //and print the desired output
	    for (q = 0; q < compCount; q++) {
	    	Graph graph1 = connexGraphs[q];
	    	//get Graph of MST
	    	Graph graph2 = maxArr[q].maxTree();
	    	int count1 = maxArr[q].getSize();
	    	System.out.println("CC:" + q + ", " + count1 + " Nodes Total");
	    	System.out.println("=Without Maximum Spanning Tree:");
	    	
	    	//get priority queue from CC object
	    	//of nodes sorted by degree
	    	nodepq = connexion.listNodes(graph1);
	    	Node node1;
	    	if (count1 > 3) {
	    		for (int q2 = 0; q2 < 3; q2++) {
	    			node1 = nodepq.remove();
	    			System.out.print(node1);
	    			System.out.print(", " + node1.getDegree() + "\n");
	    		}
	    	}
	    	else {
	    		for (int q3 = 0; q3 < count1; q3++) {
	    			node1 = nodepq.remove();
	    			System.out.print(node1);
	    			System.out.print(", " + node1.getDegree() + "\n");
	    		}
	    	}
	    	//get other pq from MaxST for
	    	//max spanning tree Graph
	    	nodepq = connexion.listNodes(graph2);
	    	System.out.println("=With Maximum Spanning Tree:");
	    	if (count1 > 3) {
	    		for (int w1 = 0; w1 < 3; w1++) {
	    			node1 = nodepq.remove();
	    			System.out.print(node1);
	    			System.out.print(", " + node1.getDegree() + "\n");
	    		}
	    	}
	    	else {
	    		for (int w2 = 0; w2 < count1; w2++) {
	    			node1 = nodepq.remove();
	    			System.out.print(node1);
	    			System.out.print(", " + node1.getDegree() + "\n");
	    		}
	    	}
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
    
	    
	      
		
}
}


