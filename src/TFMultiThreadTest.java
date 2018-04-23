import java.io.IOException;

public class TFMultiThreadTest { 
	static String[] labels = { "interview"};
//, "traffic", "flowers", "starcraft", "interview" 
	public static void main(String[] args) throws IOException {
		for (String label : labels) {
			TFNNController.outputToFile(TFMultithreadController
					.TFNNrecogizitionWithMultithreads(4,ImagePreprocess.ImagesToBytes(ImagePreprocess.getVideoImages(label))), label);

		}
	}
}
