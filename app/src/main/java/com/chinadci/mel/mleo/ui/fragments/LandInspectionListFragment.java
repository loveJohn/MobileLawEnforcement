/**
 * 土地执法》案件核查（快速处置）列表
 */
package com.chinadci.mel.mleo.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectTable;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.adapters.WebCaseAdapter;
import com.chinadci.mel.mleo.utils.CaseUtils;

@SuppressLint("SimpleDateFormat")
public class LandInspectionListFragment extends SearchListFragment {
	WebCaseAdapter adapter;
	SimpleDateFormat tableFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat viewFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	String defCaseId;

	@Override
	public void handle(Object o) {
		super.handle(o);
		try {
			String smsHandle = o.toString();
			String sh[] = smsHandle.split(",");
			defCaseId = sh[2];
			keyView.setText(defCaseId);
		} catch (Exception e) {

		}
	}

	@Override
	public void refreshUi() {
		super.refreshUi();
		initList();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
		activityHandle.replaceTitle("案件查询");
	}

	@Override
	protected void initFragment() {
		super.initFragment();
		listView.setEmptyView(listEmptyView);
		adapter = new WebCaseAdapter(context, null);
		adapter.setActivity(getActivity());
		adapter.setTableSource(InspectionCaseTable.name, CaseAnnexesTable.name,
				CasePatrolTable.name, CaseInspectTable.name);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listItemClickListener);
		if (defCaseId != null)
			keyView.setText(defCaseId);

		keyView.setOnEditorActionListener(keyEditorActionListener);
		requestView.setOnClickListener(requestClickListener);
		initList();

	}

	void initList() {
		try {
			String columns[] = new String[] { InspectionCaseTable.field_id,
					InspectionCaseTable.field_bh,
					InspectionCaseTable.field_parties,
					InspectionCaseTable.field_address,
					InspectionCaseTable.field_illegalSubject,
					InspectionCaseTable.field_illegalArea,
					InspectionCaseTable.field_mTime,
					InspectionCaseTable.field_status_text };
			String selection = new StringBuffer(
					InspectionCaseTable.field_inUser).append("=?").toString();
			String args[] = new String[] { currentUser };
			String order = new StringBuffer(InspectionCaseTable.field_mTime)
					.append(" desc").toString();
			ArrayList<ContentValues> values = DBHelper.getDbHelper(context)
					.doQuery(InspectionCaseTable.name, columns, selection,
							args, null, null, order, null);
			if (values != null) {
				adapter.setDataSet(values);
				adapter.notifyDataSetChanged();
			} else {
				adapter.setDataSet(null);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnItemClickListener listItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			final ContentValues cv = (ContentValues) adapter.getItem(i);			
			final String caseId = cv.getAsString(InspectionCaseTable.field_bh);
//			CheckCaseTask task = new CheckCaseTask(context, new TaskResultHandler<Boolean>() {
//				public void resultHander(Boolean result) {
//					if(!result){
//					}else{
//						
//					}
//				}
//			});
//			task.execute(caseId);
			
			//点击后再请求一次
			new inspectionGetTask(context,caseId, currentUser).execute();
			String id = cv.getAsString(InspectionCaseTable.field_id);
			showInspectionDetail(id);
		}
	};

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
				String k = keyView.getText().toString();
				if (k != null && !k.equals("")) {
					new inspectionGetTask(context,
							keyView.getText().toString(), currentUser)
							.execute();
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

	void showInspectionDetail(String id) {

		Bundle bundle = new Bundle();
		bundle.putString(Parameters.CASE_ID, id);

		activityHandle.replaceTitle(getString(R.string.mitem_inspect2));
		activityHandle.replaceToolFragment(new ToolLandPatrol(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
		activityHandle.replaceContentFragment(
				new LandInspectionEditeFragment(), bundle,
				R.anim.slide_in_right, R.anim.slide_out_left);
	}

	class inspectionGetTask extends AsyncTask<Void, Integer, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String user;
		AlertDialog alertDialog;

		public inspectionGetTask(Context c, String caseId, String user) {
			this.context = c;
			this.caseId = caseId;
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
								.getString(R.string.uri_inspection_service))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_inspection_service))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString();
				Log.i("ydzf", "inspection_search_uri="+uri);
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						caseId = CaseUtils.getInstance().storeCaseFulldata(
								context, currentUser, backJson,
								InspectionCaseTable.name,
								CaseAnnexesTable.name, CasePatrolTable.name,
								CaseInspectTable.name);

						// 保存核查信息
						JSONObject insObject;
						try {
							insObject = backJson.getJSONObject("inspection");
						} catch (Exception e) {
							insObject = null;
						}

						try {
							if (insObject == null) {
								ContentValues inspectValues = CaseUtils
										.getInstance()
										.caseJson2ImspectionContentValues(
												backJson.getJSONObject("case"));
								int delInspectCount = DBHelper
										.getDbHelper(context)
										.delete(CaseInspectTable.name,
												new StringBuffer(
														InspectTable.field_caseId)
														.append("=?")
														.toString(),
												new String[] { inspectValues
														.getAsString(InspectTable.field_caseId) });
								inspectValues.put(InspectTable.field_status, 0);
								DBHelper.getDbHelper(context).insert(
										CaseInspectTable.name, inspectValues);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							JSONObject situationObject = backJson
									.getJSONObject("situation");
							if (situationObject != null
									&& situationObject != JSONObject.NULL) {
								CaseUtils.getInstance().storeCaseSituation(
										context, caseId, situationObject);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						publishProgress(-1);
						msg = caseId;
						return true;
					} else {
						publishProgress(-1);
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			publishProgress(-1);
			msg = "获取案件详情发生异常";
			return false;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			try {
				int i = values[0];
				if (i == -1) {
					if (alertDialog != null && alertDialog.isShowing())
						alertDialog.dismiss();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();

				if (result) {
					Bundle bundle = new Bundle();
					bundle.putString(Parameters.CASE_ID, msg);
					activityHandle
							.replaceTitle(getString(R.string.mitem_inspect));
					activityHandle.replaceToolFragment(new ToolLandPatrol(),
							null, R.anim.slide_in_top, R.anim.slide_out_bottom);
					activityHandle.replaceContentFragment(
							new LandInspectionEditeFragment(), bundle,
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else {
					Toast toast = Toast.makeText(context, msg,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
