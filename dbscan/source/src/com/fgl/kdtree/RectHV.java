package com.fgl.kdtree;

public class RectHV {

	private final double xmin, ymin; // minimum x- and y-coordinates
	private final double xmax, ymax; // maximum x- and y-coordinates

	// construct the axis-aligned rectangle [xmin, xmax] x [ymin, ymax]
	public RectHV(double xmin, double ymin, double xmax, double ymax) {
		if (xmax < xmin || ymax < ymin) {
			throw new IllegalArgumentException("Invalid rectangle");
		}
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	public double getXmin() {
		return xmin;
	}

	public double getYmin() {
		return ymin;
	}

	public double getXmax() {
		return xmax;
	}

	public double getYmax() {
		return ymax;
	}

	// width and height of rectangle
	public double width() {
		return xmax - xmin;
	}

	public double height() {
		return ymax - ymin;
	}

	// if this axis-aligned rectangle contain p
	public boolean contains(double[] p) {
		return (p[0] >= xmin) && (p[0] <= xmax) && (p[1] >= ymin)
				&& (p[1] <= ymax);
	}

}
