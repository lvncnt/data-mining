package com.fgl.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fgl.utility.Cluster;
import com.fgl.utility.Point;

public class Kmeans {

	private int k; // clusterNumber
	private List<Point> points;
	private List<Cluster> clusters;
	private List<Double> sse;

	public Kmeans(int k, List<Point> points) {
		this.k = k;
		this.points = points;
	}

	/**
	 * Steps 1: Initialize initial clusters' centroids
	 **/
	public void init() {
		this.clusters = new ArrayList<>();
		this.sse = new ArrayList<>();

		Point[] centroids = getInitCentroid();

		// create k cluster, set their centroids
		for (int i = 0; i < k; i++) {
			Cluster cluster = new Cluster(i + 1);
			cluster.setCentroid(centroids[i]);
			clusters.add(cluster);
		}
	}

	private Point[] getInitCentroidRandom() {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
		for (Point p : points) {
			minX = Math.min(minX, p.getX());
			minY = Math.min(minY, p.getY());
			maxX = Math.max(maxX, p.getX());
			maxY = Math.max(maxY, p.getY());
		}
		Point[] c = new Point[k];
		for (int i = 0; i < k; i++) {
			c[i] = createRandomPoint(minX, maxX, minY, maxY);
		}
		return c;
	}

	// Creates random point
	private Point createRandomPoint(double minX, double maxX, double minY,
			double maxY) {
		Random r = new Random();
		double x = minX + (maxX - minX) * r.nextDouble();
		double y = minY + (maxY - minY) * r.nextDouble();
		return new Point(x, y);
	}

	/**
	 * Binary search-based initialization method proposed by Kumar and Sahoo,
	 * 2014
	 */
	private Point[] getInitCentroid() {
		Point[] mostDistant = mostDistantPair();
		Point min = mostDistant[0], max = mostDistant[1];
		Point mid = new Point((max.getX() - min.getX()) / k,
				(max.getY() - min.getY()) / k);

		Point[] c = new Point[k];
		for (int i = 0; i < k; i++) {
			c[i] = new Point(min.getX() + i * mid.getX(), min.getY() + i
					* mid.getY());
		}
		return c;
	}

	/**
	 * Find the two most distant points
	 */
	private Point[] mostDistantPair() {
		double x = 0, y = 0;
		for (Point p : points) {
			x += p.getX();
			y += p.getY();
		}
		x /= points.size();
		y /= points.size();

		Point pointAvg = new Point(x, y);
		Point temp = null;
		double max = 0;
		for (Point p : points) {
			double dist = p.euclideanDistance(pointAvg);
			if (dist > max) {
				temp = p;
				max = dist;
			}
		}

		Point c1 = new Point(temp.getX(), temp.getY());

		temp = null;
		max = 0;
		for (Point p : points) {
			double dist = p.euclideanDistance(c1);
			if (dist > max) {
				temp = p;
				max = dist;
			}
		}
		Point c2 = new Point(temp.getX(), temp.getY());
		if (c1.getX() > c2.getX()) {
			return new Point[] { c2, c1 };
		} else {
			return new Point[] { c1, c2 };
		}
	}

	public void calculate() {

		boolean changes = false;
		int iteration = 0;
		while (!changes) {
			ArrayList<Point> oldCentroids = getCentroids();

			// clear points in each cluster
			for (Cluster c : clusters) {
				c.getPoints().clear();
			}

			// Step 2: Assign each object to the group that has the closest
			// centroid
			assignCluster();

			// Step 3: recalculate the positions of the K centroids
			recalculateCentroids();

			// Check if the centroids move
			double distance = 0;
			ArrayList<Point> currentCentroids = getCentroids();
			for (int i = 0; i < k; i++) {
				distance += currentCentroids.get(i).euclideanDistance(
						oldCentroids.get(i));
			}
			sse.add(calculateSSE());

			if (distance == 0) {
				changes = true;
			}

			iteration++;

		}

	}

	private void assignCluster() {

		for (Point p : points) {
			double min = Double.MAX_VALUE;
			int id = 1;
			for (Cluster c : clusters) {
				double dist = p.euclideanDistance(c.getCentroid());
				if (dist < min) {
					min = dist;
					id = c.getId();
				}
			}
			p.setCluster(id);
			clusters.get(id - 1).addPoint(p);
		}

	}

	private void recalculateCentroids() {
		for (Cluster c : clusters) {
			int n = c.getPoints().size();
			if (n == 0) {
				continue;
			}

			double sumX = 0, sumY = 0;
			for (Point p : c.getPoints()) {
				sumX += p.getX();
				sumY += p.getY();
			}

			Point centroid = c.getCentroid();
			centroid.setX(sumX / n);
			centroid.setY(sumY / n);
		}

	}

	private ArrayList<Point> getCentroids() {
		ArrayList<Point> res = new ArrayList<>();
		for (Cluster cluster : clusters) {
			Point cent = new Point(cluster.getCentroid().getX(), cluster
					.getCentroid().getY());
			res.add(cent);
		}
		return res;
	}

	private double calculateSSE() {
		double sse = 0;
		for (Cluster cluster : clusters) {
			Point cent = cluster.getCentroid();
			for (Point p : cluster.getPoints()) {
				sse += Math.pow(p.getX() - cent.getX(), 2);
				sse += Math.pow(p.getY() - cent.getY(), 2);
			}
		}
		return sse;
	}

	private void showCluster() {
		for (Cluster c : clusters) {
			System.out.println(c);
		}
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	public List<Double> getSSE() {
		return sse;
	}

}
