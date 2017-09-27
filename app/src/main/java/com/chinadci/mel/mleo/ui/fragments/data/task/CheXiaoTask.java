package com.chinadci.mel.mleo.ui.fragments.data.task;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;

public class CheXiaoTask extends
		AbstractBaseTask<Object, Integer, Boolean> {
	private Context context;
	private String msg;
//	private SpotsDialog dialog;

	public CheXiaoTask(Context context,
			TaskResultHandler<Boolean> taskResultHandler) {
		super(context, taskResultHandler);
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		dialog = new SpotsDialog(context, R.style.SpotsDialogDefault_1);
//        dialog.show();
	}

	@Override
    protected Boolean doInBackground(Object... params) {
        String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
        if (!appUri.endsWith("/")) {
        	appUri = appUri + "/";
		}
        appUri  = appUri + context
				.getString(R.string.uri_chexiao);
        try {
//            String param = "case_id="+params[0]+"&aj_key="+params[1];
//        	String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
        	JSONObject requestData = new JSONObject();
			requestData.put("case_id", params[0]);
			requestData.put("aj_key", params[1]);
			requestData.put("chry", params[2]);
			requestData.put("chsj", params[3]);
			requestData.put("chyy", params[4]);
			JSONObject rJsonObject = new JSONObject();
			rJsonObject.put("chxx", requestData);
			String resultStr = HttpUtil.post(appUri, rJsonObject.toString());
            JSONObject resultObj = new JSONObject(resultStr);
            if (resultObj.optBoolean("succeed")) {
            	msg = resultObj.optString("msg");
            	return true;
            } else {
            	msg = resultObj.optString("msg");
            	if(msg==null||msg.equals("")){
            		msg = Constants.ERRORS.NET_CONNECTIONS_ERROR;	
            	}
            }
        } catch (JSONException e) {
            e.printStackTrace();
            msg = Constants.ERRORS.REQUEST_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            msg = Constants.ERRORS.NETWORK_CONNECTION_TIMEOUT;
        }
		return false;
    }

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
//		dialog.dismiss();
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}