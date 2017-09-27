package com.chinadci.mel.mleo.ui.fragments.data.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class SubmitSpTask extends AbstractBaseTask<Object, Void, Boolean> {
	private Context context;
	private String msg;

	private AlertDialog alertDialog;
	
	public SubmitSpTask(Context context,
			TaskResultHandler<Boolean> taskResultHandler) {
		super(context, taskResultHandler);
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		CircleProgressBusyView abv = new CircleProgressBusyView(context);
		abv.setMsg("正在发送中...");
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setContentView(abv);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
		if (!appUri.endsWith("/")) {
			appUri = appUri + "/";
		}
		appUri = appUri + context.getString(R.string.uri_putWpCaseSb);
		try {
			JSONObject requestData = new JSONObject();
			requestData.put("key", params[0]);
			requestData.put("spry", params[1]);
			requestData.put("spsj", params[2]);
			requestData.put("spsm", params[3]);
			requestData.put("caseId", params[4]);
			requestData.put("zlacc", params[5]);		//转立案查处处理去向。1为转入转立案查处系统。0为转入已审核。
			String resultStr = HttpUtil.post(appUri, requestData.toString());
			JSONObject resultObj = new JSONObject(resultStr);
			if (resultObj.optBoolean("succeed")) {
				msg = "发送成功";
				result = true;
			}else{
				msg = resultObj.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			msg = Constants.ERRORS.REQUEST_ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			msg = Constants.ERRORS.NETWORK_CONNECTION_TIMEOUT;
		}
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
	}
}