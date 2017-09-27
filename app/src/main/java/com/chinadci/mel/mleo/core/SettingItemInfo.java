package com.chinadci.mel.mleo.core;

import android.graphics.Bitmap;

/**
 * 
 * @ClassName SettingItemInfo
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:30:21
 * 
 */
public class SettingItemInfo {
	String title;
	String lable;
	Bitmap image = null;
	Bitmap nextIco = null;

	public SettingItemInfo(String t, String l, Bitmap i, Bitmap n) {
		title = t;
		lable = l;
		image = i;
		nextIco = n;
	}

	public String getTitle() {
		return title;

	}

	public String getLable() {
		return lable;

	}

	public Bitmap getImage() {
		return image;
	}

	public Bitmap getNextIco() {
		return nextIco;

	}

}
