package geneticAlgorithm;

import java.awt.image.*;
import java.util.Random;

public class ImageGenerator {
	
	public BufferedImage createImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_GRAY);
	    
		byte [] imgData = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		byte [] randomData = new byte[imgData.length];
		//generates random byte data
		new Random().nextBytes(randomData);
		
		for (int i = 0; i < imgData.length; ++i)
			imgData[i] = randomData[i];
		
		/* == OLD CODE ==
		int R, G, B, grayAvg, p;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				R = (int)(Math.random()*256);
				G = (int)(Math.random()*256);
				B = (int)(Math.random()*256);
				
				grayAvg = (R+G+B)/3;
				R = G = B = grayAvg;
				
				p = (255 << 24) | (R << 16) | (G<< 8) | B;
				img.setRGB(x, y, p);
			}
		}
		*/
		return img;
	}
	
	protected BufferedImage crossover_horizontal(Individual male, Individual female)
	{
		BufferedImage newImg = new BufferedImage(male.solution.getWidth(), male.solution.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte [] imgData = ((DataBufferByte) newImg.getRaster().getDataBuffer()).getData(),
				maleData = ((DataBufferByte) male.solution.getRaster().getDataBuffer()).getData(),
				femaleData = ((DataBufferByte) female.solution.getRaster().getDataBuffer()).getData();
		
		for (int i = 0; i < imgData.length; ++i)
		{
			if (i < imgData.length/2)
				imgData[i] = maleData[i];
			else
				imgData[i] = femaleData[i];
		}
		return newImg;
	}
	
	protected BufferedImage crossover_vertical(Individual male, Individual female)
	{
		int width = male.solution.getWidth(),
			height = male.solution.getHeight();
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		byte [] imgData = ((DataBufferByte) newImg.getRaster().getDataBuffer()).getData(),
				maleData = ((DataBufferByte) male.solution.getRaster().getDataBuffer()).getData(),
				femaleData = ((DataBufferByte) female.solution.getRaster().getDataBuffer()).getData();
		
		int x;
		for (int i = 0; i < imgData.length; ++i)
		{
			x = i % width;
			if (x < width/2)
				imgData[i] = maleData[i];
			else
				imgData[i] = femaleData[i];
		}
		return newImg;
	}
	
	protected BufferedImage crossover_quarts(Individual male, Individual female)
	{
		int width = male.solution.getWidth(),
			height = male.solution.getHeight();
		BufferedImage newImg = new BufferedImage(male.solution.getWidth(), male.solution.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		byte [] imgData = ((DataBufferByte) newImg.getRaster().getDataBuffer()).getData(),
				maleData = ((DataBufferByte) male.solution.getRaster().getDataBuffer()).getData(),
				femaleData = ((DataBufferByte) female.solution.getRaster().getDataBuffer()).getData();
		
		int x, y;
		for (int i = 0; i < imgData.length; ++i)
		{
			x = i % width;
			y = i / width;
			if (x < width/2) {
				if (y < height/2)
					imgData[i] = maleData[i];
				else
					imgData[i] = femaleData[i];
			}
			else {
				if (y < height/2)
					imgData[i] = femaleData[i];
				else
					imgData[i] = maleData[i];
			}
		}
		return newImg;
	}
}
