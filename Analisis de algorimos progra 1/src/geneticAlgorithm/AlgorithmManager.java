package geneticAlgorithm;

import java.awt.image.BufferedImage;
import application.AppWin;

public class AlgorithmManager implements Runnable
{
	
	//App UI
	private AppWin Application;
	
	//algoritmos de similitud
	public static final int FIT_Euclidean = 0;
	public static final int FIT_Algorithm1 = 1;
	public static final int FIT_Algorithm2 = 2;
	
	private int selectedAlgorithm = 0;
	
	static BufferedImage metaImg;
	private Population population;
	private int generationTotal;
	
	public void setFitnessAlgorithm(int selected) {
		selectedAlgorithm = selected;
	}
	
	public static void setMetaImg(BufferedImage img) {
		metaImg = img;
	}
	
	public void setStartingPopulation(int num) {
		population.setStartingPopulation(num, metaImg.getWidth(), metaImg.getHeight());
	}

	public AlgorithmManager(AppWin pApp) {
		Application = pApp;
	}
	
	@Override
	public void run() {
		int highIndx;
		for (int i = 0; i < generationTotal; ++i)
		{
			population.AssignFitnessScores(selectedAlgorithm);
			highIndx = population.getHighestIndex();
			Application.setOutputImg(population.getImgAt(highIndx));
		}
		Application.setUIEnabled(true);
	}
}
