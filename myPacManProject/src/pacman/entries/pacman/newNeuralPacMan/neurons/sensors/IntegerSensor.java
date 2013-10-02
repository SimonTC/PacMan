package pacman.entries.pacman.newNeuralPacMan.neurons.sensors;

import pacman.entries.pacman.newNeuralPacMan.neurons.Neuron;

public class IntegerSensor extends  Neuron{

	public IntegerSensor(String name) {
		super(name);
	}

	@Override
	public double outputValue() {
		return outputValue;
	}
	
	

}
