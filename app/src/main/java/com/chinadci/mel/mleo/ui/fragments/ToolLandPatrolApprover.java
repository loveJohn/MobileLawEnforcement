package com.chinadci.mel.mleo.ui.fragments;

import android.view.View;

public class ToolLandPatrolApprover extends ToolO2Button {
	@Override
	protected void setLeftButtonText() {
		leftButton.setText("退回");
	}

	@Override
	protected void setRightButtonText() {
		rightButton.setText("同意");
	}

	@Override
	protected void setOtherButtonText() {
		otherButton.setVisibility(View.GONE);
	}

	@Override
	protected void leftButtonClick(View v) {
		activityHandle.contentFragmentHandle(0);
	}

	@Override
	protected void rightButtonClick(View v) {
		activityHandle.contentFragmentHandle(1);
	}

	@Override
	protected void otherButtonClick() {
//		activityHandle.contentFragmentHandle(2);
	}
}
