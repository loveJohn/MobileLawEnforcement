package com.chinadci.mel.mleo.ui.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.map.dci.WGS84TiandituImageryLayer;
import com.chinadci.android.map.dci.WGS84TiandituMapLayer;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.LayerDepictInfo;
import com.chinadci.mel.mleo.ui.fragments.data.model.Poi;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetAdressTask;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.popups.BaseLayerChooser;
import com.chinadci.mel.mleo.utils.DisplayUtil;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

/**
 * @ClassName MleoMapView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:48
 * 
 */
@SuppressWarnings("deprecation")
public class MleoMapView_Adress extends ViewGroup {
	final int GETCURRENTLOCATION = 0x000001;
	View rootView;
	RelativeLayout root;
	ImageButton measureButton;
	ImageButton locationButton;
	ImageButton layerButton;
	ImageButton zoominButton;
	ImageButton zoomoutButton;
	TextView scaleView;
	protected MapView mapView;
	protected LinearLayout tabLayout;
	LinearLayout zoombar;

	protected MeasureBar measureBar;
	BaseLayerChooser layerChooser;
	GraphicsLayer graphicsLayer;
	OnStatusChangedListener mapStatusChangedListener;
	Boolean stabLayout = false;
	int dci_appid = -1;
	int i_view_map = -1;
	int i_view_map_root = -1;
	int i_view_map_tablayout = -1;
	int i_view_map_location = -1;
	int i_view_map_layer = -1;
	int i_view_map_measure = -1;
	int i_view_map_zoomin = -1;
	int i_view_map_zoomout = -1;
	int i_view_map_zoombar = -1;
	int i_view_map_mapview = -1;
	int view_map_scale = -1;

	private final double level8 = 4622333.678977588;
	private final double level20 = 1128.4994333441375;

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

	ClearEditText clearEditText;

//	private PopupWindow mPopupWindow;
	private LinearLayout linearLayout;
	private RelativeLayout relativeLayout;
	
	public void setVectorTiledLayer(TiledServiceLayer layer,
			TiledServiceLayer layer_) {
		this.vectorTiledLayer = layer;
		this.vectorTiledLayer_ = layer_;
		if (layerChooser != null)
			layerChooser.setVectorTiledLayer(layer, layer_);
	}

	public void setImageryLayer(TiledServiceLayer layer,
			TiledServiceLayer layer_) {
		this.imageryTiledLayer = layer;
		this.imageryTiledLayer_ = layer_;
		if (layerChooser != null)
			layerChooser.setImageryLayer(layer, layer_);
	}

	public MapView getMapView() {
		return mapView;
	}

	public void setMapStatusChangedListener(OnStatusChangedListener listener) {
		this.mapStatusChangedListener = listener;
	}

