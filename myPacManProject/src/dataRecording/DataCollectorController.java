package dataRecording;

import pacman.controllers.*;
import pacman.entries.pacman.neuralPacMan.NeuralPacMan;
import pacman.entries.pacman.searchPacMan.controller.SearchPacMan;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * The DataCollectorHumanController class is used to collect training data from playing PacMan.
 * Data about game state and what MOVE chosen is saved every time getMove is called.
 * @author andershh
 *
 */
/*
public class DataCollectorController extends HumanController{
	
	public DataCollectorController(KeyBoardInput input){
		super(input);
	}
	
	@Override
	public MOVE getMove(Game game, long dueTime) {
		MOVE move = super.getMove(game, dueTime);
		
		DataTuple data = new DataTuple(game, move);
				
		DataSaverLoader.SavePacManData(data);		
		return move;
	}

}
*/
public class DataCollectorController extends SearchPacMan{
	String teacherFile;
	String studentFile;
	DataSaverLoader teacherDSL;
	DataSaverLoader studentDSL;
	
	public DataCollectorController(String studentFile, String teacherFile){
		super();
		this.teacherFile = teacherFile;
		this.studentFile = studentFile;
		this.teacherDSL = new DataSaverLoader(teacherFile);
		this.studentDSL = new DataSaverLoader(studentFile);
	}
	
	@Override
	public MOVE getMove(Game game, long dueTime) {
		MOVE move = super.getMove(game, dueTime);
		
		DataTuple data = new DataTuple(game, move, this);
				
		studentDSL.SavePacManData(data);
		
		return move;
	}

}
