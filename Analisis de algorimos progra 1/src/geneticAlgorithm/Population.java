package geneticAlgorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Population 
{
	protected ArrayList<Individual> individuals = null;
	protected int fitnessAvg;
	
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
			
			case(AlgorithmManager.FIT_Manhattan):
				fitnessScore = ManhattanFitness(indv, metaImg);
				break;
			
			case(AlgorithmManager.FIT_Custom):
				fitnessScore = CustomFitness(indv,metaImg);
				break;
			}
			indv.setFitnessScore(fitnessScore);
			fitnessSum += fitnessScore;
		}
		fitnessAvg = (int) (fitnessSum / (float) individuals.size());
	}
	
	private int EuclideanFitness(Individual indv, BufferedImage metaImg) 
	{
		int width = metaImg.getWidth(),
			height = metaImg.getHeight(),
			indvPixelVal, metaPixelVal,
			fitness;
		int sum = 0;
		Color indvPixel, metaPixel;
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
		sum = (int) Math.sqrt(sum);
		int maxDiff = (int) Math.sqrt(Math.pow(255, 2)*(width*height));
		fitness = (int) (100 - (sum*100/maxDiff));
		//System.out.println("100 - ( "+sum+"*100 / "+maxDiff+" ) = "+ fitness);
		return fitness;
	}
	
	private int ManhattanFitness(Individual indv, BufferedImage metaImg)
	{
		int width = metaImg.getWidth(),
			height = metaImg.getHeight(),
			indvPixelVal, metaPixelVal,
			fitness,
			sum = 0;
		Color indvPixel, metaPixel;
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				indvPixel = indv.getPixelAt(x, y);
				indvPixelVal = indvPixel.getBlue();
				metaPixel = new Color(metaImg.getRGB(x, y));
				metaPixelVal = metaPixel.getBlue();
				sum += Math.abs(metaPixelVal - indvPixelVal);
			}
		}
		fitness = 100 - (sum / (width*height));
		return fitness;
	}
	
	private int CustomFitness(Individual indv, BufferedImage metaImage)
	{
		int width = metaImage.getWidth(),
			height = metaImage.getHeight(),
			MicroDet = 0, sum = 0,
			fitness, possibleMatrx = 0,
			a,b,c,d;
		Color aIndv, bIndv, cIndv, dIndv,
			  aMeta, bMeta, cMeta, dMeta;
		for (int y = 0; y < (height-1); ++y)
		{
			for (int x = 0; x < (width-1); ++x)
			{
				aIndv = indv.getPixelAt(x, y);		aMeta = new Color(metaImage.getRGB(x, y));
				bIndv = indv.getPixelAt(x+1, y);	bMeta = new Color(metaImage.getRGB(x+1, y));
				cIndv = indv.getPixelAt(x, y+1);	cMeta = new Color(metaImage.getRGB(x, y + 1));
				dIndv = indv.getPixelAt(x+1, y+1);	dMeta = new Color(metaImage.getRGB(x+1, y+1));
				
				a = aMeta.getBlue() - aIndv.getBlue();
				b = bMeta.getBlue() - bIndv.getBlue();
				c = cMeta.getBlue() - cIndv.getBlue();
				d = dMeta.getBlue() - dIndv.getBlue();
				
				MicroDet = (a*c) - (b*d);
				
				sum += Math.abs(MicroDet);
				possibleMatrx++;
			}
		}
		float maxMatrx = 65025*possibleMatrx; //255^2
		
		fitness = (int) ((sum/maxMatrx)*100.0);
		//System.out.println(sum + " / "+maxMatrx+" *100 = " +fitness+" - 100 = "+ (100 - fitness));
		return 100 - fitness;
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
