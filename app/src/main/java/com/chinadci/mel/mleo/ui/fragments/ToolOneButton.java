package com.chinadci.mel.mleo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.chinadci.mel.R;

/**
 * 
* @ClassName ToolOneButton 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:42 
*
 */
public class ToolOneButton extends ToolFragment implements OnClickListener {
	View rootView;
	protected Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_tool_onebutton, null);
		button = (Button) rootView
				.findViewById(R.id.fragment_tool_onebutton_button);
		setButtonText();
		button.setOnClickListener(this);
		return rootView;
	}

	protected void setButtonText() {

	}

	public void onClick(View v) {

	}
}
