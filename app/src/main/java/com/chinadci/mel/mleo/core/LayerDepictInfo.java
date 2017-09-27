package com.chinadci.mel.mleo.core;

import android.content.Context;
import android.graphics.drawable.Drawable;
/**
 * 
* @ClassName LayerDepictInfo 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:30:15 
*
 */
public class LayerDepictInfo {
	String layerName;
	String layerUri;
	String layerLocal;
	Drawable LayerIcon;

	public LayerDepictInfo(String name, String uri, String local, Drawable icon) {
		layerLocal = local;
		layerName = name;
		layerUri = uri;
		LayerIcon = icon;
	}

	public LayerDepictInfo(Context content, int nameId, String uri,
			int localId, int iconId) {
		layerLocal = content.getString(localId);
		layerName = content.getString(nameId);
		layerUri = uri;
		LayerIcon = content.getResources().getDrawable(iconId);
	}

	public LayerDepictInfo(Context content, int nameId, int uriId, int localId,
			int iconId) {
		layerLocal = content.getString(localId);
		layerName = content.getString(nameId);
		layerUri = content.getString(uriId);
		LayerIcon = content.getResources().getDrawable(iconId);
	}

	public String getName() {
		return layerName;
	}

	public String getLocal() {
		return layerLocal;
	}

	public String getUri() {
		return layerUri;
	}

	public Drawable getIcon() {
		return LayerIcon;
	}

}
