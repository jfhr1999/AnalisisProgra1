package geneticAlgorithm;

import java.util.ArrayList;

public class Population 
{
	private ArrayList<Individual> individuals;
	
	public Population(int startingPop, int width, int height) {
		individuals = new ArrayList<Individual>();
		for (int i = 0; i < startingPop; i++)
			individuals.add(new Individual(width, height));
	}
	
	public void FitnessScore(int selectedFunction) {
		switch(selectedFunction)
		{
		case(AlgorithmManager.FIT_Euclidean):
			break;
		
		case(AlgorithmManager.FIT_Algorithm1):
			break;
		
		case(AlgorithmManager.FIT_Algorithm2):
			break;
		}
	}
}
