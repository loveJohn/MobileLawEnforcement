package com.chinadci.mel.android.ui.activities;

import com.chinadci.mel.android.core.DciActivityManager;

import android.widget.Toast;

/**
 * 
 * @ClassName TwiceBack2ExitFragmentActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:29:34
 * 
 */
public class TwiceBack2ExitFragmentActivity extends DciFragmentActivity {
	private long lastBackPressTime;

	@Override
	public void onBackPressed() {
		if (this.lastBackPressTime < System.currentTimeMillis() - 3000L) {
			Toast.makeText(this, "再点一次返回键退出", 1).show();
			this.lastBackPressTime = System.currentTimeMillis();
		} else {
			super.onBackPressed();
			DciActivityManager.getInstance().exit();
		}
	}

}
