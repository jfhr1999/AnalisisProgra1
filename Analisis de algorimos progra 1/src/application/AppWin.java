package application;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import geneticAlgorithm.AlgorithmManager;

public class AppWin extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// Utilities
	private static final Font fnt_normal = new Font("DialogInput", Font.PLAIN, 14);
	private static final Font fnt_title = new Font("Consolas", Font.PLAIN, 16);
	private JFileChooser imgChooser;
	private AlgorithmManager algorithmManager = new AlgorithmManager(this);
	private Thread algorithmThread;
	private int actualWidth = 0;
	private int actualHeight = 0;
	
	// UI ~ ~ ~ ~ ~ ~
	private JPanel mainPane;
	private JButton btn_getImage;
	private JLabel lbl_sourceImg;
	private BufferedImage sourceImg;
	private BufferedImage grayscaleImg;
	private JCheckBox chkbx_grayscale;
	private JSlider sldr_startingPop;
	private JLabel lbl_imgSize;
	private JButton btn_start;

	// Output 
	private JLabel lbl_outputImg;
	private JTextArea txt_pixelInfo;
	private JProgressBar progressBar;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWin frame = new AppWin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	//Application Window Builder
	public AppWin() {
		setTitle("Proyecto 1 - Van Gogh Evolucional");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 805, 835);
		mainPane = new JPanel();
		mainPane.setBorder(null);
		setContentPane(mainPane);
		mainPane.setLayout(null);
		
		imgChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagenes (JPG, PNG, JPEG, BMP)", "jpg","png", "jpeg", "bmp");
		imgChooser.setFileFilter(filter);
		
		btn_getImage = new JButton("Seleccionar imagen");
		btn_getImage.setFont(fnt_normal);
		btn_getImage.setBounds(20, 20, 320, 25);
		btn_getImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (pickSourceImg())
						newSourceUpdateUI();
				}
				catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(rootPane, "Ha ocurrido un error al seleccionar una imagen."
														  + "El archivo seleccionado debe ser de formato JPG, PNG, JPEG o BMP.",
												  "ERROR - Seleccionar imagen fuente", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mainPane.add(btn_getImage);
		
		JPanel pnl_sourceImg = new JPanel();
		pnl_sourceImg.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnl_sourceImg.setBounds(20, 60, 320, 320);
		mainPane.add(pnl_sourceImg);
		pnl_sourceImg.setLayout(new BorderLayout(0, 0));
		
		lbl_sourceImg = new JLabel("Por favor, seleccione una imagen fuente");
		lbl_sourceImg.setFont(fnt_normal);
		lbl_sourceImg.setHorizontalAlignment(SwingConstants.CENTER);
		pnl_sourceImg.add(lbl_sourceImg, BorderLayout.CENTER);
		
		chkbx_grayscale = new JCheckBox("Grayscale");
		chkbx_grayscale.setEnabled(false);
		chkbx_grayscale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chkbx_grayscale.isSelected())
					lbl_sourceImg.setIcon(new ImageIcon(grayscaleImg));
				else
					lbl_sourceImg.setIcon(new ImageIcon(sourceImg));
			}
		});
		chkbx_grayscale.setBounds(348, 352, 113, 25);
		chkbx_grayscale.setFont(fnt_normal);
		mainPane.add(chkbx_grayscale);
		
		JLabel lbl_options = new JLabel("Opciones de par\u00E1metros:");
		lbl_options.setFont(fnt_title);
		lbl_options.setBounds(360, 20, 240, 25);
		mainPane.add(lbl_options);
		
		JTabbedPane tabPnl_conditions = new JTabbedPane(JTabbedPane.TOP);
		tabPnl_conditions.setFont(fnt_normal);
		tabPnl_conditions.setBounds(352, 60, 416, 285);
		mainPane.add(tabPnl_conditions);
		
		JPanel pnl_generalOptions = new JPanel();
		tabPnl_conditions.addTab("General", null, pnl_generalOptions, null);
		pnl_generalOptions.setLayout(null);
		generalOptionsStartup(pnl_generalOptions);
		
		JPanel pnl_crossOptions = new JPanel();
		tabPnl_conditions.addTab("Reproducción", null, pnl_crossOptions, null);
		pnl_crossOptions.setLayout(null);
		crossoverOptionsStartup(pnl_crossOptions);
		
		JPanel pnl_endOptions = new JPanel();
		tabPnl_conditions.addTab("Parada", null, pnl_endOptions, null);
		pnl_endOptions.setLayout(null);
		endOptionsStartup(pnl_endOptions);
		
		JPanel pnl_mutateOptions = new JPanel();
		tabPnl_conditions.addTab("Mutación", null, pnl_mutateOptions, null);
		pnl_mutateOptions.setLayout(null);
		mutateOptionsStartup(pnl_mutateOptions);
		btn_start = new JButton("Empezar");
		btn_start.setEnabled(false);
		btn_start.setBounds(668, 350, 100, 30);
		btn_start.setFont(fnt_title );
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (lbl_sourceImg.getIcon() != null)
					startAlgorithm();
					//generateImg();
				else
					JOptionPane.showMessageDialog(rootPane, "Por favor, seleccione una imagen fuente.", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		});
		mainPane.add(btn_start);
		
		JLabel lbl_pixelInfo = new JLabel("Pixel info:");
		lbl_pixelInfo.setFont(fnt_title);
		lbl_pixelInfo.setBounds(352, 421, 120, 16);
		mainPane.add(lbl_pixelInfo);
		
		JScrollPane pnl_pixelInfo = new JScrollPane();
		pnl_pixelInfo.setBounds(352, 450, 416, 280);
		mainPane.add(pnl_pixelInfo);
		
		txt_pixelInfo = new JTextArea();
		txt_pixelInfo.setEditable(false);
		pnl_pixelInfo.setViewportView(txt_pixelInfo);
				
		JPanel pnl_outputImg = new JPanel();
		pnl_outputImg.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnl_outputImg.setBounds(20, 450, 320, 320);
		mainPane.add(pnl_outputImg);
		pnl_outputImg.setLayout(new BorderLayout(0, 0));
		
		lbl_outputImg = new JLabel("");
		lbl_outputImg.setHorizontalAlignment(SwingConstants.CENTER);
		pnl_outputImg.add(lbl_outputImg, BorderLayout.CENTER);
		
		JLabel lbl_output = new JLabel("Output:");
		lbl_output.setFont(fnt_title);
		lbl_output.setBounds(20, 421, 100, 16);
		mainPane.add(lbl_output);
		
		JSeparator sp1 = new JSeparator();
		sp1.setBounds(20, 400, 748, 2);
		mainPane.add(sp1);
		
		lbl_imgSize = new JLabel("Tama\u00F1o:");
		lbl_imgSize.setBounds(460, 352, 196, 25);
		lbl_imgSize.setFont(fnt_normal);
		mainPane.add(lbl_imgSize);
		
		JButton btn_info = new JButton("i");
		btn_info.setBounds(757, 0, 30, 30);
		btn_info.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		btn_info.setFocusPainted(false);
		btn_info.setOpaque(false);
	    btn_info.setForeground(Color.BLUE);
	    btn_info.setBackground(Color.WHITE);
	    btn_info.setFont(fnt_title);
		btn_info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aboutPopUp();
			}
		});
		mainPane.add(btn_info);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(352, 740, 416, 30);
		mainPane.add(progressBar);
	}
	
	private void aboutPopUp()
	{
		String msg = "IC 3002 - Análisis de Algoritmos, GR 1"
				+  "\nProfesor: José Carranza Rojas"
				+"\n\nProyecto 1: Van Gogh Evolucional"
				+  "\nElaborado por:"
				+  "\n> 2017146886 = Carlos Roberto Esquivel Morales"
				+  "\n> 2017###### = José Fabio Hidalgo Rodriguez"
				+"\n\nSemestre 2, 2018";
		JOptionPane.showMessageDialog(rootPane, msg, "About", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void generalOptionsStartup(JPanel pnl)
	{
		JLabel lbl_startingPop = new JLabel("Poblaci\u00F3n inicial:");
		lbl_startingPop.setBounds(15, 15, 150, 20);
		lbl_startingPop.setFont(fnt_normal);
		pnl.add(lbl_startingPop);
		
		JLabel lbl_startingPopNum = new JLabel("64");
		lbl_startingPopNum.setBounds(339, 35, 40, 30);
		lbl_startingPopNum.setFont(fnt_normal);
		pnl.add(lbl_startingPopNum);
		
		sldr_startingPop = new JSlider();
		sldr_startingPop.setEnabled(false);
		sldr_startingPop.setMaximum(256);
		sldr_startingPop.setMinorTickSpacing(16);
		sldr_startingPop.setValue(64);
		sldr_startingPop.setMinimum(16);
		sldr_startingPop.setPaintTicks(true);
		sldr_startingPop.setBounds(25, 35, 300, 30);
		sldr_startingPop.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int startingPop = sldr_startingPop.getValue();
				lbl_startingPopNum.setText(String.valueOf(startingPop));
				algorithmManager.setStartingPopulation(startingPop);
			}
		});
		pnl.add(sldr_startingPop);
		
		JLabel lbl_fitness = new JLabel("Algoritmo de aptitud:");
		lbl_fitness.setBounds(15, 80, 200, 20);
		lbl_fitness.setFont(fnt_normal);
		pnl.add(lbl_fitness);
		
		JRadioButton rdbtn_Fit0 = new JRadioButton("Similitud Euclideana");
		rdbtn_Fit0.setSelected(true);
		rdbtn_Fit0.setBounds(15, 110, 200, 25);
		rdbtn_Fit0.setFont(fnt_normal);
		rdbtn_Fit0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmManager.setFitnessAlgorithm(AlgorithmManager.FIT_Euclidean);
			}
		});
		pnl.add(rdbtn_Fit0);
		
		JRadioButton rdbtn_Fit1 = new JRadioButton("Algoritmo 1");
		rdbtn_Fit1.setBounds(15, 140, 200, 25);
		rdbtn_Fit1.setFont(fnt_normal);
		rdbtn_Fit1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmManager.setFitnessAlgorithm(AlgorithmManager.FIT_Algorithm1);
			}
		});
		pnl.add(rdbtn_Fit1);
		
		JRadioButton rdbtn_Fit2 = new JRadioButton("Algoritmo 2");
		rdbtn_Fit2.setBounds(15, 170, 200, 25);
		rdbtn_Fit2.setFont(fnt_normal);
		rdbtn_Fit2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmManager.setFitnessAlgorithm(AlgorithmManager.FIT_Algorithm2);
			}
		});
		pnl.add(rdbtn_Fit2);
		
	    ButtonGroup FitnessBtns = new ButtonGroup();
	    FitnessBtns.add(rdbtn_Fit0);
	    FitnessBtns.add(rdbtn_Fit1);
	    FitnessBtns.add(rdbtn_Fit2);
	}
	
	private void crossoverOptionsStartup(JPanel pnl)
	{
		JLabel lbl_crossover = new JLabel("Tipo de Reproducci\u00F3n:");
		lbl_crossover.setBounds(15, 15, 180, 20);
		lbl_crossover.setFont(fnt_normal);
		pnl.add(lbl_crossover);
		
		JRadioButton rdbtn_Cross0 = new JRadioButton("Mitad Horizontal");
		rdbtn_Cross0.setSelected(true);
		rdbtn_Cross0.setBounds(15, 45, 180, 25);
		rdbtn_Cross0.setFont(fnt_normal);
		rdbtn_Cross0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmManager.setCrossoverType(AlgorithmManager.CROSS_Hor);
			}
		});
		pnl.add(rdbtn_Cross0);
		
		JRadioButton rdbtn_Cross1 = new JRadioButton("Mitad Vertical");
		rdbtn_Cross1.setBounds(15, 75, 180, 25);
		rdbtn_Cross1.setFont(fnt_normal);
		rdbtn_Cross1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmManager.setCrossoverType(AlgorithmManager.CROSS_Vert);
			}
		});
		pnl.add(rdbtn_Cross1);
		
		JRadioButton rdbtn_Cross2 = new JRadioButton("Cuartos de Imagen");
		rdbtn_Cross2.setBounds(15, 105, 180, 25);
		rdbtn_Cross2.setFont(fnt_normal);
		rdbtn_Cross2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algorithmManager.setCrossoverType(AlgorithmManager.CROSS_Quart);
			}
		});
		pnl.add(rdbtn_Cross2);
		
		ButtonGroup CrossoverBtns = new ButtonGroup();
		CrossoverBtns.add(rdbtn_Cross0);
		CrossoverBtns.add(rdbtn_Cross1);
		CrossoverBtns.add(rdbtn_Cross2);
	}
		
	private void endOptionsStartup(JPanel pnl)
	{
		JLabel lbl_endCondition = new JLabel("Condici\u00F3n de parada:");
		lbl_endCondition.setBounds(15, 15, 180, 20);
		pnl.add(lbl_endCondition);
		
		JRadioButton rdbtn_genNum = new JRadioButton("N\u00FAmero de Generaciones:");
		rdbtn_genNum.setSelected(true);
		rdbtn_genNum.setBounds(15, 45, 200, 25);
		pnl.add(rdbtn_genNum);
		
		JLabel lbl_genNum = new JLabel("20");
		lbl_genNum.setBounds(337, 80, 40, 25);
		pnl.add(lbl_genNum);
		
		JSlider sldr_genNum = new JSlider();
		sldr_genNum.setValue(20);
		sldr_genNum.setPaintTicks(true);
		sldr_genNum.setMinorTickSpacing(5);
		sldr_genNum.setMajorTickSpacing(10);
		sldr_genNum.setMinimum(10);
		sldr_genNum.setBounds(25, 80, 300, 30);
		sldr_genNum.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int genNum = sldr_genNum.getValue();
				lbl_genNum.setText(String.valueOf(genNum));
				algorithmManager.setGenTotal(genNum);
			}
		});
		pnl.add(sldr_genNum);
		
		JRadioButton rdbtn_fitMin = new JRadioButton("Aptitud M\u00EDnima:");
		rdbtn_fitMin.setBounds(15, 120, 200, 25);
		pnl.add(rdbtn_fitMin);
		
		
		JLabel lbl_fitMin = new JLabel("85");
		lbl_fitMin.setBounds(337, 160, 56, 16);
		pnl.add(lbl_fitMin);
		
		JSlider sldr_fitMin = new JSlider();
		sldr_fitMin.setEnabled(false);
		sldr_fitMin.setMinorTickSpacing(5);
		sldr_fitMin.setMajorTickSpacing(10);
		sldr_fitMin.setMinimum(70);
		sldr_fitMin.setMaximum(95);
		sldr_fitMin.setValue(85);
		sldr_fitMin.setPaintTicks(true);
		sldr_fitMin.setBounds(25, 160, 300, 30);
		sldr_fitMin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int fitMin = sldr_fitMin.getValue();
				lbl_fitMin.setText(String.valueOf(fitMin));
				algorithmManager.setFitnessMin(fitMin);
			}
		});
		pnl.add(sldr_fitMin);

		rdbtn_genNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sldr_genNum.setEnabled(true);
				sldr_fitMin.setEnabled(false);
				algorithmManager.setFitnessMin(-1);
				algorithmManager.setGenTotal(sldr_genNum.getValue());
			}
		});
		
		rdbtn_fitMin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sldr_fitMin.setEnabled(true);
				sldr_genNum.setEnabled(false);
				algorithmManager.setGenTotal(-1);
				algorithmManager.setFitnessMin(sldr_fitMin.getValue());
			}
		});
		
		ButtonGroup EndBtns = new ButtonGroup();
		EndBtns.add(rdbtn_genNum);
		EndBtns.add(rdbtn_fitMin);	
	}
	
	private void mutateOptionsStartup(JPanel pnl)
	{
		
	}
	
	public boolean pickSourceImg() throws IOException
	{
		boolean result = false;
        if (imgChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
        	File file = imgChooser.getSelectedFile();
            BufferedImage newSourceImg = ImageIO.read(file);
            if (newSourceImg.getWidth() >= 16 && newSourceImg.getHeight() >= 16) 
            {
	            actualWidth = newSourceImg.getWidth();
	            actualHeight = newSourceImg.getHeight();
	            sourceImg = resizeImg(320, 320, newSourceImg);
	            grayscaleImg = getGrayscale();
	            lbl_sourceImg.setIcon(new ImageIcon(sourceImg));
	            lbl_sourceImg.setText(null);
	            btn_getImage.setText(file.getPath());
	            result = true;
	        }
            else {
            	JOptionPane.showMessageDialog(rootPane, "El ancho y/o el largo de la imagen es menor a 16 pixeles. Las dimensiones mínimas son 16x16",
            								  "ERROR - Imagen de tamaño inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
        return result;
	}
	
	public void newSourceUpdateUI()
	{
		//start button
		if (!btn_start.isEnabled())
			btn_start.setEnabled(true);
		
		//grayscale checkbox
		if (!chkbx_grayscale.isEnabled())
			chkbx_grayscale.setEnabled(true);
		else if (chkbx_grayscale.isSelected())
			chkbx_grayscale.setSelected(false);
		
		//starting population slider
		if (!sldr_startingPop.isEnabled())
			sldr_startingPop.setEnabled(true);
		
		//size label
		String sizeStr = "Tamaño: ";
		if (sourceImg.getWidth() < actualWidth && sourceImg.getHeight() < actualHeight)
			sizeStr += sourceImg.getWidth() + "x" + sourceImg.getHeight();
		else
			sizeStr += actualWidth + "x" + actualHeight;
		lbl_imgSize.setText(sizeStr);
	}
	
	public BufferedImage resizeImg(int desiredWidth, int desiredHeight, BufferedImage img)
	{
        if (img.getWidth() > img.getHeight())
        	desiredHeight = -1;
        else if (img.getHeight() > img.getWidth())
        	desiredWidth = -1;
        Image toolkitImage = img.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
        desiredWidth = toolkitImage.getWidth(null);
        desiredHeight = toolkitImage.getHeight(null);
        BufferedImage resizedImage = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = resizedImage.getGraphics();
        g.drawImage(toolkitImage, 0, 0, null);
        g.dispose();
        return resizedImage;
	}
	
	public BufferedImage getGrayscale()
	{
		BufferedImage grayscaleImg = new BufferedImage(sourceImg.getWidth(), sourceImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		Graphics g = grayscaleImg.getGraphics();
		g.drawImage(sourceImg, 0, 0, null);
		g.dispose();
		return grayscaleImg;
	}
	
	public void generateImg()
	{
		algorithmManager.setMetaImg(grayscaleImg);
		algorithmManager.crossoverTest();
		/*txt_pixelInfo.setText(null);
		int width = sourceImg.getWidth(), 
			height = sourceImg.getHeight();
		if (width > actualWidth && height > actualHeight) {
			width = actualWidth;
			height = actualHeight;
		}
		BufferedImage img = generador.createImage(width, height);
		pixelReader.processPixels(img);
		setOutputImg(img);*/
	}
	
	public void startAlgorithm()
	{
		algorithmManager.setMetaImg(grayscaleImg);
		algorithmManager.setStartingPopulation(sldr_startingPop.getValue());
		setUIEnabled(false);
		algorithmThread = new Thread(algorithmManager);
		algorithmThread.start();
	}
	
	public void setOutputImg(BufferedImage img)
	{
		img = resizeImg(320, 320, img);
		lbl_outputImg.setIcon(new ImageIcon(img));
	}
	
	public void setUIEnabled(boolean arg) {
		btn_start.setEnabled(arg);
		progressBar.setStringPainted(!arg);
	}
	
	public void updateProgressBar(int num) {
		System.out.println("In progress bar: "+num);
		progressBar.setValue(num);
	}
}
