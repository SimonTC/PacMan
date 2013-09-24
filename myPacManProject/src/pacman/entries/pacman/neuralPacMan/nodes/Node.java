package pacman.entries.pacman.neuralPacMan.nodes;

import pacman.game.Game;

public interface Node {
	public float value(int pacManIndex, Game game);
}
