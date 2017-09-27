package com.chinadci.mel.mleo.ui.views;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;

/**
 * 
 * @ClassName PolyGatherView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:33
 * 
 */
public class PolyGatherView_Adress extends MleoMapView_Adress {

	PolyGatherBar gatherBar;
	Geometry oldGeo;

	public PolyGatherView_Adress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PolyGatherView_Adress(Context context) {
		super(context);
	}

	@Override
	protected void initView(Context c) {
		super.initView(c);
		// setMeasureVisibility(View.GONE);
		// setLocationVisibility(View.GONE);
		gatherBar = new PolyGatherBar(getContext());
		gatherBar.show(tabLayout, tabLayout.getChildCount(), mapView);
	}

	public Geometry getOldGeo() {
		return gatherBar.getOldGeo();
	}

	public ArrayList<Point> getPointList() {
		return gatherBar.getPointList();
	}

	public void setPoly(Geometry poly) {
		gatherBar.setPoly(poly);
		viewMapScale(getContext().getResources().getDisplayMetrics().density * 20000d);
	}

	public void setMapRange(Geometry range) {
		gatherBar.setMapRange(range);
	}

	public void setMapCentre(Double scale, Point point) {
		gatherBar.setMapCentre(scale, point);
	}

	@Override
	protected void showMeasureBar() {
		super.showMeasureBar();
		if (!measureBar.isShow())
			gatherBar.rebindTouchListener();
	}

	public boolean geoCrossSelf(ArrayList<Point> points) {
		if (points == null || points.size() < 4)
			return false;

		int pcount = points.size();
		for (int n = 0; n < pcount; n++) {
			Point p1 = points.get((n + 0) % pcount);
			Point p2 = points.get((n + 1) % pcount);
			MultiPath line1 = new Polyline();
			line1.startPath(p1);
			line1.lineTo(p2);
			for (int m = 0; m < pcount - 2; m++) {
				Point p3 = points.get((m + 2) % pcount);
				Point p4 = points.get((m + 3) % pcount);
				MultiPath line2 = new Polyline();
				line2.startPath(p3);
				line2.lineTo(p4);
				if (GeometryEngine.crosses(line1, line2, null))
					return true;
			}
		}
		return false;
	}
}
