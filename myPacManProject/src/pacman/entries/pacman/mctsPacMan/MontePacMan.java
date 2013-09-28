package pacman.entries.pacman.mctsPacMan;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class MontePacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	
	private final int MAX_TREE_DEPTH = 50;

	public MOVE getMove(Game game, long timeDue) 
	{
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		Node startNode = new Node(pacManIndex, null, 0);
		
		startNode.buildTree(0, MAX_TREE_DEPTH, game);
		startNode.removeIncest();
		startNode.colorFamily(game);
		return myMove;
	}
	
	
}
