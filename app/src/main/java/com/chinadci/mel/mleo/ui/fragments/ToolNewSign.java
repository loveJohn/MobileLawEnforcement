package com.chinadci.mel.mleo.ui.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
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

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;

public class ToolNewSign extends ToolOneButton {

	Location location = null;
	String appUri;
	String signUri;
	String textNotes;
	String adminCode;
	Double x, y;
	Float accuracy;
	AlertDialog alertDialogA;
	AlertDialog alertDialogB;

	@Override
	protected void setButtonText() {
		button.setText(R.string.cn_sign);
	}

	public void onClick(View v) {
		if (LocationUtils.isGPSSupport(context)) { // GPS模块已开启
			location = LocationUtils.getCurrentLocation2(context);
			if(location!=null){
				DciActivityManager.myLocationLng=location.getLongitude();
            	DciActivityManager.myLocationLat=location.getLatitude();
			}
			if (DciActivityManager.myLocationLng == 0) {
				Toast.makeText(context, "GPS获取失败,使用地址签到功能", Toast.LENGTH_SHORT).show();
				Bundle bundle = new Bundle();
				bundle.putString("sign_id", "");
				activityHandle.replaceTitle(getString(R.string.cn_site));
				activityHandle.replaceToolFragment(new ToolSaveSend(), null,
						R.anim.slide_in_top, R.anim.slide_out_bottom);
				activityHandle.replaceContentFragment(new SignEditFragment(),
						bundle, R.anim.slide_in_right, R.anim.slide_out_left);
				return;
			}
			appUri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "");
			new GetAddressTask(context).execute();

		} else {
			
			Toast.makeText(context, "设备未开启GPS,使用地址签到功能", Toast.LENGTH_SHORT).show();
			Bundle bundle = new Bundle();
			bundle.putString("sign_id", "");
			activityHandle.replaceTitle(getString(R.string.cn_site));
			activityHandle.replaceToolFragment(new ToolSaveSend(), null,
					R.anim.slide_in_top, R.anim.slide_out_bottom);
			activityHandle.replaceContentFragment(new SignEditFragment(),
					bundle, R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}

	class GetAddressTask extends AsyncTask<Void, Integer, Boolean> {
		Context context;
		String addressUri;
		String addressText;
		String msg = null;
		String errorMsg="获取不到坐标或当前坐标不在福建省内，请使用手动签到进行签到！";
		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在获取地址，请稍候...");
			alertDialogB = new AlertDialog.Builder(getActivity()).create();
			alertDialogB.show();
			alertDialogB.setCancelable(false);
			alertDialogB.getWindow().setContentView(abv);
			x = DciActivityManager.myLocationLng;
			y = DciActivityManager.myLocationLat;
			Log.i("ydzf", "NewSign_loc_long="+x+",loc_lat="+y);
			if(location!=null){
				accuracy = location.getAccuracy();
			}
		}

		public GetAddressTask(Context c) {
			try {
				this.context = c;

				signUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_patrol_signin_new))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_patrol_signin_new))
						.toString();
				addressUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_address_service))
						.toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_address_service))
								.toString();
					Log.i("ydzf","signUri="+signUri);
						
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
				addressUri = new StringBuffer(addressUri).append("?x=")
						.append(x).append("&y=").append(y).toString();
				Log.i("ydzf","addressUri="+addressUri);
				HttpResponse response = HttpUtils.httpClientExcuteGet(
						addressUri, 30000);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						JSONArray dataJsonArray = backJson.getJSONArray("data");
						if (dataJsonArray != null && dataJsonArray.length() > 0) {
							for (int i = 0; i < dataJsonArray.length(); i++) {
								JSONObject dataJsonObject = dataJsonArray
										.getJSONObject(i);
								if ("乡级".equals(dataJsonObject
										.getString("level"))) {
									addressText = dataJsonObject
											.getString("fullName");
									adminCode = dataJsonObject
											.getString("code");
									break;
								}
							}
							if (addressText != null && addressText.length() > 0) {
								textNotes = "当前签到位置为：" + addressText;
								return true;
							}
						}
					} else {
						msg = errorMsg;//backJson.getString("msg");
						return false;
					}
				}
				msg = errorMsg;//"获取不到坐标或当前坐标不在福建省内，请使用手动签到进行签到！";
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				msg = errorMsg;//"获取不到坐标或当前坐标不在福建省内，请使用手动签到进行签到！";
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialogB != null && alertDialogB.isShowing()) {
					alertDialogB.dismiss();
				}

				View alertView = LayoutInflater.from(context).inflate(
						R.layout.view_sign_alert, null);
				TextView notesView = (TextView) alertView
						.findViewById(R.id.view_sign_alert_notes);
				Button cancelButton = (Button) alertView
						.findViewById(R.id.view_sign_alert_cancel);
				Button doButtonA = (Button) alertView
						.findViewById(R.id.view_sign_alert_doa);
				Button doButtonB = (Button) alertView
						.findViewById(R.id.view_sign_alert_dob);
				if (!result) {
					textNotes = msg;
					doButtonA.setVisibility(View.GONE);
				}
				notesView.setText(textNotes);
				cancelButton.setText("暂不签到");
				//暂时隐藏暂不签到
				cancelButton.setVisibility(View.GONE);
				doButtonA.setText("确定签到");
				doButtonB.setText("手动签到");

				cancelButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (alertDialogA != null && alertDialogA.isShowing())
							alertDialogA.dismiss();
					}
				});

				doButtonA.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (alertDialogA != null && alertDialogA.isShowing())
							alertDialogA.dismiss();
						// new signDeleteTask(delView).execute(p);
						new signSendTask(context).execute();
					}
				});

				doButtonB.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (alertDialogA != null && alertDialogA.isShowing())
							alertDialogA.dismiss();

						Bundle bundle = new Bundle();
						bundle.putString("sign_id", "");
						activityHandle
								.replaceTitle(getString(R.string.cn_site));
						activityHandle.replaceToolFragment(new ToolSaveSend(),
								null, R.anim.slide_in_top,
								R.anim.slide_out_bottom);
						activityHandle.replaceContentFragment(
								new SignEditFragment(), bundle,
								R.anim.slide_in_right, R.anim.slide_out_left);
					}
				});

				alertDialogA = new AlertDialog.Builder(getActivity()).create();
				alertDialogA.show();
				alertDialogA.setCancelable(true);
				alertDialogA.getWindow().setContentView(alertView);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			location = LocationUtils.getCurrentLocation(context);
		}
	}

	class signSendTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		CircleProgressBusyView abv;

		public signSendTask(Context c) {
			this.context = c;
		}

		@Override
		protected void onPreExecute() {
			abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialogB = new AlertDialog.Builder(getActivity()).create();
			alertDialogB.show();
			alertDialogB.setCancelable(false);
			alertDialogB.getWindow().setContentView(abv);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				signUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_patrol_signin_new))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_patrol_signin_new))
						.toString();

				JSONObject signJsonObject = new JSONObject();
				signJsonObject.put("user", currentUser);
				signJsonObject.put("type", "303");
				String cTime = TimeFormatFactory2.getDateFormat(new Date());
				signJsonObject.put("time", cTime);
				JSONObject locationJson = new JSONObject();
				locationJson.put("x", x);
				locationJson.put("y", y);
				locationJson.put("accuracy", accuracy);
				signJsonObject.put("location", locationJson);
				JSONObject siteJson = new JSONObject();
				siteJson.put("admin", adminCode);
				signJsonObject.put("site", siteJson);

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(signUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(signJsonObject.toString(),
						HTTP.UTF_8));
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject signBackJson = new JSONObject(entityString);

					if (signBackJson.getBoolean("succeed")) {
						SharedPreferencesUtils.getInstance(context,
								R.string.shared_preferences)
								.writeSharedPreferences(
										R.string.sp_last_signin_time, cTime);
						return true;
					} else {
						msg = signBackJson.getString("msg");
						return false;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (alertDialogB != null && alertDialogB.isShowing()) {
				alertDialogB.dismiss();
			}
			if (result) {
				GravityCenterToast
						.makeText(context, "签到成功", Toast.LENGTH_SHORT).show();
				getActivity().onBackPressed();
			} else {
				GravityCenterToast.makeText(context, "签到失败，" + msg,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
