package com.chinadci.mel.mleo.ui.views.caseView2;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.map.dci.WGS84TiandituImageryLayer;
import com.chinadci.android.map.dci.WGS84TiandituMapLayer;
import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.core.LayerDepictInfo;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.activities.MapViewActivity;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;
import com.chinadci.mel.mleo.ui.popups.BaseLayerChooser;
import com.chinadci.mel.mleo.utils.CaseUtils;
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
 * @ClassName CaseView
 */
@SuppressWarnings("deprecation")
public class CaseView2 extends FrameLayout {

	View rootView;

	final int GETCURRENTLOCATION = 0x000011;
	MapView mapView;
	BaseLayerChooser layerChooser;
	GraphicsLayer graphicsLayer;
	ImageButton locationButton;
	ImageButton layerButton;
	ImageButton zoominButton;
	ImageButton zoomoutButton;
	TextView scaleView;
	OnStatusChangedListener mapStatusChangedListener;
	TextView ajxx;
	ImageView btnToGo;
	RelativeLayout maprr;
	TextView dh;

	ListView attriListView;

	TextView redlineView;
	TextView locationView;
	LinearLayout photoLayout;
	LinearLayout audioLayout;
	LinearLayout redlineLayout;
	GridView photoGridView;
	GridView audioGridView;
	ScrollView rootScroll;

	String redlineJson;
	String locationJson;
	String caseId = "";
	String caseTable = "";
	String annexTable = "";
	AttriAdapter attriAdapter;
	ImageGridAdapter imageGridAdapter;
	AudioGridAdapter audioGridAdapter;

	Activity parentActivity;

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

