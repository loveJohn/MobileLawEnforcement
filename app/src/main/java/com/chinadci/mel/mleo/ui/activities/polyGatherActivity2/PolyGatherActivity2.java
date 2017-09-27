package com.chinadci.mel.mleo.ui.activities.polyGatherActivity2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.OutsideManifestHandler;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ui.activities.CaptionActivity;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.Hx;
import com.chinadci.mel.mleo.ui.views.PolyGatherView;
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
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.runtime.LicenseResult;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class PolyGatherActivity2 extends CaptionActivity {
	private static final String CLIENT_ID = "fWjgn6RQYiqLZQgb";
	public final static String REDLINE = "redline";
	public final static String POINT = "point";
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

	MapView mMapView;
	Location mLocation;
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

	public void setVectorTiledLayer(TiledServiceLayer layer,
			TiledServiceLayer layer_) {
		this.vectorTiledLayer = layer;
		this.vectorTiledLayer_ = layer_;
		if (gatherView != null)
			gatherView.setVectorTiledLayer(layer, layer_);
	}

	public void setImageryLayer(TiledServiceLayer layer,
			TiledServiceLayer layer_) {
		this.imageryTiledLayer = layer;
		this.imageryTiledLayer_ = layer_;
		if (gatherView != null)
			gatherView.setImageryLayer(layer, layer_);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
		redGeoJson = getIntent().getStringExtra(Parameters.GEOMETRY);	//add teng.guo
		cn_redlinegather = getResources().getIdentifier(
				getPackageName() + ":string/" + "cn_redlinegather", null, null);
		ic_complete = getResources().getIdentifier(
				getPackageName() + ":drawable/" + "ic_complete", null, null);
		try {
			aj_id = getIntent().getStringExtra("ajid");
			xzqh_id = getIntent().getStringExtra("xzqhid");	
			jcbh = getIntent().getStringExtra("jcbh");
		} catch (Exception e) {
		}
		setTitle(cn_redlinegather);
		init();

	}

	@Override
	protected void onBackButtonClick(View v) {
		super.onBackButtonClick(v);
		finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	OnStatusChangedListener statusChangedListener = new OnStatusChangedListener() {
		private static final long serialVersionUID = 1L;

		public void onStatusChanged(Object source, STATUS status) {
			try {
				MapView mapView = (MapView) source;
				Location location = mapView.getLocationDisplayManager().getLocation();
				if ((oldRedline != null && oldRedline.length() > 0)
						|| (oldPoint != null && oldPoint.length() > 0)) {
					JsonFactory jsonFactory = new JsonFactory();
					if (oldRedline != null && oldRedline.length() > 0) {
						JsonParser jsonParser = jsonFactory
								.createJsonParser(oldRedline);

						MapGeometry mapGeo = GeometryEngine
								.jsonToGeometry(jsonParser);
						Geometry geo = mapGeo.getGeometry();
						if (gatherView != null) {
							gatherView.setPoly(geo);
						}
					}
					if (oldPoint != null && oldPoint.length() > 0) {
						JsonParser jsonPointParser = jsonFactory
								.createJsonParser(oldPoint);
						MapGeometry mapPointGeo = GeometryEngine
								.jsonToGeometry(jsonPointParser);
						Geometry pointGeo = mapPointGeo.getGeometry();
						if (gatherView != null) {
							gatherView.setPoly(pointGeo);
						}
					}
				} else if (location != null
						&& pointIsInside(new Point(location.getLongitude(),
								location.getLatitude()), mapView.getMaxExtent())) {
					mapView.zoomToScale(new Point(location.getLongitude(),
							location.getLatitude()), 25000d);
					gatherView.viewMapScale(25000d);
				} else {
					OutsideManifestHandler handler = OutsideManifestHandler
							.getHandler(PolyGatherActivity2.this);
					if (handler != null && handler.getUserMapCentre() != null) {
						Point centrePoint = handler.getUserMapCentre()
								.getPoint();
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
		gatherView = new PolyGatherView(this);
		oldRedline = getIntent().getStringExtra(REDLINE);
		oldPoint = getIntent().getStringExtra(POINT);
		mapRange = getIntent().getStringExtra(MAPRANGE);
		mMapView=gatherView.getMapView();
		if (vectorTiledLayer != null && vectorTiledLayer_ != null)
			gatherView.setVectorTiledLayer(vectorTiledLayer, vectorTiledLayer_);
		if (imageryTiledLayer != null && imageryTiledLayer_ != null)
			gatherView.setImageryLayer(imageryTiledLayer, imageryTiledLayer_);
		
		drawLayerDk = new GraphicsLayer();
		geoLayer = new GraphicsLayer();
		mMapView.addLayer(drawLayerDk);
		mMapView.addLayer(geoLayer);
		//初始化变量
		lineSymbol = new SimpleLineSymbol(Color.RED, 1,SimpleLineSymbol.STYLE.SOLID);
		outLineSymbol = new SimpleLineSymbol(Color.BLUE, 2, STYLE.SOLID);
		fillSymbol = new SimpleFillSymbol(Color.CYAN);
		fillSymbol.setAlpha(128);
		fillSymbol.setOutline(outLineSymbol);
		pointSymbol = new SimpleMarkerSymbol(Color.MAGENTA, 8,SimpleMarkerSymbol.STYLE.SQUARE);
		addCaseRedLine();
		
		returnButton = new ImageButton(this);
		returnButton.setBackgroundColor(Color.TRANSPARENT);
		returnButton.setPadding(0, 0, 0, 0);
		returnButton.setImageResource(ic_complete);
		setContent(gatherView);
		setToolBar(returnButton);
		gatherView.setMapStatusChangedListener(statusChangedListener);
		returnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (gatherView.getOldGeo() != null) {
					PolyGatherActivity2.this.finish();
					return;
				}
				final ArrayList<Point> s = gatherView.getPointList();
				if (s == null || s.size() < 3) {
					Toast.makeText(PolyGatherActivity2.this, "红线图至少3个点",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (gatherView.geoCrossSelf(s)) {
					Toast.makeText(PolyGatherActivity2.this, "红线图有自交现象",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// TODO
				AlertDialog.Builder builder = new Builder(
						PolyGatherActivity2.this);
				builder.setMessage("是否发送该红线一张图分析结果？");
				builder.setTitle("提示");
				builder.setPositiveButton("是",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									String geoJson = polygonToJson2(s);// 图斑JSON
									JSONObject object = new JSONObject();
									object.put("user", currentUser);
									object.put("polygon", new JSONObject(geoJson));
									String redlinJson = polygonToJson(s);
									Bundle bundle = new Bundle();
									bundle.putString(REDLINE, redlinJson);
									bundle.putDouble(AREA, area);
									PolyGatherActivity2.this.setResult(RESULT_OK,
											PolyGatherActivity2.this.getIntent().putExtras(bundle));
									new GeographicSendTask().execute(object);
								} catch (Exception e) {
									Toast.makeText(PolyGatherActivity2.this, "发送错误。",
											Toast.LENGTH_LONG).show();
								}
							}
						});
				builder.setNegativeButton("否",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								String redlinJson = polygonToJson(s);
								Bundle bundle = new Bundle();
								bundle.putString(REDLINE, redlinJson);
								bundle.putDouble(AREA, area);

								PolyGatherActivity2.this.setResult(RESULT_OK,
										PolyGatherActivity2.this.getIntent().putExtras(bundle));
								PolyGatherActivity2.this.finish();
							}
						});
				builder.create().show();
			}
		});
		
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
					SpatialReference
							.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));

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

	private String polygonToJson2(ArrayList<Point> points) {
		if (points == null || points.size() < 1)
			return null;

		try {
			MultiPath poly;
			poly = new Polygon();
			poly.startPath(points.get(0));
			for (int i = 1; i < points.size(); i++) {
				poly.lineTo(points.get(i));
			}

			long area = Math.round(poly.calculateArea2D());
			if (area > 0) {
				String json = GeometryEngine.geometryToJson(null, poly);
				return json;
			} else {
				MultiPath polyRe;
				polyRe = new Polygon();
				polyRe.startPath(points.get(0));
				polyRe.startPath(points.get(points.size() - 1));
				for (int i = points.size() - 2; i > -1; i--) {
					polyRe.lineTo(points.get(i));
				}
				long area2 = Math.round(polyRe.calculateArea2D());
				String json = GeometryEngine.geometryToJson(null, poly);
				return json;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	class GeographicSendTask extends AsyncTask<JSONObject, Void, Boolean> {
		CircleProgressBusyView abv;
		AlertDialog alertDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			int cn_sendding = getResources().getIdentifier(
					PolyGatherActivity2.this.getPackageName() + ":string/"
							+ "cn_sendding", null, null);
			abv = new CircleProgressBusyView(PolyGatherActivity2.this);
			abv.setMsg(getString(cn_sendding));
			alertDialog = new AlertDialog.Builder(PolyGatherActivity2.this)
					.create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(JSONObject... params) {
			boolean succeed = false;
			String uri;
			int shared_preferences = getResources().getIdentifier(
					PolyGatherActivity2.this.getPackageName() + ":string/"
							+ "shared_preferences", null, null);
			int sp_appuri = getResources().getIdentifier(
					PolyGatherActivity2.this.getPackageName() + ":string/"
							+ "sp_appuri", null, null);

			int uri_mapanalyze = getResources().getIdentifier(
					PolyGatherActivity2.this.getPackageName() + ":string/"
							+ "uri_mapanalyze", null, null);
			try {
				if (params != null && params.length > 0) {
					JSONObject json = params[0];
					String appUri = SharedPreferencesUtils.getInstance(
							PolyGatherActivity2.this, shared_preferences)
							.getSharedPreferences(sp_appuri, "");
					uri = appUri.endsWith("/") ? new StringBuffer(appUri)
							.append(PolyGatherActivity2.this
									.getString(uri_mapanalyze)).toString()
							: new StringBuffer(appUri)
									.append("/")
									.append(PolyGatherActivity2.this
											.getString(uri_mapanalyze))
									.toString();
					HttpResponse response = HttpUtils.httpClientExcutePost(uri,
							json);
					succeed = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			Toast.makeText(PolyGatherActivity2.this, "分析请求已提交，请注意查收短信",
					Toast.LENGTH_LONG).show();
			PolyGatherActivity2.this.finish();
		}
	}

	private boolean pointIsInside(Point point, Envelope extent) {
		return (point.getX() > extent.getXMin()
				&& point.getX() < extent.getXMax()
				&& point.getY() > extent.getYMin() && point.getY() < extent
				.getYMax());
	}
	
	
	//add teng.guo start
	
		GraphicsLayer geoLayer;
		SimpleLineSymbol lineSymbol;
		SimpleLineSymbol outLineSymbol;
		SimpleFillSymbol fillSymbol;
		SimpleMarkerSymbol pointSymbol;
		PictureMarkerSymbol locationSymbol;
		private GraphicsLayer drawLayerDk;
		double minX = -1;
		double minY = -1;
		double maxX = -1;
		double maxY = -1;
		boolean signPoint = false;
		String redGeoJson;
		
		private String aj_id;
		private String xzqh_id;
		private String jcbh;
		
		private void addCaseRedLine(){
			if(redGeoJson==null){
				return;
			}
			try {
				mLocation = mMapView.getLocationDisplayManager().getLocation();
				List<Hx> hxs = DbUtil.getHxsByXzquAndAj_Wpzf(
						PolyGatherActivity2.this, xzqh_id, aj_id);
				if (hxs != null && hxs.size() > 0) {
					drawLayerDk.removeAll();
					for (int i = 0; i < hxs.size(); i++) {
						try {
							if (jcbh.equals(hxs.get(i).getName())) {
								MapUtil.loadPolygonAndPointWithTextAndColor(
										drawLayerDk, mMapView,
										hxs.get(i).getHx(), hxs.get(i)
												.getName(), Color.RED, Color
												.argb(0, 255, 255, 255));
							} else {
								switch (hxs.get(i).getAjKey()) {
								case "1":
								case "2":
									MapUtil.loadPolygonAndPointWithTextAndColor(
											drawLayerDk,
											mMapView, hxs.get(i)
													.getHx(), hxs.get(i)
													.getName(), Color
													.parseColor("#EF6C00"),
											Color.argb(0, 255, 255, 255));
									break;
								case "3":
									MapUtil.loadPolygonAndPointWithTextAndColor(
											drawLayerDk,
											mMapView, hxs.get(i)
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
				if (redGeoJson != null && !redGeoJson.equals("")) {
					JsonFactory jsonFactory = new JsonFactory();
					JSONArray geoJsonArray = new JSONArray(redGeoJson);
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
						mMapView.zoomToScale(new Point(minX, minY), 25000d);
					} else {
						mMapView.zoomToScale(new Point((minX + maxX) / 2,
								(minY + maxY) / 2), getResources()
								.getDisplayMetrics().density * 20000d);
					}
					gatherView.viewMapScale(getResources().getDisplayMetrics().density * 20000d);
				} else if (mLocation != null
						&& pointIsInside(new Point(mLocation.getLongitude(),
								mLocation.getLatitude()), mMapView.getMaxExtent())) {
					mMapView.zoomToScale(new Point(mLocation.getLongitude(),
							mLocation.getLatitude()), getResources()
							.getDisplayMetrics().density * 20000d);
					gatherView.viewMapScale(getResources().getDisplayMetrics().density * 20000d);
				} else {
					OutsideManifestHandler handler = OutsideManifestHandler
							.getHandler(PolyGatherActivity2.this);
					if (handler != null && handler.getUserMapCentre() != null) {
						Point centrePoint = handler.getUserMapCentre()
								.getPoint();
						double scale = handler.getUserMapCentre().getScale();
						if (centrePoint != null && scale > 0) {
							mMapView.zoomToScale(centrePoint, scale);
							gatherView.viewMapScale(scale);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	//add teng.guo end
}
