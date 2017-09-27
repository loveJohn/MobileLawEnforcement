package com.chinadci.mel.mleo.ui.views;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chinadci.android.utils.LocationUtils;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

/**
 * 
 * @ClassName PolyGatherBar
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:36
 * 
 */
public class PolyGatherBar extends LinearLayout {
	int maxWidth;
	Geometry oldGeo = null;
	Point defMapCenter;
	ArrayList<Point> pointList;
	ArrayList<Point> pointListRedo;
	GraphicsLayer gatherLayer;
	GraphicsLayer annotationLayer;
	GraphicsLayer areaLayer;
	GraphicsLayer initLayer;
	MapView defMapView;

	Animation inAnim;
	SimpleLineSymbol lineSymbol;
	SimpleLineSymbol outLineSymbol;
	SimpleFillSymbol fillSymbol;
	SimpleMarkerSymbol pointSymbol;
	PictureMarkerSymbol pointStartSymbol;
	View rootView;
	Button gatherButton;
	Button undoButton;
	Button redoButton;
	Button clearButton;

	int slide_in_bottom;
	int measurebar_maxwidth;
	int view_polygatherbar;
	int view_polygongatherbar_gat;
	int view_polygongatherbar_undo;
	int view_polygongatherbar_redo;
	int view_polygongatherbar_clear;
	float density;

	DecimalFormat decFormat = new DecimalFormat("#.000");

