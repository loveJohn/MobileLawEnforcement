package com.chinadci.mel.mleo.ui.activities;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.NetworkUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.ui.activities.TwiceBack2ExitActivity;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.DropDownEditText;
import com.chinadci.mel.android.ui.views.DropDownEditText.OnSelectedHandleListener;
import com.chinadci.mel.mleo.core.ApkDownloader;
import com.chinadci.mel.mleo.core.utils.SMSReceiverHelper;
import com.chinadci.mel.mleo.ldb.AdminTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.UserTable;
import com.chinadci.mel.mleo.utils.AutoLogOffUtils;

/**
 * 
 * @ClassName LoginActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:31:53
 * 
 */
public class LoginActivity extends TwiceBack2ExitActivity implements OnClickListener {
	final int LOGINFAILD = 0x000000;
	final int LOGINSUCCEED = 0x000001;
	final int LOGINSERVICEBACKERROR = 0x000002;
	
	Context mContext;
	
	String name;
	String password;

	DropDownEditText userEditText;
	EditText passwordEditText;
	Button appUriSettingView;
	Button loginButton;
	Button mapbagButton;

	ArrayAdapter<String> accountAdapter;
	AlertDialog alertDialog;
	String smsHandle;
	
	List<KeyValue> logedUserList;
	
	String userid;
	String currentUserName;	
	//登陆验证时候，验证成功则缓存服务器updateTime
	String updateTimeOnSever;
	private long lastBackPressTime;
	int i=0;
	public void onBackPressed() {
		if (this.lastBackPressTime < System.currentTimeMillis() - 3000L) {
			Toast.makeText(this, "再按一次返回键退出", 1).show();
			this.lastBackPressTime = System.currentTimeMillis();
		} else {
			DciActivityManager.getInstance().exit();
			android.os.Process.killProcess(android.os.Process.myPid());
			super.onBackPressed();
		}
	}
	private Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this;
		setContentView(R.layout.activity_login);
		
		AutoLogOffUtils myAsycTool4Stop = AutoLogOffUtils.getInstance();
		Intent intent = getIntent();
		String userName = intent.getStringExtra("localname");
		System.out.println("userName4loginActivity------"+userName);
		myAsycTool4Stop.cancel();
		handler = new Handler() {  
			  
	        @Override  
	        public void handleMessage(Message msg) {  
	            // TODO Auto-generated method stub  
	        	Toast.makeText(getApplicationContext(),msg.obj.toString(), 0).show();  
//	            if(msg.what==1)
//	            {
//	            	Toast.makeText(getApplicationContext(), "用户读取服务开始", 0).show();  
//	            }
//	            else if(msg.what==2)
//	            {
//	            	Toast.makeText(getApplicationContext(), "用户读取服务结束", 0).show();  
//	            }
//	            else if(msg.what==3)
//	            {
//	            	Toast.makeText(getApplicationContext(), "用户读取服务错误", 0).show();  
//	            }
//	            else if(msg.what==4)
//	            {
//	            	Toast.makeText(getApplicationContext(), "用户读取服务成功", 0).show();  
//	            }
	            
	              
	        }  
	          
	    };  
		try {
			try {
				smsHandle = getIntent().getStringExtra(
						SMSReceiverHelper.SMS_HANDLE);
			} catch (Exception e) {
				// TODO: handle exception
			}

			logedUserList = DBHelper.getDbHelper(this).getLoginedUser();
			userEditText = (DropDownEditText) findViewById(R.id.activity_login_username);
			passwordEditText = (EditText) findViewById(R.id.activity_login_password);
			loginButton = (Button) findViewById(R.id.activity_login_butlog);
			appUriSettingView = (Button) findViewById(R.id.activity_login_butsetting);
			mapbagButton = (Button) findViewById(R.id.activity_login_mapbag);
			loginButton.setOnClickListener(this);
			appUriSettingView.setOnClickListener(this);
			mapbagButton.setOnClickListener(this);
			passwordEditText.setOnEditorActionListener(editorActionListener);
			userEditText.setOnEditorActionListener(editorActionListener);
//			if (users != null && users.length > 0) {
//				accountAdapter = new ArrayAdapter<String>(this,
//						R.layout.adapter_spinner_txt,
//						R.id.adapter_spinner_txtview, users);
//				userEditText.setAdapter(accountAdapter);
//			}
			
			////////////////////////////刘光辉修改///////////////////////////////
			//userName回显，无需输入账号
			if(userName!=null&&userName.length()>0){
				userEditText.setText(userName);
			}
			//////////////////////////////////////////////////////
			Log.i("ydzf", "loged user logedUserList="+logedUserList);
			if(logedUserList!=null){
				for(KeyValue kv:logedUserList){
					Log.i("ydzf", "user="+kv.toString());
				}
			}
			userEditText.setOnSelectedHandleListener(new OnSelectedHandleListener(){

				@Override
				public void onSelectedHandle(View view, Object value,int operate) {
					switch(operate){
						case DropDownEditText.HANDLE_SELECT:
							
							break;
						case DropDownEditText.HANDLE_DELETE:
							DBHelper.getDbHelper(mContext).deleteUserLoadLog(((KeyValue)value).getKey().toString());
							userEditText.notifyUpdate();
							break;
						default:
							break;
					}
				}
				
			});
			userEditText.setDate(logedUserList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	OnEditorActionListener editorActionListener = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			int vid = v.getId();
			if (vid == R.id.activity_login_username)
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					passwordEditText.requestFocus();
					return false;
				}

			if (vid == R.id.activity_login_password)
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					login();
					return false;
				}

