package com.chinadci.mel.mleo.core;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @ClassName TimeFormatFactory
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:30:34
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimeFormatFactory2 {

	static SimpleDateFormat idDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	static SimpleDateFormat tDateFormatS = new SimpleDateFormat(
			"yyyy年MM月dd日HH时mm分ss秒");
	static SimpleDateFormat tDateFormatM = new SimpleDateFormat(
			"yyyy年MM月dd日HH时mm分");
	static SimpleDateFormat tDateFormatH = new SimpleDateFormat(
			"yyyy年MM月dd日HH时");
	static SimpleDateFormat tDateFormatD = new SimpleDateFormat("yyyy年MM月dd日");
	static SimpleDateFormat sDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat sDateFormatS = new SimpleDateFormat("yyyy-MM-dd");

	public static String getIdFormatTime(Date t) {
		try {
			return idDateFormat.format(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getIdFormatTimeS(Date t) {
		try {
			return sDateFormatS.format(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSourceTime(Date t) {
		try {
			return sDateFormat.format(t);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static String getDisplayTimeS(String ts) {
		try {
			Date time = sDateFormat.parse(ts);
			return tDateFormatS.format(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getDisplayTimeM(String ts) {
		try {
			Date time = sDateFormat.parse(ts);
			return tDateFormatM.format(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDisplayTimeH(String ts) {
		try {
			Date time = sDateFormat.parse(ts);
			return tDateFormatH.format(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param ts
	 * @return
	 */
	public static String getDisplayTimeD(String ts) {
		try {
			Date time = sDateFormat.parse(ts);
			return tDateFormatD.format(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getDisplayTimeS(Date t) {
		try {
			return tDateFormatS.format(t);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	public static String getDisplayTimeM(Date t) {
		try {
			return tDateFormatD.format(t);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	public static String getDisplayTimeH(Date t) {
		try {
			return tDateFormatH.format(t);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public static String getDisplayTimeD(Date t) {
		try {
			return tDateFormatD.format(t);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDisplayTime(Date t, String tFormat) {
		try {
			SimpleDateFormat tdFormat = new SimpleDateFormat(tFormat);
			return tdFormat.format(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDisplayTime(String ts, String sFormat,
			String tFormat) {
		try {
			SimpleDateFormat sdFormat = new SimpleDateFormat(sFormat);
			SimpleDateFormat tdFormat = new SimpleDateFormat(tFormat);

			Date time = sdFormat.parse(ts);
			return tdFormat.format(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getDateFormat(String ts, String tFormat) {
		try {
			SimpleDateFormat tdFormat = new SimpleDateFormat(tFormat);
			Date time = tdFormat.parse(ts);
			return sDateFormat.format(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getDateFormat(Date t) {
		try {
			return sDateFormat.format(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date getDatDate(String ts) {
		try {
			Date time = sDateFormat.parse(ts);
			return time;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
