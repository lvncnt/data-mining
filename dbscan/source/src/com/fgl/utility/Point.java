package com.fgl.utility;

public class Point {
	private double x; // x-coordinate
	private double y; // y-coordinate
	private int ID; 
	private int cluster = 0;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the Euclidean distance between this point and that point.
	 */
	public double euclideanDistance(Point that) {
		double dx = this.x - that.getX(); 
		double dy = this.y - that.getY(); 
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ")";
	}


}
