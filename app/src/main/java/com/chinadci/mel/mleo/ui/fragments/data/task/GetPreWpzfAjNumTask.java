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
import com.chinadci.mel.mleo.ui.fragments.data.model.WpzfAjNum;

import java.util.ArrayList;
import java.util.List;

public class GetPreWpzfAjNumTask extends
		AbstractBaseTask<Object, Integer, List<WpzfAjNum>> {
	private Context context;
	private String msg;
//	private SpotsDialog dialog;

	public GetPreWpzfAjNumTask(Context context,
			TaskResultHandler<List<WpzfAjNum>> taskResultHandler) {
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
    protected List<WpzfAjNum> doInBackground(Object... params) {
        List<WpzfAjNum> result = null;
        String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
        if (!appUri.endsWith("/")) {
        	appUri = appUri + "/";
		}
        appUri  = appUri + context
				.getString(R.string.uri_getWpzfAjNum);
        try {
            String param = "user="+params[0]+"&xfpc="+(params[1]!=null?params[1]:"")+"&aj_year="+(params[2]!=null?params[2]:"")+"&aj_xzqbm="+(params[3]!=null?params[3]:"");	//modify teng.guo
        	String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
            JSONObject resultObj = new JSONObject(resultStr);
            if (resultObj.optBoolean("succeed")) {
            	JSONArray objArr = resultObj.optJSONArray("ajList");
                JSONObject obj;
                result = new ArrayList<WpzfAjNum>();
                for (int i = 0; i < objArr.length(); i++) {
                	obj = objArr.optJSONObject(i);
                	WpzfAjNum aj = new WpzfAjNum(obj.optString("KEY"), obj.optString("BL_ZT"), obj.optInt("COUNT"),(String) params[0]);
                	DbUtil.deleteWpzfAjNumByKeyAndXzqh(context, aj.getKEY(),aj.getXzqh());
                    DbUtil.insertWpzfAjNum(context, aj.getKEY(), aj.getBL_ZT(), aj.getCOUNT()+"",aj.getXzqh());
                    result.add(aj);
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
	protected void onPostExecute(List<WpzfAjNum> result) {
		super.onPostExecute(result);
//		dialog.dismiss();
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}