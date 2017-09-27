package com.chinadci.mel.mleo.ui.fragments.data.task;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
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
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask2;
import com.chinadci.mel.mleo.utils.CaseUtils;

/**
 * add teng.guo
 * @author LoveExtra
 *
 */

public class NewAddWpzfAjTask extends AsyncTask<Object, Integer, Boolean> {
	
	/**
	 * caseId 和 bh 其实是不同的,caseId是XC00176271,bh是T3506812017189249,由于之前的混用，导致移动端和服务器的json取名都存在混用，现阶段无法全部修改，可能会出现问题。注意区分！！！
	 */
	
	String msg = "";
	Context context;
	String user;
	AlertDialog alertDialog;

	private String bh;
	private String redline;
	private String redline_result;
	private double x;
	private double y;
	private String ajly;
	private String dz;
	
	private String xzqh_id;
	private String aj_id;
	
	private String oldBh;
	private String oldJcbh;		//原案件的监测编码
	private String qybm;		//区域编码
	
	private String jcbh;
	private String jcmj;
	private String zygdmj;
	private String xfsj;

	private String ajKey;
	private String isApprover;
	private String isRevoke;

	private String glbh = null;
	
	public NewAddWpzfAjTask(Context c,String user,String xzqh_id,String aj_id) {
		this.context = c;
		this.user = user;
		this.xzqh_id=xzqh_id;
		this.aj_id=aj_id;
	}

	@Override
	protected void onPreExecute() {
		CircleProgressBusyView abv = new CircleProgressBusyView(context);
		abv.setMsg("正在从服务器获取案件详情，请稍候...");
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setContentView(abv);
	}

