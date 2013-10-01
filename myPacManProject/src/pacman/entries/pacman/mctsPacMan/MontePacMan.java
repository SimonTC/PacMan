package pacman.entries.pacman.mctsPacMan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.MOVE;

public class MontePacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	private enum STATE {HUNT_PILLS, HUNT_POWERPILLS, HUNT_GHOSTS, FLEE};
	private final int MAX_TREE_DEPTH = 50;
	private final double EXPLORATION = 1.5d;
	private int currentGoalNode = -1;
	private STATE curState;
	//private final long DEBUG_DELAY = 10000000;
	private final long DEBUG_DELAY = 40;
	long timeDue;

	public MOVE getMove(Game game, long timeDue) 
	{
		if (game.wasPacManEaten()){
			currentGoalNode = -1;
		}
		if (timeDue == -1){
			this.timeDue = System.currentTimeMillis() + DEBUG_DELAY;
		} else {
			this.timeDue = timeDue;
		}
		Game gameCopy = game.copy();
		curState = getCurrentPacManState(gameCopy);
		int pIndex = gameCopy.getPacmanCurrentNodeIndex();
		if (pIndex == currentGoalNode || currentGoalNode == -1 ){
			Node goal = getNextGoalNode(gameCopy);
			currentGoalNode = goal.nodeIndex();
			myMove = gameCopy.getNextMoveTowardsTarget(pIndex, currentGoalNode, DM.MANHATTAN);
		} else {
			myMove = gameCopy.getNextMoveTowardsTarget(pIndex, currentGoalNode, DM.MANHATTAN);
		}
		GameView.addPoints(game, Color.GREEN, currentGoalNode);
		return myMove;
	}
	
	private STATE getCurrentPacManState(Game game){
		GHOST nearestGhost = nearestGhost(game);
		int pIndex = game.getPacmanCurrentNodeIndex();
		double distToNearestGhost = Double.POSITIVE_INFINITY;
		boolean ghostIsEatable = false;
		if (nearestGhost != null){
			int ghostIndex = game.getGhostCurrentNodeIndex(nearestGhost(game));
			distToNearestGhost = game.getDistance(pIndex, ghostIndex, DM.MANHATTAN);
			ghostIsEatable = game.getGhostEdibleTime(nearestGhost) > 5;
		}
		int powerPillIndex = nearestPowerPillIndex(game);
		
		double distToNearestPowerPill = game.getDistance(pIndex, powerPillIndex, DM.MANHATTAN);
		
		
		if (distToNearestGhost < 10 && !ghostIsEatable){
			return STATE.FLEE;
		} 
		if (ghostIsEatable){
			return STATE.HUNT_GHOSTS;
		}
		if (distToNearestPowerPill < 30 && ghostIsEatable){
			return STATE.HUNT_POWERPILLS;
		} 
		return STATE.HUNT_PILLS;
	}
	
	private int nearestPowerPillIndex (Game game){
		int pIndex = game.getPacmanCurrentNodeIndex();
		int[] arr = game.getActivePowerPillsIndices();
		return game.getClosestNodeIndexFromNodeIndex(pIndex, arr, DM.MANHATTAN);
	}
	
	
	private GHOST nearestGhost(Game game){
		double minDist = Double.POSITIVE_INFINITY;
		int pIndex = game.getPacmanCurrentNodeIndex();
		GHOST nearestGhost = null;
		for (GHOST g : GHOST.values()){
			if (game.getGhostLairTime(g)==0){
				int ghostIndex = game.getGhostCurrentNodeIndex(g);
				double dist = game.getDistance(pIndex, ghostIndex, DM.MANHATTAN);
				if (dist < minDist){
					minDist=dist;
					nearestGhost = g;
				}
			}
		}
		return nearestGhost;
	}
	/**
	 * Using mcts to get the next goal node
	 * @param gameCopy
	 * @return
	 */
	private Node getNextGoalNode(Game gameCopy){
		int pacManIndex = gameCopy.getPacmanCurrentNodeIndex();
		Node rootNode = new Node(pacManIndex, null, 0);
		buildSearchTree(rootNode, gameCopy);
		rootNode.setGameState(gameCopy.copy());
		long curTime;
				
		do{
			Node leafNode = selectNextNode(rootNode);
			//Simulate game and calculate score
			int simScore = simulation(leafNode, rootNode);
			
			//Backpropagate
			Node n = leafNode;
			do{
				n.incrementTimesVisited();
				double oldValue = n.qValue();
				n.setQvalue(oldValue + (double) simScore);
				n = n.parent();
			} while(n!=null);
			curTime = System.currentTimeMillis();
		} while (timeDue - curTime > 10);
		
		Node bestNode = getNextNode(rootNode);
		
		return bestNode;

	}
	
	private Node getNextNode(Node parent){
		Node bestChild = null;
		double uctValueMax = Double.NEGATIVE_INFINITY;
		double uctValue = 0.0;
		for (Node n: parent.children()){
			uctValue = n.qValue();
			if (uctValue>uctValueMax){
				uctValueMax = uctValue;
				bestChild = n;
			}
		}
		return bestChild;
	}
	/**
	 * Simulates a game from the state given by the startNode
	 * @param leafNode The node from which the simulation should start
	 * @param rootNode The root node of the search tree. Used in calculating points
	 * @return
	 */
	private int simulation(Node leafNode, Node rootNode){
		Game rootGame = rootNode.getGameState().copy();
		Game gameCopy = leafNode.getGameState().copy();
		boolean stopSimulation = false;
		int i = 0;
		do {
			int pIndex = gameCopy.getPacmanCurrentNodeIndex();
			MOVE pMove = nextSimulationMove(pIndex, gameCopy);
			EnumMap<GHOST,MOVE> gMoves = getGhostMoves(gameCopy, pIndex);
			gameCopy.advanceGame(pMove, gMoves);
			i++;
			if (i == 20 || gameCopy.wasPacManEaten()){
				stopSimulation = true;
			}
		} while (!stopSimulation);
		
		if (gameCopy.wasPacManEaten()){
			return 0;
		} else {
			return getPoints(rootGame, gameCopy);
		}
	}
	/**
	 * Calculates how many points PacMan has earned based on the current strategy
	 * @param game
	 * @return
	 */
	private int getPoints(Game stateBeforeSim, Game stateAfterSim){
		//Strategies not implemented yet
		switch (curState){
		case FLEE: 
			if (stateAfterSim.wasPacManEaten()){
				return 0;
			} else {
				return 1;
			}
		case HUNT_POWERPILLS:
			if (stateAfterSim.wasPowerPillEaten()){
				return 10;
			} else {
				return 0;
			}
		case HUNT_PILLS:
			int pillsBefore = stateBeforeSim.getNumberOfActivePills();
			int pillsAfter = stateAfterSim.getNumberOfActivePills();
			return pillsBefore - pillsAfter;
		
		case HUNT_GHOSTS:
			int points = 0;
			for (GHOST g : GHOST.values()){
				int lairTimeBefore = stateBeforeSim.getGhostLairTime(g);
				int lairTimeAfter = stateAfterSim.getGhostLairTime(g);
				if(lairTimeBefore == 0 && lairTimeAfter > 0){
					points += 10;
				}
			}
			return points;
		default:
			return 0;
		} 
	}
	/**
	 * STRATEGIES NOT IMPLEMENTED!!!
	 * Returns the next move pacman should take in the simulation based on the current
	 * simulation strategy.
	 * 
	 * @param pacManIndex
	 * @param gameCopy
	 * @return
	 */
	private MOVE nextSimulationMove(int pacManIndex, Game gameCopy){
		switch (curState){
		case HUNT_POWERPILLS: return nextMoveToPowerPill(pacManIndex, gameCopy);
		case HUNT_PILLS: return nextMoveToPill(pacManIndex, gameCopy);
		case HUNT_GHOSTS: return nextMoveToGhost(pacManIndex, gameCopy);
		case FLEE: return nextMoveAwayFromGhost(pacManIndex, gameCopy);
		default: return MOVE.NEUTRAL;
		}
		
		
	}
	
	private MOVE nextMoveToPowerPill(int pacManIndex, Game gameCopy){
		int[] targetNodes = gameCopy.getActivePowerPillsIndices();
		int closestNode = gameCopy.getClosestNodeIndexFromNodeIndex(pacManIndex, targetNodes, DM.MANHATTAN);
		return gameCopy.getNextMoveTowardsTarget(pacManIndex, closestNode, DM.MANHATTAN);
	}
	
	private MOVE nextMoveToPill(int pacManIndex, Game gameCopy){
		int[] targetNodes = gameCopy.getActivePillsIndices();
		int closestNode = gameCopy.getClosestNodeIndexFromNodeIndex(pacManIndex, targetNodes, DM.MANHATTAN);
		return gameCopy.getNextMoveTowardsTarget(pacManIndex, closestNode, DM.MANHATTAN);
	}
	
	private MOVE nextMoveToGhost(int pacManIndex, Game gameCopy){
		GHOST g = nearestGhost(gameCopy);
		int gIndex = gameCopy.getGhostCurrentNodeIndex(g);
		return gameCopy.getNextMoveTowardsTarget(pacManIndex, gIndex, DM.MANHATTAN);
	}
	
	private MOVE nextMoveAwayFromGhost(int pacManIndex, Game gameCopy){
		GHOST g = nearestGhost(gameCopy);
		int gIndex = gameCopy.getGhostCurrentNodeIndex(g);
		return gameCopy.getNextMoveAwayFromTarget(pacManIndex, gIndex, DM.MANHATTAN);
	}
	
	private void buildSearchTree(Node startNode, Game game){
		startNode.buildTree(0, MAX_TREE_DEPTH, game);
		startNode.removeIncest();
	}
	
	private Node selectNextNode(Node rootNode){
		Game gameCopy;
		boolean pacManWasEaten, powerPillWasEaten, noChildren;
		Node startNode = rootNode;
		Node endNode = rootNode;
		do {
			if (!startNode.fullyExpanded()){
				endNode = expandNode(startNode);
				if (goToNextState(rootNode, endNode)){;
					return endNode;
				} else {
					return rootNode;
				}
			} else {
				startNode = getBestChild(startNode, EXPLORATION);
				if (!goToNextState(rootNode, startNode)){
					startNode = rootNode;
				}
			}
			gameCopy = startNode.getGameState();
			pacManWasEaten=gameCopy.wasPacManEaten(); 
			powerPillWasEaten = gameCopy.wasPowerPillEaten();
			noChildren = startNode.children().isEmpty();
		} while (!pacManWasEaten && !powerPillWasEaten && !noChildren );
		return startNode;
	}
	/**
	 * Returns the next child node that has not been expanded
	 * @param parent
	 * @return
	 */
	private Node expandNode(Node parent){
		for (Node child: parent.children()){
			if (child.timesVisited()==0){
				return child;
			}
		}
		return null; //Should not reach here because method is only called when there are unexplored children
	}
	/**
	 * Returns the best child node to move to based on the UCT of the child and the
	 * exploration constant
	 * @param parent
	 * @param explorationConstant
	 * @return
	 */
	private Node getBestChild(Node parent, double explorationConstant ){
		Node bestChild = null;
		double uctValueMax = 0.0;
		double uctValue = 0.0;
		for (Node n: parent.children()){
			uctValue = calculateUCT(parent, n, explorationConstant);
			if (uctValue>uctValueMax){
				uctValueMax = uctValue;
				bestChild = n;
			}
		}
		return bestChild;
	}
	/**
	 * Calculates the UCT of a node
	 * @param parent  
	 * @param child the node for which the UCT is calculated
	 * @param explorationConstant
	 * @return
	 */
	private double calculateUCT(Node parent, Node child, double explorationConstant){
		double leftPart = (double) (child.qValue()/child.timesVisited());
		double rightPart = (double) explorationConstant * Math.sqrt((2*Math.log(parent.timesVisited())/child.timesVisited()));
		double result = (double) leftPart + rightPart;
		return result;
	}
	/**
	 * Runs the game game copy until pacman is at the goalNodes node index
	 * The ghost are by default moving towards pacman if they are non-eatable
	 * @param startNode
	 * @param goalNode
	 * @return true if pacman arrived succesfully at the goal node's index
	 */
	private boolean goToNextState(Node startNode, Node goalNode){
		Game gameCopy = startNode.getGameState().copy();

		int goalIndex = goalNode.nodeIndex();
		int pIndex = gameCopy.getPacmanCurrentNodeIndex();
		
		boolean pacManWasEaten, powerPillWasEaten;
		
		do{
			MOVE pMove = getNextPacManMove(gameCopy, pIndex, goalIndex);
			EnumMap<GHOST,MOVE> gMoves = getGhostMoves(gameCopy, pIndex);
			gameCopy.advanceGame(pMove, gMoves);
			pIndex = gameCopy.getPacmanCurrentNodeIndex();
			pacManWasEaten=gameCopy.wasPacManEaten();
			powerPillWasEaten = gameCopy.wasPowerPillEaten();
		} while (pIndex!=goalIndex && !pacManWasEaten);
		
		if (pacManWasEaten){
			return false;
		} else {
			goalNode.setGameState(gameCopy.copy());
			return true;
		}

	}
	/**
	 * Returns the moves for the ghosts.
	 * dangerous ghosts will move towards pacman.
	 * non-dangerous ghosts will move away from pacman.
	 * @param gameCopy
	 * @return
	 */
	private EnumMap<GHOST,MOVE> getGhostMoves(Game gameCopy, int pacManIndex){
		EnumMap<GHOST,MOVE> map= new EnumMap<GHOST, MOVE>(GHOST.class) ;
		for (GHOST g : GHOST.values()){
			int gIndex = gameCopy.getGhostCurrentNodeIndex(g);
			MOVE lastMove = gameCopy.getGhostLastMoveMade(g);
			MOVE thisMove;
			if (gameCopy.getGhostEdibleTime(g)>0){
				thisMove=gameCopy.getNextMoveAwayFromTarget(gIndex, pacManIndex, lastMove, DM.MANHATTAN);
			}else{
				thisMove=gameCopy.getNextMoveTowardsTarget(gIndex, pacManIndex, lastMove, DM.MANHATTAN);
			}
			map.put(g, thisMove);
		}
		return map;
	}
	/**
	 * Returns the next move which Pacman should take to get to the goal index.
	 * @param gameCopy
	 * @param pacManIndex
	 * @param goalIndex
	 * @return
	 */
	private MOVE getNextPacManMove(Game gameCopy, int pacManIndex, int goalIndex){
		MOVE thisMove = gameCopy.getNextMoveTowardsTarget(pacManIndex, goalIndex, DM.MANHATTAN);
		return thisMove;
	}
	
	
}
