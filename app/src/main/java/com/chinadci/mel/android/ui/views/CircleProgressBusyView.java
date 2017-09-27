package com.chinadci.mel.android.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName CircleProgressBusyView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:29:40
 * 
 */
public class CircleProgressBusyView extends LinearLayout {
	Context context;
	View rootView;
	TextView msgView;

	@SuppressLint("NewApi")
	public CircleProgressBusyView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public CircleProgressBusyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public CircleProgressBusyView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void setMsg(String s) {
		try {
			if (msgView != null)
				msgView.setText(s);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	void initView(Context c) {
		try {
			context = c;
			int popup_appbusy = getResources().getIdentifier(
					context.getPackageName() + ":layout/" + "popup_appbusy",
					null, null);
			int popup_appbusy_msg = getResources().getIdentifier(
					context.getPackageName() + ":id/" + "popup_appbusy_msg",
					null, null);

			rootView = LayoutInflater.from(getContext()).inflate(popup_appbusy,
					null);
			rootView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			addView(rootView, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			msgView = (TextView) rootView.findViewById(popup_appbusy_msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
