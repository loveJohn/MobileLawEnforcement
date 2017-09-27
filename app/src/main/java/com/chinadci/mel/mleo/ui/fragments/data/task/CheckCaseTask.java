package com.chinadci.mel.mleo.ui.fragments.data.task;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;


public class CheckCaseTask extends
		AbstractBaseTask<Object, Integer, Boolean> {
	private Context context;
	private String msg;

	public CheckCaseTask(Context context,
			TaskResultHandler<Boolean> taskResultHandler) {
		super(context, taskResultHandler);
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
    protected Boolean doInBackground(Object... params) {
		Boolean result = false;
        String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
        if (!appUri.endsWith("/")) {
        	appUri = appUri + "/";
		}
        appUri  = appUri + context
				.getString(R.string.uri_casecheck);
        try {
            String param = "caseId="+params[0];
        	String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
            JSONObject resultObj = new JSONObject(resultStr);
            if (resultObj.optBoolean("succeed")) {
            	result = resultObj.optBoolean("isWpzf");
            } else {
                msg = Constants.ERRORS.NET_CONNECTIONS_ERROR;
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
	}
}