package com.chinadci.mel.mleo.ui.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.chinadci.android.utils.DrawableUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.core.SettingItemInfo;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.UserFields;
import com.chinadci.mel.mleo.ldb.UserTable;
import com.chinadci.mel.mleo.ui.activities.LoginActivity;
import com.chinadci.mel.mleo.ui.activities.PasswordUpdateActivity;
import com.chinadci.mel.mleo.ui.activities.PhotoCutActivity;
import com.chinadci.mel.mleo.ui.adapters.SettingItemAdapter;
import com.chinadci.mel.mleo.ui.popups.PhotoSetDialog;
import com.chinadci.mel.mleo.utils.ClearDataUtils;

/**
 * 
 * @ClassName UserFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:47:54
 * 
 */
public class UserFragment extends ContentFragment {
	public static final int ALERT_USER_PHOTO = 0x000000;
	View rootView;
	Button logoffButton;
	ListView alertListView;
	ListView baseListView;
	ListView funListView;
	SettingItemAdapter alertAdapter;
	SettingItemAdapter baseAdapter;
	SettingItemAdapter funAdapter;
	IClickListener iClickListener;
	AlertDialog alertDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_user, container, false);
		alertListView = (ListView) rootView
				.findViewById(R.id.fragment_user_alterlist);
		baseListView = (ListView) rootView
				.findViewById(R.id.fragment_user_baselist);
		funListView= (ListView) rootView
				.findViewById(R.id.fragment_user_funlist);
		logoffButton = (Button) rootView
				.findViewById(R.id.fragment_user_logoff);

		logoffButton.setOnClickListener(logoffClickListener);
		alertListView.setOnItemClickListener(alertItemClickListener);
		funListView.setOnItemClickListener(funItemClickListener);
		initUserInfo();
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ALERT_USER_PHOTO) {
			initUserInfo();
		}
	}

	void initUserInfo() {
		try {

			String columns[] = new String[] { UserTable.field_admin,
					UserTable.field_chName, UserTable.field_name,
					UserTable.field_photo, UserTable.field_role,
					UserTable.field_sex, UserTable.field_tel };

			String where = new StringBuffer(UserTable.field_name).append("=?")
					.toString();
			String args[] = new String[] { currentUser };
			ContentValues userValues = DBHelper.getDbHelper(context).doQuery(
					UserTable.name, columns, where, args);

			if (userValues != null) {

				Drawable nextDrawable = getResources().getDrawable(
						R.mipmap.ic_setting_next);
				BitmapDrawable bitmapDrawableNext = (BitmapDrawable) nextDrawable;
				Bitmap nextIco = bitmapDrawableNext.getBitmap();
				int photoWidth = (int) (64 * getResources().getDisplayMetrics().density);
				Bitmap photo = null;
				byte photobyte[] = userValues.getAsByteArray(UserFields.photo);
				if (photobyte != null && photobyte.length > 0) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(photobyte, 0,
							photobyte.length);
					Bitmap wBitmap = DrawableUtils.zoomBitmap(bitmap,
							photoWidth / 2, photoWidth / 2);
					photo = DrawableUtils.getRoundedCornerBitmap(wBitmap,
							photoWidth);
				} else {
					Drawable photoDrawable = getResources().getDrawable(R.mipmap.ic_defphoto);
					BitmapDrawable bitmapDrawablePhoto = (BitmapDrawable) photoDrawable;
					Bitmap bitmap = bitmapDrawablePhoto.getBitmap();
					Bitmap wBitmap = DrawableUtils.zoomBitmap(bitmap,
							photoWidth / 2, photoWidth / 2);
					photo = DrawableUtils.getRoundedCornerBitmap(wBitmap,
							photoWidth);
				}
				
				Drawable photoDrawable = getResources().getDrawable(R.mipmap.cache_delete);
				BitmapDrawable bitmapDrawablePhoto = (BitmapDrawable) photoDrawable;
				Bitmap bitmap = bitmapDrawablePhoto.getBitmap();
				Bitmap wBitmap = DrawableUtils.zoomBitmap(bitmap,
						photoWidth / 2, photoWidth / 2);
				Bitmap deletePhoto = DrawableUtils.getRoundedCornerBitmap(wBitmap,
						photoWidth);
				
				ArrayList<SettingItemInfo> baseList = new ArrayList<SettingItemInfo>();
				ArrayList<SettingItemInfo> alertList = new ArrayList<SettingItemInfo>();
				ArrayList<SettingItemInfo> funList = new ArrayList<SettingItemInfo>();

				// baseList.add(new SettingItemInfo("用户名", userValues
				// .getAsString(UserTable.field_name), null, null));
				baseList.add(new SettingItemInfo("中文名", userValues
						.getAsString(UserTable.field_chName), null, null));
				baseList.add(new SettingItemInfo("电话", userValues
						.getAsString(UserTable.field_tel), null, null));
				baseList.add(new SettingItemInfo("职务", userValues
						.getAsString(UserTable.field_role), null, null));

				alertList.add(new SettingItemInfo("头像", null, photo, nextIco));
				alertList.add(new SettingItemInfo("修改密码", null, null, nextIco));
				
				funList.add(new SettingItemInfo("清除应用数据", null, deletePhoto, nextIco));
				// alertList
				// .add(new SettingItemInfo("修改电话号码", null, null, nextIco));

				baseAdapter = new SettingItemAdapter(context, baseList);
				alertAdapter = new SettingItemAdapter(context, alertList);
				funAdapter = new SettingItemAdapter(context, funList);

				alertListView.setAdapter(alertAdapter);
				baseListView.setAdapter(baseAdapter);
				funListView.setAdapter(funAdapter);
				
				alertAdapter.notifyDataSetChanged();
				baseAdapter.notifyDataSetChanged();
				funAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * logOff
	 */
	void logOff() {
		try {
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_actuser, "");
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_last_signin_time, "");

			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_actusername, "");
			
			Intent intent = new Intent(context, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(intent);
			// getActivity().overridePendingTransition(R.anim.slide_in_left,
			// R.anim.slide_out_right);
			getActivity().finish();
			DciActivityManager.getInstance().exit();
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	void settingPhoto() {
		int w = rootView.getMeasuredWidth();
		int h = rootView.getMeasuredHeight();
		final PhotoSetDialog dialog = new PhotoSetDialog(context, w, h);
		dialog.setClickListener(new IClickListener() {
			public Object onClick(View v, Object o) {
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

	void settingPassword() {
		Intent intent = new Intent(getActivity(), PasswordUpdateActivity.class);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
		// PasswordUpdateActivity
	}

	void settingTel() {

	}

	OnClickListener logoffClickListener = new OnClickListener() {

		public void onClick(View v) {
			View alertView = LayoutInflater.from(context).inflate(
					R.layout.view_alert, null);
			TextView notesView = (TextView) alertView
					.findViewById(R.id.view_alert_notes);
			Button cancelButton = (Button) alertView
					.findViewById(R.id.view_alert_cancel);
			Button doButton = (Button) alertView
					.findViewById(R.id.view_alert_do);
			notesView.setText("确定要退出应用吗？");
			cancelButton.setText("暂不退出");
			doButton.setText("现在退出");

			cancelButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (alertDialog != null && alertDialog.isShowing())
						alertDialog.dismiss();
				}
			});

			doButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (alertDialog != null && alertDialog.isShowing())
						alertDialog.dismiss();
					logOff();
				}
			});

			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(true);
			alertDialog.getWindow().setContentView(alertView);
		}
	};

	OnItemClickListener alertItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			switch (i) {
			// set user's photo
			case 0:
				settingPhoto();
				break;
			// reset user's password
			case 1:
				settingPassword();
				break;
			// resset user's tel
			case 2:
				settingTel();
				break;

			default:
				break;
			}

		}
	};
	
	OnItemClickListener funItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			switch (i) {
			// set user's photo
			case 0:
				clearCacheDate();
				break;
			default:
				break;
			}

		}
	};


	void showPhotoCut(int w) {
		if (w == 0) {
			Intent intent = new Intent(context, PhotoCutActivity.class);
			intent.putExtra(PhotoCutActivity.PHOTOFROM,
					PhotoCutActivity.CAMERAWAY);
			intent.putExtra("user", currentUser);
			getActivity().startActivityForResult(intent, ALERT_USER_PHOTO);
		} else if (w == 1) {
			Intent intent = new Intent(context, PhotoCutActivity.class);
			intent.putExtra(PhotoCutActivity.PHOTOFROM,
					PhotoCutActivity.FILEWAY);
			intent.putExtra("user", currentUser);
			getActivity().startActivityForResult(intent, ALERT_USER_PHOTO);
		}
	}
	
	void clearCacheDate(){
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage("清除应用数据将退出应用，确认继续?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ClearDataUtils.getInstance(context).clearDatabases();
						logOff();
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}
