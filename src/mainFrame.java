import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import soundFFT.FFT;
import soundFFT.SoundMath;
import soundFFT.WavSoundInput;

@SuppressWarnings("serial")
public class mainFrame extends JFrame implements ActionListener {
	static final String[] database = {
		"resource/databse_videos/flowers/flowers",
		"resource/databse_videos/interview/interview",
		"resource/databse_videos/movie/movie",
		"resource/databse_videos/musicvideo/musicvideo",
		"resource/databse_videos/sports/sports",
		"resource/databse_videos/starcraft/starcraft",
		"resource/databse_videos/traffic/traffic"
	};
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel inputVideo;
	private JPanel outputVideo;
	
	public JPanel createInputPanel(String imgSrc, String soundSrc) {
		JPanel inputPanel = new JPanel();
		// text
		JLabel inputText = new JLabel("Query: " + imgSrc);	
		inputPanel.add(inputText);
		
		// video
		inputVideo = createVideoPanel(imgSrc, soundSrc);
		inputPanel.add(inputVideo);		
		
		return inputPanel;
	}
	
	public JPanel createOutputPanel() {
		JPanel outputPanel = new JPanel();
		// text
		JLabel outputText = new JLabel("Matched Videos: ");		
		outputPanel.add(outputText);
				
		// create radio buttons
		ButtonGroup btnGroup = new ButtonGroup();
		boolean status = true;
		for(int i = 0; i < database.length; ++i)
		{			
			JRadioButton btn = new JRadioButton(database[i], status);
			btn.addActionListener(this);
			btnGroup.add(btn);
			outputPanel.add(btn);
			status = false;
		}
		
		// video
		outputVideo = createVideoPanel(database[0]);
		outputPanel.add(outputVideo);
		return outputPanel;
	}
	
	// create output video panel
	public JPanel createVideoPanel(String src) {
		String imgSrc = src + ".rgb";
		String soundSrc = src + ".wav";
		Player video = new Player(imgSrc, soundSrc);
		return video.getPanel();
	}
	
	// create input video panel
	public JPanel createVideoPanel(String imgSrc, String soundSrc)
	{
		Player video = new Player(imgSrc, soundSrc);
		return video.getPanel();
	}
	
	// radio button selection
	public void actionPerformed(ActionEvent e) {
        String selected = e.getActionCommand();
        // change video panel
        rightPanel.remove(outputVideo);
        outputVideo = createVideoPanel(selected);
        rightPanel.add(outputVideo);
        // refresh
        rightPanel.revalidate();
        rightPanel.repaint();
    }
	
	public mainFrame(String[] args)	{
		super("CSCI570");
		int windowWidth = 800;
		int windowHeight = 800;
		leftPanel = createInputPanel(args[0], args[1]);
		rightPanel = createOutputPanel();
		add(leftPanel);
		add(rightPanel);
		
		// Frame setting
		setSize(windowWidth, windowHeight);
		setLayout(new GridLayout(1, 2));	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	// get power spectrum array
	public static double[] getPowerSpectrum(String src) throws Exception {
		WavSoundInput inputSound = new WavSoundInput(src);
		return SoundMath.powerLog(new FFT().apply(SoundMath.multiply(SoundMath.hamming, inputSound.chunk())));
	}
	
	public static double measureSimilarity(double[] lhs, double[] rhs) {
		if(lhs.length != rhs.length) return 0.0;
		double sum = 0;
		for(int i = 0; i < lhs.length; ++i) {
			sum += Math.abs(lhs[i] - rhs[i]);
		}
		return sum / lhs.length;
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.out.print("Arguments Error");
		}
		else {
			for(String s : database) {
				System.out.println(measureSimilarity(getPowerSpectrum(args[1]), getPowerSpectrum(s + ".wav")));
			}
			new mainFrame(args);
		}
	}
}
