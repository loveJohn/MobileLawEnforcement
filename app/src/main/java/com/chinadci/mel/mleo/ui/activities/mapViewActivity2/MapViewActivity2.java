package com.chinadci.mel.mleo.ui.activities.mapViewActivity2;

import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.OutsideManifestHandler;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ui.activities.CaptionActivity;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.Hx;
import com.chinadci.mel.mleo.ui.views.MleoMapView;
import com.chinadci.mel.mleo.utils.MapUtil;
import com.esri.android.map.GraphicsLayer;
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
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.runtime.LicenseResult;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class MapViewActivity2 extends CaptionActivity {
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
	protected TiledServiceLayer imageryTiledLayer_;
	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer;

	public void setVectorTiledLayer(TiledServiceLayer layer,
			TiledServiceLayer layer_) {
		this.vectorTiledLayer = layer;
		this.vectorTiledLayer_ = layer_;
		if (mapViewer != null)
			mapViewer.setVectorTiledLayer(layer, layer_);
	}

	public void setImageryLayer(TiledServiceLayer layer,
			TiledServiceLayer layer_) {
		this.imageryTiledLayer = layer;
		this.imageryTiledLayer_ = layer_;
		if (mapViewer != null)
			mapViewer.setImageryLayer(layer, layer_);
	}

	private static final String CLIENT_ID = "fWjgn6RQYiqLZQgb";
	String title;
	String geoJson;
	MleoMapView mapViewer;
	GraphicsLayer geoLayer;

	// String jctbUrl;
	// ArcGISDynamicMapServiceLayer jctbLayer;
	private String aj_id;
	private String xzqh_id;
	private GraphicsLayer drawLayerDk;

	SimpleLineSymbol lineSymbol;
	SimpleLineSymbol outLineSymbol;
	SimpleFillSymbol fillSymbol;
	SimpleMarkerSymbol pointSymbol;
	PictureMarkerSymbol locationSymbol;

	double minX = -1;
	double minY = -1;
	double maxX = -1;
	double maxY = -1;
	boolean signPoint = false;

	private String ajKey;

	private String jcbh;

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
		geoJson = getIntent().getStringExtra(Parameters.GEOMETRY);
		title = getIntent().getStringExtra(Parameters.ACTIVITY_TITLE);
		// jctbUrl = getIntent().getStringExtra("jctbUrl");
		aj_id = getIntent().getStringExtra("ajid");
		xzqh_id = getIntent().getStringExtra("xzqhid");
		try {
			ajKey = getIntent().getStringExtra("ajKey");
		} catch (Exception e) {
		}
		try {
			jcbh = getIntent().getStringExtra("jcbh");
		} catch (Exception e) {
		}
		setTitle(title);
		init();

	}

	@Override
	protected void onBackButtonClick(View v) {
		super.onBackButtonClick(v);
		this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	OnStatusChangedListener statusChangedListener = new OnStatusChangedListener() {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("deprecation")
		public void onStatusChanged(Object source, STATUS status) {
			try {
				MapView mapView = (MapView) source;
				Location location = mapView.getLocationDisplayManager().getLocation();
				// List<Hx> hxs= DbUtil.getHxsByXzquAndAj(MapViewActivity2.this,
				// xzqh_id, aj_id);
				List<Hx> hxs = DbUtil.getHxsByXzquAndAj_Wpzf(
						MapViewActivity2.this, xzqh_id, aj_id);
				if (hxs != null && hxs.size() > 0) {
					drawLayerDk.removeAll();
					for (int i = 0; i < hxs.size(); i++) {
						try {
							if (jcbh.equals(hxs.get(i).getName())) {
								MapUtil.loadPolygonAndPointWithTextAndColor(
										drawLayerDk, mapViewer.getMapView(),
										hxs.get(i).getHx(), hxs.get(i)
												.getName(), Color.RED, Color
												.argb(0, 255, 255, 255));
							} else {
								switch (hxs.get(i).getAjKey()) {
								case "1":
								case "2":
									MapUtil.loadPolygonAndPointWithTextAndColor(
											drawLayerDk,
											mapViewer.getMapView(), hxs.get(i)
													.getHx(), hxs.get(i)
													.getName(), Color
													.parseColor("#EF6C00"),
											Color.argb(0, 255, 255, 255));
									break;
								case "3":
									MapUtil.loadPolygonAndPointWithTextAndColor(
											drawLayerDk,
											mapViewer.getMapView(), hxs.get(i)
													.getHx(), hxs.get(i)
													.getName(), Color
													.parseColor("#2979FF"),
											Color.argb(0, 255, 255, 255));
									break;
								}
							}
						} catch (Exception e) {
						}
					}
				}
				if (geoJson != null && !geoJson.equals("")) {
					JsonFactory jsonFactory = new JsonFactory();
					JSONArray geoJsonArray = new JSONArray(geoJson);
					if (geoJsonArray != null && geoJsonArray.length() > 0) {
						for (int i = 0; i < geoJsonArray.length(); i++) {
							JSONObject geoJsonObject = geoJsonArray
									.getJSONObject(i);
							if (geoJsonObject != null) {
								String geoString = geoJsonObject.toString();
								JsonParser jParser = jsonFactory
										.createJsonParser(geoString);
								if (jParser != null) {
									MapGeometry mapGeo = GeometryEngine
											.jsonToGeometry(jParser);
									if (mapGeo != null) {
										Geometry geo = mapGeo.getGeometry();

										Graphic graphic = null;
										if (geo.getType() == Geometry.Type.POINT) {
											@SuppressWarnings("unused")
											Geometry geo2 = GeometryEngine
													.project(
															geo,
															SpatialReference
																	.create(SpatialReference.WKID_WGS84),
															SpatialReference
																	.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
											graphic = new Graphic(geo,
													locationSymbol);
											Point point = (Point) geo;
											if (geoJsonArray.length() == 1) {
												signPoint = true;
												minX = point.getX();
												minY = point.getY();
											} else {
												if (minX == -1
														|| minX > point.getX()) {
													minX = point.getX();
												}

												if (minY == -1
														|| minY > point.getY()) {
													minY = point.getY();
												}

												if (maxX == -1
														|| maxX < point.getX()) {
													minX = point.getX();
												}

												if (maxY == -1
														|| maxY < point.getY()) {
													maxY = point.getY();
												}
											}
										} else if (geo.getType() == Geometry.Type.POLYLINE) {
											graphic = new Graphic(geo,
													lineSymbol);
											MultiPath mPath = (MultiPath) geo;
											for (int pi = 0; pi < mPath
													.getPointCount(); pi++) {
												Point point = mPath
														.getPoint(pi);
												if (minX == -1
														|| minX > point.getX()) {
													minX = point.getX();
												}

												if (minY == -1
														|| minY > point.getY()) {
													minY = point.getY();
												}

												if (maxX == -1
														|| maxX < point.getX()) {
													maxX = point.getX();
												}

												if (maxY == -1
														|| maxY < point.getY()) {
													maxY = point.getY();
												}
											}

										} else if (geo.getType() == Geometry.Type.POLYGON) {
											graphic = new Graphic(geo,
													fillSymbol);
											MultiPath mPath = (MultiPath) geo;
											for (int pi = 0; pi < mPath
													.getPointCount(); pi++) {
												Point point = mPath
														.getPoint(pi);
												if (minX == -1
														|| minX > point.getX()) {
													minX = point.getX();
												}

												if (minY == -1
														|| minY > point.getY()) {
													minY = point.getY();
												}

												if (maxX == -1
														|| maxX < point.getX()) {
													maxX = point.getX();
												}

												if (maxY == -1
														|| maxY < point.getY()) {
													maxY = point.getY();
												}
											}
										}
										if (graphic != null) {
											geoLayer.addGraphic(graphic);
										}
									}
								}
							}
						}
					}
					if (signPoint) {
						mapView.zoomToScale(new Point(minX, minY), 25000d);
					} else {
						mapView.zoomToScale(new Point((minX + maxX) / 2,
								(minY + maxY) / 2), getResources()
								.getDisplayMetrics().density * 20000d);
					}
					mapViewer
							.viewMapScale(getResources().getDisplayMetrics().density * 20000d);
				} else if (location != null
						&& pointIsInside(new Point(location.getLongitude(),
								location.getLatitude()), mapView.getMaxExtent())) {
					mapView.zoomToScale(new Point(location.getLongitude(),
							location.getLatitude()), getResources()
							.getDisplayMetrics().density * 20000d);
					mapViewer
							.viewMapScale(getResources().getDisplayMetrics().density * 20000d);
				} else {
					OutsideManifestHandler handler = OutsideManifestHandler
							.getHandler(MapViewActivity2.this);
					if (handler != null && handler.getUserMapCentre() != null) {
						Point centrePoint = handler.getUserMapCentre()
								.getPoint();
						double scale = handler.getUserMapCentre().getScale();
						if (centrePoint != null && scale > 0) {
							mapView.zoomToScale(centrePoint, scale);
							mapViewer.viewMapScale(scale);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void init() {
		Log.i("ydzf","MapViewActivity2_init");
		int ic_curlocation = getResources().getIdentifier(
				getPackageName() + ":drawable/" + "ic_curlocation", null, null);
		Drawable iconPosition = getResources().getDrawable(ic_curlocation);
		locationSymbol = new PictureMarkerSymbol(iconPosition);
		mapViewer = new MleoMapView(this);

		if (vectorTiledLayer != null && vectorTiledLayer_ != null)
			mapViewer.setVectorTiledLayer(vectorTiledLayer, vectorTiledLayer_);
		if (imageryTiledLayer != null && imageryTiledLayer_ != null)
			mapViewer.setImageryLayer(imageryTiledLayer, imageryTiledLayer_);
		// try {
		// jctbLayer = new ArcGISDynamicMapServiceLayer(jctbUrl);
		// mapViewer.getMapView().addLayer(jctbLayer);
		// } catch (Exception e) {
		// }

		mapViewer.setMapStatusChangedListener(statusChangedListener);
		setContent(mapViewer);
		lineSymbol = new SimpleLineSymbol(Color.RED, 1,
				SimpleLineSymbol.STYLE.SOLID);
		// if (!TextUtils.isEmpty(ajKey) && ajKey.equals("3")) {
		// outLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, STYLE.SOLID);
		// fillSymbol = new SimpleFillSymbol(Color.TRANSPARENT);
		// fillSymbol.setAlpha(0);
		// fillSymbol.setOutline(outLineSymbol);
		// } else {
		outLineSymbol = new SimpleLineSymbol(Color.RED, 2, STYLE.SOLID);
		fillSymbol = new SimpleFillSymbol(Color.CYAN);
		fillSymbol.setAlpha(64);
		fillSymbol.setOutline(outLineSymbol);
		// }
		pointSymbol = new SimpleMarkerSymbol(Color.MAGENTA, 8,
				SimpleMarkerSymbol.STYLE.SQUARE);
		drawLayerDk = new GraphicsLayer();
		mapViewer.getMapView().addLayer(drawLayerDk);
		geoLayer = new GraphicsLayer();
		mapViewer.getMapView().addLayer(geoLayer);
	}

	private boolean pointIsInside(Point point, Envelope extent) {
		return (point.getX() > extent.getXMin()
				&& point.getX() < extent.getXMax()
				&& point.getY() > extent.getYMin() && point.getY() < extent
				.getYMax());
	}
}
