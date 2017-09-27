package com.chinadci.mel.mleo.ui.activities;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.core.Feedback;
import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.NetworkUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.android.ui.activities.TwiceBack2ExitActivity;
import com.chinadci.mel.mleo.core.ApkDownloader;
import com.chinadci.mel.mleo.core.Global;
import com.chinadci.mel.mleo.core.ManifestHandler;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.core.utils.SMSReceiverHelper;
import com.chinadci.mel.mleo.ldb.AdminTable;
import com.chinadci.mel.mleo.ldb.LandUsageWpTable;
import com.chinadci.mel.mleo.ldb.SignCauseTable;
import com.chinadci.mel.mleo.ldb.ClueSourceTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.DealResultTable;
import com.chinadci.mel.mleo.ldb.FfckfsTable;
import com.chinadci.mel.mleo.ldb.FkckzTable;
import com.chinadci.mel.mleo.ldb.IllegalStatusTable;
import com.chinadci.mel.mleo.ldb.IllegalSubjectTable;
import com.chinadci.mel.mleo.ldb.IllegalTypeTable;
import com.chinadci.mel.mleo.ldb.InspectResultTable;
import com.chinadci.mel.mleo.ldb.LandUsageTable;
import com.chinadci.mel.mleo.ldb.ParmeterTable;
import com.chinadci.mel.mleo.ldb.ProjTypeTable;
import com.chinadci.mel.mleo.ldb.SyncTable;
import com.chinadci.mel.mleo.ldb.UserTable;
import com.chinadci.mel.mleo.services.DciMleoPicker;
import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.utils.EnvironmentUtils;
import com.chinadci.mel.mleo.utils.LocUtils;
import com.chinadci.mel.mleo.utils.TimeUtil;

/**
 * 
 * @ClassName SplashActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:11
 * 
 */
