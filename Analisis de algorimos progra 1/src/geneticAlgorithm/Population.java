package geneticAlgorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Population 
{
	protected ArrayList<Individual> individuals = null;
	protected float fitnessAvg;
	
	//MUTATION PARAMETERS
	protected int maxGenePerc;
	protected int individualPerc;
	
	protected void setPopulationSize(int startingPop, int width, int height) {
		individuals = new ArrayList<Individual>();
		for (int i = 0; i < startingPop; i++) {
			individuals.add(new Individual(width, height));
			individuals.get(i).setFitnessScore(i);
		}
	}
	
	protected BufferedImage getImgAt(int index)
	{
		Individual indv = individuals.get(index);
		return indv.solution;
	}
	
	protected void AssignFitnessScores(BufferedImage metaImg, int selectedAlgorithm) {
		int fitnessScore = 0,
			fitnessSum = 0;
		for (Individual indv : individuals)
		{
			switch(selectedAlgorithm)
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
			fitnessSum += fitnessScore;
		}
		fitnessAvg = fitnessSum / (float) individuals.size();
	}
	
	private int EuclideanFitness(Individual indv, BufferedImage metaImg) 
	{
		int width = metaImg.getWidth(),
			height = metaImg.getHeight(),
			indvPixelVal, metaPixelVal,
			fitness;
		Color indvPixel, metaPixel;
		double sum = 0;
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				indvPixel = indv.getPixelAt(x, y);
				indvPixelVal = indvPixel.getBlue();
				metaPixel = new Color(metaImg.getRGB(x, y));
				metaPixelVal = metaPixel.getBlue();
				sum += (metaPixelVal - indvPixelVal)*(metaPixelVal - indvPixelVal);
			}
		}
		Math.sqrt(sum);
		fitness = (int) ((sum / (width*height)) / 1000);
		return fitness;
	}
	
	private int Fitness1(Individual indv)
	{
		return 0;
	}
	
	private int Fitness2(Individual indv)
	{
		return 0;
	}
	
	//bubble sort, ordena de mayor a menor
	protected void sortFitness()
	{
		boolean change = true;
		Individual tmp;
		for (int j = individuals.size(); j > 0 && change; --j)
		{
			change = false;
			for (int i = 0; i < j-1; ++i)
			{
				if (individuals.get(i).getFitnessScore() < individuals.get(i+1).getFitnessScore())
				{
					change = true;
					tmp = individuals.get(i);
					individuals.set(i, individuals.get(i+1));
					individuals.set(i+1, tmp);
				}
			}
		}
	}
	
	protected void crossover(int type)
	{
		int maxIndx = individuals.size()/2;
		Individual male, female;
		ArrayList<Individual> newGen = new ArrayList<Individual>();
		Random r = new Random();
		int j;
		for (int i = 0; i < maxIndx; i++)
		{
			male = individuals.get(i);
			j = r.nextInt(individuals.size());
			female = individuals.get(j);
			if (female.getFitnessScore() > fitnessAvg) {
				newGen.add(male.crossover(female, type));
				newGen.add(female.crossover(male, type));
			}
			else
				--i;
		}
		individuals = newGen;
		//System.out.println("New Generation size: "+individuals.size());
	}
	
	protected int mutate()
	{
		int chance,
			mutated = 0;
		for (Individual indv : individuals)
		{
			chance = new Random().nextInt(100)+1;
			if (chance <= individualPerc) {
				if (chance > 5)
					indv.mutate(maxGenePerc);
				else
					indv.resetSolution();
				mutated++;
			}
		}
		return mutated;
	}
}
