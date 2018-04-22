package soundFFT;

import static java.lang.Math.PI;

public class SoundMath {
	public static double[] hamming = hammingWindow(WavSoundInput.CHUNKSIZE);
	public static double hammingsum = 0;
	public static double[] hammingWindow(int order) {
		double[] w = new double[order];
		for (int i = 0; i < order; i++) {
			w[i] = 0.54 - 0.46 * Math.cos((2.0 * PI * i) / (order - 1));
		}
		return w;
	}

	public static double[] multiply(double[] a, double[] b) {
		double[] res = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i] * b[i];
		}
		return res;
	}
	public static double[] multiply(double[] a, double b) {
		double[] res = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i] * b;
		}
		return res;
	}
	public static double[] sum(double[] a, double[] b) {
		double[] res = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i] + b[i];
		}
		return res;
	}	
	public static double[] powerLog(double[] input) {
		double[] result = new double[input.length/2];
		for (int i = 0; i < result.length; i++) {
			double abs = 2*Math.sqrt(input[i * 2] * input[i * 2] + input[i * 2 + 1] * input[i * 2 + 1]);
			double scaled = abs / result.length; // not counting the window.
			result[i] = -20 * Math.log(scaled);
		}
		return result;
	}
	
	public static double[] dpcm(double[] input) {
		double[] result = new double[input.length - 1];
		for (int i = 0; i < result.length; i++) {
			result[i] = input[i+1] - input[i];
		}
		return result;
	}
	
}
