package pacman.entries.pacman.neuralPacMan.sensors;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public abstract class Sensor {
	public enum DIR {N, S, E, W, NE, NW, SE, SW};
	public enum OBJ {GHOST, PILL, POWERPILL, WALL, GHOSTS_IN_JAIL, GHOST_EATABLE};
	protected OBJ objectToScanFor;
	protected int east;
	protected int north;
	
	public Sensor(OBJ objectToScanFor, DIR scanDirection, int sensorDistance){
		this.objectToScanFor = objectToScanFor;
		
		switch (scanDirection){
		case N: east = 0 ; north = 0 + sensorDistance; break;
		case S: east = 0 ; north = 0 - sensorDistance; break;
		case E: east = 0 + sensorDistance ; north = 0 ; break;
		case W: east = 0 - sensorDistance; north = 0 ; break;
		case NE: east = 0 + sensorDistance ; north = 0 + sensorDistance; break;
		case NW: east = 0 - sensorDistance ; north = 0 + sensorDistance; break;
		case SE: east = 0 + sensorDistance ; north = 0 - sensorDistance; break;
		case SW: east = 0 - sensorDistance ; north = 0 - sensorDistance; break;
		}
	}
	
	public double scan(int pacManIndex, Game game) {
		int[] objectIndexes;
		int[] validIndexes;
		
		switch(objectToScanFor){
		case GHOST: objectIndexes = getGhostIndexes(game);
		case PILL: objectIndexes = game.getActivePillsIndices();
		case POWERPILL: objectIndexes = game.getPowerPillIndices();
		default: objectIndexes = new int[0]; 
		}
		
		validIndexes = getValidIndexes(objectIndexes, game);

		return getNormalizedSensorValue(pacManIndex, validIndexes, game);
	}
	
	protected int[] getValidIndexes(int[] objectIndexes,Game game){
		int [] validIndexes = new int[objectIndexes.length];

		int curXCoord = game.getNodeXCood(game.getPacmanCurrentNodeIndex());
		int curYCoord = game.getNodeYCood(game.getPacmanCurrentNodeIndex());
		int j = 0;
		for (int i : objectIndexes){
			int xCoord = game.getNodeXCood(i);
			int yCoord = game.getNodeYCood(i);
			if ( isBetween(xCoord - curXCoord, 0 , north)  && isBetween(yCoord - curYCoord, 0, east)){
				validIndexes[j] = i;
				j++;
			}
		}
		
		return validIndexes;
	}
	
	/**
	 * 
	 * @param number the number to test
	 * @param bound1 first bound inclusive
	 * @param bound2 second bound inclusive
	 * @return
	 */
	private boolean isBetween(int number, int bound1, int bound2){
		return (number >= bound1 && number <= bound2) ||(number >= bound2 && number <= bound1) ;
	}
	
	/*
	 * Scans it's sensor area and returns a double based on the items in the area
	 */
	
	
	private int[] getGhostIndexes(Game game){
		int[] arr = new int[4];
		int i = 0;
		for (GHOST g : GHOST.values()){
			arr[i] = game.getGhostCurrentNodeIndex(g);
			i++;
		}
		return arr;
	}
	
	protected abstract double getNormalizedSensorValue(int pacManIndex, int[]validIndexes, Game game);
	
}
