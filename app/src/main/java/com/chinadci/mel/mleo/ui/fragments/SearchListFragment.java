package com.chinadci.mel.mleo.ui.fragments;

import java.util.Calendar;
import java.util.TimeZone;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * 
 * @ClassName SearchListFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:47:23
 * 
 */
public class SearchListFragment extends ContentFragment {
	protected View rootView;
	protected EditText keyView;
	protected ListView listView;
	protected View listEmptyView;
	protected Button requestView;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_search_list, container, false);
		listView = (ListView) rootView.findViewById(R.id.fragment_search_list_listview);
		listEmptyView = rootView.findViewById(R.id.fragment_search_list_emptyview);
		keyView = (EditText) rootView.findViewById(R.id.fragment_search_list_keyview);
		requestView = (Button) rootView.findViewById(R.id.fragment_search_list_get);
		String userAdmin[] = DBHelper.getDbHelper(context).getUserAdmin(currentUser);
		if (userAdmin != null && userAdmin.length > 0 && userAdmin[0] != null
				&& !userAdmin[0].equals("")) {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
			int year = calendar.get(Calendar.YEAR);
			if (userAdmin[0].length() >= 6) {
				keyView.setText(new StringBuffer("T").append(userAdmin[0].substring(0, 6))
						.append(year).toString());
			}
		}
		initFragment();
		return rootView;
	}

	protected void initFragment() {

	}
}
