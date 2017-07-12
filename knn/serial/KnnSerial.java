package knn.serial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import knn.common.Distance;
import knn.common.Sample;
import knn.common.Utils;

public class KnnSerial {

	public static void main(String[] args) {
		String filename = "bank-full.csv";
		String delim = ";";
		double split = 0.7;
		List<Sample> trainingSet = new ArrayList<>();
		List<Sample> testSet = new ArrayList<>();
		Utils.loadDataset(filename, delim, split, trainingSet, testSet);

		Utils.scale(trainingSet);
		Utils.scale(testSet);
		System.out.println(trainingSet.size() + ":" + testSet.size());

		int k = 15;
		String[] predictions = new String[testSet.size()];

		long timeBefore = System.currentTimeMillis();

		for (int i = 0; i < testSet.size(); i++) {
			predictions[i] = classify(trainingSet, k, testSet.get(i));
			System.out.println(i + "-Actual:" + testSet.get(i).label + "; Prediction: " + predictions[i]);
		}

		long timeAfter = System.currentTimeMillis();
		float totalProcessingTime = new Float((float) (timeAfter - timeBefore) / (float) 1000);
		System.out.println("Tests completed in " + totalProcessingTime + " seconds");

		float correct = Utils.getAccuracy(testSet, predictions);
		System.out.println("Accuracy: " + correct * 100);
	}


	static String classify(List<Sample> trainingSet, int k, Sample testInstance) {
		Distance[] distances = new Distance[trainingSet.size()];
		for (int i = 0; i < trainingSet.size(); i++) {
			Sample e = trainingSet.get(i);
			distances[i] = new Distance();
			distances[i].index = i;
			distances[i].distance = testInstance.euclideanDistance(e);
		}

		Arrays.sort(distances, (o1, o2) -> { 
			return Double.compare(o1.distance, o2.distance);
		});

		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < k; i++) {
			int index = distances[i].index;
			Sample e = trainingSet.get(index);
			String label = e.label;
			map.merge(label, 1, (a, b) -> a + b);
		}
		return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
	}

}
