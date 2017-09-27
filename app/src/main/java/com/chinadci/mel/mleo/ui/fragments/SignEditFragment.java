package com.chinadci.mel.mleo.ui.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.DropDownSpinner;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.SignCauseTable;
import com.chinadci.mel.mleo.ldb.SignTable;
import com.chinadci.mel.mleo.ui.activities.AdminChooserActivity;
import com.chinadci.mel.mleo.ui.views.UserView;
import com.chinadci.mel.mleo.utils.VibratorUtil;

/**
 * 
 * @ClassName LandCaseEditeFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:46:38
 * 
 */
@SuppressLint("SimpleDateFormat")
public class SignEditFragment extends ContentFragment {
	UserView userView;
	View contentView;
	DropDownSpinner causeSpinner;
	EditText notesView;
	EditText addressView;
	TextView adminView;

	String keyCause;
	String textCause;
	String cTime;

	String keySource = "16";

	String signId;
	String textSource = "16";

	String adminCode = "350000";
	String adminName = "福建省";
	String address = "";
	String notes = "";
	Double x, y;
	Float accuracy;

	AlertDialog alertDialog;
	Thread minorThread;
	Animation scaleInAnim;
	DecimalFormat kmFormat = new DecimalFormat("#.####");
	boolean isCurrentUnitSqm = true;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Parameters.GET_ADMIN) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				adminCode = bundle.getString(AdminChooserActivity.ADMIN_CODE);
				adminName = DBHelper.getDbHelper(context).queryAdminFullName(
						adminCode);
				adminView.setText(adminName);
			}
		}
	}

	@Override
	public void handle(Object o) {
		super.handle(o);
		int tag = (Integer) o;
		if (tag == 0) {//保存签到信息在本地
			new signSaveTask(context, false).execute();
		} else if (tag == 1) {//发送签到信息到服务器
			if (keyCause == null || "".equals(keyCause)) {
				Toast.makeText(context, "请选择手动签到的原因", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (adminCode.length() < 12) {
				Toast.makeText(context, "签到地址要求为村级行政区", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			new signSaveTask(context, true).execute();
			new signSendTask(context).execute();
		}
	}

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		super.refreshUi();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
		String admin[] = DBHelper.getDbHelper(context)
				.getUserAdmin(currentUser);
		if (admin != null) {
			if (!admin[0].equals("") && !admin[1].equals("")) {
				adminCode = admin[0];
				adminName = admin[1];
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		signId = getArguments().getString("sign_id");
		contentView = inflater.inflate(R.layout.fragment_sign_edit, container,
				false);
		adminView = (TextView) contentView
				.findViewById(R.id.fragment_sign_edit_admin);
		causeSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_sign_edit_cause);
		addressView = (EditText) contentView
				.findViewById(R.id.fragment_sign_edit_address);
		notesView = (EditText) contentView
				.findViewById(R.id.fragment_sign_edit_notes);

		causeSpinner.setSelectedChangedListener(spinnerSelectedChangedListener);

		try {
			ArrayList<KeyValue> cause = DBHelper.getDbHelper(context)
					.getParameters(SignCauseTable.name);
			causeSpinner.setData(cause);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (signId != null && !signId.equals("")) {
			loadSignInfo(signId);
		} else {
			signId = new StringBuffer("XC").append(
					TimeFormatFactory2.getDisplayTime(new Date(),
							"yyyyMMdd_hhmmss")).toString();
			cTime = TimeFormatFactory2.getDateFormat(new Date());
		}
		adminView.setText(adminName);
		adminView.setOnClickListener(clickListener);
		return contentView;
	}

	ISelectedChanged spinnerSelectedChangedListener = new ISelectedChanged() {

		public Object onSelectedChanged(View v, Object o) {
			int vid = v.getId();
			switch (vid) {
			case R.id.fragment_sign_edit_cause:
				keyCause = o.toString();
				break;
			default:
				break;
			}
			return null;
		}
	};

	void loadSignInfo(String id) {
		try {
			String columns[] = new String[] { SignTable.field_address,
					SignTable.field_admin, SignTable.field_id,
					SignTable.field_cause, SignTable.field_type,
					SignTable.field_notes, SignTable.field_user,
					SignTable.field_time, SignTable.field_status };
			String selection = new StringBuffer(SignTable.field_id)
					.append("=?").toString();
			String args[] = new String[] { id };
			ContentValues signValues = DBHelper.getDbHelper(context).doQuery(
					SignTable.name, columns, selection, args);

			causeSpinner.setSelectedItem(signValues
					.getAsString(SignTable.field_cause));

			try {
				adminCode = signValues.getAsString(SignTable.field_admin);
				adminName = DBHelper.getDbHelper(context).queryAdminFullName(
						adminCode);
			} catch (Exception e) {
				// TODO: handle exception
			}

			address = signValues.getAsString(SignTable.field_address);
			notes = signValues.getAsString(SignTable.field_notes);
			String timeValues = signValues.getAsString(SignTable.field_time);
			cTime = timeValues;
			addressView.setText(address);
			notesView.setText(notes);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class signSendTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		CircleProgressBusyView abv;
		String signUri = "";

		public signSendTask(Context c) {
			this.context = c;
		}

		@Override
		protected void onPreExecute() {
			abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			address = addressView.getText().toString();
			notes = notesView.getText().toString();
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
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
				signJsonObject.put("type", "304");
				signJsonObject.put("time", cTime);
				signJsonObject.put("location", null);
				JSONObject siteJson = new JSONObject();
				siteJson.put("admin", adminCode);
				siteJson.put("address", address);
				siteJson.put("cause", keyCause);
				siteJson.put("note", notes);
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
					Log.i("entity", entityString);
					JSONObject signBackJson = new JSONObject(entityString);

					if (signBackJson.getBoolean("succeed")) {
						DBHelper.getDbHelper(context).delete(SignTable.name,
								SignTable.field_id + "=?",
								new String[] { signId });
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
			} catch (InterruptedException e) {
				msg = "任务被中断";
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
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
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

	/**
	 * 
	 * @ClassName: caseSaveTask
	 * @Description: TODO
	 * @author leix@geo-k.cn
	 * @date 2014年5月13日
	 * 
	 */
	class signSaveTask extends AsyncTask<Void, Void, Boolean> {

		Context context;
		boolean slient;

		public signSaveTask(Context c, boolean slient) {
			this.context = c;
			this.slient = slient;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (slient)
				return;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_saveing));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);

			address = addressView.getText().toString();
			notes = notesView.getText().toString();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				ContentValues signValues = new ContentValues();
				signValues.put(SignTable.field_user, currentUser);
				signValues.put(SignTable.field_type, "site");
				signValues.putNull(SignTable.field_x);
				signValues.putNull(SignTable.field_y);
				signValues.putNull(SignTable.field_accuracy);
				signValues.put(SignTable.field_admin, adminCode);
				signValues.put(SignTable.field_address, address);
				signValues.put(SignTable.field_cause, keyCause);
				signValues.put(SignTable.field_notes, notes);

				String where = new StringBuffer(SignTable.field_id)
						.append("=?").toString();
				String args[] = new String[] { signId };
				int count = DBHelper.getDbHelper(context).queryCount(
						SignTable.name, null, where, args);
				if (count > 0) {
					int rows = DBHelper.getDbHelper(context).update(
							SignTable.name, signValues, where, args);
					succeed = (rows > 0);

				} else {
					signValues.put(SignTable.field_time, cTime);
					signValues.put(SignTable.field_id, signId);
					long row = DBHelper.getDbHelper(context).insert(
							SignTable.name, signValues);
					succeed = (row > -1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (slient)
				return;

			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (result) {
				GravityCenterToast
						.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
				VibratorUtil.Vibrate(getActivity(), 100);
			} else {
				GravityCenterToast
						.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
				VibratorUtil.Vibrate(getActivity(), 200);
			}
		}
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent(context, AdminChooserActivity.class);
			intent.putExtra(AdminChooserActivity.ADMIN_CODE, adminCode);
			getActivity().startActivityForResult(intent, Parameters.GET_ADMIN);
			getActivity().overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	};

	double sqm2Mu(double m) {
		return 0.0015d * m;
	}

	double mu2Sqm(double m) {
		return m / 0.0015d;
	}
}
