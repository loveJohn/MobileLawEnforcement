package com.chinadci.mel.mleo.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import com.chinadci.mel.mleo.core.utils.SMSReceiverHelper;
import com.chinadci.mel.mleo.receivers.SMSReceiver;

public class DciMleoPicker extends Service {
	public final static String ACTION = "com.chinadci.mel.mleo.actions.DCIMLEOPICKER";
	static String lastSMSTime = "";
	Context context;

	static final int NEWTASK_NOTIFICATION_ID = 0x000001;
	NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		getContentResolver().registerContentObserver(
				Uri.parse(SMSReceiverHelper.SMS_URI_INBOX), true,
				new SMSReceiver(new Handler(), DciMleoPicker.this));
		SMSReceiverHelper.getHelper(this).notifySMSChanged();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Intent localIntent = new Intent();
		localIntent.setClass(this, DciMleoPicker.class); // 销毁时重新启动Service
		this.startService(localIntent);
	}

}
