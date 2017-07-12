package knn.common;

public class Sample {
	public String label;
	public double[] data;

	public Sample() {
	}

	public Sample(double[] data) {
		this.data = data;
	}

	public double euclideanDistance(Sample other) {
		double sum = 0;
		double[] data1 = data;
		double[] data2 = other.data;
		if (data1.length != data2.length) {
			throw new IllegalArgumentException("Vectors do not have same length");
		}
		for (int i = 0; i < data1.length; i++) {
			sum += Math.pow(data1[i] - data2[i], 2);
		}
		return Math.sqrt(sum);
	}
}
