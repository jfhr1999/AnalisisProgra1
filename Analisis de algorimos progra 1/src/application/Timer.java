package application;

import java.awt.Color;

import javax.swing.JLabel;

public class Timer implements Runnable
{
	private JLabel lbl_text;
	private boolean run = false;
	
	public Timer(JLabel pLabel) {
		lbl_text = pLabel;
	}
	
	@Override
	public void run() {
		lbl_text.setForeground(Color.BLACK);
		run = true;
		long startTime = System.currentTimeMillis(),
			 elapsedTime;
		int	 millisDisplay,
			 elapsedSeconds,
			 secondsDisplay,
			 elapsedMinutes,
			 minutesDisplay,
			 elapsedHours;
		String timeStr;
		while (run)
		{
			elapsedTime = System.currentTimeMillis() - startTime;
			millisDisplay = (int) (elapsedTime % 1000);
			elapsedSeconds = (int) (elapsedTime / 1000);
			secondsDisplay = elapsedSeconds % 60;
			elapsedMinutes = elapsedSeconds / 60;
			minutesDisplay = elapsedMinutes % 60;
			elapsedHours = elapsedMinutes / 60;
			timeStr = String.valueOf(elapsedHours) + ":" +
					  String.valueOf(minutesDisplay) + ":" +
					  String.valueOf(secondsDisplay) + "." +
					  String.valueOf(millisDisplay);
			lbl_text.setText(timeStr);
		}
	}
	
	public void stop() {
		run = false;
		lbl_text.setForeground(Color.BLUE);
	}
}
