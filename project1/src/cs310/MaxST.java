package cs310;

import java.util.*;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;


public class MaxST {
	
	private boolean[] marked;
	private LinkedList<Edge> maxST;
	private PriorityQueue<Edge> pq;
	private HashMap<String,Integer> hmap;
	private String[] index;
	int cnt, weight;
	private Graph cpy;
	
	public MaxST(Graph g) {
		weight = 0;
		maxST = new LinkedList<Edge>();
		//count how many nodes in graph
		cnt = 0;
		for (Node N : g.getEachNode()) {
			cnt++;
		}
		//create array to hold names of Nodes
		index = new String[cnt];
		int j = 0;
		for (Node m : g.getEachNode()) {
			index[j] = m.getAttribute("ui.label");
			j++;
		}
		//create hash lookup based on name of Node
		hmap = new HashMap<String, Integer>();
		
		for (int i = 0; i < cnt; i++) {
			hmap.put(index[i], i);
		}
		
		//create MaxPQ that compares by attribute "weight"
		pq = new PriorityQueue<Edge>(100, new Comparator<Edge>() {
			
			public int compare(Edge x, Edge y) {
				Integer o1 = x.getAttribute("weight");
				Integer o2 = y.getAttribute("weight");
				
				if (o1 > o2) return -1;
				else if (o2 > 01) return 1;
				else return 0;
			}
		});
		//boolean array to mark as visited
	    marked = new boolean[cnt];
	    
	    //run prims for each Node
	    for (int v = 0; v < cnt; v++) {
	    	if (!marked[v]) prim(g, v);
	    }
		
	    
	}
	
	//prims algorithm
	private void prim(Graph g, int s) {
		//find adjacent edges at source
		scan(g, s);
		while (pq.size() != 0) {
			//take the edge with largest weight
			Edge e = pq.remove();
			
			Node m1, m2;
			//find two attached nodes
			m1 = e.getSourceNode();
			m2 = e.getOpposite(m1);
			int i1, i2;
			i1 = hmap.get(m1.getAttribute("ui.label"));
			i2 = hmap.get(m2.getAttribute("ui.label"));
			
			assert marked[i1] || marked[i2];
			//check if edge makes cycle
			if (marked[i1] && marked[i2]) continue;
			
			maxST.addFirst(e);
			Integer k = e.getAttribute("weight");
			
			weight += k;
			//find adjacent edges to new node, unless tree is complete
			if (!marked[i1]) scan(g, i1);
			if (!marked[i2]) scan(g, i2);
			
			
			
			
			
		}
		
	    
	    
	    
	    
	}
	//helper method for prims
	
	private void scan(Graph g, int v) {
		assert !marked[v];
		marked[v] = true;
		Node m = g.getNode(index[v]);
		
		//add adjacent edges to PQ if appropriate
		for (Edge e : m.getEachEdge()) {
			Node n = e.getOpposite(m);
			if (!marked[hmap.get(n.getAttribute("ui.label"))]) {
				pq.add(e);
			}
			
		}
	}
	
	public Iterable<Edge> edges() {
		return maxST;
	}
    
	//create a graph representing MaxST
	public Graph maxTree() {
		Graph maxTree = new SingleGraph("");
		Queue<Edge> copy = maxST;
		
		while (!copy.isEmpty()) {
			
			Edge max = copy.remove();
			Node n1 = max.getSourceNode();
			String src = n1.getAttribute("ui.label");
			Node n2 = max.getOpposite(n1);
			String dst = n2.getAttribute("ui.label");
			int weight1 = max.getAttribute("weight");
			
			if (maxTree.getNode(src) == null) {
				n1 = maxTree.addNode(src);
				n1.addAttribute("ui.label", src);
				}
			if (maxTree.getNode(dst) == null) {
				n2 = maxTree.addNode(dst);
				n2.addAttribute("ui.label", dst);
			}
			
			max = maxTree.addEdge(src + "-" + dst, src, dst);
			max.addAttribute("weight", weight1);
			max.addAttribute("ui.label", weight1);
			
		}
		
		return maxTree;
		
	}
	
	public int getWeight() {
		return weight; }
	
	public int getSize() {
		return cnt;
	}
	
	
	
	
	}

    