	public CaseView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public CaseView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CaseView2(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @Title: setCaseSource
	 */
	public void setDataSource(String id, String caseTable, String annexTable,String fxjg) {
		this.caseId = id;
		this.caseTable = caseTable;
		this.annexTable = annexTable;
		// display case detail info
		Log.i("guoteng", "CaseView2_caseId="+id+",caseTable="+caseTable+",annexTable="+annexTable+",fxjg="+fxjg);
		viewCaseInfo(this.caseId, this.caseTable, this.annexTable,fxjg);
	}

	/**
	 * @Title: setParentActivity
	 */
	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public GraphicsLayer getGraphicsLayer() {
		return graphicsLayer;
	}

	public void setGraphicsLayer(GraphicsLayer graphicsLayer) {
		this.graphicsLayer = graphicsLayer;
	}

	/**
	 * @Title: initView
	 */
	void initView(Context context) {
		imageGridAdapter = new ImageGridAdapter(context,
				new ArrayList<String>(), imageClickListener, View.GONE,
				R.layout.view_photo, R.id.view_photo_photo,
				R.id.view_photo_delete);
		audioGridAdapter = new AudioGridAdapter(context,
				new ArrayList<String>(), audioClickListener, View.GONE,
				R.layout.view_audio, R.id.view_audio_index,
				R.id.view_audio_length, R.id.view_audio_delete);

		rootView = LayoutInflater.from(context).inflate(R.layout.view_case2,
				null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		attriListView = (ListView) rootView
				.findViewById(R.id.view_case_listview);
		redlineView = (TextView) rootView.findViewById(R.id.view_case_redline);
		locationView = (TextView) rootView
				.findViewById(R.id.view_case_location);
		rootScroll = (ScrollView) rootView.findViewById(R.id.root_scroll);
		photoGridView = (GridView) rootView
				.findViewById(R.id.view_media_photogrid);
		audioGridView = (GridView) rootView
				.findViewById(R.id.view_media_audiolist);
		photoLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_photolayout);
		audioLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_audiolayout);
		redlineLayout = (LinearLayout) rootView
				.findViewById(R.id.view_case_redline_pad);
		locationView.setOnClickListener(commandClickListener);
		redlineView.setOnClickListener(commandClickListener);

		photoGridView.setAdapter(imageGridAdapter);
		audioGridView.setAdapter(audioGridAdapter);

		ajxx = (TextView) rootView.findViewById(R.id.ajxx);
		ajxx.setText("");
		btnToGo = (ImageView)rootView.findViewById(R.id.btn_togo);
		maprr = (RelativeLayout) rootView.findViewById(R.id.maprr);
		maprr.setVisibility(View.GONE);
		dh = (TextView)rootView.findViewById(R.id.view_case_dh);
		mapView = (com.esri.android.map.MapView) rootView
				.findViewById(R.id.view_map_mapview);
		mapView.setMapBackground(0xffffff, 0xffffff, 0, 0);// 设置背景色
		mapView.setEsriLogoVisible(false);// 打开或关闭地图上的ESRI的logo标签
		mapView.setAllowRotationByPinch(false);// 设置是否允许地图通过pinch方式旋转；
		mapView.setRotationAngle(0.0);// 设置地图的旋转角度；
		graphicsLayer = new GraphicsLayer();
		mapView.addLayer(graphicsLayer);
		mapView.setOnZoomListener(zoomListener);
		initLayerChooser();
		locationButton = (ImageButton) rootView
				.findViewById(R.id.view_map_location);
		layerButton = (ImageButton) rootView.findViewById(R.id.view_map_layer);
		zoominButton = (ImageButton) rootView
				.findViewById(R.id.view_map_zoomin);
		zoomoutButton = (ImageButton) rootView
				.findViewById(R.id.view_map_zoomout);
		scaleView = (TextView) rootView.findViewById(R.id.view_map_scale);

		zoominButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (mapView.getResolution() > mapView.getMinResolution()) {
					mapView.zoomin();
				}
			}
		});
		zoomoutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (mapView.getResolution() < mapView.getMaxResolution()) {
					mapView.zoomout();
				}
			}
		});
		layerButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!layerChooser.isShowing())
					layerChooser.showAsDropDown(v);
			}
		});
		locationButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				locateCurrentPosition();
			}
		});
		mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {
				if (source == mapView && status == STATUS.INITIALIZED) {
					Log.i("guoteng","CaseView2_onStatusChanged");
					LocationDisplayManager ls = mapView.getLocationDisplayManager();
					ls.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
					ls.start();
					if (mapStatusChangedListener != null)
						mapStatusChangedListener
								.onStatusChanged(source, status);

				}
			}
		});
		mapView.getChildAt(0).setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// 允许ScrollView截断点击事件，ScrollView可滑动
					rootScroll.requestDisallowInterceptTouchEvent(false);
				} else {
					// 不允许ScrollView截断点击事件，点击事件由子View处理
					rootScroll.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});
	}

	public void setBtnToGoClickListener(View.OnClickListener lis){
		btnToGo.setOnClickListener(lis);
		dh.setOnClickListener(lis);
	}
	
	public void setAjxxText(String text) {
		ajxx.setText(text);
	}

	public void clearGraphicsLayer() {
		graphicsLayer.removeAll();
	}

	/**
	 * @Title: viewCaseInfo
	 */
	// void viewCaseInfo(String id, String cTable, String aTable) {
	// try {
	// String columns[] = new String[] { InspectionCaseTable.field_id,
	// InspectionCaseTable.field_bh, InspectionCaseTable.field_address,
	// InspectionCaseTable.field_illegalSubject,
	// InspectionCaseTable.field_illegalArea,
	// InspectionCaseTable.field_illegalStatus,
	// InspectionCaseTable.field_illegalType,
	// InspectionCaseTable.field_landUsage,
	// InspectionCaseTable.field_location, InspectionCaseTable.field_notes,
	// InspectionCaseTable.field_parties, InspectionCaseTable.field_redline,
	// InspectionCaseTable.field_mTime, InspectionCaseTable.field_source,
	// InspectionCaseTable.field_analysis,
	// InspectionCaseTable.field_user };
	// String selection = InspectionCaseTable.field_id + "=?";
	// String args[] = new String[] { id };
	// ContentValues caseInfo =
	// DBHelper.getDbHelper(getContext()).doQuery(cTable, columns,
	// selection, args);
	//
	// if (caseInfo != null) {
	// String values[] = new String[13];
	// String keys[] = new String[13];
	//
	// values[0] = caseInfo.getAsString(InspectionCaseTable.field_bh);
	// values[1] = caseInfo.getAsString(InspectionCaseTable.field_source);
	// values[2] =
	// caseInfo.getAsString(InspectionCaseTable.field_illegalSubject);
	// values[3] = caseInfo.getAsString(InspectionCaseTable.field_parties);
	// values[4] = caseInfo.getAsString(InspectionCaseTable.field_address);
	// values[5] = caseInfo.getAsString(InspectionCaseTable.field_landUsage);
	// values[6] = caseInfo.getAsString(InspectionCaseTable.field_illegalType);
	// values[7] =
	// caseInfo.getAsString(InspectionCaseTable.field_illegalStatus);
	// values[8] = caseInfo.getAsString(InspectionCaseTable.field_illegalArea);
	// values[9] = caseInfo.getAsString(InspectionCaseTable.field_notes);
	// values[10] = caseInfo.getAsString(InspectionCaseTable.field_user);
	// values[11] = caseInfo.getAsString(InspectionCaseTable.field_mTime);
	// values[12] = caseInfo.getAsString(InspectionCaseTable.field_analysis);
	//
	// keys[0] = getContext().getString(R.string.v_f23);
	// keys[1] = getContext().getString(R.string.v_f1);
	// keys[2] = getContext().getString(R.string.v_f2);
	// keys[3] = getContext().getString(R.string.v_f3);
	// keys[4] = getContext().getString(R.string.v_f5);
	// keys[5] = getContext().getString(R.string.v_f7);
	// keys[6] = getContext().getString(R.string.v_f8);
	// keys[7] = getContext().getString(R.string.v_f9);
	// keys[8] = getContext().getString(R.string.v_f10) + "(m²)";
	// keys[9] = getContext().getString(R.string.v_f11);
	// keys[10] = getContext().getString(R.string.v_f24);
	// keys[11] = getContext().getString(R.string.v_f25);
	// keys[12] = getContext().getString(R.string.v_f43);
	//
	// attriAdapter = new AttriAdapter(getContext(), keys, values);
	// attriListView.setAdapter(attriAdapter);
	// attriAdapter.notifyDataSetChanged();
	//
	// redlineJson = caseInfo.getAsString(InspectionCaseTable.field_redline);
	// locationJson = caseInfo.getAsString(InspectionCaseTable.field_location);
	// if (redlineJson != null && !redlineJson.equals(""))
	// redlineLayout.setVisibility(View.VISIBLE);
	// else
	// redlineLayout.setVisibility(View.GONE);
	//
	// String annexFields[] = new String[] { CaseAnnexesTable.field_path };
	// String annexSelection = new StringBuffer(CaseAnnexesTable.field_caseId)
	// .append("=? and ").append(CaseAnnexesTable.field_tag).append("=?")
	// .toString();
	// String annexArgs[] = new String[] { caseId, cTable };
	// ArrayList<ContentValues> annexList =
	// DBHelper.getDbHelper(getContext()).doQuery(
	// aTable, annexFields, annexSelection, annexArgs, null, null, null, null);
	// if (annexList != null && annexList.size() > 0) {
	// for (ContentValues annex : annexList) {
	// String path = annex.getAsString(CaseAnnexesTable.field_path);
	// String ext = path.substring(path.lastIndexOf("."));
	// String type = MIMEMapTable.getInstance().getParentMIMEType(ext);
	// if (type.equals("audio/*")) {
	// // load this annex as a image
	// audioGridAdapter.addItem(path);
	// if (audioLayout.getVisibility() != View.VISIBLE)
	// audioLayout.setVisibility(View.VISIBLE);
	// } else if (type.equals("image/*")) {
	// // load this annex as a audio
	// imageGridAdapter.addItem(path);
	// if (photoLayout.getVisibility() != View.VISIBLE)
	// photoLayout.setVisibility(View.VISIBLE);
	// }
	// }
	// }
	// imageGridAdapter.notifyDataSetChanged();
	// audioGridAdapter.notifyDataSetChanged();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	void viewCaseInfo(String id, String cTable, String aTable,String fxjg) {
		try {
			String columns[] = new String[] { InspectionCaseTable.field_id,
					InspectionCaseTable.field_bh,
					InspectionCaseTable.field_address,
					InspectionCaseTable.field_illegalSubject,
					InspectionCaseTable.field_illegalArea,
					InspectionCaseTable.field_illegalStatus,
					InspectionCaseTable.field_illegalType,
					InspectionCaseTable.field_landUsage,
					InspectionCaseTable.field_location,
					InspectionCaseTable.field_notes,
					InspectionCaseTable.field_parties,
					InspectionCaseTable.field_redline,
					InspectionCaseTable.field_mTime,
					InspectionCaseTable.field_source,
					InspectionCaseTable.field_analysis,
					InspectionCaseTable.field_user };
			String selection = InspectionCaseTable.field_id + "=?";
			String args[] = new String[] { id };
			ContentValues caseInfo = DBHelper.getDbHelper(getContext())
					.doQuery(cTable, columns, selection, args);

			if (caseInfo != null) {
				String values[] = new String[13];
				String keys[] = new String[13];

				values[0] = caseInfo.getAsString(InspectionCaseTable.field_bh);
				values[1] = caseInfo
						.getAsString(InspectionCaseTable.field_source);
				values[2] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalSubject);
				values[3] = caseInfo
						.getAsString(InspectionCaseTable.field_parties);
				values[4] = caseInfo
						.getAsString(InspectionCaseTable.field_address);
				values[5] = caseInfo
						.getAsString(InspectionCaseTable.field_landUsage);
				values[6] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalType);
				values[7] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalStatus);
				values[8] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalArea);
				values[9] = caseInfo
						.getAsString(InspectionCaseTable.field_notes);
				values[10] = caseInfo
						.getAsString(InspectionCaseTable.field_user);
				values[11] = caseInfo
						.getAsString(InspectionCaseTable.field_mTime);
