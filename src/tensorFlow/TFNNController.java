package tensorFlow;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TFNNController {

	public static List<Integer> TFNNrecogizition(List<byte[]> images) throws IOException {
		//long startTime = System.nanoTime();
		List<Integer> objectListOfVideo = new ArrayList<>();
		TensorFlowNeuralNetwork tfinstance = new TensorFlowNeuralNetwork();

		objectListOfVideo = tfinstance.objectRecognition(images);
		// time measure
		//long endTime = System.nanoTime();
		//System.out.println(endTime - startTime);
		return objectListOfVideo;

	}

	public static void outputToFile(List<Integer> objectListOfVideo, String label) throws IOException {
		FileWriter writer = new FileWriter(label + ".txt");
		for (int str : objectListOfVideo) {
			writer.write(str + "\n");
		}
		writer.close();
	}
}
