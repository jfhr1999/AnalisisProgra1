package generador;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

public class AppWin extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPane;
	private GeneradorImagenes generador = new GeneradorImagenes();
	private PixelReader pixelReader;
	private JLabel imgLabel;
	private JTextArea pixelText;

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 790, 440);
		mainPane = new JPanel();
		mainPane.setBorder(null);
		setContentPane(mainPane);
		mainPane.setLayout(null);
		
		JButton btnGenerarImagen = new JButton("Generar imagen");
		btnGenerarImagen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				generarImg();
			}
		});
		btnGenerarImagen.setBounds(12, 13, 140, 25);
		mainPane.add(btnGenerarImagen);
		
		JPanel imagePanel = new JPanel();
		imagePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		imagePanel.setBounds(12, 60, 320, 320);
		mainPane.add(imagePanel);
		imagePanel.setLayout(new BorderLayout(0, 0));
		
		imgLabel = new JLabel("");
		imagePanel.add(imgLabel, BorderLayout.CENTER);
		
		JLabel lblPixelInfo = new JLabel("Pixel info:");
		lblPixelInfo.setBounds(344, 17, 56, 16);
		mainPane.add(lblPixelInfo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(344, 60, 416, 320);
		mainPane.add(scrollPane);
		
		pixelText = new JTextArea();
		pixelText.setEditable(false);
		scrollPane.setViewportView(pixelText);
		
		pixelReader = new PixelReader(pixelText);
	}
	
	public void generarImg()
	{
		pixelText.setText(null);
		BufferedImage img = generador.createImage();
		ImageIcon imgIcon = new ImageIcon(img);
		imgLabel.setIcon(imgIcon);
		pixelReader.processPixels(img);
	}
}