//				values[12] = caseInfo
//						.getAsString(InspectionCaseTable.field_analysis);
				values[12] = fxjg;

				keys[0] = getContext().getString(R.string.v_f23);
				keys[1] = getContext().getString(R.string.v_f1);
				keys[2] = getContext().getString(R.string.v_f2);
				keys[3] = getContext().getString(R.string.v_f3);
				keys[4] = getContext().getString(R.string.v_f5);
				keys[5] = getContext().getString(R.string.v_f7);
				keys[6] = getContext().getString(R.string.v_f8);
				keys[7] = getContext().getString(R.string.v_f9);
				keys[8] = getContext().getString(R.string.v_f10) + "(m²)";
				keys[9] = getContext().getString(R.string.v_f11);
				keys[10] = getContext().getString(R.string.v_f24);
				keys[11] = getContext().getString(R.string.v_f25);
				keys[12] = getContext().getString(R.string.v_f43);

				attriAdapter = new AttriAdapter(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();

				redlineJson = caseInfo
						.getAsString(InspectionCaseTable.field_redline);
				locationJson = caseInfo
						.getAsString(InspectionCaseTable.field_location);
				if (redlineJson != null && !redlineJson.equals(""))
					redlineLayout.setVisibility(View.VISIBLE);
				else
					redlineLayout.setVisibility(View.GONE);
				new annexGetTask(getContext(), id, cTable, aTable).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void showRedline() {
		Log.i("guoteng","showRedLine_CaseView2");
		if (redlineJson != null && !redlineJson.equals("")) {
			String geoArrayString = "[" + redlineJson + "]";
			Intent intent = new Intent(parentActivity, MapViewActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Parameters.GEOMETRY, geoArrayString);
			intent.putExtra(Parameters.ACTIVITY_TITLE, "案件红线");
			parentActivity.startActivity(intent);
			parentActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	}

	/**
	 * 
	 */
	OnClickListener commandClickListener = new OnClickListener() {

		public void onClick(View v) {
			int vid = v.getId();
			switch (vid) {
			// show case's location in map view
			case R.id.view_case_redline:
				showRedline();
				break;

			// show redline in map view
			case R.id.view_case_location:

				break;

			default:
				break;
			}

		}
	};
	/**
	 * 
	 */
	IClickListener audioClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			String path = o.toString();

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			File f = new File(path);
			Uri mUri = Uri.fromFile(f);
			intent.setDataAndType(mUri, "audio/*");
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);

			return null;
		}
	};

	/**
	 * 
	 */
	IClickListener imageClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			String path = o.toString();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			Uri mUri = Uri.parse("file://" + path);
			intent.setDataAndType(mUri, "image/*");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			getContext().startActivity(intent);
			return null;
		}
	};

	class annexGetTask extends AsyncTask<Void, String, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String cTable;
		String aTable;
		ArrayList<ContentValues> annexList;

		public annexGetTask(Context c, String caseId, String cTable,
				String aTable) {
			this.context = c;
			this.caseId = caseId;
			this.cTable = cTable;
			this.aTable = aTable;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String annexFields[] = new String[] {
						CaseAnnexesTable.field_path, CaseAnnexesTable.field_uri };
				String annexSelection = new StringBuffer(
						CaseAnnexesTable.field_caseId).append("=? and ")
						.append(CaseAnnexesTable.field_tag).append("=?")
						.toString();
				String annexArgs[] = new String[] { caseId, cTable };
				annexList = DBHelper.getDbHelper(context).doQuery(aTable,
						annexFields, annexSelection, annexArgs, null, null,
						null, null);
				if (annexList != null && annexList.size() > 0) {
					for (ContentValues annex : annexList) {
						String path = annex
								.getAsString(CaseAnnexesTable.field_path);
						String uri = annex
								.getAsString(CaseAnnexesTable.field_uri);
						File file = new File(path);
						if (file.exists()) {
							publishProgress(path);
						} else {
							CaseUtils.getInstance().downAnnex(path, uri);
							publishProgress(path);
						}
					}
					return true;
				} else {
					msg = "上报信息无附件";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//msg = "服务器返回异常";
			return false;
		}

		@Override
		protected void onProgressUpdate(String... paths) {
			String path = paths[0];
			String ext = path.substring(path.lastIndexOf("."));
			String type = "";
			try {
				type = MIMEMapTable.getInstance().getParentMIMEType(ext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (type.equals("audio/*")) {
				// load this annex as a image
				audioGridAdapter.addItem(path);
				if (audioLayout.getVisibility() != View.VISIBLE)
					audioLayout.setVisibility(View.VISIBLE);
				audioGridAdapter.notifyDataSetChanged();
			} else if (type.equals("image/*")) {
				// load this annex as a audio
				imageGridAdapter.addItem(path);
				if (photoLayout.getVisibility() != View.VISIBLE)
					photoLayout.setVisibility(View.VISIBLE);
				imageGridAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result&&!"".equals(msg)) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public MapView getMapView() {
		return mapView;
	}

	public void mapPause() {
		mapView.pause();
	}

	public void mapUnPause() {
		mapView.unpause();
	}

	OnZoomListener zoomListener = new OnZoomListener() {
		private static final long serialVersionUID = 1L;

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

	public void viewMapScale(Double s) {
		try {
			Double scale = mapView.getScale();
			if (s != null)
				scale = s;
			String str = Double.toString(scale);
			String sScale = str.substring(0, str.indexOf("."));
			Log.i("viewMapScale.scale", sScale);
			if (scaleView != null) {
				scaleView.setText(new StringBuffer("1:").append(sScale)
						.toString());
				Log.i("viewMapScale.scale", "true");
			}
		} catch (Exception e) {
			Log.i("viewMapScale.scale", e.toString());
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

	private void locateCurrentPosition() {
		int i_cn_gettinglocation = getContext().getResources().getIdentifier(
				getContext().getPackageName() + ":string/"
						+ "cn_gettinglocation", null, null);
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
			Toast.makeText(getContext(), "请开启GPS定位...", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GETCURRENTLOCATION:
				Log.i("guoteng","CaseView2_handleMessage");
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

	public void setMapStatusChangedListener(OnStatusChangedListener listener) {
		this.mapStatusChangedListener = listener;
	}
}
