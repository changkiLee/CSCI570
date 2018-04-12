package playWave;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * plays a wave file using PlaySound class
 * 
 * @author Giulio
 */
public class PlayWaveFile {

    /**
     * <Replace this with one clearly defined responsibility this method does.>
     * 
     * @param args
     *            the name of the wave file to play
     */
	PlaySound soundSrc;
	public PlayWaveFile(String args){
		String filename = args;
	
		// opens the inputStream
		FileInputStream inputStream;
		try {
		    inputStream = new FileInputStream(filename);
		    //inputStream = this.getClass().getResourceAsStream(filename);
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return;
		}
	
		// initializes the playSound Object
		soundSrc = new PlaySound(inputStream);		
    }
	
	public void play(){
		// plays the sound
		try {
			soundSrc.play();
		} catch (PlayWaveException e) {
			e.printStackTrace();
		}
	}
	
	public void pause(){
		/*
		try {
			soundSrc.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
	}
}
