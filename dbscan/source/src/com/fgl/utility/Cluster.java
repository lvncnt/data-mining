package com.fgl.utility;

import java.util.ArrayList;

public class Cluster {

	private ArrayList<Point> points;
	private Point centroid;
	private int id;

	public Cluster(int id) {
		this.id = id;
		points = new ArrayList<>();
	}

	public void addPoint(Point point) {
		points.add(point);
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cluster " + id + ": ").append("centroid=").append(centroid)
				.append(", points=").append(points);
		return sb.toString();
	}

}
