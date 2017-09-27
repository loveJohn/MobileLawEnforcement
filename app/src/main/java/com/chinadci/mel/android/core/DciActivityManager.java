package com.chinadci.mel.android.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

/**
 * 
 * @ClassName: DciActivityManager
 * @Description: TODO
 * @author Leix
 * @date 2014-3-19 下午2:01:15
 * 
 */
public class DciActivityManager extends Application implements
		Thread.UncaughtExceptionHandler {
	
	public static double myLocationLng = 0;
	public static double myLocationLat = 0;
	
	private String userCookie = null;
	
	public String getUserCookie() {
		return userCookie;
	}

	public void setUserCookie(String userCookie) {
		this.userCookie = userCookie;
	}

	private List<Activity> mList = new LinkedList<Activity>();
	private static DciActivityManager instance;

	public DciActivityManager() {
	}

	/**
	 * 
	 * @Title: getInstance
	 * @Description: 获取ActivityManager 单例
	 * @return
	 */
	public synchronized static DciActivityManager getInstance() {
		if (null == instance) {
			instance = new DciActivityManager();
		}
		return instance;
	}

	/**
	 * 
	 * @Title: destroyActivityExcept
	 * @Description: 销毁'cls'以外的Activity
	 * @param cls
	 */
	public void destroyActivityExcept(Class<?> cls) {
		destroyActivityExceptMe(cls);
	}

	private void destroyActivityExceptMe(Class<?> cls) {
		for (Activity act : mList) {
			if (!act.getClass().equals(cls)) {
				act.finish();
				act = null;
				mList.remove(act);
			}
		}
	}

	/**
	 * 
	 * @Title: addActivity
	 * @Description: 添加Activity
	 * @param activity
	 * @return
	 */
	public boolean addActivity(Activity activity) {
		return addActivityMe(activity);
	}

	private boolean addActivityMe(Activity activity) {
		try {
			mList.add(activity);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * 
	 * @Title: getActivity
	 * @Description: 获取'cls'类的Activity
	 * @param cls
	 * @return
	 */
	public Activity getActivity(Class<?> cls) {
		return getActivityMe(cls);
	}

	private Activity getActivityMe(Class<?> cls) {
		try {
			for (Activity act : mList) {
				if (act.getClass().equals(cls)) {
					return act;
				}
			}
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 
	 * @Title: destroyActivity
	 * @Description: 销毁'cls'Activity
	 * @param cls
	 */
	public void destroyActivity(Class<?> cls) {
		destroyActivityMe(cls);
	}

	private void destroyActivityMe(Class<?> cls) {
		for (Activity activity : mList) {
			if (activity.getClass().getName().equals(cls.getName())) {
				activity.finish();
				activity = null;
			}
		}
	}

	/**
	 * 
	 * @Title: getActivities
	 * @Description: 获取'cls'类Activity列表
	 * @param cls
	 * @return
	 */
	public ArrayList<Activity> getActivities(Class<?> cls) {
		return getActivitiesMe(cls);
	}

	private ArrayList<Activity> getActivitiesMe(Class<?> cls) {
		try {
			ArrayList<Activity> activities = new ArrayList<Activity>();
			for (Activity act : mList) {
				if (act.getClass().equals(cls)) {
					activities.add(act);
				}
			}
			return activities;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 
	 * @Title: exit
	 * @Description: 退出
	 */
	public void exit() {
		exitMe();
	}

	private void exitMe() {
		try {
			for (Activity activity : mList) {
				if (activity != null) {
					activity.finish();
					activity = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public void uncaughtException(Thread arg0, Throwable arg1) {
		exit();
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
}
