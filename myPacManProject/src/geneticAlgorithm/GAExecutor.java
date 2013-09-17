package geneticAlgorithm;

import static pacman.game.Constants.DELAY;

import java.util.EnumMap;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.RandomGhosts;
import pacman.entries.pacman.myFirstPacMan.ANewAndBetterPacMan;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class GAExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GAExecutor exec=new GAExecutor();
		int numTrials=20;
		for (int maxDepth = 20; maxDepth <= 120; maxDepth +=20){
			exec.runExperiment(new ANewAndBetterPacMan(maxDepth),new RandomGhosts(),numTrials);
			System.out.println();
		}
	}
	
	public void runExperiment(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,int trials)
    {
    	double avgScore=0;
    	
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
			
			avgScore+=game.getScore();
			System.out.print(game.getScore() + "\t");
			//System.out.println(i+"\t"+game.getScore());
		}
		
		//System.out.println(avgScore/trials);
    }

}
