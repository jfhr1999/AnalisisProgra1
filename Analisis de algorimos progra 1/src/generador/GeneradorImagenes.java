package generador;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GeneradorImagenes {
	
	public BufferedImage createImage(int width, int height) {
		File f = null;
		BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int R = (int)(Math.random()*256);
				int G = (int)(Math.random()*256);
				int B = (int)(Math.random()*256);
				int a = (int)(Math.random()*256);

				int p = (R << 24) | (G<< 16) | (a << 8) | B;
				img.setRGB(x, y, p);
			}
		}
		f = new File("Output.png");
		try {
			ImageIO.write(img,"png",f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
		
	}
	

}
