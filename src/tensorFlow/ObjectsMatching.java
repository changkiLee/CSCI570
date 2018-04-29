package tensorFlow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet; 
import java.util.List;
import java.util.Set;

public class ObjectsMatching {
	
	public static void sortScores(List <ObjectsMatchingScore> scores){
		scores.sort(ObjectsMatchingScore.OMScomparator);
	}	
	
	public static float getMaxScore(List<Float> scores){
		float best = 0.0f;
		for(float score : scores){
			if(score>best){
				best = score;
			}
		}
		return best;
	}

	public static List<Integer> loadObjectsList(String label) {
		List<Integer> objectsList = new ArrayList<>();
		try {
			File file = new File(label + ".txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				objectsList.add(Integer.valueOf(line));
			}
			fr.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return objectsList;
	}

	public static HashSet<Integer> objectListToSet(List<Integer> objectsList) {
		HashSet<Integer> objectsSet = new HashSet<>(objectsList);
		return objectsSet;

	}

	public static boolean queryInDataSets(List<Integer> queryObjectsList, Set<Integer> dataSet) {
		for (int i : queryObjectsList) {
			if (dataSet.contains(i)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<Integer> getMatchingDistance(List<Integer> query, List<Integer> database,int stride){
		int querySize = query.size();
		int databaseSize = database.size();
		List<Integer> results = new ArrayList<>();
		for(int index = 0; index < (databaseSize - querySize*stride); index++){
			results.add(getMatchingDistance(query,database,index,stride));
		}
		return results;
	}

	public static List<Float> getMatchingScore(List<Integer> distance, int querySize ){
		List<Float> results = new ArrayList<>();
		for(int d : distance){
			results.add((querySize - d)/(float)querySize);
		}
		return results; 
	}
	
	public static int getMatchingDistance(List<Integer> query, List<Integer> database, int startIndex,int stride) {
		int querySize = query.size(); 
		if(database.size()<startIndex+querySize*stride){
			System.out.println("something funny");
			return 0;
		}
		int[] v0 = new int[querySize+1];
		int[] v1 = new int[querySize+1];
		for(int i = 0; i < v0.length; i++){
			v0[i] = i;
		}
		for(int i = 0; i < v0.length; i++){
			v1[0] = i+1;
			for(int j = 0; j < querySize; j ++ ){
				
				int deletionCost = v0[j+1] +1;
				int insertionCost = v1[j] +1;
				int substitutionCost;
				if (query.get(j).equals(database.get(startIndex+i*stride))){
					substitutionCost = v0[j];
				}
				else{
					substitutionCost = v0[j] + 1;
				}
				v1[j+1] = Math.min(substitutionCost,Math.min(deletionCost, insertionCost));
			}
			v0 = v1.clone();
		} 
		return v0[querySize];

	} 
}