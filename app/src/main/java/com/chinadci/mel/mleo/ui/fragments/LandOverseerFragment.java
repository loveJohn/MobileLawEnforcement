package com.chinadci.mel.mleo.ui.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.OverseerAnnexesTable;
import com.chinadci.mel.mleo.ldb.OverseerCaseTable;
import com.chinadci.mel.mleo.ldb.OverseerInspectionTable;
import com.chinadci.mel.mleo.ldb.OverseerPatrolTable;
import com.chinadci.mel.mleo.ui.views.CaseView;
import com.chinadci.mel.mleo.ui.views.HisPatrolViewGroup;
import com.chinadci.mel.mleo.ui.views.InspectionView;

/**
 */
@SuppressLint("SimpleDateFormat")
public class LandOverseerFragment extends LandCaseViewpagerFragment implements
		OnClickListener {
	View overseerView;
	RadioButton qualifiedView;
	RadioButton unqualifiedView;
	EditText notEditText;
	Button cancelButton;
	Button sendButton;
	boolean qualified = true;
	String notes = "";
	PopupWindow popupWindow;

	String caseId;
	CaseView caseInfoView;
	InspectionView inspectionView;
	HisPatrolViewGroup hisPatrolView;
	AlertDialog alertDialog;

	public void onClick(View v) {
		int vid = v.getId();
		switch (vid) {
		case R.id.overseer_do:
			notes = notEditText.getText().toString();
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(notEditText.getWindowToken(), 0);
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
		super.refreshUi();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		caseId = getArguments().getString(Parameters.CASE_ID);
		contentView = inflater.inflate(R.layout.fragment_case_viewpager,
				container, false);
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
		initViewpager();
		initFragment();

		return contentView;
	}

	void initFragment() {
		try {
			// 信息核查
			inspectionView = new InspectionView(context);
			inspectionView.setDataSource(caseId, OverseerInspectionTable.name);
			viewList.add(inspectionView);

			// 处理结果
			hisPatrolView = new HisPatrolViewGroup(context);
			hisPatrolView.setParentActivity(getActivity());
			hisPatrolView.setDataSource(currentUser, caseId,
					OverseerPatrolTable.name, OverseerAnnexesTable.name);
			viewList.add(hisPatrolView);

			// 上报信息
			caseInfoView = new CaseView(context);
			caseInfoView.setParentActivity(getActivity());
			caseInfoView.setDataSource(caseId, OverseerCaseTable.name,
					OverseerAnnexesTable.name);
			viewList.add(caseInfoView);

			pagerAdapter = new ViewPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
			viewPager.setCurrentItem(2);
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
						DBHelper.getDbHelper(context).delete(
								OverseerCaseTable.name,
								OverseerCaseTable.field_id + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								OverseerAnnexesTable.name,
								OverseerAnnexesTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								OverseerPatrolTable.name,
								OverseerPatrolTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								OverseerInspectionTable.name,
								OverseerInspectionTable.field_id + "=?",
								new String[] { caseId });
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
				getActivity().onBackPressed();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
