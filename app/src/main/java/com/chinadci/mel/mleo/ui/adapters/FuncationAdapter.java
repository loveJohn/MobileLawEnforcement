package com.chinadci.mel.mleo.ui.adapters;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinadci.mel.mleo.core.Funcation;

public class FuncationAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	AdapterHolder holder;
	HashMap<Integer, Funcation> funcationMap;
	int ids[];
	int layoutId;
	int iconId;
	int labelId;

	public FuncationAdapter(Context context, int funcationIds[],
			HashMap<Integer, Funcation> funcationMap, int layoutRes,
			int iconLayoutRes, int labelLayoutRes) {
		this.context = context;
		this.ids = funcationIds;
		this.funcationMap = funcationMap;
		this.layoutId = layoutRes;
		this.iconId = iconLayoutRes;
		this.labelId = labelLayoutRes;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return (ids != null && ids.length > 0) ? ids.length : 0;
	}

	public Object getItem(int i) {
		return (ids != null && ids.length > i) ? ids[i] : null;
	}

	public long getItemId(int i) {
		return (ids != null && ids.length > i) ? i : -1;

	}

	public View getView(int position, View view, ViewGroup pview) {
		if (ids != null && ids.length > position) {
			try {
				if (view == null) {
					view = inflater.inflate(layoutId, null);
				}
				holder = new AdapterHolder();
				holder.iconView = (ImageView) view.findViewById(iconId);
				holder.labelView = (TextView) view.findViewById(labelId);
				Funcation funcation = funcationMap.get(ids[position]);
				if (funcation != null) {
					holder.iconView.setImageResource(funcation.getIconRes());
					holder.labelView.setText(funcation.getLabelRes());
				}
				view.setTag(funcation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		view.setLayoutParams(new GridView.LayoutParams((int) (88 * context
				.getResources().getDisplayMetrics().density),
				(int) (88* context.getResources().getDisplayMetrics().density)));
		return view;
	}

	final class AdapterHolder {
		private ImageView iconView;
		private TextView labelView;
	}
}