	@SuppressWarnings("unused")
	@Override
	protected Boolean doInBackground(Object... params) {
		try {
			qybm=(String)params[0];
			oldBh = (String) params[1];
			oldJcbh=(String)params[2];
			
			redline = (String) params[3];
			redline_result = (String) params[4];
			x = (Double) params[5];
			y = (Double) params[6];
			ajly = (String) params[7];
			dz = (String) params[8];
			ajKey = (String) params[9];
			isApprover = (String) params[10];
			isRevoke = (String) params[11];
			String appUri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "");
			String adduri = appUri.endsWith("/") ? 
					new StringBuffer(appUri)
					.append(context.getString(R.string.uri_wpCaseAjNew))
					.append("?case_bh=").append(oldBh).append("&jcbh=").append(oldJcbh)
					.append("&qybm=").append(qybm).toString()
					
					: new StringBuffer(appUri).append("/")
					.append(context.getString(R.string.uri_wpCaseAjNew))
					.append("?case_bh=").append(oldBh).append("&jcbh=").append(oldJcbh)
					.append("&qybm=").append(qybm).toString();
			Log.i("guoteng", "adduri="+adduri);
					//开始请求新增服务
			HttpResponse addResponse = HttpUtils.httpClientExcuteGet(adduri);
			
			if (addResponse.getStatusLine().getStatusCode() == 200) {
				String addEntiryString = EntityUtils.toString(addResponse.getEntity());
				JSONObject addBackJson = new JSONObject(addEntiryString);
				boolean status = addBackJson.getBoolean("status");
				msg=addBackJson.getString("msg");
				if (status) {
					bh=addBackJson.getString("case_bh");	//从新增服务器获取新案件的bh	
				}
			}
			//开始请求案件详情服务
			String newAjappUri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "");
			String simpleAjuri = newAjappUri.endsWith("/") ?
					new StringBuffer(newAjappUri)
					.append(context.getString(R.string.uri_inspection_service))
					.append("?user=").append(user).append("&caseId=")
					.append(bh).toString()
					: new StringBuffer(newAjappUri).append("/")
					.append(context.getString(R.string.uri_inspection_service))
					.append("?user=").append(user).append("&caseId=")
					.append(bh).toString();
			Log.i("guoteng", "simpleAjuri="+simpleAjuri);
			HttpResponse newResponse = HttpUtils.httpClientExcuteGet(simpleAjuri);
			if (newResponse.getStatusLine().getStatusCode() == 200) {
				DbUtil.deleteINSPECTIONGETTASK2DbDatasByBh(context, bh);
				String newEntiryString = EntityUtils.toString(newResponse
						.getEntity());
				JSONObject newBackJson = new JSONObject(newEntiryString);
				boolean succeed = newBackJson.getBoolean("succeed");
				if (succeed) {
					bh=CaseUtils.getInstance().storeCaseFulldata_Wpzf(context, user,newBackJson, InspectionCaseTable.name,
						CaseAnnexesTable.name,CasePatrolTable.name,CaseInspectTable.name);
					jcbh = newBackJson.getJSONObject("case").getString("jcbh");		//modify teng.guo
					jcmj = newBackJson.getJSONObject("case").getString("jcmj")+ " ㎡";
					zygdmj = newBackJson.getJSONObject("case").getString("zygdmj")+ " ㎡";
					xfsj = newBackJson.getJSONObject("case").getString("xfsj");
					if(newBackJson!=null&&newBackJson.has("glbh")){
						glbh = newBackJson.optString("glbh");
					}

					// 2017 02 15
					try {
						JSONArray arraysss = newBackJson
								.getJSONArray("patrols");
						if (arraysss.length() > 0) {
							for (int kk = 0; kk < arraysss.length(); kk++) {
								JSONObject objjj = arraysss
										.getJSONObject(kk);
								String id = objjj.optString("id");	// 其实是巡查编号不是案件id
								String fxjg = objjj.optString("fxjg");
								DbUtil.deletePatrolsById(context, id);
								DbUtil.insertPatrols(context, id, fxjg);
							}
						}
					} catch (Exception e1) {
					}

					// 2017 03 14
					try {
						JSONObject abbb = newBackJson
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
						insObject = newBackJson.getJSONObject("inspection");
					} catch (Exception e) {
						insObject = null;
					}
					try {
						if (insObject == null) {
							ContentValues inspectValues = CaseUtils
									.getInstance()
									.caseJson2ImspectionContentValues(
											newBackJson.getJSONObject("case"));
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
						JSONObject situationObject = newBackJson
								.getJSONObject("situation");
						if (situationObject != null
								&& situationObject != JSONObject.NULL) {
							CaseUtils.getInstance().storeCaseSituation(
									context, bh, situationObject);
						}
					} catch (Exception e) {
					}
					publishProgress(-1);
					msg = bh;
					DbUtil.insertINSPECTIONGETTASK2DbDatas(context, bh,redline, x + "", y + "", ajly, dz, jcbh, jcmj,zygdmj,xfsj, newEntiryString);
					return true;
				} else {
					publishProgress(-1);
					msg = newBackJson.getString("msg");
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
				Intent intent = new Intent(context,
						ModuleRealizeActivity.class);
				if (ajKey.equals("2") && isApprover.equals("true")) {
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21028);// 处理审批
				} else if ((ajKey.equals("2") && !isApprover.equals("true"))
						|| ajKey.equals("3")) {
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21029);// 查看
				} else {
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
				}
				intent.putExtra(Parameters.CASE_ID, bh);
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
				intent.putExtra("ajid", aj_id);
				intent.putExtra("ajKey", ajKey);
				intent.putExtra("isApprover", isApprover);
				intent.putExtra("isRevoke", isRevoke);
				intent.putExtra("glbh", glbh);
				context.startActivity(intent);
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
							bh = CaseUtils.getInstance()
									.storeCaseFulldata_Wpzf(context,
											user, backJson,
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
													bh, situationObject);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						return;
					}
					Intent intent = new Intent(context,
							ModuleRealizeActivity.class);
					if (ajKey.equals("2") && isApprover.equals("true")) {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21028);// 处理审批
					} else if ((ajKey.equals("2") && !isApprover
							.equals("true")) || ajKey.equals("3")) {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21029);// 查看
					} else {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
					}
					intent.putExtra(Parameters.CASE_ID, bh);
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
					intent.putExtra("ajid", aj_id);
					intent.putExtra("ajKey", ajKey);
					intent.putExtra("isApprover", isApprover);
					intent.putExtra("isRevoke", isRevoke);
					intent.putExtra("glbh", glbh);
					context.startActivity(intent);
				} else {
					Toast toast2 = Toast.makeText(context,
							"请联网并下拉刷新数据后重试", Toast.LENGTH_SHORT);
					toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}