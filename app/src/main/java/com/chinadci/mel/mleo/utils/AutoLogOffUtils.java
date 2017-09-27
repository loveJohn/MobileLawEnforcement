package com.chinadci.mel.mleo.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.activities.LoginActivity;

public class AutoLogOffUtils {
	
	String currentUser;
	static String currentUserName;
	String severmsg;
	boolean succeed;
	CheckTask checkTask;
	
	private static Lock instanceLock = new ReentrantLock();
	private volatile static AutoLogOffUtils instance;
	
	public static AutoLogOffUtils getInstance(String currentUserName) {
		AutoLogOffUtils.currentUserName = currentUserName;
		if (instance == null) {
			instanceLock.lock();
			try {
				instance = new AutoLogOffUtils();				
			} catch (Exception e) {
			} finally {
				instanceLock.unlock();
			}
		}
		return instance;
	}
	
	public static AutoLogOffUtils getInstance() {
		if (instance == null) {
			instanceLock.lock();
			try {
				instance = new AutoLogOffUtils();
			} catch (Exception e) {
			} finally {
				instanceLock.unlock();
			}
		}
		return instance;
	}
	
	public void beginToCheck(Context context,String userid,String updatetimeonsever){
		
		checkTask = new CheckTask(context, userid,updatetimeonsever);
		checkTask.execute();
	}
	
	public void cancel() {
		if(checkTask!=null){
			checkTask.cancel(true);	
			checkTask=null;
		}
	}
	 /**
     * 
     * @class CheckTask
     * 
     * 异步处理上传userid审核当前登陆用户信息是否已经修改
     * @author guanghuil
     * @data 2015.2.5
     * */
    class CheckTask extends AsyncTask<Void, Void, Boolean> {
		private String userid;
		private String updatetimeonsever;
		private Context context;

    	public CheckTask(Context context, String userid,String updatetimeonsever) {
			this.context = context;
			this.userid = userid;
			this.updatetimeonsever = updatetimeonsever;
		}
    	
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		//检查服务器账户信息是否改变
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
	    		System.out.println("updatetimeonsever---Utils---"+updatetimeonsever);
	    		String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_userupdate_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_userupdate_service))
						.toString();
				JSONObject userJson = new JSONObject();
				userJson.put("action", "check");
				userJson.put("userId", userid);  		
				userJson.put("updateTimeInClient", updatetimeonsever);
				HttpResponse response = HttpUtils.httpClientExcutePost(uri,
						userJson.toString());
				
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					succeed = backJson.getBoolean("succeed");
					severmsg = backJson.getString("msg");
					System.out.println(succeed);
					System.out.println(severmsg);
					if (succeed==true&&severmsg=="用户有更新!") {
					}else if(succeed==false&&severmsg=="用户无更新!"){
					}else if(succeed==true&&severmsg=="用户不存在！"){
						currentUser=null;
						currentUserName=null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof ConnectTimeoutException) {
					severmsg = "连接服务器超时";
					succeed=false;
				}		
			}			
			return false;
		}
		
		//true才执行
    	@Override
		protected void onPostExecute(Boolean result) {			
    		showResult(context);         
		}

    	//执行异步线程取消操作
		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
		}    	
    }
    
    public void showToast(final Context context){  
    	Toast toast=Toast.makeText(context, "连接超时，请重新登陆", Toast.LENGTH_SHORT);  
    	toast.show();  
    }

	public void showResult(final Context context){
		System.out.println("severResult"+succeed);
		if(succeed){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);  
	    	builder.setTitle("温馨提示")
	    		   .setIcon(R.mipmap.ic_app)
	    		   .setMessage(severmsg+" "+"请重新登陆！")   
	               .setCancelable(false)   
	               .setPositiveButton("确定", new DialogInterface.OnClickListener() {   
	            	   public void onClick(DialogInterface dialog, int id) {   
	            		   logOff(context);  
	            	   }  
	               });  
	    	AlertDialog alert = builder.create(); 
	    	alert.show();
		}else{
			if(severmsg==null || severmsg.equals("用户无更新!")) return;
			Toast toast=Toast.makeText(context, severmsg, Toast.LENGTH_SHORT);  
	    	toast.show();
		}
    }

	public void logOff(Context context) {
		try {
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_actuser, "");
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_actusername, "");
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_last_signin_time, "");
			Intent intent = new Intent(context, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			System.out.println("userName4AutoUtils------"+currentUserName);
			intent.putExtra("localname",currentUserName );
			((Activity) context).startActivity(intent);
			((Activity) context).overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			((Activity) context).finish();
		} catch (Exception e) {
		}
	}
}
