package com.chinadci.mel.mleo.ui.fragments;

import android.view.View;

import com.chinadci.mel.R;

/**
 * 
* @ClassName ToolLandTrackIdSearch 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:32 
*
 */
public class ToolLandTrackIdSearch extends ToolOneButton {

	@Override
	protected void setButtonText() {
		button.setText(R.string.idsearch);
	}

	public void onClick(View v) {
		activityHandle.replaceTitle("案件搜索");
		activityHandle.replaceContentFragment(
				new LandCaseTrackSearchFragment(), null, R.anim.slide_in_right,
				R.anim.slide_out_left);
		activityHandle.replaceToolFragment(new ToolFragment(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
	}
}
