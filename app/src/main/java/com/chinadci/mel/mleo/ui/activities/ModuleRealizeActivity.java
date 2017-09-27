package com.chinadci.mel.mleo.ui.activities;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.utils.SMSReceiverHelper;

import android.os.Bundle;

/**
 * 
* @ClassName ModuleRealizeActivity 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:38:54 
*
 */
public class ModuleRealizeActivity extends ModuleActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module);
		toolLayoutId = R.id.activity_module_toolbar;
		contentLayoutId = R.id.activity_module_content;
		backLayoutId = R.id.activity_module_backbutton;
		titleLayoutId = R.id.activity_module_title;
		currentUser = SharedPreferencesUtils.getInstance(this,
				R.string.shared_preferences).getSharedPreferences(
				R.string.sp_actuser, "");
		initActivity();

		try {
			String smsHandle = getIntent().getStringExtra(
					SMSReceiverHelper.SMS_HANDLE);
			contentFragment.handle(smsHandle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

}
