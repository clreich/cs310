package cs310;
import java.util.*;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;



public class CC {
	private String[] index1;
	private boolean[] mark;
	
	private int[] id;
	private int count1, count2;
	private HashMap<String, Integer> hm;
	private Graph cpy; 
	/*
	 * Creates an object that inputs a Graph
	 * and then finds each connected component
	 */
	public CC(Graph g) {
		 cpy = g;
		 count2 = 0;
		 //find how many nodes the graph has
		 //in order to create an array
		 for (Node N : g.getEachNode()) {
			 count2++;
		 }
		 //index of nodes
		 index1 = new String[count2];
		 int j = 0;
		 for (Node N : g.getEachNode()) {
			 index1[j] = N.getAttribute("ui.label");
			 j++;
		 }
		 //map of each node's index
		 hm = new HashMap<String, Integer>();
		 for (int t = 0; t < count2; t++) {
			 hm.put(index1[t], t);
		 }
		 
         //index of visited nodes
		 mark = new boolean[count2];
		 //associates nodes with a group number
	     //representing a connected component
		 id = new int[count2];
		 
		 
		 for (int s = 0; s < count2; s++) {
			 if (!mark[s]) {
				 dfs(g, s);
				 count1++;
			 }
		 }
	}
	
	//depth first search
	private void dfs(Graph g, int v) {
		String s = index1[v];  
        Node N = g.getNode(s);
		mark[v] = true;
		id[v] = count1;
		//get a list of all connected nodes
		Iterator<Node> it = N.getNeighborNodeIterator();
		//recursively search
		while (it.hasNext()) {
			Node M = it.next();
			int w = hm.get(M.getAttribute("ui.label"));
			if (!mark[w])
				dfs(g, w);
			 }
		 }
	
	public boolean connected(String x, String y) {
	            return id[hm.get(x)] == id[hm.get(y)];
	}
	
	public int id(String k) {
		return id[hm.get(k)];
	}
	
	
	//returns Graph representing a connected component
	public Graph connexGraph(int k) {
		int len = id.length;
		Graph g = new SingleGraph("");
		int cntr1 = 0;
		for (int i = 0; i < len; i++) {
			if (id[i] == k) {
				String s = index1[i];
				Node n = g.addNode(s);
				n.addAttribute("ui.label", s);
				cntr1++;
			}
		}
		g.addAttribute("num", cntr1);
		
		for (Node n : g.getEachNode()) {
			String x = n.getAttribute("ui.label");
			Node m = cpy.getNode(x);
			
			for (Edge e : m.getEachEdge()) {
				Node f = e.getOpposite(m);
				String src = f.getAttribute("ui.label");
				if (g.getEdge(src + "-" + x) == null && g.getEdge(x + "-" + src) == null) {
				    Edge e2 = g.addEdge(src + "-" + x, src, x);
				    int weight1 = e.getAttribute("weight");
				    e2.addAttribute("weight", weight1);
				    e2.addAttribute("ui.label", weight1);
				    
				    
				}
				
			}
		}
		
		return g;
	}
	
	public int count() {
		return count1;
	}
	
	public HashMap<String, Integer> getMap() {
		return hm;
	}
	//return a pq of nodes sorted by degree
	public PriorityQueue<Node> listNodes(Graph g) {
		
		PriorityQueue<Node> nodeq = new PriorityQueue<Node>(50, new Comparator<Node>() {
			
			public int compare(Node x, Node y) {
				int xdegree = x.getDegree();
				int ydegree = y.getDegree();
				
				if (xdegree > ydegree) return -1;
				else if (xdegree < ydegree) return 1;
				else return 0;
			}
		}
		);
		
		for (Node n : g.getEachNode()) {
			nodeq.add(n);
		}
		
		return nodeq;
		
	}
	
	}
	


