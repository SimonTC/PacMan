package pacman.entries.pacman.newNeuralPacMan;

import java.util.ArrayList;
import java.util.Random;

import pacman.entries.pacman.newNeuralPacMan.neurons.Neuron;
import pacman.entries.pacman.newNeuralPacMan.neurons.sensors.IntegerSensor;

public class NeuralNetwork {
	
	ArrayList<Neuron> sensors = new ArrayList<>();
	ArrayList<Neuron> hiddenNodes = new ArrayList<>();
	ArrayList<Neuron> outputNodes = new ArrayList<>();
		
	public NeuralNetwork(int numberOfSensors, int numberOfHiddenNodes, int numberOfOutputNodes){
		buildInitialNetwork(numberOfSensors, numberOfHiddenNodes, numberOfOutputNodes);
	}
	
	public NeuralNetwork(String networkTopology){
		buildInitialNetwork(networkTopology);
	}
	
	private void buildInitialNetwork(int numberOfSensors, int numberOfHiddenNodes, int numberOfOutputNodes){
		addSensors(numberOfSensors);
		addHiddenNodes(numberOfHiddenNodes);
		addOutputNodes(numberOfOutputNodes);
		//connectNeuronsManually();
		connectNeurons(hiddenNodes, sensors);
		connectNeurons(outputNodes, hiddenNodes);
	}
	
	private void buildInitialNetwork(String networkTopology){
		String[] pairs = networkTopology.split(";");
		for (int i = 0; i < pairs.length; i++){
			String[] parts = pairs[i].split(",");
			String parent = parts[0];
			String child = parts[1];
			Double weight = Double.parseDouble(parts[2]);
			Neuron parentNode = createNeuron(parent);
			Neuron childNode = createNeuron(child);			
			parentNode.addInputNeuron(childNode, weight);
			childNode.addOutputNeuron(parentNode);			
		}
	}
	
	private Neuron createNeuron(String name){
		Neuron n = null;
		
		if (name.startsWith("ON")){
			n = new Neuron(name);
			if (!outputNodes.contains(n)){
				outputNodes.add(n);
			} else {
				int i = outputNodes.indexOf(n);
				n = outputNodes.get(i);
			}
		} else if (name.startsWith("HN")){
			n = new Neuron(name);
			if (!hiddenNodes.contains(n)){
				hiddenNodes.add(n);
			} else {
				int i = hiddenNodes.indexOf(n);
				n = hiddenNodes.get(i);
			}
		}else if (name.startsWith("S")){
			Neuron s = new IntegerSensor(name);
			if (!sensors.contains(s)){
				sensors.add(s);
			} else {
				int i = sensors.indexOf(s);
				s = sensors.get(i);
			}
			return s;
		}
		return n;
		
	}
	
	private void connectNeurons(ArrayList<Neuron> parents, ArrayList<Neuron>  children){
		Random rand = new Random();
		for (Neuron p : parents){
			for (Neuron c : children){
				double weight = rand.nextDouble();
				p.addInputNeuron(c, weight);
				c.addOutputNeuron(p);
			}
		}
	}
	
	private void connectNeuronsManually(){
		hiddenNodes.get(0).addInputNeuron(sensors.get(0), 0.5d);
		hiddenNodes.get(0).addInputNeuron(sensors.get(1), 0.3d);
		outputNodes.get(0).addInputNeuron(hiddenNodes.get(0), 0.6d);
		sensors.get(0).addOutputNeuron(hiddenNodes.get(0));
		sensors.get(1).addOutputNeuron(hiddenNodes.get(0));
		hiddenNodes.get(0).addOutputNeuron(outputNodes.get(0));
	}
	
	private void addSensors(int numberOfSensors){
		for (int i = 0; i < numberOfSensors; i++){
			sensors.add( new IntegerSensor("S" + i));
		}
	}
	
	private void addHiddenNodes(int numberOfNodes){
		for (int i = 0; i < numberOfNodes; i++){
			hiddenNodes.add( new Neuron("HN" + i));
		}
	}
	
	private void addOutputNodes(int numberOfNodes){
		for (int i = 0; i < numberOfNodes; i++){
			outputNodes.add( new Neuron("ON" + i));
		}
	}

	public double[] backPropagate(String[] inputs, String[] desiredOutputs, double learningRate){
		//Read inputs
		for (int i = 0; i < sensors.size(); i++){
			double newValue = Double.parseDouble(inputs[i]);
			sensors.get(i).setOutputValue(newValue);
		}
		
		//Convert desired output values to doubles
		double[] desiredOutputValues = new double [desiredOutputs.length];
		for (int i = 0; i < desiredOutputs.length; i++){
			desiredOutputValues[i] = Double.parseDouble(desiredOutputs[i]);
		}
		
		//Calculate output errors
		double[] outputError = new double [outputNodes.size()];
		for (int i = 0; i < outputNodes.size(); i++){
			Neuron n = outputNodes.get(i);
			double actualOutput = n.outputValue();
			outputError[i] = actualOutput * (1 - actualOutput) * (desiredOutputValues[i] - actualOutput);
			n.setError(outputError[i]);			
		}		
		
		//Calculate error in hidden layer
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
	
	public String getWeights(){
		String weights ="";
		for (Neuron n : outputNodes){
			for (Neuron c : n.inputNeurons()){
				weights += n.getName() + "," + c.getName() + "," + n.getWeight(c) + ";";
			}
		}
		for (Neuron n : hiddenNodes){
			for (Neuron c : n.inputNeurons()){
				weights += n.getName() + "," + c.getName() + "," + n.getWeight(c) + ";";
			}
		}
		return weights;
	}
	
	public int numberOfOutputNodes(){
		return outputNodes.size();
	}
	
	public int numberOfInputNodes(){
		return sensors.size();
	}
}
