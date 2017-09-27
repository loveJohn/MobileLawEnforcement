package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinadci.mel.R;

/**
 * 
 * @ClassName AttriAdapter2
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:37
 * 
 */
public class AttriAdapter2 extends BaseAdapter {

	ArrayList<String> keys;
	ArrayList<String> values;
	Context context;
	AdapterHolder holder;

	public AttriAdapter2(Context c, ArrayList<String> k, ArrayList<String> v) {
		this.context = c;
		this.keys = k;
		this.values = v;
	}

	public int getCount() {
		if (keys != null && keys.size() > 0)
			return keys.size();
		return 0;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (keys != null && keys.size() > position && values != null
				&& values.size() > position) {
			String key = keys.get(position);
			String value = values.get(position);
			if (key != null && !key.equals("")) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.adapter_attri, null);
				holder = new AdapterHolder();
				holder.keyView = (TextView) convertView
						.findViewById(R.id.adapter_attri_key);
				holder.valueView = (TextView) convertView
						.findViewById(R.id.adapter_attri_value);
				holder.keyView.setText(key);
				if (value != null)
					holder.valueView.setText(value);
			}
		}
		return convertView;
	}

	class AdapterHolder {
		private TextView keyView;
		private TextView valueView;
	}
}