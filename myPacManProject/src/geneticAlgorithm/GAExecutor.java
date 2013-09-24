package geneticAlgorithm;

import static pacman.game.Constants.DELAY;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.pacman.searchPacMan.controller.SearchPacMan;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class GAExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numTrials=20;
		if (args.length > 0){
			numTrials = Integer.parseInt(args[0]);
		}
		GAExecutor exec=new GAExecutor();
		exec.runExperiment(new SearchPacMan(),new StarterGhosts(),numTrials);

	}
	
	public void runExperiment(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,int trials)
    {
    	String score ="";
    	Random rnd=new Random(0);
		Game game;
		
		for(int i=0;i<trials;i++)
		{
			game=new Game(rnd.nextLong());
			
			while(!game.gameOver())
			{
		        game.advanceGame(pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
		        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
			}
			
			score += game.getScore() + "\t";
		}
		printScoreToFile(score);
    }
	
	private void printScoreToFile(String score){
    	try {
    	    //PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("score.txt", true)));
    		PrintWriter out = new PrintWriter("score.txt");
    		out.println(score);
    	    out.close();
    	} catch (IOException e) {
    	    //oh noes!
    	}
    	
    }
}
