package pacman.entries.pacman.mctsPacMan;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class MontePacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
		
	private final int MAX_TREE_DEPTH = 50;
	private final int MAX_TIME_UNITS = 10;
	private final double EXPLORATION = 1.5d;

	public MOVE getMove(Game game, long timeDue) 
	{
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		Node startNode = new Node(pacManIndex, null, 0);
		buildSearchTree(startNode, game);
		startNode.colorFamily(game);

		return myMove;
	}
	
	private MOVE mctsSearch(Game gameCopy){
		int pacManIndex = gameCopy.getPacmanCurrentNodeIndex();
		Node startNode = new Node(pacManIndex, null, 0);
		buildSearchTree(startNode, gameCopy);
		startNode.setGameState(gameCopy);
		
		for (int i = 0; i <= MAX_TIME_UNITS; i++){
			Node leaf = selectNextNode(startNode);
			//Simulate game
			
			//Calculate score
			
			//Backpropagate
		}

	}
	
	private void simulation(Node startNode){
		//Simulate moves
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
				goToNextState(endNode);
				return endNode;
			} else {
				n = getBestChild(n, EXPLORATION);
				goToNextState(n);
			}
			gameCopy = n.getGameState();
			pacManWasEaten=gameCopy.wasPacManEaten();
			powerPillWasEaten = gameCopy.wasPowerPillEaten();
			noChildren = n.children().isEmpty();
		} while (!pacManWasEaten && !powerPillWasEaten && !noChildren );
		return n;
	}
	
	private Node expandNode(Node parent){
		for (Node n: parent.children()){
			if (n.timesVisited()==0){
				return n;
			}
		}
		return null; //Should not reach here because method is only called when there are unexplored children
	}
	
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
	
	private double calculateUCT(Node parent, Node child, double explorationConstant){
		double leftPart = (double) (child.qValue()/child.timesVisited());
		double rightPart = (double) explorationConstant * Math.sqrt((2*Math.log(parent.timesVisited())/child.timesVisited()));
		double result = (double) leftPart + rightPart;
		return result;
	}
	
	private double getScore(Node n){
		return n.qValue();
	}
	
	private void goToNextState(Node goalNode){
		Game gameCopy = goalNode.getGameState();
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
		} while (pIndex!=goalIndex && !pacManWasEaten && !powerPillWasEaten);

	}
	/**
	 * Returns the moves for the ghosts.
	 * dangerous ghosts will move towards pacman.
	 * non-dangerous ghosts will move away from pacman.
	 * @param gameCopy
	 * @return
	 */
	private EnumMap<GHOST,MOVE> getGhostMoves(Game gameCopy, int pacManIndex){
		EnumMap<GHOST,MOVE> map=null;
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
	
	private MOVE getNextPacManMove(Game gameCopy, int pacManIndex, int goalIndex){
		MOVE lastMove = gameCopy.getPacmanLastMoveMade();
		return gameCopy.getNextMoveTowardsTarget(pacManIndex, goalIndex, lastMove, DM.PATH);
	}
}
