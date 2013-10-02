package pacman.entries.pacman.newNeuralPacMan;

import pacman.controllers.Controller;
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
public class NeuPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	private NeuralNetwork nn;
	
	public NeuPacMan(){
		nn = new NeuralNetwork(3, 2, 4);
	}
	
	public MOVE getMove(Game game, long timeDue) {
		int pacManIndex = game.getPacmanCurrentNodeIndex();
		double maxValue = Double.NEGATIVE_INFINITY;
		int maxIndex = -1;
		
		//Reading output values
		double[] outputValues = nn.returnOutputValues(pacManIndex, game);
		for (int i = 0; i < outputValues.length; i++){
			double value = outputValues[i];
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

		return myMove;
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
}