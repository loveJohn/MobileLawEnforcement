package com.chinadci.mel.mleo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.chinadci.mel.R;


public class ToolMineralNewLog extends ToolFragment implements OnClickListener {
	View rootView;
	ImageButton button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_tool_newminerallog, null);
		button = (ImageButton) rootView
				.findViewById(R.id.fragment_tool_newcase_button);
		button.setOnClickListener(this);
		return rootView;
	}

	public void onClick(View v) {
		Bundle bundle = new Bundle();
		bundle.putString("LOGID", "");
		activityHandle.replaceTitle("编辑巡查日志");
		activityHandle.replaceContentFragment(new MineralLogFragment(), bundle,
				R.anim.slide_in_right, R.anim.slide_out_left);
		activityHandle.replaceToolFragment(new ToolSaveSend(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
	}
}
