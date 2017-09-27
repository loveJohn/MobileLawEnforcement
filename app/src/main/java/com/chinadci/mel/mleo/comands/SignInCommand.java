package com.chinadci.mel.mleo.comands;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Command;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ui.views.UserView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

/**
 * 
* @ClassName SignInCommand 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:42:22 
*
 */
public class SignInCommand extends Command {

	Activity handleActivity;
	String currentUser;
	UserView userView;

	@Override
	public Object run(Activity activity, View view, Object object) {
		handleActivity = activity;
		//userView = (UserView) handleActivity.findViewById(R.id.activity_home_user);
		currentUser = SharedPreferencesUtils.getInstance(activity,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_actuser, "");
		if (LocationUtils.isGPSSupport(activity)) {// GPS模块已开启
			new PatrolSignInTask(activity).execute();
		} else {
			Toast.makeText(activity, "请先开启设备GPS再签到", Toast.LENGTH_SHORT).show();
		}

		return null;
	}

	class PatrolSignInTask extends AsyncTask<Void, Integer, Boolean> {
		Context context;
		String uri;
		Location location = null;
		String msg = null;
		AlertDialog alertDialog;

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在与服务器通讯，请稍候...");
			alertDialog = new AlertDialog.Builder(handleActivity).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			location = LocationUtils.getCurrentLocation2(context);
		}

		public PatrolSignInTask(Context c) {
			try {
				this.context = c;
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_patrol_signin))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_patrol_signin))
						.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				msg = null;
				Thread.sleep(400);
				JSONObject obj = new JSONObject();
				if (location == null) {
					msg = "签到失败,没有获取到你的位置!";
					return false;
				} else {
					Point point = new Point(location.getLongitude(),
							location.getLatitude());
					String pointJsonString = GeometryEngine.geometryToJson(SpatialReference.create(4326), point);
					obj.put("location", new JSONObject(pointJsonString));
					obj.put("accuracy", location.getAccuracy());
				}

				obj.put("user", currentUser);
				HttpResponse response = HttpUtils
						.httpClientExcutePost(uri, obj);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {
						try {
							String time = backJson.getString("curTime");
							SharedPreferencesUtils.getInstance(context,
									R.string.shared_preferences)
									.writeSharedPreferences(
											R.string.sp_last_signin_time, time);
						} catch (Exception e) {
							e.printStackTrace();
						}

						msg = "签到成功!";
						return true;
					} else {
						msg = backJson.getString("msg");
						return false;
					}
				}
				msg = "签到失败,请重试!";
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				msg = "签到失败,请重试!";
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}

				if (result) {
					/*userView.setSignTime(String.format(
							context.getString(R.string.format_last_signin),
							TimeFormatFactory2
									.getDisplayTimeM(SharedPreferencesUtils
											.getInstance(context,
													R.string.shared_preferences)
											.getSharedPreferences(
													R.string.sp_last_signin_time,
													""))));*/
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			location = LocationUtils.getCurrentLocation2(context);

		}
	}
}
