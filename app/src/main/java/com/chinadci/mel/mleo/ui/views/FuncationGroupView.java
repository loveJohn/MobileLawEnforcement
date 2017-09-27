package com.chinadci.mel.mleo.ui.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class FuncationGroupView extends FrameLayout {
	View rootView;
	GridView gridView;
	TextView titleView;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public FuncationGroupView(Context context, int layoutId, int titleLayoutId,
			int gridLayoutId) {
		super(context);
		initLayout(context, layoutId, titleLayoutId, gridLayoutId);
	}

	private void initLayout(Context context, int layoutId, int titleLayoutId,
			int gridLayoutId) {
		rootView = LayoutInflater.from(context).inflate(layoutId, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		titleView = (TextView) rootView.findViewById(titleLayoutId);
		gridView = (GridView) rootView.findViewById(gridLayoutId);
		addView(rootView);
	}

	public void setTitle(String title) {
		if (titleView != null)
			titleView.setText(title);
	}

	public void setAdapter(ListAdapter adapter) {
		if (gridView != null)
			gridView.setAdapter(adapter);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		if (gridView != null)
			gridView.setOnItemClickListener(listener);
	}
}
