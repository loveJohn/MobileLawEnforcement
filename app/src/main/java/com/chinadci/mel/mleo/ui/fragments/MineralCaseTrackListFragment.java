package com.chinadci.mel.mleo.ui.fragments;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.NetworkUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.views.pullvessel.IVesselPullListener;
import com.chinadci.mel.android.views.pullvessel.ListViewRefreshVessel;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ldb.TrackAnnexesTable;
import com.chinadci.mel.mleo.ldb.TrackCaseTable;
import com.chinadci.mel.mleo.ldb.TrackInspectionTable;
import com.chinadci.mel.mleo.ldb.TrackPatrolTable;
import com.chinadci.mel.mleo.ui.adapters.WebCaseAdapter;
import com.chinadci.mel.mleo.utils.CaseUtils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MineralCaseTrackListFragment extends ContentFragment{
	ListView finishListView;
	View listEmptyView;
	View rootView;
	ListViewRefreshVessel finishVessel;
	
	WebCaseAdapter finishAdapter;
	SimpleDateFormat tableFormat=new SimpleDateFormat("yyyy-mm-dd hh:mm");
	
	@Override 
	public void handle(Object o)
	{
		super.handle(o);
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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_casetrack_list,
				container, false);
		finishListView = (ListView) rootView
				.findViewById(R.id.fragment_casetrack_listview_finish);
		
		listEmptyView = rootView.findViewById(R.id.fragment_casetrack_nodata);
		
		finishVessel = (ListViewRefreshVessel) rootView
				.findViewById(R.id.fragment_casetrack_vessel_finish);

		finishVessel.setPullupEnable(false);

		finishAdapter = new WebCaseAdapter(context, null);
		finishAdapter.setActivity(getActivity());
		finishListView.setAdapter(finishAdapter);

//		finishListView.setOnItemClickListener(listItemClickListener);
//		
//		finishVessel.addVesselPullListener(finishedVesselPullListener);
		refreshList();
		return rootView;
	}
	
	void refreshList() {
		try {
			String columns[] = new String[] { TrackCaseTable.field_id,
					TrackCaseTable.field_parties,
					TrackCaseTable.field_illegalStatus,
					TrackCaseTable.field_address,
					TrackCaseTable.field_illegalType,
					TrackCaseTable.field_mTime };
			String selection = new StringBuffer(TrackCaseTable.field_inUser)
					.append("=?").toString();
			String args[] = new String[] { currentUser };
			String order = new StringBuffer(TrackCaseTable.field_mTime).append(
					" desc").toString();
			ArrayList<ContentValues> values = DBHelper.getDbHelper(context)
					.doQuery(TrackCaseTable.name, columns, selection, args,
							null, null, order, null);

			if (values != null && values.size() > 0) {
				finishAdapter.setDataSet(values);
			} else {
				finishAdapter.setDataSet(null);
			}
			finishAdapter.notifyDataSetChanged();
	
			if (values == null || values.size() < 1)
				Toast.makeText(context, "没有数据,你可以下拉更新", Toast.LENGTH_LONG)
						.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void showInspectionDetail(String id) {
		Bundle bundle = new Bundle();
		bundle.putString(Parameters.CASE_ID, id);
		activityHandle.replaceTitle(getString(R.string.title_case_info));
		activityHandle.replaceToolFragment(new ToolFragment(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
		activityHandle.replaceContentFragment(new LandCaseTrackFragment(),
				bundle, R.anim.slide_in_right, R.anim.slide_out_left);
	}
	OnItemClickListener listItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			ContentValues cv = (ContentValues) v.getTag();
			String id = cv.getAsString(InspectionCaseTable.field_id);
			showInspectionDetail(id);
		}
	};
	
	IVesselPullListener finishedVesselPullListener = new IVesselPullListener() {

		public Object doTask(int action) {
			try {
				if (action == IVesselPullListener.ACTION_PULL_DOWN)
					if (!NetworkUtils.checkNetwork(context)) {
						Toast.makeText(context, "无可用网络,请先设置网络连接",
								Toast.LENGTH_LONG).show();
						finishVessel.finishRefresh();
					} else {
						new caseGetTask(context, 1, currentUser).execute();
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
	};

	IVesselPullListener unfinishedVesselPullListener = new IVesselPullListener() {

		public Object doTask(int action) {
			try {
				if (action == IVesselPullListener.ACTION_PULL_DOWN)
					if (!NetworkUtils.checkNetwork(context)) {
						Toast.makeText(context, "无可用网络,请先设置网络连接",
								Toast.LENGTH_LONG).show();
						//unfinishVessel.finishRefresh();
					} else {
						new caseGetTask(context, 0, currentUser).execute();
					}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
	};

	OnCheckedChangeListener statusCheckedChangeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
				refreshList();
		}
	};

	class caseGetTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		int status;
		String user;
		AlertDialog alertDialog;

		public caseGetTask(Context c, int status, String user) {
			this.context = c;
			this.status = status;
			this.user = user;
		}

		@Override
		protected void onPreExecute() {
			// CircleProgressBusyView abv = new CircleProgressBusyView(context);
			// abv.setMsg("正在从服务器获取案件详情，请稍候...");
			// alertDialog = new AlertDialog.Builder(getActivity()).create();
			// alertDialog.show();
			// alertDialog.setCancelable(false);
			// alertDialog.getWindow().setContentView(abv);
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
								.getString(R.string.uri_casetrack_service))
						.append("?user=").append(user).append("&status=")
						.append(status).toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_casetrack_service))
						.append("?user=").append(user).append("&status=")
						.append(status).toString();

				HttpResponse response = HttpUtils.httpClientExcuteGet(uri,
						30000);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						JSONArray trackJsonArray = backJson
								.getJSONArray("particulars");
						if (trackJsonArray != null
								&& trackJsonArray.length() > 0) {
							for (int i = 0; i < trackJsonArray.length(); i++) {
								JSONObject trackJsonObject = trackJsonArray
										.getJSONObject(i);
								if (trackJsonObject != null
										&& trackJsonObject != JSONObject.NULL) {
									CaseUtils.getInstance().storeCaseFulldata(
											context, currentUser, status,
											trackJsonObject,
											TrackCaseTable.name,
											TrackAnnexesTable.name,
											TrackPatrolTable.name,
											TrackInspectionTable.name);
								}
							}
							return true;
						}
						msg = "服务器上没有与你相关的案件";
						return false;
					} else {
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

				if (e instanceof SocketTimeoutException) {
					msg = "连接服务器超时";
					return false;
				}
			}
			msg = "服务器返回异常";
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
				finishVessel.finishRefresh();

			if (result) {
				refreshList();
				Toast.makeText(context, "数据已刷新", Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
