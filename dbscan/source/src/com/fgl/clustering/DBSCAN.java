package com.fgl.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.fgl.kdtree.KDTree;
import com.fgl.utility.Cluster;
import com.fgl.utility.Point;

public class DBSCAN {

	private List<Point> points;
	private List<Cluster> clusters;

	private double epsilon;
	private int minPoints;
	private int size;

	private boolean[] isVisited;
	private int[] status;

	private static final int NOISE = -1;

	private KDTree kdTree;

	public DBSCAN(List<Point> points, double epsilon, int minPoints) {
		this.points = points;
		this.epsilon = epsilon;
		this.minPoints = minPoints;

		this.size = points.size();
		isVisited = new boolean[size];
		status = new int[size];

		this.clusters = new ArrayList<>();
	}

	public void buildKDTree() {
		kdTree = new KDTree();
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			kdTree.insert(i, new double[] { p.getX(), p.getY() });
		}
	}

	public void dbscan() {
		int clusterID = 0;
		for (int i = 0; i < points.size(); i++) {

			if (isVisited[i]) {
				continue;
			}

			isVisited[i] = true;
			Point point = points.get(i);
			Queue<Integer> neighbor = getEpsilonNeighborKDTree(point);

			if (neighbor.size() < minPoints) {
				status[i] = NOISE;
			} else {
				clusterID++;
				Cluster cluster = new Cluster(clusterID);
				status[i] = clusterID;
				expandCluster(point, neighbor, cluster);
				clusters.add(cluster);
			}

		}

		// add noise (-1)
		Cluster cluster = new Cluster(0);
		for (int i = 0; i < status.length; i++) {
			if (status[i] == -1) {
				cluster.addPoint(points.get(i));
			}
		}
		if (cluster.getPoints().size() > 0) {
			clusters.add(cluster);
		}
	}

	private void expandCluster(Point point, Queue<Integer> neighbor,
			Cluster cluster) {

		cluster.addPoint(point);

		while (!neighbor.isEmpty()) {
			int index = neighbor.poll();
			Point q = points.get(index);

			if (!isVisited[index]) {
				isVisited[index] = true;
				Queue<Integer> neighborQ = getEpsilonNeighborKDTree(q);
				if (neighborQ.size() >= minPoints) {
					neighbor.addAll(neighborQ);
				}
			}

			if (status[index] <= 0) {
				cluster.addPoint(q);
				status[index] = cluster.getId();
			}
		}
	}

	// brute force
	private Queue<Integer> getEpsilonNeighbor(Point point) {
		Queue<Integer> neighbor = new LinkedList<>();
		for (int i = 0; i < points.size(); i++) {
			Point q = points.get(i);
			if (point.euclideanDistance(q) <= epsilon) {
				neighbor.offer(i);
			}
		}
		return neighbor;
	}

	// 2d tree
	private Queue<Integer> getEpsilonNeighborKDTree(Point point) {
		Queue<Integer> neighbor = new LinkedList<>(kdTree.rangeRadius(
				new double[] { point.getX(), point.getY() }, epsilon));
		return neighbor;
	}

	public double[] kNNdist() {
		double[] avg = new double[size];
		for (int i = 0; i < size; i++) {
			Point p = points.get(i);
			double temp = 0;
			Queue<Double> queue = kdTree.kNearest(
					new double[] { p.getX(), p.getY() }, minPoints);

			while (!queue.isEmpty()) {
				temp += queue.poll();
			}
			avg[i] = temp / minPoints;
		}
		Arrays.sort(avg);
		return avg;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

}
