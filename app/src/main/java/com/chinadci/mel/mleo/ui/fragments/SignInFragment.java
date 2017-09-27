package com.chinadci.mel.mleo.ui.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.SignTable;
import com.chinadci.mel.mleo.ui.adapters.SignAdapter;

@SuppressLint("SimpleDateFormat")
public class SignInFragment extends ContentFragment {

	ListView listView;
	SignAdapter adapter;
	View listEmptyView;
	View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_signin, container, false);
		listView = (ListView) rootView
				.findViewById(R.id.fragment_sign_listview);
		listEmptyView = rootView
				.findViewById(R.id.fragment_sign_listview_nodata);
		listView.setEmptyView(listEmptyView);

		adapter = new SignAdapter(context, null);
		adapter.setActivity(getActivity());
		adapter.setTableSource(SignTable.name);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(signItemClickListener);
		initSignList();
		return rootView;
	}

	void initSignList() {
		try {
			String columns[] = new String[] { SignTable.field_id,
					SignTable.field_address, SignTable.field_admin,
					SignTable.field_type, SignTable.field_time,
					SignTable.field_user, SignTable.field_cause,
					SignTable.field_notes };
			String selection = new StringBuffer(SignTable.field_user)
					.append("=? and ").append(SignTable.field_status)
					.append("=?").toString();
			String args[] = new String[] { currentUser, String.valueOf(0) };
			String order = new StringBuffer(SignTable.field_time).append(
					" desc").toString();
			ArrayList<ContentValues> signValues = DBHelper.getDbHelper(context)
					.doQuery(SignTable.name, columns, selection, args, null,
							null, order, null);

			if (signValues != null) {
				adapter.setDataSet(signValues);
				adapter.notifyDataSetChanged();
			} else {
				adapter.setDataSet(null);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnItemClickListener signItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			ContentValues cv = (ContentValues) adapter.getItem(i);
			String id = cv.getAsString(SignTable.field_id);
			showSignDetail(id);
		}
	};

	void showSignDetail(String id) {
		Bundle bundle = new Bundle();
		bundle.putString("sign_id", id);
		activityHandle.replaceTitle(getString(R.string.cn_info_edit));
		activityHandle.replaceToolFragment(new ToolSaveSend(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
		activityHandle.replaceContentFragment(new SignEditFragment(), bundle,
				R.anim.slide_in_right, R.anim.slide_out_left);
	}

}
