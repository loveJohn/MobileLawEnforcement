package com.chinadci.mel.mleo.ui.fragments;

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.OverseerAnnexesTable;
import com.chinadci.mel.mleo.ldb.OverseerCaseTable;
import com.chinadci.mel.mleo.ldb.OverseerInspectionTable;
import com.chinadci.mel.mleo.ldb.OverseerPatrolTable;
import com.chinadci.mel.mleo.ui.views.CaseView;
import com.chinadci.mel.mleo.ui.views.HisPatrolViewGroup;
import com.chinadci.mel.mleo.ui.views.InspectionView;
import com.chinadci.mel.mleo.ui.views.PatrolView;
import com.chinadci.mel.mleo.utils.CaseUtils;
/**
 * 
* @ClassName LandCaseOverseerFragment 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:46:48 
*
 */
@SuppressLint("SimpleDateFormat")
public class LandCaseOverseerFragment extends LandCaseViewpagerFragment
		implements OnClickListener {
	View overseerView;
	RadioButton qualifiedView;
	RadioButton unqualifiedView;
	EditText notEditText;
	Button cancelButton;
	Button sendButton;
	boolean qualified = true;
	String notes = "";
	PopupWindow popupWindow;
	EditText keyView;
	LinearLayout includeLayout;
	String caseId;
	CaseView caseInfoView;
	InspectionView inspectionView;

	PatrolView patrolView;
	HisPatrolViewGroup hisPatrolView;
	AlertDialog alertDialog;

	public void onClick(View v) {
		int vid = v.getId();
		switch (vid) {
		case R.id.overseer_do:
			notes = notEditText.getText().toString();
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(keyView.getWindowToken(), 0);
			new OverseerSendTask(context).execute();
			break;

		case R.id.overseer_cancel:
			popupWindow.dismiss();
			break;

		default:
			break;
		}
	}

	@Override
	public void handle(Object o) {
		if (popupWindow == null) {
			popupWindow = new PopupWindow(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			popupWindow.setAnimationStyle(R.style.slide_top_animation);
			popupWindow.setOutsideTouchable(false);
			popupWindow.setFocusable(true);
			popupWindow.setContentView(overseerView);
		}
		popupWindow.showAtLocation(contentView, Gravity.TOP, 0, 0);
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
		activityHandle.toolFragmentHandle(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		overseerView = inflater.inflate(R.layout.view_overseer_result, null);
		notEditText = (EditText) overseerView.findViewById(R.id.overseer_notes);
		qualifiedView = (RadioButton) overseerView
				.findViewById(R.id.overseer_qualified);
		unqualifiedView = (RadioButton) overseerView
				.findViewById(R.id.overseer_unqualified);
		sendButton = (Button) overseerView.findViewById(R.id.overseer_do);
		cancelButton = (Button) overseerView.findViewById(R.id.overseer_cancel);
		sendButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		qualifiedView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				qualified = isChecked;
			}
		});
		unqualifiedView
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						qualified = !isChecked;
					}
				});

		contentView = inflater.inflate(R.layout.fragment_case_search_viewpager,
				container, false);
		initViewpager();
		includeLayout = (LinearLayout) contentView
				.findViewById(R.id.fragment_case_search_viewpager_include);
		keyView = (EditText) contentView
				.findViewById(R.id.fragment_case_search_viewpager_key);
		keyView.setHint("输入案件编号，为空时随机抽取案件");
		keyView.setOnEditorActionListener(keyEditorActionListener);
		String userAdmin[] = DBHelper.getDbHelper(context).getUserAdmin(
				currentUser);
		if (userAdmin != null && userAdmin.length > 0 && userAdmin[0] != null
				&& !userAdmin[0].equals("")) {
			Calendar calendar = Calendar.getInstance(TimeZone
					.getTimeZone("GMT+08:00"));
			int year = calendar.get(Calendar.YEAR);
			if (userAdmin[0].length() == 6) {
				keyView.setText(new StringBuffer(userAdmin[0]).append("000")
						.append(year).toString());
			} else if (userAdmin[0].length() >= 9) {
				keyView.setText(new StringBuffer(userAdmin[0].substring(0, 9))
						.append(year).toString());
			}
		}
		addTabViews();
		return contentView;
	}

	OnEditorActionListener keyEditorActionListener = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| actionId == EditorInfo.IME_MASK_ACTION
					|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				String key = keyView.getText().toString();
				new caseRequestTask(context, key, currentUser).execute();
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(keyView.getWindowToken(), 0);

			}
			return true;
		}
	};

	void addTabViews() {
		try {
			inspectionView = new InspectionView(context);
			viewList.add(inspectionView);

			hisPatrolView = new HisPatrolViewGroup(context);
			hisPatrolView.setParentActivity(getActivity());
			viewList.add(hisPatrolView);

			caseInfoView = new CaseView(context);
			caseInfoView.setParentActivity(getActivity());
			viewList.add(caseInfoView);

			pagerAdapter = new ViewPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void refreshCaseInfo() {
		try {
			caseInfoView.setDataSource(caseId, OverseerCaseTable.name,
					OverseerAnnexesTable.name);
			inspectionView.setDataSource(caseId, OverseerInspectionTable.name);
			hisPatrolView.setDataSource(currentUser, caseId,
					OverseerPatrolTable.name, OverseerAnnexesTable.name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class OverseerSendTask extends AsyncTask<Void, Integer, Boolean> {
		Context context;
		String uri;
		Location location = null;
		String msg = null;
		AlertDialog alertDialog;

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在与服务器通讯，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		public OverseerSendTask(Context c) {
			try {
				this.context = c;
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_case_overseer))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_case_overseer))
						.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				msg = null;
				JSONObject obj = new JSONObject();
				obj.put("caseId", caseId);
				obj.put("user", currentUser);
				obj.put("notes", notes);
				obj.put("qualified", qualified);
				HttpResponse response = HttpUtils
						.httpClientExcutePost(uri, obj);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {
						msg = "上报成功!";
						return true;
					} else {
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			msg = "上报失败,请重试!";
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
			if (result) {
				if (popupWindow != null && popupWindow.isShowing())
					popupWindow.dismiss();
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	class caseRequestTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		String id;
		String user;
		AlertDialog alertDialog;

		public caseRequestTask(Context c, String caseId, String user) {
			this.context = c;
			this.id = caseId;
			this.user = user;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取案件详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_case_overseer))
						.append("?caseId=").append(id).toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_case_overseer))
								.append("?caseId=").append(id).toString();

				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						JSONObject trackJsonObject = backJson
								.getJSONObject("particular");
						if (trackJsonObject != null
								&& trackJsonObject != JSONObject.NULL) {
							JSONObject caseJsonObject = trackJsonObject
									.getJSONObject("case");
							caseId = caseJsonObject.getString("id");
							CaseUtils.getInstance().storeCaseFulldata(context,
									currentUser, trackJsonObject,
									OverseerCaseTable.name,
									OverseerAnnexesTable.name,
									OverseerPatrolTable.name,
									OverseerInspectionTable.name);
							return true;
						} else {
							try {
								msg = backJson.getString("案件信息为空");
							} catch (Exception e) {
								// TODO: handle exception
							}

							return false;
						}

					} else {
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			msg = "服务器返回异常";
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}

			if (result) {
				includeLayout.setVisibility(View.VISIBLE);
				refreshCaseInfo();
				viewPager.setCurrentItem(2);
				activityHandle.toolFragmentHandle(true);
			} else {
				includeLayout.setVisibility(View.GONE);
				activityHandle.toolFragmentHandle(false);
				Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}
}
