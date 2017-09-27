package com.chinadci.mel.mleo.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.Parameters;
/**
 * 
* @ClassName ToolLandNewCase 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:26 
*
 */
public class ToolLandNewCase extends ToolOneButton {

	@Override
	protected void setButtonText() {
		button.setText(R.string.cn_new);
	}

	public void onClick(View v) {
		Bundle bundle = new Bundle();
		bundle.putString(Parameters.CASE_ID, "");
		activityHandle.replaceTitle(getString(R.string.cn_info_edit));
		activityHandle.replaceContentFragment(new LandCaseEditeFragment(),
				bundle, R.anim.slide_in_right, R.anim.slide_out_left);
		activityHandle.replaceToolFragment(new ToolSaveSend(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
	}
}
