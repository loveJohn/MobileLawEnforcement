package com.chinadci.mel.mleo.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClockView extends LinearLayout {

	private float density;

	private TextView timeViwe;
	private TextView dateView;
	private TextView weatherView;

	@SuppressLint("NewApi")
	public ClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public ClockView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context) {
		density = context.getResources().getDisplayMetrics().density;
		timeViwe = new TextView(context);
		dateView = new TextView(context);
		weatherView = new TextView(context);

		timeViwe.setTextSize(24 * density);
		dateView.setTextSize(12 * density);
		weatherView.setTextSize(8 * density);

		timeViwe.setTextColor(Color.WHITE);
		dateView.setTextColor(Color.WHITE);
		weatherView.setTextColor(Color.WHITE);

		timeViwe.setShadowLayer(3, 3, 3, Color.BLACK);
		dateView.setShadowLayer(2, 2, 2, Color.BLACK);
		weatherView.setShadowLayer(2, 2, 2, Color.BLACK);

		timeViwe.setPadding(4, 4, 4, 4);
		dateView.setPadding(4, 4, 4, 4);
		weatherView.setPadding(4, 4, 4, 4);

		setGravity(Gravity.RIGHT);
		setOrientation(LinearLayout.VERTICAL);
		addView(timeViwe, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		addView(dateView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		addView(weatherView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
	}
}
