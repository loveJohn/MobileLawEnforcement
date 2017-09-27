package com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2;

import java.util.List;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.chinadci.mel.android.core.DciActivityManager;

public class MyLocationListener implements BDLocationListener {

	public void onReceiveLocation(BDLocation location) {
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		Gps gps = PositionUtil.bd09_To_Gps84(location.getLatitude(),
				location.getLongitude());
		sb.append("WGS84纬度：");
		sb.append(gps.getWgLat());
		sb.append("\r\n");
		sb.append("WGS84经度：");
		sb.append(gps.getWgLon());
		sb.append("\r\n");
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());// 单位：公里每小时
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
			sb.append("\nheight : ");
			sb.append(location.getAltitude());// 单位：米
			sb.append("\ndirection : ");
			sb.append(location.getDirection());// 单位度
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			sb.append("\ndescribe : ");
			sb.append("gps定位成功");
			DciActivityManager.myLocationLng = location.getLongitude();
			DciActivityManager.myLocationLat = location.getLatitude();
			Log.i("ydzf", "BD_GPS_location,long="+location.getLongitude()+",lat="+location.getLatitude());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			sb.append("\noperationers : ");
			sb.append(location.getOperators());
			sb.append("\ndescribe : ");
			sb.append("网络定位成功");
			DciActivityManager.myLocationLng = location.getLongitude();
			DciActivityManager.myLocationLat = location.getLatitude();
			Log.i("ydzf", "BD_NET_location,long="+location.getLongitude()+",lat="+location.getLatitude());
		} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
			sb.append("\ndescribe : ");
			sb.append("离线定位成功，离线定位结果也是有效的");
			DciActivityManager.myLocationLng = location.getLongitude();
			DciActivityManager.myLocationLat = location.getLatitude();
			Log.i("ydzf", "BD_UN__location,long="+location.getLongitude()+",lat="+location.getLatitude());
		} else if (location.getLocType() == BDLocation.TypeServerError) {
			sb.append("\ndescribe : ");
			sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
		} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
			sb.append("\ndescribe : ");
			sb.append("网络不同导致定位失败，请检查网络是否通畅");
		} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
			sb.append("\ndescribe : ");
			sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
		}
		sb.append("\nlocationdescribe : ");
		sb.append(location.getLocationDescribe());// 位置语义化信息
		List<Poi> list = location.getPoiList();// POI数据
		if (list != null) {
			sb.append("\npoilist size = : ");
			sb.append(list.size());
			for (Poi p : list) {
				sb.append("\npoi= : ");
				sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
			}
		}
		// StringBuffer sb = new StringBuffer(256);
		// sb.append("时间： ");
		// sb.append(location.getTime());
		// sb.append("\r\n");
		// sb.append("返回值：");
		// sb.append(location.getLocType());
		// sb.append("\r\n");
		// sb.append("纬度：");
		// sb.append(location.getLatitude());
		// sb.append("\r\n");
		// sb.append("经度：");
		// sb.append(location.getLongitude());
		// sb.append("\r\n");
		// Gps gps = PositionUtil.bd09_To_Gps84(location.getLatitude(),
		// location.getLongitude());
		// sb.append("WGS84纬度：");
		// sb.append(gps.getWgLat());
		// sb.append("\r\n");
		// sb.append("WGS84经度：");
		// sb.append(gps.getWgLon());
		// sb.append("\r\n");
		// DciActivityManager.myLocationLng = gps.getWgLon();
		// DciActivityManager.myLocationLat = gps.getWgLat();
		// sb.append("定位精度半径：");
		// sb.append(location.getRadius());
		// sb.append("\r\n");
		// sb.append("省份：");
		// sb.append(location.getProvince());
		// sb.append("\r\n");
		// sb.append("城市：");
		// sb.append(location.getCity());
		// sb.append("\r\n");
		// sb.append("城市编码：");
		// sb.append(location.getCityCode());
		// sb.append("\r\n");
		// sb.append("区县：");
		// sb.append(location.getDistrict());
		// sb.append("\r\n");
		// sb.append("街道：");
		// sb.append(location.getStreet());
		// sb.append("\r\n");
		// sb.append("街道号：");
		// sb.append(location.getStreetNumber());
		// sb.append("\r\n");
		// if (location.getLocType() == BDLocation.TypeGpsLocation) {
		// sb.append("耗时：");
		// sb.append(location.getSpeed());
		// sb.append("\r\n");
		// sb.append("gps锁定用的卫星数：");
		// sb.append(location.getSatelliteNumber());
		// sb.append("\r\n");
		// sb.append("详细地址：");
		// sb.append(location.getAddrStr());
		// sb.append("\r\n");
		// sb.append("手机屏幕方向：");
		// sb.append(location.getDirection());
		// sb.append("\r\n");
		// } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
		// sb.append("网络定位类型：");
		// sb.append(location.getNetworkLocationType());
		// sb.append("\r\n");
		// sb.append("详细地址：");
		// sb.append(location.getAddrStr());
		// sb.append("\r\n");
		// sb.append("运营商：");
		// sb.append(location.getOperators());
		// sb.append("\r\n");
		// }
		// // Log.i("BaiduLocationApiDem", sb.toString());
	}

	@Override
	public void onConnectHotSpotMessage(String s, int i) {

	}
}
