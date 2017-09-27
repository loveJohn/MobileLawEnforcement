package com.chinadci.mel.mleo.ui.views;

import java.io.FileNotFoundException;

import com.chinadci.mel.android.core.interfaces.IClickListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 
 * @ClassName ImageBox
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:58
 * 
 */
public class ImageBox extends RelativeLayout {

	View rootView;
	ImageView imgView;
	ImageButton delView;
	RelativeLayout.LayoutParams delParams;
	RelativeLayout.LayoutParams imgParams;
	String imgPath;
	Bitmap bitmap;

	IClickListener clickListener;

	public ImageBox(Context context, String path, int layoutRID,
			int imageLayoutRID, int deleteLayoutRID) {
		super(context);
		try {
			this.imgPath = path;
			this.bitmap = decodeUri(this.imgPath);
			rootView = LayoutInflater.from(context).inflate(layoutRID, null);
			rootView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			addView(rootView);

			delView = (ImageButton) rootView.findViewById(deleteLayoutRID);
			imgView = (ImageView) rootView.findViewById(imageLayoutRID);
			imgView.setImageBitmap(bitmap);

			delView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (clickListener != null)
						clickListener.onClick(ImageBox.this, imgPath);
				}
			});
			setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (clickListener != null)
						clickListener.onClick(null, imgPath);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setClickListener(IClickListener listener) {
		clickListener = listener;
	}

	public void setDeleteVisiblity(int v) {
		delView.setVisibility(v);
	}

	private Bitmap decodeUri(String path) throws FileNotFoundException {
		int w = getContext().getResources().getDisplayMetrics().widthPixels / 2;
		int h = w * 9 / 16;
		Bitmap bmp = BitmapFactory.decodeFile(imgPath);
		Bitmap fd = Bitmap.createScaledBitmap(bmp, w, h, false);
		return fd;
	}
}
