package com.chinadci.mel.mleo.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {
	public static String nullToString(Object obj) {
		return obj == null || obj.equals("null") ? "" : obj.toString();
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());//

	/**
	 * 获取当前时间的字符串表示
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTimeStr() {
		return sdf.format(new Date());
	}

	/**
	 * 时间字符串变秒数，字符串非法时返回-1
	 * @param str
	 *            时间字符串 yyyy-MM-dd HH:mm:ss
	 * @return 时间秒数
	 */
	public static long getTimeMillis(String str) {
		try {
			Date date = sdf.parse(str);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
