package com.chinadci.mel.mleo.ui.fragments;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.OutsideManifestHandler;
import com.chinadci.mel.mleo.ui.views.PolyGatherView_Adress;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;

public class MapAnalyzeFragment extends ContentFragment {

	PolyGatherView_Adress gatherView;
	String geoJson;

	/**
	 * 基础地图
	 */
	protected TiledServiceLayer vectorTiledLayer;
	/**
	 * 基础地图
	 */
	protected TiledServiceLayer vectorTiledLayer_;
	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer;
	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer_;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.i("ydzf","MapAnalyzeFragment onCreateView");
		gatherView = new PolyGatherView_Adress(getActivity());
		Log.i("ydzf","MapAnalyzeFragment PolyGatherView_Adress onCreate");
		if (vectorTiledLayer != null&&vectorTiledLayer_ != null)
			gatherView.setVectorTiledLayer(vectorTiledLayer,vectorTiledLayer_);
		if (imageryTiledLayer != null&&imageryTiledLayer_ != null)
			gatherView.setImageryLayer(imageryTiledLayer,imageryTiledLayer_);
		gatherView.setMapStatusChangedListener(statusChangedListener);
		return gatherView;
	}

	@Override
	public void handle(Object o) {
		super.handle(o);
		try {
			ArrayList<Point> s = gatherView.getPointList();
			if (s == null || s.size() < 3) {
				Toast.makeText(context, "分析图斑至少要3个点", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			if (gatherView.geoCrossSelf(s)) {
				Toast.makeText(context, "图斑有自交现象", Toast.LENGTH_SHORT).show();
				return;
			}

			geoJson = polygonToJson(s);// 图斑JSON
			JSONObject object = new JSONObject();
			object.put("user", currentUser);
			object.put("polygon", new JSONObject(geoJson));
			new GeographicSendTask().execute(object);
		} catch (Exception e) {
		}

	}

	OnStatusChangedListener statusChangedListener = new OnStatusChangedListener() {

		/**
		 * @Fields serialVersionUID : TODO
		 */
		private static final long serialVersionUID = 1L;

		public void onStatusChanged(Object source, STATUS status) {
			try {
				MapView mapView = (MapView) source;
				@SuppressWarnings("deprecation")
				Location location = mapView.getLocationDisplayManager().getLocation();
				if (location != null
						&& pointIsInside(new Point(location.getLongitude(),
								location.getLatitude()), mapView.getMaxExtent())) {
					mapView.zoomToScale(new Point(location.getLongitude(),
							location.getLatitude()), context.getResources()
							.getDisplayMetrics().density * 20000d);
					gatherView.viewMapScale(context.getResources()
							.getDisplayMetrics().density * 20000d);
				} else {
					OutsideManifestHandler handler = OutsideManifestHandler
							.getHandler(context);
					if (handler != null && handler.getUserMapCentre() != null) {
						Point centrePoint = handler.getUserMapCentre()
								.getPoint();
						double scale = handler.getUserMapCentre().getScale();
						if (centrePoint != null && scale > 0) {
							mapView.zoomToScale(centrePoint, scale);
							gatherView.viewMapScale(scale);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private String polygonToJson(ArrayList<Point> points) {
		if (points == null || points.size() < 1)
			return null;

		try {
			MultiPath poly;
			poly = new Polygon();
			poly.startPath(points.get(0));
			for (int i = 1; i < points.size(); i++) {
				poly.lineTo(points.get(i));
			}

			long area = Math.round(poly.calculateArea2D());
			if (area > 0) {
				String json = GeometryEngine.geometryToJson(null, poly);
				return json;
			} else {
				MultiPath polyRe;
				polyRe = new Polygon();
				polyRe.startPath(points.get(0));
				polyRe.startPath(points.get(points.size() - 1));
				for (int i = points.size() - 2; i > -1; i--) {
					polyRe.lineTo(points.get(i));
				}
				long area2 = Math.round(polyRe.calculateArea2D());
				String json = GeometryEngine.geometryToJson(null, poly);
				return json;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	class GeographicSendTask extends AsyncTask<JSONObject, Void, Boolean> {
		CircleProgressBusyView abv;
		AlertDialog alertDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			int cn_sendding = getResources().getIdentifier(
					context.getPackageName() + ":string/" + "cn_sendding",
					null, null);
			abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(JSONObject... params) {
			boolean succeed = false;
			String uri;
			int shared_preferences = getResources().getIdentifier(
					context.getPackageName() + ":string/"
							+ "shared_preferences", null, null);
			int sp_appuri = getResources().getIdentifier(
					context.getPackageName() + ":string/" + "sp_appuri", null,
					null);

			int uri_mapanalyze = getResources().getIdentifier(
					context.getPackageName() + ":string/" + "uri_mapanalyze",
					null, null);
			try {
				if (params != null && params.length > 0) {
					JSONObject json = params[0];
					String appUri = SharedPreferencesUtils.getInstance(context,
							shared_preferences).getSharedPreferences(sp_appuri,
							"");
					uri = appUri.endsWith("/") ? new StringBuffer(appUri)
							.append(context.getString(uri_mapanalyze))
							.toString() : new StringBuffer(appUri).append("/")
							.append(context.getString(uri_mapanalyze))
							.toString();
					HttpResponse response = HttpUtils.httpClientExcutePost(uri,
							json);
					succeed = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			Toast.makeText(context, "分析请求已提交，请注意查收短信", Toast.LENGTH_LONG)
					.show();
			MapAnalyzeFragment.this.getActivity().finish();
		}
	}

	private boolean pointIsInside(Point point, Envelope extent) {
		return (point.getX() > extent.getXMin()
				&& point.getX() < extent.getXMax()
				&& point.getY() > extent.getYMin() && point.getY() < extent
				.getYMax());
	}
}
