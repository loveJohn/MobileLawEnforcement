package com.chinadci.mel.mleo.ui.views;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinadci.mel.android.core.interfaces.IClickListener;

/**
 * 
 * @ClassName AudioBox
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:33:40
 * 
 */
public class AudioBox extends RelativeLayout {
	View rootView;
	TextView lengthView;
	ImageButton delView;
	TextView indexView;

	RelativeLayout.LayoutParams delParams;
	RelativeLayout.LayoutParams imgParams;
	String tapePath;
	Bitmap bitmap;
	IClickListener clickListener;

	public AudioBox(Context context, String path, int layoutRID,
			int indexLayoutRID, int lengthLayoutRID, int deleteLayoutRID) {
		super(context);
		try {
			this.tapePath = path;
			rootView = LayoutInflater.from(context).inflate(layoutRID, null);
			rootView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			addView(rootView);

			delView = (ImageButton) rootView.findViewById(deleteLayoutRID);
			lengthView = (TextView) rootView.findViewById(lengthLayoutRID);
			indexView = (TextView) rootView.findViewById(indexLayoutRID);

			Uri uri = Uri.fromFile(new File(tapePath));
			MediaPlayer mp = MediaPlayer.create(context, uri);
			int l = mp.getDuration() / 1000;
			int h = (l / 3600);
			int m = ((l % 3600) / 60);
			int s = ((l % 3600) % 60);
			String lenString = String.format("%1$02d:%2$02d:%3$02d", h, m, s);
			lengthView.setText(lenString);

			delView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					if (clickListener != null)
						clickListener.onClick(AudioBox.this, tapePath);
				}
			});
			setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					if (clickListener != null)
						clickListener.onClick(null, tapePath);
				}
			});

			Log.i("audio-box", "init-succeed");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("audio-box", e.toString());
		}
	}

	public void setClickListener(IClickListener listener) {
		clickListener = listener;
	}

	public void setDeleteVisiblity(int v) {
		delView.setVisibility(v);
	}

	public void setIndex(int i) {
		if (indexView != null)
			indexView.setText(String.valueOf(i));
	}
}
