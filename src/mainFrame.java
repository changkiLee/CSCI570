import image.ImagePreprocess;
import image.Player;
import sound.FFT;
import sound.SoundMath;
import sound.WavSoundInput;
import tensorFlow.ObjectsList;
import tensorFlow.ObjectsMatching;
import tensorFlow.ObjectsMatchingScore;
import tensorFlow.SoundSpectrumList;
import tensorFlow.TFMultithreadController;
import tensorFlow.TFNNController;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class mainFrame extends JFrame implements ActionListener {
	static final String[] labels = { "flowers", "interview", "movie", "musicvideo", "sports", "starcraft", "traffic" };
	static final String QUERY_DIR = "resource/query/";
	static final String VIDEO_DIR = "resource/databse_videos/";
	static final float FONT_SIZE = 32.0f;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel inputVideo;
	private JPanel outputVideo;
	private Container contentPane;
	private DrawingPanel graph;
	private static List<ObjectsMatchingScore> similarityList = new ArrayList<>();
	private static List<SoundSpectrumList> spectrumList = new ArrayList<>();
	
	public JPanel createInputPanel(String src) {
		JPanel inputPanel = new JPanel();
		// text
		JLabel inputText = new JLabel("Query: " + src + ".rgb");
		inputText.setFont(inputText.getFont().deriveFont(FONT_SIZE));
		inputPanel.add(inputText);
		
		// video
		inputVideo = createVideoPanel(src, QUERY_DIR);
		inputPanel.add(inputVideo);		
		
		return inputPanel;
	}
	
	public JPanel createOutputPanel() {
		JPanel outputPanel = new JPanel();
		// text
		JLabel outputText = new JLabel("Matched Videos: ");		
		outputText.setFont(outputText.getFont().deriveFont(FONT_SIZE));
		outputPanel.add(outputText);
		
		// video
		outputVideo = createVideoPanel(similarityList.get(0).name, VIDEO_DIR);
		outputPanel.add(outputVideo);
		
		return outputPanel;
	}
	
	// create output video panel
	public JPanel createVideoPanel(String src, String dir) { 
		Player video = new Player(src, dir);
		return video.getPanel();
	}
	
	// radio button selection
	public void actionPerformed(ActionEvent e) {
        String selected = e.getActionCommand();
        // remove panel
        rightPanel.remove(outputVideo);
        contentPane.remove(graph);
        // add panel
		outputVideo = createVideoPanel(selected, VIDEO_DIR);
        rightPanel.add(outputVideo);
        graph = new DrawingPanel(selected);
        contentPane.add(graph);
        // refresh
        rightPanel.revalidate();
        rightPanel.repaint();
        contentPane.revalidate();
        contentPane.repaint();
    }
	
	public mainFrame(String arg)	{
		super("CSCI570");
		int windowWidth = 2300;
		int windowHeight = 600;
		leftPanel = createInputPanel(arg);
		rightPanel = createOutputPanel();
		add(leftPanel);
		add(rightPanel);
		
		// create radio buttons
		JPanel radioBtnPanel = new JPanel(new GridLayout(similarityList.size(), 1));
		ButtonGroup btnGroup = new ButtonGroup();
		boolean status = true;
		for(int i = 0; i < similarityList.size(); i++)
		{			
			JRadioButton btn = new JRadioButton(similarityList.get(i).name, status);
			btn.setFont(btn.getFont().deriveFont(FONT_SIZE));
			JLabel similarity = new JLabel("(" + (similarityList.get(i).bestScore*100) + "%)");
			similarity.setFont(similarity.getFont().deriveFont(FONT_SIZE));
			btn.addActionListener(this);
			btnGroup.add(btn);
			radioBtnPanel.add(btn);
			radioBtnPanel.add(similarity);
			status = false;
		}
		add(radioBtnPanel);
			
		contentPane = getContentPane();
		graph = new DrawingPanel(similarityList.get(0).name);
		contentPane.add(graph);
		
		// Frame setting
		setSize(windowWidth, windowHeight);
		setLayout(new GridLayout(1, 3));	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void initSimilarity(String arg, int samplerate) throws IOException {
		HashSet<String> labelSet = new HashSet<>();

		List<ObjectsMatchingScore> scores = new ArrayList<>();
		List<ObjectsList> labelsObjects = new ArrayList<>();
		for (String label : labels) {
			labelSet.add(label);
			labelsObjects.add(new ObjectsList(label, ObjectsMatching.loadObjectsList(label)));
		}
		String query = arg;
		List<Integer> queryObjects = TFMultithreadController.TFNNrecogizitionWithMultithreads(4,
				ImagePreprocess.ImagesToBytesWithSampling(ImagePreprocess.getQueryVideoImages(query), samplerate));

		for (ObjectsList labelObjects : labelsObjects) {
			HashSet<Integer> objectSet = ObjectsMatching.objectListToSet(labelObjects.objects);
			int num = 0;
			for (int objectInQuery : queryObjects) {
				if (objectSet.contains(objectInQuery)) {
					num++;
				}
			}
			if (num == 0) {
				scores.add(new ObjectsMatchingScore(labelObjects.label, 0, null));
				labelSet.remove(labelObjects.label);
			}
		}
		Iterator<String> it = labelSet.iterator();
		while (it.hasNext()) {
			String baseLabel = it.next();
			List<Integer> baseObjects = null;
			for (ObjectsList labelObjects : labelsObjects) {
				if (labelObjects.label.equals(baseLabel)) {
					baseObjects = labelObjects.objects;
				}
			}
			
			List<Float> score = ObjectsMatching.getMatchingScore(ObjectsMatching.getMatchingDistance(queryObjects, baseObjects, samplerate),
					queryObjects.size());
			float best = ObjectsMatching.getMaxScore(score);
			scores.add(new ObjectsMatchingScore(baseLabel,best,score));
		}
		ObjectsMatching.sortScores(scores);
		similarityList = scores;
	}
	
	// get power spectrum array
	public static double[] getPowerSpectrum(String src, String path) throws Exception {
		String soundPath = path + src + "/" + src + ".wav";
		WavSoundInput inputSound = new WavSoundInput(soundPath);
		return //SoundMath.dpcm(
				SoundMath.powerLog(
						new FFT().apply(
								SoundMath.multiply(SoundMath.hamming, inputSound.chunk())));
	}

	public static void initSoundSpectrum(String src) throws Exception {		
		SoundSpectrumList lhs = new SoundSpectrumList(src, getPowerSpectrum(src, QUERY_DIR));
		spectrumList.add(lhs);
		for(int i = 0; i < labels.length; i++) {
			SoundSpectrumList rhs = new SoundSpectrumList(labels[i], getPowerSpectrum(labels[i], VIDEO_DIR));
			spectrumList.add(rhs);
		}		
	}

	class DrawingPanel extends JPanel {
		private int dbIdx = 0;
		public DrawingPanel(String arg) {
			for(int i = 0; i < labels.length; i++) {
				if(labels[i] == arg) {
					dbIdx = i;
					break;
				}
			}
		}
		
		public void drawImageGraph(Graphics g, int x, int y) {
			int graphHeight = 200;
			int graphWidth = 550;
			int gap = 50;
			
			// draw graph
			g.setColor(Color.RED);
			Iterator<ObjectsMatchingScore> itS = similarityList.iterator();
			while(itS.hasNext()) {
				ObjectsMatchingScore score = itS.next();
				if(score.name.equals(labels[dbIdx])) {
					if(score.bestScore == 0.0f) {
						break;
					}
					int k = 0;
					for(float val : score.scores) {
						int currY = (int)(val * graphHeight);			
						g.fillRect(x + k++, y + graphHeight + gap - currY, 1, currY);
					}
				}
			}

			// draw x-y axis
			g.setColor(Color.BLACK);
			g.drawString("Image Match", x, y + gap/2);
			g.drawLine(x, y + graphHeight + gap, x + graphWidth, y + graphHeight + gap); // x-axis
			for(int i = 0; i <= graphWidth; i+= 100) {
				g.drawString(Integer.toString(i), x + i, y + graphHeight + gap + 10);		
			}
			g.drawLine(x, y + gap, x, y + graphHeight + gap); // y-axis
			for(int j = graphHeight; j > 0; j-= (int)(graphHeight/10)) {
				g.drawString(Integer.toString(j*100/graphHeight) + "%", x, y + graphHeight + gap - j);
			}
		}

		public void drawSoundGraph(Graphics g, int x, int y) {
			int graphHeight = 200;
			int graphWidth = WavSoundInput.CHUNKSIZE / 2 - 1;
			int gap = 50;
			
			// draw graph
			g.setColor(Color.RED);
			Iterator<SoundSpectrumList> itS = spectrumList.iterator();
			while(itS.hasNext()) {
				SoundSpectrumList score = itS.next();
				if(score.name.equals(labels[dbIdx])) {
					int k = 0;
					for(double val : score.scores) {
						int currY = (int)(val / 3);
						g.drawLine(x + k, y + graphHeight + gap - currY, x + k + 1, y + graphHeight + gap - currY);
						k++;
					}
				}
			}

			g.setColor(Color.BLUE);
			int k = 0;
			for(double val : spectrumList.get(0).scores) {
				int currY = (int)(val / 3);
				g.drawLine(x + k, y + graphHeight + gap - currY, x + k + 1, y + graphHeight + gap - currY);
				k++;
			}
			// draw x-y axis
			g.setColor(Color.black);
			g.drawString("Sound Power Spectrum", x, y + gap - 20);
			g.drawLine(x, y + graphHeight + gap, x + graphWidth, y + graphHeight + gap); // x-axis
			for(int i = 0; i <= graphWidth; i+= 100) {
				g.drawString(Integer.toString(i), x + i, y + graphHeight + gap + 10);		
			}
			g.drawLine(x, y + gap, x, y + graphHeight + gap); // y-axis
			for(int j = graphHeight; j > 0; j-= (int)(graphHeight/5)) {
				g.drawString(Integer.toString(j*3), x, y + graphHeight + gap - j);
			}
		}
		
		// create similarity graph
		public void paint(Graphics g) {
			drawImageGraph(g, 0, 0);
			drawSoundGraph(g, 0, 250);
		}
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.out.print("Arguments Error");
		}
		else {
			initSimilarity(args[0], 3);
			initSoundSpectrum(args[0]);
			new mainFrame(args[0]);
		}
	}
}
