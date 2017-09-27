package com.chinadci.mel.mleo.comands;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.Command;
import com.chinadci.mel.mleo.ui.activities.LandscapeWebviewActivity;
import com.chinadci.mel.mleo.ui.activities.WebviewActivity;

/**
 * 
 * @ClassName StatisticsCommand
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:42:26
 * 
 */
public class LandStatisticsCommand extends Command {

	@Override
	public Object run(Activity activity, View view, Object object) {
		Intent intent = new Intent(activity, LandscapeWebviewActivity.class);
		intent.putExtra(WebviewActivity.TAG_TITLE,
				activity.getString(R.string.mitem_statistics));
		intent.putExtra(WebviewActivity.TAG_URL,
				activity.getString(R.string.uri_statistics_land));
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
		return null;
	}
}
