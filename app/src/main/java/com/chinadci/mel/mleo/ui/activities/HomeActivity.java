package com.chinadci.mel.mleo.ui.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.FuncationGroup;
import com.chinadci.mel.mleo.core.Global;
import com.chinadci.mel.mleo.core.ManifestHandler;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.core.utils.SMSReceiverHelper;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.UserFields;
import com.chinadci.mel.mleo.ldb.UserTable;
import com.chinadci.mel.mleo.ui.adapters.FuncationAdapter;
import com.chinadci.mel.mleo.ui.views.FuncationGroupView;
import com.chinadci.mel.mleo.ui.views.UserView;
import com.chinadci.mel.mleo.utils.AutoLogOffUtils;
import com.chinadci.mel.mleo.utils.TimeUtil;

/**
 * 
 * @ClassName HomeActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:38:47
 * 
 */
public class HomeActivity extends FuncationGroupActivity implements OnClickListener {
	//UserView userView;
	String currentUser;
	String updateTimeOnSever;
	/////////////////////////刘辉加   2015-3-1//////////////////////////////
	String userid;                  //账号Id					
	int recLen = 0;			//判断参数
	String currentUserName;
	AutoLogOffUtils myAsycTool4Check;
	Timer timer;
	///////////////////////////////////////////////////////////////////
	
	
	private long lastBackPressTime;

	public void onBackPressed() {
		if (this.lastBackPressTime < System.currentTimeMillis() - 3000L) {
			Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
			this.lastBackPressTime = System.currentTimeMillis();
		} else {
			super.onBackPressed();
		}
	}

