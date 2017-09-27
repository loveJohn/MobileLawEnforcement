package com.chinadci.mel.mleo.receivers;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.chinadci.android.utils.SharedPreferencesUtils;

public class ApkDownloadReceiver extends BroadcastReceiver {
	static receiver rec;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (rec == null)
			rec = new receiver();
		rec.openApk(context, intent);
	}

	static class receiver {
		int sp;
		int spdl;

		@SuppressLint("NewApi")
		public void openApk(Context context, Intent intent) {
			try {
				sp = context.getResources().getIdentifier(
						context.getPackageName() + ":string/"
								+ "shared_preferences", null, null);
				spdl = context.getResources().getIdentifier(
						context.getPackageName() + ":string/"
								+ "sp_apkdownload_id", null, null);

				long completeId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				String sdid = SharedPreferencesUtils.getInstance(context, sp)
						.getSharedPreferences(spdl, "");
				if (sdid.equals(String.valueOf(completeId))) {
					Query query = new Query();
					query.setFilterById(completeId);
					DownloadManager downloadManager = (DownloadManager) context
							.getSystemService(Context.DOWNLOAD_SERVICE);
					Cursor cursor = downloadManager.query(query);
					int columnCount = cursor.getColumnCount();
					String path = null;
					while (cursor.moveToNext()) {
						for (int j = 0; j < columnCount; j++) {
							String columnName = cursor.getColumnName(j);
							String string = cursor.getString(j);
							if (columnName.equals("local_uri")) {
								path = string;
							}
							if (string != null) {
								System.out.println(columnName + ": " + string);
							} else {
								System.out.println(columnName + ": null");
							}
						}
					}
					cursor.close();
					Intent si = new Intent(Intent.ACTION_GET_CONTENT);
					Uri mUri = Uri.parse("file://" + path);
					si.setDataAndType(mUri,
							"application/vnd.android.package-archive");
					si.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					si.setAction(android.content.Intent.ACTION_VIEW);
					context.startActivity(si);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
