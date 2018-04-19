package soundFFT;
 
import java.io.File;
import javax.sound.sampled.*;

public class WavSoundInput {
	// The sound format properties
	static final int channels = 2;
	static final int bits = 16;
	static final float sampleRate = 44100.0f;
	static final AudioFormat format = new AudioFormat(sampleRate, bits,	channels, true, false);
	private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	private AudioInputStream audioInputStream;
	private final String file;
	private final byte[] buffer = new byte[EXTERNAL_BUFFER_SIZE];
	// We will read chunks of 1024 samples.
	public static int CHUNKSIZE = 1024;
	private double[] chunk = new double[WavSoundInput.CHUNKSIZE];
	
	// The constructor accepts a file name to open
	public WavSoundInput(String file) throws Exception {
		this.file = file;
		open();
	}
 
	public void open() throws Exception {
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(file));
		audioInputStream = AudioSystem.getAudioInputStream(format, ais);
	}
	
	// Notice we use the @Apply annotation to guide Zaluum on which methods
	// are candidates to be used as box behaviour 
	public double[] chunk() {
		for (int i = 0; i < chunk.length; i++)
			chunk[i] = read();
		return chunk;
	}
	// We read the data a frame at a time in an endless loop.
	// The output data is the first channel scaled to 0 to 1.
	private double read() {
		try {
			int read = audioInputStream.read(buffer);
			if (read == -1) {
				audioInputStream.close();
				//open();
				return 0;
			} else {
				short sample = (short) ((((int) buffer[1] & 0xff) << 8)
						+ ((int) buffer[0] & 0xff));
				return (sample) / 32767.0;
			}
		} catch (Exception e) {
			return 0;
		}
	}
}