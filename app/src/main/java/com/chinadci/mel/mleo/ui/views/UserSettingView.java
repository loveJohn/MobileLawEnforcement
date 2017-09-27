package com.chinadci.mel.mleo.ui.views;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.core.SettingItemInfo;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.UserFields;
import com.chinadci.mel.mleo.ui.activities.LoginActivity;
import com.chinadci.mel.mleo.ui.adapters.SettingItemAdapter;
import com.chinadci.mel.mleo.ui.popups.PhotoSetDialog;
/**
 * 
* @ClassName UserSettingView 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:36:18 
*
 */
public class UserSettingView extends LinearLayout {
	Context context;
	View rootView;
	Button logoffButton;
	ListView alertListView;
	ListView baseListView;
	SettingItemAdapter alertAdapter;
	SettingItemAdapter baseAdapter;
	IClickListener iClickListener;
	String currentUser;

	public UserSettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public UserSettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public UserSettingView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void setUser(String userName) {
		currentUser = userName;
		initUserInfo();
	}

	public void setIClickListener(IClickListener listener) {
		iClickListener = listener;
	}

	void initView(Context c) {
		context = c;
		rootView = LayoutInflater.from(getContext()).inflate(
				R.layout.fragment_user, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);

		alertListView = (ListView) rootView
				.findViewById(R.id.fragment_user_alterlist);
		baseListView = (ListView) rootView
				.findViewById(R.id.fragment_user_baselist);
		logoffButton = (Button) rootView
				.findViewById(R.id.fragment_user_logoff);
		logoffButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				logOff();
			}
		});

		alertListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> p, View v, int i, long l) {
				// TODO Auto-generated method stub
				if (i == 0) {
					settingPhoto();
				} else if (i == 1) {
					setPassword();
				}
			}
		});
		initUserInfo();
	}

	void initUserInfo() {
		try {
			String table = context.getString(R.string.tb_users);
			String columns[] = new String[] { UserFields.name,
					UserFields.chName, UserFields.photo, UserFields.sex,
					UserFields.role };
			String args[] = new String[] { currentUser };
			String where = new StringBuffer(UserFields.name).append("=?")
					.toString();
			ContentValues userValues = DBHelper.getDbHelper(getContext())
					.doQuery(table, columns, where, args);
			if (userValues != null) {
				Drawable nextDrawable = getResources().getDrawable(
						R.mipmap.ic_setting_next);
				BitmapDrawable bitmapDrawableNext = (BitmapDrawable) nextDrawable;
				Bitmap nextIco = bitmapDrawableNext.getBitmap();

				Bitmap photo = null;
				byte photobyte[] = userValues.getAsByteArray(UserFields.photo);
				if (photobyte != null && photobyte.length > 0) {
					photo = BitmapFactory.decodeByteArray(photobyte, 0,
							photobyte.length);
				} else {
					Drawable photoDrawable = getResources().getDrawable(
							R.mipmap.ic_defphoto);
					BitmapDrawable bitmapDrawablePhoto = (BitmapDrawable) photoDrawable;
					photo = bitmapDrawablePhoto.getBitmap();
				}
				ArrayList<SettingItemInfo> baseList = new ArrayList<SettingItemInfo>();
				ArrayList<SettingItemInfo> alertList = new ArrayList<SettingItemInfo>();
				baseList.add(new SettingItemInfo("用户名", userValues
						.getAsString(UserFields.name), null, null));
				baseList.add(new SettingItemInfo("中文名", userValues
						.getAsString(UserFields.chName), null, null));
				baseList.add(new SettingItemInfo("性别", userValues
						.getAsString(UserFields.sex), null, null));
				baseList.add(new SettingItemInfo("用户角色", userValues
						.getAsString(UserFields.role), null, null));
				alertList.add(new SettingItemInfo("头像", null, photo, nextIco));

				alertList.add(new SettingItemInfo("修改密码", null, null, nextIco));
				baseAdapter = new SettingItemAdapter(context, baseList);
				alertAdapter = new SettingItemAdapter(context, alertList);
				alertListView.setAdapter(alertAdapter);
				baseListView.setAdapter(baseAdapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void logOff() {
		try {
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_actuser, "");
			Intent intent = new Intent(context, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void settingPhoto() {
		int w = rootView.getMeasuredWidth();
		int h = rootView.getMeasuredHeight();
		final PhotoSetDialog dialog = new PhotoSetDialog(context, w, h);
		dialog.setClickListener(new IClickListener() {
			public Object onClick(View v, Object o) {
				// TODO Auto-generated method stub

				int io = (Integer) o;
				if (io == 0 || io == 1) {
					showPhotoCut(io);
				}
				dialog.dismiss();
				return null;
			}
		});
		dialog.setOutsideTouchable(true);
		dialog.setFocusable(true);
		dialog.setOutsideTouchable(true);
		dialog.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shape_bg_black_tr25));
		dialog.setShowAnim(R.anim.slide_in_bottom);
		dialog.setHideAnim(R.anim.slide_out_bottom);
		dialog.showAtLocation(rootView, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	void setPassword() {
		if (iClickListener != null)
			iClickListener.onClick(null, 1);
	}

	private void showPhotoCut(int io) {
		// TODO Auto-generated method stub
		Intent intent = null;
		if (io == 0) {
			iClickListener.onClick(null, 10);
		} else if (io == 1) {
			iClickListener.onClick(null, 11);
		}
	}
}
