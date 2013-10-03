package pacman.entries.pacman.newNeuralPacMan.neurons.sensors;

import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.entries.pacman.newNeuralPacMan.neurons.Neuron;

public  abstract class Sensor extends Neuron {
	public enum OBJ {GHOST, GHOST_EADABLE, POWERPILL, PILL, BIAS};
	protected OBJ objectToScanFor;
	
	public Sensor(String name){
		super(name);		
	}
	
	public void setObjectToScanFor(OBJ objectToScanFor){
		this.objectToScanFor = objectToScanFor;
	}
	
	@Override
	public double outputValue(int pacManIndex, Game game){
		return scan( pacManIndex,  game);
	}
	
	protected double scan(int pacManIndex, Game game) {
		int[] objectIndexes;
		
		switch(objectToScanFor){
		case GHOST: objectIndexes = getGhostIndexes(game, false);break;
		case POWERPILL: objectIndexes = game.getPowerPillIndices();break;
		default: objectIndexes = new int[0]; 
		}
		
		return getNormalizedSensorValue(pacManIndex, objectIndexes, game);
	}
	
	private int[] getGhostIndexes(Game game, boolean safe){
		ArrayList<Integer> tmp = new ArrayList<>();

		if (safe){
			for (GHOST g : GHOST.values()){
				if (game.getGhostEdibleTime(g) > 5 && game.getGhostLairTime(g) == 0){
					tmp.add( game.getGhostCurrentNodeIndex(g));
				}
			}
		} else {
			for (GHOST g : GHOST.values()){
				if (game.getGhostEdibleTime(g) < 5 && game.getGhostLairTime(g) == 0){
					tmp.add( game.getGhostCurrentNodeIndex(g));
				}
			}
		}
		int[] arr = new int[tmp.size()];
		for (int i = 0; i < tmp.size(); i++){
			arr[i] = tmp.get(i);
		}
		return arr;
	}
	
	/*
	 * Scans it's sensor area and returns a double based on the items in the area
	 */
	protected abstract double getNormalizedSensorValue(int pacManIndex, int[]validIndexes, Game game);
	
}
