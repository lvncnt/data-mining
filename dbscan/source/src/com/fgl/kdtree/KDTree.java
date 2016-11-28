package com.fgl.kdtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class KDTree {

	private static class Node {
		private Node left, right;

		private double[] point;
		private int id;

		public Node(int id, double[] point) {
			this.point = point;
			this.id = id;
		}
	}

	private final static int k = 2; // k dimensional point
	private Node root;
	private int size;

	public KDTree() {
		this.root = null;
		this.size = 0;
	}

	/**
	 * insert a new point into KD Tree
	 */
	public void insert(int id, double[] point) {
		this.root = insert(this.root, id, point, 0);
	}

	private Node insert(Node root, int id, double[] point, int depth) {
		if (root == null) {
			size++;
			return new Node(id, point);
		}

		// Calculate current dimension (cd) of comparison
		int cd = depth % k;

		if (point[cd] < root.point[cd]) {
			root.left = insert(root.left, id, point, depth + 1);
		} else {
			root.right = insert(root.right, id, point, depth + 1);
		}

		return root;

	}

	/**
	 * if the KD-tree contains point p
	 * 
	 * @param point
	 * @return
	 */
	public boolean contains(double[] point) {
		return search(this.root, point, 0);
	}

	private boolean search(Node root, double[] point, int depth) {
		if (root == null) {
			return false;
		}

		if (isEqual(root.point, point)) {
			return true;
		}

		int cd = depth % k;
		if (point[cd] < root.point[cd]) {
			return search(root.left, point, depth + 1);
		} else {
			return search(root.right, point, depth + 1);
		}

	}

	/**
	 * determine if two Points are same in k Dimensional space
	 */
	private boolean isEqual(double[] p, double[] q) {
		for (int i = 0; i < k; i++) {
			if (p[i] != q[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return all points that are inside the rectangle
	 */
	public List<Integer> range(double xmin, double ymin, double xmax,
			double ymax) {
		List<Integer> result = new ArrayList<>();
		RectHV queryRect = new RectHV(xmin, ymin, xmax, ymax);
		range(root, queryRect, result, 0);
		return result;
	}

	private void range(Node root, RectHV queryRect, List<Integer> result,
			int depth) {
		if (root == null) {
			return;
		}

		if (queryRect.contains(root.point)) {
			result.add(root.id);
		}

		int cd = depth % k;
		boolean isVertical = cd == 0;

		if (root.left != null
				&& ((isVertical && root.point[0] >= queryRect.getXmin()) || (!isVertical && root.point[1] >= queryRect
						.getYmin()))) {
			range(root.left, queryRect, result, depth + 1);
		}

		if (root.right != null
				&& ((isVertical && root.point[0] <= queryRect.getXmax()) || (!isVertical && root.point[1] <= queryRect
						.getYmax()))) {
			range(root.right, queryRect, result, depth + 1);
		}

	}

	/**
	 * Return all points that are within a fixed radius epsilon from a query
	 * point
	 */
	public List<Integer> rangeRadius(double[] query, double epsilon) {
		List<Integer> result = new ArrayList<>();
		rangeRadius(root, query, epsilon, result, 0);
		return result;
	}

	private void rangeRadius(Node root, double[] query, double epsilon,
			List<Integer> result, int depth) {
		if (root == null) {
			return;
		}

		if (distance(query, root.point) <= epsilon) {
			result.add(root.id);
		}

		int cd = depth % k;

		if (root.left != null && query[cd] - epsilon <= root.point[cd]) {
			rangeRadius(root.left, query, epsilon, result, depth + 1);
		}

		if (root.right != null && query[cd] + epsilon >= root.point[cd]) {
			rangeRadius(root.right, query, epsilon, result, depth + 1);
		}

	}

	/**
	 * Return a nearest neighbor from a query point
	 */
	public double[] nearest(double[] query) {
		double[] result = new double[k];
		nearest(root, query, Double.MAX_VALUE, result, 0);
		return result;
	}

	private void nearest(Node root, double[] query, double minDist,
			double[] result, int depth) {
		if (root == null) {
			return;
		}

		double dist = distance(query, root.point);
		if (dist == 0) {
			return;
		}

		if (dist < minDist) {
			minDist = dist;
			System.arraycopy(root.point, 0, result, 0, k);
		}

		int cd = depth % k;
		if (query[cd] <= root.point[cd]) {
			// left subtree
			if (query[cd] - minDist <= root.point[cd]) {
				nearest(root.left, query, minDist, result, depth + 1);
			}
			if (query[cd] + minDist > root.point[cd]) {
				nearest(root.right, query, minDist, result, depth + 1);
			}
		} else {
			// right subtree
			if (query[cd] + minDist > root.point[cd]) {
				nearest(root.right, query, minDist, result, depth + 1);
			}
			if (query[cd] - minDist <= root.point[cd]) {
				nearest(root.left, query, minDist, result, depth + 1);
			}
		}
	}

	/**
	 * Return the k distance for k nearest neighbor from a query point
	 */
	public Queue<Double> kNearest(double[] query, int k) {
		Queue<Double> queue = new PriorityQueue<>(k, Collections.reverseOrder());
		kNearest(root, query, queue, 0, k);
		return queue;
	}

	private void kNearest(Node root, double[] query, Queue<Double> queue,
			int depth, int minPts) {
		if (root == null) {
			return;
		}

		double dist = distance(query, root.point);

		if (dist != 0 && (queue.isEmpty() || dist < queue.peek())) { // exclude
																		// query
																		// itself
			queue.offer(dist);
			if (queue.size() > minPts) {
				queue.poll();
			}
		}

		double minDist = queue.isEmpty() ? Double.MAX_VALUE : queue.peek();
		int cd = depth % k;
		if (query[cd] <= root.point[cd]) {
			// left subtree
			if (query[cd] - minDist <= root.point[cd]) {
				kNearest(root.left, query, queue, depth + 1, minPts);
			}
			if (query[cd] + minDist > root.point[cd]) {
				kNearest(root.right, query, queue, depth + 1, minPts);
			}
		} else {
			// right subtree
			if (query[cd] + minDist > root.point[cd]) {
				kNearest(root.right, query, queue, depth + 1, minPts);
			}
			if (query[cd] - minDist <= root.point[cd]) {
				kNearest(root.left, query, queue, depth + 1, minPts);
			}
		}
	}

	private double distance(double[] p, double[] q) {
		double result = 0;
		for (int i = 0; i < k; i++) {
			double di = p[i] - q[i];
			result += di * di;
		}
		return Math.sqrt(result);
	}

	public int getSize() {
		return size;
	}

}
