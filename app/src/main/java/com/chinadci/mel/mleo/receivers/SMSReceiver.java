package com.chinadci.mel.mleo.receivers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.chinadci.mel.mleo.core.utils.SMSReceiverHelper;

public class SMSReceiver extends ContentObserver {

	public static final String ACTION = "com.chinadci.mel.mleo.actions.NEWSMS";

	private Context context;

	public SMSReceiver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	public SMSReceiver(Handler handler, Context s) {
		super(handler);
		this.context = s;

	}

	@SuppressLint("NewApi")
	@Override
	public void onChange(boolean selfChange) {
		smsChanged();
		super.onChange(selfChange);
	}

	@SuppressLint("NewApi")
	public void smsChanged() {
		SMSReceiverHelper.getHelper(context).notifySMSChanged();
	}
}
