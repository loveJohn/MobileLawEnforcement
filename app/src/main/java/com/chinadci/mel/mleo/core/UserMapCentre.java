package com.chinadci.mel.mleo.core;

import com.esri.core.geometry.Point;

public class UserMapCentre {
	double x = -1;
	double y = -1;
	double scale = -1;

	public UserMapCentre(double x, double y, double scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;
	}

	public Point getPoint() {
		if (x > 0 & y > 0) {
			return new Point(x, y);
		}
		return null;
	}

	public double getScale() {

		return scale;
	}
}
