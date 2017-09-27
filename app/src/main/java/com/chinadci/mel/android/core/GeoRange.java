package com.chinadci.mel.android.core;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
/**
 * 
* @ClassName GeoRange 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:29:02 
*
 */
public class GeoRange {
	Double xMin, yMin, xMax, yMax;

	public GeoRange(Double xmin, Double ymin, Double xmax, Double ymax) {
		xMin = xmin;
		yMin = ymin;
		xMax = xmax;
		yMax = ymax;
	}

	public GeoRange(Point lbPoint, Point rtPoint) {
		xMin = lbPoint.getX();
		yMin = lbPoint.getY();
		xMax = rtPoint.getX();
		yMax = rtPoint.getY();
	}

	public Geometry getRange() {
		Point p1 = new Point(xMin, yMin);
		Point p2 = new Point(xMin, yMax);
		Point p3 = new Point(xMax, yMax);
		Point p4 = new Point(xMax, yMin);

		MultiPath poly = new Polygon();
		poly.startPath(p1);
		poly.lineTo(p2);
		poly.lineTo(p3);
		poly.lineTo(p4);
		return poly;
	}

	public Double getXMin() {
		return xMin;
	}

	public Double getXmax() {
		return xMax;
	}

	public Double getYMin() {
		return yMin;
	}

	public Double getYMax() {
		return yMax;
	}
}
