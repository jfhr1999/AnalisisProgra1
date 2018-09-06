package generador;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;

public class AppWin extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//util
	private static final Font fnt_normal = new Font("DialogInput", Font.PLAIN, 14);
	private static final Font fnt_title = new Font("Consolas", Font.PLAIN, 16);
	private JFileChooser imgChooser;
	private GeneradorImagenes generador = new GeneradorImagenes();
	private PixelReader pixelReader;
	
	//UI
	private JPanel mainPane;
	private JButton btn_getImage;
	private JLabel lbl_sourceImg;
	private BufferedImage sourceImg;
	private JButton btn_start;
	private JLabel lbl_outputImg;
	private JTextArea txt_pixelInfo;

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

	/**
	 * Create the frame.
	 */
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
					pickSourceImg();
					btn_start.setEnabled(true);
				}
				catch (Exception ex) {
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
		
		JLabel lbl_conditions = new JLabel("Condiciones del algoritmo");
		lbl_conditions.setFont(fnt_title);
		lbl_conditions.setBounds(360, 20, 240, 25);
		mainPane.add(lbl_conditions);
		
		JTabbedPane tabPnl_conditions = new JTabbedPane(JTabbedPane.TOP);
		tabPnl_conditions.setFont(fnt_normal);
		tabPnl_conditions.setBounds(352, 60, 416, 285);
		mainPane.add(tabPnl_conditions);
		
		JPanel panel = new JPanel();
		tabPnl_conditions.addTab("New tab", null, panel, null);
		
		JPanel panel_1 = new JPanel();
		tabPnl_conditions.addTab("New tab", null, panel_1, null);
		
		btn_start = new JButton("Empezar");
		btn_start.setEnabled(false);
		btn_start.setBounds(668, 350, 100, 30);
		btn_start.setFont(fnt_title );
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				generarImg();
			}
		});
		mainPane.add(btn_start);
		
		JLabel lbl_pixelInfo = new JLabel("Pixel info:");
		lbl_pixelInfo.setFont(fnt_title);
		lbl_pixelInfo.setBounds(352, 421, 120, 16);
		mainPane.add(lbl_pixelInfo);
		
		JScrollPane pnl_pixelInfo = new JScrollPane();
		pnl_pixelInfo.setBounds(352, 450, 416, 320);
		mainPane.add(pnl_pixelInfo);
		
		txt_pixelInfo = new JTextArea();
		txt_pixelInfo.setEditable(false);
		pnl_pixelInfo.setViewportView(txt_pixelInfo);
		
		pixelReader = new PixelReader(txt_pixelInfo);
		
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
	}
	
	public void pickSourceImg() throws IOException
	{
        if (imgChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
        	File file = imgChooser.getSelectedFile();
            sourceImg = ImageIO.read(file);
            int width = 320,
            	height = 320;
            if (sourceImg.getWidth() > sourceImg.getHeight())
            	height = -1;
            else if (sourceImg.getHeight() > sourceImg.getWidth())
            	width = -1;
            ImageIcon icon = new ImageIcon(sourceImg.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            lbl_sourceImg.setIcon(icon);
            lbl_sourceImg.setText(null);
            btn_getImage.setText(file.getPath());
        }
	}
	
	public void generarImg()
	{
		txt_pixelInfo.setText(null);
		BufferedImage img = generador.createImage();
		ImageIcon imgIcon = new ImageIcon(img);
		lbl_outputImg.setIcon(imgIcon);
		pixelReader.processPixels(img);
	}
}
