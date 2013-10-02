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
		
		NeuralNetwork n = new NeuralNetwork(3, 2, 4);
		
		String inputs = "0.2 0.5 0;0.3 0.2 1";
		String outputs = "0.3 0.2 0.1 0.9;0.1 0.5 0.2 0.3";
		nt.train(n, inputs, outputs, 0.1, 100000);
	}
	
	/**
	 * givenInputs: 1 0;0 1;1 1
	 * expectedOutputs: 0;0;1
	 * @param givenInputs
	 * @param expectedOutputs
	 */
	public void train(NeuralNetwork networkToBeTrained, String givenInputs, String expectedOutputs, double maxDiff, int maxRuns){
		String[] expectedOutputTuples = expectedOutputs.split(";");
		String[] givenInputTuples = givenInputs.split(";");
		nn  = networkToBeTrained;
		
		int numberOfOutputNodes = nn.numberOfOutputNodes();
		double[][] errors = new double[expectedOutputTuples.length][numberOfOutputNodes];
		
		int counter = 0;
		
		do {
			counter++;
			for (int i = 0; i < givenInputTuples.length; i++){
				String[] givenInputValues = givenInputTuples[i].split(" ");
				String[] expectedOutputValues = expectedOutputTuples[i].split(" ");
									
				errors[i] = this.backPropagate(givenInputValues, expectedOutputValues, 1.5);
			}
			printRunInformation(counter, errors);
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
	
	public double[] backPropagate(String[] inputs, String[] desiredOutputs, double learningRate){
		//Read inputs
		ArrayList<Neuron> networkSensors = nn.getSensors();
		for (int i = 0; i < networkSensors.size(); i++){
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
		for (int i = 0; i < outputNodes.size(); i++){
			Neuron n = outputNodes.get(i);
			double actualOutput = n.outputValue();
			outputError[i] = actualOutput * (1 - actualOutput) * (desiredOutputValues[i] - actualOutput);
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
		
		return outputError;
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
	
	private void printRunInformation(int counter, double[][] errors ){
		System.out.println("Run " + counter + ": Errors " + printErrors(errors));
	}
	
	private String printErrors(double[][] errors){
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(',');
		DecimalFormat df = new DecimalFormat("#0.000", dfs);
		String s = "";
		for (int i = 0; i < errors.length; i++){
			s += "[";
			for (int j = 0; j< errors[i].length; j++){
				s += df.format(errors[i][j]) +" ";
			}
			s+="]";
		}
		return s;
	}

}
