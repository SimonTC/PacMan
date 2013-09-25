package pacman.entries.pacman.neuralPacMan.nodes;

import pacman.game.Game;

public interface Node {
	public double value(int pacManIndex, Game game);
	public void setName (String name);
	public String getName();
}
