package pacman.entries.pacman.neuralPacMan;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;

public class Trainer {

	public void train(){
		//Do something
	}
	
	private void loadData(){
		DataSaverLoader dsl = new DataSaverLoader();
		DataTuple[] data = dsl.LoadPacManData();
		
		for (DataTuple dt : data){
			
		}
	}
	
	private double roundDown (double d){
		if (isBetween(d, 0.0d, 0.01d)) {
			return 0.0d;
		}
	}
	
	private boolean isBetween(double d, double lBound, double uBound){
		if (d >= lBound && d < uBound){
			return true;
		}
		return false;
	}
}
