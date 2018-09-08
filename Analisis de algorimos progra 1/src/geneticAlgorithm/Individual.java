package geneticAlgorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;

import generador.GeneradorImagenes;

public class Individual 
{
	private BufferedImage solution;
	
	public Individual(int width, int height) {
		GeneradorImagenes img = new GeneradorImagenes();
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
