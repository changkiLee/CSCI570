import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import sun.audio.*;
import playRGB.imageReader;

public class Player extends MouseAdapter {
	final static int FRAME_DELAY = 33;
	// controller
	private JButton playBtn = new JButton("Play");
	private JButton pauseBtn = new JButton("Pause");
	private JButton stopBtn = new JButton("Stop");

	// image variables
	private String imgPath;
	private imageReader ims;	// contains video
	private int framePos = 0;
	private Timer tm;
	
	// audio variables
	private String soundPath;
	@SuppressWarnings("restriction")
	private AudioStream as;		// contains audio
	
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
		pic.setIcon(new ImageIcon(ims.imgSrc[0]));  // first frame image
		tm = new Timer(FRAME_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pic.setIcon(new ImageIcon(ims.imgSrc[framePos++]));  // change image to next frame
                // when video playback is complete
				if(framePos >= ims.imgSrc.length) {
                    videoStop();
                }
            }
		});
		videoPanel.add(pic);
		videoPanel.add(createCtrlPanel());
		
		return videoPanel;
	}
	
	@SuppressWarnings("restriction")
	@Override
	public void mouseClicked(MouseEvent me) {
		// play
		if (me.getSource() == playBtn) {
			tm.start();
			AudioPlayer.player.start(as);
		}
		// pause
		else if (me.getSource() == pauseBtn) {
			tm.stop();
			AudioPlayer.player.stop(as);
		}
		// stop
		else if (me.getSource() == stopBtn) { 
			videoStop();
		}
	}
	
	@SuppressWarnings("restriction")
	private void videoStop() {
		tm.stop();
		framePos = 0;
		AudioPlayer.player.stop(as);
		try {
			as = new AudioStream(new FileInputStream(soundPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("restriction")
	public Player(String imgSrc, String soundSrc)
	{	
		// create image
		imgPath = imgSrc;		
		ims = new imageReader(imgPath);

		// create sound
		soundPath = soundSrc;
		try {
			as = new AudioStream(new FileInputStream(soundPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// add listeners
		playBtn.addMouseListener(this);
		pauseBtn.addMouseListener(this);
		stopBtn.addMouseListener(this);
	}
}
