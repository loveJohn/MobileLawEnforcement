package com.chinadci.mel.mleo.ui.fragments;

import android.view.View;

import com.chinadci.mel.R;

public class ToolLandPatrol extends ToolO2Button {
	@Override
	protected void setLeftButtonText() {
		leftButton.setText(R.string.save);
	}

	@Override
	protected void setRightButtonText() {
		rightButton.setText(R.string.send);
	}

	@Override
	protected void setOtherButtonText() {
		otherButton.setText(R.string.response);
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
		activityHandle.contentFragmentHandle(2);
	}
}
