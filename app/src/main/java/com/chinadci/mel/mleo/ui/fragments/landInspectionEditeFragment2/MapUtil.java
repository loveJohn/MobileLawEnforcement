package com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

public class MapUtil {

	public static void loadPoint(GraphicsLayer graLayer, MapView map,
			double lng, double lat, Drawable drawable) {// 画出一个点
		if (graLayer == null) {
			graLayer = new GraphicsLayer();
			map.addLayer(graLayer);
		}
		// 加载目标中心点
		Point center_latlng = new Point(lng, lat);// x对应lng，y对应lat
		// SimpleMarkerSymbol sy = new SimpleMarkerSymbol(Color.RED,
		// 1,SimpleMarkerSymbol.STYLE.CIRCLE);//点描
		Graphic graphic = new Graphic(center_latlng, new PictureMarkerSymbol(
				drawable));// 图标
		graLayer.addGraphic(graphic);
	}

	public static void moveToCenter(MapView map, double lng, double lat,
			double d) {
		// x对应lng，y对应lat,平移
		Point center_latlng = new Point(lng, lat);
		map.centerAt(center_latlng, true);
		map.zoomTo(center_latlng, (float) d);
	}

	public static void moveToCenter(MapView map, Point p, double d) {
		map.zoomTo(p, (float) d);
	}

	public static void moveToCenterScale(MapView map, Point p, double d) {
		map.zoomToScale(p, d);
	}

	public static void moveToUserPosition(MapView mMap, double lng, double lat,
			double d) {
		// TODO
	}

