package com.chinadci.mel.mleo.utils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.Gps;
import com.chinadci.mel.mleo.ui.fragments.landInspectionEditeFragment2.PositionUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.widget.Toast;

public class LocUtils {
	
	public static final String LOCATION_SERVER="locService";

    Context c;
    static LocUtils locUtil;
    Location loc;
    LocationClient client;
    NetLocationCallback callback;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    private LocUtils(Context c){
        this.c=c;
    }

    public static LocUtils newInstance(Context c){
        if (locUtil==null){
            locUtil=new LocUtils(c);
        }
        return locUtil;
    }

    public boolean getGPSStatus(){
        if (!LocationUtils.isGPSSupport(c)){      //检测GPS
        	Toast.makeText(c, R.string.str_gps_support_close_tips, Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    public Location catchGPSLocation(){
        Location location =null;
        if (getGPSStatus()){
            location = LocationUtils.getGPSLocation(c);
            if (location!=null){
            	DciActivityManager.myLocationLng=location.getLongitude();
            	DciActivityManager.myLocationLat=location.getLatitude();
            }
        }
        return location;
    }

    public Location catchCurrentLocation(){
        Location location= LocationUtils.getCurrentLocation(c);
        if (location!=null){
        	DciActivityManager.myLocationLng=location.getLongitude();
        	DciActivityManager.myLocationLat=location.getLatitude();
        }else {
            location=catchGPSLocation();
        }
        return location;
    }


    //**********************************百度定位*****************************************

    /**
     * @Teng.guo
     * 不管调用者有没有做回调，只要本应用有过网络定位操作，都能在适当网络延时后在MELApplication静态值中获取到经纬度值。
     */

    public LocationClient getBDLocationClient(){
        if (client==null) {
            client = new LocationClient(c.getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

            option.setCoorType("bd09ll");
            //可选，默认gcj02，设置返回的定位结果坐标系

            option.setScanSpan(5000);
            //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

            option.setIsNeedAddress(true);
            //可选，设置是否需要地址信息，默认不需要

            option.setOpenGps(false);
            //可选，默认false,设置是否使用gps

            option.setLocationNotify(false);
            //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

            option.setIsNeedLocationDescribe(true);
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

            option.setIgnoreKillProcess(false);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

            option.SetIgnoreCacheException(false);
            //可选，默认false，设置是否收集CRASH信息，默认收集

            option.setEnableSimulateGps(false);
            //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

            option.setNeedDeviceDirect(true);
            //可选，返回的定位结果包含手机机头的方向

            client.setLocOption(option);
        }
        return client;
    }

    public LocUtils setNetLocationCallback(NetLocationCallback callback){
        this.callback=callback;
        return this;
    }

    //开辟子线程进行网络定位，并回调接口。调用者需要setNetLocationCallback，重写接口回调方法，并startNetLocation即可。
    public void startNetLocation(){
        if (NetWorkUtils.isNetworkAvailable(c)){
            if (dialog!=null){
                dialog.dismiss();
            }
        }else {
        	Toast.makeText(c, R.string.cn_nonetwork, Toast.LENGTH_SHORT).show();
            return;
        }
        LocationClient cacheClient= getBDLocationClient();
        cacheClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                StringBuffer sb = new StringBuffer();

                sb.append(" time : ");
                sb.append(bdLocation.getTime());

                sb.append("\n LocType : ");
                sb.append(bdLocation.getLocType());

                sb.append("\n latitude : ");
                sb.append(bdLocation.getLatitude());

                sb.append("\n longitude : ");
                sb.append(bdLocation.getLongitude());

                sb.append("\n radius : ");
                sb.append(bdLocation.getRadius());
                Gps gps=null;
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {    // GPS定位结果
                    gps = PositionUtil.bd09_To_Gps84(bdLocation.getLatitude(),bdLocation.getLongitude());
                    sb.append("\n address :");
                    sb.append(bdLocation.getAddrStr());
                    sb.append("\n operator : ");
                    sb.append(bdLocation.getOperators());
                    sb.append("\n describe : ");
                    sb.append("GPS定位成功");
                    DciActivityManager.myLocationLng=gps.getWgLon();
                	DciActivityManager.myLocationLat=gps.getWgLat();
                   }else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    gps = PositionUtil.bd09_To_Gps84(bdLocation.getLatitude(),bdLocation.getLongitude());
                    sb.append("\n WGS84经度 :");
                    sb.append(gps.getWgLon());
                    sb.append(" WGS84纬度 :");
                    sb.append(gps.getWgLat());
                    sb.append("\n address : ");
                    sb.append(bdLocation.getAddrStr());
                    sb.append("\n operator : ");
                    sb.append(bdLocation.getOperators());
                    sb.append("\n describe : ");
                    sb.append("网络定位成功");
                    DciActivityManager.myLocationLng=gps.getWgLon();
                	DciActivityManager.myLocationLat=gps.getWgLat();
                   } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {  // 离线定位结果
                    gps = PositionUtil.bd09_To_Gps84(bdLocation.getLatitude(),bdLocation.getLongitude());
                    sb.append("\n describe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                    DciActivityManager.myLocationLng=gps.getWgLon();
                	DciActivityManager.myLocationLat=gps.getWgLat();
                   }
                if (callback!=null) {               //如果没有设置回调监听，则不做回调。
                    callback.onLocationReceive(bdLocation, gps);
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }

        });
        cacheClient.start();
    }

    public interface NetLocationCallback{
        void onLocationReceive(BDLocation l,Gps g);
    }

}
