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
	private enum STATE {HUNT_PILLS, HUNT_GHOSTS, SURVIVE};
	private final int MAX_TREE_DEPTH = 50;
	private final double EXPLORATION = 1.5d;
	private int currentGoalNode = -1;
	private STATE curState;
	private final long DEBUG_DELAY = 10000000;
	//private final long DEBUG_DELAY = 40;
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
		int pIndex = gameCopy.getPacmanCurrentNodeIndex();
		if (pIndex == currentGoalNode || currentGoalNode == -1 ){
			Node goal = getNextGoalNode(gameCopy);
			currentGoalNode = goal.nodeIndex();
			myMove = gameCopy.getNextMoveTowardsTarget(pIndex, currentGoalNode, DM.PATH);
		} else {
			myMove = gameCopy.getNextMoveTowardsTarget(pIndex, currentGoalNode, DM.PATH);
		}
		GameView.addPoints(game, Color.GREEN, currentGoalNode);
		return myMove;
	}
	/**
	 * Using mcts to get the next goal node
	 * @param gameCopy
	 * @return
	 */
	private Node getNextGoalNode(Game gameCopy){
		int pacManIndex = gameCopy.getPacmanCurrentNodeIndex();
		Node startNode = new Node(pacManIndex, null, 0);
		buildSearchTree(startNode, gameCopy);
		startNode.setGameState(gameCopy.copy());
		long curTime;
				
		do{
			Node leaf = selectNextNode(startNode);
			//Simulate game and calculate score
			int simScore = simulation(leaf);
			
			//Backpropagate
			Node n = leaf;
			do{
				n.incrementTimesVisited();
				double oldValue = n.qValue();
				n.setQvalue(oldValue + (double) simScore);
				n = n.parent();
			} while(n!=null);
			curTime = System.currentTimeMillis();
			System.out.println();
		} while (timeDue - curTime > 10);
		
		Node bestNode = getNextNode(startNode);
		
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
	 * @param startNode
	 * @return
	 */
	private int simulation(Node startNode){
		
		Game gameCopy = startNode.getGameState().copy();
		int pointsBeforeSimulation = gameCopy.getScore();
		boolean stopSimulation = false;
		int i = 0;
		do {
			int pIndex = gameCopy.getPacmanCurrentNodeIndex();
			MOVE pMove = nextMoveToPill(pIndex, gameCopy);
			EnumMap<GHOST,MOVE> gMoves = getGhostMoves(gameCopy, pIndex);
			gameCopy.advanceGame(pMove, gMoves);
			i++;
			if (i == 20 || gameCopy.wasPacManEaten()){
				stopSimulation = true;
			}
		} while (!stopSimulation);
		
		int pointsAfterSimulation = gameCopy.getScore();
		if (gameCopy.wasPacManEaten()){
			return 0;
		} else {
			return pointsAfterSimulation - pointsBeforeSimulation;
		}
	}
	
	private MOVE nextMoveToPill(int pacManIndex, Game gameCopy){
		int[] targetNodes = gameCopy.getActivePillsIndices();
		int closestNode = gameCopy.getClosestNodeIndexFromNodeIndex(pacManIndex, targetNodes, DM.PATH);
		return gameCopy.getNextMoveTowardsTarget(pacManIndex, closestNode, DM.PATH);
	}
	private void buildSearchTree(Node startNode, Game game){
		startNode.buildTree(0, MAX_TREE_DEPTH, game);
		startNode.removeIncest();
	}
	
	private Node selectNextNode(Node startNode){
		Game gameCopy;
		boolean pacManWasEaten, powerPillWasEaten, noChildren;
		Node n = startNode;
		Node endNode = startNode;
		do {
			if (!n.fullyExpanded()){
				endNode =expandNode(n);
				if (goToNextState(startNode, endNode)){;
					return endNode;
				} else {
					return startNode;
				}
			} else {
				n = getBestChild(n, EXPLORATION);
				if (!goToNextState(startNode, n)){
					n = startNode;
				}
			}
			gameCopy = n.getGameState();
			pacManWasEaten=gameCopy.wasPacManEaten(); 
			powerPillWasEaten = gameCopy.wasPowerPillEaten();
			noChildren = n.children().isEmpty();
		} while (!pacManWasEaten && !powerPillWasEaten && !noChildren );
		return n;
	}
	/**
	 * Returns the next child node that has not been expanded
	 * @param parent
	 * @return
	 */
	private Node expandNode(Node parent){
		for (Node n: parent.children()){
			if (n.timesVisited()==0){
				return n;
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
	 * The ghost are by default moving towards pacman if the are non-eatable
	 * @param startNode
	 * @param goalNode
	 * @return true if pacman arrived succesfully at the goal node's index
	 */
	private boolean goToNextState(Node startNode, Node goalNode){
		Game gameCopy = startNode.getGameState();
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
				thisMove=gameCopy.getNextMoveAwayFromTarget(gIndex, pacManIndex, lastMove, DM.PATH);
			}else{
				thisMove=gameCopy.getNextMoveTowardsTarget(gIndex, pacManIndex, lastMove, DM.PATH);
			}
			map.put(g, thisMove);
		}
		return map;
	}
	/**
	 * Returns the next move which Pacman should take to get to the goal index.
	 * Takes the last move of pacman into account.
	 * @param gameCopy
	 * @param pacManIndex
	 * @param goalIndex
	 * @return
	 */
	private MOVE getNextPacManMove(Game gameCopy, int pacManIndex, int goalIndex){
		MOVE lastMove = gameCopy.getPacmanLastMoveMade();
		return gameCopy.getNextMoveTowardsTarget(pacManIndex, goalIndex, lastMove, DM.PATH);
	}
	
	
}
