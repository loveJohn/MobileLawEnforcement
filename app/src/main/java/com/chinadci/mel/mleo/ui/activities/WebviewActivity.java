package com.chinadci.mel.mleo.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chinadci.mel.android.ui.activities.DciFragmentActivity;
import com.chinadci.mel.mleo.core.interfaces.IDrawerFragmentActivityHandle;
import com.chinadci.mel.mleo.ui.fragments.ContentFragment;
import com.chinadci.mel.mleo.ui.fragments.ToolFragment;
import com.chinadci.mel.mleo.ui.fragments.WebviewFragment;

/**
 * 
 * @ClassName CatalogActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:31:41
 * 
 */
public class WebviewActivity extends DciFragmentActivity implements
		IDrawerFragmentActivityHandle {
	public final static String TAG_TITLE = "title";
	public final static String TAG_URL = "url";

	FrameLayout frameLayout;
	ImageButton backButton;
	TextView titleView;

	String title;
	String url;

	int i_layout_res;
	int i_back_res;
	int i_title_res;
	int i_frame_res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getIntent().getStringExtra(TAG_TITLE);
		url = getIntent().getStringExtra(TAG_URL);

		initRes();
		setContentView(i_layout_res);
		titleView = (TextView) findViewById(i_title_res);
		backButton = (ImageButton) findViewById(i_back_res);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WebviewActivity.this.finish();
			}
		});

		if (title != null)
			titleView.setText(title);
		new doSleep().execute();
	}

	void initRes() {
		i_layout_res = getResources().getIdentifier(
				getPackageName() + ":layout/" + "activity_webview", null, null);
		i_back_res = getResources()
				.getIdentifier(
						getPackageName() + ":id/" + "activity_webview_back",
						null, null);
		i_title_res = getResources().getIdentifier(
				getPackageName() + ":id/" + "activity_webview_title", null,
				null);
		i_frame_res = getResources().getIdentifier(
				getPackageName() + ":id/" + "activity_webview_frame", null,
				null);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void switchFragment(android.support.v4.app.Fragment fragment,
			int layoutRes) {

	}

	class doSleep extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ContentFragment fragment = new WebviewFragment();
			Bundle bundle = new Bundle();
			bundle.putString(WebviewFragment.TAG_URL, url);
			fragment.setArguments(bundle);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.setCustomAnimations(android.R.anim.fade_in,
					android.R.anim.fade_out);
			ft.replace(i_frame_res, fragment).commitAllowingStateLoss();
		}
	}

	public void activityHandle(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void contentFragmentHandle(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void replaceContentFragment(ContentFragment arg0, Bundle arg1,
			int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	public void replaceTitle(String arg0) {
		// TODO Auto-generated method stub

	}

	public void replaceToolFragment(ToolFragment arg0, Bundle arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	public void toolFragmentHandle(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void setModule(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setToolButtonType(int i) {
		// TODO Auto-generated method stub
		
	}
}
