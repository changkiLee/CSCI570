package tensorFlow; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TFMultiThread implements Callable<List<Integer>> { 
	private List<byte[]> images;

	TFMultiThread(List<byte[]> images) { 
		this.images = images;  
	}

	public List<Integer> call() {
		List<Integer> result = new ArrayList<Integer>();
		try {
			result = TFNNController.TFNNrecogizition(images);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
 
}
