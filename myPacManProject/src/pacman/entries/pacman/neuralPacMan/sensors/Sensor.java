package pacman.entries.pacman.neuralPacMan.sensors;

import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public abstract class Sensor {
	public enum DIR {N, S, E, W, NE, NW, SE, SW};
	public enum OBJ {GHOST_SAFE, GHOST_UNSAFE, PILL, POWERPILL, WALL, GHOSTS_IN_JAIL, GHOST_EATABLE};
	protected OBJ objectToScanFor;
	protected int east;
	protected int north;
	
	public Sensor(OBJ objectToScanFor, DIR scanDirection, int sensorDistance){
		this.objectToScanFor = objectToScanFor;
		
		switch (scanDirection){
		case N: east = 0 ; north = 0 - sensorDistance; break;
		case S: east = 0 ; north = 0 + sensorDistance; break;
		case E: east = 0 + sensorDistance ; north = 0 ; break;
		case W: east = 0 - sensorDistance; north = 0 ; break;
		case NE: east = 0 + sensorDistance ; north = 0 + sensorDistance; break;
		case NW: east = 0 - sensorDistance ; north = 0 + sensorDistance; break;
		case SE: east = 0 + sensorDistance ; north = 0 - sensorDistance; break;
		case SW: east = 0 - sensorDistance ; north = 0 - sensorDistance; break;
		}
	}
	
	public float scan(int pacManIndex, Game game) {
		int[] objectIndexes;
		int[] validIndexes;
		
		switch(objectToScanFor){
		case GHOST_UNSAFE: objectIndexes = getGhostIndexes(game, false);break;
		case GHOST_SAFE: objectIndexes = getGhostIndexes(game, true);break;
		case PILL: objectIndexes = game.getActivePillsIndices();break;
		case POWERPILL: objectIndexes = game.getPowerPillIndices();break;
		default: objectIndexes = new int[0]; 
		}
		
		validIndexes = getValidIndexes(objectIndexes, game);
		
		if (validIndexes.length==0) {
			return Float.POSITIVE_INFINITY;
		}

		return getNormalizedSensorValue(pacManIndex, validIndexes, game);
	}
	
	protected int[] getValidIndexes(int[] objectIndexes,Game game){
		ArrayList<Integer> tmp = new ArrayList<>();

		int curXCoord = game.getNodeXCood(game.getPacmanCurrentNodeIndex());
		int curYCoord = game.getNodeYCood(game.getPacmanCurrentNodeIndex());
		for (int i : objectIndexes){
			int xCoord = game.getNodeXCood(i);
			int yCoord = game.getNodeYCood(i);
System.out.println(curXCoord + " " + curYCoord + " " + xCoord + " " + yCoord + " N: " + north + " E: " +  east );
			//(0,0) in the coordinate system is in the upper left corner.
			//To calculate the correct distances, curX has to be subtracted from xCoord when calculating x, 
			//but opposite when calculating y
			if ( isBetween(curXCoord - xCoord, 0 , north)  && isBetween(yCoord - curYCoord, 0, east)){
					tmp.add(i);
				}
			}
		
		int [] validIndexes = new int[tmp.size()];
		for (int j = 0; j < tmp.size(); j++){
			validIndexes[j] = tmp.get(j);
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
	protected abstract float getNormalizedSensorValue(int pacManIndex, int[]validIndexes, Game game);
	
}
