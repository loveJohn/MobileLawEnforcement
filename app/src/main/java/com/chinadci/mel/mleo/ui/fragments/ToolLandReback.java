package com.chinadci.mel.mleo.ui.fragments;

import android.view.View;

import com.chinadci.mel.R;


public class ToolLandReback extends ToolOneButton {
	
	public static final int BUTTON_GONE=0x000;		//按钮隐藏
	public static final int APPLY_RETURN=0x001;		//申请退回
	public static final int HANDLE_APPLY=0x002;		//处理申请
	
	private int type=0;
	
	
	@Override
	protected void setButtonText() {
		button.setText(R.string.apply_reback);
	}

	public void onClick(View v) {
		if(type==APPLY_RETURN){
			activityHandle.contentFragmentHandle(3);		//申请退回
		}else if(type==HANDLE_APPLY){
			activityHandle.contentFragmentHandle(4);		//处理申请
		}
	}
	
	
	@Override
	public void setToolButtonType(int i){
		type=i;
		if(type==APPLY_RETURN){
			button.setText(R.string.apply_reback);
		}else if(type==HANDLE_APPLY){
			button.setText(R.string.deal_with_apply);
		}else if(type==BUTTON_GONE){
			button.setVisibility(View.GONE);
		}
	}
}
