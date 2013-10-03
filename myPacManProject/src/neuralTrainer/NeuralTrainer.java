package neuralTrainer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import pacman.entries.pacman.newNeuralPacMan.NeuralNetwork;
import pacman.entries.pacman.newNeuralPacMan.neurons.Neuron;

public class NeuralTrainer {
	NeuralNetwork nn;
	
	
	public static void main(String[] args){
		NeuralTrainer nt = new NeuralTrainer();
		
		NeuralNetwork n = new NeuralNetwork(3, 10, 4);
		
		String inputs = "1 1 1;0 0 0";
		String outputs = "1 1 1 1;0 0 0 0";
		nt.train(n, inputs, outputs, 0.01, 100000, 0.1);
	}
	
	/**
	 * givenInputs: 1 0;0 1;1 1
	 * expectedOutputs: 0;0;1
	 * @param givenInputs
	 * @param expectedOutputs
	 */
	public void train(NeuralNetwork networkToBeTrained, String givenInputs, String expectedOutputs, double maxDiff, int maxRuns, double learningRate){
		String[] expectedOutputTuples = expectedOutputs.split(";");
		String[] givenInputTuples = givenInputs.split(";");
		nn  = networkToBeTrained;
		
		int numberOfOutputNodes = nn.numberOfOutputNodes();
		double[][] errors = new double[expectedOutputTuples.length][numberOfOutputNodes];
		double[][][] result = new double[expectedOutputTuples.length][2][numberOfOutputNodes];
		int counter = 0;
		
		do {
			counter++;
			for (int i = 0; i < givenInputTuples.length; i++){
				String[] givenInputValues = givenInputTuples[i].split(" ");
				String[] expectedOutputValues = expectedOutputTuples[i].split(" ");
									
				result[i] = this.backPropagate(givenInputValues, expectedOutputValues, learningRate);
				errors[i] = result[i][0];
			}
			printRunInformation(counter, result);
		} while (!errorIsTolerable(errors, maxDiff) && counter <= maxRuns);
		
		String weights = nn.getWeights();
		System.out.println(weights);
		
	}
	private boolean errorIsTolerable(double[][] errors, double maxDiff){
		for (int i = 0; i < errors.length; i++){
			for (int j = 0; j< errors[i].length; j++){
				if (errors[i][j]< maxDiff * -1 || errors[i][j]> maxDiff){
					return false;
				}
			}
		}
		return true;
	}
	
	public double[][] backPropagate(String[] inputs, String[] desiredOutputs, double learningRate){
		//Read inputs
		ArrayList<Neuron> networkSensors = nn.getSensors();
		for (int i = 0; i < networkSensors.size() - 1; i++){
			double newValue = Double.parseDouble(inputs[i]);
			networkSensors.get(i).setOutputValue(newValue);
		}
		
		//Convert desired output values to doubles
		double[] desiredOutputValues = new double [desiredOutputs.length];
		for (int i = 0; i < desiredOutputs.length; i++){
			desiredOutputValues[i] = Double.parseDouble(desiredOutputs[i]);
		}
		
		//Calculate output errors
		ArrayList<Neuron> outputNodes = nn.getOutputNodes();
		double[] outputError = new double [outputNodes.size()];
		double[] actualOutput = new double [outputNodes.size()];
		for (int i = 0; i < outputNodes.size(); i++){
			Neuron n = outputNodes.get(i);
			actualOutput[i] = n.outputValue();
			outputError[i] = actualOutput[i] * (1 - actualOutput[i]) * (desiredOutputValues[i] - actualOutput[i]);
			//outputError[i] = 0.5*Math.pow(desiredOutputValues[i] - actualOutput[i], 2);
			n.setError(outputError[i]);			
		}		
		
		//Calculate error in hidden layer
		ArrayList<Neuron> hiddenNodes = nn.getHiddenNodes();
		for(Neuron n : hiddenNodes){
			double outputValue = n.outputValue();
			double hiddenError = outputValue * (1-outputValue) * getWeightedErrors(n);
			n.setError(hiddenError);
		}
		
		//Change weights from input to hidden layer
		changeWeightsFromLowerLayerToHigherLayer(hiddenNodes, learningRate);
		
		//Change weights from hidden layer to output layer
		changeWeightsFromLowerLayerToHigherLayer(outputNodes, learningRate);
		double[][] result = new double[2][outputNodes.size()];
		result[0] = outputError;
		result[1] = actualOutput;
		return result;
	}
	
	private void changeWeightsFromLowerLayerToHigherLayer(ArrayList<Neuron> nodesInHigherLayer, double learningRate){
		for (Neuron n : nodesInHigherLayer){
			for (Neuron l : n.inputNeurons()){
				double output = l.outputValue();
				double error = n.error();
				double oldWeight = n.getWeight(l);
				double changeBy = (double) learningRate * output * error;
				double newWeight = oldWeight + changeBy;
				n.updateInputWeight(l, newWeight);
			}
		}		
	}
	
	private double getWeightedErrors(Neuron node){
		double weightedErrors = 0.0;
		for (Neuron p : node.outputNodes()){
			double error = p.error();
			double weight = p.getWeight(node);
			weightedErrors += (double) error * weight;
		}
		return weightedErrors;
	}
	
	private void printRunInformation(int counter, double[][][] values ){
		System.out.println("Run " + counter + ": Errors " + printValues(values[0]));
		System.out.println("Run " + counter + ": Outputs " + printValues(values[1]));
	}
	
	private String printValues(double[][] values){
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(',');
		DecimalFormat df = new DecimalFormat("#0.000", dfs);
		String s = "";
		for (int i = 0; i < values.length; i++){
			s += "[";
			for (int j = 0; j< values[i].length; j++){
				s += df.format(values[i][j]) +" ";
			}
			s+="]";
		}
		return s;
	}

}
