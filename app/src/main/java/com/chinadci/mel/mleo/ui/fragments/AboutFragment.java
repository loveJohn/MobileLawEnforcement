package com.chinadci.mel.mleo.ui.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinadci.mel.R;

/**
 * 
* @ClassName AboutFragment 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:46:31 
*
 */
public class AboutFragment extends ContentFragment {
	View rootView;
	TextView txtName;
	TextView txtVersion;
	TextView txtIntro;
	String version;
	String packageName = "com.chinadci.mel.mobilelam";
	
	@Override
	public void handle(Object o) {
		// TODO Auto-generated method stub
		super.handle(o);
	}

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		super.refreshUi();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_about, container, false);
		txtName = (TextView) rootView.findViewById(R.id.fragment_about_name);
		txtVersion = (TextView) rootView
				.findViewById(R.id.fragment_about_version);
		txtIntro = (TextView) rootView.findViewById(R.id.fragment_about_intro);

		try {
			PackageManager packageManager = getActivity().getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(getActivity()
					.getPackageName(), 0);
			String version = packInfo.versionName;

			txtVersion.setText("当前版本:" + version);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootView;
	}
	}
