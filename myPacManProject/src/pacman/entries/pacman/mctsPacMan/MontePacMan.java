package pacman.entries.pacman.mctsPacMan;

import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class MontePacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	
	public MOVE getMove(Game game, long timeDue) 
	{
		return myMove;
	}
}
