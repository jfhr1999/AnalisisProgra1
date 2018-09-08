package geneticAlgorithm;

import java.awt.image.BufferedImage;

public class AlgorithmManager 
{
	//algoritmos de similitud
	public static final int FIT_Euclidean = 0;
	public static final int FIT_Algorithm1 = 1;
	public static final int FIT_Algorithm2 = 2;
	
	private int selectedAlgorithm = 0;
	
	private BufferedImage solution;
	private Population population;
	
	public void setFitnessAlgorithm(int selected) {
		selectedAlgorithm = selected;
	}
	
	public void setSolution(BufferedImage img) {
		solution = img;
	}
	
	public void setStartingPopulation(int num) {
		population = new Population(num, solution.getWidth(), solution.getHeight());
	}
	
}
