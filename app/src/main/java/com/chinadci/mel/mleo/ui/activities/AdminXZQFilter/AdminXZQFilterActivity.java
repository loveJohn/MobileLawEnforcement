package com.chinadci.mel.mleo.ui.activities.AdminXZQFilter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.NetworkUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.ldb.AdminTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ui.activities.CaptionActivity;

public class AdminXZQFilterActivity extends CaptionActivity {
	public static final String XZQ_ADMIN_CODE = "da";

	Button buttonView;
	ListView listView;
	AdminAdapter adapter;
	
	String userAdminCode;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	protected void onBackButtonClick(View v) {
		// TODO Auto-generated method stub
		super.onBackButtonClick(v);// 退出
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onBackButtonClick(null);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		userAdminCode= getIntent().getStringExtra(XZQ_ADMIN_CODE);
		setTitle("行政区选择");
		adapter = new AdminAdapter(this, null);
		setContent(R.layout.activity_admin_chooser);
		buttonView = (Button) findViewById(R.id.activity_admin_chooser_button);
		listView = (ListView) findViewById(R.id.activity_admin_chooser_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listItemClickListener);
		listView.setOnItemLongClickListener(itemLongClickListener);
		buttonView.setOnClickListener(buttonClickListener);
		initAdmins(userAdminCode);
	}
	
	//是否有返回上一级的权限
	boolean hasPreUpPermission(String parenteCode){
		if(userAdminCode.endsWith("0000")){
			return true;
		}
		if(parenteCode.length()<userAdminCode.length()){	//不能越到上一级	
			return false;
		}else if(parenteCode.length()==userAdminCode.length()){
			if(parenteCode.equals(userAdminCode)){		//返回自身一级
				return true;
			}else{			//市级和省级长度都是6
				return false;
			}
		}else{		//下级返上
			return true;
		}
	}
	

