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
 * @ClassName ToolTowButton
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:47:48
 * 
 */
public class ToolO2Button extends ToolFragment {
	View rootView;
	protected Button otherButton;
	protected Button leftButton;
	protected Button rightButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_tool_o2button, null);
		leftButton = (Button) rootView
				.findViewById(R.id.fragment_tool_o2button_leftbutton);
		rightButton = (Button) rootView
				.findViewById(R.id.fragment_tool_o2button_rightbutton);
		otherButton = (Button) rootView
				.findViewById(R.id.fragment_tool_o2button_obutton);
		leftButton.setOnClickListener(clickListener);
		rightButton.setOnClickListener(clickListener);
		otherButton.setOnClickListener(clickListener);
		setLeftButtonText();
		setRightButtonText();
		setOtherButtonText();
		return rootView;
	}

	private OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			int vid = v.getId();
			switch (vid) {
			case R.id.fragment_tool_o2button_leftbutton:
				leftButtonClick(v);
				break;

			case R.id.fragment_tool_o2button_rightbutton:
				rightButtonClick(v);
				break;

			case R.id.fragment_tool_o2button_obutton:
				otherButtonClick();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 
	 * @Title setLeftButtonText
	 * @Description 设置左侧工具文字
	 */
	protected void setLeftButtonText() {

	}

	/**
	 * 
	 * @Title setRightButtonText
	 * @Description 设置右侧工具文字
	 */
	protected void setRightButtonText() {

	}

	/**
	 * 
	 * @Title setOtherButtonText
	 * @Description 设置附加按钮文字
	 */
	protected void setOtherButtonText() {

	}

	/**
	 * 
	 * @Title leftButtonClick
	 * @Description 左侧按钮点击事件
	 * @param v
	 *            void
	 */
	protected void leftButtonClick(View v) {

	}

	/**
	 * 
	 * @Title rightButtonClick
	 * @Description 右侧按钮点击事件
	 * @param v
	 *            void
	 */
	protected void rightButtonClick(View v) {

	}

	/**
	 * 
	 * @Title otherButtonClick
	 * @Description 附加按钮点击事件
	 */
	protected void otherButtonClick() {

	}
}
