package com.chinadci.mel.mleo.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinadci.android.utils.DrawableUtils;


/**
 * 
 * @ClassName UserView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月8日 上午9:23:18
 * 
 */
public class UserView extends FrameLayout {

	int layoutRId;
	int photoRId;
	int nameRId;
	int roleRId;
	int advRId;
	int advAnimRId;
	int signRId;
	int defPhotoRId;

	View rootView;
	View adView;
	ImageView photoView;
	TextView nameView;
	TextView roleView;
	TextView signView;
	Animation adAnim;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public UserView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
	}

	public UserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public void setPhoto(Drawable photo) {
		int photoWidth = (int) (80 * getResources().getDisplayMetrics().density);
		Drawable photoDrawable = DrawableUtils.zoomDrawable(photo, photoWidth,
				photoWidth);
		Bitmap photoBitmap = DrawableUtils.drawableToBitmap(photoDrawable);
		Bitmap cphotoBitmap = DrawableUtils.getRoundedCornerBitmap(photoBitmap,
				photoWidth / 2);
		photoView.setImageBitmap(cphotoBitmap);
	}

	public void setPhoto(Bitmap bitmap) {
		int photoWidth = (int) (80 * getResources().getDisplayMetrics().density);
		Bitmap sbitmap = DrawableUtils.zoomBitmap(bitmap, photoWidth,
				photoWidth);
		Bitmap cphotoBitmap = DrawableUtils.getRoundedCornerBitmap(sbitmap,
				photoWidth / 2);
		photoView.setImageBitmap(cphotoBitmap);
	}

	public void setUserName(String name) {
		nameView.setText(name);
	}

	public void setRoleName(String name) {
		roleView.setText(name);
	}

	public void setSignTime(String stime) {
		signView.setText(stime);
	}

	public void setOnClickListener(OnClickListener listener) {
		if (this.photoView != null)
			this.photoView.setOnClickListener(listener);
	}

	private void initView(Context context, AttributeSet attrs) {
		String layoutRIdText = attrs.getAttributeValue(null, "layoutRID");
		String photoRIdText = attrs.getAttributeValue(null, "photoRID");
		String nameRIdText = attrs.getAttributeValue(null, "nameRID");
		String roleRIdText = attrs.getAttributeValue(null, "roleRID");
		String signRIdText = attrs.getAttributeValue(null, "signRID");
		String advRIdText = attrs.getAttributeValue(null, "advRID");
		String advAnimRIdText = attrs.getAttributeValue(null, "advAnimRID");
		String defPhotoRIdText = attrs.getAttributeValue(null, "defPhotoRID");

		layoutRId = context.getResources().getIdentifier(
				context.getPackageName() + layoutRIdText, null, null);
		photoRId = context.getResources().getIdentifier(
				context.getPackageName() + photoRIdText, null, null);
		nameRId = context.getResources().getIdentifier(
				context.getPackageName() + nameRIdText, null, null);
		roleRId = context.getResources().getIdentifier(
				context.getPackageName() + roleRIdText, null, null);
		signRId = context.getResources().getIdentifier(
				context.getPackageName() + signRIdText, null, null);
		advRId = context.getResources().getIdentifier(
				context.getPackageName() + advRIdText, null, null);
		advAnimRId = context.getResources().getIdentifier(
				context.getPackageName() + advAnimRIdText, null, null);
		defPhotoRId = context.getResources().getIdentifier(
				context.getPackageName() + defPhotoRIdText, null, null);

		rootView = LayoutInflater.from(context).inflate(layoutRId, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);

		photoView = (ImageView) rootView.findViewById(photoRId);
		nameView = (TextView) rootView.findViewById(nameRId);
		roleView = (TextView) rootView.findViewById(roleRId);
		signView = (TextView) rootView.findViewById(signRId);
		adView = (ImageView) rootView.findViewById(advRId);
		setPhoto(getResources().getDrawable(defPhotoRId));
		adAnim = AnimationUtils.loadAnimation(getContext(), advAnimRId);
		long sdur = getResources().getDisplayMetrics().widthPixels / 24;
		adAnim.setDuration(sdur * 500);
		adView.startAnimation(adAnim);
	}
}
