package com.chinadci.mel.mleo.ui.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.android.ui.activities.DciFragmentActivity;
import com.chinadci.mel.mleo.core.Global;
import com.chinadci.mel.mleo.core.ManifestHandler;
import com.chinadci.mel.mleo.core.Module;
import com.chinadci.mel.mleo.core.interfaces.IDrawerFragmentActivityHandle;
import com.chinadci.mel.mleo.ui.fragments.ContentFragment;
import com.chinadci.mel.mleo.ui.fragments.ToolFragment;
import com.chinadci.mel.mleo.utils.TimeUtil;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.runtime.LicenseResult;

@SuppressLint("HandlerLeak")
public class ModuleActivity extends DciFragmentActivity implements
		IDrawerFragmentActivityHandle {
	public final static String TAG_MODULE_ID = "module_id";
	Context context;
	protected String currentUser;
	protected ImageButton backButton;
	protected TextView titleView;
	protected TextSwitcher titleSwitcher;
	
	protected ContentFragment contentFragment;
	
	protected ToolFragment toolFragment;
	
	
	protected int toolLayoutId;
	protected int contentLayoutId;
	protected int backLayoutId;
	protected int titleLayoutId;
	protected int moduleId;
	static final String CLIENT_ID = "fWjgn6RQYiqLZQgb";
	protected boolean isInitialFragment = true;

	protected boolean isNeedBackLastFragment = false;// TODO 2016.12.7
	protected List<ContentFragment> lastContentFragments = new ArrayList<ContentFragment>();
	protected List<Bundle> lastbundles = new ArrayList<Bundle>();
	
	@SuppressWarnings("unused")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DciActivityManager.getInstance().addActivity(this);
		context=this;
		LicenseResult licenseResult = ArcGISRuntime.setClientId(CLIENT_ID);
		if (Global.funcationMap == null || Global.moduleMap == null) {
			ManifestHandler manifestHandler;
			try {
				manifestHandler = new ManifestHandler(ModuleActivity.this,
						getAssets().open("modules.xml"));
				Global.funcationMap = manifestHandler.getFunctionMap();
				Global.moduleMap = manifestHandler.getModuleMap();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//add teng.guo 20170721
		SharedPreferencesUtils.getInstance(this, R.string.shared_preferences)
		.writeSharedPreferences(R.string.sp_last_login_time,TimeUtil.currentTimeMillisString());		//记录退出时间
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	

	@Override
	public void onBackPressed() {
		if (isInitialFragment) {
			lastContentFragments.clear();
			lastbundles.clear();
			super.onBackPressed();
		} else {// 重新加载初始内容
			isInitialFragment = true;
			if (!isNeedBackLastFragment) {// TODO 2016.12.7
				intFragment();
			} else {
				if(lastContentFragments.size()>0){
					isNeedBackLastFragment = false;
					if(lastContentFragments.size()>1){
						replaceContentFragmentNeedBackLast(lastContentFragments.get(lastContentFragments.size()-2),lastContentFragments.get(lastContentFragments.size()-1), lastbundles.get(lastContentFragments.size()-1),R.anim.slide_in_right,R.anim.slide_out_left);
					}else{
						replaceContentFragment(lastContentFragments.get(0), lastbundles.get(0),R.anim.slide_in_right,R.anim.slide_out_left);
					}
				}
			}
		}
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		contentFragment.onActivityResult(requestCode, resultCode, data);
	}*/

	@Override
	public void setTitle(CharSequence title) {
		titleView.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		titleView.setText(titleId);
	}

	public void replaceTitle(String t) {
		try {
			if (t != null) {
				titleSwitcher.setText(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void replaceContentFragment(ContentFragment fragment, Bundle bundle,
			int inAnimRes, int outAnimRes) {
		try {
			contentFragment = fragment;
			switchFragment(contentFragment, bundle, contentLayoutId, inAnimRes,
					outAnimRes);
			isInitialFragment = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO 2016.12.7
	public void replaceContentFragmentNeedBackLast(
			ContentFragment lastFragment, ContentFragment fragment,
			Bundle bundle, int inAnimRes, int outAnimRes) {
		try {
			contentFragment = fragment;
			switchFragment(contentFragment, bundle, contentLayoutId, inAnimRes,
					outAnimRes);
			isInitialFragment = false;
			isNeedBackLastFragment = true;
			lastContentFragments.add((ContentFragment) lastFragment);
			lastbundles.add(bundle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void replaceToolFragment(ToolFragment fragment, Bundle bundle,
			int inAnimRes, int outAnimRes) {
		try {
			toolFragment = fragment;
			switchFragment(toolFragment, bundle, toolLayoutId, inAnimRes,
					outAnimRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void contentFragmentHandle(Object o) {
		try {
			if (contentFragment != null) {
				contentFragment.handle(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void toolFragmentHandle(Object o) {
		try {
			if (toolFragment != null) {
				toolFragment.handle(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void activityHandle(Object o) {
		@SuppressWarnings("unused")
		Integer tag = (Integer) o;
	}

	/**
	 * 
	 * @Title: initActivity
	 * @Description: 初始化视图
	 * @throws
	 */
	protected void initActivity() {
		try {
			backButton = (ImageButton) findViewById(backLayoutId);
			titleSwitcher = (TextSwitcher) findViewById(titleLayoutId);
			titleSwitcher.setFactory(titleViewFactory);
			backButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					onBackPressed();
				}
			});
			intFragment();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void intFragment() {
		try {
			moduleId = getIntent().getIntExtra(TAG_MODULE_ID, -1);
			if (moduleId > -1) {
				Module module = Global.moduleMap.get(moduleId);
				if (module != null) {
					Class<?> contentCls = Class.forName(module.getContent());
					contentFragment = (ContentFragment) contentCls
							.newInstance();
					Class<?> toolCls = Class.forName(module.getTool());
					toolFragment = (ToolFragment) toolCls.newInstance();
					switchFragment(contentFragment, null, contentLayoutId,
							R.anim.slide_in_left, R.anim.slide_out_right);
					switchFragment(toolFragment, null, toolLayoutId,
							R.anim.fade_in, R.anim.fade_out);
					int titleId = module.getTitleRes();
					replaceTitle(getString(titleId));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: switchFragment
	 * @Description: Fragment 切换
	 * @param fragment
	 * @param bundle
	 * @param layoutRes
	 * @param inAnimRes
	 * @param outAnimRes
	 * @throws
	 */
	protected void switchFragment(android.support.v4.app.Fragment fragment,
			Bundle bundle, int layoutRes, int inAnimRes, int outAnimRes) {
		fragment.setArguments(bundle);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(inAnimRes, outAnimRes);
		ft.replace(layoutRes, fragment).commitAllowingStateLoss();
	}

	/**
	 * 标题
	 */
	ViewFactory titleViewFactory = new ViewFactory() {

		public View makeView() {
			titleView = new TextView(ModuleActivity.this);
			titleView.setTextColor(Color.WHITE);
			titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			return titleView;
		}
	};

	public void setModule(int i) {

	}
	//add teng.guo start
	//地块列表查询条件
	String patch;			//所要查询的批次
	String ajYear;			//所要查询的案件年份
	String xzqbmCode;		//所要查找的行政区编码
	String xzqName;			//所要查找的行政区名
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("ydzf", "onActivity_Md_resultCode="+resultCode+",requestCode="+requestCode);
		super.onActivityResult(requestCode, resultCode, data);
		contentFragment.onActivityResult(requestCode, resultCode, data);
	}
	
	public void refreshUI() {
		// TODO Auto-generated method stub
		if (contentFragment != null){
			contentFragment.refreshUi();
		}
	}
	
	public String getPatch(){
		return patch;
	}
	
	public void setPatch(String p){
		patch=p;
	}
	
	
	public String getAjYear(){
		return ajYear;
	}
	
	public void setAjYear(String year){
		ajYear=year;
	}
	
	public String getXzqbmCode(){
		return xzqbmCode;
	}
	
	public void setXzqbmCode(String code){
		xzqbmCode=code;
	}
	
	public String getXzqName(){
		return xzqName;
	}
	
	public void setXzqName(String name){
		xzqName=name;
	}

	@Override
	public void setToolButtonType(int i) {
		// TODO Auto-generated method stub
		toolFragment.setToolButtonType(i);
	}
	
	//add teng.guo end
}
