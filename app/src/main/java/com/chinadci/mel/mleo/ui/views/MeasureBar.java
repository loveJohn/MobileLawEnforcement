package com.chinadci.mel.mleo.ui.views;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.chinadci.mel.android.core.interfaces.IClickListener;
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
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleLineSymbol.STYLE;
import com.esri.core.symbol.SimpleMarkerSymbol;

/**
 * 
 * @ClassName MeasureBar
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:50
 * 
 */
public class MeasureBar extends LinearLayout {
	final static int DRAW_POLYLIN = 10;
	final static int DRAW_POLYGON = 20;
	final static int DRAW_POINT = 30;
	final static int SHOW_LENGTH = 40;
	final static int SHOW_AREA = 50;
	final static int CLEAR_MAP = 60;
	int maxWidth;
	Switch switcher;
	View rootView;
	TextView numView;
	ImageButton clearButton;
	ImageButton shutButton;
	MapView defMapView;
	ViewGroup parenGroup;
	ArrayList<Point> pointList;
	GraphicsLayer measureLayer;
	RadioGroup alRadioGroup;
	
	DecimalFormat kmFormat = new DecimalFormat("#.###");
	DecimalFormat mFormat = new DecimalFormat("#");

	Boolean measureArea = false;
	Boolean shown = false;

	IClickListener shutClickListener;
	OnClickListener shutListener;
	SimpleLineSymbol lineSymbol;
	SimpleLineSymbol outLineSymbol;
	SimpleFillSymbol fillSymbol;
	SimpleMarkerSymbol pointSymbol;
	
	int en_small = -1;
	int measurebar_maxwidth = -1;
	int view_measurebar = -1;
	int view_measurebar_clear = -1;
	int view_measurebar_shut = -1;
	int view_measurebar_num = -1;
	int view_measurebar_switch = -1;
	int view_measurebar_a = -1;
	int view_measurebar_l = -1;
	int view_measurebar_la = -1;

	public Boolean isShow() {
		return shown;
	}

	public void isShow(Boolean b) {
		shown = b;
	}

	public MeasureBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	public MeasureBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public void show(ViewGroup g, int index, MapView m) {
		if (!isShow()) {
			parenGroup = g;
			bindMapView(m);
			int pwidth = parenGroup.getWidth();
			String size = parenGroup.getTag().toString();

			if (size.equals(getContext().getString(en_small))) {
				parenGroup.addView(MeasureBar.this, index, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			} else {
				if (pwidth < maxWidth) {
					parenGroup.addView(MeasureBar.this, index,
							new LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT));
				} else {
					parenGroup.addView(MeasureBar.this, index,
							new LayoutParams(maxWidth,
									LayoutParams.WRAP_CONTENT));
				}
			}
			isShow(true);
		}
	}

	public void setShutClickListener(OnClickListener listener) {
		this.shutListener = listener;
		
	}

