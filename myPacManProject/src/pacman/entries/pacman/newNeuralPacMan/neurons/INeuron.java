package pacman.entries.pacman.newNeuralPacMan.neurons;

import pacman.game.Game;

public interface INeuron {
	
	/**
	 * Used when playing the game
	 * @param pacManIndex
	 * @param game
	 * @return
	 */
	public double outputValue(int pacManIndex, Game game);
	
	/**
	 * Used when sensor inputs are given during training
	 * @return
	 */
	public double outputValue();
	
	public String getName();
	
	public boolean equals(Object other);
}
