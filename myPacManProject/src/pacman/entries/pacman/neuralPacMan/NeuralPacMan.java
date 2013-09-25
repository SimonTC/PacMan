package pacman.entries.pacman.neuralPacMan;

import pacman.controllers.Controller;
import pacman.entries.pacman.neuralPacMan.nodes.HiddenLayerNode;
import pacman.entries.pacman.neuralPacMan.nodes.Node;
import pacman.entries.pacman.neuralPacMan.nodes.sensors.BooleanSensor;
import pacman.entries.pacman.neuralPacMan.nodes.sensors.DistanceSensor;
import pacman.entries.pacman.neuralPacMan.nodes.sensors.Sensor;
import pacman.entries.pacman.neuralPacMan.nodes.sensors.Sensor.OBJ;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class NeuralPacMan extends Controller<MOVE>{
	private final int NUMBER_OF_SENSORS = 3;
	private final int NUMBER_OF_HIDDEN_NODES = 4;
	private final int NUMBER_OF_OUTPUT_NODES = 4;
	private Sensor[] sensors = new Sensor[NUMBER_OF_SENSORS];
	private Node[] hiddenNodes = new Node[NUMBER_OF_HIDDEN_NODES];
	private Node[] outputNodes = new Node[NUMBER_OF_OUTPUT_NODES];
	
	private double[] sensorValues = new double[NUMBER_OF_SENSORS];
	
	private MOVE myMove=MOVE.NEUTRAL;
	
	public NeuralPacMan(int sensorDistance){
		addSensors(sensorDistance);	
		addHiddenNodes();
		addOutputNodes();
	}
	
	private void addHiddenNodes(){
		for (int i = 0; i < NUMBER_OF_HIDDEN_NODES; i++){
			Node n = new HiddenLayerNode(sensors);
			hiddenNodes[i] = n;			
		}
	}
	
	private void addOutputNodes(){
		for (int i = 0; i < NUMBER_OF_OUTPUT_NODES; i++){
			Node n = new HiddenLayerNode(hiddenNodes);
			outputNodes[i] = n;			
		}
	}
	
	
 	private void addSensors(int sensorDistance){
		sensors[0] = new DistanceSensor(OBJ.GHOST);
		sensors[1] = new BooleanSensor(OBJ.GHOST_EADABLE);
		sensors[2] = new DistanceSensor (OBJ.POWERPILL);		
	}
	
	public MOVE getMove(Game game, long timeDue) {		
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		float maxValue = Float.NEGATIVE_INFINITY;
		int maxIndex = -1;
		
		for (int i = 0; i < NUMBER_OF_OUTPUT_NODES; i++){
			float value = outputNodes[i].value(pacManIndex, game);
			if ( value > maxValue){
				maxValue = value;
				maxIndex = i;
			}
		}		
		
		int ghostIndex = nearestItemIndex(game, pacManIndex, OBJ.GHOST);
		int pill = nearestItemIndex(game, pacManIndex, OBJ.PILL);
		int powerPill = nearestItemIndex(game, pacManIndex, OBJ.POWERPILL);
		DM dm = DM.EUCLID;
		switch (maxIndex){
		case 0: myMove = game.getNextMoveTowardsTarget(pacManIndex, ghostIndex, dm); break;
		case 1: myMove = game.getNextMoveAwayFromTarget(pacManIndex, ghostIndex, dm); break;
		case 2: myMove = game.getNextMoveTowardsTarget(pacManIndex, powerPill, dm); break;
		case 3: myMove = game.getNextMoveTowardsTarget(pacManIndex, pill, dm); break;
		}
		printAllNodes(pacManIndex, game);
		System.out.println("Max index: " + maxIndex + " Move: " + myMove);
		return myMove;
	}
	
	private void printNodeValues(Node[] nodeList, int pacManIndex, Game game){
		for (int i = 0; i < nodeList.length; i++){
			System.out.println("Node " + i + ": " + nodeList[i].value(pacManIndex, game));
		}
		
	}
	
	private int nearestItemIndex(Game game,int pacManIndex, OBJ item){
		switch (item){
		case GHOST: return ghostIndex(game, pacManIndex);
		case PILL: return pillIndex(game, pacManIndex);
		case POWERPILL: return powerPillIndex(game, pacManIndex);
		default: return 0;
		}
	}
	
	private int ghostIndex(Game game, int pacManIndex){
		double minDist = Double.POSITIVE_INFINITY;
		GHOST minGhost = null;
		for (GHOST g : GHOST.values()){
			double dist = game.getDistance(pacManIndex, game.getGhostCurrentNodeIndex(g), DM.EUCLID);
			if (dist < minDist){
				minGhost = g;
				minDist = dist;
			}
		}
		return game.getGhostCurrentNodeIndex(minGhost);
	}
	
	private int pillIndex(Game game, int pacManIndex){
		double minDist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i : game.getActivePillsIndices()){
			double dist = game.getDistance(pacManIndex, i, DM.EUCLID);
			if (dist < minDist){
				minIndex = i;
				minDist = dist;
			}
		}
		return minIndex;
	}
	
	private int powerPillIndex(Game game, int pacManIndex){
		double minDist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i : game.getActivePowerPillsIndices()){
			double dist = game.getDistance(pacManIndex, i, DM.EUCLID);
			if (dist < minDist){
				minIndex = i;
				minDist = dist;
			}
		}
		return minIndex;
	}
	
	private void printAllNodes(int pacManIndex, Game game){
		System.out.println("Sensor values:");
		printNodeValues(sensors, pacManIndex, game);
		System.out.println();
		System.out.println("Hidden layer values:");
		printNodeValues(hiddenNodes, pacManIndex, game);
		System.out.println();
		System.out.println("Output values:");
		printNodeValues(outputNodes, pacManIndex, game);

	}
	public double[] getSensorValues(){
		return sensorValues;
	}
}