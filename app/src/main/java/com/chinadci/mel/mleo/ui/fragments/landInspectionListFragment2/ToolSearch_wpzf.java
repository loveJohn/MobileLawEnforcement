package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.activities.ScreenOutActivity;
import com.chinadci.mel.mleo.ui.fragments.ToolOneButton;

public class ToolSearch_wpzf extends ToolOneButton {
	
	private int buttonType=0x001;		//默认批量查找
	
	@Override
	public void setToolButtonType(int i){
		buttonType=i;
	}
	
	@Override
	protected void setButtonText() {
		button.setText("查找");
	}
	
	public void onClick(View v) {
		//add teng.guo start
		if(buttonType==TOOL_FILTER){
			//批量查找
			Intent intent = new Intent(getActivity(), ScreenOutActivity.class);
			intent.putExtra(ScreenOutActivity.PATCH, ((ModuleActivity)getActivity()).getPatch());
			intent.putExtra(ScreenOutActivity.AJYEAR, ((ModuleActivity)getActivity()).getAjYear());
			intent.putExtra(ScreenOutActivity.XZQBMCODE, ((ModuleActivity)getActivity()).getXzqbmCode());
			intent.putExtra(ScreenOutActivity.XZQNAME, ((ModuleActivity)getActivity()).getXzqName());
			startActivityForResult(intent,Parameters.GET_FILTER_CONDITION);
			getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}else if(buttonType==TOOL_SEARCH){
			Intent intent = new Intent(getActivity(), ModuleRealizeActivity.class);
			intent.putExtra(ModuleActivity.TAG_MODULE_ID, 210212);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Parameters.GET_FILTER_CONDITION) {
				Bundle bundle = data.getExtras();
				((ModuleActivity)getActivity()).setPatch(bundle.getString(ScreenOutActivity.PATCH));
				((ModuleActivity)getActivity()).setAjYear(bundle.getString(ScreenOutActivity.AJYEAR));
				((ModuleActivity)getActivity()).setXzqbmCode(bundle.getString(ScreenOutActivity.XZQBMCODE));		//拿到所选行政区编号
				((ModuleActivity)getActivity()).setXzqName(bundle.getString(ScreenOutActivity.XZQNAME));	//通过数据库查询所选行政区编号
				Log.i("ydzf", "patch="+bundle.getString(ScreenOutActivity.PATCH)+",year="+(bundle.getString(ScreenOutActivity.AJYEAR))+",xzqbmcode="+bundle.getString(ScreenOutActivity.XZQBMCODE)+",xzqName="+bundle.getString(ScreenOutActivity.XZQNAME));
				((ModuleActivity)getActivity()).refreshUI();
			}
		}
	}
}