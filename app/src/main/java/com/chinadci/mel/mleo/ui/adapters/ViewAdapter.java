package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 
* @ClassName ViewAdapter 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:46:20 
*
 */
public class ViewAdapter extends BaseAdapter {

	Context context;
	int selIndex = -1;
	ArrayList<View> views;
	View view;

	public void setItems(ArrayList<View> vs) {
		views = vs;
	}

	public void deleteItem(int p) {
		if (p > -1 & (views != null && p < views.size()))
			views.remove(p);
	}

	public void setSelected(int position) {
		this.selIndex = position;
	}

	public ViewAdapter(Context context, ArrayList<View> vs) {
		views = vs;
		this.context = context;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		if (views != null && views.size() > 0)
			return views.size();
		else
			return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (views != null && views.size() > position)
			return views.get(position);
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//if (convertView == null)
			convertView = views.get(position);
		return convertView;

	}
}
