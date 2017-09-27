package com.chinadci.mel.android.ui.views;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
/**
 * 
* @ClassName GravityCenterToast 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:30:00 
*
 */
public class GravityCenterToast extends Toast {

	public GravityCenterToast(Context context) {
		super(context);
		setGravity(Gravity.CENTER, 0, 0);
		// TODO Auto-generated constructor stub
	}
	
}
