import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TFMultithreadController {
	private static List<byte[]> splitInputForThreads(List<byte[]> inputs, int threadsNum, int id) {
		List<byte[]> images = new ArrayList<>();
		int index = 0;
		for (byte[] input : inputs) {
			// System.out.println(file.getName());
			if (index % threadsNum == id)
				images.add(input);
			index++;
			// Process file
		}
		return images;
	}

	private static List<Integer> compositeInput(List<List<Integer>> inputs) {
		List<Integer> objects = new ArrayList<>();
		int sizeList = inputs.size();
		while (true) {
			int size = objects.size();
			for (int i = 0; i < sizeList; i++) {
				if (!inputs.get(i).isEmpty())
					objects.add(inputs.get(i).remove(0));
			}
			if (size == objects.size()) {
				break;
			}
		}
		return objects;
	}

	public static List<Integer> TFNNrecogizitionWithMultithreads(int numThreads, List<byte[]> inputs) {
		ExecutorService exec = Executors.newCachedThreadPool();
		ArrayList<Future<List<Integer>>> results = new ArrayList<Future<List<Integer>>>();
		for (int i = 0; i < numThreads; i++) {
			results.add(exec.submit(new TFMultiThread(splitInputForThreads(inputs, numThreads, i))));
		}
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		for (Future<List<Integer>> fs : results) {
			try {
				lists.add(fs.get());
			} catch (InterruptedException e) {
				System.out.println(e);
				return null;
			} catch (ExecutionException e) {
				System.out.println(e);
				return null;
			}
		}
		return compositeInput(lists);
	}

}
