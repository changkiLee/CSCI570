import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import playRGB.Player;
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
	private Container contentPane;
	private DrawingPanel graph;
	private static double[][] soundDisSimilarityList = new double[database.length][];  
	private static double[] DisSimilarity = new double[database.length];
	
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
		for(int i = 0; i < database.length; i++)
		{			
			JRadioButton btn = new JRadioButton(database[i] + " : " + (DisSimilarity[i]*100) + "%", status);
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
        // remove panel
        rightPanel.remove(outputVideo);
        contentPane.remove(graph);
        // add panel
		outputVideo = createVideoPanel(selected);
        rightPanel.add(outputVideo);
        graph = new DrawingPanel(selected);
        contentPane.add(graph);
        // refresh
        rightPanel.revalidate();
        rightPanel.repaint();
        contentPane.revalidate();
        contentPane.repaint();
    }
	
	public mainFrame(String[] args)	{
		super("CSCI570");
		int windowWidth = 1600;
		int windowHeight = 1000;
		leftPanel = createInputPanel(args[0], args[1]);
		rightPanel = createOutputPanel();
		add(leftPanel);
		add(rightPanel);
			
		contentPane = getContentPane();
		graph = new DrawingPanel(database[0]);
		contentPane.add(graph);
		
		// Frame setting
		setSize(windowWidth, windowHeight);
		setLayout(new GridLayout(1, 3));	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	// get power spectrum array
	public static double[] getPowerSpectrum(String src) throws Exception {
		WavSoundInput inputSound = new WavSoundInput(src);
		return SoundMath.dpcm(
				SoundMath.powerLog(
						new FFT().apply(
								SoundMath.multiply(SoundMath.hamming, inputSound.chunk()))));
	}
	
	public static void initSoundDisSimilarity(String soundSrc) throws Exception {
		int spectrumLength = WavSoundInput.CHUNKSIZE / 2 - 1;
		// get spectrum of query
		double[] lhs = new double[spectrumLength];
		lhs = getPowerSpectrum(soundSrc);
		for(int i = 0; i < database.length; i++) {
			// get spectrum of database
			double[] rhs = new double[spectrumLength];
			rhs = getPowerSpectrum(database[i] + ".wav");

			// init sound soundDisSimilarity and similarity
			DisSimilarity[i] = 0;
			soundDisSimilarityList[i] = new double[spectrumLength];
			for(int j = 0; j < spectrumLength; j++) {
				soundDisSimilarityList[i][j] = Math.abs(lhs[j] - rhs[j]);
				DisSimilarity[i] += soundDisSimilarityList[i][j];
			}
			DisSimilarity[i] /= spectrumLength;
		}
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.out.print("Arguments Error");
		}
		else {
			// TODO:
			// combine similarity % of image and sound -> calculate(8:2) -> create list -> sorting
			initSoundDisSimilarity(args[1]);
			new mainFrame(args);
		}
	}
	
	class DrawingPanel extends JPanel {
		private int dbIdx = 0;
		public DrawingPanel(String arg) {
			for(int i = 0; i < database.length; i++) {
				if(database[i] == arg) {
					dbIdx = i;
					break;
				}
			}
		}

		// create similarity graph
		public void paint(Graphics g) {
			g.setColor(Color.RED);
			for(int i = 0; i < soundDisSimilarityList[dbIdx].length; i++) {
				int currY = (int)(soundDisSimilarityList[dbIdx][i] * 100);			
				g.fillRect(i, 700-currY, 1, currY);
			}
		}
	}
}
