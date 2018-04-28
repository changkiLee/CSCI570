import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List; 

public class ObjectiveMatchingTest {

	final static String[] labels = { "interview", "movie", "musicvideo", "sports", "starcraft", "traffic" };
	final static int samplerate = 3;

	public static void main(String[] agrv) throws IOException {
		HashSet<String> labelSet = new HashSet<>();

		List<ObjectsMatchingScore> scores = new ArrayList<>();
		List<ObjectsList> labelsObjects = new ArrayList<>();
		for (String label : labels) {
			labelSet.add(label);
			labelsObjects.add(new ObjectsList(label, ObjectsMatching.loadObjectsList(label)));
		}
		String query = "first";
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
			if(baseObjects == null){
				System.out.println("something funniest");
				return;
			}
			List<Float> score = ObjectsMatching.getMatchingScore(ObjectsMatching.getMatchingDistance(queryObjects, baseObjects, samplerate),
					queryObjects.size());
			float best = ObjectsMatching.getMaxScore(score);
			scores.add(new ObjectsMatchingScore(baseLabel,best,score));
		}
		
		Iterator<ObjectsMatchingScore> itS = scores.iterator();
		while(itS.hasNext()){
			ObjectsMatchingScore result = itS.next();
			System.out.println(result.bestScore); 
			System.out.println(result.name);			
		}

	}
}
