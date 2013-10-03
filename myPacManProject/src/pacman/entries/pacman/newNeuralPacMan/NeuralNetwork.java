package pacman.entries.pacman.newNeuralPacMan;

import java.util.ArrayList;
import java.util.Random;

import pacman.entries.pacman.newNeuralPacMan.neurons.sensors.BooleanSensor;
import pacman.entries.pacman.newNeuralPacMan.neurons.sensors.Bias;
import pacman.entries.pacman.newNeuralPacMan.neurons.sensors.DistanceSensor;
import pacman.entries.pacman.newNeuralPacMan.neurons.sensors.Sensor.OBJ;
import pacman.entries.pacman.newNeuralPacMan.neurons.Neuron;
import pacman.game.Game;

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
		}else if (name.startsWith("Sd")){
			Neuron s = new DistanceSensor(name);
			if (!sensors.contains(s)){
				sensors.add(s);
			} else {
				int i = sensors.indexOf(s);
				s = sensors.get(i);
			}			
			return s;
		}else if (name.startsWith("Sb")){
			Neuron s = new BooleanSensor(name);
			if (!sensors.contains(s)){
				sensors.add(s);
			} else {
				int i = sensors.indexOf(s);
				s = sensors.get(i);
			}
			return s;
		}else if (name.startsWith("B0")){
			Neuron b = new Bias(name);
			if (!sensors.contains(b)){
				sensors.add(b);
			} else {
				int i = sensors.indexOf(b);
				b = sensors.get(i);
			}
			return b;
		}else if (name.startsWith("B1")){
			Neuron b = new Bias(name);
			if (!sensors.contains(b)){
				sensors.add(b);
			} else {
				int i = sensors.indexOf(b);
				b = sensors.get(i);
			}
			return b;
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
	
	private void addSensors(int numberOfSensors){
		DistanceSensor s0 = new DistanceSensor("Sd0");
		s0.setObjectToScanFor(OBJ.GHOST);
		sensors.add(s0);
		
		DistanceSensor s1 = new DistanceSensor ("Sd1");
		s1.setObjectToScanFor(OBJ.POWERPILL);
		sensors.add(s1);
		
		BooleanSensor s2 = new BooleanSensor("Sb2");
		s2.setObjectToScanFor(OBJ.GHOST_EADABLE);
		sensors.add(s2);
		
		//Add Bias Node
		sensors.add(new Bias("B" + 0));
	}
	
	private void addHiddenNodes(int numberOfNodes){
		for (int i = 0; i < numberOfNodes; i++){
			hiddenNodes.add( new Neuron("HN" + i));
		}
		
		//Add Bias Node
		hiddenNodes.add(new Bias("B" + 1));
	}
	
	private void addOutputNodes(int numberOfNodes){
		for (int i = 0; i < numberOfNodes; i++){
			outputNodes.add( new Neuron("ON" + i));
		}
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
	
	public ArrayList<Neuron> getSensors(){
		return sensors;
	}
	
	public ArrayList<Neuron> getOutputNodes(){
		return outputNodes;
	}
	
	public ArrayList<Neuron> getHiddenNodes(){
		return hiddenNodes;
	}
	
	public double[] returnOutputValues(int pacManIndex, Game game ){
		double[] result = new double[outputNodes.size()];
		for (int i = 0; i < result.length; i++){
			result[i] = outputNodes.get(i).outputValue(pacManIndex, game);
		}
		return result;
	}
}
