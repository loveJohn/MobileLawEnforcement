package com.chinadci.mel.mleo.ui.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.mleo.core.Command;
import com.chinadci.mel.mleo.core.Funcation;
import com.chinadci.mel.mleo.core.FuncationGroup;

public class FuncationGroupActivity extends Activity {
	protected LinearLayout funcationGroupLayout;
	protected List<FuncationGroup> userFuncationGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DciActivityManager.getInstance().addActivity(this);
	}

	protected OnItemClickListener onFuncationClickListener = new OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> parent,
				View view, int position, long id) {
			try {
				Funcation funcation = (Funcation) view.getTag();
				String commandName = funcation.getCommand();
				Class<?> cls = Class.forName(commandName);
				Command command = (Command) cls.newInstance();
				command.run(FuncationGroupActivity.this, view, funcation);
				parent.setSelection(-1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		};
	};

}
