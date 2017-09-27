package com.chinadci.mel.mleo.ui.fragments.data.task;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.StNum;

import java.util.ArrayList;
import java.util.List;

public class GetSumNumTask extends
		AbstractBaseTask<Object, Integer, List<StNum>> {
	private Context context;
	private String msg;
//	private SpotsDialog dialog;

	public GetSumNumTask(Context context,
			TaskResultHandler<List<StNum>> taskResultHandler) {
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
    protected List<StNum> doInBackground(Object... params) {
        List<StNum> result = null;
        String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
        if (!appUri.endsWith("/")) {
        	appUri = appUri + "/";
		}
        appUri  = appUri + context
				.getString(R.string.uri_getSumNum);
        try {
            String param = "user="+params[0];
        	String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
            JSONObject resultObj = new JSONObject(resultStr);
            if (resultObj.optBoolean("succeed")) {
            	DbUtil.deleteSTNUMDbDatasByUser(context, (String) params[0]);
            	boolean isShowDetails = resultObj.optBoolean("is_xz");
            	String xzqh_id = resultObj.optString("ID");
            	JSONArray objArr = resultObj.optJSONArray("msg");
                JSONObject obj;
                result = new ArrayList<StNum>();
                for (int i = 0; i < objArr.length(); i++) {
                    obj = objArr.optJSONObject(i);
                    StNum stn = new StNum(obj.optString("AJLY_ID"),obj.optString("AJLY_CN"),obj.optInt("COUNT"),isShowDetails,xzqh_id);
                    DbUtil.insertStNumDbDatas(context, stn ,(String) params[0]);
                    result.add(stn);
                }
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
        return result;
    }

	@Override
	protected void onPostExecute(List<StNum> result) {
		super.onPostExecute(result);
//		dialog.dismiss();
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}