	public MleoMapView_Adress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public MleoMapView_Adress(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MleoMapView_Adress(Context context) {
		super(context);
		initView(context);
	}

	public void mapPause() {
		mapView.pause();
	}

	public void mapUnPause() {
		mapView.unpause();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		rootView.layout(l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(widthSpecSize, heightSpecSize);
		View child = getChildAt(0);
		child.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	protected void initView(Context c) {
		dci_appid = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "dci_appid", null,
				null);
		i_view_map = c.getResources()
				.getIdentifier(
						c.getPackageName() + ":layout/" + "view_map_adress",
						null, null);
		i_view_map_root = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_root", null, null);
		i_view_map_tablayout = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_tablayout", null, null);
		i_view_map_location = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_location", null, null);
		i_view_map_layer = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_layer", null, null);

		i_view_map_measure = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_measure", null, null);
		i_view_map_zoomin = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_zoomin", null, null);
		i_view_map_zoomout = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_zoomout", null, null);
		i_view_map_zoombar = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_zoombar", null, null);
		i_view_map_mapview = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_mapview", null, null);
		view_map_scale = c.getResources().getIdentifier(
				c.getPackageName() + ":id/" + "view_map_scale", null, null);

		rootView = LayoutInflater.from(c).inflate(i_view_map, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		root = (RelativeLayout) rootView.findViewById(i_view_map_root);

		tabLayout = (LinearLayout) rootView.findViewById(i_view_map_tablayout);

		locationButton = (ImageButton) rootView.findViewById(i_view_map_location);

		clearEditText = (ClearEditText) rootView.findViewById(R.id.filter_edit);
		linearLayout = (LinearLayout)rootView.findViewById(R.id.wr_areas);
		relativeLayout = (RelativeLayout)rootView.findViewById(R.id.lll);
		relativeLayout.setVisibility(View.GONE);

		layerButton = (ImageButton) rootView.findViewById(i_view_map_layer);
		measureButton = (ImageButton) rootView.findViewById(i_view_map_measure);
		zoominButton = (ImageButton) rootView.findViewById(i_view_map_zoomin);
		zoomoutButton = (ImageButton) rootView.findViewById(i_view_map_zoomout);
		zoombar = (LinearLayout) rootView.findViewById(i_view_map_zoombar);
		mapView = (com.esri.android.map.MapView) rootView.findViewById(i_view_map_mapview);
		scaleView = (TextView) rootView.findViewById(view_map_scale);
		mapView.addLayer(graphicsLayer);

		initLayerChooser();
		measureBar = new MeasureBar(c);
		mapView.setOnZoomListener(zoomListener);
		zoominButton.setOnClickListener(clickListener);
		zoomoutButton.setOnClickListener(clickListener);
		layerButton.setOnClickListener(clickListener);
		measureButton.setOnClickListener(clickListener);
		locationButton.setOnClickListener(clickListener);
		clearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				doSearch(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		measureBar.setShutClickListener(new OnClickListener() {
			public void onClick(View v) {
				showMeasureBar();
			}
		});

		mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {
				if (source == mapView && status == STATUS.INITIALIZED) {
					LocationDisplayManager ls = mapView.getLocationDisplayManager();
					ls.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
					ls.start();
					if (mapStatusChangedListener != null)
						mapStatusChangedListener
								.onStatusChanged(source, status);

				}
			}
		});
		mapView.setEsriLogoVisible(false);// 打开或关闭地图上的ESRI的logo标签
		mapView.setAllowRotationByPinch(false);// 设置是否允许地图通过pinch方式旋转；
		mapView.setRotationAngle(0.0);// 设置地图的旋转角度；
		mapView.setMinScale(level8);// 最小比例尺5
		mapView.setMaxScale(level20);// 最大比例尺16
	}

	protected void doSearch(String string) {
		GetAdressTask task = new GetAdressTask(getContext(),
				new TaskResultHandler<List<Poi>>() {
					@Override
					public void resultHander(List<Poi> result) {
						if (result != null && result.size() > 0) {
							relativeLayout.setVisibility(VISIBLE);
							linearLayout.removeAllViews();
							View view0 = new View(getContext());
			                view0.setLayoutParams(new LinearLayout.LayoutParams(
			                        LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), 1)));
			                view0.setBackgroundColor(Color.parseColor("#999999"));
			                linearLayout.addView(view0);
							for (int i = 0; i < result.size(); i++) {
								View item = LayoutInflater.from(getContext()).inflate(R.layout.wr_text_item, null);
				                final TextView nameTxt = (TextView) item
				                        .findViewById(R.id.name);
				                nameTxt.setText(result.get(i).getNAME());
				                final double lng = result.get(i).getLON();
				                final double lat = result.get(i).getLAT();
				                nameTxt.setOnClickListener(new View.OnClickListener() {
				                    @Override
				                    public void onClick(View v) {
//				                    	clearEditText.setText(nameTxt.getText().toString());
				                        Point point = new Point(lng,lat);
										if (mapView.getSpatialReference().isAnyWebMercator()) {
											Point mercatorPoint = (Point) GeometryEngine
													.project(
															point,
															SpatialReference
																	.create(SpatialReference.WKID_WGS84),
															SpatialReference
																	.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
											showMapLocation(mercatorPoint);
										} else if (mapView.getSpatialReference().isWGS84()) {
											showMapLocation(point);
										} else {
											Toast.makeText(getContext(), "位置坐标转换失败",
													Toast.LENGTH_SHORT).show();
										}
				                    	relativeLayout.setVisibility(GONE);
				                    	closeKeyboard();
				                    }
				                });
				                linearLayout.addView(item);
				                View view = new View(getContext());
				                view.setLayoutParams(new LinearLayout.LayoutParams(
				                        LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), 1)));
				                view.setBackgroundColor(Color.parseColor("#999999"));
				                linearLayout.addView(view);
							}
						}else{
							relativeLayout.setVisibility(GONE);
						}
					}
				});
		task.execute(string);
	}
	
	private void closeKeyboard() {
        View view = ((Activity)getContext()).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

	public void viewMapScale(Double s) {
		try {
			Double scale = mapView.getScale();
			if (s != null)
				scale = s;
			String str = Double.toString(scale);
			String sScale = str.substring(0, str.indexOf("."));
			if (scaleView != null) {
				scaleView.setText(new StringBuffer("1:").append(sScale)
						.toString());
			}

		} catch (Exception e) {
			Log.i("ydzf", e.toString());
		}
	}

	private void initLayerChooser() {
		ArrayList<LayerDepictInfo> infos = new ArrayList<LayerDepictInfo>();
		int i_cn_bvlayer = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "cn_bvlayer",
				null, null);
		int i_file_bvlayer = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "file_bvlayer",
				null, null);
		int i_ic_bvlayer = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":drawable/" + "ic_bvlayer",
				null, null);

		LayerDepictInfo bvInfo = new LayerDepictInfo(getContext(),
				i_cn_bvlayer, WGS84TiandituMapLayer.URL, i_file_bvlayer,
				i_ic_bvlayer);

		int i_cn_rslayer = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "cn_rslayer",
				null, null);
		int i_file_rslayer = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "file_rslayer",
				null, null);
		int i_ic_rslayer = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":drawable/" + "ic_rslayer",
				null, null);

		LayerDepictInfo rsInfo = new LayerDepictInfo(getContext(),
				i_cn_rslayer, WGS84TiandituImageryLayer.URL, i_file_rslayer,
				i_ic_rslayer);
		infos.add(bvInfo);
		infos.add(rsInfo);
		layerChooser = new BaseLayerChooser(getContext(), 10, 10, mapView,
				infos);

		if (vectorTiledLayer != null && vectorTiledLayer_ != null)
			layerChooser.setVectorTiledLayer(vectorTiledLayer,
					vectorTiledLayer_);
		if (imageryTiledLayer != null && imageryTiledLayer_ != null)
			layerChooser.setImageryLayer(imageryTiledLayer, imageryTiledLayer_);

		int i_rightlayerAnimation = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":style/"
						+ "rightlayerAnimation", null, null);
		int i_bg_popup_rt = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":drawable/" + "bg_popup_rt",
				null, null);
		layerChooser.setAnimationStyle(i_rightlayerAnimation);
		layerChooser.setBackgroundDrawable(getContext().getResources()
				.getDrawable(i_bg_popup_rt));
		layerChooser.setFocusable(true);
		layerChooser.setOutsideTouchable(false);
		layerChooser.update();
	}

	protected void setLocationVisibility(int v) {
		locationButton.setVisibility(v);
	}

	protected void setLayerVisibility(int v) {
		layerButton.setVisibility(v);
	}

	protected void setMeasureVisibility(int v) {
		measureButton.setVisibility(v);
	}

	protected void setZoombarVisibility(int v) {
		locationButton.setVisibility(v);
	}

	protected void showMeasureBar() {
		if (!measureBar.isShow()) {
			measureBar.show(tabLayout, 0, mapView);
		} else {
			measureBar.shut();
		}
	}

	private void locateCurrentPosition() {

		int i_cn_gettinglocation = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/"
						+ "cn_gettinglocation", null, null);
		int i_cn_prompt = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "cn_prompt", null,
				null);
		int i_cn_gpsoff = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "cn_gpsoff", null,
				null);
		int i_cn_opengps = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/" + "cn_opengps",
				null, null);

		int i_cn_cancellocation = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/"
						+ "cn_cancellocation", null, null);

		if (LocationUtils.isGPSSupport(getContext())) {
			Toast.makeText(getContext(),
					getContext().getString(i_cn_gettinglocation),
					Toast.LENGTH_SHORT).show();
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(200);
						handler.sendEmptyMessage(GETCURRENTLOCATION);
					} catch (Exception e) {
					}
				}
			}).start();

		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setCancelable(true)
					.setTitle(getContext().getString(i_cn_prompt))
					.setMessage(getContext().getString(i_cn_gpsoff))
					.setPositiveButton(getContext().getString(i_cn_opengps),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Intent callGPSSettingIntent = new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									getContext().startActivity(
											callGPSSettingIntent);
								}
							})
					.setNegativeButton(
							getContext().getString(i_cn_cancellocation),
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

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GETCURRENTLOCATION:

				Location loc = mapView.getLocationDisplayManager().getLocation();
				if (loc == null)
					loc = LocationUtils.getGPSLocation(getContext());

				if (loc == null) {
					Toast.makeText(getContext(), "没有获取到你的位置",
							Toast.LENGTH_SHORT).show();
				} else {
					Point point = new Point(loc.getLongitude(),
							loc.getLatitude());
					if (mapView.getSpatialReference().isAnyWebMercator()) {
						Point mercatorPoint = (Point) GeometryEngine
								.project(
										point,
										SpatialReference
												.create(SpatialReference.WKID_WGS84),
										SpatialReference
												.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
						showMapLocation(mercatorPoint);
					} else if (mapView.getSpatialReference().isWGS84()) {
						showMapLocation(point);
					} else {
						Toast.makeText(getContext(), "位置坐标转换失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			default:
				break;
			}

		};
	};

	private void showMapLocation(Point p) {
		if (p == null)
			return;
		if (mapView.getScale() > getContext().getResources()
				.getDisplayMetrics().density * 20000d)
			mapView.zoomToScale(p, getContext().getResources()
					.getDisplayMetrics().density * 20000d);
		else
			mapView.centerAt(p, true);
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vid = v.getId();

			if (vid == i_view_map_layer) {
				if (!layerChooser.isShowing())
					layerChooser.showAsDropDown(v);
			} else if (vid == i_view_map_location) {
				locateCurrentPosition();
			}

			else if (vid == i_view_map_location) {
				locateCurrentPosition();
			} else if (vid == i_view_map_measure) {
				showMeasureBar();
			} else if (vid == i_view_map_zoomin) {
				if (mapView.getResolution() > mapView.getMinResolution()) {
					mapView.zoomin();
				}
			} else if (vid == i_view_map_zoomout) {
				if (mapView.getResolution() < mapView.getMaxResolution()) {
					mapView.zoomout();
				}
			}

		}
	};

	OnZoomListener zoomListener = new OnZoomListener() {
		public void preAction(float arg0, float arg1, double arg2) {
		}

		public void postAction(float arg0, float arg1, double arg2) {
			viewMapScale(null);
			if (mapView.getResolution() == mapView.getMinResolution()) {
				zoominButton.setEnabled(false);
				Toast.makeText(getContext(), "地图已缩放到最大级别", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (!zoominButton.isEnabled()) {
					zoominButton.setEnabled(true);
				}
			}

			if (mapView.getResolution() == mapView.getMaxResolution()) {
				zoomoutButton.setEnabled(false);
				Toast.makeText(getContext(), "地图已缩放到最小级别", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (!zoomoutButton.isEnabled()) {
					zoomoutButton.setEnabled(true);
				}
			}
		}
	};
}