	@SuppressLint("NewApi")
	public PolyGatherBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public PolyGatherBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		// TODO Auto-generated constructor stub
	}

	public PolyGatherBar(Context context) {
		super(context);
		initView();
		// TODO Auto-generated constructor stub
	}

	public Geometry getOldGeo() {
		return oldGeo;
	}

	public ArrayList<Point> getPointList() {
		return pointList;
	}

	public void setPoly(Geometry poly) {
		try {
			if (poly.getType() == Geometry.Type.POLYGON) {
				gatherLayer.removeAll();
				oldGeo = poly;
				gatherLayer.addGraphic(new Graphic(oldGeo, fillSymbol));
				defMapView.setExtent(oldGeo);
				double minX = -1, minY = -1, maxX = -1, maxY = -1;
				MultiPath mPath = (MultiPath) oldGeo;
				for (int pi = 0; pi < mPath.getPointCount(); pi++) {
					Point point = mPath.getPoint(pi);
					if (minX == -1 || minX > point.getX()) {
						minX = point.getX();
					}

					if (minY == -1 || minY > point.getY()) {
						minY = point.getY();
					}

					if (maxX == -1 || maxX < point.getX()) {
						maxX = point.getX();
					}

					if (maxY == -1 || maxY < point.getY()) {
						maxY = point.getY();
					}
				}
				defMapView.zoomToScale(new Point((minX + maxX) / 2,
						(minY + maxY) / 2), getContext().getResources()
						.getDisplayMetrics().density * 10000d);

			} else if (poly.getType() == Geometry.Type.POINT) {
				initLayer.removeAll();
				initLayer.addGraphic(new Graphic(poly, pointStartSymbol));
				defMapView.zoomToScale((Point) poly, getContext()
						.getResources().getDisplayMetrics().density * 10000d);
				// defMapView.setExtent(oldGeo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMapRange(Geometry range) {
		defMapView.setExtent(range);
	}

	public void setMapCentre(Double scale, Point point) {
		if (point != null)
			defMapView.zoomToScale(point, scale);
	}

	public void show(ViewGroup g, int index, MapView m) {
		bindMapView(m);
		int pwidth = g.getWidth();
		String size = g.getTag().toString();
		int en_small = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "en_small", null,
				null);
		if (size.equals(getContext().getString(en_small))) {
			g.addView(PolyGatherBar.this, index, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		} else {
			if (pwidth < maxWidth) {
				g.addView(PolyGatherBar.this, index, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			} else {
				g.addView(PolyGatherBar.this, index, new LayoutParams(maxWidth,
						LayoutParams.WRAP_CONTENT));

			}
		}
		startAnimation(inAnim);
	}

	private void initView() {
		density = getContext().getResources().getDisplayMetrics().density;

		slide_in_bottom = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":anim/" + "slide_in_bottom",
				null, null);
		inAnim = AnimationUtils.loadAnimation(getContext(), slide_in_bottom);

		measurebar_maxwidth = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":dimen/"
						+ "measurebar_maxwidth", null, null);
		view_polygatherbar = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":layout/"
						+ "view_polygatherbar", null, null);
		view_polygongatherbar_gat = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/"
						+ "view_polygongatherbar_gat", null, null);
		view_polygongatherbar_undo = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/"
						+ "view_polygongatherbar_undo", null, null);
		view_polygongatherbar_redo = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/"
						+ "view_polygongatherbar_redo", null, null);
		view_polygongatherbar_clear = getContext().getResources()
				.getIdentifier(
						getContext().getPackageName() + ":id/"
								+ "view_polygongatherbar_clear", null, null);

		maxWidth = getContext().getResources().getDimensionPixelSize(
				measurebar_maxwidth);
		pointList = new ArrayList<Point>();
		pointListRedo = new ArrayList<Point>();

		lineSymbol = new SimpleLineSymbol(Color.RED, 1,
				SimpleLineSymbol.STYLE.SOLID);
		outLineSymbol = new SimpleLineSymbol(Color.RED, 1, STYLE.SOLID);
		fillSymbol = new SimpleFillSymbol(Color.RED);
		fillSymbol.setAlpha(64);
		fillSymbol.setOutline(outLineSymbol);
		pointSymbol = new SimpleMarkerSymbol(Color.RED, 8,
				SimpleMarkerSymbol.STYLE.CIRCLE);
		pointStartSymbol = new PictureMarkerSymbol(getResources().getDrawable(
				getResources().getIdentifier(
						this.getContext().getPackageName() + ":mipmap/"
								+ "ic_curlocation", null, null)));
		rootView = LayoutInflater.from(getContext()).inflate(
				view_polygatherbar, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		gatherButton = (Button) rootView
				.findViewById(view_polygongatherbar_gat);
		undoButton = (Button) rootView.findViewById(view_polygongatherbar_undo);
		redoButton = (Button) rootView.findViewById(view_polygongatherbar_redo);
		clearButton = (Button) rootView
				.findViewById(view_polygongatherbar_clear);

		gatherButton.setOnClickListener(toolClickListener);
		undoButton.setOnClickListener(toolClickListener);
		redoButton.setOnClickListener(toolClickListener);
		clearButton.setOnClickListener(toolClickListener);

	}

	public ArrayList<Point> getPolyPoints() {
		return pointList;
	}

	public void setInitialGraphic(Geometry geo) {
		oldGeo = geo;
		gatherLayer.removeAll();
		gatherLayer.addGraphic(new Graphic(oldGeo, fillSymbol));
		new Thread(new Runnable() {
			public void run() {

				try {
					Thread.sleep(500);
					handler.sendEmptyMessage(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setMapCenter(Point p) {
		if (p != null) {
			defMapCenter = p;
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public void bindMapView(MapView map) {
		gatherLayer = new GraphicsLayer();
		annotationLayer = new GraphicsLayer();
		areaLayer = new GraphicsLayer();
		initLayer = new GraphicsLayer();
		defMapView = map;
		defMapView.addLayer(gatherLayer);
		defMapView.addLayer(annotationLayer);
		defMapView.addLayer(areaLayer);
		defMapView.addLayer(initLayer);
		defMapView.setOnTouchListener(new GatherBarMapTouchListener(
				getContext(), defMapView));
	}

	public void rebindTouchListener() {
		defMapView.setOnTouchListener(new GatherBarMapTouchListener(
				getContext(), defMapView));
	}

	OnClickListener toolClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vid = v.getId();
			if (vid == view_polygongatherbar_clear) {
				if (oldGeo != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getContext());
					builder.setCancelable(true)
							.setTitle("提示")
							.setMessage("确定要重新绘制红线吗?")
							.setPositiveButton("重绘红线",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											oldGeo = null;
											clearGather();
											dialog.dismiss();
											dialog = null;
										}
									})
							.setNegativeButton("暂不重绘",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											dialog = null;
										}
									});
					builder.create().show();
				} else {
					clearGather();
				}
			} else if (vid == view_polygongatherbar_redo) {
				redoGather();
			} else if (vid == view_polygongatherbar_undo) {
				undoGather();
			} else if (vid == view_polygongatherbar_gat) {
				if (oldGeo != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getContext());
					builder.setCancelable(true)
							.setTitle("提示")
							.setMessage("确定要重新绘制红线吗?")
							.setPositiveButton("重绘红线",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											oldGeo = null;
											// doGather();
											dialog.dismiss();
											dialog = null;
										}
									})
							.setNegativeButton("暂不重绘",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											dialog = null;
										}
									});
					builder.create().show();
				} else {
					doGather();
				}
			}

		}
	};

	class GatherBarMapTouchListener extends MapOnTouchListener {
		MapView mapView;

		public GatherBarMapTouchListener(Context context, MapView view) {
			super(context, view);
			// TODO Auto-generated constructor stub
			mapView = view;
		}

		@Override
		public boolean onSingleTap(MotionEvent point) {
			try {

				if (mapView.getScale() > 5000) {
					Toast.makeText(getContext(), "请放大地图后再绘制红线",
							Toast.LENGTH_LONG).show();
					return true;
				}

				final Point mapPoint = mapView.toMapPoint(new Point(point
						.getX(), point.getY()));

				if (oldGeo != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getContext());
					builder.setCancelable(true)
							.setTitle("提示")
							.setMessage("确定要重新绘制红线吗?")
							.setPositiveButton("重绘红线",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											oldGeo = null;
											// pointList.add(mapPoint);
											buttonEnable();
											drawMultiPath(pointList);
											dialog.dismiss();
											dialog = null;
										}
									})
							.setNegativeButton("暂不重绘",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											dialog = null;
										}
									});
					builder.create().show();
				} else {
					pointList.add(mapPoint);
					buttonEnable();
					drawMultiPath(pointList);
					String txt = String.format("X:%1$s Y:%2$s",
							decFormat.format(mapPoint.getX()),
							decFormat.format(mapPoint.getY()));
					TextSymbol textSymbol = new TextSymbol(18, txt, 0xffff6400);
					Graphic annotation = new Graphic(mapPoint, textSymbol);
					annotationLayer.removeAll();
					annotationLayer.addGraphic(annotation);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return true;
		}
	}

	void drawMultiPath(ArrayList<Point> points) {
		MultiPath poly;
		Point startPoint;
		int pointCount = points.size();
		if (pointCount == 2) {
			poly = new Polyline();
			startPoint = points.get(0);
			poly.startPath(startPoint);
			Point endPoint = points.get(1);
			poly.lineTo(endPoint);
			gatherLayer.removeAll();
			gatherLayer.addGraphic(new Graphic(poly, lineSymbol));
			for (Point p : points) {
				gatherLayer.addGraphic(new Graphic(p, pointSymbol));
			}
		} else if (pointCount > 2) {
			poly = new Polygon();
			startPoint = points.get(0);

			double centerX = startPoint.getX();
			double centerY = startPoint.getY();
			poly.startPath(startPoint);
			for (int i = 1; i < pointCount; i++) {
				poly.lineTo(points.get(i));
				centerX += points.get(i).getX();
				centerY += points.get(i).getY();
			}
			centerX = centerX / pointCount;
			centerY = centerY / pointCount;
			gatherLayer.removeAll();
			gatherLayer.addGraphic(new Graphic(poly, fillSymbol));

			// 计算面积
			Geometry geo = GeometryEngine.project(poly, SpatialReference
					.create(SpatialReference.WKID_WGS84), SpatialReference
					.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
			long num = Math.abs(Math.round(geo.calculateArea2D()));

			String txt = num + "m²";
			// String txt = String.format("面积：%1$s 平方米",
			// decFormat.format(num));
			TextSymbol textSymbol = new TextSymbol(18, txt, 0xffff6400);
			Graphic numGrap = new Graphic(new Point(centerX, centerY),
					textSymbol);
			areaLayer.removeAll();
			areaLayer.addGraphic(numGrap);

			for (Point p : points) {
				gatherLayer.addGraphic(new Graphic(p, pointSymbol));
			}

		} else if (pointCount == 1) {
			gatherLayer.removeAll();
			gatherLayer.addGraphic(new Graphic(points.get(0), pointSymbol));
		} else if (pointCount < 1) {
			gatherLayer.removeAll();
		}
	}

	void buttonEnable() {
		if (pointList.size() > 0) {
			if (!undoButton.isEnabled())
				undoButton.setEnabled(true);
		} else {

			if (undoButton.isEnabled())
				undoButton.setEnabled(false);
		}

		if (pointListRedo.size() > 0) {
			if (!redoButton.isEnabled())
				redoButton.setEnabled(true);
		} else {
			if (redoButton.isEnabled())
				redoButton.setEnabled(false);
		}

		if (pointList.size() < 1 && pointListRedo.size() < 1) {
			if (clearButton.isEnabled())
				clearButton.setEnabled(false);

		} else {
			if (!clearButton.isEnabled())
				clearButton.setEnabled(true);
		}
	}

	void clearGather() {
		gatherLayer.removeAll();
		annotationLayer.removeAll();
		areaLayer.removeAll();
		pointList.clear();
		pointListRedo.clear();
		buttonEnable();
	}

	void redoGather() {
		if (pointListRedo.size() > 0) {
			pointList.add(pointListRedo.get(pointListRedo.size() - 1));
			pointListRedo.remove(pointListRedo.size() - 1);
			buttonEnable();
			drawMultiPath(pointList);
		}

	}

	void undoGather() {
		if (pointList.size() > 0) {
			pointListRedo.add(pointList.get(pointList.size() - 1));
			pointList.remove(pointList.size() - 1);
			buttonEnable();
			drawMultiPath(pointList);
		}
	}

	void doGather() {
		if (LocationUtils.isGPSSupport(getContext())) {
			Log.i("guoteng","PolyGatherBar_doGather");
			Location loc = defMapView.getLocationDisplayManager().getLocation();
			if (loc == null)
				loc = LocationUtils.getGPSLocation(getContext());
			if (loc == null) {
				Toast.makeText(getContext(), "获取当前位置失败", Toast.LENGTH_SHORT)
						.show();
			} else {
				Point point = new Point(loc.getLongitude(), loc.getLatitude());
				if (pointList.size() > 0) {
					Point lastPoint = pointList.get(pointList.size() - 1);
					if (lastPoint.getX() != point.getX()
							&& lastPoint.getY() != point.getY()) {
						pointList.add(point);
						buttonEnable();
						drawMultiPath(pointList);
					} else {
						Toast.makeText(getContext(), "与上一点位置相同",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					pointList.add(point);
					buttonEnable();
					drawMultiPath(pointList);
				}

			}

		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setCancelable(true)
					.setTitle("提示")
					.setMessage("GPS功能未开启")
					.setPositiveButton("开启GPS",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Intent callGPSSettingIntent = new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									getContext().startActivity(
											callGPSSettingIntent);
								}
							})
					.setNegativeButton("放弃操作",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									dialog = null;
								}

							});
			builder.create().show();
		}

	}

	/**
	 * 
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (oldGeo != null) {
					Polygon gon = (Polygon) oldGeo;
					defMapView.setExtent(gon);
				}
				break;

			case 0:
				if (defMapCenter != null) {
					Double xmin = defMapView.getLayer(0).getFullExtent()
							.getXMin();
					Double xmax = defMapView.getLayer(0).getFullExtent()
							.getXMax();
					Double ymin = defMapView.getLayer(0).getFullExtent()
							.getYMin();
					Double ymax = defMapView.getLayer(0).getFullExtent()
							.getYMax();
					if (defMapCenter.getX() > xmin
							&& defMapCenter.getX() < xmax
							&& defMapCenter.getY() > ymin
							&& defMapCenter.getY() < ymax) {

						defMapView
								.zoomToScale(
										defMapCenter,
										getContext().getResources()
												.getDisplayMetrics().density * 20000d);
					} else {
						defMapView.setScale(getContext().getResources()
								.getDisplayMetrics().density * 20000d);
					}
				}
				break;

			default:
				break;
			}

		};
	};
}
