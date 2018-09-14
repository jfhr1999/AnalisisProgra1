package geneticAlgorithm;

import java.awt.image.BufferedImage;

public class ImageGenerator {
	
	public BufferedImage createImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
		int R, G, B, grayAvg;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				R = (int)(Math.random()*256);
				G = (int)(Math.random()*256);
				B = (int)(Math.random()*256);
				
				grayAvg = (R+G+B)/3;
				R = G = B = grayAvg;
				
				int p = (255 << 24) | (R << 16) | (G<< 8) | B;
				img.setRGB(x, y, p);
			}
		}
		return img;
		
	}
	

}
