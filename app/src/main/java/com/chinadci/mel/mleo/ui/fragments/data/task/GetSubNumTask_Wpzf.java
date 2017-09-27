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
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionEdit;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.MapUtil;
import com.esri.core.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class GetSubNumTask_Wpzf extends
		AbstractBaseTask<Object, Integer, List<Object>> {
	private Context context;
	private String msg;

	// private SpotsDialog dialog;

	public GetSubNumTask_Wpzf(Context context,
			TaskResultHandler<List<Object>> taskResultHandler) {
		super(context, taskResultHandler);
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// dialog = new SpotsDialog(context, R.style.SpotsDialogDefault_1);
		// dialog.show();
	}

	@Override
	protected List<Object> doInBackground(Object... params) {
		List<Object> result = null;
		String appUri = SharedPreferencesUtils.getInstance(context,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_appuri, "http://");
		if (!appUri.endsWith("/")) {
			appUri = appUri + "/";
		}
		appUri = appUri + context.getString(R.string.uri_getSubNum1);
		try {
			String param = "xzqh_id=" + params[0] + "&aj_id=" + params[1] + "&aj_key=" + params[2] +"&user="+params[3]+"&code="+params[4]+"&xfpc="+(params[5]!=null?params[5]:"")+"&aj_year="+(params[6]!=null?params[6]:"")+"&aj_xzqbm="+(params[7]!=null?params[7]:"");		//modify teng.guo
			String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
			JSONObject resultObj = new JSONObject(resultStr);
			if (resultObj.optBoolean("succeed")) {
				int wpWorker=0;
				if (!resultObj.optBoolean("isDetails")) {
					DbUtil.deleteXZQHNumDbDatasByXzqhAndAjAndAjKey(context, (String)params[0], (String)params[1],(String)params[2]);
					JSONArray objArr = resultObj.optJSONArray("msg");
					JSONObject obj;
					result = new ArrayList<Object>();
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
						DbUtil.insertXZQHNumDbDatasWithAjKey(context, xhn, null ,(String)params[1], (String)params[0]);	//modify teng.guo replace instead of insert
						result.add(xhn);
					}
				} else {
					DbUtil.deleteSimpleAjDbDatasByXzqhAndAjAndAjKey(context, (String)params[0], (String)params[1] ,(String)params[2]);
					try{
						wpWorker=resultObj.getInt("isCheckers");
					}catch(Exception e){
						
					}
					JSONArray objArr = resultObj.optJSONArray("msg");
					JSONObject obj;
					result = new ArrayList<Object>();
					for (int i = 0; i < objArr.length(); i++) {
						obj = objArr.optJSONObject(i);
						SimpleAj_Wpzf aj = new SimpleAj_Wpzf();
						aj.setChecker(wpWorker);
						aj.setId(obj.optString("CASE_ID"));
						if(obj.has("lastStatus")){
							aj.setLastState(obj.optString("lastStatus"));
						}else{
							aj.setLastState(null);
						}
						//2017 02 25
						InspectionEdit inspectionEdit = DbUtil.getInspectionEditByBh(context, obj.optString("CASE_ID"));
						if(inspectionEdit!=null){
							aj.setIsSave("true");
						}else{
							aj.setIsSave("false");
						}
						aj.setBh(obj.optString("CASE_BH"));
						if (obj.has("JCBH")) {
							aj.setJcbh(obj.optString("JCBH"));
						} else {
							aj.setJcbh(null);
						}
						//add teng.guo 20170626 start
						if(obj.has("QYBM")){
							aj.setQybm(obj.optString("QYBM"));
						}else{
							aj.setQybm(null);
						}
						//add teng.guo 20170626 end
						aj.setAjly(obj.optString("BMVALUE"));
						aj.setXxdz(obj.optString("WFDD"));
						aj.setHxfxjg(obj.optString("hx_result"));
						try {
							JSONObject jObject = new JSONObject(
									obj.optString("HX"));
							String redline = jObject.optString("rings");
							aj.setHx(redline);
							JSONArray jarrrr = new JSONArray(redline);
							List<Point> pList00 = new ArrayList<Point>();
							for (int m = 0; m < jarrrr.length(); m++) {
								JSONArray pointArrOut = jarrrr.getJSONArray(m);
								List<Point> pList = new ArrayList<Point>();
								for (int k = 0; k < pointArrOut.length(); k++) {
									JSONArray point = pointArrOut
											.getJSONArray(k);
									Point p = new Point(
											Double.parseDouble(point
													.optString(0)),
											Double.parseDouble(point
													.optString(1)));
									pList.add(p);
								}
								Point cenP = MapUtil
										.GetCenterPointFromListOfPoint(pList);
								pList00.add(cenP);
							}
							Point cenP00 = MapUtil
									.GetCenterPointFromListOfPoint(pList00);
							aj.setX(cenP00.getX());
							aj.setY(cenP00.getY());
						} catch (Exception e) {
							aj.setHx(null);
							aj.setX(-1);
							aj.setY(-1);
						}
						if (obj.has("SBSJ")) {
							aj.setSj(obj.optString("SBSJ"));
						}
						if (obj.has("XFSJ")) {
							aj.setSj(obj.optString("XFSJ"));
						}
						aj.setAjKey((String)params[2]);
						if(resultObj.has("isRevoke")&&resultObj.optBoolean("isRevoke")){
							aj.setRevoke("true");
						}else{
							aj.setRevoke("false");
						}
						if(resultObj.has("isApprover")&&resultObj.optBoolean("isApprover")){
							aj.setApprover("true");
						}else{
							aj.setApprover("false");
						}
						DbUtil.replaceSimpleAjDbDatasWithAjKey(context, aj, (String)params[1], (String)params[0]);	//modify teng.guo replace instead of insert
						result.add(aj);
					}
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
	protected void onPostExecute(List<Object> result) {
		super.onPostExecute(result);
		// dialog.dismiss();
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}