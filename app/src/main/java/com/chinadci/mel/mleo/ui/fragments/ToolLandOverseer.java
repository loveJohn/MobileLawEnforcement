package com.chinadci.mel.mleo.ui.fragments;

import android.view.View;

import com.chinadci.mel.R;

/**
 * 
* @ClassName ToolLandOverseer 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:29 
*
 */
public class ToolLandOverseer extends ToolOneButton {

	@Override
	protected void setButtonText() {
		button.setText(R.string.snapresult);
	}

	public void onClick(View v) {
		activityHandle.contentFragmentHandle(0);
	}

	@Override
	public void handle(Object o) {
		button.setEnabled((Boolean) o);
	}
}
