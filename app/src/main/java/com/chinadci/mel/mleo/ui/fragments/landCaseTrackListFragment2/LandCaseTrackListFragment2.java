package com.chinadci.mel.mleo.ui.fragments.landCaseTrackListFragment2;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.views.pullvessel.IVesselPullListener;
import com.chinadci.mel.android.views.pullvessel.ListViewRefreshVessel;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectTable;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ldb.TrackAnnexesTable;
import com.chinadci.mel.mleo.ldb.TrackCaseTable2;
import com.chinadci.mel.mleo.ldb.TrackInspectionTable;
import com.chinadci.mel.mleo.ldb.TrackPatrolTable;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.adapters.WebCaseAdapter;
import com.chinadci.mel.mleo.ui.adapters.WebCaseAdapter2;
import com.chinadci.mel.mleo.ui.fragments.ContentFragment;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask2;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj2;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetCaseAloneInfoTask;
import com.chinadci.mel.mleo.utils.CaseUtils;

/**
 */
@SuppressLint("SimpleDateFormat")
public class LandCaseTrackListFragment2 extends ContentFragment {
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

	WebCaseAdapter2 finishAdapter;
	WebCaseAdapter unfinishAdapter;
	SimpleDateFormat tableFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat viewFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

	@Override
	public void handle(Object o) {
		super.handle(o);
	}

