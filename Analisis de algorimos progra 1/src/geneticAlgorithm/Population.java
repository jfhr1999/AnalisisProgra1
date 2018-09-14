package geneticAlgorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Population 
{
	protected ArrayList<Individual> individuals = null;

	protected void setStartingPopulation(int startingPop, int width, int height) {
		individuals = new ArrayList<Individual>();
		for (int i = 0; i < startingPop; i++)
			individuals.add(new Individual(width, height));
	}
	
	protected BufferedImage getImgAt(int index)
	{
		Individual indv = individuals.get(index);
		return indv.solution;
	}
	
	protected void AssignFitnessScores(int selectedFunction) {
		int fitnessScore = 0;
		BufferedImage metaImg = AlgorithmManager.metaImg;
		for (Individual indv : individuals)
		{
			switch(selectedFunction)
			{
			case(AlgorithmManager.FIT_Euclidean):
				fitnessScore = EuclideanFitness(indv, metaImg);
				break;
			
			case(AlgorithmManager.FIT_Algorithm1):
				fitnessScore = Fitness1(indv);
				break;
			
			case(AlgorithmManager.FIT_Algorithm2):
				fitnessScore = Fitness2(indv);
				break;
			}
			indv.setFitnessScore(fitnessScore);
		}
	}
	
	private int EuclideanFitness(Individual indv, BufferedImage metaImg) 
	{
		return 0;
	}
	
	private int Fitness1(Individual indv)
	{
		return 0;
	}
	
	private int Fitness2(Individual indv)
	{
		return 0;
	}
	
	protected int getHighestIndex()
	{
		int index = 0,
			maxFitness = 0;
		Individual indv;
		for (int i = 0; i < individuals.size(); ++i) {
			indv = individuals.get(i);
			if (maxFitness < indv.getFitnessScore()) {
				maxFitness = indv.getFitnessScore();
				index = i;
			}
		}
		return index;
	}
}
