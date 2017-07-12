package knn.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import knn.common.Sample;
import knn.common.Utils;
 
public class KnnConcurrent {

	private static final int NB_THREAD = 5;

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

		ExecutorService executor = Executors.newFixedThreadPool(NB_THREAD);
		int n = testSet.size();
		int p = n / NB_THREAD;
		int start = 0, end = p;
		List<Future<List<String>>> list = new ArrayList<>();
		for (int i = 0; i < NB_THREAD; i++) {
			Callable<List<String>> task = new WorkerClassify(trainingSet, testSet, start, end, k);
			start = end;
			end = (i < NB_THREAD - 2) ? end + p : n;
			Future<List<String>> submit = executor.submit(task);
			list.add(submit);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		int i = 0;
		for (Future<List<String>> future : list) {
			try {
				List<String> l = future.get();
				for (String s : l) {
					predictions[i++] = s;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		long timeAfter = System.currentTimeMillis();
		float totalProcessingTime = new Float((float) (timeAfter - timeBefore) / (float) 1000);
		System.out.println("Tests completed in " + totalProcessingTime + " seconds");

 		float correct = Utils.getAccuracy(testSet, predictions);
		System.out.println("Accuracy: " + correct * 100);
	}

}
