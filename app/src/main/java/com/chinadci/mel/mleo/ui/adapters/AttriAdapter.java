package com.chinadci.mel.mleo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinadci.mel.R;

/**
 * 
* @ClassName AttriAdapter 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:32:33 
*
 */
public class AttriAdapter extends BaseAdapter {
	String keys[];
	String values[];
	Context context;
	AdapterHolder holder;

	public AttriAdapter(Context c, String k[], String v[]) {
		this.context = c;
		this.keys = k;
		this.values = v;
	}

	public int getCount() {
		if (keys != null && keys.length > 0)
			return keys.length;
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
		if (keys != null && keys.length > position && values != null
				&& values.length > position) {
			String key = keys[position];
			String value = values[position];
			if (key != null && !key.equals("")) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.adapter_attri, null);
				holder = new AdapterHolder();
				holder.keyView = (TextView) convertView
						.findViewById(R.id.adapter_attri_key);
				holder.valueView = (TextView) convertView
						.findViewById(R.id.adapter_attri_value);
				holder.keyView.setText(key);
				if (value != null && !value.equals(""))
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