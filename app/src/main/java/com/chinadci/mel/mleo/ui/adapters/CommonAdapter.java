package com.chinadci.mel.mleo.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;
	protected int viewTypeCount;
	protected ItemViewTypeCallback itemViewTypeCallback;
	protected ItemViewTypeCountCallback itemViewTypeCountCallback;

	public interface ItemViewTypeCallback {
		int getViewType(int position);
	}

	public interface ItemViewTypeCountCallback {
		int getViewTypeCount();
	}

	public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}

	public int getCount() {
		return mDatas.size();
	}

	public T getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView,
				parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();
	}

	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
				position);
	}

	public abstract void convert(ViewHolder holder, T item, int position);

	public void updateData(List<T> datas) {
		this.mDatas = datas;
		// this.notifyDataSetChanged();
		this.notifyDataSetInvalidated();
	}

	@Override
	public int getItemViewType(int position) {
		if (null != itemViewTypeCallback) {
			return itemViewTypeCallback.getViewType(position);
		} else {
			return super.getItemViewType(position);
		}

	}

	@Override
	public int getViewTypeCount() {
		if (null != itemViewTypeCallback) {
			return itemViewTypeCountCallback.getViewTypeCount();
		} else {
			return super.getViewTypeCount();
		}

	}

	public void setItemViewTypeCallback(
			ItemViewTypeCallback itemViewTypeCallback) {
		this.itemViewTypeCallback = itemViewTypeCallback;
	}

	public void setItemViewTypeCountCallback(
			ItemViewTypeCountCallback itemViewTypeCountCallback) {
		this.itemViewTypeCountCallback = itemViewTypeCountCallback;
	}

}
