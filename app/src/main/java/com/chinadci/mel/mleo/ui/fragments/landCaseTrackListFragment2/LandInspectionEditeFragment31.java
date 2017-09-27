/*
 * 土地执法》案件核查（快速处置）编辑3
 */
package com.chinadci.mel.mleo.ui.fragments.landCaseTrackListFragment2;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.andbase.library.http.entity.MultipartEntity;
import com.andbase.library.http.entity.mine.content.ContentBody;
import com.andbase.library.http.entity.mine.content.FileBody;
import com.andbase.library.http.entity.mine.content.StringBody;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import com.chinadci.mel.mleo.core.OutsideManifestHandler;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.activities.PolyGatherActivity;
import com.chinadci.mel.mleo.ui.activities.TapeActivity;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.landCaseViewpagerFragment2.LandCaseViewpagerFragment2;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.Gps;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.MapUtil;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.MyLocationListener;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.PositionUtil;
import com.chinadci.mel.mleo.ui.views.caseView2.CaseView3;
import com.chinadci.mel.mleo.ui.views.inspectionEditeView2.InspectionEditeView2;
import com.chinadci.mel.mleo.ui.views.inspectionView2.InspectionView2;
import com.chinadci.mel.mleo.ui.views.patrolEditeView2.PatrolEditeView31;
import com.chinadci.mel.mleo.utils.AMapUtil;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.runtime.LicenseResult;

@SuppressLint("SimpleDateFormat")
public class LandInspectionEditeFragment31 extends LandCaseViewpagerFragment2 {

	private LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private String geoJson;
	private String fxjg;
	private double x;
	private double y;
	private String ajly;
	private String dz;
	private String jcbh;
	private String jcmj;
	private String zygdmj;
	private String xfsj;
	private final DecimalFormat degreeFormat4Location = new DecimalFormat(
			"##0.0000000");
	private Gps zd_bd09;
	private Gps qd_bd09;

	private String aj_id;
	private String xzqh_id;

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

	String caseId;

	private static final String CLIENT_ID = "fWjgn6RQYiqLZQgb";
	CaseView3 caseInfoView;
	InspectionView2 inspectionView;
	InspectionEditeView2 inspectionEditeView;
	PatrolEditeView31 patrolEditeView;

	AlertDialog alertDialog;

	@Override
	public void onResume() {
		super.onResume();
		caseInfoView.mapUnPause();
	}

	@Override
	public void onPause() {
		super.onPause();
		caseInfoView.mapPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		// photo
		case Parameters.GET_PHOTO:
			Bundle photoBundle = data.getExtras();
			String photos[] = photoBundle
					.getStringArray(CameraPhotoActivity.PHOTOARRAY);
			patrolEditeView.addPhotoPaths(photos);
			break;
		case Parameters.PICK_IMAGE:
			try {
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				@SuppressWarnings("deprecation")
				Cursor cursor = getActivity().managedQuery(uri, proj, null,
						null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String path = cursor.getString(column_index);
				String photoss[] = {path};
				patrolEditeView.addPhotoPaths(photoss);
			} catch (Exception e) {
			}
			break;
		// audio
		case Parameters.GET_AUDIO:
			Bundle audioBundle = data.getExtras();
			String amrPath = audioBundle.getString(TapeActivity.AMRFILE);
			patrolEditeView.addAudioPath(amrPath);
			break;
		// redline
		case Parameters.GET_REDLINE:
			Bundle redlineBundle = data.getExtras();
			String redlineString = redlineBundle.getString(
					PolyGatherActivity.REDLINE).toString();
			patrolEditeView.setRedline(redlineString);
			break;
		default:
			break;
		}
	}

