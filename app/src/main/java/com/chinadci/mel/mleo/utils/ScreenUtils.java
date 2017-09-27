package com.chinadci.mel.mleo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 获得屏幕相关的辅助类
 */
public class ScreenUtils {
	private ScreenUtils(){
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获得屏幕高度
	 */
	public static int getScreenWidth(Context context){
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 */
	public static int getScreenHeight(Context context){
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 */
	public static int getStatusHeight(Context context){
		int statusHeight = -1;
		try{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e){
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}
	
	
	 /**
     * 設置View的寬度（像素），
     * 若設置爲自適應則應該傳入MarginLayoutParams.WRAP_CONTENT
     */
    public static void setLayoutWidth(View view,int width)  {  
       /**
        MarginLayoutParams margin=new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(margin.leftMargin,y, margin.rightMargin, y+margin.height);  
        //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);  
        //view.setLayoutParams(layoutParams);
        ViewGroup.MarginLayoutParams  layoutParams =newLayParms(view, margin);
        //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);       
        view.setLayoutParams(layoutParams); 
        view.requestLayout();
        */
    	if (view.getParent() instanceof FrameLayout){
    		FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams) view.getLayoutParams();
    		lp.width=width;
    		view.setLayoutParams(lp);
    		//view.setX(x);
    		view.requestLayout();    		
    	}
    	else if (view.getParent() instanceof RelativeLayout){
    		RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams)view.getLayoutParams();
    		lp.width=width;
    		view.setLayoutParams(lp);
    		//view.setX(x);
    		view.requestLayout();    		
    	}
    	else if (view.getParent() instanceof LinearLayout){
    		LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)view.getLayoutParams();
    		lp.width=width;
    		view.setLayoutParams(lp);
    		//view.setX(x);
    		view.requestLayout();    		    		
    	}
    }  
}
