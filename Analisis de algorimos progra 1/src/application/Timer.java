package application;

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
		run = true;
		long startTime = System.currentTimeMillis(),
			 elapsedTime,
			 millisDisplay,
			 elapsedSeconds,
			 secondsDisplay,
			 elapsedMinutes,
			 elapsedHours;
		String timeStr;
		while (run)
		{
			elapsedTime = System.currentTimeMillis() - startTime;
			millisDisplay = elapsedTime % 1000;
			elapsedSeconds = elapsedTime / 1000;
			secondsDisplay = elapsedSeconds % 60;
			elapsedMinutes = elapsedSeconds / 60;
			elapsedHours = elapsedMinutes / 60;
			timeStr = String.valueOf(elapsedHours) + ":" +
					  String.valueOf(elapsedMinutes) + ":" +
					  String.valueOf(secondsDisplay) + "." +
					  String.valueOf(millisDisplay);
			lbl_text.setText(timeStr);
		}
	}
	
	public void stop() {
		run = false;
	}
}
