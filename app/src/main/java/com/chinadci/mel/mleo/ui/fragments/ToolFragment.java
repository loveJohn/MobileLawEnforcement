package com.chinadci.mel.mleo.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.mleo.core.interfaces.IDrawerFragmentActivityHandle;
import com.chinadci.mel.mleo.core.interfaces.IDrawerFragmentHandle;

public class ToolFragment extends Fragment implements IDrawerFragmentHandle {

	public static final int TOOL_FILTER=0x001;		//批量查找功能
	public static final int TOOL_SEARCH=0x002;		//编号查询功能
	
	protected Context context;
	protected String currentUser;
	protected IDrawerFragmentActivityHandle activityHandle;

	int i_shared_preferences = -1;
	int i_sp_actuser = -1;
	int i_en_fcb_not_realize = -1;
	int i_fragment_tool_base = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			context = getActivity().getApplicationContext();
			i_en_fcb_not_realize = context.getResources().getIdentifier(
					context.getPackageName() + ":string/"
							+ "en_fcb_not_realize", null, null);
			i_shared_preferences = context.getResources().getIdentifier(
					context.getPackageName() + ":string/"
							+ "shared_preferences", null, null);
			i_sp_actuser = context.getResources().getIdentifier(
					context.getPackageName() + ":string/" + "sp_actuser", null,
					null);
			i_fragment_tool_base = context.getResources().getIdentifier(
					context.getPackageName() + ":layout/"
							+ "fragment_tool_base", null, null);
			currentUser = SharedPreferencesUtils.getInstance(context,
					i_shared_preferences)
					.getSharedPreferences(i_sp_actuser, "");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(i_fragment_tool_base, null);
		return view;
	}

	public void handle(Object o) {
		// TODO Auto-generated method stub

	}

	public void refreshUi() {
		// TODO Auto-generated method stub

	}
	
	public void setToolButtonType(int i){
		
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

}