			return true;
		}
	};

	void login() {
		if (checkLoginEntering()) {
			InputMethodManager imm = (InputMethodManager) LoginActivity.this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
			try {
				if (!NetworkUtils.checkNetwork(LoginActivity.this)) {
					Toast toast = Toast.makeText(LoginActivity.this,
							R.string.cn_nonetwork, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
//					Toast.makeText(getApplicationContext(), "开始执行登录操作！",
//							Toast.LENGTH_SHORT).show();
					new LoginTask(LoginActivity.this, userEditText.getText().toString(), passwordEditText.getText().toString()).execute();
				}
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onClick(View v) {
		try {
			int vid = v.getId();
			switch (vid) {
			case R.id.activity_login_butlog:// 登录
				login();
				break;

			case R.id.activity_login_butsetting:// 设置服务地址
				Intent intent = new Intent(LoginActivity.this,
						ServiceSettingActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			case R.id.activity_login_mapbag:
				new mapbagTask(LoginActivity.this).execute();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * @Title checkLoginEntering
	 * @Description 验证登录数据是否录入完整
	 * @return boolean
	 */
	boolean checkLoginEntering() {
		String name = userEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		if (name == null || "".equals(name)) {
			Toast toast = Toast.makeText(LoginActivity.this,
					R.string.cn_typeaccount, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}

		if (password == null || "".equals(password)) {
			Toast toast = Toast.makeText(LoginActivity.this,
					R.string.cn_typepassword, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		return true;
	}

	class mapbagTask extends AsyncTask<Void, Boolean, Boolean> {
		String msg = "";
		String bagUrl;
		Context context;
		AlertDialog alertDialog;

		public mapbagTask(Context c) {
			context = c;
		}
		
		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(mContext);
			abv.setMsg("正在从服务器获取批次列表，请稍候...");
			alertDialog = new AlertDialog.Builder(mContext).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (NetworkUtils.checkNetwork(context)) {
					String appUri = SharedPreferencesUtils.getInstance(context,
							R.string.shared_preferences).getSharedPreferences(
							R.string.sp_appuri, "");
					String mapbagUrl = appUri.endsWith("/") ? new StringBuffer(
							appUri).append(
							context.getString(R.string.uri_version)).toString()
							: new StringBuffer(appUri)
									.append("/")
									.append(context
											.getString(R.string.uri_mapbag))
									.toString();
					Log.i("ydzf", "mapbagUrl"+mapbagUrl);
					HttpResponse response = HttpUtils.httpClientExcuteGet(mapbagUrl);
					if (response.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(response
								.getEntity());
						JSONObject backJson = new JSONObject(entityString);
						if (backJson.getBoolean("succeed")) {
							bagUrl = backJson.getString("url");
							msg = bagUrl;
							return true;
						}
					}
				} else {
					msg = "没有可用的网络";
					return false;
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
			if (!result) {
				mapbagButton.setEnabled(true);
				if (!msg.equals(""))Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
			} else {
				mapbagButton.setEnabled(false);
				downloadApk(bagUrl);
				Toast.makeText(LoginActivity.this, "离线地图安装包开始下载",Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}

	void downloadApk(String url) {
		Log.i("ydzf", "downApkUri="+url);
		String title = getString(R.string.app_name_abs) + "离线地图包";
		String desc = "正在下载...";
		Long idLong = ApkDownloader.startDownload(LoginActivity.this, url,
				title, desc, getString(R.string.dir_apk));
		SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
				.writeSharedPreferences(R.string.sp_apkdownload_id,
						String.valueOf(idLong));
	}

	/**
	 * 
	 * @ClassName LoginTask
	 * @Description 向服务器发启登录验证
	 * @author leix@geo-k.cn
	 * @date 2014年6月18日 上午9:03:28
	 * 
	 */
	class LoginTask extends AsyncTask<Void, Void, Boolean> {
		Context context;
		String name;
		String password;
		String msg = "";
		String adminCode = "";

		public LoginTask(Context context, String name, String password) {
			this.context = context;
			this.name = name;
			this.password = password;
		}

		@Override
		protected void onPreExecute() {
			name = userEditText.getText().toString();
			password = passwordEditText.getText().toString();
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在验证用户信息,请稍候...");
			alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				PackageManager packageManager = getPackageManager();
				PackageInfo packInfo;
				packInfo = packageManager.getPackageInfo(getPackageName(), 0);
				String versionName = packInfo.versionName;
				int versionCode = packInfo.versionCode;
				
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_login_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_login_service))
						.toString();
				JSONObject userJson = new JSONObject();
				userJson.put("name", name);
				userJson.put("password", password);
				userJson.put("versionCode", versionCode);
				userJson.put("versionName", versionName);
				
				Message msg1 = new Message();  
				msg1.what = 1;  
				msg1.obj="调用服务开始";
                //handler.sendMessage(msg1); 
                
				HttpResponse response = HttpUtils.httpClientExcutePost(uri,
						userJson.toString());
				Message msg2 = new Message();  
				msg2.what = 2;  
				msg2.obj="调用服务结束";
                //handler.sendMessage(msg2); 
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						Log.i("ydzf", "login_backJson="+backJson.toString());
						JSONObject backUserJson = backJson
								.getJSONObject("user");
						ContentValues userValues = new ContentValues();
						updateTimeOnSever = backJson.getString("updateTime");
						System.out.println("updateTimeOnSever====================="+updateTimeOnSever);
						Message msg4 = new Message();  
						msg4.what = 4; 
						msg4.obj="调用服务成功";
		                //handler.sendMessage(msg4); 
						// rights
						try {
							JSONArray rightsArray = backUserJson
									.getJSONArray("rights");
							if (rightsArray != null && rightsArray.length() > 0) {
								userValues.put(UserTable.field_rights,
										rightsArray.toString());
							}
						} catch (Exception e) {

						}

						// role
						try {
							JSONObject roleJson = backUserJson
									.getJSONObject("role");
							userValues.put(UserTable.field_role,
									roleJson.getString("name"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						// name
						try {
							String name = backUserJson.getString("name");
							userValues.put(UserTable.field_name, name);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// chName
						try {
							String chName = backUserJson.getString("chname");
							userValues.put(UserTable.field_chName, chName);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// tel
						try {
							String tel = backUserJson.getString("tel");
							userValues.put(UserTable.field_tel, tel);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// admin
						try {
							JSONObject admin = backUserJson
									.getJSONObject("admin");
							adminCode = admin.getString("code");
							String adminName = admin.getString("name");

							userValues.put(UserTable.field_admin, adminCode);

						} catch (Exception e) {
							e.printStackTrace();
						}

						int c = DBHelper.getDbHelper(context).queryCount(
								UserTable.name,
								null,
								UserTable.field_name + "=?",
								new String[] { userValues
										.getAsString(UserTable.field_name) });
						if (c > 0) {
							DBHelper.getDbHelper(context)
									.update(UserTable.name,
											userValues,
											UserTable.field_name + "=?",
											new String[] { userValues
													.getAsString(UserTable.field_name) });

						} else {
							DBHelper.getDbHelper(context).insert(
									UserTable.name, userValues);
						}
						SharedPreferencesUtils
								.getInstance(LoginActivity.this,
										R.string.shared_preferences)
								.writeSharedPreferences(
										R.string.sp_actuser,
										userValues
												.getAsString(UserTable.field_name));
						
						///////////////////////刘光辉修改/////////////////////
						SharedPreferencesUtils
						.getInstance(LoginActivity.this,
								R.string.shared_preferences)
						.writeSharedPreferences(
								R.string.sp_updatetimeonsever,
								updateTimeOnSever);
						//新增记录登陆名到sharedpreference中
						SharedPreferencesUtils
						.getInstance(LoginActivity.this,
								R.string.shared_preferences)
						.writeSharedPreferences(
								R.string.sp_actusername,
								userValues
										.getAsString(UserTable.field_chName));
						//记录userid
						userid=userValues
								.getAsString(UserTable.field_name);
						currentUserName = userValues.getAsString(UserTable.field_chName);
						//////////////////////////////////////////////////////
						
						msg = "登录验证成功";
						return true;
					} else {
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg3 = new Message();  
				msg3.what = 3;  
				msg3.obj=e.getMessage();
                //handler.sendMessage(msg3); 
				if (e instanceof ConnectTimeoutException) {
					msg = "连接服务器超时";
					return false;
				}
			}
			msg = "登录失败";
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			if (result) {
				Intent intent = null;
				intent = new Intent(LoginActivity.this, HomeActivity.class);
				if (smsHandle != null && !smsHandle.equals("")) {
					intent.putExtra(SMSReceiverHelper.SMS_HANDLE, smsHandle);
				}
				//传递userid
				intent.putExtra("userid",userid);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				if (adminCode != null && !adminCode.equals("")) {
					new UserAdminRequestTask().execute(adminCode);
				}
			}
		}
	}

	/**
	 * 
	 * @ClassName UserAdminRequestTask
	 * @Description 获取用户的行政区数据
	 * @author leix@geo-k.cn
	 * @date 2014年6月18日 上午9:04:10
	 * 
	 */
	class UserAdminRequestTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				String admin = params[0];
				String cityAdmin = "";
				String countyAdmin = "";
				String townAdmin = "";
				if (admin != null && !admin.equals("")) {
					int l = admin.length();
					switch (l) {
					case 12:// 村
						townAdmin = admin.substring(0, 9);
						countyAdmin = admin.substring(0, 6);
						cityAdmin = admin.substring(0, 4) + "00";
						getChildrenAdmin(cityAdmin);
						getChildrenAdmin(countyAdmin);
						getChildrenAdmin(townAdmin);
						break;
					case 9:// 乡镇
						countyAdmin = admin.substring(0, 6);
						cityAdmin = admin.substring(0, 4) + "00";
						getChildrenAdmin(cityAdmin);
						getChildrenAdmin(countyAdmin);
						break;

					case 6:
						if (!admin.endsWith("00")) {// 区县
							cityAdmin = admin.substring(0, 4) + "00";
							getChildrenAdmin(cityAdmin);
						}
						break;

					default:
						break;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return true;
		}
	}

	void getChildrenAdmin(String code) {
		try {
			if (code == null || code.equals(""))
				return;

			String uri = "";
			String appUri = SharedPreferencesUtils.getInstance(this,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "");
			uri = appUri.endsWith("/") ? new StringBuffer(appUri)
					.append(getString(R.string.uri_admin_service))
					.append("?code=").append(code).toString()
					: new StringBuffer(appUri).append("/")
							.append(getString(R.string.uri_admin_service))
							.append("?code=").append(code).toString();
			Log.i("ydzf", "UserAdminRequestTask getChildrenAdmin uri="+uri);
			HttpResponse adminResponse = HttpUtils.httpClientExcuteGet(uri);
			if (adminResponse.getStatusLine().getStatusCode() == 200) {
				String entityString = EntityUtils.toString(adminResponse.getEntity());
				Log.i("ydzf", "UserAdminRequestTask getChildrenAdmin entityString="+entityString);
				JSONObject entiryJson = new JSONObject(entityString);
				boolean succeed = entiryJson.getBoolean("succeed");
				if (succeed) {

					JSONArray adminArray = entiryJson.getJSONArray("admins");
					for (int i = 0; i < adminArray.length(); i++) {

						try {
							JSONObject admin = adminArray.getJSONObject(i);
							ContentValues cv = new ContentValues();
							String curCode = "";

							// code
							try {
								curCode = admin.getString("code");
								if (curCode != null && !code.equals("")
										&& !code.equals("null"))
									cv.put("code", curCode);
							} catch (Exception e) {
								e.printStackTrace();
							}

							// name
							try {
								String name = admin.getString("name");
								if (name != null && !name.equals("")
										&& !name.equals("null"))
									cv.put("name", name);
							} catch (Exception e) {
								e.printStackTrace();
							}

							// parent code
							try {
								String parentCode = admin
										.getString("parentCode");
								if (parentCode != null
										&& !parentCode.equals("")
										&& !parentCode.equals("null"))
									cv.put("parentCode", parentCode);
							} catch (Exception e) {
								e.printStackTrace();
							}

							// min x
							try {
								String minXStr = admin.getString("minX");
								if (minXStr != null
										&& !minXStr.equals("")
										&& !minXStr.toLowerCase()
												.equals("null")) {
									double minX = Double.parseDouble(minXStr);
									cv.put("minX", minX);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							// min y
							try {
								String minYStr = admin.getString("minY");
								if (minYStr != null
										&& !minYStr.equals("")
										&& !minYStr.toLowerCase()
												.equals("null")) {
									double minY = Double.parseDouble(minYStr);
									cv.put("minY", minY);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							// max x
							try {
								String maxXStr = admin.getString("maxX");
								if (maxXStr != null
										&& !maxXStr.equals("")
										&& !maxXStr.toLowerCase()
												.equals("null")) {
									double maxX = Double.parseDouble(maxXStr);
									cv.put("maxX", maxX);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}

							// max y
							try {
								String maxYStr = admin.getString("maxY");
								if (maxYStr != null
										&& !maxYStr.equals("")
										&& !maxYStr.toLowerCase()
												.equals("null")) {
									double maxY = Double.parseDouble(maxYStr);
									cv.put("maxY", maxY);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}

							// centre
							try {
								String centre = admin.getString("centre");
								if (centre != null && !centre.equals("")
										&& !centre.equals("null")) {

									cv.put("centre", centre);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							// shape
							try {
								String shape = admin.getString("shape");
								if (shape != null && !shape.equals("")
										&& !shape.equals("null")) {
									cv.put("shape", shape);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							String delWhere = new StringBuffer(
									AdminTable.field_code).append("=?")
									.toString();
							String args[] = new String[] { curCode };
							DBHelper.getDbHelper(this).delete(AdminTable.name,
									delWhere, args);
							DBHelper.getDbHelper(this).insert(AdminTable.name,
									cv);
						} catch (Exception e) {
							continue;
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
