package pacman.entries.pacman.neuralPacMan.nodes;

import java.util.Random;

import pacman.game.Game;

public class HiddenLayerNode extends MasterNode {
	Node[] inputNodes;
	float[] inputWeights;
	
	public HiddenLayerNode(Node[] inputNodes){
		this.inputNodes = inputNodes;
		inputWeights = new float[inputNodes.length];
		randomizeWeights();
	}
	
	private void randomizeWeights(){
		Random rand = new Random();
		for (int i = 0; i < inputWeights.length; i++){
			//Gives a weight between 0 and 1
			inputWeights[i] = rand.nextFloat();			
		}
	}
	
	@Override
	public double value(int pacManIndex, Game game) {
		float totalInputValue = 0.0f;
		for (int i = 0; i < inputNodes.length; i++){
			totalInputValue += inputNodes[i].value(pacManIndex, game) * inputWeights[i];
		}
		return (float) sigmoidValue(totalInputValue);
		
		
	}
	
	private double sigmoidValue (float value){
		double result = (double) (1/(1+Math.pow(Math.E, (double)-value)));
		return (double) result;
	}
	
	public String getWeights(){
		String result = "";
		for (int i = 0; i < inputNodes.length; i++){
			result += this.getName() + "," + inputNodes[i].getName() + "," + inputWeights[i] + ";";
		}
		return result;
			
	}

}
