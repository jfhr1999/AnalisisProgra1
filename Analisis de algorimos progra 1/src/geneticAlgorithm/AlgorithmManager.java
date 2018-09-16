package geneticAlgorithm;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import application.AppWin;

public class AlgorithmManager implements Runnable
{
	
	//App UI
	private AppWin Application;
	
	//FITNESS ALGORITHMS
	public static final int FIT_Euclidean = 0;
	public static final int FIT_Algorithm1 = 1;
	public static final int FIT_Algorithm2 = 2;
	
	//CROSSOVER TYPES
	public static final int CROSS_Hor = 0;
	public static final int CROSS_Vert = 1;
	public static final int CROSS_Quart = 2;
	
	//END CONDITION
	private int generationTotal = 20;
	private int fitnessMin = -1;
	
	private int selectedAlgorithm = 0;
	private int selectedCrossover = 0;
	private BufferedImage metaImg;
	private Population population = new Population();
	
	public void setGenTotal(int pTotal) {
		generationTotal = pTotal;
	}
	public void setFitnessMin(int pMin) {
		fitnessMin = pMin;
	}
	
	public void setFitnessAlgorithm(int selected) {
		selectedAlgorithm = selected;
	}
	public void setCrossoverType(int selected) {
		selectedCrossover = selected;
	}
	
	public void setMetaImg(BufferedImage img) {
		metaImg = img;
	}
	
	public void setStartingPopulation(int num) {
		population.setStartingPopulation(num, metaImg.getWidth(), metaImg.getHeight());
	}

	public AlgorithmManager(AppWin pApp) {
		Application = pApp;
	}
	
	private String outputFolderName()
	{
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String output = ts.toString().replace('-', '_');
		output = output.replace(':', '-');
		return "src/output/" + output;
	}
	
	private boolean stopCondition(int arg)
	{
		boolean out = false;
		if ((generationTotal > 0 && arg >= generationTotal) ||
			(fitnessMin > 0 && arg >= fitnessMin))
			out = true;
		return out;
	}
	
	private void updateProgressBar(int arg)
	{
		float out;
		if (generationTotal > 0)
			out = (arg/ (float) generationTotal)*100;
		else
			out = (arg/ (float) fitnessMin)*100;
		Application.updateProgressBar((int) out);
	}
	
	@Override
	public void run() {
		int highIndx;
		BufferedImage outputImg;
		File outputFile;
		String outputFolder = outputFolderName(),
			   fileName;
		new File(outputFolder).mkdir();
		try {
			int i = 1;
			while (!stopCondition(i))
			{
				population.AssignFitnessScores(metaImg, selectedAlgorithm);
				highIndx = population.getHighestIndex();
				if (generationTotal > 0) {
					fileName = "Generation"+i+".png";
					i++;
				}
				else {
					i = population.individuals.get(highIndx).getFitnessScore();
					fileName = "Fitness"+i+"png";
				}
				outputImg = population.getImgAt(highIndx);
				outputFile = new File(outputFolder + fileName);
				ImageIO.write(outputImg, "png", outputFile);
				Application.setOutputImg(outputImg);
				updateProgressBar(i);
			}
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(Application, "Error al guardar la imagen", "ERROR - Algoritmo Genético", JOptionPane.ERROR_MESSAGE);
		}
		finally {
			Application.setUIEnabled(true);
		}
	}
	
	//CROSSOVER TEST
	public void crossoverTest() {
		BufferedImage flippedImg = createRotated(metaImg);
		
		Individual male = new Individual(metaImg),
				   female = new Individual(flippedImg);
		
		Individual offspring = male.crossover(female, selectedCrossover);
		String outputFolder = outputFolderName();
		System.out.println(outputFolder);
		new File(outputFolder).mkdir();
		try {
			ImageIO.write(offspring.solution, "png", new File(outputFolder + "/test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Application.setOutputImg(offspring.solution);
	}
	
    private BufferedImage createRotated(BufferedImage image)
    {
        AffineTransform at = AffineTransform.getRotateInstance(
            Math.PI, image.getWidth()/2, image.getHeight()/2.0);
        return createTransformed(image, at);
    }

    private BufferedImage createTransformed(
        BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(
            image.getWidth(), image.getHeight(),
            BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
