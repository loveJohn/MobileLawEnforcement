package com.chinadci.mel.mleo.comands;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.Command;
import com.chinadci.mel.mleo.core.Funcation;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;

/**
 * 
 * @ClassName OpenModuleCommand
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:42:17
 * 
 */
public class OpenModuleCommand extends Command {

	@Override
	public Object run(Activity activity, View view, Object object) {
		if (object != null && object instanceof Funcation) {
			Funcation funcation = (Funcation) object;
			Intent intent = new Intent(activity, ModuleRealizeActivity.class);
			intent.putExtra(ModuleActivity.TAG_MODULE_ID, funcation.getModule());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
		}
		return null;
	}
}
