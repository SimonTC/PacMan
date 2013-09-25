package pacman.entries.pacman.neuralPacMan.nodes.sensors;

import java.util.ArrayList;

import pacman.entries.pacman.neuralPacMan.nodes.Node;
import pacman.game.Game;
import pacman.game.Constants.GHOST;

public abstract class Sensor implements Node {
	public enum OBJ {GHOST, GHOST_EADABLE, POWERPILL, PILL};
	protected OBJ objectToScanFor;
	
	public Sensor(OBJ objectToScanFor){
		this.objectToScanFor = objectToScanFor;
	}
	
	public float value(int pacManIndex, Game game){
		return scan( pacManIndex,  game);
	}
	
	protected float scan(int pacManIndex, Game game) {
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
	protected abstract float getNormalizedSensorValue(int pacManIndex, int[]validIndexes, Game game);
	
}
