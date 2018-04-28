import java.util.List;

public class ObjectsMatchingScore {
	String name;
	float bestScore;
	List<Float> scores ;
	ObjectsMatchingScore(String name, float bestScore, List<Float> scores){
		this.name = name ;
		this.bestScore = bestScore;
		this.scores = scores;
	}
}
