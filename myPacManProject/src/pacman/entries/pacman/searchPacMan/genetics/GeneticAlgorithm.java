package pacman.entries.pacman.searchPacMan.genetics;

import geneticAlgorithm.GAExecutor;
import geneticAlgorithm.GeneComparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;        // for generating random numbers
import java.util.ArrayList;     // arrayLists are more versatile than arrays
import java.util.Scanner;

import pacman.Executor;

public class GeneticAlgorithm {
    // --- constants
    static int POPULATION_SIZE=50;
    static int CHROMOSOME_SIZE = 12;
    static int MAX_GENERATIONS = 200;
    static int NUMBER_OF_TRIALS = 10;
    // --- variables:

    /**
     * The population contains an ArrayList of genes (the choice of arrayList over
     * a simple array is due to extra functionalities of the arrayList, such as sorting)
     */
    ArrayList<Gene> mPopulation;

    // --- functions:

    /**
     * Creates the starting population of Gene classes, whose chromosome contents are random
     * @param size: The size of the popultion is passed as an argument from the main class
     */
    public GeneticAlgorithm(int size){
        // initialize the arraylist and each gene's initial weights HERE
        mPopulation = new ArrayList<Gene>();
        for(int i = 0; i < size; i++){
            Gene entry = new Gene();
            entry.randomizeChromosome();
            mPopulation.add(entry);
        }
    }
    
