package com.chinadci.mel.mleo.ui.fragments;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ldb.OverseerAnnexesTable;
import com.chinadci.mel.mleo.ldb.OverseerCaseTable;
import com.chinadci.mel.mleo.ldb.OverseerInspectionTable;
import com.chinadci.mel.mleo.ldb.OverseerPatrolTable;
import com.chinadci.mel.mleo.ldb.WebCaseTable;
import com.chinadci.mel.mleo.ui.adapters.WebCaseAdapter;
import com.chinadci.mel.mleo.utils.CaseUtils;

/**
 * 
 * @ClassName LandOverseerListFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:47:12
 * 
 */
public class LandOverseerListFragment extends SearchListFragment {

	WebCaseAdapter adapter;

	@Override
	protected void initFragment() {
		super.initFragment();
		listView.setEmptyView(listEmptyView);
		keyView.setHint("输入案件编号，为空时随机抽取案件");
		requestView.setOnClickListener(requestClickListener);
		keyView.setOnEditorActionListener(keyEditorActionListener);
		adapter = new WebCaseAdapter(context, null);
		adapter.setActivity(getActivity());
		adapter.setTableSource(OverseerCaseTable.name,
				OverseerAnnexesTable.name, OverseerPatrolTable.name,
				OverseerInspectionTable.name);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listItemClickListener);
		refreshAdapter();
	}

	OnClickListener requestClickListener = new OnClickListener() {
		public void onClick(View v) {
			keyEditorActionListener.onEditorAction(keyView,
					EditorInfo.IME_ACTION_SEARCH, null);

		}
	};

	void refreshAdapter() {
		try {
			String columns[] = new String[] { CaseTable.field_id,
					CaseTable.field_parties, CaseTable.field_illegalArea,
					CaseTable.field_address, WebCaseTable.field_status_text,
					CaseTable.field_mTime, };

			String selection = new StringBuffer(OverseerCaseTable.field_inUser)
					.append("=?").toString();
			String args[] = new String[] { currentUser };
			String order = new StringBuffer(CaseTable.field_id).append(" desc")
					.toString();
			ArrayList<ContentValues> values = DBHelper.getDbHelper(context)
					.doQuery(OverseerCaseTable.name, columns, selection, args,
							null, null, order, null);
			if (values != null) {
				adapter.setDataSet(values);
				adapter.notifyDataSetChanged();
			} else {
				adapter.setDataSet(null);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	void showOverseerDetail(String id) {

		Bundle bundle = new Bundle();
		bundle.putString(Parameters.CASE_ID, id);

		activityHandle.replaceTitle(getString(R.string.mitem_supervise));
		activityHandle.replaceToolFragment(new ToolLandOverseer(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
		activityHandle.replaceContentFragment(new LandOverseerFragment(),
				bundle, R.anim.slide_in_right, R.anim.slide_out_left);
	}

	OnItemClickListener listItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			ContentValues cv = (ContentValues) adapter.getItem(i);
			String id = cv.getAsString(InspectionCaseTable.field_id);
			showOverseerDetail(id);
		}
	};

	OnEditorActionListener keyEditorActionListener = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| actionId == EditorInfo.IME_MASK_ACTION
					|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				String k = keyView.getText().toString();
				if (k == null)
					k = "";
				new RequestTask().execute(k);
			}
			return true;
		}
	};

	class RequestTask extends AsyncTask<String, Integer, Boolean> {
		String msg = "";
		AlertDialog alertDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取案件详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				String key = params[0];
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_case_overseer))
						.append("?caseId=").append(key).append("&user=?")
						.append(currentUser).toString() : new StringBuffer(
						appUri).append("/")
						.append(context.getString(R.string.uri_case_overseer))
						.append("?caseId=").append(key).append("&user=")
						.append(currentUser).toString();
				Log.i("overseer_search_uri", uri);
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("track_search_entiry", entiryString);
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						JSONArray caseJsonArray = backJson
								.getJSONArray("particulars");
						if (caseJsonArray != null && caseJsonArray.length() > 0) {
							for (int i = 0; i < caseJsonArray.length(); i++) {
								JSONObject caseJsonObject = caseJsonArray
										.getJSONObject(i);
								if (caseJsonObject != null
										&& caseJsonObject != JSONObject.NULL) {
									CaseUtils.getInstance().storeCaseFulldata(
											context, currentUser,
											caseJsonObject,
											OverseerCaseTable.name,
											OverseerAnnexesTable.name,
											OverseerPatrolTable.name,
											OverseerInspectionTable.name);
								}
							}
							return true;
						} else {
							msg = "没有抽取到案件";
							return false;
						}
					} else {
						msg = backJson.getString("msg");
						return false;
					}
				} else {
					msg = "服务器返回异常";
					return false;
				}
			} catch (Exception e) {
				if (e instanceof ConnectTimeoutException) {
					msg = "连接服务器超时";
					return false;
				}
				msg = "获取案件信息发生异常";
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
			if (result) {
				refreshAdapter();
				Toast.makeText(context, "数据已刷新", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
		}
	}

}
