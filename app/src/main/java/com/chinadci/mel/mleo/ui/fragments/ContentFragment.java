package com.chinadci.mel.mleo.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.mleo.core.interfaces.IDrawerFragmentActivityHandle;
import com.chinadci.mel.mleo.core.interfaces.IDrawerFragmentHandle;

public class ContentFragment extends Fragment implements IDrawerFragmentHandle {
	protected Context context;
	protected String currentUser;
	protected IDrawerFragmentActivityHandle activityHandle;
	int i_shared_preferences = 0;
	int i_sp_actuser = 0;
	int i_en_fcb_not_realize = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			context = getActivity().getApplicationContext();
			i_en_fcb_not_realize = context.getResources().getIdentifier(
					context.getPackageName() + ":string/" + "en_fcb_not_realize", null, null);//activity不认识IDrawerFragmentActivityHandle
			i_shared_preferences = context.getResources().getIdentifier(
					context.getPackageName() + ":string/" + "shared_preferences", null, null);//ls.sharedpreferences
			i_sp_actuser = context.getResources().getIdentifier(context.getPackageName() + ":string/" + "sp_actuser", null, null);//actuser
			currentUser = SharedPreferencesUtils.getInstance(context, i_shared_preferences).getSharedPreferences(i_sp_actuser, "");//获得当前登入用户
		} catch (Exception e) {
		}
	}

	public void handle(Object o) {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof IDrawerFragmentActivityHandle))
			throw new IllegalStateException(getString(i_en_fcb_not_realize));
		activityHandle = (IDrawerFragmentActivityHandle) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityHandle = null;
	}

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		
	}

}
