package pacman.entries.pacman.neuralPacMan.sensors;

import pacman.game.Game;

public class QuantitySensor extends Sensor {
	public QuantitySensor(OBJ objectToScanFor, DIR scanDirection,  int sensorDistance) {
		super(objectToScanFor, scanDirection, sensorDistance);
	}

	@Override
	protected double getNormalizedSensorValue(int pacManIndex, int[] validIndexes, Game game) {
		int numOfItemsInArea = validIndexes.length;
		int numOfItemsInLevel = 0;
		
		switch (objectToScanFor){
		case GHOST_SAFE: numOfItemsInLevel = 4; break;
		case GHOST_UNSAFE: numOfItemsInLevel = 4; break;
		case PILL: numOfItemsInLevel = game.getActivePillsIndices().length; break;
		case POWERPILL: numOfItemsInLevel = game.getActivePowerPillsIndices().length; break;
		}
		
		return normalizeNumberOfItems(numOfItemsInArea, numOfItemsInLevel);
	}
	
	public double normalizeNumberOfItems(int numOfItemsInArea, int numOfItemsInLevel)
	{
		return ((numOfItemsInArea-0)/(numOfItemsInLevel-0))*(1-0)+0;
	}

}
