package knn.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public interface Utils {
	public static void loadDataset(String filename, String delim, double split, List<Sample> trainingSet,
			List<Sample> testSet) {

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = null;
			Random rand = new Random(123);

			while ((line = br.readLine()) != null) {
				String[] s = line.replaceAll("\"", "").split(delim);

				if (s.length < 3) {
					continue;
				}

				try {
					Sample sample = new Sample();
					sample.data = new double[] { Double.parseDouble(s[0]),  
							s[2].equalsIgnoreCase("married") ? 1 : 0, s[2].equalsIgnoreCase("married") ? 1 : 0,  
							s[2].equalsIgnoreCase("single") ? 1 : 0, 
							s[2].equalsIgnoreCase("divorced") ? 1 : 0,  
 							s[3].equalsIgnoreCase("primary") ? 1 : 0, s[3].equalsIgnoreCase("secondary") ? 1 : 0,
							s[3].equalsIgnoreCase("tertiary") ? 1 : 0, s[3].equalsIgnoreCase("unknown") ? 1 : 0,
							Double.parseDouble(s[5]),  
							s[6].equalsIgnoreCase("yes") ? 1 : 0,  
							Double.parseDouble(s[11]), 
							Double.parseDouble(s[13]),    
							s[15].equalsIgnoreCase("failure") ? 1 : 0, s[15].equalsIgnoreCase("other") ? 1 : 0,
							s[15].equalsIgnoreCase("success") ? 1 : 0, s[15].equalsIgnoreCase("unknown") ? 1 : 0, 

					};
					sample.label = s[s.length - 1].trim();
					if (rand.nextDouble() < split) {
						trainingSet.add(sample);
					} else {
						testSet.add(sample);
					}

				} catch (NumberFormatException e) {
					continue;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void scale(List<Sample> dataSet) {
		int m = dataSet.size();
		int n = dataSet.get(0).data.length;
		double[] mean = new double[n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				mean[i] += dataSet.get(j).data[i];
			}
		}
		for (int i = 0; i < n; i++) {
			mean[i] /= m;  
		}

		double[] std = new double[n];
		for (Sample e : dataSet) {
			for (int i = 0; i < n; i++) {
				std[i] += Math.pow(e.data[i] - mean[i], 2); 
			}
		}
		for (int i = 0; i < n; i++) {
			std[i] = Math.sqrt((std[i]) / (m - 1));  
		}

		for (Sample e : dataSet) {
			for (int i = 0; i < n; i++) {
				e.data[i] = (e.data[i] - mean[i]) / std[i];
			}
		}
	}

	public static float getAccuracy(List<Sample> testSet, String[] predictions) {
		int n = testSet.size();
		int correct = 0;
		for (int i = 0; i < n; i++) {
			if (testSet.get(i).label.equals(predictions[i])) {
				correct++;
			}
		}
		return (float) correct / n;
	}
}