	// /**
	// * 加载多边形，无返回
	// */
	// public static void loadPolygon(GraphicsLayer graPolyLayer, MapView
	// map,String pointsStr) {
	// if (pointsStr != null && !"".equals(pointsStr)) {
	// try {
	// JSONArray pointArr = new JSONArray(pointsStr);
	// Polygon poly = new Polygon();
	// JSONObject pointStart = pointArr.getJSONObject(0);
	// Point firstPoint = new
	// Point(pointStart.getDouble("lng"),pointStart.getDouble("lat"));
	// poly.startPath(firstPoint);// 添加初始点
	// for (int i = 1; i < pointArr.length(); i++) {
	// JSONObject point = pointArr.getJSONObject(i);
	// poly.lineTo(new Point(point.getDouble("lng"), point.getDouble("lat")));
	// }
	// poly.lineTo(new Point(pointStart.getDouble("lng"),
	// pointStart.getDouble("lat")));// 添加初始点
	// SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb(0,217,34,34));
	// sfs.setOutline(new SimpleLineSymbol(Color.RED, 2 ,STYLE.NULL));
	// sfs.setAlpha(85);
	// Graphic gra = new Graphic(poly, sfs);
	// graPolyLayer.addGraphic(gra);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// /**
	// * 加载多边形，返回地块坐标点
	// */
	// public static Point loadPolygonAndPoint(GraphicsLayer graPolyLayer,
	// MapView map,String pointsStr) {
	// Point firstPoint = null;
	// if (pointsStr != null && !"".equals(pointsStr)) {
	// try {
	// JSONArray pointArr = new JSONArray(pointsStr);
	// Polygon poly = new Polygon();
	// JSONObject pointStart = pointArr.getJSONObject(0);
	// firstPoint = new
	// Point(pointStart.getDouble("lng"),pointStart.getDouble("lat"));
	// poly.startPath(firstPoint);// 添加初始点
	// for (int i = 1; i < pointArr.length(); i++) {
	// JSONObject point = pointArr.getJSONObject(i);
	// poly.lineTo(new Point(point.getDouble("lng"), point.getDouble("lat")));
	// }
	// poly.lineTo(new Point(pointStart.getDouble("lng"),
	// pointStart.getDouble("lat")));// 添加初始点
	// SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb(0,217,34,34));
	// sfs.setOutline(new SimpleLineSymbol(Color.RED, 2,STYLE.NULL));
	// sfs.setAlpha(85);
	// Graphic gra = new Graphic(poly, sfs);
	// graPolyLayer.addGraphic(gra);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// return firstPoint;
	// }
	//
	//
	// /**
	// *自定义多边形，无返回
	// *
	// * @tags @param graPolyLayer
	// * @tags @param map
	// * @tags @param pointsStr
	// * @tags @param drawable
	// * @return_type void
	// * @date 2015年9月11日
	// */
	// public static void loadCurPolygon(GraphicsLayer graPolyLayer,MapView map,
	// String pointsStr,Drawable drawable) {
	// if (pointsStr != null && !"".equals(pointsStr)) {
	// try {
	// JSONArray pointArr = new JSONArray(pointsStr);
	// Polygon poly = new Polygon();
	// JSONObject pointStart = pointArr.getJSONObject(0);
	// poly.startPath(new
	// Point(pointStart.getDouble("lng"),pointStart.getDouble("lat")));// 添加初始点
	// for (int i = 1; i < pointArr.length(); i++) {
	// JSONObject point = pointArr.getJSONObject(i);
	// poly.lineTo(new Point(point.getDouble("lng"), point.getDouble("lat")));
	// }
	// poly.lineTo(new Point(pointStart.getDouble("lng"),
	// pointStart.getDouble("lat")));// 添加初始点
	// // 加载目标图形
	// if (graPolyLayer == null) {
	// graPolyLayer = new GraphicsLayer();
	// map.addLayer(graPolyLayer);
	// }
	// graPolyLayer.removeAll();
	// SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb( 0,217,34,34));
	// sfs.setOutline(new SimpleLineSymbol(Color.RED, 2,STYLE.NULL));
	// sfs.setAlpha(85);
	// Graphic gra = new Graphic(poly, sfs);
	// graPolyLayer.addGraphic(gra);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// *
	// *加载多种类型多边形且带定位，返回地块坐标
	// * @tags @param graPolyLayer
	// * @tags @param map
	// * @tags @param pointsStr---服务器传的redline数组字符
	// * @tags @param jd
	// * @tags @return
	// * @return_type Point
	// * @date 2015年9月11日
	// */
	// public static Point loadPolygonAndPoint(GraphicsLayer graPolyLayer,
	// MapView map,String pointsStr,int jd) {
	// Point firstPoint = null;
	// if (pointsStr != null && !"".equals(pointsStr)) {
	// try {
	// JSONArray pointArr = new JSONArray(pointsStr);
	// Polygon poly = new Polygon();
	// JSONObject pointStart = pointArr.getJSONObject(0);
	// firstPoint = new
	// Point(pointStart.getDouble("lng"),pointStart.getDouble("lat"));
	// poly.startPath(firstPoint);// 添加初始点
	// for (int i = 1; i < pointArr.length(); i++) {
	// JSONObject point = pointArr.getJSONObject(i);
	// poly.lineTo(new Point(point.getDouble("lng"), point.getDouble("lat")));
	// }
	// poly.lineTo(new Point(pointStart.getDouble("lng"),
	// pointStart.getDouble("lat")));// 添加初始点
	// SimpleFillSymbol sfs;
	// Graphic gra;
	// switch (jd) {
	// case 0:
	// sfs = new SimpleFillSymbol(Color.argb(0,255,121,0));
	// sfs.setOutline(new SimpleLineSymbol(Color.RED, 2,STYLE.NULL));
	// sfs.setAlpha(85);
	// gra = new Graphic(poly, sfs);
	// graPolyLayer.addGraphic(gra);
	// break;
	// case 1:
	// sfs = new SimpleFillSymbol(Color.argb(0,255,121,0));
	// sfs.setOutline(new SimpleLineSymbol(Color.RED, 2,STYLE.NULL));
	// sfs.setAlpha(85);
	// gra = new Graphic(poly, sfs);
	// graPolyLayer.addGraphic(gra);
	// break;
	// case 2:
	// sfs = new SimpleFillSymbol(Color.argb(0,59,181,255));
	// sfs.setOutline(new SimpleLineSymbol(Color.RED, 2,STYLE.NULL));
	// sfs.setAlpha(85);
	// gra = new Graphic(poly, sfs);
	// graPolyLayer.addGraphic(gra);
	// break;
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// return firstPoint;
	// }
	/**
	 * 加载多边形，无返回--[[[119],[26]],[]]
	 */
	public static void loadPolygon(GraphicsLayer graPolyLayer, MapView map,
			String pointsStr) {
		if (pointsStr != null && !"".equals(pointsStr)) {
			try {
				JSONArray pointArr = new JSONArray(pointsStr);
				for (int i = 0; i < pointArr.length(); i++) {
					JSONArray pointArrOut = pointArr.getJSONArray(i);
					Polygon poly = new Polygon();
					JSONArray firstArrPoint = pointArrOut.getJSONArray(0);
					Point firstPoint = new Point(
							Double.parseDouble(firstArrPoint.optString(0)),
							Double.parseDouble(firstArrPoint.optString(1)));
					poly.startPath(firstPoint);// 添加初始点
					for (int j = 1; j < pointArrOut.length(); j++) {
						JSONArray point = pointArrOut.getJSONArray(j);
						poly.lineTo(new Point(Double.parseDouble(point
								.optString(0)), Double.parseDouble(point
								.optString(1))));
					}
					poly.lineTo(new Point(Double.parseDouble(firstArrPoint
							.optString(0)), Double.parseDouble(firstArrPoint
							.optString(1))));// 添加初始点
					if (i == 0) {
						SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb(
								0, 46, 117, 182));
						sfs.setOutline(new SimpleLineSymbol(Color.RED, 2));
						sfs.setAlpha(15);
						Graphic gra = new Graphic(poly, sfs);
						graPolyLayer.addGraphic(gra);
					} else {
						SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb(
								0, 255, 255, 255));
						sfs.setOutline(new SimpleLineSymbol(Color.RED, 2));
						sfs.setAlpha(25);
						Graphic gra = new Graphic(poly, sfs);
						graPolyLayer.addGraphic(gra);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加载多边形，返回地块坐标点
	 */
	public static Point loadPolygonAndPoint(GraphicsLayer graPolyLayer,
			MapView map, String pointsStr) {
		Point firstPoint = null;
		if (pointsStr != null && !"".equals(pointsStr)) {
			try {
				JSONArray pointArr = new JSONArray(pointsStr);
				for (int i = 0; i < pointArr.length(); i++) {
					JSONArray pointArrOut = pointArr.getJSONArray(i);
					Polygon poly = new Polygon();
					JSONArray firstArrPoint = pointArrOut.getJSONArray(0);
					firstPoint = new Point(Double.parseDouble(firstArrPoint
							.optString(0)), Double.parseDouble(firstArrPoint
							.optString(1)));
					poly.startPath(firstPoint);// 添加初始点
					for (int j = 1; j < pointArrOut.length(); j++) {
						JSONArray point = pointArrOut.getJSONArray(j);
						poly.lineTo(new Point(Double.parseDouble(point
								.optString(0)), Double.parseDouble(point
								.optString(1))));
					}
					poly.lineTo(new Point(Double.parseDouble(firstArrPoint
							.optString(0)), Double.parseDouble(firstArrPoint
							.optString(1))));// 添加初始点
					if (i == 0) {
						SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb(
								0, 46, 117, 182));
						sfs.setOutline(new SimpleLineSymbol(Color.RED, 2));
						sfs.setAlpha(15);
						Graphic gra = new Graphic(poly, sfs);
						graPolyLayer.addGraphic(gra);
					} else {
						SimpleFillSymbol sfs = new SimpleFillSymbol(Color.argb(
								0, 255, 255, 255));
						sfs.setOutline(new SimpleLineSymbol(Color.RED, 2));
						sfs.setAlpha(25);
						Graphic gra = new Graphic(poly, sfs);
						graPolyLayer.addGraphic(gra);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return firstPoint;
	}

	/**
	 * 
	 * 加载多种类型多边形且带定位，返回地块坐标
	 * 
	 * @tags @param graPolyLayer
	 * @tags @param map
	 * @tags @param pointsStr---服务器传的redline数组字符
	 * @tags @param jd
	 * @tags @return
	 * @return_type Point
	 * @date 2015年9月11日
	 */
	public static Point loadPolygonAndPoint(GraphicsLayer graPolyLayer,
			MapView map, String pointsStr, int jd) {
		Point firstPoint = null;
		if (pointsStr != null && !"".equals(pointsStr)) {
			try {
				JSONArray pointArr = new JSONArray(pointsStr);
				for (int i = 0; i < pointArr.length(); i++) {
					JSONArray pointArrOut = pointArr.getJSONArray(i);
					Polygon poly = new Polygon();
					JSONArray firstArrPoint = pointArrOut.getJSONArray(0);
					firstPoint = new Point(Double.parseDouble(firstArrPoint
							.optString(0)), Double.parseDouble(firstArrPoint
							.optString(1)));
					poly.startPath(firstPoint);// 添加初始点
					for (int j = 1; j < pointArrOut.length(); j++) {
						JSONArray point = pointArrOut.getJSONArray(j);
						poly.lineTo(new Point(Double.parseDouble(point
								.optString(0)), Double.parseDouble(point
								.optString(1))));
					}
					poly.lineTo(new Point(Double.parseDouble(firstArrPoint
							.optString(0)), Double.parseDouble(firstArrPoint
							.optString(1))));// 添加初始点
					if (i == 0) {
						SimpleFillSymbol sfsOut;
						Graphic graOut;
						switch (jd) {
						case 0:
							sfsOut = new SimpleFillSymbol(Color.argb(0, 46,
									117, 182));
							sfsOut.setOutline(new SimpleLineSymbol(Color.RED, 2));
							sfsOut.setAlpha(15);
							graOut = new Graphic(poly, sfsOut);
							graPolyLayer.addGraphic(graOut);
							break;
						case 1:
							sfsOut = new SimpleFillSymbol(Color.argb(0, 46,
									117, 182));
							sfsOut.setOutline(new SimpleLineSymbol(Color.RED, 2));
							sfsOut.setAlpha(15);
							graOut = new Graphic(poly, sfsOut);
							graPolyLayer.addGraphic(graOut);
							break;
						case 2:
							sfsOut = new SimpleFillSymbol(Color.argb(0, 255,
									217, 102));
							sfsOut.setOutline(new SimpleLineSymbol(Color.RED, 2));
							sfsOut.setAlpha(15);
							graOut = new Graphic(poly, sfsOut);
							graPolyLayer.addGraphic(graOut);
							break;
						}
					} else {
						SimpleFillSymbol sfsIn = new SimpleFillSymbol(
								Color.argb(0, 255, 255, 255));
						sfsIn.setOutline(new SimpleLineSymbol(Color.RED, 2));
						sfsIn.setAlpha(25);
						Graphic graIn = new Graphic(poly, sfsIn);
						graPolyLayer.addGraphic(graIn);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return firstPoint;
	}

	private static final double EARTH_RADIUS = 6378137;// 赤道半径(单位m)

	/**
	 * 转化为弧度(rad)
	 * */
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 基于余弦定理求两经纬度距离
	 * 
	 * @param lon1
	 *            第一点的精度
	 * @param lat1
	 *            第一点的纬度
	 * @param lon2
	 *            第二点的精度
	 * @param lat3
	 *            第二点的纬度
	 * @return 返回的距离，单位m
	 * */
	public static double LantitudeLongitudeDist(double lon1, double lat1,
			double lon2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double radLon1 = rad(lon1);
		double radLon2 = rad(lon2);
		if (radLat1 < 0)
			radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
		if (radLat1 > 0)
			radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
		if (radLon1 < 0)
			radLon1 = Math.PI * 2 - Math.abs(radLon1);// west
		if (radLat2 < 0)
			radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
		if (radLat2 > 0)
			radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
		if (radLon2 < 0)
			radLon2 = Math.PI * 2 - Math.abs(radLon2);// west
		double x1 = EARTH_RADIUS * Math.cos(radLon1) * Math.sin(radLat1);
		double y1 = EARTH_RADIUS * Math.sin(radLon1) * Math.sin(radLat1);
		double z1 = EARTH_RADIUS * Math.cos(radLat1);
		double x2 = EARTH_RADIUS * Math.cos(radLon2) * Math.sin(radLat2);
		double y2 = EARTH_RADIUS * Math.sin(radLon2) * Math.sin(radLat2);
		double z2 = EARTH_RADIUS * Math.cos(radLat2);
		double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
				+ (z1 - z2) * (z1 - z2));
		// 余弦定理求夹角
		double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS
				* EARTH_RADIUS - d * d)
				/ (2 * EARTH_RADIUS * EARTH_RADIUS));
		double dist = theta * EARTH_RADIUS;// 单位m
		return dist;
	}

	// / <summary>
	// / 根据输入的地点坐标计算中心点
	// / </summary>
	// / <param name="geoCoordinateList"></param>
	// / <returns></returns>
	public static Point GetCenterPointFromListOfPoint(List<Point> geoCoordinateList) {
		int total = geoCoordinateList.size();
		double X = 0, Y = 0, Z = 0;
		for (Point p : geoCoordinateList) {
			double lat, lon, x, y, z;
			lat = p.getY() * Math.PI / 180;
			lon = p.getX() * Math.PI / 180;
			x = Math.cos(lat) * Math.cos(lon);
			y = Math.cos(lat) * Math.sin(lon);
			z = Math.sin(lat);
			X += x;
			Y += y;
			Z += z;
		}
		X = X / total;
		Y = Y / total;
		Z = Z / total;
		double Lon = Math.atan2(Y, X);
		double Hyp = Math.sqrt(X * X + Y * Y);
		double Lat = Math.atan2(Z, Hyp);
		return new Point(Lon * 180 / Math.PI, Lat * 180 / Math.PI);
	}

	// / <summary>
	// / 根据输入的地点坐标计算中心点（适用于400km以下的场合）
	// / </summary>
	// / <param name="geoCoordinateList"></param>
	// / <returns></returns>
	public static Point GetCenterPointFromListOfCoordinates(
			List<Point> geoCoordinateList) {
		// 以下为简化方法（400km以内）
		int total = geoCoordinateList.size();
		double lat = 0, lon = 0;
		for (Point p : geoCoordinateList) {
			lat += p.getY() * Math.PI / 180;
			lon += p.getX() * Math.PI / 180;
		}
		lat /= total;
		lon /= total;
		return new Point(lon * 180 / Math.PI, lat * 180 / Math.PI);
	}
}
