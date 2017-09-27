package com.chinadci.mel.mleo.ui.popups;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.ui.views.PhotoSetView;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.PopupWindow;

/**
 * 
 * @ClassName PhotoSetDialog
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:37:24
 * 
 */
public class PhotoSetDialog extends PopupWindow {
	public static final int PHOTO_FROM_CAMERA = 0x000000;
	public static final int PHOTO_FROM_FILE = 0x000001;

	Context context;
	PhotoSetView contentView;
	View centView;
	View parentView;
	View animView;
	Animation showAnim, dismAnim;
	IClickListener iClickListener;

	public void setIClickLitener(IClickListener listener) {
		iClickListener = listener;
	}

	public PhotoSetDialog(Context cxt, int width, int height) {
		super(width, height);
		context = cxt;
		contentView = new PhotoSetView(cxt);
		animView = contentView.getAnimView();
		setContentView(contentView);
		setAnimationStyle(R.style.fade_animation);
	}

	public void setClickListener(IClickListener listener) {
		contentView.setIClickListener(listener);
	}

	public void setShowAnim(int resId) {
		// showAnim = AnimationUtils.loadAnimation(context, resId);
	}

	public void setHideAnim(int resId) {
		
	}

	IClickListener photoClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			// TODO Auto-generated method stub
			int io = (Integer) o;
			if (io == 0 || io == 1) {
				if (iClickListener != null)
					iClickListener.onClick(null, io);
			}
			return null;

		}
	};

	Handler animHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			superDismiss();
		};
	};

	private void superDismiss() {
		try {
			super.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// TODO Auto-generated method stub
		super.showAtLocation(parent, gravity, x, y);
		if (showAnim != null) {
			animView.startAnimation(showAnim);
		}

	}

	Handler hander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				animView.startAnimation(showAnim);
				break;

			case 1:
				animView.startAnimation(dismAnim);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if (dismAnim != null) {
			animView.startAnimation(dismAnim);
		}

		else {
			super.dismiss();
		}
	}
}
