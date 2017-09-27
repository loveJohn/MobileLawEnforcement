package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.ui.views.AudioBox;

/**
 * 
 * @ClassName AudioGridAdapter
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:40
 * 
 */
public class AudioGridAdapter extends AnnexAdapter {
	int deleteVisibility = View.VISIBLE;

	int layoutRID;
	int indexLayoutRID;
	int lengthLayoutRID;
	int deleteLayoutRID;

	private void initAdapter(Context context, ArrayList<String> paths,
			IClickListener listener, int delVisibility, int layoutRID,
			int indexLayoutRID, int lengthLayoutRID, int deleteLayoutRID) {
		this.context = context;
		this.pathList = paths;
		this.itemClickListener = listener;
		this.deleteVisibility = delVisibility;
		this.layoutRID = layoutRID;
		this.deleteLayoutRID = deleteLayoutRID;
		this.indexLayoutRID = indexLayoutRID;
		this.lengthLayoutRID = lengthLayoutRID;

		if (this.pathList == null)
			this.pathList = new ArrayList<String>();
	}

	public AudioGridAdapter(Context context, ArrayList<String> paths,
			IClickListener listener, int layoutRID, int indexLayoutRID,
			int lengthLayoutRID, int deleteLayoutRID) {
		initAdapter(context, paths, listener, View.VISIBLE, layoutRID,
				indexLayoutRID, lengthLayoutRID, deleteLayoutRID);
	}

	public AudioGridAdapter(Context context, ArrayList<String> paths,
			IClickListener listener, int delVisibility, int layoutRID,
			int indexLayoutRID, int lengthLayoutRID, int deleteLayoutRID) {
		initAdapter(context, paths, listener, delVisibility, layoutRID,
				indexLayoutRID, lengthLayoutRID, deleteLayoutRID);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("audio-grid-adapter", String.valueOf(pathList.size()));
		try {
			if (this.pathList != null && this.pathList.size() > position) {
				String path = this.pathList.get(position);
				AudioBox box = new AudioBox(context, path, layoutRID,
						indexLayoutRID, lengthLayoutRID, deleteLayoutRID);
				box.setIndex(position + 1);
				box.setClickListener(this.itemClickListener);
				box.setDeleteVisiblity(deleteVisibility);
				convertView = box;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("audio-grid-adapter", e.toString());
		}
		return convertView;
	}
}
