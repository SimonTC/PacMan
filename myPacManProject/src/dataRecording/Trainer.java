package dataRecording;


public class Trainer {

	public void train(){
		//Do something
	}
	
	/**
	 * Load sensor data from a file and converts the sensor data such that the
	 * number of unique values is lesser. Used to make the correctness check easier.
	 */
	private DataTuple[] loadData(String fileName){
		DataSaverLoader dsl = new DataSaverLoader(fileName);
		DataTuple[] data = dsl.LoadPacManData();
		
		for (DataTuple dt : data){
			dt.distanceToNearestGhost = roundDown(dt.distanceToNearestGhost);
			dt.distanceToNearestPowerPill = roundDown(dt.distanceToNearestPowerPill);
			dt.nearestGhostEdible = roundDown(dt.nearestGhostEdible);
		}
		return data;
	}
	/**
	 *  
	 * @param d
	 * @return returns the lower bound of the value bracket in which d is
	 * 
	 */
	private double roundDown (double d){
		for (double i = 0.00d ; i<= 1.0d; i += 0.005d){
			if (isBetween(d, i, i+0.005d)){
				return i;
			}
		}
		return 1.00d;
		
	}
	
	/**
	 * 
	 * @param d
	 * @param lBound
	 * @param uBound
	 * @return true if d is between lBound (inclusive) and uBound (exclusive)
	 */
	private boolean isBetween(double d, double lBound, double uBound){
		if (d >= lBound && d < uBound){
			return true;
		}
		return false;
	}
}
