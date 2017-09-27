package com.chinadci.mel.mleo.utils;

public class TimeUtil {
	
	public static final int FREE_LOAD_TIME=10;		//免登陆时间(小时)
	
	 public static boolean isEarly(int hours, long time) {
		 return (currentTimeMillis() - time) > (hours * 3600 * 1000);
	 }

	 public static boolean isOverDue(int hours,String time){
		 return isEarly(hours,Long.valueOf(time));
	 }
	 
	 public static String currentTimeMillisString(){
		 return String.valueOf(currentTimeMillis());
	 }
	 
	 public static long currentTimeMillis() {
		 return System.currentTimeMillis();
	 }
}
