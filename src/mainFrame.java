import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

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
	
	public JPanel createInput(String imgSrc, String soundSrc)
	{
		JPanel inputPanel = new JPanel();
		JLabel inputText = new JLabel("Query: " + imgSrc);	
		inputPanel.add(inputText);
		
		inputVideo = createVideoPanel(imgSrc, soundSrc);
		inputPanel.add(inputVideo);
		
		return inputPanel;
	}
	
	public JPanel createOutput()
	{
		JPanel outputPanel = new JPanel();
		JLabel outputText = new JLabel("Matched Videos: ");		
		outputPanel.add(outputText);
				
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
		
		outputVideo = createVideoPanel(database[0]);
		outputPanel.add(outputVideo);
		return outputPanel;
	}
	
	public JPanel createVideoPanel(String src)
	{
		String imgSrc = src + ".rgb";
		String soundSrc = src + ".wav";
		Player video = new Player(imgSrc, soundSrc);
		return video.getPanel();
	}
	
	public JPanel createVideoPanel(String imgSrc, String soundSrc)
	{
		Player video = new Player(imgSrc, soundSrc);
		return video.getPanel();
	}
	
	public void actionPerformed(ActionEvent e) {
        String selected = e.getActionCommand();
        rightPanel.remove(outputVideo);
        outputVideo = createVideoPanel(selected);
        rightPanel.add(outputVideo);
        rightPanel.revalidate();
        rightPanel.repaint();
    }
	
	public mainFrame(String[] args)
	{
		super("CSCI570");
		int windowWidth = 800;
		int windowHeight = 800;
		leftPanel = createInput(args[0], args[1]);
		rightPanel = createOutput();
		add(leftPanel);
		add(rightPanel);
		
		// Frame setting
		setSize(windowWidth, windowHeight);
		setLayout(new GridLayout(1, 2));	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		if(args.length != 2)
			System.out.print("Arguments Error");
		else
			new mainFrame(args);
	}
}
