package geneticAlgorithm;

import java.util.Comparator;

import pacman.entries.pacman.myFirstPacMan.genetics.Gene;


public class GeneComparator implements Comparator<Gene> {

	@Override
	public int compare(Gene gene1, Gene gene2) {
		if (gene1.getFitness() > gene2.getFitness()) return 1;
		if (gene1.getFitness() < gene2.getFitness()) return -1;		
		return 0;
	}

}