    public GeneticAlgorithm(String seedFileName){
       mPopulation = new ArrayList<Gene>();
       File file = new File(seedFileName + ".txt");
       Scanner input;
	try {
		input = new Scanner(file);
		do {
	    	   String values = input.nextLine();
	    	   String[] arrValues = values.split(" ");
	    	   Gene entry = new Gene();
	    	   for (int i = 0; i < arrValues.length; i++){
	    		   int value = Integer.parseInt(arrValues[i]);
	    		   entry.setChromosomeElement(i, value);
	    	   }
	    	   mPopulation.add(entry);
	       } while (input.hasNextLine());
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
    }
    /**
     * For all members of the population, runs a heuristic that evaluates their fitness
     * based on their phenotype. The evaluation of this problem's phenotype is fairly simple,
     * and can be done in a straightforward manner. In other cases, such as agent
     * behavior, the phenotype may need to be used in a full simulation before getting
     * evaluated (e.g based on its performance)
     */
    public void evaluateGeneration(){
        
    	for(int i = 0; i < mPopulation.size(); i++){
            Gene actGene = this.getGene(i);
            int[] parameters = actGene.mChromosome;
            writeParametersToFile("PacManParameters", parameters);
            String[] param = new String[1];
            param[0] = "" + NUMBER_OF_TRIALS;
            GAExecutor.main(param);
            actGene.setFitness(getFitnessFromFile("score"));
            actGene.setScoreInfo(getInfoFromFile("score"));
        }
    }
    
    private void writeParametersToFile (String filename, int[] parameters){
    	 try {
			PrintWriter out = new PrintWriter(filename +".txt");
			for (int i : parameters){
    	    	out.print(i + " ");
    	    }
    	    out.close();
    	} catch (IOException e) {
    	    //oh noes!
    	}
    }
    
    private String getInfoFromFile(String filename){
    	File file = new File(filename + ".txt");
		try {
			Scanner input = new Scanner(file);
			return input.nextLine();
			} catch (FileNotFoundException e) {
			//oh noes!
		}
		return null;
    }
    
    /**
     * Fitness is only calculated from scores that are less than 1 StdDev bigger than 
     * the average.
     * @param filename
     * @return The adapted average score
     */
    private double getFitnessFromFile (String filename){
    	String values = getInfoFromFile(filename);
    	String[] arrValues = values.split("\t");
    	
    	double totalScore = 0;
    	for (int i = 0; i < arrValues.length; i++){
    		totalScore += Double.parseDouble(arrValues[i]);
    	}
    	double avgScore = totalScore/ arrValues.length;
    	double stdDev = 0;
    	for (int i = 0; i < arrValues.length; i++){
    		stdDev += Math.pow(Double.parseDouble(arrValues[i]) - avgScore,2);
    	}
    	stdDev = Math.sqrt(((double)1/(arrValues.length-1))* stdDev);
    	
    	//Removing results more than 1 stdDev bigger than average
    	double newTotalScore = totalScore;
    	int newNumberOfTrials = arrValues.length;
    	for (int i = 0; i < arrValues.length; i++){
    		double score = Double.parseDouble(arrValues[i]);
    		if (score > avgScore + stdDev){
    			newTotalScore = newTotalScore - score;
    			newNumberOfTrials--;
    		}
    	}
    	//double result = avgScore / stdDev; //AvgScore / StdDev doesn't give a good indication
    	double result = (double) newTotalScore / newNumberOfTrials;
    	return result;
    }
    

    /**
     * With each gene's fitness as a guide, chooses which genes should mate and produce offspring.
     * The offspring are added to the population, replacing the previous generation's Genes either
     * partially or completely. The population size, however, should always remain the same.
     * If you want to use mutation, this function is where any mutation chances are rolled and mutation takes place.
     */
    public void produceNextGeneration(){
        /*Survival strategy:
    	1) Sort population by fitness
    	2) All genes except the two best and the worst will mutate with a 50 % chance
    	3) Two best genes produce offspring
    	4) The worst gene is killed
    	*/
    	GeneComparator gComp = new GeneComparator();
    	Collections.sort(mPopulation, gComp);
      	ArrayList<Gene> mNextGeneration = new ArrayList<Gene>();
    	//Mate 20 % best genes
    	int numParents = (int) (mPopulation.size() * 0.2);
    	for (int i = 1; i<numParents + 1; i = i +2){
    		Gene father = mPopulation.get(mPopulation.size()-1);
    		Gene mother = mPopulation.get(mPopulation.size()-2);
    		Gene[] children = mateGenes(father,mother);
    		for (Gene g : children){
    			mNextGeneration.add(g);
    		}
    		mNextGeneration.add(father);
    		mNextGeneration.add(mother);
    		mPopulation.remove(father);
    		mPopulation.remove(mother);
    	}
    	
    	//Remove 20 % worst
    	int numToDie = numParents;
    	for (int i = 0 ; i < numToDie; i++){
    		mPopulation.remove(i);
    	}
    	
    	//Mutate rest of the population
    	for (Gene g : mPopulation){
    		g.mutate();
    		mNextGeneration.add(g);
    	}
    	
    	mPopulation.clear();
    	for (Gene g : mNextGeneration){
    		mPopulation.add(g);
    	}
    	
    	
    }
    
    private Gene[] mateGenes(Gene father, Gene mother){
    	Gene[] children = father.reproduce(mother);
    	return children;
    }
    
    // accessors
    /**
     * @return the size of the population
     */
    public int size(){ return mPopulation.size(); }
    /**
     * Returns the Gene at position <b>index</b> of the mPopulation arrayList
     * @param index: the position in the population of the Gene we want to retrieve
     * @return the Gene at position <b>index</b> of the mPopulation arrayList
     */
    public Gene getGene(int index){ return mPopulation.get(index); }

    // Genetic Algorithm maxA testing method
    public static void main( String[] args ){
        // Initializing the population (we chose 500 genes for the population,
        // but you can play with the population size to try different approaches)
   
    	
    	GeneticAlgorithm population = new GeneticAlgorithm(POPULATION_SIZE);
        //GeneticAlgorithm population = new GeneticAlgorithm("Population Seed");
        
        int generationCount = 0;
        // For the sake of this sample, evolution goes on forever.
        // If you wish the evolution to halt (for instance, after a number of
        //   generations is reached or the maximum fitness has been achieved),
        //   this is the place to make any such checks
        boolean doContinue = true;
        while(doContinue){
            // --- evaluate current generation:
            population.evaluateGeneration();
            // --- print results here:
            // we choose to print the average fitness,
            // as well as the maximum and minimum fitness
            // as part of our progress monitoring
            double avgFitness=0.d;
            double minFitness=Double.POSITIVE_INFINITY;
            double maxFitness=Double.NEGATIVE_INFINITY;
            String bestIndividual="";
            String worstIndividual="";
            String[] wholeGeneration = new String[population.size()];
            for(int i = 0; i < population.size(); i++){
            	double currFitness = population.getGene(i).getFitness();
            	wholeGeneration[i] =  population.getGene(i).getPhenotype() + "\t Fitness: " + currFitness;
            	avgFitness += currFitness;
                if(currFitness < minFitness){
                    minFitness = currFitness;
                    worstIndividual = population.getGene(i).getPhenotype();
                }
                if(currFitness > maxFitness){
                    maxFitness = currFitness;
                    bestIndividual = population.getGene(i).getPhenotype();
                }
            }
            if(population.size()>0){ avgFitness = avgFitness/population.size(); }
            String output = "Generation: " + generationCount;
            output += "\t AvgFitness: " + (int) avgFitness;
            output += "\t MinFitness: " + (int) minFitness + " (" + worstIndividual +")";
            output += "\t MaxFitness: " + (int) maxFitness + " (" + bestIndividual +")";
            System.out.println(output);
            population.writeToGenerationLog(output);
            writePopulationToLog(wholeGeneration, generationCount);
            // produce next generation:
            population.produceNextGeneration();
            generationCount++;
            if (generationCount == MAX_GENERATIONS){
            	doContinue=false;
            }
        }
    }
    private void writeToGenerationLog(String s){
    	try {
    	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("GenerationLog.txt", true)));
    		out.println(s);
    	    out.close();
    	} catch (IOException e) {
    	    //oh noes!
    	}
    }
    
    private static void writePopulationToLog(String[] population, int generation){
    	try {
    	    PrintWriter out = new PrintWriter("Population gen " + generation +".txt");
    		for (int i = 0; i< population.length; i++){
    			String s = population[i];
    			out.println(s);
    		}    		
    	    out.close();
    	} catch (IOException e) {
    	    //oh noes!
    	}
    }
};