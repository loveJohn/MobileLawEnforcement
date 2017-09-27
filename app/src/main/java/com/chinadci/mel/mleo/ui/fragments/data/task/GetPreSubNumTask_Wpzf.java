package com.chinadci.mel.mleo.ui.fragments.data.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.fragments.data.HttpUtil;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum_Wpzf;

import java.util.ArrayList;
import java.util.List;

public class GetPreSubNumTask_Wpzf extends
		AbstractBaseTask<Object, Integer, List<XZQHNum_Wpzf>> {
	private Context context;
	private String msg;
//	private SpotsDialog dialog;

	public GetPreSubNumTask_Wpzf(Context context,
			TaskResultHandler<List<XZQHNum_Wpzf>> taskResultHandler) {
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
    protected List<XZQHNum_Wpzf> doInBackground(Object... params) {
        List<XZQHNum_Wpzf> result = null;
        String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
        if (!appUri.endsWith("/")) {
        	appUri = appUri + "/";
		}
        appUri  = appUri + context
				.getString(R.string.uri_getPreSubNum1);
        try {
            String param = "user="+params[0]+"&aj_id="+params[1]+"&aj_key="+params[2]+"&xfpc="+(params[3]!=null?params[3]:"")+"&aj_year="+(params[4]!=null?params[4]:"")+"&aj_xzqbm="+(params[5]!=null?params[5]:"");
            Log.i("guoteng","param="+param);
        	String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
            JSONObject resultObj = new JSONObject(resultStr);
            if (resultObj.optBoolean("succeed")) {
            	DbUtil.deleteXZQHNumDbDatasByUserAndAjAndAjKey(context, (String)params[0], (String)params[1], (String)params[2]);
                JSONArray objArr = resultObj.optJSONArray("msg");
                JSONObject obj;
                result = new ArrayList<XZQHNum_Wpzf>();
                for (int i = 0; i < objArr.length(); i++) {
                    obj = objArr.optJSONObject(i);
                    XZQHNum_Wpzf xhn = new XZQHNum_Wpzf();
                    xhn.setName(obj.optString("NAME"));
                    xhn.setNum(obj.optInt("COUNT"));
                    xhn.setId(obj.optString("ID"));
                    xhn.setPid(obj.optString("PID"));
                    xhn.setHasSub(obj.optBoolean("hasSub"));
                    xhn.setXzqudm(obj.optString("CODE"));
                    xhn.setAjKey((String)params[2]);
                    DbUtil.replaceXZQHNumDbDatasWithAjKey(context, xhn, (String)params[0], (String)params[1], null);	//modify teng.guo replace instead of insert
                    result.add(xhn);
                }
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
	protected void onPostExecute(List<XZQHNum_Wpzf> result) {
		super.onPostExecute(result);
//		dialog.dismiss();
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}