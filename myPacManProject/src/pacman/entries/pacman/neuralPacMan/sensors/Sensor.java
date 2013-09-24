package pacman.entries.pacman.neuralPacMan.sensors;

import java.util.ArrayList;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public abstract class Sensor {
	public enum DIR {N, S, E, W, NE, NW, SE, SW};
	public enum OBJ {GHOST_SAFE, GHOST_UNSAFE, PILL, POWERPILL, WALL, GHOSTS_IN_JAIL, GHOST_EATABLE};
	protected OBJ objectToScanFor;
	protected DIR scanDirection;
	
	public Sensor(OBJ objectToScanFor, DIR scanDirection){
		this.objectToScanFor = objectToScanFor;
		this.scanDirection = scanDirection;
		
	}
	
	protected DIR getSection(int pacmanIndex, int itemIndex, Game game){
		boolean N =false;
		boolean E = false;
		boolean straightX = false;
		boolean straightY = false;
		int pX, pY, iX, iY;
		
		pX = game.getNodeXCood(pacmanIndex);
		pY= game.getNodeYCood(pacmanIndex);
		iX = game.getNodeXCood(itemIndex);
		iY = game.getNodeYCood(itemIndex);
		
		if (pX > iX){
			E = false;
		}else if (pX < iX){
			E = true;
		} else {
			straightX = true;
		}
		
		if (pY > iY){
			N = true;
		} else if (pY < iY){
			N = false;
		} else{
			straightY = true;
		}
		
		if (N && E){
			return DIR.NE;
		}else if (N && !E) {
			return DIR.NW;
		} else if (!N && E){
			return DIR.SE;
		} else if (!N && !E){
			return DIR.SW;
		} else if (straightX && N){
			return DIR.N;
		}else if (straightX && !N){
			return DIR.S;
		}else if (straightY && E){
			return DIR.E;
		}else {
			// if (straightY && !E)
			return DIR.W;
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
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		
		for (int i : objectIndexes){
			if ( getSection(pacManIndex, i, game) == this.scanDirection ){
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
