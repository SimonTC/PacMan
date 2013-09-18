package pacman.entries.pacman.myFirstPacMan.genetics;

import java.util.Random;

import pacman.entries.pacman.myFirstPacMan.genetics.Gene;
import pacman.entries.pacman.myFirstPacMan.genetics.GeneticAlgorithm;


public class Gene {
    // --- variables:

    /**
     * Fitness evaluates to how "close" the current gene is to the
     * optimal solution
     */
    protected double mFitness;
    
    protected int mChromosome[];
    
    protected String mScoreInfo;

    // --- functions:
    /**
     * Allocates memory for the mChromosome array and initializes any other data, such as fitness
     * We chose to use a constant variable as the chromosome size, but it can also be
     * passed as a variable in the constructor
     */
    Gene() {
    	mChromosome = new int[GeneticAlgorithm.CHROMOSOME_SIZE];
        // initializing fitness
        mFitness = 0.d;
    }

    /**
     * Randomizes the numbers on the mChromosome array to values 0 or 1
     */
    public void randomizeChromosome(){
        Random rand = new Random();
        mChromosome[0] = rand.nextInt(79) + 20 ; //Max depth
        mChromosome[1] = rand.nextInt(5); //Points for pills
        mChromosome[2] = rand.nextInt(10); //Points for power pills
        mChromosome[3] = rand.nextInt(21) - 20; //Points for non edible ghosts
        mChromosome[4] = rand.nextInt(10); //Points for edible ghosts
        mChromosome[5] = rand.nextInt(2); //Distance metric
        mChromosome[6] = rand.nextInt(5); //Points for junctions
        mChromosome[7] = rand.nextInt(2); //If memory is used or not
    }

    /**
     * Creates a number of offspring by combining (using crossover) the current
     * Gene's chromosome with another Gene's chromosome.
     * Usually two parents will produce an equal amount of offpsring, although
     * in other reproduction strategies the number of offspring produced depends
     * on the fitness of the parents.
     * @param other: the other parent we want to create offpsring from
     * @return Array of Gene offspring (default length of array is 2).
     * These offspring will need to be added to the next generation.
     */
    public Gene[] reproduce(Gene other){
        Gene[] result = new Gene[2];
        Gene child1 = new Gene();
        Gene child2 = new Gene();
        
        int chromosomeMid = mChromosome.length / 2;
        
        for (int i = 0; i <= chromosomeMid; i++){
        	child1.mChromosome[i] = this.mChromosome[i];
        	child2.mChromosome[i]=other.mChromosome[i];
        }
        for (int i = chromosomeMid +1; i < mChromosome.length; i++){
        	child1.mChromosome[i] = other.mChromosome[i];
        	child2.mChromosome[i]= this.mChromosome[i];
        }
        result[0] = child1;
        result[1] = child2;
        return result;
        
    }

    /**
     * Mutates a gene using inversion, random mutation or other methods.
     * This function is called after the mutation chance is rolled.
     * Mutation can occur (depending on the designer's wishes) to a parent
     * before reproduction takes place, an offspring at the time it is created,
     * or (more often) on a gene which will not produce any offspring afterwards.
     */
    public void mutate(){
    	Random rand = new Random();
    	int selector = rand.nextInt(8);
    	int changeBy = 0;
    	switch(selector){
    	case 0: 
    		{
	    		//Max depth
		    	changeBy = rand.nextInt(5) - 10;
		    	mChromosome[0]= mChromosome[0] + changeBy;
		    	if (mChromosome[0] < 1){
		    		mChromosome[0] = 1;
		    	}
    		}
    	case 1: 
	    	{	//Pills
		    	changeBy = rand.nextInt(5) - 2; 
		    	mChromosome[1] = mChromosome[1] + changeBy;
	    	}
    	case 2:
    		{
		    	//Power pills
		    	changeBy = rand.nextInt(5) - 2; 
		    	mChromosome[2] = mChromosome[2] + changeBy;
    		}
    	case 3:
    		{
	    		//Non edible ghosts
	    		changeBy = rand.nextInt(5) - 2;
		        mChromosome[3] = mChromosome[3] + changeBy;
    		}
    	case 4:
    		{
		        //Edible ghosts
		    	changeBy = rand.nextInt(5) - 2;
		        mChromosome[4] = mChromosome[4] + changeBy;
    		}
    	case 5:
	    	{
	    		// Distance Metric
		        mChromosome[5] = rand.nextInt(2); //Distance metric
	    	}
    	case 6:
			{
		        //junction points
		    	changeBy = rand.nextInt(5) - 2;
		        mChromosome[6] = mChromosome[6] + changeBy;
			}
    	case 7:
	    	{
	    		// Use memory
		        mChromosome[7] = rand.nextInt(2); 
	    	}
    	}
    }
    /**
     * Sets the fitness, after it is evaluated in the GeneticAlgorithm class.
     * @param value: the fitness value to be set
     */
    public void setFitness(double value) { mFitness = value; }
    /**
     * @return the gene's fitness value
     */
    public double getFitness() { return mFitness; }
    /**
     * Returns the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @return the value of the element we want to access (0 or 1)
     */
    public int getChromosomeElement(int index){ return mChromosome[index]; }

    /**
     * Sets a <b>value</b> to the element at position <b>index</b> of the mChromosome array
     * @param index: the position on the array of the element we want to access
     * @param value: the value we want to set at position <b>index</b> of the mChromosome array (0 or 1)
     */
    public void setChromosomeElement(int index, int value){ mChromosome[index]=value; }
    /**
     * Returns the size of the chromosome (as provided in the Gene constructor)
     * @return the size of the mChromosome array
     */
    public int getChromosomeSize() { return mChromosome.length; }
    /**
     * Corresponds the chromosome encoding to the phenotype, which is a representation
     * that can be read, tested and evaluated by the main program.
     * @return a String with a length equal to the chromosome size, composed of A's
     * at the positions where the chromosome is 1 and a's at the posiitons
     * where the chromosme is 0
     */
    public String getPhenotype() {
        // create an empty string
        String result="";
        for(int i = 0; i < mChromosome.length; i++){
        	result = result + mChromosome[i] + " ";
        }
        return result;
    }
    
    public String getScoreInfo(){
    	return mScoreInfo;
    }
    
    public void setScoreInfo(String info){
    	mScoreInfo = info;
    }
}