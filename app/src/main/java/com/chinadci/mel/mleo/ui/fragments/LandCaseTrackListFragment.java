package com.chinadci.mel.mleo.ui.fragments;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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

/**
 * 
 * @ClassName LandCaseTrackListFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:46:54
 * 
 */
@SuppressLint("SimpleDateFormat")
public class LandCaseTrackListFragment extends ContentFragment {
	ListView finishListView;
	ListView unfinishListView;
	View listEmptyView;
	View rootView;
	Switch statusSwitch;
	RadioButton blzRadioButton;
	RadioButton ybjRadioButton;
	RadioGroup conditionRadioGroup;
	ViewSwitcher viewSwitcher;
	ListViewRefreshVessel unfinishVessel;
	ListViewRefreshVessel finishVessel;

	WebCaseAdapter finishAdapter;
	WebCaseAdapter unfinishAdapter;
	SimpleDateFormat tableFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat viewFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

	@Override
	public void handle(Object o) {
		// TODO Auto-generated method stub
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
		unfinishListView = (ListView) rootView
				.findViewById(R.id.fragment_casetrack_listview_unfinish);
		listEmptyView = rootView.findViewById(R.id.fragment_casetrack_nodata);
		statusSwitch = (Switch) rootView
				.findViewById(R.id.fragment_casetrack_switch);
		viewSwitcher = (ViewSwitcher) rootView
				.findViewById(R.id.fragment_casetrack_view_switcher);
		unfinishVessel = (ListViewRefreshVessel) rootView
				.findViewById(R.id.fragment_casetrack_vessel_unfinish);
		finishVessel = (ListViewRefreshVessel) rootView
				.findViewById(R.id.fragment_casetrack_vessel_finish);
		blzRadioButton = (RadioButton) rootView
				.findViewById(R.id.fragment_casetrack_blz);
		ybjRadioButton = (RadioButton) rootView
				.findViewById(R.id.fragment_casetrack_ybj);
		conditionRadioGroup = (RadioGroup) rootView
				.findViewById(R.id.fragment_casetrack_condition);
		conditionRadioGroup.setOnCheckedChangeListener(conditionChangeListener);
		unfinishVessel.setPullupEnable(false);
		finishVessel.setPullupEnable(false);

		unfinishAdapter = new WebCaseAdapter(context, null);
		unfinishAdapter.setActivity(getActivity());
		unfinishListView.setAdapter(unfinishAdapter);

		finishAdapter = new WebCaseAdapter(context, null);
		finishAdapter.setActivity(getActivity());
		finishListView.setAdapter(finishAdapter);

		finishListView.setOnItemClickListener(listItemClickListener);
		unfinishListView.setOnItemClickListener(listItemClickListener);
		statusSwitch.setOnCheckedChangeListener(statusCheckedChangeListener);

		finishVessel.addVesselPullListener(finishedVesselPullListener);
		unfinishVessel.addVesselPullListener(unfinishedVesselPullListener);
			refreshList(0);
		return rootView;
	}

	android.widget.RadioGroup.OnCheckedChangeListener conditionChangeListener = new android.widget.RadioGroup.OnCheckedChangeListener() {

		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.fragment_casetrack_blz) {
				viewSwitcher.setDisplayedChild(0);
				refreshList(0);
			} else if (checkedId == R.id.fragment_casetrack_ybj) {
				viewSwitcher.setDisplayedChild(1);
				refreshList(1);
			}

		}

	};

	void refreshList(int status) {
		try {
			String columns[] = new String[] { TrackCaseTable.field_id,
					TrackCaseTable.field_parties,
					TrackCaseTable.field_status_text,
					TrackCaseTable.field_address,
					TrackCaseTable.field_illegalArea,
					TrackCaseTable.field_mTime };
			String selection = new StringBuffer(TrackCaseTable.field_inUser)
					.append("=? and ").append(TrackCaseTable.field_status)
					.append("=?").toString();
			String args[] = new String[] { currentUser, String.valueOf(status) };
			String order = new StringBuffer(TrackCaseTable.field_mTime).append(
					" desc").toString();
			ArrayList<ContentValues> values = DBHelper.getDbHelper(context)
					.doQuery(TrackCaseTable.name, columns, selection, args,
							null, null, order, null);

			if (status == 0) {
				if (values != null && values.size() > 0) {
					unfinishAdapter.setDataSet(values);
				} else {
					unfinishAdapter.setDataSet(null);
				}
				unfinishAdapter.notifyDataSetChanged();
			} else if (status == 1) {
				if (values != null && values.size() > 0) {
					finishAdapter.setDataSet(values);
				} else {
					finishAdapter.setDataSet(null);
				}
				finishAdapter.notifyDataSetChanged();
			}
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
						new caseGetTask(context, 1,0, currentUser).execute();
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
						unfinishVessel.finishRefresh();
					} else {
						new caseGetTask(context, 0,0, currentUser).execute();
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
			if (isChecked) {
				viewSwitcher.setDisplayedChild(1);
				refreshList(1);
			} else {
				viewSwitcher.setDisplayedChild(0);
				refreshList(0);
			}
		}
	};

	class caseGetTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		int status;
		String user;
		AlertDialog alertDialog;
		int iswpzf; 

		public caseGetTask(Context c, int status,int iswpzf, String user) {
			this.context = c;
			this.status = status;
			this.iswpzf =iswpzf;
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
						.append(status)
						.append("&is_wpzf=").append(iswpzf).toString();
				Log.i("track_search_url", uri);
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri,
						30000);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("track_search_entiry", entiryString);
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
									CaseUtils.getInstance().storeCaseFulldata2(
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
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (status == 1)
				finishVessel.finishRefresh();
			if (status == 0)
				unfinishVessel.finishRefresh();

			if (result) {
				refreshList(status);
				Toast.makeText(context, "数据已刷新", Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
