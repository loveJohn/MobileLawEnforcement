package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import android.content.Intent;
import android.view.View;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.fragments.ToolOneButton;

public class ToolSearch extends ToolOneButton {
	@Override
	protected void setButtonText() {
		button.setText("查找");
	}
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), ModuleRealizeActivity.class);
		intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21021);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
}
