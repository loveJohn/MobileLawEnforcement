package com.chinadci.mel.mleo.ui.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.NetworkUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.utils.AutoLogOffUtils;

public class PasswordUpdateActivity extends CaptionActivity implements
		OnClickListener {

	String currentTime;
	TextView userView;
	EditText curPasswordView;
	EditText newPasswordView_1;
	EditText newPasswordView_2;
	Button modifyButton;

	String curPassword;
	String newPassword_1;
	String newPassword_2;
	
	String currentUserName;

	OnClickListener clickListener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			curPassword = curPasswordView.getText().toString();
			newPassword_1 = newPasswordView_1.getText().toString();
			newPassword_2 = newPasswordView_2.getText().toString();
			if (curPassword == null || "".equals(curPassword)) {
				Toast.makeText(PasswordUpdateActivity.this, "请输入当前密码", Toast.LENGTH_SHORT).show();
				curPasswordView.requestFocus();
				return;
			}

			if (newPassword_1 == null || "".equals(newPassword_1)) {
				Toast.makeText(PasswordUpdateActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
				newPasswordView_1.requestFocus();
				return;
			}

			if (newPassword_2 == null || "".equals(newPassword_2)) {
				Toast.makeText(PasswordUpdateActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
				newPasswordView_2.requestFocus();
				return;
			}

			if (!newPassword_1.equals(newPassword_2)) {
				Toast.makeText(PasswordUpdateActivity.this, "新密码输入不一致", Toast.LENGTH_SHORT).show();
				newPasswordView_2.requestFocus();
				return;
			}

			try {
				if (NetworkUtils.checkNetwork(PasswordUpdateActivity.this)) {
					new PasswordUpdateTask(PasswordUpdateActivity.this).execute(0);
				} else {
					Toast.makeText(PasswordUpdateActivity.this, "没有可用网络，请设置网络连接", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		super.onClick(arg0);
		
	}

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
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setTitle("修改用户密码");
		setContent(R.layout.activity_password_update);
		userView = (TextView) findViewById(R.id.activity_passowrdupdate_name);
		modifyButton = (Button) findViewById(R.id.activity_passowrdupdate_modify);
		curPasswordView = (EditText) findViewById(R.id.activity_passowrdupdate_curp);
		newPasswordView_1 = (EditText) findViewById(R.id.activity_passowrdupdate_newfp);
		newPasswordView_2 = (EditText) findViewById(R.id.activity_passowrdupdate_newtp);
		modifyButton.setOnClickListener(clickListener);
		//userView.setText(currentUser);
		
		currentUserName = SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
				.getSharedPreferences(R.string.sp_actusername, "");
			userView.setText(currentUserName);
	}

	class PasswordUpdateTask extends AsyncTask<Integer, Integer, Boolean> {

		AlertDialog alertDialog;
		Context context;
		String errorMsg;

		public PasswordUpdateTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在提交修改请求,请稍候...");
			alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String currentTime=getCurrentTime();
			System.out.println("currentTime+++++++++++++++++++"+currentTime);
			try {
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_update_password))
						.toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_update_password))
								.toString();

				JSONObject obj = new JSONObject();
				obj.put("user", currentUser);
				obj.put("password", curPassword);
				obj.put("newPassword", newPassword_1);
				obj.put("newUpdateTime", currentTime);
				HttpResponse httpResponse = HttpUtils.httpClientExcutePost(uri,
						obj);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(httpResponse
							.getEntity());
					JSONObject entiryJson = new JSONObject(entityString);
					boolean succeed = entiryJson.getBoolean("succeed");
					if (succeed) {
						return true;
					}
					errorMsg = entiryJson.getString("msg");
					return false;
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

			errorMsg = "修改密码发生异常，请重试";
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();
			if (result)
				showAlert();
			else
				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
//			if (result)
//				Toast.makeText(context, "用户密码修改成功", Toast.LENGTH_SHORT).show();
//			else
//				Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
		}
		
		
	}
	/**
	 * 获取当前手机系统时间
	 * */
	 @SuppressLint("SimpleDateFormat") 
	 public static String getCurrentTime() {  
       String returnStr = null;  
       SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
       Date date = new Date();  
       returnStr = f.format(date);  
       return returnStr;  
   }  
	/**
	 * 提醒用户修改密码成功重新登录
	 * @author guanghuil
	 * */
	public void showAlert(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
    	builder.setTitle("温馨提示")
    		   .setIcon(R.mipmap.ic_app)
    		   .setMessage("修改密码成功，请重新登录")   
               .setCancelable(false)   
               .setPositiveButton("确定", new DialogInterface.OnClickListener() {   
            	   public void onClick(DialogInterface dialog, int id) {   
            		   AutoLogOffUtils.getInstance(currentUserName).logOff(PasswordUpdateActivity.this);
						//本地修改密码后更新记录updateTimeOnSever到sharedpreference中
            		   SharedPreferencesUtils
						.getInstance(PasswordUpdateActivity.this,
								R.string.shared_preferences)
						.writeSharedPreferences(
								R.string.sp_updatetimeonsever,
								currentTime);
            	   }  
               });  
    	AlertDialog alert = builder.create();
    	alert.show();
    }
}
