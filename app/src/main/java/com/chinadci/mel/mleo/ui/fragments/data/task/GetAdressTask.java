package com.chinadci.mel.mleo.ui.fragments.data.task;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.Poi;

import java.util.ArrayList;
import java.util.List;

public class GetAdressTask extends
		AbstractBaseTask<Object, Integer, List<Poi>> {
	private Context context;
	private String msg;
//	private SpotsDialog dialog;

	public GetAdressTask(Context context,
			TaskResultHandler<List<Poi>> taskResultHandler) {
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
    protected List<Poi> doInBackground(Object... params) {
        List<Poi> result = null;
        String appUri  = "http://www.fjmap.net/fjmapsvc/api/district/search";
        try {
            String param = (String) params[0];
        	String resultStr = HttpUtil.getDataWithoutCookie2(appUri, param);
            JSONArray resultObj = new JSONArray(resultStr);
            if(resultObj!=null&&resultObj.length()>0){
            	result = new ArrayList<Poi>();
            	for(int i=0;i<resultObj.length();i++){
            		JSONObject jsonObject = resultObj.optJSONObject(i);
            		result.add(new Poi(jsonObject.optString("NAME"), jsonObject.optDouble("LON"), jsonObject.optDouble("LAT"),
            				jsonObject.optDouble("CLASS"), jsonObject.optString("GBCODE"), jsonObject.optDouble("ID"), jsonObject.optString("SubDistricts")));
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
	protected void onPostExecute(List<Poi> result) {
		super.onPostExecute(result);
//		dialog.dismiss();
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}