package com.chinadci.mel.mleo.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 
 * @ClassName VibratorUtil
 * @Description 手机震动工具类
 * @author xulei
 * @email leix@geo-k.cn
 * @date 2014年10月15日 上午10:09:57
 * 
 */
public class VibratorUtil {
	/**
	 * 
	 * @Title Vibrate
	 * @Description TODO
	 * @param activity
	 *            调用该方法的Activity实例
	 * @param milliseconds
	 *            震动的时长，单位是毫秒
	 * 
	 */
	public static void Vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	/**
	 * 
	 * @Title Vibrate
	 * @Description TODO
	 * @param activity
	 *            调用该方法的Activity实例
	 * @param pattern
	 *            自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
	 * @param isRepeat
	 *            是否反复震动，如果是true，反复震动，如果是false，只震动一次
	 * 
	 */
	public static void Vibrate(final Activity activity, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

}
