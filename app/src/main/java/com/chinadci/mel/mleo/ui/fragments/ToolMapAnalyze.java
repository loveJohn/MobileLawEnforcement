package com.chinadci.mel.mleo.ui.fragments;


import android.view.View;
import com.chinadci.mel.R;

/**
 * 
* @ClassName ToolMapAnalyze 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:35 
*
 */
public class ToolMapAnalyze extends ToolOneButton {

	@Override
	protected void setButtonText() {
		// TODO Auto-generated method stub
		button.setText(R.string.conmitanalyze);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		activityHandle.contentFragmentHandle(0);
	}
}
