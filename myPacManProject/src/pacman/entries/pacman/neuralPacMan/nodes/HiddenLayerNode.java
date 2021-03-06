package pacman.entries.pacman.neuralPacMan.nodes;

import java.util.Random;

import pacman.game.Game;

public class HiddenLayerNode implements Node {
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
	public float value(int pacManIndex, Game game) {
		float totalInputValue = 0.0f;
		for (int i = 0; i < inputNodes.length; i++){
			totalInputValue += inputNodes[i].value(pacManIndex, game) * inputWeights[i];
		}
		return (float) sigmoidValue(totalInputValue);
		
		
	}
	
	private float sigmoidValue (float value){
		float result = (float) (1/(1+Math.pow(Math.E, (double)-value)));
		return (float) result;
	}

}