@SuppressLint("DefaultLocale")
public class SplashActivity extends TwiceBack2ExitActivity {
	AlertDialog alertDialog;
	TextView versionView;
	String smsHandle;
	String userid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		versionView = (TextView) findViewById(R.id.activity_splash_version);
		try {
			smsHandle = getIntent()
					.getStringExtra(SMSReceiverHelper.SMS_HANDLE);
			if (smsHandle != null) {
				DciActivityManager.getInstance().destroyActivity(
						ModuleRealizeActivity.class);
				DciActivityManager.getInstance().destroyActivity(
						HomeActivity.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LocUtils.newInstance(this).startNetLocation();

		try {
			new SplashCheckTask(this).execute();
			if (NetworkUtils.checkNetwork(this)) {
				new AdminGetTask(this).execute();
				new ArgumentsGetTask(this).execute();
			}

			Intent inetnt = new Intent(this, DciMleoPicker.class);
			inetnt.setAction(DciMleoPicker.ACTION);
			startService(inetnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @ClassName SplashCheckTask
	 * @Description
	 * @author leix@geo-k.cn
	 * @date 2014年6月11日 下午2:12:12
	 * 
	 */
	class SplashCheckTask extends AsyncTask<Void, String, Boolean> {
		Context context;
		String latestAppUrl;
		String updates;
		String latestVersionName;

		public SplashCheckTask(Context c) {
			this.context = c;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				long stime = new Date().getTime();
				Feedback.getInstance(SplashActivity.this).feedbackDeviceInfo(
						Feedback.ACTION_TYPE_LAUNCH,
						R.string.dci_appid,
						"",
						SharedPreferencesUtils.getInstance(SplashActivity.this,
								R.string.shared_preferences)
								.getSharedPreferences(R.string.sp_actuser, ""),
						null);
				EnvironmentUtils.prepareEnvir(SplashActivity.this);
				PackageManager packageManager = getPackageManager();
				PackageInfo packInfo;
				packInfo = packageManager.getPackageInfo(getPackageName(), 0);
				String versionName = packInfo.versionName;
				int versionCode = packInfo.versionCode;
				publishProgress(versionName);

				if (NetworkUtils.checkNetwork(SplashActivity.this)) {
					try {
						String appUri = SharedPreferencesUtils.getInstance(
								context, R.string.shared_preferences)
								.getSharedPreferences(R.string.sp_appuri, "");
						String versionUrl = appUri.endsWith("/") ? new StringBuffer(
								appUri).append(
								context.getString(R.string.uri_version))
								.toString()
								: new StringBuffer(appUri)
										.append("/")
										.append(context
												.getString(R.string.uri_version))
										.toString();
						JSONObject versionJson = new JSONObject();
						versionJson.put("versionCode", versionCode);
						versionJson.put("versionName", versionName);
						HttpResponse versionResponse = HttpUtils
								.httpClientExcutePost(versionUrl, versionJson);

						if (versionResponse.getStatusLine().getStatusCode() == 200) {
							String entityString = EntityUtils
									.toString(versionResponse.getEntity());
							JSONObject backJson = new JSONObject(entityString);
							if (backJson.getBoolean("succeed")) {
								// boolean appIsLatest =
								// backJson.getBoolean("appIsLatest");
								try {
									updates = backJson.getString("updates");
								} catch (Exception e) {
									e.printStackTrace();
								}

								latestVersionName = backJson
										.getString("latestVersionName");
								boolean needUpdate = (latestVersionName
										.compareTo(versionName) > 0);
								if (needUpdate) {
									latestAppUrl = backJson.getString("url");
									return false;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (Global.funcationMap == null || Global.moduleMap == null) {
					ManifestHandler manifestHandler = new ManifestHandler(
							SplashActivity.this, getAssets()
									.open("modules.xml"));
					Global.funcationMap = manifestHandler.getFunctionMap();
					Global.moduleMap = manifestHandler.getModuleMap();
				}

				String actUser = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_actuser, "");
				if (actUser.equalsIgnoreCase("")) {
					succeed = false;
				} else {
					int count = DBHelper.getDbHelper(context).queryCount(
							UserTable.name, null, UserTable.field_name + "=?",
							new String[] { actUser });// 检查缓存数据中是否存在当前登录用户的信息
					if (count > 0)
						succeed = true;
					else {
						SharedPreferencesUtils
								.getInstance(context,
										R.string.shared_preferences)
								.writeSharedPreferences(R.string.sp_actuser, "");
						succeed = false;
					}
				}

				long sub = new Date().getTime() - stime;
				if (smsHandle == null || smsHandle.equals(""))
					if (sub < 3000)// 完成操作小于2000毫秒-阻塞线程
						Thread.sleep(3000 - sub);
				// //////////////////////刘光辉新加///////////////////////////////
				userid = SharedPreferencesUtils.getInstance(
						SplashActivity.this, R.string.shared_preferences)
						.getSharedPreferences(R.string.sp_actuser, "");
				// ////////////////////////////////////////////////////////////

			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			versionView.setText(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (latestAppUrl != null) {
				showVersionDownload(latestAppUrl, latestVersionName, updates);
			} else {
				if (result) {// 有已登录用户
					//获取上次登陆时间。
					String lastLoadTime=SharedPreferencesUtils.getInstance(context,R.string.shared_preferences).getSharedPreferences(R.string.sp_last_login_time, "0");
					if(TimeUtil.isOverDue(TimeUtil.FREE_LOAD_TIME, lastLoadTime)){		//登陆过期，跳转登陆界面
						Toast.makeText(SplashActivity.this, "登录信息已过期，请重新登录", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(SplashActivity.this,LoginActivity.class);// 跳转到登录界面
						if (smsHandle != null && !smsHandle.equals(""))
							intent.putExtra(SMSReceiverHelper.SMS_HANDLE, smsHandle);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					}else{
						Intent intent = null;
						intent = new Intent(SplashActivity.this, HomeActivity.class);
						if (smsHandle != null && !smsHandle.equals("")) {
							intent.putExtra(SMSReceiverHelper.SMS_HANDLE, smsHandle);
						}
						intent.putExtra("userid", userid);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);// 跳转到主界面
					}
				} else {
					Intent intent = new Intent(SplashActivity.this,
							LoginActivity.class);// 跳转到登录界面
					if (smsHandle != null && !smsHandle.equals(""))
						intent.putExtra(SMSReceiverHelper.SMS_HANDLE, smsHandle);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
			}
		}
	}

	void shutDown() {
		this.finish();
		DciActivityManager.getInstance().exit();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	void showVersionDownload(String url, String versionName, String updataes) {
		final String uri = url;
		final String lvn = versionName;
		View alertView = LayoutInflater.from(this).inflate(R.layout.view_alert,
				null);
		TextView notesView = (TextView) alertView
				.findViewById(R.id.view_alert_notes);
		Button cancelButton = (Button) alertView
				.findViewById(R.id.view_alert_cancel);
		Button doButton = (Button) alertView.findViewById(R.id.view_alert_do);

		String msg = (updataes != null && !updataes.equals("")) ? new StringBuffer(
				"发现新版本(").append(versionName).append(")，请更新！").append("\n")
				.append("更新内容：").append("\n").append(updataes).toString()
				: new StringBuffer("发现新版本(").append(versionName)
						.append(")，请更新！").toString();
		notesView.setText(msg);
		cancelButton.setText("退出应用");
		doButton.setText("下载更新");

		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				shutDown();
			}
		});

		doButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				downloadApk(uri, lvn);
				shutDown();
			}
		});

		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setContentView(alertView);
	}

	/**
	 * 
	 * @ClassName ArgumentsGetTask
	 * @Description 表单参数获取
	 * @author leix@geo-k.cn
	 * @date 2014年6月11日 下午2:14:52
	 * 
	 */
	class ArgumentsGetTask extends AsyncTask<Void, Void, Boolean> {
		Context context;
		// 违法状态，违法类型，处理情况，项目类型，初判地类，线索来源,违法主体,核查结果 , 手工签到原因
		String argumentTables[] = new String[] { IllegalStatusTable.name,
				IllegalTypeTable.name, DealResultTable.name,
				ProjTypeTable.name, LandUsageTable.name, ClueSourceTable.name,
				IllegalSubjectTable.name, InspectResultTable.name,
				FfckfsTable.name, FkckzTable.name, SignCauseTable.name, "JSDTALL",LandUsageWpTable.name};
		String reUris[] = new String[] { "illegalStatus", "illegalType",
				"CLQK", "XMLX", "CPDL", "XSLY", "WFZT", "HCJG", "FFKCFS",
				"FFKCKZ", "SGQDYY", "JSDT","CPDL_WP" };

		public ArgumentsGetTask(Context c) {
			this.context = c;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			String uri = "";
			String appUri = "";
			try {
				appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
			} catch (Exception e) {

			}
			uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
					context.getString(R.string.uri_arguments_service))
					.toString() : new StringBuffer(appUri).append("/")
					.append(context.getString(R.string.uri_arguments_service))
					.toString();
			for (int i = 0; i < argumentTables.length; i++) {
				try {
					String table = argumentTables[i];
					String reUri = reUris[i];
					String argumentUri = uri + "?type=" + reUri;
					String param="type=" + reUri;
					String entityString=HttpUtil.get(uri,param);
					/*HttpResponse response = HttpUtils.httpClientExcuteGet(argumentUri);
					if (response.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(response.getEntity());
					}*/
					Log.i("ydzf", "argumentTables "+table+",entityString="+entityString);
					if (entityString != null && !entityString.equals("")) {
						JSONObject entiryJson = new JSONObject(entityString);
						boolean succeed = entiryJson.getBoolean("succeed");
						if (succeed) {
							if(!reUri.equals("JSDT")){
								DBHelper.getDbHelper(context).delete(table,
										null, null);
							}
							JSONArray dataSet = entiryJson
									.getJSONArray("dataset");
							if (dataSet != null && dataSet.length() > 0) {
								if (reUri.equals("JSDT")) {
									DbUtil.deleteJSDTDbDatas(context);
									for(int mm=0;mm<dataSet.length();mm++){
										JSONObject oJsonObject = dataSet.optJSONObject(mm);
										DbUtil.insertJSDTDbDatas(context, oJsonObject.optString("key"), oJsonObject.optString("value"));
									}
								}else if (reUri.equals("CPDL_WP")){        //地类
									for (int n = 0; n < dataSet.length(); n++) {
										try {
											JSONObject data = dataSet.getJSONObject(n);
											ContentValues cv = new ContentValues();
											String key = null;
											if(data.has("key")){
												key = data.getString("key");
											}

											String value = null;
											if(data.has("value")){
												value = data.getString("value");
											}

											String sub = null;
											if(data.has("sub")){
												sub = data.getString("sub");
											}

											cv.put(LandUsageWpTable.field_id, key);
											cv.put(LandUsageWpTable.field_name,value);
											cv.put(LandUsageWpTable.field_sub,sub);
											DBHelper.getDbHelper(context).insert(table, cv);
										} catch (Exception e) {
											continue;
										}
									}
									ContentValues ucv = new ContentValues();
									ucv.put(SyncTable.field_time,TimeFormatFactory2.getDateFormat(new Date()));
									ucv.put(SyncTable.field_name, table);
									DBHelper.getDbHelper(context).insert(SyncTable.name, ucv);
								}else{
									for (int n = 0; n < dataSet.length(); n++) {
										try {
											JSONObject data = dataSet
													.getJSONObject(n);
											ContentValues cv = new ContentValues();
											String key = data.getString("key");
											String value = data
													.getString("value");
											cv.put(ParmeterTable.field_id, key);
											cv.put(ParmeterTable.field_name,value);
											DBHelper.getDbHelper(context)
													.insert(table, cv);
										} catch (Exception e) {
											continue;
										}
									}
									ContentValues ucv = new ContentValues();
									ucv.put(SyncTable.field_time,
											TimeFormatFactory2
													.getDateFormat(new Date()));
									ucv.put(SyncTable.field_name, table);
									DBHelper.getDbHelper(context).insert(
											SyncTable.name, ucv);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	class AdminGetTask extends AsyncTask<Void, Void, Boolean> {
		Context context;

		public AdminGetTask(Context c) {
			context = c;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				try {
					String uri = "";
					String appUri = SharedPreferencesUtils.getInstance(context,
							R.string.shared_preferences).getSharedPreferences(
							R.string.sp_appuri, "");
					uri = appUri.endsWith("/") ? new StringBuffer(appUri)
							.append(context
									.getString(R.string.uri_admin_service))
							.append("?code=")
							.append(context.getString(R.string.cn_defadmincode))
							.toString()
							: new StringBuffer(appUri)
									.append("/")
									.append(context.getString(R.string.uri_admin_service))
									.append("?code=")
									.append(context.getString(R.string.cn_defadmincode))
									.toString();

					HttpResponse adminResponse = HttpUtils
							.httpClientExcuteGet(uri);
					if (adminResponse.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(adminResponse.getEntity());
						Log.i("ydzf", "AdminGetTask entityString="+entityString);
						JSONObject entiryJson = new JSONObject(entityString);
						boolean succeed = entiryJson.getBoolean("succeed");
						if (succeed) {
							JSONArray adminArray = entiryJson.getJSONArray("admins");
							for (int i = 0; i < adminArray.length(); i++) {
								try {
									JSONObject admin = adminArray
											.getJSONObject(i);
									ContentValues cv = new ContentValues();
									String code = "";

									// code
									try {
										code = admin.getString("code");
										if (code != null && !code.equals("")
												&& !code.equals("null"))
											cv.put("code", code);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// name
									try {
										String name = admin.getString("name");
										if (name != null && !name.equals("")
												&& !name.equals("null"))
											cv.put("name", name);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// parent code
									try {
										String parentCode = admin
												.getString("parentCode");
										if (parentCode != null
												&& !parentCode.equals("")
												&& !parentCode.equals("null"))
											cv.put("parentCode", parentCode);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// min x
									try {
										String minXStr = admin
												.getString("minX");
										if (minXStr != null
												&& !minXStr.equals("")
												&& !minXStr.toLowerCase()
														.equals("null")) {
											double minX = Double
													.parseDouble(minXStr);
											cv.put("minX", minX);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									// min y
									try {
										String minYStr = admin
												.getString("minY");
										if (minYStr != null
												&& !minYStr.equals("")
												&& !minYStr.toLowerCase()
														.equals("null")) {
											double minY = Double
													.parseDouble(minYStr);
											cv.put("minY", minY);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									// max x
									try {
										String maxXStr = admin
												.getString("maxX");
										if (maxXStr != null
												&& !maxXStr.equals("")
												&& !maxXStr.toLowerCase()
														.equals("null")) {
											double maxX = Double
													.parseDouble(maxXStr);
											cv.put("maxX", maxX);
										}
									} catch (Exception e) {
									}

									// max y
									try {
										String maxYStr = admin
												.getString("maxY");
										if (maxYStr != null
												&& !maxYStr.equals("")
												&& !maxYStr.toLowerCase()
														.equals("null")) {
											double maxY = Double
													.parseDouble(maxYStr);
											cv.put("maxY", maxY);
										}
									} catch (Exception e) {
									}

									// centre
									try {
										String centre = admin
												.getString("centre");
										if (centre != null
												&& !centre.equals("")
												&& !centre.equals("null")) {

											cv.put("centre", centre);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									// shape
									try {
										String shape = admin.getString("shape");
										if (shape != null && !shape.equals("")
												&& !shape.equals("null")) {
											cv.put("shape", shape);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									String delWhere = new StringBuffer(
											AdminTable.field_code).append("=?")
											.toString();
									String args[] = new String[] { code };
									DBHelper.getDbHelper(context).delete(
											AdminTable.name, delWhere, args);
									DBHelper.getDbHelper(context).insert(
											AdminTable.name, cv);
								} catch (Exception e) {
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	void downloadApk(String url, String versionName) {
		String title = getString(R.string.app_name_abs) + versionName;
		String desc = "正在下载...";

		Long idLong = ApkDownloader.startDownload(SplashActivity.this, url,
				title, desc, getString(R.string.dir_apk));
		SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
				.writeSharedPreferences(R.string.sp_apkdownload_id,
						String.valueOf(idLong));
	}
}
