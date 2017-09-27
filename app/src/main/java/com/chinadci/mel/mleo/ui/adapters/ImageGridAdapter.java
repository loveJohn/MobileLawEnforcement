package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.ui.views.ImageBox;

/**
 * 
 * @ClassName ImageGridAdapter
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:48
 * 
 */
public class ImageGridAdapter extends AnnexAdapter {
	int selIndex = -1;
	int deleteVisibility = View.VISIBLE;
	View view;

	int layoutRID;
	int imageLayoutRID;
	int deleteLayoutRID;

	public ImageGridAdapter(Context context, ArrayList<String> paths,
			IClickListener lis, int layoutRID, int imageLayoutRID,
			int deleteLayoutRID) {
		initAdapter(context, paths, lis, View.VISIBLE, layoutRID,
				imageLayoutRID, deleteLayoutRID);

	}

	public ImageGridAdapter(Context context, ArrayList<String> paths,
			IClickListener lis, int delVisibility, int layoutRID,
			int imageLayoutRID, int deleteLayoutRID) {
		initAdapter(context, paths, lis, delVisibility, layoutRID,
				imageLayoutRID, deleteLayoutRID);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			String path = pathList.get(position);
			ImageBox box = new ImageBox(context, path, layoutRID,
					imageLayoutRID, deleteLayoutRID);
			box.setClickListener(itemClickListener);
			box.setDeleteVisiblity(deleteVisibility);
			convertView = box;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private void initAdapter(Context context, ArrayList<String> paths,
			IClickListener lis, int delVisibility, int layoutRID,
			int imageLayoutRID, int deleteLayoutRID) {
		this.context = context;
		this.pathList = paths;
		this.itemClickListener = lis;
		this.deleteVisibility = delVisibility;
		this.layoutRID = layoutRID;
		this.imageLayoutRID = imageLayoutRID;
		this.deleteLayoutRID = deleteLayoutRID;

		if (this.pathList == null)
			this.pathList = new ArrayList<String>();

	}
}
