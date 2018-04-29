package tensorFlow;

public class SoundSpectrumList {
	public String name;
	public double[] scores;
	public SoundSpectrumList(String name, double[] scores){
		this.name = name;
		this.scores = new double[scores.length];
		this.scores = scores;
	}
}
