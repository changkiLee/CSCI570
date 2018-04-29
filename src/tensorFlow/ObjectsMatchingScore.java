package tensorFlow;
import java.util.Comparator;
import java.util.List;

public class ObjectsMatchingScore implements Comparable<Object> {
	public String name;
	public float bestScore;
	public List<Float> scores;
	public ObjectsMatchingScore(String name, float bestScore, List<Float> scores){
		this.name = name ;
		this.bestScore = bestScore;
		this.scores = scores;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if(((ObjectsMatchingScore)o).bestScore > this.bestScore)
			return 1;
		return -1;
	}
	public static Comparator<ObjectsMatchingScore> OMScomparator = new Comparator<ObjectsMatchingScore>(){
		public int compare(ObjectsMatchingScore o1, ObjectsMatchingScore o2) {
			// TODO Auto-generated method stub
			 
			return o1.compareTo(o2);
		} 
	};
}
