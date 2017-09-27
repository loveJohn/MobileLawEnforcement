package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chinadci.mel.R;

/**
 * 
* @ClassName ViewListAdapter 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:33:09 
*
 */
public class ViewListAdapter extends BaseAdapter {

	Context context;
	int selIndex = -1;
	ArrayList<View> views;
	View view;

	public void delItem(int position) {
		if (position > -1 & (views != null && position < views.size()))
			views.remove(position);
	}

	public int getSelectedIndex() {
		return selIndex;
	}

	public void setSelected(int position) {
		this.selIndex = position;
	}

	public ViewListAdapter(Context context, ArrayList<View> vs) {
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
		try {
			if (convertView == null)
				convertView = views.get(position);
			view = convertView;
			if (position == selIndex)
				view.setBackgroundColor(context.getResources().getColor(
						R.color.list_selector));
			else
				view.setBackgroundColor(Color.TRANSPARENT);

			return convertView;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

	}
}
