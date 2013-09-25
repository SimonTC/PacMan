package pacman.entries.pacman.mctsPacMan;

import java.util.ArrayList;

public class Node {
	Node parent;
	ArrayList<Node> children = new ArrayList<>();
	double qValue =0.0d;
	int nodeIndex;
	int timesVisited;
	int distanceToParent;
	
	public Node(int nodeIndex, Node parent, int distanceToParent){
		this.nodeIndex = nodeIndex;
		this.parent = parent;
		this.distanceToParent = distanceToParent;
	}
}
