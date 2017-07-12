package knn.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import knn.common.Distance;
import knn.common.Sample;

public class WorkerClassify implements Callable<List<String>> {
	List<Sample> trainingSet;
	List<Sample> testSet;
	Sample testInstance;
	int start, end, k;

	public WorkerClassify(List<Sample> trainingSet, List<Sample> testSet, int start, int end,
			int k) {
		this.trainingSet = trainingSet;
		this.testSet = testSet;
		this.start = start;
		this.end = end;
		this.k = k;
	}

	@Override
	public List<String> call() throws Exception {
		List<String> predictions = new ArrayList<>();
		for (int i = start; i < end; i++) {
			Sample testInstance = testSet.get(i);
			String p = classify(trainingSet, k, testInstance, i);
			predictions.add(p);
			System.out.println(i + "-Actual:" + testInstance.label + "; Prediction: " + p);

		}
		return predictions;
	}

	String classify(List<Sample> trainingSet, int k, Sample testInstance, int deb) {
		Distance[] distances = new Distance[trainingSet.size()];
		for (int i = 0; i < trainingSet.size(); i++) {
			Sample e = trainingSet.get(i);
			distances[i] = new Distance();
			distances[i].index = i;
			distances[i].distance = testInstance.euclideanDistance(e);
		}

		Arrays.parallelSort(distances, (o1, o2) -> {
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
