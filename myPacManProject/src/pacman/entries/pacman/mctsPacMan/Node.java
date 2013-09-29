package pacman.entries.pacman.mctsPacMan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class Node {
	private Node parent;
	private ArrayList<Node> children = new ArrayList<>();
	private double qValue =0.0d;
	private int nodeIndex;
	private int timesVisited;
	private int distanceToParent;
	private Game gameState;
	
	public Node(int nodeIndex, Node parent, int distanceToParent){
		this.nodeIndex = nodeIndex;
		this.parent = parent;
		this.distanceToParent = distanceToParent;
		this.timesVisited = 0;
	}
	
	public void buildTree(int curDepth, int maxDepth, Game game){
		int distToMe = curDepth + this.distanceToParent;
		if (distToMe >= maxDepth){
			return;
		}
		
		findChildren(game);
		Iterator<Node>i = children.iterator();
		while (i.hasNext()){
			Node n = i.next();
			if (n.distanceToParent + distToMe <= maxDepth){
				n.buildTree(distToMe, maxDepth, game);
			} else{
				//If the total distance to the new child is too big, 
				//it is removed from the tree
				i.remove();
			}
		}
		
	}
	/**
	 * removes children that are also the children of the parent
	 * (I.e. brothers)
	 */
	public void removeIncest(){
		if (!children.isEmpty()){
			Iterator<Node>i = children.iterator();
			while (i.hasNext()){
				Node n = i.next();
				if (isBrother(n)){
					i.remove();
				} else {
					n.removeIncest();
				}
			}
			//printFamily();
		}
	}
	
	private boolean isBrother(Node nodeToTest){
		if (parent!= null){
			for (Node n : parent.children){
				if (n.nodeIndex == nodeToTest.nodeIndex){
					return true;
				}
			}
		}
		return false;
	}
	
	private void printFamily(){
		String offSpring ="";
		for (Node n : children){
			offSpring += "(I: " + n.nodeIndex() + ", D: " + n.distanceToParent() + ") ";
		}
		String s;
		if (parent == null){
			s = "Me: " + nodeIndex + ", Parent: {None} Children: {" + offSpring + "}";
		} else {
			s = "Me: " + nodeIndex + ", Parent: {I: " + parent.nodeIndex + "} Children: {" + offSpring + "}";
		}
		System.out.println(s);
	}
	
	/**
	 * Searches for directly connected junctions and adds them to the children list.
	 * @param game
	 */
	private void findChildren(Game game){
		int parentIndex = -1;
		if (this.parent != null){
			parentIndex = this.parent().nodeIndex();
		}
		
		int[] neighbours = game.getNeighbouringNodes(nodeIndex);
		int childIndex;
		for (int i : neighbours){
			childIndex = nextJunctionIndex(i, nodeIndex, game);
			if (childIndex != parentIndex){
				int distanceToParent = game.getShortestPathDistance(nodeIndex, childIndex);
				children.add(new Node(childIndex, this, distanceToParent));
			}
		}
}
	/**
	 * Returns the next junctions index.
	 * A junction is a node with more than 2 neighbouring nodes
	 * @param index
	 * @param parentIndex
	 * @param startIndex - the index at the root of the tree
	 * @param game
	 * @return
	 */
	private int nextJunctionIndex(int index, int parentIndex, Game game){
		int[] neighbours = game.getNeighbouringNodes(index);
		if (neighbours.length > 2){
			return index;
		}
		
		for (int i : neighbours){
			if (i != parentIndex ){
				return nextJunctionIndex(i, index, game);
			}
		}
		//Will never reach here. There will always be at least one 
		//other neighbour than the parent
		return 0;
	}
	
	
	public Node parent(){
		return this.parent;
	}
	
	public ArrayList<Node> children(){
		return this.children;
	}
	
	public double qValue(){
		return this.qValue;
	}
	
	public int nodeIndex(){
		return this.nodeIndex;
	}
	
	public int timesVisited(){
		return this.timesVisited;
	}
	
	public int distanceToParent(){
		return this.distanceToParent;
	}
	
	public void colorFamily(Game game){
		if (parent != null){
			GameView.addLines(game, Color.BLUE, nodeIndex, parent.nodeIndex);
			GameView.addPoints(game, Color.BLUE, this.nodeIndex);
		} else{
			GameView.addPoints(game, Color.green, this.nodeIndex);
		}
		
		if (children.isEmpty()){
			return;
		}

		for (Node n: children){
			n.colorFamily(game);
		}
	}
	public boolean fullyExpanded(){
		for (Node n : children){
			if (n.timesVisited() == 0){
				return false;
			}
		}
		return true;
	}
	
	public void setGameState (Game gameCopy){
		this.gameState=gameCopy;
	}
	public Game getGameState (){
		return this.gameState;
	}
	
	public void incrementTimesVisited(){
		timesVisited++;
	}
	
	public void setQvalue(double newValue){
		qValue = newValue;
	}
	
}
