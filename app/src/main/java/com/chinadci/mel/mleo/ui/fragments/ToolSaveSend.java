package com.chinadci.mel.mleo.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.chinadci.mel.R;

/**
 * 
* @ClassName ToolSaveSend 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:45 
*
 */
public class ToolSaveSend extends ToolTowButton {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLeftButtonText() {
		leftButton.setText(R.string.save);
	}

	@Override
	protected void setRightButtonText() {
		rightButton.setText(R.string.send);
	}

	@Override
	protected void leftButtonClick(View v) {
		activityHandle.contentFragmentHandle(0);
	}

	@Override
	protected void rightButtonClick(View v) {
		activityHandle.contentFragmentHandle(1);
	}
}
