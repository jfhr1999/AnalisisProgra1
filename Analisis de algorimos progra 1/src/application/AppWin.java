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
	private Timer timer;
	private Thread timerThread;
	
	// UI ~ ~ ~ ~ ~ ~
	private JPanel mainPane;
	private JButton btn_getImage;
	private JLabel lbl_sourceImg;
	private BufferedImage sourceImg;
	private BufferedImage grayscaleImg;
	private JCheckBox chkbx_grayscale;
	
	private JSlider sldr_startingPop;
	private JSlider sldr_genNum;
	private JSlider sldr_fitMin;
	private JSlider sldr_genePerc;
	private JSlider sldr_indvPerc;
	
	private JLabel lbl_imgSize;
	private JButton btn_start;

	// Output 
	private JLabel lbl_outputImg;
	private JTextArea txt_appConsole;
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

	
	public AppWin() {
		setTitle("Proyecto 1 - Van Gogh Evolucional");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 805, 835);
		setResizable(false);
		
		mainPane = new JPanel();
		mainPane.setBorder(null);
		mainPane.setLayout(null);
		setContentPane(mainPane);
		
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
		tabPnl_conditions.addTab("Cruces", null, pnl_crossOptions, null);
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
				if (lbl_sourceImg.getIcon() != null) {
					if (btn_start.getText().equals("Empezar"))
						startAlgorithm();
					else
						algorithmManager.manualStop();
					//generateImg();
				}
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
		
		txt_appConsole = new JTextArea();
		txt_appConsole.setEditable(false);
		txt_appConsole.setFont(fnt_normal);
		pnl_pixelInfo.setViewportView(txt_appConsole);
				
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
		btn_info.setBounds(769, 0, 30, 30);
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
		progressBar.setBounds(352, 740, 304, 30);
		mainPane.add(progressBar);
		
		JLabel lbl_execTime = new JLabel("00:00:00.0");
		lbl_execTime.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_execTime.setBounds(668, 740, 100, 30);
		lbl_execTime.setFont(fnt_normal);
		mainPane.add(lbl_execTime);
		
		timer = new Timer(lbl_execTime);
	}
	
	private void aboutPopUp()
	{
		String msg = "IC 3002 - Análisis de Algoritmos, GR 1"
				+  "\nProfesor: José Carranza Rojas"
				+"\n\nProyecto 1: Van Gogh Evolucional"
				+  "\nElaborado por:"
				+  "\n> 2017146886 = Carlos Roberto Esquivel Morales"
				+  "\n> 2017100950 = José Fabio Hidalgo Rodriguez"
				+"\n\nSemestre 2, 2018";
		JOptionPane.showMessageDialog(rootPane, msg, "About", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void generalOptionsStartup(JPanel pnl)
	{
		JLabel lbl_startingPop = new JLabel("Poblaci\u00F3n inicial:");
		lbl_startingPop.setBounds(15, 15, 150, 20);
		lbl_startingPop.setFont(fnt_normal);
		pnl.add(lbl_startingPop);
		
		JLabel lbl_startingPopNum = new JLabel("512");
		lbl_startingPopNum.setBounds(339, 35, 40, 30);
		lbl_startingPopNum.setFont(fnt_normal);
		pnl.add(lbl_startingPopNum);
		
		sldr_startingPop = new JSlider();
		sldr_startingPop.setMajorTickSpacing(256);
		sldr_startingPop.setEnabled(false);
		sldr_startingPop.setMaximum(2048);
		sldr_startingPop.setMinorTickSpacing(64);
		sldr_startingPop.setValue(512);
		sldr_startingPop.setMinimum(256);
		sldr_startingPop.setPaintTicks(true);
		sldr_startingPop.setBounds(25, 35, 300, 30);
		sldr_startingPop.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int startingPop = sldr_startingPop.getValue();
				lbl_startingPopNum.setText(String.valueOf(startingPop));
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
		JLabel lbl_crossOptions = new JLabel("Tipo de Cruce:");
		lbl_crossOptions.setBounds(15, 15, 180, 20);
		lbl_crossOptions.setFont(fnt_normal);
		pnl.add(lbl_crossOptions);
		
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
		JLabel lbl_endOptions = new JLabel("Condici\u00F3n de parada:");
		lbl_endOptions.setBounds(15, 15, 180, 20);
		lbl_endOptions.setFont(fnt_normal);
		pnl.add(lbl_endOptions);
		
		JRadioButton rdbtn_genNum = new JRadioButton("N\u00FAmero de Generaciones:");
		rdbtn_genNum.setSelected(true);
		rdbtn_genNum.setBounds(15, 45, 220, 25);
		rdbtn_genNum.setFont(fnt_normal);
		pnl.add(rdbtn_genNum);
		
		JLabel lbl_genNum = new JLabel("1000");
		lbl_genNum.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_genNum.setBounds(340, 80, 40, 30);
		lbl_genNum.setFont(fnt_normal);
		pnl.add(lbl_genNum);
		
		sldr_genNum = new JSlider();
		sldr_genNum.setMaximum(5000);
		sldr_genNum.setValue(1000);
		sldr_genNum.setPaintTicks(true);
		sldr_genNum.setMinorTickSpacing(100);
		sldr_genNum.setMajorTickSpacing(500);
		sldr_genNum.setMinimum(500);
		sldr_genNum.setBounds(25, 80, 300, 30);
		sldr_genNum.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int genNum = sldr_genNum.getValue();
				lbl_genNum.setText(String.valueOf(genNum));
			}
		});
		pnl.add(sldr_genNum);
		
		JRadioButton rdbtn_fitMin = new JRadioButton("Aptitud M\u00EDnima:");
		rdbtn_fitMin.setBounds(15, 120, 220, 25);
		rdbtn_fitMin.setFont(fnt_normal);
		pnl.add(rdbtn_fitMin);
		
		
		JLabel lbl_fitMin = new JLabel("85");
		lbl_fitMin.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_fitMin.setBounds(340, 160, 40, 30);
		lbl_fitMin.setFont(fnt_normal);
		pnl.add(lbl_fitMin);
		
		sldr_fitMin = new JSlider();
		sldr_fitMin.setEnabled(false);
		sldr_fitMin.setMinorTickSpacing(1);
		sldr_fitMin.setMajorTickSpacing(5);
		sldr_fitMin.setMinimum(70);
		sldr_fitMin.setMaximum(95);
		sldr_fitMin.setValue(70);
		sldr_fitMin.setPaintTicks(true);
		sldr_fitMin.setBounds(25, 160, 300, 30);
		sldr_fitMin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int fitMin = sldr_fitMin.getValue();
				lbl_fitMin.setText(String.valueOf(fitMin));
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
		JLabel lbl_mutateOptions = new JLabel("Par\u00E1metros de mutaci\u00F3n:");
		lbl_mutateOptions.setBounds(15, 15, 200, 20);
		lbl_mutateOptions.setFont(fnt_normal);
		pnl.add(lbl_mutateOptions);
		
		JLabel lbl_genePerc = new JLabel("% de genes a mutar: de 5% a 25%");
		lbl_genePerc.setBounds(30, 45, 300, 20);
		lbl_genePerc.setFont(fnt_normal);
		pnl.add(lbl_genePerc);
		
		sldr_genePerc = new JSlider();
		sldr_genePerc.setSnapToTicks(true);
		sldr_genePerc.setPaintTicks(true);
		sldr_genePerc.setMinorTickSpacing(1);
		sldr_genePerc.setMajorTickSpacing(5);
		sldr_genePerc.setMaximum(35);
		sldr_genePerc.setMinimum(10);
		sldr_genePerc.setValue(25);
		sldr_genePerc.setBounds(30, 70, 300, 30);
		sldr_genePerc.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lbl_genePerc.setText("% de genes a mutar: de 5% a "+
									 sldr_genePerc.getValue()+"%");
			}
		});
		pnl.add(sldr_genePerc);
		
		JLabel lbl_indvPerc = new JLabel("% de mutar de un individuo: 20%*");
		lbl_indvPerc.setBounds(30, 110, 300, 20);
		lbl_indvPerc.setFont(fnt_normal);
		pnl.add(lbl_indvPerc);
		
		sldr_indvPerc = new JSlider();
		sldr_indvPerc.setPaintTicks(true);
		sldr_indvPerc.setSnapToTicks(true);
		sldr_indvPerc.setMinorTickSpacing(1);
		sldr_indvPerc.setMajorTickSpacing(5);
		sldr_indvPerc.setValue(20);
		sldr_indvPerc.setMinimum(10);
		sldr_indvPerc.setMaximum(35);
		sldr_indvPerc.setBounds(30, 140, 300, 30);
		sldr_indvPerc.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lbl_indvPerc.setText("% de mutar de un individuo: "+
									 sldr_indvPerc.getValue()+"%*");
			}
		});
		pnl.add(sldr_indvPerc);
		
		JTextPane lbl_disclaimer = new JTextPane();
		lbl_disclaimer.setEnabled(false);
		lbl_disclaimer.setEditable(false);
		lbl_disclaimer.setDisabledTextColor(Color.GRAY);
		lbl_disclaimer.setBackground(UIManager.getColor("menu"));
		lbl_disclaimer.setText("* Hay un 5% de posibilidad de que la soluci\u00F3n de un individuo se reinicie completamente.");
		lbl_disclaimer.setBounds(30, 192, 381, 46);
		lbl_disclaimer.setFont(fnt_normal);
		pnl.add(lbl_disclaimer);
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
	            grayscaleImg = getGrayscale(sourceImg);
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
	
	public BufferedImage getGrayscale(BufferedImage img)
	{
		BufferedImage grayscaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		Graphics g = grayscaleImg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return grayscaleImg;
	}
	
	public void startAlgorithm()
	{
		txt_appConsole.setText(null);
		
		int width = sourceImg.getWidth(), 
			height = sourceImg.getHeight();
		if (width > actualWidth && height > actualHeight) {
			width = actualWidth;
			height = actualHeight;
		}
		
		BufferedImage metaImg = getGrayscale(resizeImg(width, height, sourceImg));
		algorithmManager.setMetaImg(metaImg);
		
		algorithmManager.setStartingPopulation(sldr_startingPop.getValue());
		
		if (sldr_genNum.isEnabled()) {
			algorithmManager.setGenTotal(sldr_genNum.getValue());
			algorithmManager.setFitnessMin(-1);
		}
		else {
			algorithmManager.setFitnessMin(sldr_fitMin.getValue());
			algorithmManager.setGenTotal(-1);
		}
		
		algorithmManager.setGenePercentage(sldr_genePerc.getValue());
		algorithmManager.setIndvPercentage(sldr_indvPerc.getValue());
		
		setUIEnabled(false);
		algorithmThread = new Thread(algorithmManager);
		timerThread = new Thread(timer);
		algorithmThread.start();
		timerThread.start();
	}
	
	public void setOutputImg(BufferedImage img) {
		img = resizeImg(320, 320, img);
		lbl_outputImg.setIcon(new ImageIcon(img));
	}
	
	public void setUIEnabled(boolean arg) {
		if (arg) {
			btn_start.setText("Empezar");
			timer.stop();
			progressBar.setValue(100);
		}
		else
			btn_start.setText("Parar");
		btn_getImage.setEnabled(arg);
		progressBar.setStringPainted(!arg);
	}
	
	public void updateProgressBar(int num) {
		progressBar.setValue(num);
	}
	
	public void writeMsg(String msg) {
		txt_appConsole.append(msg + "\n");
	}
}
