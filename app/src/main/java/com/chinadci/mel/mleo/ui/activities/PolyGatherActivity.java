package com.chinadci.mel.mleo.ui.activities;

import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import android.R;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chinadci.mel.mleo.core.OutsideManifestHandler;
import com.chinadci.mel.mleo.ui.views.PolyGatherView;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.runtime.LicenseResult;

/**
 * 
 * @ClassName PolyGatherActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:05
 * 
 */
public class PolyGatherActivity extends CaptionActivity {
	private static final String CLIENT_ID = "fWjgn6RQYiqLZQgb";
	public final static String REDLINE = "redline";
	public final static String POINT="point";
	public final static String AREA = "area";
	public final static String MAPRANGE = "range";
	public final static String MAPCENTRE = "centre";
	public final static String MAPSCALE = "scale";

	ImageButton returnButton;
	PolyGatherView gatherView;

	String oldRedline;
	String oldPoint;
	String mapRange;

	int cn_redlinegather;
	int ic_complete;
	double area;
	
	/**
	 * 基础地图
	 */
	protected TiledServiceLayer vectorTiledLayer;
	/**
	 * 基础地图
	 */
	protected TiledServiceLayer vectorTiledLayer_;
	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer;
	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer_;
	
	public void setVectorTiledLayer(TiledServiceLayer layer,TiledServiceLayer layer_){
		this.vectorTiledLayer=layer;
		this.vectorTiledLayer_=layer_;
		if(gatherView!=null)
			gatherView.setVectorTiledLayer(layer,layer_);
	}
	
	public void setImageryLayer(TiledServiceLayer layer,TiledServiceLayer layer_)
	{
		this.imageryTiledLayer=layer;
		this.imageryTiledLayer_=layer_;
		if(gatherView!=null)
			gatherView.setImageryLayer(layer,layer_);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
		cn_redlinegather = getResources().getIdentifier(
				getPackageName() + ":string/" + "cn_redlinegather", null, null);
		ic_complete = getResources().getIdentifier(getPackageName() + ":drawable/" + "ic_complete",
				null, null);
		setTitle(cn_redlinegather);
		init();

	}

	@Override
	protected void onBackButtonClick(View v) {
		// TODO Auto-generated method stub
		super.onBackButtonClick(v);
		finish();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	OnStatusChangedListener statusChangedListener = new OnStatusChangedListener() {

		/**
		 * @Fields serialVersionUID : TODO
		 */
		private static final long serialVersionUID = 1L;

		public void onStatusChanged(Object source, STATUS status) {
			try {
				MapView mapView = (MapView) source;
				Location location = mapView.getLocationDisplayManager().getLocation();
				if ((oldRedline != null && oldRedline.length() > 0)||(oldPoint != null && oldPoint.length() > 0)) {
					JsonFactory jsonFactory = new JsonFactory();
					if(oldRedline != null && oldRedline.length() > 0)
					{
						JsonParser jsonParser = jsonFactory.createJsonParser(oldRedline);
						
						MapGeometry mapGeo = GeometryEngine.jsonToGeometry(jsonParser);
						Geometry geo = mapGeo.getGeometry();
						if (gatherView != null)
						{
							gatherView.setPoly(geo);
						}
					}
					if(oldPoint != null && oldPoint.length() > 0)
					{
						JsonParser jsonPointParser=jsonFactory.createJsonParser(oldPoint);
						MapGeometry mapPointGeo = GeometryEngine.jsonToGeometry(jsonPointParser);
						Geometry pointGeo=mapPointGeo.getGeometry();
						if (gatherView != null)
						{
							gatherView.setPoly(pointGeo);
						}
					}
				}
				else if (location != null
						&& pointIsInside(
								new Point(location.getLongitude(), location.getLatitude()),
								mapView.getMaxExtent())) {
					mapView.zoomToScale(new Point(location.getLongitude(), location.getLatitude()),
							25000d);
					gatherView.viewMapScale(25000d);
				} else {
					OutsideManifestHandler handler = OutsideManifestHandler
							.getHandler(PolyGatherActivity.this);
					if (handler != null && handler.getUserMapCentre() != null) {
						Point centrePoint = handler.getUserMapCentre().getPoint();
						double scale = handler.getUserMapCentre().getScale();
						if (centrePoint != null && scale > 0) {
							mapView.zoomToScale(centrePoint, scale);
							gatherView.viewMapScale(scale);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void init() {
		oldRedline = getIntent().getStringExtra(REDLINE);
		oldPoint=getIntent().getStringExtra(POINT);
		mapRange = getIntent().getStringExtra(MAPRANGE);
		gatherView = new PolyGatherView(this);
		
		if (vectorTiledLayer != null&&vectorTiledLayer_ != null)
			gatherView.setVectorTiledLayer(vectorTiledLayer,vectorTiledLayer_);
		if (imageryTiledLayer != null&&imageryTiledLayer_ != null)
			gatherView.setImageryLayer(imageryTiledLayer,imageryTiledLayer_);
		
		returnButton = new ImageButton(this);
		returnButton.setBackgroundColor(Color.TRANSPARENT);
		returnButton.setPadding(0, 0, 0, 0);
		returnButton.setImageResource(ic_complete);
		setContent(gatherView);
		setToolBar(returnButton);
		returnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (gatherView.getOldGeo() != null) {
					PolyGatherActivity.this.finish();
					return;
				}

				ArrayList<Point> s = gatherView.getPointList();
				if (s == null || s.size() < 3) {
					Toast.makeText(PolyGatherActivity.this, "红线图至少3个点", Toast.LENGTH_SHORT).show();
					return;
				}

				if (gatherView.geoCrossSelf(s)) {
					Toast.makeText(PolyGatherActivity.this, "红线图有自交现象", Toast.LENGTH_SHORT).show();
					return;
				}

				String redlinJson = polygonToJson(s);
				Bundle bundle = new Bundle();
				bundle.putString(REDLINE, redlinJson);
				bundle.putDouble(AREA, area);

				PolyGatherActivity.this.setResult(RESULT_OK, PolyGatherActivity.this.getIntent()
						.putExtras(bundle));
				PolyGatherActivity.this.finish();
			}
		});

		gatherView.setMapStatusChangedListener(statusChangedListener);
	}

	private String polygonToJson(ArrayList<Point> points) {
		if (points == null || points.size() < 1)
			return null;
		try {
			MultiPath poly;
			poly = new Polygon();
			poly.startPath(points.get(0));
			for (int i = 1; i < points.size(); i++) {
				poly.lineTo(points.get(i));
			}
			Geometry mercatorPoly = GeometryEngine.project(poly,
					SpatialReference.create(SpatialReference.WKID_WGS84),
					SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));

			area = mercatorPoly.calculateArea2D();
			if (area > 0) {
				String json = GeometryEngine.geometryToJson(null, poly);
				return json;

			} else {
				area = Math.abs(area);
				poly.reverseAllPaths();
				// poly.startPath(points.get(points.size() - 1));
				// for (int i = points.size() - 2; i > -1; i--) {
				// poly.lineTo(points.get(i));
				// }
				String json = GeometryEngine.geometryToJson(null, poly);
				return json;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean pointIsInside(Point point, Envelope extent) {
		return (point.getX() > extent.getXMin() && point.getX() < extent.getXMax()
				&& point.getY() > extent.getYMin() && point.getY() < extent.getYMax());
	}
}
