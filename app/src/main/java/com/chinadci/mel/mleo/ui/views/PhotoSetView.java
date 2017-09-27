package com.chinadci.mel.mleo.ui.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;

/**
 * 
 * @ClassName PhotoSetView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:40
 * 
 */
public class PhotoSetView extends ViewGroup {

	View rootView;
	Button camareView;
	Button pictureView;
	Button cancelView;
	View animView;
	IClickListener cClickListener;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		rootView.layout(l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(widthSpecSize, heightSpecSize);

		View child = getChildAt(0);
		child.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public PhotoSetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		// TODO Auto-generated constructor stub
	}

	public PhotoSetView(Context context) {
		super(context);
		initView();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param listener
	 */
	public void setIClickListener(IClickListener listener) {
		cClickListener = listener;
	}

	/**
	 * 
	 */
	private void initView() {
		rootView = LayoutInflater.from(getContext()).inflate(
				R.layout.view_photoset, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		camareView = (Button) rootView.findViewById(R.id.view_photoset_camera);
		pictureView = (Button) rootView
				.findViewById(R.id.view_photoset_picture);
		cancelView = (Button) rootView.findViewById(R.id.view_photoset_cancel);
		addView(rootView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		animView = (View) rootView.findViewById(R.id.view_photoset_animview);
		camareView.setOnClickListener(clickListener);
		pictureView.setOnClickListener(clickListener);
		cancelView.setOnClickListener(clickListener);
	}

	public View getAnimView() {
		return animView;
	}

	/**
	 * 
	 */
	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = null;
			int vid = v.getId();
			switch (vid) {
			case R.id.view_photoset_camera:
				if (cClickListener != null)
					cClickListener.onClick(v, 0);

				break;

			case R.id.view_photoset_picture:
				if (cClickListener != null)
					cClickListener.onClick(v, 1);

				break;

			default:
				if (cClickListener != null)
					cClickListener.onClick(v, 2);
				break;
			}
		}
	};
}
