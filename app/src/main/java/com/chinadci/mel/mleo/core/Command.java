package com.chinadci.mel.mleo.core;

import android.app.Activity;
import android.view.View;

public abstract class Command {
	public abstract Object run(Activity activity, View view, Object object);
}