	@SuppressLint("NewApi")
	void initView() {
		en_small = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "en_small", null,
				null);
		measurebar_maxwidth = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":dimen/"
						+ "measurebar_maxwidth", null, null);
		view_measurebar = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":layout/" + "view_measurebar",
				null, null);
		view_measurebar_clear = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/"
						+ "view_measurebar_clear", null, null);
		view_measurebar_shut = getContext().getResources()
				.getIdentifier(
						getContext().getPackageName() + ":id/"
								+ "view_measurebar_shut", null, null);
		view_measurebar_num = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/" + "view_measurebar_num",
				null, null);
		view_measurebar_switch = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/"
						+ "view_measurebar_switch", null, null);
		view_measurebar_a = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/" + "view_measurebar_a",
				null, null);
		view_measurebar_l = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/" + "view_measurebar_l",
				null, null);
		view_measurebar_la = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":id/" + "view_measurebar_la",
				null, null);

		maxWidth = getContext().getResources().getDimensionPixelSize(
				measurebar_maxwidth);

		lineSymbol = new SimpleLineSymbol(Color.BLUE, 1,
				SimpleLineSymbol.STYLE.SOLID);
		outLineSymbol = new SimpleLineSymbol(Color.BLUE, 1, STYLE.SOLID);
		fillSymbol = new SimpleFillSymbol(Color.BLUE);
		fillSymbol.setAlpha(64);
		fillSymbol.setOutline(outLineSymbol);
		pointSymbol = new SimpleMarkerSymbol(Color.BLUE, 8,
				SimpleMarkerSymbol.STYLE.SQUARE);

		rootView = LayoutInflater.from(getContext()).inflate(view_measurebar,
				null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		clearButton = (ImageButton) rootView
				.findViewById(view_measurebar_clear);
		shutButton = (ImageButton) rootView.findViewById(view_measurebar_shut);
		numView = (TextView) rootView.findViewById(view_measurebar_num);
		switcher = (Switch) rootView.findViewById(view_measurebar_switch);
		alRadioGroup = (RadioGroup) rootView.findViewById(view_measurebar_la);
		alRadioGroup.setOnCheckedChangeListener(conditionChangeListener);
		pointList = new ArrayList<Point>();

		clearButton.setOnClickListener(clickListener);
		shutButton.setOnClickListener(clickListener);

		switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@SuppressLint("NewApi")
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				measureArea = isChecked;
				pointList.clear();
				numView.setText("");
				measureLayer.removeAll();

			}
		});

		addView(rootView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	public void bindMapView(MapView map) {
		measureLayer = new GraphicsLayer();
		defMapView = map;
		defMapView.addLayer(measureLayer);
		defMapView.setOnTouchListener(new MeasureMapTouchListener(getContext(),
				defMapView));
	}

	public void setShutClickListener(IClickListener listener) {
		shutClickListener = listener;
	}

	public void shut() {
		shutView();
	}

	android.widget.RadioGroup.OnCheckedChangeListener conditionChangeListener = new android.widget.RadioGroup.OnCheckedChangeListener() {

		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == view_measurebar_l) {
				measureArea = false;
				pointList.clear();
				numView.setText("");
				measureLayer.removeAll();
			} else if (checkedId == view_measurebar_a) {
				measureArea = true;
				pointList.clear();
				numView.setText("");
				measureLayer.removeAll();
			}
		}
	};

	void shutView() {
		shown = false;
		measureLayer.removeAll();
		if (defMapView != null) {
			defMapView.removeLayer(measureLayer);
			defMapView.setOnTouchListener(new defaultMapOnTouchListener(
					getContext(), defMapView));
		}
		numView.setText("");
		pointList.clear();

		parenGroup.removeView(MeasureBar.this);

		// if (shutClickListener != null)
		// shutClickListener.onClick(shutButton, 1);
	}

	void clearMeasure() {
		numView.setText("");
		pointList.clear();
		if (measureLayer != null)
			measureLayer.removeAll();
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vid = v.getId();
			if (vid == view_measurebar_clear) {
				clearMeasure();
			} else if (vid == view_measurebar_shut) {
				if (shutListener != null)
					shutListener.onClick(v);
				else
					shutView();
			}
		}
	};

	class MeasureMapTouchListener extends MapOnTouchListener {
		MapView mapView;

		public MeasureMapTouchListener(Context context, MapView view) {
			super(context, view);
			// TODO Auto-generated constructor stub
			mapView = view;
		}

		@Override
		public boolean onSingleTap(MotionEvent point) {
			try {
				Point mapPoint = mapView.toMapPoint(new Point(point.getX(),
						point.getY()));
				pointList.add(mapPoint);
				Log.i("mapPoint", GeometryEngine.geometryToJson(null, mapPoint));
				if (!measureArea)
					doLengthCompute();
				else
					doAreaCompute();

			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;
		}
	}

	class defaultMapOnTouchListener extends MapOnTouchListener {

		public defaultMapOnTouchListener(Context context, MapView view) {
			super(context, view);
		}
	}

	void doAreaCompute() {

		new Thread(new Runnable() {

			final ArrayList<Point> points = pointList;

			public void run() {
				// TODO Auto-generated method stub
				try {
					final MultiPath poly;
					Message msg = MeasureHander.obtainMessage();
					Point startPoint = null;

					if (points.size() > 2) {
						poly = new Polygon();
						startPoint = points.get(0);
						poly.startPath(startPoint);
						for (int i = 1; i < points.size(); i++) {
							poly.lineTo(points.get(i));
						}
						new Thread(new Runnable() {
							public void run() {
								Message msg = MeasureHander.obtainMessage();
								// long num =
								// Math.abs(Math.round(poly.calculateArea2D()));

								Geometry p = GeometryEngine.project(
										poly,
										SpatialReference
												.create(SpatialReference.WKID_WGS84),
										SpatialReference
												.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
								long num = Math.abs(Math.round(p
										.calculateArea2D()));

								msg.what = SHOW_AREA;
								msg.obj = num;
								MeasureHander.sendMessage(msg);
							}
						}).start();
						msg.what = DRAW_POLYGON;
						msg.obj = poly;
						MeasureHander.sendMessage(msg);

					} else if (points.size() == 2) {
						poly = new Polyline();
						startPoint = points.get(0);
						poly.startPath(startPoint);
						for (int i = 1; i < points.size(); i++) {
							poly.lineTo(points.get(i));
						}
						msg.what = DRAW_POLYLIN;
						msg.obj = poly;
						MeasureHander.sendMessage(msg);
					} else if (points.size() == 1) {
						msg.what = DRAW_POINT;
						MeasureHander.sendMessage(msg);
					} else {
						msg.what = CLEAR_MAP;
						MeasureHander.sendMessage(msg);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	void doLengthCompute() {

		new Thread(new Runnable() {

			final ArrayList<Point> points = pointList;

			public void run() {
				// TODO Auto-generated method stub
				try {
					final MultiPath poly;
					Message msg = MeasureHander.obtainMessage();
					Point startPoint = null;
					if (points.size() > 1) {
						poly = new Polyline();
						startPoint = points.get(0);
						poly.startPath(startPoint);
						for (int i = 1; i < points.size(); i++) {
							poly.lineTo(points.get(i));
						}

						new Thread(new Runnable() {
							public void run() {
								// TODO Auto-generated method stub
								Message msg = MeasureHander.obtainMessage();
								// long num = Math.abs(Math.round(poly
								// .calculateLength2D()));
								Geometry p = GeometryEngine.project(
										poly,
										SpatialReference
												.create(SpatialReference.WKID_WGS84),
										SpatialReference
												.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
								long num = Math.abs(Math.round(p
										.calculateLength2D()));

								msg.what = SHOW_LENGTH;
								msg.obj = num;
								MeasureHander.sendMessage(msg);
							}
						}).start();
						msg.what = DRAW_POLYLIN;
						msg.obj = poly;
						MeasureHander.sendMessage(msg);
					} else if (points.size() == 1) {
						msg.what = DRAW_POINT;
						MeasureHander.sendMessage(msg);
					} else {
						msg.what = CLEAR_MAP;
						MeasureHander.sendMessage(msg);
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	Handler MeasureHander = new Handler() {
		public void handleMessage(Message msg) {
			try {

				MultiPath poly = null;
				switch (msg.what) {
				case DRAW_POLYGON:
					poly = (MultiPath) msg.obj;
					measureLayer.removeAll();
					measureLayer.addGraphic(new Graphic(poly, fillSymbol));
					for (Point point : pointList) {
						measureLayer
								.addGraphic(new Graphic(point, pointSymbol));
					}
					break;

				case DRAW_POLYLIN:
					poly = (MultiPath) msg.obj;
					measureLayer.removeAll();
					measureLayer.addGraphic(new Graphic(poly, lineSymbol));

					for (Point point : pointList) {
						measureLayer
								.addGraphic(new Graphic(point, pointSymbol));
					}
					break;

				case DRAW_POINT:
					measureLayer.removeAll();
					numView.setText("");
					for (Point point : pointList) {
						measureLayer
								.addGraphic(new Graphic(point, pointSymbol));
					}
					break;

				case SHOW_AREA:
					String areaS = msg.obj.toString();
					float figureA = Float.valueOf(areaS);
					if (figureA < 1000000f)
						numView.setText(mFormat.format(figureA) + "平方米");
					else
						numView.setText(kmFormat.format(figureA / 1000000f)
								+ "平方千米");
					break;

				case SHOW_LENGTH:
					String lengthS = msg.obj.toString();
					float figureL = Float.valueOf(lengthS);
					if (figureL < 1000)
						numView.setText(mFormat.format(figureL) + "米");
					else
						numView.setText(kmFormat.format(figureL / 1000f) + "千米");
					break;

				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
}
