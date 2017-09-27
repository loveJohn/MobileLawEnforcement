package com.chinadci.mel.mleo.ui.fragments;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.ldb.TrackMineralAnnexesTable;
import com.chinadci.mel.mleo.ldb.TrackMineralHcTable;
import com.chinadci.mel.mleo.ldb.TrackMineralTable;
import com.chinadci.mel.mleo.ui.views.MineralHcView;
import com.chinadci.mel.mleo.ui.views.MineralView;
import com.chinadci.mel.mleo.utils.MineralCaseUtils;

/**
 * 
 * @ClassName LandCaseTrackSearchFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:46:57
 * 
 */
@SuppressLint("SimpleDateFormat")
public class MineralTrackSearchFragment extends MineralHcPageFragment{

	EditText keyView;
	Button requestView;
	LinearLayout includeLayout;
	String caseId;
	MineralView mineralInfoView;
	MineralHcView mineralhcView;

	LinearLayout mineralHc;
	
	AlertDialog alertDialog;

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		super.refreshUi();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(R.layout.fragment_mineral_search_viewpager,
				container, false);
		includeLayout = (LinearLayout) contentView
				.findViewById(R.id.fragment_mineralhc_search_viewpager_include);
		initViewpager();
		
		keyView = (EditText) contentView
				.findViewById(R.id.fragment_case_search_viewpager_key);
		requestView = (Button) contentView
				.findViewById(R.id.fragment_case_search_viewpager_get);
		keyView.setOnEditorActionListener(keyEditorActionListener);
		requestView.setOnClickListener(requestClickListener);

//		String userAdmin[] = DBHelper.getDbHelper(context).getUserAdmin(
//				currentUser);
//		if (userAdmin != null && userAdmin.length > 0 && userAdmin[0] != null
//				&& !userAdmin[0].equals("")) {
//			Calendar calendar = Calendar.getInstance(TimeZone
//					.getTimeZone("GMT+08:00"));
//			int year = calendar.get(Calendar.YEAR);
//			if (userAdmin[0].length() == 6) {
//				keyView.setText(new StringBuffer(userAdmin[0]).append("000")
//						.append(year).toString());
//			} else if (userAdmin[0].length() >= 9) {
//				keyView.setText(new StringBuffer(userAdmin[0].substring(0, 9))
//						.append(year).toString());
//			}
//		}
		addTabViews();
		return contentView;
	}

	OnClickListener requestClickListener = new OnClickListener() {
		public void onClick(View v) {
			keyEditorActionListener.onEditorAction(keyView,
					EditorInfo.IME_ACTION_SEARCH, null);

		}
	};

	OnEditorActionListener keyEditorActionListener = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| actionId == EditorInfo.IME_MASK_ACTION
					|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				String key = keyView.getText().toString();
				if (key != null && !key.equals("")) {
					new caseRequestTask(context, key, currentUser).execute();
					InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(keyView.getWindowToken(), 0);
				} else {
					Toast.makeText(context, "请输入案件编号", Toast.LENGTH_SHORT)
							.show();
				}
			}
			return true;
		}
	};

	void addTabViews() {
		try {
			mineralhcView = new MineralHcView(context);
			mineralhcView.setParentActivity(getActivity());
			mineralhcView.noneTitle();
			viewList.add(mineralhcView);
			
			mineralInfoView = new MineralView(context);
			mineralInfoView.setParentActivity(getActivity());
			viewList.add(mineralInfoView);

			

			
			pagerAdapter = new MineralHcPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void refreshCaseInfo() {
		try {
			mineralInfoView.setDataSource(currentUser,caseId,  TrackMineralTable.name,
					TrackMineralAnnexesTable.name);
			mineralhcView.setDataSource(caseId+"1", TrackMineralHcTable.name,TrackMineralAnnexesTable.name);
			
		} catch (Exception e) {
			e.printStackTrace();
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
						.append(context
								.getString(R.string.uri_mineralhc_log))
						.append("?caseId=").append(id).toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_mineralhc_log))
								.append("?caseId=").append(id).toString();

				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				Log.i("uri_mineraltrack_service", uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("track_search_entiry", entiryString);
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						JSONObject trackJsonObject = backJson
								.getJSONObject("particular");
						if (trackJsonObject != null
								&& trackJsonObject != JSONObject.NULL) {
							caseId = MineralCaseUtils.getInstance().storeTrackMineralFulldata(context, currentUser, trackJsonObject, 
									TrackMineralTable.name, TrackMineralAnnexesTable.name, TrackMineralHcTable.name,3);
							
							return true;
						} else {
							msg = backJson.getString("案件信息为空");
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
				//viewPager.setCurrentItem(1);
				
			} else {
				includeLayout.setVisibility(View.GONE);
				Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}
}
