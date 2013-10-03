package pacman.entries.pacman.newNeuralPacMan.neurons.sensors;

import pacman.game.Game;

public class Bias extends Sensor {

	public Bias(String name) {
		super(name);
	}
	
	@Override
	public double outputValue() {
		return 1;
	}

	@Override
	protected float getNormalizedSensorValue(int pacManIndex,
		int[] validIndexes, Game game) {
		return 1;
	}
	
	@Override
	protected double scan(int pacManIndex, Game game){
		return 1;
	}

}