	public void onClick(View v) {
		Intent intent = new Intent(this, ModuleRealizeActivity.class);
		intent.putExtra(ModuleActivity.TAG_MODULE_ID, 2401);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Log.i("ydzf","HomeActivity onCreate");
		if (Global.funcationMap==null||Global.moduleMap==null) {
			ManifestHandler manifestHandler;
			try {
				manifestHandler = new ManifestHandler(HomeActivity.this, getAssets().open("modules.xml"));
				Global.funcationMap = manifestHandler.getFunctionMap();
				Global.moduleMap = manifestHandler.getModuleMap();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.i("ydzf","HomeActivity onCreate ManifestHandler");
		currentUser = SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
				.getSharedPreferences(R.string.sp_actuser, "");
		funcationGroupLayout = (LinearLayout) findViewById(R.id.activity_home_funcationlayout);
		/*userView = (UserView) findViewById(R.id.activity_home_user);
		userView.setOnClickListener(this);*/
		initUserFuncationGroup();
		Log.i("ydzf","HomeActivity onCreate initUserFuncationGroup");
		handleSMS();
		try {
			Intent i = new Intent("com.chinadci.mel.mleo.services.GPSService");
			startService(i);
		} catch (Exception e) {
		}
		/////////////////////////刘辉加   2015-3-1//////////////////////////////
		currentUserName = SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
				.getSharedPreferences(R.string.sp_actusername, "");
		System.out.println("userName4HomeActivity------"+currentUserName);
		updateTimeOnSever = SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
				.getSharedPreferences(R.string.sp_updatetimeonsever, "");
		myAsycTool4Check = AutoLogOffUtils.getInstance(currentUserName);//实例化查询工具类	
		///////////////////////////////////////////////////////////////////
		timer = new Timer(true);
		timer.schedule(task,1000, 3600*1000); //延时1s后执行，一小时执行一次
	}

	
	@Override
	protected void onStart(){
		super.onStart();
		initUserView();
		if(timer==null){
			timer = new Timer(true);
			timer.schedule(task,1000, 3600*1000); //延时1s后执行，一小时执行一次
		}
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		stopService(new Intent("com.chinadci.mel.mleo.services.GPSService"));
		
		
		/////////////////////刘光辉新加/////////////////////
		if (timer != null) {
			timer.cancel();
			timer = null;
		   }	
		super.onDestroy();
		//add teng.guo 20170721
		SharedPreferencesUtils.getInstance(this,R.string.shared_preferences)
		.writeSharedPreferences(R.string.sp_last_login_time,TimeUtil.currentTimeMillisString());		//记录退出时间
		////////////////////////////////////////////////
	}
	protected void initUserFuncationGroup() {
		try {
			if (funcationGroupLayout == null)
				return;
			funcationGroupLayout.removeAllViews();
			LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			String columns[] = new String[] { UserTable.field_name, UserTable.field_rights };
			String selection = new StringBuffer(UserTable.field_name).append("=?").toString();
			String args[] = new String[] { currentUser };
			ContentValues userValues = DBHelper.getDbHelper(this).doQuery(UserTable.name, columns,
					selection, args);

			String rightsJson = userValues.getAsString(UserTable.field_rights);
			// rightsJson = String
			// .format("[{%1$sgname%1$s:%1$s土地执法%1$s,%1$smids%1$s:[101,102,103,104,105,106]},{%1$sgname%1$s:%1$s矿产执法%1$s,%1$smids%1$s:[201,202,203,204]},{%1$sgname%1$s:%1$s其它功能%1$s,%1$smids%1$s:[302,303,301,304,305]}]",
			// '"');
			if (rightsJson != null) {
				JSONArray rightsJsonArray = new JSONArray(rightsJson);

				if (rightsJsonArray != null && rightsJsonArray.length() > 0) {
					userFuncationGroup = new ArrayList<FuncationGroup>();
					for (int i = 0; i < rightsJsonArray.length(); i++) {
						JSONObject json = rightsJsonArray.getJSONObject(i);
						String title = json.getString("gname");
						int ids[] = null;
						JSONArray idsArray = json.getJSONArray("mids");
						if (idsArray != null && idsArray.length() > 0) {
							ids = new int[idsArray.length()];
							for (int j = 0; j < idsArray.length(); j++) {
								int id = idsArray.getInt(j);
								ids[j] = id;
							}
						}
						FuncationGroupView funcationGroupView = new FuncationGroupView(this,
								R.layout.view_funcation_group, R.id.view_funcationgroup_title,
								R.id.view_funcationgroup_grid);
						funcationGroupView.setTitle(title);
						funcationGroupView.setOnItemClickListener(onFuncationClickListener);
						funcationGroupView.setAdapter(new FuncationAdapter(this, ids,
								Global.funcationMap, R.layout.adapter_funcation,
								R.id.adapter_funcation_icon, R.id.adapter_funcation_label));
						if (i > 0)
							params.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
						funcationGroupLayout.addView(funcationGroupView, params);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void handleSMS() {
		try {
			String smsHandle = getIntent().getStringExtra(SMSReceiverHelper.SMS_HANDLE);
			userid = getIntent().getStringExtra("userid");//获得当前登陆账号id
			String sh[] = smsHandle.split(",");
			int mindex = Integer.parseInt(sh[1]);
			Intent intent = new Intent(this, ModuleRealizeActivity.class);
			intent.putExtra(ModuleActivity.TAG_MODULE_ID, mindex);
			intent.putExtra(SMSReceiverHelper.SMS_HANDLE, smsHandle);
			
			////////////////////////////////刘光辉新加///////////////////////////
			
			intent.putExtra("userid", userid);//向后面moduleRealActivity传递当前登陆账号ID
			//////////////////////////////////////////////////////////////////
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initUserView() {
		try {
			if (currentUser != null && !currentUser.equals("")) {
				ContentValues userValues = DBHelper.getDbHelper(this).doQuery(
						UserTable.name,
						new String[] { UserTable.field_photo, UserTable.field_chName,
								UserTable.field_name, UserTable.field_role },
						new StringBuffer(UserTable.field_name).append("=?").toString(),
						new String[] { currentUser });
				if (userValues != null) {
					String name = userValues.getAsString(UserTable.field_name);
					String chName = userValues.getAsString(UserTable.field_chName);
					//userView.setUserName((chName != null && !chName.equals("")) ? chName : name);
					try {
						String role = userValues.getAsString(UserTable.field_role);
						//if (role != null) userView.setRoleName(role);
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						byte photoByte[] = userValues.getAsByteArray(UserFields.photo);
						Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0,photoByte.length);
						//userView.setPhoto(bitmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String signTime = SharedPreferencesUtils.getInstance(this,
							R.string.shared_preferences).getSharedPreferences(
							R.string.sp_last_signin_time, "");

					/*if (signTime != null && !signTime.equals(""))
						userView.setSignTime(String.format(getString(R.string.format_last_signin), TimeFormatFactory2.getDisplayTimeM(signTime)));*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//handler+TimerTask实现后台账号信息监测和超时自动注销功能
	@SuppressLint("HandlerLeak") 
	final Handler handler = new Handler(){  
		public void handleMessage(Message msg) {  			
			switch (msg.what) {      
            	case 1: 
            		recLen+=3600;
                	myAsycTool4Check.beginToCheck(HomeActivity.this, userid,updateTimeOnSever); 
                	if(recLen>=3600*24){
                    	myAsycTool4Check.showToast(HomeActivity.this);
                    	myAsycTool4Check.cancel();
                    	myAsycTool4Check.logOff(HomeActivity.this);
                    	timer.cancel();
                    	recLen=0;
                	}
            		break; 
			}      
			super.handleMessage(msg);  
		}    
    };
    
    TimerTask task = new TimerTask(){  
    	public void run() {  
    		Message message = new Message();      
    		message.what = 1;      
    		handler.sendMessage(message);    
    	}  
   };
}
