package com.chinadci.mel.mleo.ui.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ldb.MineralHcTable;
import com.chinadci.mel.mleo.ldb.WebMinPatrolTable;
import com.chinadci.mel.mleo.ui.adapters.MineralHcAdapter;
import com.chinadci.mel.mleo.utils.MineralCaseUtils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class MineralHcListFragment extends ContentFragment {
	ListView listView;
	View listEmptyView;
	View rootView;

	Button btnSearch;
	EditText keyView;
	SimpleDateFormat tableFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat viewFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	MineralHcAdapter adapter;

	@Override
	public void handle(Object o) {
		super.handle(o);
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_mineralhc_list,
				container, false);
		listView = (ListView) rootView
				.findViewById(R.id.fragment_mineralhc_listview);
		listEmptyView = rootView
				.findViewById(R.id.fragment_mineralhc_listview_nodata);
		keyView = (EditText) rootView
				.findViewById(R.id.fragment_Mineral_list_key);

		listView.setEmptyView(listEmptyView);
		btnSearch=(Button)rootView
				.findViewById(R.id.btnSearch);
		adapter = new MineralHcAdapter(context, null);
		adapter.setActivity(getActivity());
		adapter.setTableSource(MineralHcTable.name,MilPatrolAnnexesTable.name);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listItemClickListener);
		keyView.setOnEditorActionListener(keyEditorActionListener);
		btnSearch.setOnClickListener(btnClickSearchListener);
		initList();
		return rootView;
	}

	void initList() {
		try {
			String columns[] = new String[] { WebMinPatrolTable.field_id,
					WebMinPatrolTable.field_wfztmc,
					WebMinPatrolTable.field_szcj,
					WebMinPatrolTable.field_ffckbh,
					WebMinPatrolTable.field_hasMining,
					WebMinPatrolTable.field_haszz,
					WebMinPatrolTable.field_logTime,
					WebMinPatrolTable.field_zzwsbh,
					WebMinPatrolTable.field_ajzt};
			String selection = new StringBuffer(
					WebMinPatrolTable.field_inUser).append("=?").toString();
			String args[] = new String[] { currentUser };
			String order = new StringBuffer(WebMinPatrolTable.field_logTime)
					.append(" desc").toString();
			ArrayList<ContentValues> values = DBHelper.getDbHelper(context)
					.doQuery(WebMinPatrolTable.name, columns, selection, args,
							null, null, order, null);
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
			ContentValues cv = (ContentValues) adapter.getItem(i);
			String id = cv.getAsString(WebMinPatrolTable.field_id);
			showMinPatrolDetail(id);
		}
	};

	OnEditorActionListener keyEditorActionListener = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			if (actionId == EditorInfo.IME_ACTION_SEARCH
					|| actionId == EditorInfo.IME_MASK_ACTION
					|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//				String k = keyView.getText().toString();
//				if (k != null && !k.equals("")) {
//					new MineralGetTask(context, keyView.getText().toString(),
//							currentUser).execute();
//					InputMethodManager imm = (InputMethodManager) context
//							.getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(keyView.getWindowToken(), 0);
//				} else {
//					Toast.makeText(context, "请输入案件编号", Toast.LENGTH_SHORT)
//							.show();
//				}
				searchFun();
			}
			return true;
		}
	};

	OnClickListener btnClickSearchListener=new OnClickListener() {
		
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			searchFun();
		}
	};
	
	void searchFun()
	{
		String k = keyView.getText().toString();
		if (k != null && !k.equals("")) {
			new MineralGetTask(context, keyView.getText().toString(),
					currentUser).execute();
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(keyView.getWindowToken(), 0);
		} else {
			Toast.makeText(context, "请输入案件编号", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	void showMinPatrolDetail(String id) {
		Bundle bundle = new Bundle();
		bundle.putString(Parameters.CASE_ID, id);
		activityHandle.replaceTitle(getString(R.string.mitem_inspect));
		activityHandle.replaceToolFragment(new ToolSaveSend(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
		activityHandle.replaceContentFragment(new MineralHcEditeFragment(),
				bundle, R.anim.slide_in_right, R.anim.slide_out_left);
	}

	// 与服务交互
	class MineralGetTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String user;
		AlertDialog alertDialog;

		public MineralGetTask(Context c, String caseId, String user) {
			this.context = c;
			this.caseId = caseId.toUpperCase();
			this.user = user;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取案件详情，请稍候……");
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
						.append(context.getString(R.string.uri_mineralhc_log))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString() : new StringBuffer(appUri)
						.append("/")
						.append(context.getString(R.string.uri_mineralhc_log))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString();

				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
//						DBHelper.getDbHelper(context).delete(
//								MilPatrolTable.name,
//								MilPatrolTable.field_id + "=?",
//								new String[] { caseId });
//						DBHelper.getDbHelper(context).delete(
//								MilPatrolAnnexesTable.name,
//								MilPatrolAnnexesTable.field_tagId + "=?",
//								new String[] { caseId });
//						DBHelper.getDbHelper(context).delete(
//								MineralHcTable.name,
//								MineralHcTable.field_caseId + "=?",
//								new String[] { caseId });
//
//						JSONObject caseJson = backJson.getJSONObject("case");
//						String caseId = caseJson.getString("id");
//						saveMineralInfo(caseId, caseJson);
						
						caseId=MineralCaseUtils.getInstance().storeMineralFulldata(context, currentUser, backJson.getJSONObject("particular"), 
								WebMinPatrolTable.name, MilPatrolAnnexesTable.name, MineralHcTable.name);
//						try {
//							JSONArray patrolsjson = backJson
//									.getJSONArray("patrols");
//							if (patrolsjson != null && patrolsjson.length() > 0) {
//								saveMineralHcInfo(caseId, patrolsjson);
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
						msg = caseId;
						return true;
					} else {
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			msg = "获取案件详情发生异常";
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (result) {
				Bundle bundle = new Bundle();
				bundle.putString(Parameters.CASE_ID, msg);
				activityHandle.replaceTitle(getString(R.string.mitem_inspect));
				activityHandle.replaceToolFragment(new ToolSaveSend(), null,
						R.anim.slide_in_top, R.anim.slide_out_bottom);
				activityHandle.replaceContentFragment(
						new MineralHcEditeFragment(), bundle,
						R.anim.slide_in_right, R.anim.slide_out_left);
			} else {
				Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}

	void saveMineralInfo(String caseId, JSONObject obj) {
		try {
			ContentValues values = new ContentValues();
			values.put(MilPatrolTable.field_id, caseId);
			values.put(MilPatrolTable.field_user, currentUser);
			// 巡查时间
			try {
				String xcsj = obj.getString("xcsj");
				if (xcsj != null && !xcsj.equals("null") && !xcsj.equals(""))
					values.put(MilPatrolTable.field_logTime, xcsj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 巡查线路
			try {
				String xcxl = obj.getString("xcxl");
				if (xcxl != null && !xcxl.equals("null") && !xcxl.equals(""))
					values.put(MilPatrolTable.field_line, xcxl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 是否发现非法采矿点
			try {
				Boolean sffxffckd = obj.getBoolean("sffxffckd");
				if (sffxffckd != null && !sffxffckd.equals("null")
						&& !sffxffckd.equals(""))
					values.put(MilPatrolTable.field_hasMining, sffxffckd ? 1
							: 0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 是否发现非法采矿点
			try {
				String fxwt = obj.getString("fxwt");
				if (fxwt != null && !fxwt.equals("null") && !fxwt.equals(""))
					values.put(MilPatrolTable.field_exception, fxwt);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 备注
			try {
				String bz = obj.getString("bz");
				if (bz != null && !bz.equals("null") && !bz.equals(""))
					values.put(MilPatrolTable.field_notes, bz);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String location = obj.getString("location").toString();
				// 判定xy是否为空
				if (location != null && !location.equals("")
						&& !location.equals("null"))
					values.put(MilPatrolTable.field_location, location);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// redline
			try {
				JSONObject redlineObject = obj.getJSONObject("redline");
				if (redlineObject != null && redlineObject != JSONObject.NULL)
					values.put(MilPatrolTable.field_redline,
							redlineObject.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 非法采矿点编号
			try {
				String ffckdbh = obj.getString("ffckdbh");
				if (ffckdbh != null && !ffckdbh.equals("null")
						&& !ffckdbh.equals(""))
					values.put(MilPatrolTable.field_ffckbh, ffckdbh);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 是否制止非法行为
			try {

				Boolean sfzzffxw = obj.getBoolean("sfzzffxw");
				if (sfzzffxw != null && !sfzzffxw.equals("null")
						&& !sfzzffxw.equals(""))
					values.put(MilPatrolTable.field_haszz, sfzzffxw ? 1 : 0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 制止文书编号
			try {
				String zzwhbh = obj.getString("zzwhbh");
				if (zzwhbh != null && !zzwhbh.equals("null")
						&& !zzwhbh.equals(""))
					values.put(MilPatrolTable.field_zzwsbh, zzwhbh);
			} catch (Exception e) {
				e.printStackTrace();
			}

			DBHelper.getDbHelper(context).insert(MilPatrolTable.name, values);

			try {
				JSONArray annexesJson = obj.getJSONArray("annexes");
				if (annexesJson != null && annexesJson.length() > 0) {
					saveAnnexes(caseId, caseId, InspectionCaseTable.name,
							annexesJson);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			// // 填写时间
			// try {
			// String txsj = obj.getString("txsj");
			// if (txsj != null && !txsj.equals("null")
			// && !txsj.equals(""))
			// values.put(MilPatrolTable.field_logTime, txsj);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// // 填写人员
			// try {
			// String txry = obj.getString("txry");
			// if (txry != null && !txry.equals("null")
			// && !txry.equals(""))
			// values.put(MilPatrolTable.field_user, txry);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// 巡查 人员
			// try {
			// String xcry = obj.getString("xcry");
			// if (xcry != null && !xcry.equals("null")
			// && !xcry.equals(""))
			// values.put(MilPatrolTable.field_logTime, xcsj);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void saveMineralHcInfo(String caseId, JSONArray array) {
		try {
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject obj = array.getJSONObject(i);
					if (obj != null && obj != JSONObject.NULL) {
						ContentValues values = new ContentValues();
						values.put(MineralHcTable.field_caseId, caseId);
						String id = null;
						values.put(MineralHcTable.field_status, "2");
						// 编号
						try {
							id = obj.getString("id");
							values.put(MineralHcTable.field_id, id);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 人员名称
						try {
							String hcrmc = obj.getString("hcrmc");
							values.put(MineralHcTable.field_hcrmc, hcrmc);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 是否非法采矿点，0：否，1：是
						try {
							String sfffckd = obj.getString("sfffckd");
							values.put(MineralHcTable.field_sfffckd,
									sfffckd.equals("0") ? "否" : "是");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 违法主体性质，1：个人，2：企业
						try {
							String wfztxz = obj.getString("wfztxz");
							values.put(MineralHcTable.field_wfztxz,
									wfztxz.equals("1") ? "个人" : "企业");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 违法主体名称
						try {
							String wfztmc = obj.getString("wfztmc");
							values.put(MineralHcTable.field_wfztmc, wfztmc);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 非法开采方式
						try {
							String ffkcfs = obj.getJSONObject("ffkcfs")
									.getString("value");// obj.getString("ffkcfs");

							values.put(MineralHcTable.field_ffkcfs, ffkcfs);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 是否停止非法开采，0：否，1：是
						try {
							String sftzffkc = obj.getString("sftzffkc");
							values.put(MineralHcTable.field_sftzffkc,
									sftzffkc.equals("0") ? "否" : "是");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 备注
						try {
							String hccomment = obj.getString("hccomment");
							values.put(MineralHcTable.field_hccomment,
									hccomment);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 核查时间，形如2014-05-01
						try {
							String hcsj = obj.getString("hcsj");
							values.put(MineralHcTable.field_hcsj, hcsj);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 非法开采矿种
						try {
							String fkckz = obj.getJSONObject("fkckz")
									.getString("value");// obj.getString("fkckz");
							values.put(MineralHcTable.field_fkckz, fkckz);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 附件
						try {
							JSONArray annexes = obj.getJSONArray("annexes");
							if (annexes != null && annexes.length() > 0)
								saveAnnexes(caseId, id, MineralHcTable.name,
										annexes);
						} catch (Exception e) {
							e.printStackTrace();
						}
						DBHelper.getDbHelper(context).insert(
								MineralHcTable.name, values);
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	void saveAnnexes(String caseId, String tagId, String talbe,
			JSONArray annexesJson) {
		for (int i = 0; i < annexesJson.length(); i++) {
			try {
				JSONObject obj = annexesJson.getJSONObject(i);
				String ext = obj.getString("extension");
				String name = obj.getString("name");
				String uri = obj.getString("uri");
				String relativepPath = "";
				relativepPath = new StringBuffer(getString(R.string.dir_annex))
						.append("/").append(UUID.randomUUID().toString())
						.append(ext).toString();
				String filePath = new StringBuffer(Environment
						.getExternalStorageDirectory().getAbsolutePath())
						.append("/").append(relativepPath).toString();
				File targetFile = new File(filePath);
				OutputStream outStream = new FileOutputStream(targetFile);

				URL url = new URL(uri);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setConnectTimeout(10000);
				InputStream inputStream = urlConn.getInputStream();

				byte[] buffer = new byte[1024];
				int readLen = 0;
				while ((readLen = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, readLen);
				}

				outStream.flush();
				inputStream.close();
				ContentValues values = new ContentValues();
				values.put(CaseAnnexesTable.field_caseId, caseId);
				values.put(CaseAnnexesTable.field_name, name);
				values.put(CaseAnnexesTable.field_path, filePath);
				values.put(CaseAnnexesTable.field_tagId, tagId);
				values.put(CaseAnnexesTable.field_tag, talbe);
				values.put(CaseAnnexesTable.field_uri, uri);
				DBHelper.getDbHelper(context).insert(CaseAnnexesTable.name,
						values);
			} catch (Exception e) {
				continue;
			}
		}
	}
}
