package com.chinadci.mel.android.ui.activities;

import com.chinadci.mel.android.core.DciActivityManager;

import android.app.Activity;
import android.widget.Toast;
/**
 * 
* @ClassName TwiceBack2ExitActivity 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:29:31 
*
 */
public class TwiceBack2ExitActivity extends Activity {
	private long lastBackPressTime;

	public void onBackPressed() {
		if (this.lastBackPressTime < System.currentTimeMillis() - 3000L) {
			Toast.makeText(this, "再按一次返回键退出", 1).show();
			this.lastBackPressTime = System.currentTimeMillis();
		} else {
			super.onBackPressed();
			DciActivityManager.getInstance().exit();
		}
	}
}
