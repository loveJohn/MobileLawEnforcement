package com.chinadci.mel.mleo.core.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.chinadci.mel.android.core.DciActivityManager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class SMSReceiverHelper {
	static SMSReceiverHelper instance;// 单例
	static Lock instanceLock = new ReentrantLock();// 单例锁
	static final int NEW_INSPECT_SMS = 0x000001;

	// 所有的短信
	public static final String SMS_URI_ALL = "content://sms/";
	// 收件箱短信
	public static final String SMS_URI_INBOX = "content://sms/inbox";

	// 发件箱短信
	public static final String SMS_URI_SEND = "content://sms/sent";

	// 草稿箱短信
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	public static final String SMS_HANDLE = "handle";

	private Context context;
	public static long lastSMSTime;
	NotificationManager nm;

	public static SMSReceiverHelper getHelper(Context context) {
		if (instance == null) {
			instanceLock.lock();
			try {
				instance = new SMSReceiverHelper(context);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				instanceLock.unlock();
			}
		}
		return instance;
	}

	public SMSReceiverHelper(Context context) {
		this.context = context;
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@SuppressLint("NewApi")
	public void notifySMSChanged() {
		// 【案件编号35020501203620140036】
		try {
			Uri uri = Uri.parse(SMS_URI_INBOX);// 设置一个uri来查看各种类别短信内容
			Cursor cursor = context.getContentResolver().query(uri, null, null,
					null, null);
			long l = lastSMSTime;
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("_id"));// 编号
				String address = cursor.getString(cursor
						.getColumnIndex("address"));// 发送号
				String boddy = cursor.getString(cursor.getColumnIndex("body"));// 短信内容
				long time = cursor.getLong(cursor.getColumnIndex("date")); // 接收时间
				int read = cursor.getInt(cursor.getColumnIndex("read"));// 是否已读,0未读,1已读

				int count = DBHelper.getDbHelper(context).queryCount(
						DBHelper.T_SMS, new String[] { DBHelper.F_SMS_SMSID },
						DBHelper.F_SMS_SMSID + "=?",
						new String[] { String.valueOf(id) });
				if (count > 0)
					continue;

				if (time > l)
					l = time;

				if (lastSMSTime < time) {
					if (boddy.startsWith("【案件编号")) {
						String bidText = boddy.substring(5);
						String caseId = bidText.substring(0,
								bidText.indexOf("】"));
						if (caseId != null && !caseId.equals("")) {
							try {
								String ticker = "你有新的任务";
								String title = "你有新的核查任务";
								String text = "点击查看,案件编号" + caseId;
								Class<?> cls = Class
										.forName("com.chinadci.mel.mleo.ui.activities.SplashActivity");
								int ic_app = context.getResources()
										.getIdentifier(
												context.getPackageName()
														+ ":drawable/"
														+ "ic_app", null, null);
								// 案件编号存在
								Intent intent = new Intent(context, cls);
								intent.putExtra(SMS_HANDLE, "L,2102," + caseId);
								PendingIntent pi = PendingIntent.getActivity(
										context, 0, intent, 0);
								Builder notifyBuilder = new Notification.Builder(
										context);
								notifyBuilder.setContentIntent(pi);
								notifyBuilder.setAutoCancel(true)
										.setTicker(ticker).setSmallIcon(ic_app)
										.setContentTitle(title)
										.setContentText(text)
										.setWhen(System.currentTimeMillis())
										.setDefaults(Notification.DEFAULT_ALL);
								Notification notify = notifyBuilder
										.getNotification();
								nm.notify(NEW_INSPECT_SMS, notify);
								ContentValues smsValues = new ContentValues();
								smsValues.put(DBHelper.F_SMS_SMSID, id);
								smsValues.put(DBHelper.F_SMS_READ, 1);
								DBHelper.getDbHelper(context).insert(
										DBHelper.T_SMS, smsValues);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
				break;
			}
			lastSMSTime = l;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	static class DBHelper extends SQLiteOpenHelper {
		static DBHelper instance;// 单例
		static Lock instanceLock = new ReentrantLock();// 单例锁
		static final String DB_NAME = "mango_sms";// 数据库名
		static final int DB_VERSION = 1;// 数据库版本

		public static final String T_SMS = "SMS";
		public static final String F_SMS_ID = "ID";
		public static final String F_SMS_SMSID = "SMSID";
		public static final String F_SMS_READ = "READ";

		String CREATE_T_SMS;

		public static DBHelper getDbHelper(Context context) {
			if (instance == null) {
				instanceLock.lock();
				try {
					instance = new DBHelper(context);
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					instanceLock.unlock();
				}
			}
			return instance;
		}

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			initSQL();
			db.execSQL(CREATE_T_SMS);// 创建SMS表
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

		public static void destroy() {
			if (instance != null) {
				instance.close();
				instance = null;
			}
		}

		public int queryCount(String table, String columns[], String selection,
				String args[]) throws Exception {
			try {
				int count = 0;
				Cursor cursor = getReadableDatabase().query(table, columns,
						selection, args, null, null, null);
				if (cursor != null) {
					count = cursor.getCount();
					cursor.close();
				}
				return count;
			} catch (Exception e) {
				throw e;
			}
		}

		public long insert(String table, ContentValues values) throws Exception {
			try {
				long row = getWritableDatabase().insertOrThrow(table, null,
						values);
				return row;
			} catch (Exception e) {
				throw e;
			}
		}

		public int update(String table, ContentValues values,
				String whereClause, String whereArgs[]) throws Exception {
			try {
				int rows = getWritableDatabase().update(table, values,
						whereClause, whereArgs);
				return rows;
			} catch (Exception e) {
				throw e;
			}
		}

		void initSQL() {
			CREATE_T_SMS = String
					.format("CREATE TABLE [%1$s] ([%2$s] INTEGER PRIMARY KEY, [%3$s] INTEGER NOT NULL,[%4$s] INTEGER DEFAULT 1)",
							T_SMS, F_SMS_ID, F_SMS_SMSID, F_SMS_READ);
		}
	}
}
