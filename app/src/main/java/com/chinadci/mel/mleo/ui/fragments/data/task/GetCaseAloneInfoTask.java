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
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj2;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.MapUtil;
import com.esri.core.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class GetCaseAloneInfoTask extends
		AbstractBaseTask<Object, Integer, List<Object>> {
	private Context context;
	private String msg;

	// private SpotsDialog dialog;

	public GetCaseAloneInfoTask(Context context,
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
		appUri = appUri + context.getString(R.string.uri_casealoneinfo);
		try {
			String param = "case_id=" + params[0];
			String resultStr = HttpUtil.getDataWithoutCookie(appUri, param);
			JSONObject resultObj = new JSONObject(resultStr);
			if (resultObj.optBoolean("succeed")) {
				DbUtil.deleteSimpleAj2DbDatasById(context,
						(String) params[0]);
				JSONArray objArr = resultObj.optJSONArray("msg");
				JSONObject obj;
				result = new ArrayList<Object>();
				for (int i = 0; i < objArr.length(); i++) {
					obj = objArr.optJSONObject(i);
					SimpleAj2 aj = new SimpleAj2();
					aj.setId(obj.optString("CASE_ID"));
					aj.setIsSave("false");
					aj.setBh(obj.optString("CASE_BH"));
					if (obj.has("JCBH")) {
						aj.setJcbh(obj.optString("JCBH"));
					} else {
						aj.setJcbh(null);
					}
					aj.setAjly(obj.optString("BMVALUE"));
					aj.setXxdz(obj.optString("WFDD"));
					aj.setHxfxjg(obj.optString("hx_result"));
					try {
						JSONObject jObject = new JSONObject(obj.optString("HX"));
						String redline = jObject.optString("rings");
						aj.setHx(redline);
						JSONArray jarrrr = new JSONArray(redline);
						List<Point> pList00 = new ArrayList<Point>();
						for (int m = 0; m < jarrrr.length(); m++) {
							JSONArray pointArrOut = jarrrr.getJSONArray(m);
							List<Point> pList = new ArrayList<Point>();
							for (int k = 0; k < pointArrOut.length(); k++) {
								JSONArray point = pointArrOut.getJSONArray(k);
								Point p = new Point(Double.parseDouble(point
										.optString(0)),
										Double.parseDouble(point.optString(1)));
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
					DbUtil.insertSimpleAj2DbDatas(context, aj);
					result.add(aj);
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