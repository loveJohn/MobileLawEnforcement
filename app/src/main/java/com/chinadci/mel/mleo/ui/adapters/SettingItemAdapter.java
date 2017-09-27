package com.chinadci.mel.mleo.ui.adapters;

import java.util.ArrayList;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.SettingItemInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @ClassName SettingItemAdapter
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:33:01
 * 
 */
public class SettingItemAdapter extends BaseAdapter {
	ArrayList<SettingItemInfo> items;
	private Context context;
	private LayoutInflater inflater;
	private SettingItemAdapterHolder holder;

	/**
	 * 
	 * @param cxt
	 * @param kvs
	 */
	public SettingItemAdapter(Context cxt, ArrayList<SettingItemInfo> its) {
		context = cxt;
		items = its;
		inflater = LayoutInflater.from(context);
	}

	public Boolean insertItem(int position, SettingItemInfo item) {
		if (items != null && items.size() >= position) {
			items.add(position, item);
			return true;
		}
		return false;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		if (items != null && items.size() > 0)
			return items.size();
		else
			return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (items == null || items.size() < position)
			return null;

		SettingItemInfo itemInfo = items.get(position);
		try {

			convertView = inflater.inflate(R.layout.adapter_setting_item, null);
			holder = new SettingItemAdapterHolder();
			holder.titleView = (TextView) convertView
					.findViewById(R.id.adapte_setting_item_title);
			holder.lableView = (TextView) convertView
					.findViewById(R.id.adapte_setting_item_lable);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.adapte_setting_item_image);
			holder.nextView = (ImageView) convertView
					.findViewById(R.id.adapte_setting_item_next);
			convertView.setTag(holder);

			if (itemInfo.getTitle() != null) {
				holder.titleView.setText(itemInfo.getTitle());
				holder.titleView.setVisibility(View.VISIBLE);
			}

			if (itemInfo.getLable() != null) {
				holder.lableView.setText(itemInfo.getLable());
				holder.lableView.setVisibility(View.VISIBLE);
			}

			if (itemInfo.getImage() != null) {
				int padding = context.getResources().getDimensionPixelSize(
						R.dimen.spacing2h);
				// int radius = context.getResources().getDimensionPixelSize(
				// R.dimen.spacing2h);
				// Bitmap bitmap = DrawableResFactory.toRoundCorner(
				// itemInfo.getImage(), radius);

				holder.imageView
						.setBackgroundResource(R.drawable.bg_user_photo);
				holder.imageView.setPadding(padding, padding, padding, padding);

				holder.imageView.setImageBitmap(itemInfo.getImage());
				holder.imageView.setVisibility(View.VISIBLE);
			}

			if (itemInfo.getNextIco() != null) {
				holder.nextView.setImageBitmap(itemInfo.getNextIco());
				holder.nextView.setVisibility(View.VISIBLE);
			}

			return convertView;

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	private final class SettingItemAdapterHolder {
		private TextView titleView;
		private TextView lableView;
		private ImageView imageView;
		private ImageView nextView;
	}

}
