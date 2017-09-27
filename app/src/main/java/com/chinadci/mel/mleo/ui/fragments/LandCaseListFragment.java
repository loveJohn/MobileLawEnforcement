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
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.LocalAnnexTable;
import com.chinadci.mel.mleo.ldb.LocalCaseTable;
import com.chinadci.mel.mleo.ui.adapters.LocalCaseAdapter;

/**
 * 
 * @ClassName LandCaseListFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:46:44
 * 
 */
@SuppressLint("SimpleDateFormat")
public class LandCaseListFragment extends ContentFragment {
	ListView listView;
	View listEmptyView;
	View rootView;
	LocalCaseAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_case_list, container,
				false);
		listView = (ListView) rootView
				.findViewById(R.id.fragment_case_listview);
		listEmptyView = rootView
				.findViewById(R.id.fragment_case_listview_nodata);
		listView.setEmptyView(listEmptyView);

		adapter = new LocalCaseAdapter(context, null);
		adapter.setActivity(getActivity());
		adapter.setTableSource(LocalCaseTable.name, LocalAnnexTable.name);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(caseItemClickListener);
		initCaseList();
		return rootView;
	}

	void initCaseList() {
		try {
			String columns[] = new String[] { CaseTable.field_id,
					CaseTable.field_parties, CaseTable.field_address,
					LocalCaseTable.field_admin, CaseTable.field_illegalArea,
					CaseTable.field_illegalType, CaseTable.field_mTime };
			String selection = new StringBuffer(CaseTable.field_user)
					.append("=? and ").append(LocalCaseTable.field_status)
					.append("=?").toString();
			String args[] = new String[] { currentUser, String.valueOf(0) };
			String order = new StringBuffer(CaseTable.field_mTime).append(
					" desc").toString();
			ArrayList<ContentValues> casevValues = DBHelper
					.getDbHelper(context).doQuery(LocalCaseTable.name, columns,
							selection, args, null, null, order, null);

			if (casevValues != null) {
				adapter.setDataSet(casevValues);
				adapter.notifyDataSetChanged();
			} else {
				adapter.setDataSet(null);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnItemClickListener caseItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			ContentValues cv = (ContentValues) adapter.getItem(i);
			String id = cv.getAsString(LocalCaseTable.field_id);
			showCaseDetail(id);
		}
	};

	void showCaseDetail(String id) {
		Bundle bundle = new Bundle();
		bundle.putString(Parameters.CASE_ID, id);
		activityHandle.replaceTitle(getString(R.string.cn_info_edit));
		activityHandle.replaceToolFragment(new ToolSaveSend(), null,
				R.anim.slide_in_top, R.anim.slide_out_bottom);
		activityHandle.replaceContentFragment(new LandCaseEditeFragment(),
				bundle, R.anim.slide_in_right, R.anim.slide_out_left);
	}
}