	@Override
	public void refreshUi() {
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

		finishAdapter = new WebCaseAdapter2(getActivity(), null);
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
			String columns[] = new String[] { TrackCaseTable2.field_id,
					TrackCaseTable2.field_parties,
					TrackCaseTable2.field_status_text,
					TrackCaseTable2.field_address,
					TrackCaseTable2.field_illegalArea,
					TrackCaseTable2.field_mTime };
			String selection = new StringBuffer(TrackCaseTable2.field_inUser)
					.append("=? and ").append(TrackCaseTable2.field_status)
					.append("=?").toString();
			String args[] = new String[] { currentUser, String.valueOf(status) };
			String order = new StringBuffer(TrackCaseTable2.field_mTime)
					.append(" desc").toString();
			ArrayList<ContentValues> values = DBHelper.getDbHelper(context)
					.doQuery(TrackCaseTable2.name, columns, selection, args,
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
		// Bundle bundle = new Bundle();
		// bundle.putString(Parameters.CASE_ID, id);
		// activityHandle.replaceTitle(getString(R.string.title_case_info));
		// activityHandle.replaceToolFragment(new ToolFragment(), null,
		// R.anim.slide_in_top, R.anim.slide_out_bottom);
		// activityHandle.replaceContentFragment(new LandCaseTrackFragment2(),
		// bundle, R.anim.slide_in_right, R.anim.slide_out_left);
		SimpleAj2 aj = DbUtil.getSimpleAj2ById(getActivity(), id);
		if (aj != null) {
			new inspectionGetTask2(context, aj.getBh(), currentUser).execute(
					aj.getBh(), aj.getHx(), aj.getHxfxjg(), aj.getX(),
					aj.getY(), aj.getAjly(), aj.getXxdz(), aj.getXzqyid(),
					aj.getAjid());
		} else {
			GetCaseAloneInfoTask tasst = new GetCaseAloneInfoTask(
					getActivity(), new TaskResultHandler<List<Object>>() {
						public void resultHander(List<Object> result) {
							if (result != null && result.size() > 0
									&& result.get(0) instanceof SimpleAj2) {
								new inspectionGetTask2(context,
										((SimpleAj2) result.get(0)).getBh(),
										currentUser).execute(
										((SimpleAj2) result.get(0)).getBh(),
										((SimpleAj2) result.get(0)).getHx(),
										((SimpleAj2) result.get(0)).getHxfxjg(),
										((SimpleAj2) result.get(0)).getX(),
										((SimpleAj2) result.get(0)).getY(),
										((SimpleAj2) result.get(0)).getAjly(),
										((SimpleAj2) result.get(0)).getXxdz(),
										((SimpleAj2) result.get(0)).getXzqyid(),
										((SimpleAj2) result.get(0)).getAjid());
							}
						}
					});
			tasst.execute(id);
		}
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
						new caseGetTask(context, 1, 1, currentUser).execute();
					}
			} catch (Exception e) {
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
						new caseGetTask(context, 0, 1, currentUser).execute();
					}
			} catch (Exception e) {
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

		public caseGetTask(Context c, int status, int iswpzf, String user) {
			this.context = c;
			this.status = status;
			this.iswpzf = iswpzf;
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
						.append(status).append("&is_wpzf=").append(iswpzf)
						.toString();
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
											TrackCaseTable2.name,
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

	class inspectionGetTask2 extends AsyncTask<Object, Integer, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String user;
		AlertDialog alertDialog;

		private String bh;
		private String redline;
		private String redline_result;
		private double x;
		private double y;
		private String ajly;
		private String dz;

		private String jcbh;
		private String jcmj;
		private String zygdmj;
		private String xfsj;

		private String xzquid;
		private String ajid;

		private boolean isServerBackFalse = false;

		public inspectionGetTask2(Context c, String caseId, String user) {
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

		@SuppressWarnings("unused")
		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				bh = (String) params[0];
				redline = (String) params[1];
				redline_result = (String) params[2];
				x = (Double) params[3];
				y = (Double) params[4];
				ajly = (String) params[5];
				dz = (String) params[6];
				xzquid = (String) params[7];
				ajid = (String) params[8];
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
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					DbUtil.deleteINSPECTIONGETTASK2DbDatasByBh(context, bh);
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("inspection_search_entiry", entiryString);
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						caseId = CaseUtils.getInstance().storeCaseFulldata(
								context, currentUser, backJson,
								InspectionCaseTable.name,
								CaseAnnexesTable.name, CasePatrolTable.name,
								CaseInspectTable.name);
						jcbh = backJson.getJSONObject("case").getString("jcbh");
						jcmj = backJson.getJSONObject("case").getString("jcmj")
								+ " ㎡";
						zygdmj = backJson.getJSONObject("case").getString("zygdmj")
								+ " ㎡";
						xfsj = backJson.getJSONObject("case").getString("xfsj");
						
						//2017 02 15
						try {
							JSONArray arraysss = backJson.getJSONArray("patrols");
							if(arraysss.length()>0){
								for(int kk = 0;kk<arraysss.length();kk++){
									JSONObject objjj = arraysss.getJSONObject(kk);
									String id = objjj.optString("id");
									String fxjg = objjj.optString("fxjg");
									DbUtil.deletePatrolsById(context, id);
									DbUtil.insertPatrols(context, id, fxjg);
								}	
							}
						} catch (Exception e1) {
						}
						
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
						DbUtil.insertINSPECTIONGETTASK2DbDatas(context, bh,
								redline, x + "", y + "", ajly, dz, jcbh, jcmj,zygdmj,
								xfsj, entiryString);
						return true;
					} else {
						publishProgress(-1);
						msg = backJson.getString("msg");
						isServerBackFalse = true;
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

		@SuppressWarnings("unused")
		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				if (result) {
					Intent intent = new Intent(getActivity(),
							ModuleRealizeActivity.class);
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 210421);
					intent.putExtra(Parameters.CASE_ID, msg);
					intent.putExtra("title", bh);
					intent.putExtra("hx", redline);
					intent.putExtra("hx_result", redline_result);
					intent.putExtra("x", x);
					intent.putExtra("y", y);
					intent.putExtra("ajly", ajly);
					intent.putExtra("dz", dz);
					intent.putExtra("jcbh", jcbh);
					intent.putExtra("jcmj", jcmj);
					intent.putExtra("xfsj", xfsj);
					intent.putExtra("xzqhid", xzquid);
					intent.putExtra("ajid", ajid);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else {
					if (!isServerBackFalse) {
						Toast toast = Toast.makeText(context, msg
								+ ",读取缓存数据中...", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						InspectionGetTask2 ist2 = DbUtil
								.getInspectionGetTask2ByBh(context, bh);
						if (ist2 != null) {
							try {
								String entiryString = ist2.getEntiryString();
								JSONObject backJson = new JSONObject(
										entiryString);
								boolean succeed = backJson
										.getBoolean("succeed");
								if (succeed) {
									caseId = CaseUtils.getInstance()
											.storeCaseFulldata(context,
													currentUser, backJson,
													InspectionCaseTable.name,
													CaseAnnexesTable.name,
													CasePatrolTable.name,
													CaseInspectTable.name);
									jcbh = backJson.getJSONObject("case")
											.getString("jcbh");
									jcmj = backJson.getJSONObject("case")
											.getString("jcmj") + " ㎡";
									xfsj = backJson.getJSONObject("case")
											.getString("xfsj");
									// 保存核查信息
									JSONObject insObject;
									try {
										insObject = backJson
												.getJSONObject("inspection");
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
											inspectValues.put(
													InspectTable.field_status,
													0);
											DBHelper.getDbHelper(context)
													.insert(CaseInspectTable.name,
															inspectValues);
										}

									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										JSONObject situationObject = backJson
												.getJSONObject("situation");
										if (situationObject != null
												&& situationObject != JSONObject.NULL) {
											CaseUtils.getInstance()
													.storeCaseSituation(
															context, caseId,
															situationObject);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								return;
							}
							Intent intent = new Intent(getActivity(),
									ModuleRealizeActivity.class);
							intent.putExtra(ModuleActivity.TAG_MODULE_ID,
									210421);
							intent.putExtra(Parameters.CASE_ID, caseId);
							intent.putExtra("title", bh);
							intent.putExtra("hx", redline);
							intent.putExtra("hx_result", redline_result);
							intent.putExtra("x", x);
							intent.putExtra("y", y);
							intent.putExtra("ajly", ajly);
							intent.putExtra("dz", dz);
							intent.putExtra("jcbh", jcbh);
							intent.putExtra("jcmj", jcmj);
							intent.putExtra("xfsj", xfsj);
							intent.putExtra("xzqhid", xzquid);
							intent.putExtra("ajid", ajid);
							startActivity(intent);
							getActivity().overridePendingTransition(
									R.anim.slide_in_right,
									R.anim.slide_out_left);
						} else {
							Toast toast2 = Toast.makeText(context,
									"网络连接失败且缓存数据不存在", Toast.LENGTH_SHORT);
							toast2.setGravity(Gravity.CENTER, 0, 0);
							toast2.show();
						}
					} else {
						Toast toast = Toast.makeText(context, msg,
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

				}
			} catch (Exception e) {
			}
		}
	}
}
