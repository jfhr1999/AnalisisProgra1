package geneticAlgorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;

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
		ImageGenerator img = new ImageGenerator();
		solution = img.createImage(width, height);
	}
	
	protected Color getPixelAt(int x, int y)
	{
		Color pixel = new Color(solution.getRGB(x, y));
		return pixel;
	}
	
	protected void mutatePixelAt(int x, int y)
	{
		int R = (int)(Math.random()*256);
		int G = (int)(Math.random()*256);
		int B = (int)(Math.random()*256);
		int a = (int)(Math.random()*256);

		int p = (R << 24) | (G<< 16) | (a << 8) | B;
		solution.setRGB(x, y, p);
	}
}
