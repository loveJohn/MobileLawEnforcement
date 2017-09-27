package com.chinadci.mel.mleo.receivers;

import com.chinadci.mel.mleo.services.DciMleoPicker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	private PendingIntent mAlarmSender;

	@Override
	public void onReceive(Context context, Intent intent) {

		// start service
		Intent inetnt = new Intent(context, DciMleoPicker.class);
		inetnt.setAction(DciMleoPicker.ACTION);
		context.startService(inetnt);

		// mAlarmSender = PendingIntent.getService(context, 0, new
		// Intent(context,
		// TaskRequester.class), 0);
		// long firstTime = SystemClock.elapsedRealtime();
		// AlarmManager am = (AlarmManager) context
		// .getSystemService(Activity.ALARM_SERVICE);

		// am.cancel(mAlarmSender);
		// am.setRepeating(AlarmManager.ELAPSED_REALTIME, firstTime,
		// 30 * 60 * 1000, mAlarmSender);
	}

}
