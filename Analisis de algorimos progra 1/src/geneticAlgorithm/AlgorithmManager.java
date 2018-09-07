package geneticAlgorithm;

import java.awt.image.BufferedImage;

public class AlgorithmManager 
{
	//algoritmos de similitud
	private static final int FIT_Euclidean = 0;
	private static final int FIT_Algorithm1 = 1;
	private static final int FIT_Algorithm2 = 2;
	
	private int selectedAlgorithm = 0;
	
	private BufferedImage solution;
	
	public void setFitnessAlgorithm(int selected) {
		selectedAlgorithm = selected;
	}
	
	public void setSolution(BufferedImage img) {
		solution = img;
	}
	
	
}
