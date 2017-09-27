package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectTable;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.adapters.CommonAdapter;
import com.chinadci.mel.mleo.ui.adapters.ViewHolder;
import com.chinadci.mel.mleo.ui.fragments.base.BaseV4Fragment4Content;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask2;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj_Wpzf_find;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.fragments.data.task.CheXiaoTask;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetWpTbCzTask_Wpzf;
import com.chinadci.mel.mleo.ui.views.BadgeView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView.IXListViewListener;
import com.chinadci.mel.mleo.utils.CaseUtils;
import com.chinadci.mel.mleo.utils.NetWorkUtils;

public class LandSearchListFragment extends BaseV4Fragment4Content implements
		IXListViewListener {
	private EditText keyView;
	private Button requestView;
	private View mView;
	private XListView mListView;
	@SuppressWarnings("rawtypes")
	private CommonAdapter mAdapter;

	private String xzqh_id;
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = getActivity().getApplicationContext();
		activityHandle.replaceTitle("图斑查询");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_search_list2, container,
				false);
		keyView = (EditText) mView
				.findViewById(R.id.fragment_search_list_keyview);
		keyView.setHint("输入监测编号");
		requestView = (Button) mView
				.findViewById(R.id.fragment_search_list_get);
		mListView = (XListView) mView
				.findViewById(R.id.land_xListView);
		init();
		return mView;
	}

	private void init() {
		requestView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!"".equals(keyView.getText().toString())) {
					getDatas(keyView.getText().toString());
				} else {
					Toast.makeText(getActivity(), "请输入监测编号！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Object obj = (Object) mAdapter.getItem(position - 1);
				if (obj instanceof SimpleAj_Wpzf_find) {
					if (!((SimpleAj_Wpzf_find) obj).getAj().getAjly().equals("卫片执法")) {
					} else {
						xzqh_id = ((SimpleAj_Wpzf_find) obj).getQuid();
						new inspectionGetTask2(context, ((SimpleAj_Wpzf_find) obj)
								.getAj().getBh(), currentUser).execute(
								((SimpleAj_Wpzf_find) obj).getAj().getBh(),// 1
								((SimpleAj_Wpzf_find) obj).getAj().getHx(),// 2
								((SimpleAj_Wpzf_find) obj).getAj().getHxfxjg(),// 3
								((SimpleAj_Wpzf_find) obj).getAj().getX(),// 4
								((SimpleAj_Wpzf_find) obj).getAj().getY(),// 5
								((SimpleAj_Wpzf_find) obj).getAj().getAjly(),// 6
								((SimpleAj_Wpzf_find) obj).getAj().getXxdz(),// 7
								((SimpleAj_Wpzf_find) obj).getAj().getAjKey(),// 8
								((SimpleAj_Wpzf_find) obj).getAj().isApprover(),// 9
								((SimpleAj_Wpzf_find) obj).getAj().isRevoke()// 10
								);
					}
				}
			}
		});
	}

	private void getDatas(final String bh) {
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
					.show();
		} else {
			final AlertDialog alertDialog;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取图斑信息，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			GetWpTbCzTask_Wpzf task1 = new GetWpTbCzTask_Wpzf(context,
					new TaskResultHandler<List<Object>>() {
						@Override
						public void resultHander(List<Object> result) {
							if (alertDialog != null && alertDialog.isShowing()) {
								alertDialog.dismiss();
							}
							if (result != null) {
								if (result.size() > 0) {
									if (result.get(0) instanceof SimpleAj_Wpzf_find) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page3) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj_Wpzf_find) {
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf_find) item)
																	.getAj().isRevoke())) {
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setOnClickListener(new OnClickListener() {
																	public void onClick(
																			View arg0) {
																		AlertDialog.Builder builder = new Builder(
																				getActivity());
																		builder.setMessage("确认要撤销吗？");
																		builder.setTitle("提示");
																		builder.setPositiveButton(
																				"确认",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																						//2017 05 02
																						LayoutInflater factory = LayoutInflater.from(getActivity());
																						final View view = factory.inflate(R.layout.dialog_edit_view, null);
																						final EditText edit = (EditText) view.findViewById(R.id.editText);
																						new AlertDialog.Builder(getActivity())
																								.setTitle("请填写原因")
																								.setView(view)
																								.setPositiveButton(
																										"发送",
																										new android.content.DialogInterface.OnClickListener() {
																											@Override
																											public void onClick(final DialogInterface dialog,
																													int which) {
																												String input = edit.getText().toString();  
																									            if (input.equals("")) {  
																									                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																									            } else {
																									                CheXiaoTask task = new CheXiaoTask(
																															context,
																															new TaskResultHandler<Boolean>() {
																																public void resultHander(
																																		Boolean result) {
																																	if (result) {
																																		getDatas(bh);
																																		dialog.dismiss();
																																	}
																																}
																															});
																													task.execute(
																															((SimpleAj_Wpzf_find) item)
																																	.getAj().getId(),
																															((SimpleAj_Wpzf_find) item)
																																	.getAj().getAjKey(),
																																	currentUser,
																																	TimeFormatFactory2
																																	.getSourceTime(new Date()),input);
																									            }  
																											}
																										}).setNegativeButton("取消", null).create().show();
																					}
																				});
																		builder.setNegativeButton(
																				"取消",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																					}
																				});
																		builder.create()
																				.show();
																	}
																});
													} else {
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setVisibility(View.GONE);
													}
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf_find) item)
																	.getAj().getIsSave())) {
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#51B0FB"));
													} else {
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#000000"));
													}
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													if (((SimpleAj_Wpzf_find) item).getAj().getAjKey()
															.equals("1")
															&& ((SimpleAj_Wpzf_find) item)
																	.getAj().getLastState() != null) {
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setText(((SimpleAj_Wpzf_find) item)
																.getAj().getLastState());
													}else{
														mBadge.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj_Wpzf_find) item)
																	.getAj().getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj_Wpzf_find) item)
																	.getAj().getAjly());
													if (((SimpleAj_Wpzf_find) item)
															.getAj().getJcbh() != null
															&& !((SimpleAj_Wpzf_find) item)
																	.getAj().getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj_Wpzf_find) item)
																		.getAj().getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj_Wpzf_find) item)
																	.getAj().getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj_Wpzf_find) item)
																	.getAj().getSj());
												}
											}
										};
										mAdapter.notifyDataSetChanged();
										mListView.setAdapter(mAdapter);
									}
								}
							}
						}
					});
			task1.execute(currentUser, bh);
		}
	}

	private void showViewAnimation(View v) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);
		v.startAnimation(anim);
	}

	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
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

		private String ajKey;
		private String isApprover;
		private String isRevoke;
		
		private String glbh = null;

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
				ajKey = (String) params[7];
				isApprover = (String) params[8];
				isRevoke = (String) params[9];
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
						caseId = CaseUtils.getInstance()
								.storeCaseFulldata_Wpzf(context, currentUser,
										backJson, InspectionCaseTable.name,
										CaseAnnexesTable.name,
										CasePatrolTable.name,
										CaseInspectTable.name);

						jcbh = backJson.getJSONObject("case").getString("jcbh");
						jcmj = backJson.getJSONObject("case").getString("jcmj")
								+ " ㎡";
						zygdmj = backJson.getJSONObject("case").getString("zygdmj")
								+ " ㎡";
						xfsj = backJson.getJSONObject("case").getString("xfsj");
						
						if(backJson!=null&&backJson.has("glbh")){
							glbh = backJson.optString("glbh");	
						}

						// 2017 02 15
						try {
							JSONArray arraysss = backJson
									.getJSONArray("patrols");
							if (arraysss.length() > 0) {
								for (int kk = 0; kk < arraysss.length(); kk++) {
									JSONObject objjj = arraysss
											.getJSONObject(kk);
									String id = objjj.optString("id");// 其实是巡查编号不是案件id
									String fxjg = objjj.optString("fxjg");
									DbUtil.deletePatrolsById(context, id);
									DbUtil.insertPatrols(context, id, fxjg);
								}
							}
						} catch (Exception e1) {
						}

						// 2017 03 14
						try {
							JSONObject abbb = backJson
									.getJSONObject("situation");
							if (abbb != null && abbb != JSONObject.NULL) {
								JSONArray arraysss = abbb
										.getJSONArray("clqk_new");
								if (arraysss != null && arraysss.length() > 0) {
									for (int kk = 0; kk < arraysss.length(); kk++) {
										JSONObject objjj = arraysss
												.getJSONObject(kk);
										String key = objjj.optString("key");
										String value = objjj.optString("value");
										String parent = null;
										DbUtil.deleteclqk_nowByKey(context, key);
										DbUtil.insertclqk_now(context, key,
												value, parent);
										if (objjj.has("sub")) {
											JSONArray arraysss1 = objjj
													.getJSONArray("sub");
											if (arraysss1 != null
													&& arraysss1.length() > 0) {
												for (int kkk = 0; kkk < arraysss1
														.length(); kkk++) {
													JSONObject objjj1 = arraysss1
															.getJSONObject(kkk);
													String key1 = objjj1
															.optString("key");
													String value1 = objjj1
															.optString("value");
													String parent1 = key;
													DbUtil.deleteclqk_nowByKey(
															context, key1);
													DbUtil.insertclqk_now(
															context, key1,
															value1, parent1);
													if (objjj1.has("sub")) {
														JSONArray arraysss2 = objjj1
																.getJSONArray("sub");
														if (arraysss2 != null
																&& arraysss2
																		.length() > 0) {
															for (int kkkk = 0; kkkk < arraysss2
																	.length(); kkkk++) {
																JSONObject objjj2 = arraysss2
																		.getJSONObject(kkkk);
																String key2 = objjj2
																		.optString("key");
																String value2 = objjj2
																		.optString("value");
																String parent2 = key1;
																DbUtil.deleteclqk_nowByKey(
																		context,
																		key2);
																DbUtil.insertclqk_now(
																		context,
																		key2,
																		value2,
																		parent2);
															}
														}
													}
												}
											}
										}
									}
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
					if (ajKey.equals("2") && isApprover.equals("true")) {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21028);// 处理审批
					} else if ((ajKey.equals("2") && !isApprover.equals("true"))
							|| ajKey.equals("3")) {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21029);// 查看
					} else {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
					}
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
					intent.putExtra("xzqhid", xzqh_id);
					intent.putExtra("ajid", "401");
					intent.putExtra("ajKey", ajKey);
					intent.putExtra("isApprover", isApprover);
					intent.putExtra("isRevoke", isRevoke);
					intent.putExtra("glbh", glbh);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else {
					Toast toast = Toast.makeText(context, msg + ",读取缓存数据中...",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					InspectionGetTask2 ist2 = DbUtil.getInspectionGetTask2ByBh(
							context, bh);
					if (ist2 != null) {
						try {
							String entiryString = ist2.getEntiryString();
							JSONObject backJson = new JSONObject(entiryString);
							boolean succeed = backJson.getBoolean("succeed");
							if (succeed) {
								caseId = CaseUtils.getInstance()
										.storeCaseFulldata_Wpzf(context,
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
								
								if(backJson!=null&&backJson.has("glbh")){
									glbh = backJson.optString("glbh");	
								}

								// 2017 02 15
								try {
									JSONArray arraysss = backJson
											.getJSONArray("patrols");
									if (arraysss != null
											&& arraysss.length() > 0) {
										for (int kk = 0; kk < arraysss.length(); kk++) {
											JSONObject objjj = arraysss
													.getJSONObject(kk);
											String id = objjj.optString("id");
											String fxjg = objjj
													.optString("fxjg");
											DbUtil.deletePatrolsById(context,
													id);
											DbUtil.insertPatrols(context, id,
													fxjg);
										}
									}
								} catch (Exception e1) {
								}

								// 2017 03 14
								try {
									JSONObject abbb = backJson
											.getJSONObject("situation");
									if (abbb != null && abbb != JSONObject.NULL) {
										JSONArray arraysss = abbb
												.getJSONArray("clqk_new");
										if (arraysss != null
												&& arraysss.length() > 0) {
											for (int kk = 0; kk < arraysss
													.length(); kk++) {
												JSONObject objjj = arraysss
														.getJSONObject(kk);
												String key = objjj
														.optString("key");
												String value = objjj
														.optString("value");
												String parent = null;
												DbUtil.deleteclqk_nowByKey(
														context, key);
												DbUtil.insertclqk_now(context,
														key, value, parent);
												if (objjj.has("sub")) {
													JSONArray arraysss1 = objjj
															.getJSONArray("sub");
													if (arraysss1 != null
															&& arraysss1
																	.length() > 0) {
														for (int kkk = 0; kkk < arraysss1
																.length(); kkk++) {
															JSONObject objjj1 = arraysss1
																	.getJSONObject(kkk);
															String key1 = objjj1
																	.optString("key");
															String value1 = objjj1
																	.optString("value");
															String parent1 = key;
															DbUtil.deleteclqk_nowByKey(
																	context,
																	key1);
															DbUtil.insertclqk_now(
																	context,
																	key1,
																	value1,
																	parent1);
															if (objjj1
																	.has("sub")) {
																JSONArray arraysss2 = objjj1
																		.getJSONArray("sub");
																if (arraysss2 != null
																		&& arraysss2
																				.length() > 0) {
																	for (int kkkk = 0; kkkk < arraysss2
																			.length(); kkkk++) {
																		JSONObject objjj2 = arraysss2
																				.getJSONObject(kkkk);
																		String key2 = objjj2
																				.optString("key");
																		String value2 = objjj2
																				.optString("value");
																		String parent2 = key1;
																		DbUtil.deleteclqk_nowByKey(
																				context,
																				key2);
																		DbUtil.insertclqk_now(
																				context,
																				key2,
																				value2,
																				parent2);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								} catch (Exception e1) {
								}

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
												InspectTable.field_status, 0);
										DBHelper.getDbHelper(context).insert(
												CaseInspectTable.name,
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
										CaseUtils
												.getInstance()
												.storeCaseSituation(context,
														caseId, situationObject);
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
						if (ajKey.equals("2") && isApprover.equals("true")) {
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21028);// 处理审批
						} else if ((ajKey.equals("2") && !isApprover
								.equals("true")) || ajKey.equals("3")) {
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21029);// 查看
						} else {
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
						}
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
						intent.putExtra("xzqhid", xzqh_id);
						intent.putExtra("ajid", "401");
						intent.putExtra("ajKey", ajKey);
						intent.putExtra("isApprover", isApprover);
						intent.putExtra("isRevoke", isRevoke);
						intent.putExtra("glbh", glbh);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.slide_in_right, R.anim.slide_out_left);
					} else {
						Toast toast2 = Toast.makeText(context,
								"网络连接失败且缓存数据不存在", Toast.LENGTH_SHORT);
						toast2.setGravity(Gravity.CENTER, 0, 0);
						toast2.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
