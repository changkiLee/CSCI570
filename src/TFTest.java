import java.io.IOException;

/*
 * TODO:1. cut down the frames 
		2. multithreading 
		3. text comparing algorithm 
 * 
 * */
public class TFTest {
	static String[] labels = { "movie", "sports", "musicvideo" };

	public static void main(String[] args) throws IOException {
		for (String label : labels) {
			TFNNController.outputToFile(TFNNController
					.TFNNrecogizition(
							ImagePreprocess.ImagesToBytes(ImagePreprocess.
									getVideoImages(label)))
					, label);

		}
	}
}
