package com.chinadci.mel.android.ui.views;

import java.util.ArrayList;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import com.chinadci.mel.mleo.ui.adapters.ViewAdapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 
 * @ClassName DropDownSpinner
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:29:57
 * 
 */
public class DropDownSpinner extends TextView {
	PopupWindow popup;
	ListView listView;
	Context context;
	ArrayList<KeyValue> kvs;
	ArrayList<View> views;
	ISelectedChanged selectedChangedListener;
	int selIndex = -1;
	ViewAdapter listAdapter;
	int popupHeight;
	int spacing;

	public void setSelectedItem(int i) {
		if (kvs != null && kvs.size() > i) {
			selIndex = i;
			setText(kvs.get(i).getValue().toString());
			if (selectedChangedListener != null)
				selectedChangedListener.onSelectedChanged(DropDownSpinner.this,
						kvs.get(i).getKey());
		}
	}

	public void setSelectedItem(Object k) {
		if (kvs != null && kvs.size() > 0)
			for (int i = 0; i < kvs.size(); i++) {
				if (kvs.get(i).getKey().equals(k)) {
					selIndex = i;
					setText(kvs.get(i).getValue().toString());
					if (selectedChangedListener != null)
						selectedChangedListener.onSelectedChanged(
								DropDownSpinner.this, kvs.get(i).getKey());
					break;
				}
			}
	}

	public Object getSelectedItem() {
		if (kvs != null && selIndex > -1 && kvs.size() > selIndex)
			return kvs.get(selIndex);
		else
			return null;
	}

	public Object getSelectedKey() {
		if (kvs != null && selIndex > -1 && kvs.size() > selIndex)
			return kvs.get(selIndex).getKey();
		else
			return null;
	}

	public Object getSelectedValue() {
		if (kvs != null && selIndex > -1 && kvs.size() > selIndex)
			return kvs.get(selIndex).getValue();
		else
			return null;
	}

	public DropDownSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public DropDownSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public DropDownSpinner(Context context) {
		super(context);
		initView(context);

		// TODO Auto-generated constructor stub
	}

	public void setSelectedChangedListener(ISelectedChanged listener) {
		selectedChangedListener = listener;
	}

	public void setData(ArrayList<KeyValue> data) {

		kvs = data;
		if (views.size() > 0) {
			views.clear();
		}

		if (kvs != null && kvs.size() > 0) {
			for (int i = 0; i < kvs.size(); i++) {
				TextView view = new TextView(context);
				view.setPadding(spacing, spacing / 2, spacing, spacing / 2);
				view.setTextColor(getResources().getColor(
						R.color.dpspinner_txtcolor));
				view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view.setText(kvs.get(i).getValue().toString());
				view.setTag(kvs.get(i).getKey());
				views.add(view);
			}
			listAdapter = new ViewAdapter(context, views);
			listView.setAdapter(listAdapter);
			listAdapter.notifyDataSetChanged();
		}
		setSelectedItem(0);
	}

	private void initView(Context c) {
		context = c;
		setEllipsize(TruncateAt.END);
		setOnClickListener(clickListener);
		popupHeight = (int) (120 * context.getResources().getDisplayMetrics().density);
		spacing = (int) (8 * context.getResources().getDisplayMetrics().density);

		views = new ArrayList<View>();
		listAdapter = new ViewAdapter(context, views);
		listView = new ListView(context);
		listView.setAdapter(listAdapter);

		listView.setBackgroundResource(R.drawable.bg_autotextview);
		listView.setDividerHeight(1);
		listView.setDivider(context.getResources().getDrawable(
				R.color.darkgreen));

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> p, View v, int i, long l) {

				if (kvs != null && kvs.size() > i) {
					String txtString = kvs.get(i).getValue().toString();
					setText(txtString);
					if (selectedChangedListener != null)
						selectedChangedListener.onSelectedChanged(
								DropDownSpinner.this, v.getTag().toString());
				}
				popup.dismiss();
			}
		});
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			showPopup();
		}
	};

	void showPopup() {

		popup = new PopupWindow(listView, getMeasuredWidth(), popupHeight);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.setOutsideTouchable(true);
		popup.setFocusable(true);

		if (popup != null) {
			if (!popup.isShowing()) {
				popup.showAsDropDown(this);
			} else {
				popup.dismiss();
			}
		}
	}
}
