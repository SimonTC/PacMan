package pacman.entries.pacman.searchPacMan.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class SearchPacMan extends Controller<MOVE>
{
	//Parameters
	private  int MAX_DEPTH; 
	private  int PILL_VALUE = 1;
	private  int POWER_PILL_VALUE = 2;
	private  int NONEDIBLE_GHOST_VALUE = -5;
	private  int EDIBLE_GHOST_VALUE = 5;
	private  int JUNCTION_VALUE = 0;
	private  int MIN_DISTANCE = 20;
	private  int MIN_EADIBLE_TIME = 10;
	private  DM DISTANCE_METRIC = DM.MANHATTAN;
	private 	int USE_MEMORY = 1;
	private Path currentPath = null;
	
	
	private MOVE myMove=MOVE.NEUTRAL;
	private ArrayList<Path> possiblePaths = new ArrayList<Path>();
	
	public SearchPacMan(){
		try {
			loadParameters("PacManParameters");
		} catch (FileNotFoundException e) {
			//System.out.println("Bruger standard parametre");
		}
	}
	/*
	 * Used when only depth is to be changed
	 */
	public SearchPacMan(int depth){
		MAX_DEPTH = depth; 
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{
		//Place your game logic here to play the game as Ms Pac-Man
		possiblePaths.clear();
		int maxDepth = MAX_DEPTH;
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		if (pacManIndex == game.getCurrentMaze().initialPacManNodeIndex){
			currentPath = null;
		}
		Node thisNode = new Node(game, pacManIndex, null);
		POWER_PILL_VALUE = calculatePowerPillValue(game, pacManIndex, MIN_DISTANCE, MIN_EADIBLE_TIME);
	//printNodeInfo(thisNode, game);
		Path startPath = null;
		if (currentPath == null){
			startPath = new Path(maxDepth, thisNode, PILL_VALUE, POWER_PILL_VALUE, NONEDIBLE_GHOST_VALUE, EDIBLE_GHOST_VALUE, JUNCTION_VALUE);
		} else {
			if (continueDownCurrentPath(game, currentPath)){
			startPath = currentPath;
			}else {
				startPath = new Path(maxDepth, thisNode, PILL_VALUE, POWER_PILL_VALUE, NONEDIBLE_GHOST_VALUE, EDIBLE_GHOST_VALUE, JUNCTION_VALUE);
			}
		}
		calculatePossiblePaths(game, maxDepth, startPath);
	//printPossiblePaths();
		Path optimalPath = findOptimalPath();
	//printOptimalPath(optimalPath);
		int nextNodeIndex = optimalPath.getNextNode().getNodeIndex();
		myMove = game.getNextMoveTowardsTarget(pacManIndex, nextNodeIndex, DISTANCE_METRIC);
		if (USE_MEMORY==1){
			currentPath = optimalPath;
		}
		return myMove;
	}
	/*
	 * Evaluates the current path to see if PacMan should continue this route
	 */
	private boolean continueDownCurrentPath(Game game, Path currentPath){
		currentPath.reEvaluateValue(game, POWER_PILL_VALUE);
		boolean startNodeIsJunction = currentPath.getStartNode().isJunction();
		boolean dangerousGhostIsNear = distanceToNearestNonEatableGhost(game) < MIN_DISTANCE;
		if (startNodeIsJunction || dangerousGhostIsNear){				
			return false;
		} else{
			return true;
		}


	}
	private void calculatePossiblePaths(Game game, int maxDepth, Path startPath){
		if (startPath.getNumberOfNodes() == maxDepth){
			possiblePaths.add(startPath);
		} else {
			Node parent = startPath.getLastNode();
			ArrayList<Node> children = parent.getChildren(game);
			for (Node n : children){
				Path newPath = new Path(maxDepth,startPath, PILL_VALUE, POWER_PILL_VALUE, NONEDIBLE_GHOST_VALUE, EDIBLE_GHOST_VALUE, JUNCTION_VALUE);
				newPath.addNode(n);
				calculatePossiblePaths(game, maxDepth, newPath);
			}
		}
	}
	private Path findOptimalPath(){
		int maxValue = -1;
		ArrayList <Path> optimalPaths = new ArrayList<Path>();
		for (Path p : possiblePaths){
			if (!p.hasNonEdibleGhost()){
				int pathValue = p.getValue();
				if (pathValue == maxValue){
					optimalPaths.add(p);
				} else if (pathValue > maxValue){
					optimalPaths.clear();
					optimalPaths.add(p);
					maxValue = pathValue;
				}
			}
		}
		Random rand = new Random();
		Path result;
		if (optimalPaths.isEmpty()){
			result = possiblePaths.get(rand.nextInt(possiblePaths.size()));
		} else{
			result = optimalPaths.get(rand.nextInt(optimalPaths.size()));
		}
		return result;
	}
	
	private int calculatePowerPillValue (Game game, int pacManIndex, int minDistance, int minEadibleTime){
		GHOST nearestFreeGhost = getNearestFreeGhost(game, pacManIndex);
		if (nearestFreeGhost == null){
			return -2;
		}
		double distToNearestGhost = distanceToNearestNonEatableGhost(game);
//System.out.println("Distance to nearest ghost: " + distToNearestGhost);
		if (distToNearestGhost < minDistance) {
			return 5;
		}
		
		if (game.getGhostEdibleTime(nearestFreeGhost)< minEadibleTime){
			return 0;
		} else {
			return -2;
		}
	}
	
	private double distanceToNearestNonEatableGhost(Game game){
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		GHOST nearestFreeGhost = getNearestFreeGhost(game, pacManIndex);
		if (nearestFreeGhost != null){
			int ghostIndex = game.getGhostCurrentNodeIndex(nearestFreeGhost);
			return game.getDistance(pacManIndex, ghostIndex, DISTANCE_METRIC);
		} else {
			return 10000;
		}
	}
	
	private GHOST getNearestFreeGhost(Game game, int pacManIndex){
		double distToNearestGhost = 10000;
		GHOST nearestGhost = null;
		for (GHOST ghost : GHOST.values()){
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			double distToGhost = game.getDistance(pacManIndex, ghostNode, DISTANCE_METRIC);
			int ghostLairTime = game.getGhostLairTime(ghost);
			if (distToGhost<distToNearestGhost && game.getGhostLairTime(ghost)==0){
				distToNearestGhost = distToGhost;
				nearestGhost = ghost;
			}
		}
		return nearestGhost;
	}
	
	private void printPossiblePaths(){
		for (Path p : possiblePaths){
			System.out.print("(" + p.getValue() + "):  " );
			for (Node n : p.getNodes()){
				System.out.print(n.getNodeIndex() + " - ");
			}
			System.out.println();
		}
	}
	
	/**
	 * How the file should look:
	 * maxDepth pillValue powerPillValue nonEdibleGhostValue EdibleGhostValue DistanceMeasurer JunctionValue useMemory
	 * @param textFileName
	 * @throws FileNotFoundException
	 */
	private void loadParameters(String textFileName) throws FileNotFoundException{
		File file = new File(textFileName + ".txt");
		Scanner input = new Scanner(file);
		
		while (input.hasNext()){
			MAX_DEPTH = input.nextInt();
			if (MAX_DEPTH < 1){
				MAX_DEPTH = 1;
			} else if (MAX_DEPTH > 100){
				MAX_DEPTH = 100;
			}
			PILL_VALUE = input.nextInt();
			POWER_PILL_VALUE = input.nextInt();
			NONEDIBLE_GHOST_VALUE = input.nextInt();
			EDIBLE_GHOST_VALUE = input.nextInt();
			DISTANCE_METRIC = parseDistanceMetric(input.nextInt());
			JUNCTION_VALUE = input.nextInt();
			USE_MEMORY = input.nextInt();
			
		}
	}
	
	private DM parseDistanceMetric(int i){
		switch(i){
		case 0: return DM.EUCLID;
		case 1: return DM.MANHATTAN;
		case 2: return DM.PATH;
		default: return DM.PATH;
		}
	}
	private void printNodeInfo(Node node, Game game){
		String nodeIndex = "" + node.getNodeIndex();
		Node parentNode = node.getParent();
		String parent;
		if (parentNode != null){
			parent = "" + parentNode.getNodeIndex();
		} else{
			parent = "Null";
		}
		
		ArrayList<Node> children = node.getChildren(game);
		String kids ="";
		for (Node n : children){
			kids = kids + n.getNodeIndex() + "    ";
		}
		String pill = "" + node.hasPill();
		String powerPill = "" + node.hasPowerPill();
		String ghost = "" + node.hasNonEdibleGhost();
		
		System.out.println(nodeIndex + "    " + parent + "    " + kids + pill + "    " + powerPill + "    " + ghost);
	}
	
	private void printOptimalPath(Path p){
		System.out.print("Optimal path: ");
		System.out.print("(" + p.getValue() + "):  " );
		for (Node n : p.getNodes()){
			System.out.print(n.getNodeIndex() + " - ");
		}
		System.out.println();
	}
	
}


