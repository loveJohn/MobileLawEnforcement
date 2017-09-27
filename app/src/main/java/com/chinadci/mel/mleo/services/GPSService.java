package com.chinadci.mel.mleo.services;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;

public class GPSService extends Service {
	private boolean started;
	private boolean threadDisable;
	private Boolean isStart=false;
	private int gpsCount=0;
	private int positonCount=0;
	private LocationManager lm;
	
	private static final String TAG="GpsActivity";
	String currentUser;
	String currentUrl;
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		try
		{
			currentUser = SharedPreferencesUtils.getInstance(this, R.string.shared_preferences).getSharedPreferences(R.string.sp_actuser, "");
			//String uri = "";
			String appUri = SharedPreferencesUtils.getInstance(this,R.string.shared_preferences).getSharedPreferences(R.string.sp_appuri, "");
			currentUrl = appUri.endsWith("/") ? new StringBuffer(appUri).append(getString(R.string.uri_gps_service)).toString(): new StringBuffer(appUri).append("/").append(getString(R.string.uri_gps_service)).toString();
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        //PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); 
	        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag"); 
	        mWakeLock.acquire(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onStart(Intent intent,int startId)
	{
		Log.d(TAG, "GPS定位服务开始.");
		//Toast.makeText(getApplicationContext(), "启动服务", Toast.LENGTH_SHORT).show();
		lm=(LocationManager)getSystemService(LOCATION_SERVICE);
		//lm.req
		//判断GPS是否正常启动
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
//            startActivityForResult(intent,0); 
            return;
        }
        
        
        stratmap();
		isStart=true;
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy()
	{
		//Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();  
		isStart=false;
        super.onDestroy();  
        lm.removeUpdates(locationListener);  
      
        //lm.removeUpdates(locationListener);  
        //stopSelf();  
	}
	
	
	private void stratmap()
	{
		
	        String bestProvider = lm.getBestProvider(getCriteria(), true);
	        //获取位置信息
	        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
	        Location location= lm.getLastKnownLocation(bestProvider);   
	        //如果位置信息为null，则请求更新位置信息
	        if(location == null){
	        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	        }
	        
        	//updateView(location);
	      
	        //监听状态
	        lm.addGpsStatusListener(listener);
	        
	        //绑定监听，有4个参数    
	        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
	        //参数2，位置信息更新周期，单位毫秒    
	        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息    
	        //参数4，监听    
	        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新   
	        
	        // 1秒更新一次，或最小位移变化超过1米更新一次；
	        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
	        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
		
	}
	
	//位置监听
    private LocationListener locationListener=new LocationListener() {
        
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
        	if(isStart){
	        	positonCount++;

				/* 2014年10月24日修改，注释if块，直接执行new GPSGetTask(location).execute();
				leix@geo-k.cn*/
	        	//if(positonCount==3)
	        	//{
	        	//	positonCount=0;
	        	//	new GPSGetTask(location).execute();
        		//	//updateView(location);
	        	//}
	            new GPSGetTask(location).execute();

	            Log.i(TAG, "时间："+location.getTime()); 
	            Log.i(TAG, "经度："+location.getLongitude()); 
	            Log.i(TAG, "纬度："+location.getLatitude()); 
	            Log.i(TAG, "海拔："+location.getAltitude()); 
        	}
        }
        
        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
                Log.i(TAG, "当前GPS状态为可见状态");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
                Log.i(TAG, "当前GPS状态为服务区外状态");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.i(TAG, "当前GPS状态为暂停服务状态");
                break;
            }
        }
    
        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location=lm.getLastKnownLocation(provider);
            updateView(location);
        }
    
        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

    
    };
    
    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
            //第一次定位
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.i(TAG, "第一次定位");
                break;
            //卫星状态改变
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.i(TAG, "卫星状态改变");
                //获取当前状态
                GpsStatus gpsStatus=lm.getGpsStatus(null);
                //获取卫星颗数的默认最大值
                int maxSatellites = gpsStatus.getMaxSatellites();
              //通过遍历重新整理为ArrayList    
                ArrayList<GpsSatellite> satelliteList=new ArrayList<GpsSatellite>();    
                //创建一个迭代器保存所有卫星 
                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                int count = 0;     
                while (iters.hasNext() && count <= maxSatellites) {     
                    GpsSatellite s = iters.next();     
                    satelliteList.add(s);   
                    count++;     
                }   
                gpsCount=count;
                System.out.println("搜索到："+count+"颗卫星");
              //输出卫星信息    
                for(int i=0;i<satelliteList.size();i++){   
                    //卫星的方位角，浮点型数据    
                    System.out.println(satelliteList.get(i).getAzimuth());   
                    //卫星的高度，浮点型数据    
                    System.out.println(satelliteList.get(i).getElevation());   
                    //卫星的伪随机噪声码，整形数据    
                    System.out.println(satelliteList.get(i).getPrn());   
                    //卫星的信噪比，浮点型数据    
                    System.out.println(satelliteList.get(i).getSnr());   
                    //卫星是否有年历表，布尔型数据    
                    System.out.println(satelliteList.get(i).hasAlmanac());   
                    //卫星是否有星历表，布尔型数据    
                    System.out.println(satelliteList.get(i).hasEphemeris());   
                    //卫星是否被用于近期的GPS修正计算    
                    System.out.println(satelliteList.get(i).hasAlmanac());   
                }
                break;
            //定位启动
            case GpsStatus.GPS_EVENT_STARTED:
                Log.i(TAG, "定位启动");
                break;
            //定位结束
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.i(TAG, "定位结束");
                break;
            }
        };
    };
    
    
 // 与服务交互
 	class GPSGetTask extends AsyncTask<Void, Void, Boolean> {
 		Location location;

 		public GPSGetTask(Location _location) {
 			location=_location;
 		}

 	

 		@Override
 		protected Boolean doInBackground(Void... params) {
 			try {		
 				
// 				String url1 =  currentUrl+ "?user=UE000118&x=111111&y=1111&accuracy=1111";
//	            HttpUtils.httpClientExcuteGet(url1);
	            
				double lon=location.getLongitude();
	        	double lat=location.getLatitude();
	        	double acc=location.getAccuracy();
	        	
	        	// 使用GET方法发送请求,需要把参数加在URL后面，用？连接，参数之间用&分隔
	            String url =  currentUrl+ "?user="+currentUser+"&x=" + lon+"&y="+lat+"&accuracy="+acc;
	            String msg="";

 				HttpResponse response = HttpUtils.httpClientExcuteGet(url);
 				if (response.getStatusLine().getStatusCode() == 200) {
 					String entiryString = EntityUtils.toString(response
 							.getEntity());
 					JSONObject backJson = new JSONObject(entiryString);
 					boolean succeed = backJson.getBoolean("succeed");
 					if (succeed) {
 						return true;
 					} else {
 						msg = backJson.getString("msg");
 						Log.i("tag", msg);
 					}
 				}
 				
 				
 			} catch (Exception e) {
 				e.printStackTrace();

 			}
 			return false;
 		}

 		@Override
 		protected void onPostExecute(Boolean result) {
 			
 		}
 	}
    
    /**
     * 实时更新文本内容
     * 
     * @param location
     */
    private void updateView(Location location){
        if(location!=null){
//        	editText.setText("设备位置信息\n\n经度：");
//        	editText.append(String.valueOf(location.getLongitude()));
//        	editText.append("\n纬度：");
//        	editText.append(String.valueOf(location.getLatitude()));
//        	if(gpsCount>18)
//        	{
	        	double lon=location.getLongitude();
	        	double lat=location.getLatitude();
	        	
	
	        	// 使用GET方法发送请求,需要把参数加在URL后面，用？连接，参数之间用&分隔
	            String url =  currentUrl+ "?user="+currentUser+"&x=" + lon+"&y="+lat+"&accuracy=34";
	
	            // 生成请求对象
	            HttpGet httpGet = new HttpGet(url);
	            HttpClient httpClient = new DefaultHttpClient();
	
	            // 发送请求
	            try
	            {
	
	                HttpResponse response = httpClient.execute(httpGet);
	
	                // 显示响应
	                //showResponseResult(response);// 一个私有方法，将响应结果显示出来
	
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	            }
//        	}
        	
        }else{
            //清空EditText对象
//        	editText.getEditableText().clear();
        }
    }
    
    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
        
    }
	
}
