package pacman.entries.pacman.newNeuralPacMan.neurons;

import java.util.ArrayList;
import java.util.HashMap;

import pacman.game.Game;

public class Neuron implements INeuron {
	protected String name="";
	protected ArrayList<Neuron> outputNeurons = new ArrayList<>();
	protected double outputValue;
	protected double error;
	private double inputValue;
	private ArrayList<Neuron> inputNeurons = new ArrayList<>();
	private HashMap<Neuron, Double> inputWeights;
	
	public Neuron(String name){
		this.name=name;
		this.inputWeights = new HashMap<Neuron, Double>();
	}
	
	public String getName(){
		return this.name;
	}
	
	@Override
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Neuron))return false;
		Neuron otherNeuron = (Neuron) other;
	    if (this.name.equalsIgnoreCase(otherNeuron.getName())){
			return true;
		} else {
			return false;
		}
	}
	
	public void addOutputNeuron(Neuron n){
		this.outputNeurons.add(n);
	}
	
	public ArrayList<Neuron> outputNodes(){
		return this.outputNeurons;
	}
	
	public void setError(double error){
		this.error = error;
	}
	
	public double error(){
		return this.error;
	}
	
	public double outputValue(int pacManIndex, Game game){
		this.inputValue = calculateInputValue(pacManIndex, game);
		this.outputValue = calculateOutputValue(this.inputValue);
		return this.outputValue;
	}
	
	private double calculateInputValue(int pacManIndex, Game game){
		double value = 0.0d;
		for (INeuron n : inputNeurons){
			value += (double) n.outputValue(pacManIndex, game) * inputWeights.get(n);
		}
		return value;
	}
	
	private double calculateOutputValue(double inputValue){
		double value = sigmoid(inputValue);
		return value;
	}
	
	private double sigmoid(double totalInputValue){
		double result = 1 / (1+Math.pow(Math.E, totalInputValue * -1));
		return result;
	}
		
	public void addInputNeuron(Neuron neuron, double weight){
		this.inputWeights.put(neuron, weight);
		this.inputNeurons.add(neuron);
	}
	
	/**
	 * Changes the weight of the given neuron if it is part of the input neurons.
	 * @param neuron
	 * @param newWeight
	 * @return true if neuron is part of input neurons. Otherwise false
	 */
	public boolean changeWeight(Neuron neuron, double newWeight){
		if (this.inputWeights.containsKey(neuron)){
			this.inputWeights.put(neuron, newWeight);
			return true;
		} else {
			return false; 
		}
	}

	public double getWeight(Neuron neuron){
		return inputWeights.get(neuron);
	}
	
	public boolean updateInputWeight(Neuron inputNode, double newWeight){
		if (this.inputWeights.containsKey(inputNode)){
			this.inputWeights.put(inputNode, newWeight);
			return true;
		}else{
			return false;
		}
	}
	
	public ArrayList<Neuron>inputNeurons(){
		return this.inputNeurons;
	}
	
	public void setOutputValue(double newValue){
		this.outputValue = newValue;
	}
	
	public void setInputValue(double newValue){
		this.inputValue = newValue;
	}
	

}
