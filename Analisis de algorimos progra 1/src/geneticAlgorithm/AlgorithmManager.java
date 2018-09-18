package geneticAlgorithm;

import java.awt.image.BufferedImage;
//import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

//import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import application.AppWin;

public class AlgorithmManager implements Runnable
{
	
	//App UI
	private AppWin Application;
	
	//FITNESS ALGORITHMS
	public static final int FIT_Euclidean = 0;
	public static final int FIT_Manhattan = 1;
	public static final int FIT_Custom = 2;
	
	//CROSSOVER TYPES
	public static final int CROSS_Hor = 0;
	public static final int CROSS_Vert = 1;
	public static final int CROSS_Quart = 2;
	
	//END CONDITION
	private int generationTotal = -1;
	private int fitnessMin = -1;
	
	private int selectedAlgorithm = 0;
	private int selectedCrossover = 0;

	private BufferedImage metaImg;
	private Population population;
	private boolean manualStop = false;
	
	public void manualStop() {
		manualStop = true;
	}
	public void setGenTotal(int pTotal) {
		generationTotal = pTotal;
	}
	public void setFitnessMin(int pMin) {
		fitnessMin = pMin;
	}
	
	public void setGenePercentage(int pPerc) {
		population.maxGenePerc = pPerc;
	}
	public void setIndvPercentage(int pPerc) {
		population.individualPerc = pPerc;
	}
	
	public String getFitnessAlgorithm() 
	{
		String algorithm = "";
		switch(selectedAlgorithm)
		{
		case(FIT_Euclidean):
			algorithm = "Similitud Euclideana";
			break;
		case(FIT_Manhattan):
			algorithm = "Algoritmo Manhattan";
			break;
		case(FIT_Custom):
			algorithm = "Algoritmo Personalizado";
			break;
		}
		return algorithm;
	}
	public void setFitnessAlgorithm(int selected) {
		selectedAlgorithm = selected;
	}
	
	public String getCrossoverType()
	{
		String crossover = "";
		switch(selectedCrossover)
		{
		case(CROSS_Hor):
			crossover = "Mitad horizontal";
			break;
		case(CROSS_Vert):
			crossover = "Mitad vertical";
			break;
		case(CROSS_Quart):
			crossover = "Cuartos de imagen";
			break;
		}
		return crossover;
	}
	
	public void setCrossoverType(int selected) {
		selectedCrossover = selected;
	}
	
	public void setMetaImg(BufferedImage img) {
		metaImg = img;
	}
	
	public void setPopulationSize(int num) {
		population.setPopulationSize(num, metaImg.getWidth(), metaImg.getHeight());
	}

	public AlgorithmManager(AppWin pApp) {
		Application = pApp;
		population = new Population();
	}
	
	private String outputFolderName()
	{
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String output = ts.toString().replace('-', '_');
		output = output.replace(':', '-');
		return  "/src/output/" + output;
	}
	
	private boolean stopCondition(int genNum, int fitHigh)
	{
		boolean out = false;
		if ((generationTotal > 0 && genNum >= generationTotal) ||
			(fitnessMin > 0 && fitHigh >= fitnessMin))
			out = true;
		return out;
	}
	
	private void updateProgressBar(int genNum, int fitHigh)
	{
		float out;
		if (generationTotal > 0)
			out = (genNum/ (float) generationTotal)*100;
		else
			out = (fitHigh/ (float) fitnessMin)*100;
		Application.updateProgressBar((int) out);
	}
	
	@Override
	public void run() {
		BufferedImage outputImg;
		String outputFolder = outputFolderName();
		//new File(outputFolder).mkdir();
		try {
			int genNum = 0,
				fitHigh = 0;
			float genPerc;
			while (!stopCondition(genNum, fitHigh) && !manualStop)
			{
				population.AssignFitnessScores(metaImg, selectedAlgorithm);
				population.sortFitness();
				fitHigh = population.individuals.get(0).getFitnessScore();
				outputImg = population.getImgAt(0);
				Application.setOutputImg(outputImg);
				Application.setGeneration(genNum);

				updateProgressBar(genNum, fitHigh);
				genPerc = (genNum/ (float) generationTotal)*100;
				if ((generationTotal > 0 && (genPerc%10 ==  0) ) ||
					(fitnessMin > 0 && (genNum%500 == 0)))
					writeFile(outputFolder, outputImg, genNum, fitHigh);
				genNum++;
				
				population.crossover(selectedCrossover);
				population.mutate();
			}
			if (manualStop)
				manualStop = false;
			outputImg = population.getImgAt(0);
			Application.writeMsg("\nFINAL - - -");
			writeFile(outputFolder, outputImg, genNum, fitHigh);
			Application.setOutputImg(outputImg);
			Application.setGeneration(genNum);
		}
		catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Application, "Error al guardar la imagen", "ERROR - Algoritmo Genético", JOptionPane.ERROR_MESSAGE);
		}
		finally {
			Application.setUIEnabled(true);
		}
	}
	
	/*	Por Generacion: se imprime file cada que se pase un 10% del progreso (10 imagenes en total)
	 * 	Por Aptitud: se imprime file cada 500 generaciones
	 */
	private void writeFile(String outputFolder, BufferedImage outputImg, int genNum, int fitHigh) throws IOException 
	{
		String fileName = "/Gen"+genNum+" - Fitness "+fitHigh+".png";
		//File outputFile = new File(outputFolder + fileName);
		//ImageIO.write(outputImg, "png", outputFile);

		String msgOutput = "\n> Gen. "+String.valueOf(genNum) +
						   "\n     Aptitud Máx: "+String.valueOf(fitHigh) +
						   "\n     Aptitud Media: "+String.valueOf(population.fitnessAvg)+
						   "\n     Directorio de archivo: "+outputFolder+fileName;
		Application.writeMsg(msgOutput);
	}
	
}
