package pacman.entries.pacman.neuralPacMan.sensors;

import pacman.game.Constants.DM;
import pacman.game.Game;

public class DistanceSensor extends Sensor {
	public DistanceSensor(OBJ objectToScanFor, DIR scanDirection, int sensorDistance) {
		super(objectToScanFor, scanDirection, sensorDistance);		
	}

	@Override
	protected float getNormalizedSensorValue(int pacManIndex, int[] validIndexes,Game game) {
		int closestIndex = game.getClosestNodeIndexFromNodeIndex(pacManIndex, validIndexes, DM.EUCLID);
		int dist;
		int numberOfNodesInLevel = game.getNumberOfNodes();
		if (closestIndex == -1){
			dist = numberOfNodesInLevel;
		} else {
			dist = game.getShortestPathDistance(pacManIndex, closestIndex);
		}		 
		return normalizeDistance(dist, numberOfNodesInLevel);	
	}
	
	/**
	 * Used to normalize distances. Done via min-max normalization.
	 * Supposes that minimum possible distance is 0. Supposes that
	 * the maximum possible distance is the total number of nodes in
	 * the current level.
	 * @param dist Distance to be normalized
	 * @return Normalized distance
	 */
	public float normalizeDistance(int dist, int numberOfNodesInLevel)
	{
		return (float)((dist-0)/(numberOfNodesInLevel-0))*(1-0)+0;
	}
}