	void initAdmins(String adminCode) {
		Log.i("guoteng", "adminCode="+adminCode);
		Toast.makeText(AdminXZQFilterActivity.this,"长按选择行政区，单击进入下一级",Toast.LENGTH_LONG).show();
		try {
			if (adminCode == null || adminCode.equals(""))
				adminCode = getString(R.string.cn_defadmincode);
			String padminCode = adminCode;

			ContentValues admin = DBHelper.getDbHelper(
					AdminXZQFilterActivity.this).doQuery(AdminTable.name,
							new String[] { AdminTable.field_code,AdminTable.field_name,AdminTable.field_parentCode },
							AdminTable.field_code + "=?",
							new String[] { padminCode });

			if (admin != null) {
				buttonView.setText(admin.getAsString(AdminTable.field_name));
				buttonView.setTag(admin);
				ArrayList<ContentValues> adminValues = DBHelper.getDbHelper(
						AdminXZQFilterActivity.this).doQuery(
						AdminTable.name,
						new String[] { AdminTable.field_code,
								AdminTable.field_name,
								AdminTable.field_parentCode },
						AdminTable.field_parentCode + "=?",
						new String[] { padminCode }, null, null,
						AdminTable.field_code + " asc", null);
				adapter.setDataset(adminValues);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnClickListener buttonClickListener = new OnClickListener() {
		public void onClick(View v) {
			try {
				ContentValues curAdmin = (ContentValues) buttonView.getTag();
				String pcode = curAdmin.getAsString(AdminTable.field_parentCode);
				if(!hasPreUpPermission(pcode)){
					Toast.makeText(AdminXZQFilterActivity.this,"您无权继续上一级查询",Toast.LENGTH_LONG).show();
					return;
				}
				if (pcode != null && !pcode.equals("")) {
					if (NetworkUtils.checkNetwork(AdminXZQFilterActivity.this))// 网络可用
					{
						new AdminRequestTask(AdminXZQFilterActivity.this)
								.execute(new String[] { pcode,
										AdminRequestTask.DIR_UP_LEVEL });
					} else {
						upAdminLevel(pcode);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	OnItemClickListener listItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			try {
				ContentValues values = (ContentValues) v.getTag();
				String pcode = values.getAsString(AdminTable.field_code);
				//add teng.guo
				if(9==pcode.length()){
					Toast.makeText(AdminXZQFilterActivity.this,"只能选到这一级",Toast.LENGTH_LONG).show();
					Bundle bundle = new Bundle();
					bundle.putString(XZQ_ADMIN_CODE, pcode);
					Log.i("guoteng", "pcode="+pcode);
					AdminXZQFilterActivity.this.setResult(RESULT_OK, AdminXZQFilterActivity.this.getIntent().putExtras(bundle));
					AdminXZQFilterActivity.this.finish();
					return;
				}
				
				if (pcode != null && !pcode.equals("")) {
					if (NetworkUtils.checkNetwork(AdminXZQFilterActivity.this)) {
						new AdminRequestTask(AdminXZQFilterActivity.this)
								.execute(new String[] { pcode,
										AdminRequestTask.DIR_DOWN_LEVEL });
					} else {
						downAdminLevel(pcode);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	
	
	AdapterView.OnItemLongClickListener itemLongClickListener=new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			try{
				ContentValues values = (ContentValues) view.getTag();
				String pcode = values.getAsString(AdminTable.field_code);
				Bundle bundle = new Bundle();
				bundle.putString(XZQ_ADMIN_CODE, pcode);
				Log.i("guoteng", "pcode="+pcode);
				AdminXZQFilterActivity.this
						.setResult(RESULT_OK, AdminXZQFilterActivity.this
								.getIntent().putExtras(bundle));
				AdminXZQFilterActivity.this.finish();
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
			return true;
		}
	};

	boolean upAdminLevel(String pcode) {
		try {
			ContentValues pAdmin = DBHelper.getDbHelper(
					AdminXZQFilterActivity.this)
					.doQuery(
							AdminTable.name,
							new String[] { AdminTable.field_code,
									AdminTable.field_name,
									AdminTable.field_parentCode },
							AdminTable.field_code + "=?",
							new String[] { pcode });
			if (pAdmin != null) {
				buttonView.setTag(pAdmin);
				buttonView.setText(pAdmin.getAsString(AdminTable.field_name));
				ArrayList<ContentValues> adminValues = DBHelper.getDbHelper(
						AdminXZQFilterActivity.this).doQuery(
						AdminTable.name,
						new String[] { AdminTable.field_code,
								AdminTable.field_name,
								AdminTable.field_parentCode },
						AdminTable.field_parentCode + "=?",
						new String[] { pcode }, null, null,
						AdminTable.field_code + " asc", null);
				adapter.setDataset(adminValues);
				adapter.notifyDataSetChanged();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	void downAdminLevel(String pcode) {
		try {
			ContentValues values = DBHelper.getDbHelper(
					AdminXZQFilterActivity.this)
					.doQuery(
							AdminTable.name,
							new String[] { AdminTable.field_code,
									AdminTable.field_name,
									AdminTable.field_parentCode },
							AdminTable.field_code + "=?",
							new String[] { pcode });

			ArrayList<ContentValues> adminValues = DBHelper.getDbHelper(
					AdminXZQFilterActivity.this)
					.doQuery(
							AdminTable.name,
							new String[] { AdminTable.field_code,
									AdminTable.field_name,
									AdminTable.field_parentCode },
							AdminTable.field_parentCode + "=?",
							new String[] { pcode }, null, null,
							AdminTable.field_code + " asc", null);

			if (adminValues != null && adminValues.size() > 0) {
				buttonView.setText(values.getAsString(AdminTable.field_name));
				buttonView.setTag(values);
				adapter.setDataset(adminValues);
				adapter.notifyDataSetChanged();
			} else {
				Bundle bundle = new Bundle();
				bundle.putString(XZQ_ADMIN_CODE, pcode);
				Log.i("guoteng", "pcode="+pcode);
				AdminXZQFilterActivity.this
						.setResult(RESULT_OK, AdminXZQFilterActivity.this
								.getIntent().putExtras(bundle));
				AdminXZQFilterActivity.this.finish();
			}
		} catch (Exception e) {

		}
	}

	class AdminRequestTask extends AsyncTask<String, Void, Void> {
		public static final String DIR_UP_LEVEL = "U";
		public static final String DIR_DOWN_LEVEL = "D";

		AlertDialog alertDialog;
		Context context;
		String dirc = "";
		String pcode = "";

		public AdminRequestTask(Context c) {
			context = c;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在获取数据,请稍候...");
			alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				pcode = params[0];
				dirc = params[1];

				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_admin_service))
						.append("?code=").append(pcode).toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_admin_service))
								.append("?code=").append(pcode).toString();
						
				Log.i("guoteng", "xzqUri="+uri);
				HttpResponse adminResponse = HttpUtils.httpClientExcuteGet(uri);
				if (adminResponse.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(adminResponse
							.getEntity());
					JSONObject entiryJson = new JSONObject(entityString);
					boolean succeed = entiryJson.getBoolean("succeed");
					if (succeed) {
						JSONArray adminArray = entiryJson
								.getJSONArray("admins");
						for (int i = 0; i < adminArray.length(); i++) {
							try {
								JSONObject admin = adminArray.getJSONObject(i);
								ContentValues cv = new ContentValues();
								String code = "";

								try {
									code = admin.getString("code");
									if (code != null && !code.equals("")
											&& !code.equals("null"))
										cv.put("code", code);
								} catch (Exception e) {
									// TODO: handle exception
									continue;
								}

								try {
									String name = admin.getString("name");
									if (name != null && !name.equals("")
											&& !name.equals("null"))
										cv.put("name", name);
								} catch (Exception e) {
									// TODO: handle exception
									continue;
								}

								try {

									String parentCode = admin
											.getString("parentCode");
									if (parentCode != null
											&& !parentCode.equals("")
											&& !parentCode.equals("null"))
										cv.put("parentCode", parentCode);
								} catch (Exception e) {
									// TODO: handle exception
									continue;
								}

								try {
									String minXStr = admin.getString("minX");
									if (minXStr != null
											&& !minXStr.equals("")
											&& !minXStr.toLowerCase().equals(
													"null")) {
										double minX = Double
												.parseDouble(minXStr);
										cv.put("minX", minX);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

								try {
									String minYStr = admin.getString("minY");
									if (minYStr != null
											&& !minYStr.equals("")
											&& !minYStr.toLowerCase().equals(
													"null")) {
										double minY = Double
												.parseDouble(minYStr);
										cv.put("minY", minY);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

								try {
									String maxXStr = admin.getString("maxX");
									if (maxXStr != null
											&& !maxXStr.equals("")
											&& !maxXStr.toLowerCase().equals(
													"null")) {
										double maxX = Double
												.parseDouble(maxXStr);
										cv.put("maxX", maxX);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

								try {
									String maxYStr = admin.getString("maxY");
									if (maxYStr != null
											&& !maxYStr.equals("")
											&& !maxYStr.toLowerCase().equals(
													"null")) {
										double maxY = Double
												.parseDouble(maxYStr);
										cv.put("maxY", maxY);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

								try {
									String centre = admin.getString("centre");
									if (centre != null && !centre.equals("")
											&& !centre.equals("null")) {

										cv.put("centre", centre);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

								try {
									String shape = admin.getString("shape");
									if (shape != null && !shape.equals("")
											&& !shape.equals("null")) {
										cv.put("shape", shape);
									}
								} catch (Exception e) {
									// TODO: handle exception
								}

								String delWhere = new StringBuffer(
										AdminTable.field_code).append("=?")
										.toString();
								String args[] = new String[] { code };
								DBHelper.getDbHelper(context).delete(
										AdminTable.name, delWhere, args);
								DBHelper.getDbHelper(context).insert(
										AdminTable.name, cv);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {

				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();

				if (dirc.equals(DIR_UP_LEVEL)) {
					ContentValues pAdmin = DBHelper.getDbHelper(context)
							.doQuery(
									AdminTable.name,
									new String[] { AdminTable.field_code,
											AdminTable.field_name,
											AdminTable.field_parentCode },
									AdminTable.field_code + "=?",
									new String[] { pcode });

					if (pAdmin != null) {
						buttonView.setTag(pAdmin);
						buttonView.setText(pAdmin
								.getAsString(AdminTable.field_name));
						ArrayList<ContentValues> adminValues = DBHelper
								.getDbHelper(context).doQuery(
										AdminTable.name,
										new String[] { AdminTable.field_code,
												AdminTable.field_name,
												AdminTable.field_parentCode },
										AdminTable.field_parentCode + "=?",
										new String[] { pcode }, null, null,
										AdminTable.field_code + " asc", null);

						adapter.setDataset(adminValues);
						adapter.notifyDataSetChanged();
					}
				} else if (dirc.equals(DIR_DOWN_LEVEL)) {
					ContentValues values = DBHelper.getDbHelper(context)
							.doQuery(
									AdminTable.name,
									new String[] { AdminTable.field_code,
											AdminTable.field_name,
											AdminTable.field_parentCode },
									AdminTable.field_code + "=?",
									new String[] { pcode });

					ArrayList<ContentValues> adminValues = DBHelper
							.getDbHelper(context).doQuery(
									AdminTable.name,
									new String[] { AdminTable.field_code,
											AdminTable.field_name,
											AdminTable.field_parentCode },
									AdminTable.field_parentCode + "=?",
									new String[] { pcode }, null, null,
									AdminTable.field_code + " asc", null);

					if (adminValues != null && adminValues.size() > 0) {
						buttonView.setText(values
								.getAsString(AdminTable.field_name));
						buttonView.setTag(values);
						adapter.setDataset(adminValues);
						adapter.notifyDataSetChanged();
					} else {
						Bundle bundle = new Bundle();
						bundle.putString(XZQ_ADMIN_CODE, pcode);
						AdminXZQFilterActivity.this.setResult(RESULT_OK,
								AdminXZQFilterActivity.this.getIntent()
										.putExtras(bundle));
						AdminXZQFilterActivity.this.finish();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	class AdminAdapter extends BaseAdapter {
		ArrayList<ContentValues> dataset;
		Context context;

		public AdminAdapter(Context c, ArrayList<ContentValues> d) {
			context = c;
			dataset = d;
		}

		public void setDataset(ArrayList<ContentValues> values) {
			dataset = values;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			if (dataset != null && dataset.size() > 0)
				return dataset.size();
			return 0;
		}

		public Object getItem(int position) {
			if (dataset != null && dataset.size() > 0)
				return dataset.get(position);
			return null;
		}

		public long getItemId(int position) {
			if (dataset != null && dataset.size() > 0)
				return position;
			return -1;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (dataset != null && dataset.size() > position) {
				ContentValues data = dataset.get(position);
				AdminAdapterTextView view = new AdminAdapterTextView(context);
				view.setText(data.getAsString(AdminTable.field_name));
				view.setTag(data);
				convertView = view;
			}
			return convertView;
		}

	class AdminAdapterTextView extends TextView {

			public AdminAdapterTextView(Context context, AttributeSet attrs,
					int defStyle) {
				super(context, attrs, defStyle);
				init(context);
				// TODO Auto-generated constructor stub
			}

			public AdminAdapterTextView(Context context, AttributeSet attrs) {
				super(context, attrs);
				init(context);
				// TODO Auto-generated constructor stub
			}

			public AdminAdapterTextView(Context context) {
				super(context);
				init(context);
				// TODO Auto-generated constructor stub
			}

			void init(Context context) {
				int padding = (int) (8 * context.getResources()
						.getDisplayMetrics().density);
				setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				setTextColor(Color.BLACK);
				setPadding(padding, padding, padding, padding);
			}
		}
	}
}