	@Override
	public void handle(Object o) {
		super.handle(o);
		int tag = (Integer) o;
		if (tag == 0) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setMessage("确认要保存吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							new inspectionSaveTask(false).execute();
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else if (tag == 1) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setMessage("确认要发送吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							String patrolCheckMsg = patrolEditeView
									.checkInput();
							if (patrolCheckMsg == null) {
								if (inspectionEditeView != null) {
									String inspectionCheckMsg = inspectionEditeView
											.checkInput();
									if (inspectionCheckMsg == null) {
										new inspectionSaveTask(true).execute();
										new inspectionSendTask2().execute();
									} else {
										Toast.makeText(context,
												inspectionCheckMsg,
												Toast.LENGTH_SHORT).show();
									}
								} else {
									new inspectionSaveTask(true).execute();
									new inspectionSendTask2().execute();
								}
							} else {
								Toast.makeText(context, patrolCheckMsg,
										Toast.LENGTH_SHORT).show();
							}
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else if (tag == 2) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setMessage("确认要开始响应吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (LocationUtils.isGPSSupport(context)) {// GPS模块已开启
								String uri = "";
								String appUri = SharedPreferencesUtils
										.getInstance(context,
												R.string.shared_preferences)
										.getSharedPreferences(
												R.string.sp_appuri, "");
								uri = appUri.endsWith("/") ? new StringBuffer(
										appUri)
										.append(context
												.getString(R.string.uri_response))
										.toString()
										: new StringBuffer(appUri)
												.append("/")
												.append(context
														.getString(R.string.uri_response))
												.toString();
								new ResponseTask(context).execute(currentUser,
										caseId, uri);
							} else {
								Toast.makeText(context, "请开启设备GPS",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
	}

	@Override
	public void refreshUi() {
		super.refreshUi();
	}

	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_case_viewpager3,
				container, false);
		Intent intent = getActivity().getIntent();
		caseId = intent.getStringExtra(Parameters.CASE_ID);
		geoJson = intent.getStringExtra("hx");
		fxjg = intent.getStringExtra("hx_result");
		x = intent.getDoubleExtra("x", 0.0);
		y = intent.getDoubleExtra("y", 0.0);
		ajly = intent.getStringExtra("ajly");
		dz = intent.getStringExtra("dz");
		jcbh = intent.getStringExtra("jcbh");
		jcmj = intent.getStringExtra("jcmj");
		zygdmj = intent.getStringExtra("zygdmj");
		xfsj = intent.getStringExtra("xfsj");
		xzqh_id = intent.getStringExtra("xzqhid");
		aj_id = intent.getStringExtra("ajid");
		// activityHandle.replaceTitle(intent.getStringExtra("title"));
		myPageListener = new MyPageListener();

		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(myListener);
		InitLocation();
		mLocationClient.start();

		initViewpager();
		initFragment();
		return contentView;
	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		int span = 1000;
		option.setScanSpan(span);
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setLocationNotify(true);
		option.setIsNeedLocationDescribe(true);
		option.setIsNeedLocationPoiList(true);
		option.setIgnoreKillProcess(false);
		option.SetIgnoreCacheException(false);
		option.setEnableSimulateGps(false);
		mLocationClient.setLocOption(option);
	}

	void initFragment() {
		Log.i("ydzf", "LandInspectionEditeFragment31_initFragment");
		try {
			viewList.clear();
			makeFg3();
			makeFg2();
			viewList.add(0, caseInfoView);
			makeFg1();
			viewList.add(2, patrolEditeView);
			pagerAdapter = new ViewPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makeFg1() throws Exception {
		ContentValues inspectionValues = DBHelper.getDbHelper(context).doQuery(
				CaseInspectTable.name,
				new String[] { CaseInspectTable.field_caseId,
						CaseInspectTable.field_status },
				CaseInspectTable.field_caseId + "=?", new String[] { caseId });

		if (inspectionValues != null) {
			int inspectionStatus = inspectionValues
					.getAsInteger(CaseInspectTable.field_status);
			if (inspectionStatus == 2) {
				inspectionView = new InspectionView2(context);
				inspectionView.setDataSource(inspectionValues
						.getAsString(CaseInspectTable.field_caseId),
						CaseInspectTable.name);
				viewList.add(1, inspectionView);
			} else {
				inspectionEditeView = new InspectionEditeView2(context,
						getActivity());
				inspectionEditeView.setDataSource(currentUser, caseId,
						CaseInspectTable.name);
				viewList.add(1, inspectionEditeView);

			}
		} else {
			inspectionEditeView = new InspectionEditeView2(context,
					getActivity());
			inspectionEditeView.setDataSource(currentUser, caseId,
					CaseInspectTable.name);
			viewList.add(1, inspectionEditeView);

		}
	}

	private void makeFg2() {
		patrolEditeView = new PatrolEditeView31(context);
		patrolEditeView.setHxfxjg(fxjg);
		patrolEditeView.setParentActivity(getActivity());
		patrolEditeView
				.setDataSource(currentUser, caseId, CasePatrolTable.name);
	}

	private void makeFg3() {
		caseInfoView = new CaseView3(context);
		caseInfoView.setAjxxText("案件来源：" + ajly + "\n" + "详细地址：" + dz);
		caseInfoView.setJcbh(jcbh);
		caseInfoView.setParentActivity(getActivity());
		Log.i("guoteng","LandInspectionEditeFragment31_makeFg3");
		caseInfoView.setDataSource(caseId, InspectionCaseTable.name,
				CaseAnnexesTable.name, jcbh, jcmj,zygdmj, xfsj, fxjg);
		caseInfoView.setAj_id(aj_id);
		caseInfoView.setXzqh_id(xzqh_id);
		if (vectorTiledLayer != null && vectorTiledLayer_ != null)
			caseInfoView.setVectorTiledLayer(vectorTiledLayer,
					vectorTiledLayer_);
		if (imageryTiledLayer != null && imageryTiledLayer_ != null)
			caseInfoView.setImageryLayer(imageryTiledLayer, imageryTiledLayer_);
		caseInfoView.setMapStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			public void onStatusChanged(Object source, STATUS status) {
				try {
					MapView mapView = (MapView) source;
					Log.i("guoteng","LandInspectionEditeFragment31_makeFg3_mapStatusChange");
					Location location = mapView.getLocationDisplayManager()
							.getLocation();
					if (location != null) {
						Gps Gcj02 = PositionUtil.gps84_To_Gcj02(Double
								.valueOf(degreeFormat4Location.format(location
										.getLatitude())), Double
								.valueOf(degreeFormat4Location.format(location
										.getLongitude())));
						assert Gcj02 != null;
						qd_bd09 = PositionUtil.gcj02_To_Bd09(Gcj02.getWgLat(),
								Gcj02.getWgLon());
					}
					if (geoJson != null && !geoJson.equals("")) {
						caseInfoView.clearGraphicsLayer();
						MapUtil.loadPolygon(caseInfoView.getGraphicsLayer(),
								caseInfoView.getMapView(), geoJson);
						if (x > 0.0
								&& y > 0.0
								&& pointIsInside(new Point(x, y),
										mapView.getMaxExtent())) {
							mapView.zoomToScale(
									new Point(x, y),
									context.getResources().getDisplayMetrics().density * 20000d);
							caseInfoView.viewMapScale(context.getResources()
									.getDisplayMetrics().density * 20000d);

						} else {
							OutsideManifestHandler handler = OutsideManifestHandler
									.getHandler(context);
							if (handler != null
									&& handler.getUserMapCentre() != null) {
								Point centrePoint = handler.getUserMapCentre()
										.getPoint();
								double scale = handler.getUserMapCentre()
										.getScale();
								if (centrePoint != null && scale > 0) {
									mapView.zoomToScale(centrePoint, scale);
									caseInfoView.viewMapScale(scale);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		caseInfoView.setBtnToGoClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (AMapUtil.isInstallByRead("com.autonavi.minimap")) {
					if (x > 0.0 && y > 0.0) {
						AMapUtil.goToNaviActivity(getActivity(), getResources()
								.getString(R.string.app_name), null, y + "", x
								+ "", "1", "2");
					} else {
						Toast.makeText(getActivity(), "没有目的地信息(红线)，无法导航。",
								Toast.LENGTH_SHORT).show();
						return;
					}
				} else {
					/*NaviParaOption para = new NaviParaOption();
					if (x > 0.0 && y > 0.0) {
						Gps Gcj02 = PositionUtil.gps84_To_Gcj02(y, x);
						zd_bd09 = PositionUtil.gcj02_To_Bd09(Gcj02.getWgLat(),
								Gcj02.getWgLon());
					} else {
						Toast.makeText(getActivity(), "没有目的地信息(红线)，无法导航。",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (qd_bd09 == null) {
						Location location = LocationUtils
								.getCurrentLocation2(getActivity());
						if (location != null) {
							Gps Gcj02 = PositionUtil.gps84_To_Gcj02(Double
									.valueOf(degreeFormat4Location
											.format(location.getLatitude())),
									Double.valueOf(degreeFormat4Location
											.format(location.getLongitude())));
							assert Gcj02 != null;
							qd_bd09 = PositionUtil.gcj02_To_Bd09(
									Gcj02.getWgLat(), Gcj02.getWgLon());
						}
						if (qd_bd09 == null) {
							InitLocation();
							mLocationClient.start();
							if (DciActivityManager.myLocationLng != 0
									&& DciActivityManager.myLocationLat != 0
									&& DciActivityManager.myLocationLng != 0.0
									&& DciActivityManager.myLocationLat != 0.0) {
								Gps Gcj02 = PositionUtil.gps84_To_Gcj02(
										Double.valueOf(degreeFormat4Location
												.format(DciActivityManager.myLocationLat)),
										Double.valueOf(degreeFormat4Location
												.format(DciActivityManager.myLocationLng)));
								assert Gcj02 != null;
								qd_bd09 = PositionUtil.gcj02_To_Bd09(
										Gcj02.getWgLat(), Gcj02.getWgLon());
								Toast.makeText(
										getActivity(),
										"" + Gcj02.getWgLon() + ","
												+ Gcj02.getWgLat(),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getActivity(),
										"定位失败，请检查是否开启GPS或者网络是否畅通。",
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}
					if (qd_bd09 == null) {
						Toast.makeText(getActivity(), "还没有获得当前位置,请先定位。",
								Toast.LENGTH_SHORT).show();
						return;
					}
					para.startPoint(new LatLng(qd_bd09.getWgLat(), qd_bd09
							.getWgLon()));
					para.startName("从这里开始");
					para.endPoint(new LatLng(zd_bd09.getWgLat(), zd_bd09
							.getWgLon()));
					para.endName("到这里结束");
					try {
						BaiduMapNavigation
								.openBaiduMapNavi(para, getActivity());
					} catch (BaiduMapAppNotSupportNaviException e) {
						e.printStackTrace();
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setMessage("您尚未安装百度地图app或app版本过低，请先下载安装？");
						builder.setTitle("提示");
						builder.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.create().show();
					}*/
				}
			}
		});
	}

	/**
	 */
	class ResponseTask extends AsyncTask<String, Integer, Boolean> {
		boolean succeed = false;
		String msg;
		Location location;
		Context rContext;
		String user;
		String caseId;
		String uri;

		public ResponseTask(Context context) {
			rContext = context;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在提交响应...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			location = LocationUtils.getCurrentLocation2(getActivity());
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				user = params[0];
				caseId = params[1];
				uri = params[2];
				Thread.sleep(400);
				if (location != null) {
					JSONObject object = new JSONObject();
					object.put("user", user);
					object.put("caseId", caseId);
					object.put("x", location.getLongitude());
					object.put("y", location.getLatitude());
					object.put("accuracy", location.getAccuracy());
					Log.i("uri", uri);
					HttpResponse response = HttpUtils.httpClientExcutePost(uri,
							object);
					if (response.getStatusLine().getStatusCode() == 200) {
						String entity = EntityUtils.toString(response
								.getEntity());
						Log.i("entity", entity);
						JSONObject backJson = new JSONObject(entity);
						msg = backJson.getString("msg");
						if (backJson.getBoolean("succeed")) {
							succeed = backJson.getBoolean("succeed");
						}
					}
				} else {
					msg = "未开启GPS或GPS信号弱，定位失败";
					succeed = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (msg == null) {
				if (result)
					msg = "响应成功！";
				else
					msg = "响应失败";
			}
			Toast.makeText(rContext, msg, Toast.LENGTH_SHORT).show();
		}
	}

	// save inspection in background thread
	class inspectionSaveTask extends AsyncTask<Void, Boolean, Boolean> {
		String msg = "";
		boolean slient;

		public inspectionSaveTask(boolean slient) {
			this.slient = slient;
		}

		@Override
		protected void onPreExecute() {
			if (slient)
				return;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_saveing));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (inspectionEditeView != null)
				inspectionEditeView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean patrolSaved = savePatrol();
			if (patrolSaved)
				msg = "处理结果保存成功";
			else
				msg = "处理结果保存失败";

			publishProgress(patrolSaved);

			if (inspectionEditeView != null) {
				boolean inspectionSaved = saveInspection();
				if (inspectionSaved)
					msg = "信息核查保存成功";
				else
					msg = "信息核查保存失败";
				publishProgress(inspectionSaved);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Boolean... values) {
			if (slient)
				return;
			if (msg != null && !msg.equals("")) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (slient)
				return;
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
		}

		boolean savePatrol(){
			return patrolEditeView.savePatrol();
		}

		boolean saveInspection(){
			return inspectionEditeView.saveInspection();
		}
	}

	// send inspection in background thread
	class inspectionSendTask2 extends AsyncTask<Void, Void, Boolean> {
		String msg = "";

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (inspectionEditeView != null)
				inspectionEditeView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				// patrolEditeView.savePatrol();
				String inspectionUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				inspectionUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_casecheck_service))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_casecheck_service))
						.toString();

				// 判断是否有照片
				ArrayList<String> annexTem = getAnnexes();
				if (annexTem.size() == 0) {
					succeed = false;
					msg = "请至少上传一张照片！";
					return succeed;
				}

				JSONArray annexArray = sendAnnexes();
				String patrolId = getPatrolId();

				JSONObject patrolJson = getPatrolJson();
				patrolJson.put("annexes", annexArray);
				JSONObject resJson = new JSONObject();
				resJson.put("patrol", patrolJson);
				if (inspectionEditeView != null) {saveInspection();
					JSONObject inspectionJson = getInspectionJson();
					resJson.put("inspection", inspectionJson);
				} else {
					resJson.put("inspection", JSONObject.NULL);
				}

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(inspectionUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(resJson.toString(), HTTP.UTF_8));
				Log.i("post-entity", resJson.toString());
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {

						ArrayList<String> annexes = getAnnexes();
						if (annexes != null && annexes.size() > 0) {
							for (String s : annexes) {
								File file = new File(s);
								file.delete();
							}
						}

						ArrayList<ContentValues> annexValues = DBHelper
								.getDbHelper(context)
								.doQuery(
										CaseAnnexesTable.name,
										new String[] { CaseAnnexesTable.field_path },
										new StringBuffer(
												CaseAnnexesTable.field_caseId)
												.append("=?").toString(),
										new String[] { caseId }, null, null,
										null, null);
						if (annexValues != null && annexValues.size() > 0) {
							for (ContentValues contentValues : annexValues) {
								String path = contentValues
										.getAsString(CaseAnnexesTable.field_path);
								File file = new File(path);
								if (file.exists())
									file.delete();
							}
						}

						DBHelper.getDbHelper(context).delete(
								InspectionCaseTable.name,
								InspectionCaseTable.field_id + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseAnnexesTable.name,
								CaseAnnexesTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CasePatrolTable.name,
								CasePatrolTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseInspectTable.name,
								CaseInspectTable.field_id + "=?",
								new String[] { caseId });
						succeed = true;
						msg = "核查处理发送成功";
					} else {
						succeed = false;
						msg = backJson.getString("msg");
					}
				} else {
					succeed = false;
					msg = "核查处理发送失败";
				}
			} catch (Exception e) {
				e.printStackTrace();
				succeed = false;
				msg = "发送核查处理时发生异常";
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			GravityCenterToast.makeText(context, msg, Toast.LENGTH_SHORT)
					.show();

			if (result) {
				getActivity().onBackPressed();
			}
		}

		ArrayList<String> getAnnexes(){
			return patrolEditeView.getAnnexes();
		}

		String getPatrolId(){
			return patrolEditeView.getPatrolId();
		}

		JSONObject getPatrolJson(){
			return patrolEditeView.getPatrolJson();
		}

		void saveInspection(){
			inspectionEditeView.saveInspection();
		}

		JSONObject getInspectionJson(){
			return inspectionEditeView.getInspectionJson();
		}

		private String postAnnex(String path, String url) throws Exception {
			try {
				File file = new File(path);
				String fileName = file.getName();
				String prefix = fileName.substring(fileName.lastIndexOf("."));
				String mime = MIMEMapTable.getInstance().getMIMEType(prefix);
				JSONObject attriJson = new JSONObject();
				attriJson.put("type", "inspection");
				attriJson.put("fjlx", mime.substring(0, mime.lastIndexOf("/")));
				attriJson.put("name", fileName);
				try {
					attriJson.put("xzb", DbUtil.getPHOTO_XZB_ByPath(context, path));
					attriJson.put("yzb", DbUtil.getPHOTO_YZB_ByPath(context, path));
					attriJson.put("fwj", DbUtil.getPHOTO_FWJ_ByPath(context, path));
					attriJson.put("pssj", DbUtil.getPHOTO_PSSJ_ByPath(context, path));
				} catch (Exception e) {
				}
				MultipartEntity postEntity = new MultipartEntity();
				ContentBody cbAttri = new StringBody(attriJson.toString(),
						Charset.forName(HTTP.UTF_8));

				ContentBody cbFileData = new FileBody(file, mime);
				postEntity.addPart("attriData", cbAttri);
				postEntity.addPart("fileData", cbFileData);

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost annexPost = new HttpPost(url);
				annexPost.setEntity(postEntity);

				/**
				 * 上传发生异常时,重复操作三次,如果三次都没成功,提示用户上传附件发生异常
				 */
				int i = 0;
				while (i++ < 3) {
					HttpResponse response = client.execute(annexPost);
					if (response != null
							&& response.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(response
								.getEntity());
						Log.i("entity", entityString);
						JSONObject backJson = new JSONObject(entityString);
						if (backJson.getBoolean("succeed"))
							return backJson.getString("annexId");
						else
							throw new Exception(backJson.getString("msg"));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			throw new Exception("上传附件发生异常，请重试");
		}

		private JSONArray sendAnnexes() throws Exception {
			JSONArray annexArray = new JSONArray();
			try {
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				String annexUri = appUri.endsWith("/") ? new StringBuffer(
						appUri).append(
						context.getString(R.string.uri_annexupload_service))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_annexupload_service))
						.toString();

				ArrayList<String> annexes = patrolEditeView.getAnnexes();
				if (annexes != null && annexes.size() > 0) {
					for (String s : annexes) {
						String annexId = postAnnex(s, annexUri);
						annexArray.put(annexId);
					}
				}
			} catch (Exception e) {
				throw e;
			}
			return annexArray;
		}
	}

	// send inspection in background thread
	class inspectionSendTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (inspectionEditeView != null)
				inspectionEditeView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				// patrolEditeView.savePatrol();
				String inspectionUri = "";
				String annexUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				inspectionUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_inspection_service))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_inspection_service))
						.toString();
				annexUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_annex_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_annex_service))
						.toString();

				String patrolId =getPatrolId();

				JSONObject patrolJson = getPatrolJson();
				JSONObject resJson = new JSONObject();
				resJson.put("patrol", patrolJson);
				if (inspectionEditeView != null) {
					saveInspection();
					JSONObject inspectionJson = getInspectionJson();
					resJson.put("inspection", inspectionJson);
				} else {
					resJson.put("inspection", JSONObject.NULL);
				}

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(inspectionUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(resJson.toString(), HTTP.UTF_8));
				Log.i("post-entity", resJson.toString());
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {
						String serverPatrolId = backJson
								.getString("inspectionId");
						ArrayList<String> annexes = getAnnexes();
						if (annexes != null && annexes.size() > 0) {
							for (String s : annexes) {
								File file = new File(s);
								String fileName = file.getName();
								String prefix = fileName.substring(fileName
										.lastIndexOf("."));
								String mime = MIMEMapTable.getInstance()
										.getMIMEType(prefix);
								JSONObject attriJson = new JSONObject();
								attriJson.put("type", "inspection");
								attriJson.put("tagId", serverPatrolId);
								attriJson.put("name", fileName);

								attriJson.put("fjlx", mime.substring(0,
										mime.lastIndexOf("/")));
								MultipartEntity postEntity = new MultipartEntity();
								ContentBody cbAttri = new StringBody(
										attriJson.toString(),
										Charset.forName(HTTP.UTF_8));

								ContentBody cbFileData = new FileBody(file,
										mime);
								postEntity.addPart("attriData", cbAttri);
								postEntity.addPart("fileData", cbFileData);

								HttpPost annexPost = new HttpPost(annexUri);
								annexPost.setEntity(postEntity);
								client.execute(annexPost);
							}
						}

						if (annexes != null && annexes.size() > 0) {
							for (String s : annexes) {
								File file = new File(s);
								file.delete();
							}
						}

						ArrayList<ContentValues> annexValues = DBHelper
								.getDbHelper(context)
								.doQuery(
										CaseAnnexesTable.name,
										new String[] { CaseAnnexesTable.field_path },
										new StringBuffer(
												CaseAnnexesTable.field_caseId)
												.append("=?").toString(),
										new String[] { caseId }, null, null,
										null, null);
						if (annexValues != null && annexValues.size() > 0) {
							for (ContentValues contentValues : annexValues) {
								String path = contentValues
										.getAsString(CaseAnnexesTable.field_path);
								File file = new File(path);
								if (file.exists())
									file.delete();
							}
						}

						DBHelper.getDbHelper(context).delete(
								InspectionCaseTable.name,
								InspectionCaseTable.field_id + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseAnnexesTable.name,
								CaseAnnexesTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CasePatrolTable.name,
								CasePatrolTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseInspectTable.name,
								CaseInspectTable.field_id + "=?",
								new String[] { caseId });
						succeed = true;
						msg = "核查处理发送成功";
					} else {
						succeed = false;
						msg = backJson.getString("msg");
					}
				} else {
					succeed = false;
					msg = "核查处理发送失败";
				}
			} catch (Exception e) {
				e.printStackTrace();
				succeed = false;
				msg = "发送核查处理时发生异常";
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			GravityCenterToast.makeText(context, msg, Toast.LENGTH_SHORT)
					.show();

			if (result) {
				getActivity().onBackPressed();
			}
		}

		ArrayList<String> getAnnexes(){
			return patrolEditeView.getAnnexes();
		}

		String getPatrolId(){
			return patrolEditeView.getPatrolId();
		}

		JSONObject getPatrolJson(){
			return patrolEditeView.getPatrolJson();
		}

		void saveInspection(){
			inspectionEditeView.saveInspection();
		}

		JSONObject getInspectionJson(){
			return inspectionEditeView.getInspectionJson();
		}

	}

	private boolean pointIsInside(Point point, Envelope extent) {
		return (point.getX() > extent.getXMin()
				&& point.getX() < extent.getXMax()
				&& point.getY() > extent.getYMin() && point.getY() < extent
				.getYMax());
	}

	private class MyPageListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			int x = moveX * 2 + width;
			Animation animation = new TranslateAnimation(x * index, x * arg0,
					0, 0);
			index = arg0;
			animation.setFillAfter(true); // 设置动画停止在结束位置
			animation.setDuration(300); // 设置动画时间
			image.startAnimation(animation); // 启动动画
			// Toast.makeText(getActivity(), arg0+"",
			// Toast.LENGTH_SHORT).show();
		}
	}

	protected class ViewPagerAdapter extends PagerAdapter {
		List<View> viewList;
		List<String> viewTitle;

		public ViewPagerAdapter(List<View> viewList, List<String> viewTitle) {
			this.viewList = viewList;
			this.viewTitle = viewTitle;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			return viewList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return viewTitle.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		mLocationClient.stop();
	}
}
