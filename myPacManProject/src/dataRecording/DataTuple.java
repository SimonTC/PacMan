package dataRecording;

import pacman.entries.pacman.neuralPacMan.NeuralPacMan;
import pacman.entries.pacman.searchPacMan.controller.SearchPacMan;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DataTuple implements Comparable<DataTuple> {
	
	public MOVE DirectionChosen;	
	
	public double distanceToNearestGhost;
	public double nearestGhostEdible;
	public double distanceToNearestPowerPill;
	
	public DataTuple(Game game, MOVE move, NeuralPacMan pacMan)
	{
		if(move == MOVE.NEUTRAL)
		{
			move = game.getPacmanLastMoveMade();
		}
		
		this.DirectionChosen = move;
		
		double[] sensorValues = pacMan.getSensorValues();
		this.distanceToNearestGhost = sensorValues[0];		
		this.distanceToNearestPowerPill  = sensorValues[1];
		this.nearestGhostEdible  = sensorValues[2];
		
	}
	public DataTuple(Game game, MOVE move, SearchPacMan pacMan)
	{
		if(move == MOVE.NEUTRAL)
		{
			move = game.getPacmanLastMoveMade();
		}
		
		this.DirectionChosen = move;
		
		double[] sensorValues = pacMan.getSensorValues();
		this.distanceToNearestGhost = sensorValues[0];
		this.nearestGhostEdible  = sensorValues[1];
		this.distanceToNearestPowerPill  = sensorValues[2];
		
	}
	public DataTuple(String data)
	{
		String[] dataSplit = data.split(";");
		
		this.DirectionChosen = MOVE.valueOf(dataSplit[0]);
		this.distanceToNearestGhost = Float.parseFloat(dataSplit[1]);
		this.nearestGhostEdible = Float.parseFloat(dataSplit[2]);
		this.distanceToNearestPowerPill = Float.parseFloat(dataSplit[3]);		
	}
	
	public String getSaveString()
	{
		StringBuilder stringbuilder = new StringBuilder();
		
		stringbuilder.append(this.DirectionChosen+";");
		stringbuilder.append(this.distanceToNearestGhost+";");
		stringbuilder.append(this.nearestGhostEdible+";");
		stringbuilder.append(this.distanceToNearestPowerPill+";");
		
		
		return stringbuilder.toString();
	}
	
	public String getSensorValues(){
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(this.distanceToNearestGhost+";");
		stringbuilder.append(this.nearestGhostEdible+";");
		stringbuilder.append(this.distanceToNearestPowerPill+";");
		return stringbuilder.toString();
	}

	@Override
	public int compareTo(DataTuple o) {
		String myValues = this.getSensorValues();
		String yourValues = o.getSensorValues();
		if (myValues.equalsIgnoreCase(yourValues)){
			return 1;
		} 
		return 0;
	}


	
}
