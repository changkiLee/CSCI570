package image;
import java.awt.Adjustable;
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
import javax.swing.JScrollBar;
import javax.swing.Timer;
import sun.audio.*;

public class Player extends MouseAdapter {
	final static int FRAME_DELAY = 34;
	// controller
	private JButton playBtn = new JButton("Play");
	private JButton pauseBtn = new JButton("Pause");
	private JButton stopBtn = new JButton("Stop");
	private JLabel frame = new JLabel("Frame: 0");
	private JScrollBar scroll = new JScrollBar(Adjustable.HORIZONTAL, 0, 0, 0, 100);

	// image variables
	private imageReader ims;	// contains video
	private int framePos = 0;
	private Timer tm;
	
	// audio variables
	private String soundPath;
	@SuppressWarnings("restriction")
	private AudioStream as;		// contains audio
	
	public JPanel createCtrlPanel()
	{
		JPanel ctrlPanel = new JPanel(new GridLayout(4, 1));
		JPanel btn = new JPanel();
		btn.add(playBtn);
		btn.add(pauseBtn);
		btn.add(stopBtn);
		ctrlPanel.add(frame);
		ctrlPanel.add(scroll);
		ctrlPanel.add(btn);
		return ctrlPanel;
	}
	
	public JPanel getPanel()
	{
		JPanel videoPanel = new JPanel(new GridLayout(2, 1));
		JLabel pic = new JLabel();
		pic.setIcon(new ImageIcon(ims.imgSrc[0]));  // first frame image
		frame.setFont(frame.getFont().deriveFont(32.0f));
		tm = new Timer(FRAME_DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pic.setIcon(new ImageIcon(ims.imgSrc[framePos++]));  // change image to next frame
				frame.setText("Frame: " + framePos);
				scroll.setValue(framePos * 100 / ims.imgSrc.length);
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
		scroll.setValue(0);
		AudioPlayer.player.stop(as);
		try {
			as = new AudioStream(new FileInputStream(soundPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("restriction")
	public Player(String src, String path)
	{	
		// create image	
		ims = new imageReader(src, path);

		soundPath = path + src + "/" + src + ".wav";
		// create sound
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
