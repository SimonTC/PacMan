package pacman.entries.pacman.neuralPacMan.sensors;

import java.util.ArrayList;

import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class BooleanSensor extends Sensor {

	public BooleanSensor(OBJ objectToScanFor, DIR scanDirection,  int sensorDistance) {
		super(objectToScanFor, scanDirection, sensorDistance);
		
	}

	@Override
	protected float getNormalizedSensorValue(int pacManIndex, int[] validIndexes, Game game) {
		switch (objectToScanFor) {
		case GHOST_EATABLE: return nearestGhostIsEatable(pacManIndex, game);
		case GHOSTS_IN_JAIL: return allGhostsAreInJail(game);
		case WALL: return isWall(pacManIndex, game, game.getNodeXCood(pacManIndex), game.getNodeYCood(pacManIndex));
		default: return 0;
		}
	}
	
	private float isWall( int pacManIndex, Game game, int xCoord, int yCoord){
		int[] neighbours = game.getNeighbouringNodes(pacManIndex);
		for (int i: neighbours){
			int x = game.getNodeXCood(i);
			int y = game.getNodeYCood(i);
			if (x == xCoord + north && y == yCoord + east){
				return 0;
			}
		}
		
		return 1;
	}
	
	private float allGhostsAreInJail(Game game ){
		for (GHOST g: GHOST.values()){
			if (game.getGhostLairTime(g) == 0){
				return 0;
			}
		}
		return 1;
	}
	
	private float nearestGhostIsEatable(int pacManIndex, Game game ){
		ArrayList<GHOST> nearestGhost = new ArrayList<>();
		double e = 0.5;
		double ghostDist = 0;
		double minDist = 100000;
		for (GHOST g: GHOST.values()){
			ghostDist = game.getEuclideanDistance(pacManIndex, game.getGhostCurrentNodeIndex(g));
			if (minDist - e < ghostDist && ghostDist < minDist + e){
				nearestGhost.add(g);
			} else if (ghostDist < minDist - e ){
				nearestGhost.clear();
				nearestGhost.add(g);
			}
		}
		for (GHOST g : nearestGhost){
			if (game.getGhostEdibleTime(g)<10){
				return 0;
			} else {
				return 1;
			}
		}
		return 0;
		
	}

}
