package pacman.entries.pacman.neuralPacMan.sensors;

import java.util.ArrayList;

import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class BooleanSensor extends Sensor {

	public BooleanSensor(OBJ objectToScanFor, DIR scanDirection,  int sensorDistance) {
		super(objectToScanFor, scanDirection);
		
	}

	@Override
	protected float getNormalizedSensorValue(int pacManIndex, int[] validIndexes, Game game) {
		switch (objectToScanFor) {
		case GHOST_EATABLE: return nearestGhostIsEatable(pacManIndex, game);
		case GHOSTS_IN_JAIL: return allGhostsAreInJail(game);
		case WALL: return isWall(pacManIndex, game);
		default: return 0;
		}
	}
	/*
	 * Testing if there is a wall in the direction of the sensor.
	 * Using the fact that getNeighbouringNodes only returns nodes which is
	 * accessible (== not walls)
	 */
	private float isWall( int pacManIndex, Game game){
		int[] neighbours = game.getNeighbouringNodes(pacManIndex);
		int pX = game.getNodeXCood(pacManIndex);
		int pY = game.getNodeYCood(pacManIndex);
		for (int i: neighbours){
			int x = game.getNodeXCood(i);
			int y = game.getNodeYCood(i);
			switch (scanDirection){
			case N: if (pY - 1 == y){return 0;}; break;
			case S: if (pY + 1 == y){return 0;}; break;
			case E: if (pX + 1 == x){return 0;}; break;
			case W: if (pX - 1 == x){return 0;}; break;
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
