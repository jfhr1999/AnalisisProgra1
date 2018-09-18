package geneticAlgorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Individual 
{
	protected BufferedImage solution;
	private int fitnessScore = 0;
	
	public int getFitnessScore() {
		return fitnessScore;
	}
	public void setFitnessScore(int fitnessScore) {
		this.fitnessScore = fitnessScore;
	}

	public Individual(int width, int height) {
		solution = new ImageHandler().createImage(width, height);
	}
	
	public Individual(BufferedImage newImg) {
		solution = newImg;
	}
	
	public void resetSolution() {
		int width = solution.getWidth(),
			height = solution.getHeight();
		solution = new ImageHandler().createImage(width, height);
	}
	
	protected Color getPixelAt(int x, int y)
	{
		Color pixel = new Color(solution.getRGB(x, y));
		return pixel;
	}
	
	
	protected Individual crossover(Individual mate, int crossoverType)
	{
		BufferedImage imgOffspring = null;
		ImageHandler imgHandler= new ImageHandler();
		switch(crossoverType)
		{
		case(AlgorithmManager.CROSS_Hor):
			imgOffspring = imgHandler.crossover_horizontal(this, mate);
			break;

		case(AlgorithmManager.CROSS_Vert):
			imgOffspring = imgHandler.crossover_vertical(this, mate);
			break;

		case(AlgorithmManager.CROSS_Quart):
			imgOffspring = imgHandler.crossover_quarts(this, mate);
			break;
		}
		Individual offspring = new Individual(imgOffspring);
		return offspring;
	}
	
	protected void mutate(int maxPercentage)
	{
		int geneTotal = solution.getWidth() * solution.getHeight();
		int geneMin = (int) ((geneTotal/100.0) * 5),
			geneMax = (int) ((geneTotal/100.0) * maxPercentage);
		
		int geneNum = new Random().nextInt(geneMax - geneMin) + geneMin;
		//System.out.println(geneMin + " ~ " + geneMax + " :: "+ geneNum);
		ImageHandler imgHandler = new ImageHandler();
		solution = imgHandler.mutateImg(geneNum, solution);
	}
}
