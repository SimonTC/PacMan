package pacman.entries.pacman.searchPacMan.controller;

import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class Node {
	private Node parent;
	private int value = 0;
	private boolean hasNonEdibleGhost = false;
	private boolean hasEdibleGhost = false;
	private boolean hasPill = false;
	private boolean hasPowerPill = false;
	private boolean isJunction = false;
	private int nodeIndex;
	private ArrayList<Node> children = new ArrayList<Node>();
	
	public Node(Game game, int nodeIndex, Node parent){
		this.nodeIndex=nodeIndex;
		this.parent = parent;
		evaluateNode(game);
		
	}
	
	public void reEvaluateNode (Game game){
		evaluateNode(game);		
	}
	
	private void evaluateNode (Game game){
		//test for ghosts
		hasNonEdibleGhost = nodeContainsNonEdibleGhost(game);
		hasEdibleGhost = nodeContainsEdibleGhost(game);
		
		//Test for pills
		hasPill = nodeHasPill(game);
		hasPowerPill = nodeHasPowerPill(game);
		
		//Test if it is in the middle of a junction (3 or more neighbours)
		isJunction = nodeIsAJunction(game);
		
	}
	
	private boolean nodeContainsNonEdibleGhost(Game game){
		for(GHOST ghost : GHOST.values()){
			if (game.getGhostCurrentNodeIndex(ghost)==nodeIndex){
				if (!game.isGhostEdible(ghost)){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean nodeContainsEdibleGhost(Game game){
		for(GHOST ghost : GHOST.values()){
			if (game.getGhostCurrentNodeIndex(ghost)==nodeIndex){
				if (game.isGhostEdible(ghost)){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean nodeHasPill(Game game){
		int pillIndex = game.getPillIndex(nodeIndex);
		if (pillIndex !=-1){
			if (game.isPillStillAvailable(pillIndex)){
				return true;
			}
		}
		return false;
	}
	private boolean nodeHasPowerPill(Game game){
		int powerPillIndex = game.getPowerPillIndex(nodeIndex);
		if ( powerPillIndex !=-1){
			if (game.isPowerPillStillAvailable(powerPillIndex)){
				return true;
			}
		}
		return false;
	}
	
	private boolean nodeIsAJunction(Game game){
		int[] junctions = game.getJunctionIndices();
		for (int i: junctions){
			if (i == nodeIndex){
				return true;
			}
		}
		return false;
	}
	public ArrayList<Node> getChildren(Game game){
		if (children.isEmpty()){
			int parentIndex = -1;
			if (parent != null) {
				parentIndex = parent.getNodeIndex();
			}
			int[] childrenIndex = game.getNeighbouringNodes(nodeIndex);
			for (int n : childrenIndex){
				if (n != parentIndex){
					Node child = new Node(game,n,this);
					children.add(child);
				}
			}
		}
		return children;
	}
	
	public Node getParent(){
		return parent;
	}
	
	public void setValue (int newValue){
		this.value=newValue;
	}
	
	public int getValue(){
		return value;
	}
	
	public boolean hasNonEdibleGhost(){
		return this.hasNonEdibleGhost;
	}
	
	public boolean hasEdibleGhost(){
		return this.hasEdibleGhost;
	}
	
	public boolean hasPill(){
		return this.hasPill;
	}
	
	public boolean hasPowerPill(){
		return this.hasPowerPill;
	}
	
	public int getNodeIndex(){
		return this.nodeIndex;
	}
	
	public boolean isJunction(){
		return this.isJunction;
	}
}
