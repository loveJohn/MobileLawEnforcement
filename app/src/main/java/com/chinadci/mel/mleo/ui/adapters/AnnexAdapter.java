package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import com.chinadci.mel.android.core.interfaces.IClickListener;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AnnexAdapter extends BaseAdapter {
	protected Context context;
	protected ArrayList<String> pathList;
	protected IClickListener itemClickListener;

	/**
	 * 
	 * @Title getPaths
	 * @Description 获取文件路径列表
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getPaths() {
		return pathList;
	}

	/**
	 * 
	 * @Title deleteItem
	 * @Description 从界面上删除一个对象
	 * @param position
	 */
	public void deleteItem(int position) {
		if (position > -1 & (pathList != null && position < pathList.size()))
			pathList.remove(position);
	}

	/**
	 * 
	 * @Title deleteItem
	 * @Description 从界面上删除一个对象
	 * @param path
	 */
	public void deleteItem(String path) {
		if (pathList != null && pathList.size() > 0)
			for (String p : pathList) {
				if (p.equals(path)) {
					pathList.remove(p);
					break;
				}
			}
	}

	/**
	 * 
	 * @Title addItem
	 * @Description 添加一个附件对象
	 * @param path
	 *            void
	 */
	public void addItem(String path) {
		if (pathList == null)
			pathList = new ArrayList<String>();
		pathList.add(path);
	}

	public int getCount() {
		return (pathList != null) ? pathList.size() : 0;
	}

	public Object getItem(int position) {
		return (position > -1 && pathList != null && pathList.size() > position) ? pathList
				.get(position) : null;
	}

	public long getItemId(int position) {
		return (position > -1 && pathList != null && pathList.size() > position) ? position
				: -1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		return null;
	}
}
