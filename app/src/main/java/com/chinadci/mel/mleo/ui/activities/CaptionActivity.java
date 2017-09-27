package com.chinadci.mel.mleo.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinadci.android.utils.SharedPreferencesUtils;


/**
 * 
 * @ClassName CaptionActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:31:38
 * 
 */
public class CaptionActivity extends FragmentActivity implements
		OnClickListener {

	protected RelativeLayout toolBar;
	protected RelativeLayout contentPad;
	protected String currentUser;

	ImageButton backButton;
	TextView titleView;

	int shared_preferences;
	int sp_actuser;
	int activity_caption_backbutton;
	int activity_caption_title;
	int activity_caption_ttoolbar;
	int activity_caption_content;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		int activity_caption = getResources().getIdentifier(
				getPackageName() + ":layout/" + "activity_caption", null, null);
		setContentView(activity_caption);
		initActivity();
	}

	protected void initActivity() {
		try {
			shared_preferences = getResources().getIdentifier(
					getPackageName() + ":string/" + "shared_preferences", null,
					null);
			sp_actuser = getResources().getIdentifier(
					getPackageName() + ":string/" + "sp_actuser", null, null);
			activity_caption_backbutton = getResources().getIdentifier(
					getPackageName() + ":id/" + "activity_caption_backbutton",
					null, null);
			activity_caption_title = getResources().getIdentifier(
					getPackageName() + ":id/" + "activity_caption_title", null,
					null);
			activity_caption_ttoolbar = getResources().getIdentifier(
					getPackageName() + ":id/" + "activity_caption_ttoolbar",
					null, null);
			activity_caption_content = getResources().getIdentifier(
					getPackageName() + ":id/" + "activity_caption_content",
					null, null);

			currentUser = SharedPreferencesUtils.getInstance(this,
					shared_preferences).getSharedPreferences(sp_actuser, "");
			backButton = (ImageButton) findViewById(activity_caption_backbutton);
			titleView = (TextView) findViewById(activity_caption_title);
			toolBar = (RelativeLayout) findViewById(activity_caption_ttoolbar);
			contentPad = (RelativeLayout) findViewById(activity_caption_content);
			backButton.setOnClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	protected void setToolBar(View v) {
		toolBar.removeAllViews();
		toolBar.addView(v, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
	}

	protected View setToolBar(int res) {
		View v = LayoutInflater.from(this).inflate(res, null);
		toolBar.removeAllViews();
		toolBar.addView(v, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		return v;
	}

	protected void setContent(View v) {
		contentPad.removeAllViews();
		contentPad.addView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	protected View setContent(int res) {
		View v = LayoutInflater.from(this).inflate(res, null);
		contentPad.removeAllViews();
		contentPad.addView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		return v;
	}

	@Override
	public void setTitle(CharSequence title) {

		titleView.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		titleView.setText(titleId);
	}

	protected void onBackButtonClick(View v) {
	}

	public void onClick(View v) {
		int vid = v.getId();
		if (vid == activity_caption_backbutton) {
			onBackButtonClick(v);
		}
	}
}
