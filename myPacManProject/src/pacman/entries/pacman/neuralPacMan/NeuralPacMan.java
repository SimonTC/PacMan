package pacman.entries.pacman.neuralPacMan;

import pacman.controllers.Controller;
import pacman.entries.pacman.neuralPacMan.sensors.DistanceSensor;
import pacman.entries.pacman.neuralPacMan.sensors.QuantitySensor;
import pacman.entries.pacman.neuralPacMan.sensors.BooleanSensor;
import pacman.entries.pacman.neuralPacMan.sensors.Sensor;
import pacman.entries.pacman.neuralPacMan.sensors.Sensor.DIR;
import pacman.entries.pacman.neuralPacMan.sensors.Sensor.OBJ;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class NeuralPacMan extends Controller<MOVE>{
	private final int NUMBER_OF_SENSORS = 30;
	private Sensor[] sensors = new Sensor[NUMBER_OF_SENSORS];
	private double[] sensorValues = new double[NUMBER_OF_SENSORS];
	
	private MOVE myMove=MOVE.NEUTRAL;
	
	public NeuralPacMan(int sensorDistance){
		addSensors(sensorDistance);		
	}
	
	private void addSensors(int sensorDistance){
		//Adding enemy distance sensors
		sensors[0] = new DistanceSensor(OBJ.GHOST, DIR.N, sensorDistance);
		sensors[1] = new DistanceSensor(OBJ.GHOST, DIR.S, sensorDistance);
		sensors[2] = new DistanceSensor(OBJ.GHOST, DIR.E, sensorDistance);
		sensors[3] = new DistanceSensor(OBJ.GHOST, DIR.W, sensorDistance);
		sensors[4] = new DistanceSensor(OBJ.GHOST, DIR.NE, sensorDistance);
		sensors[5] = new DistanceSensor(OBJ.GHOST, DIR.NW, sensorDistance);
		sensors[6] = new DistanceSensor(OBJ.GHOST, DIR.SE, sensorDistance);
		sensors[7] = new DistanceSensor(OBJ.GHOST, DIR.SW, sensorDistance);
		
		//Adding PowerPill distance sensors
		sensors[8] = new DistanceSensor(OBJ.POWERPILL, DIR.N, sensorDistance);
		sensors[9] = new DistanceSensor(OBJ.POWERPILL, DIR.S, sensorDistance);
		sensors[10] = new DistanceSensor(OBJ.POWERPILL, DIR.E, sensorDistance);
		sensors[11] = new DistanceSensor(OBJ.POWERPILL, DIR.W, sensorDistance);
		sensors[12] = new DistanceSensor(OBJ.POWERPILL, DIR.NE, sensorDistance);
		sensors[13] = new DistanceSensor(OBJ.POWERPILL, DIR.NW, sensorDistance);
		sensors[14] = new DistanceSensor(OBJ.POWERPILL, DIR.SE, sensorDistance);
		sensors[15] = new DistanceSensor(OBJ.POWERPILL, DIR.SW, sensorDistance);
		
		//Adding Pill quantity sensors
		sensors[16] = new QuantitySensor(OBJ.PILL, DIR.N, sensorDistance);
		sensors[17] = new QuantitySensor(OBJ.PILL, DIR.S, sensorDistance);
		sensors[18] = new QuantitySensor(OBJ.PILL, DIR.E, sensorDistance);
		sensors[19] = new QuantitySensor(OBJ.PILL, DIR.W, sensorDistance);
		sensors[20] = new QuantitySensor(OBJ.PILL, DIR.NE, sensorDistance);
		sensors[21] = new QuantitySensor(OBJ.PILL, DIR.NW, sensorDistance);
		sensors[22] = new QuantitySensor(OBJ.PILL, DIR.SE, sensorDistance);
		sensors[23] = new QuantitySensor(OBJ.PILL, DIR.SW, sensorDistance);
		
		//Adding wall sensors
		sensors[24] = new BooleanSensor(OBJ.WALL, DIR.N, 0);
		sensors[25] = new BooleanSensor(OBJ.WALL, DIR.S, 0);
		sensors[26] = new BooleanSensor(OBJ.WALL, DIR.E, 0);
		sensors[27] = new BooleanSensor(OBJ.WALL, DIR.W, 0);
		
		//Adding sensor to see if all ghosts are in jail
		sensors[28] = new BooleanSensor(OBJ.GHOSTS_IN_JAIL, null, 0);
		
		//Adding sensor to see if nearest ghost is eatable
		sensors[29] = new BooleanSensor(OBJ.GHOST_EATABLE, null, 0);
	}
	
	public MOVE getMove(Game game, long timeDue) {
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		for (int i = 0; i < NUMBER_OF_SENSORS; i++){
			sensorValues[i] = sensors[i].scan(pacManIndex, game);
		}
		return myMove;
	}
	
	public double[] getSensorValues(){
		return sensorValues;
	}
}