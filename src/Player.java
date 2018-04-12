import playWave.PlayWaveFile;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import playRGB.imageReader;

public class Player extends MouseAdapter {
	final static int FRAME_DELAY = 35;
	private JButton playBtn = new JButton("Play");
	private JButton pauseBtn = new JButton("Pause");
	private JButton stopBtn = new JButton("Stop");

	private imageReader ims;
	private PlayWaveFile sound;
	private Timer tm;
	private int frameNum = 0;
	
	public JPanel createCtrlPanel()
	{
		JPanel ctrlPanel = new JPanel();
		ctrlPanel.add(playBtn);
		ctrlPanel.add(pauseBtn);
		ctrlPanel.add(stopBtn);		
		return ctrlPanel;
	}
	
	public JPanel getPanel()
	{
		JPanel videoPanel = new JPanel(new GridLayout(2, 1));
		JLabel pic = new JLabel();
		pic.setIcon(new ImageIcon(ims.imgSrc[0]));
		tm = new Timer(FRAME_DELAY, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				pic.setIcon(new ImageIcon(ims.imgSrc[frameNum++]));
                if(frameNum >= ims.imgSrc.length)
                {
                    tm.stop();
                    frameNum = 0;
                }
            }
		});
		videoPanel.add(pic);
		videoPanel.add(createCtrlPanel());
		return videoPanel;
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getSource() == playBtn) {
			tm.start();
		}
		else if (me.getSource() == pauseBtn) {
			tm.stop();
		}
		else if (me.getSource() == stopBtn) { 
			tm.stop();
			frameNum = 0;
		}
	}
	
	public Player(String imgSrc, String soundSrc)
	{	
		ims = new imageReader(imgSrc);
		sound = new PlayWaveFile(soundSrc);
	
		playBtn.addMouseListener(this);
		pauseBtn.addMouseListener(this);
		stopBtn.addMouseListener(this);
	}
}
