package pacman.entries.pacman.neuralPacMan.nodes.sensors;

import java.util.ArrayList;

import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class BooleanSensor extends Sensor {

	public BooleanSensor(OBJ objectToScanFor) {
		super(objectToScanFor);
		
	}

	@Override
	protected float getNormalizedSensorValue(int pacManIndex, int[] validIndexes, Game game) {
		switch (objectToScanFor) {
		case GHOST_EADABLE: return nearestGhostIsEatable(pacManIndex, game);
		default: return 0;
		}
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
