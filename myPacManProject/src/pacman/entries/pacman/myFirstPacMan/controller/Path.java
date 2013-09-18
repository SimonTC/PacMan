package pacman.entries.pacman.myFirstPacMan.controller;

import java.util.ArrayList;

import pacman.game.Game;

public class Path {
	private ArrayList<Node> nodes;
	private Node startNode;
	private int value = 0;
	private boolean hasNonEdibleGhost = false;
	private int maxDepth;
	private int PILL_VALUE;
	private int POWER_PILL_VALUE;
	private int NONEDIBLE_GHOST_VALUE;
	private int EDIBLE_GHOST_VALUE;
	private int JUNCTION_VALUE;
	
	/**
	 * Used when starting a new path from scratch
	 * @param maxDepth
	 * @param startNode
	 */
	public Path(int maxDepth, Node startNode, int pillValue, int powerPillValue, int nonEdibleGhostValue, int edibleGhostValue, int junctionValue){
		//Loading attributes
		this.maxDepth = maxDepth;
		this.PILL_VALUE=pillValue;
		this.POWER_PILL_VALUE = powerPillValue;
		this.NONEDIBLE_GHOST_VALUE = nonEdibleGhostValue;
		this.EDIBLE_GHOST_VALUE = edibleGhostValue;
		this.JUNCTION_VALUE = junctionValue;
		nodes = new ArrayList<Node>();
		this.addNode(startNode);
		this.startNode = startNode;
		
	}
	/**
	 * Used when continuing on an old path
	 * @param maxDepth
	 * @param startOfPath
	 */
	public Path(int maxDepth, Path startOfPath, int pillValue, int powerPillValue, int nonEdibleGhostValue, int edibleGhostValue, int junctionValue){
		//Loading attributes
		this.maxDepth = maxDepth;
		this.PILL_VALUE=pillValue;
		this.POWER_PILL_VALUE = powerPillValue;
		this.NONEDIBLE_GHOST_VALUE = nonEdibleGhostValue;
		this.EDIBLE_GHOST_VALUE = edibleGhostValue;
		this.JUNCTION_VALUE = junctionValue;
		
		nodes = new ArrayList<Node>();
		this.value = startOfPath.getValue();
		this.hasNonEdibleGhost=startOfPath.hasNonEdibleGhost();
		this.startNode = startOfPath.getStartNode();
		copyPath(startOfPath);
	}
	
	public boolean addNode(Node node){
		if (nodes.size() == maxDepth ){
			return false;
		}
		nodes.add(node);
		value = value + calculateNodeValue(node);
		if (node.hasNonEdibleGhost()){
			this.hasNonEdibleGhost = true;
		}
		return true;
	}
	
	public void reEvaluateValue(Game game, int powerPillValue){
		value = 0;
		POWER_PILL_VALUE = powerPillValue;
		for (Node n : nodes){
			n.reEvaluateNode(game);
			n.setValue(calculateNodeValue(n));
			value = value + n.getValue();
		}
	}
	
	private int calculateNodeValue(Node node){
		int result = 0;
		if (node.hasPill()){
			result = result + PILL_VALUE;
		} else if (node.hasPowerPill()){
			result = result + POWER_PILL_VALUE;
		}
		if(node.hasNonEdibleGhost()){
			result = result + NONEDIBLE_GHOST_VALUE;
		} else if (node.hasEdibleGhost()){
			result = result + EDIBLE_GHOST_VALUE;
		}
		if (node.isJunction()){
			result = result + JUNCTION_VALUE;
		}
		return result;
		
	}
	
	private void copyPath(Path path){
		for (Node n : path.getNodes()){
			this.nodes.add(n);
		}
	}
	
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public boolean hasNonEdibleGhost(){
		return this.hasNonEdibleGhost;
	}
	
	public Node getStartNode(){
		return this.startNode;
	}
	
	public Node getLastNode(){
		return nodes.get(nodes.size() - 1);		
	}
	
	public int getNumberOfNodes(){
		return nodes.size();
	}
	
	public Node getNextNode(){
		Node nodeToReturn;
		if (nodes.size() <= 1){
			nodeToReturn = nodes.get(0);
			nodes.remove(0);
		} else {
			nodeToReturn = nodes.get(1);
			nodes.remove(0);
		}
		return nodeToReturn;
	}

}
