package pacman.entries.pacman.newNeuralPacMan.neurons;

import pacman.game.Game;

public interface INeuron {
	
	public double outputValue(int pacManIndex, Game game);
	
	public String getName();
	
	public boolean equals(Object other);
}
