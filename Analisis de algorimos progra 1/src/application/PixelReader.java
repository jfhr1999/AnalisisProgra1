package application;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JTextArea;


public class PixelReader 
{
	private JTextArea txtArea;
	
	public PixelReader(JTextArea txtArea)
	{
		this.txtArea = txtArea;
	}
	
	public void processPixels(BufferedImage img)
	{
		txtArea.setEditable(true);
		
		int width = img.getWidth(),
			height = img.getHeight();
		Color c;
		String output;
		for (int i = 0; i < height; ++i)
		{
			for (int j = 0; j < width; ++j)
			{
				c = new Color(img.getRGB(j, i));
				output = "- ["+j+","+i+"] >\tR: "+c.getRed()+"\tG: "+c.getGreen()+"\tB: "+c.getBlue()+"\ta: "+c.getAlpha()+"\n";
				txtArea.append(output);
			}
		}
		txtArea.setEditable(false);
	}
}
