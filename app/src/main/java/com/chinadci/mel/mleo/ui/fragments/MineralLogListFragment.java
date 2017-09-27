package com.chinadci.mel.mleo.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.LocalCaseTable;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ui.adapters.MineralLogAdapter;

public class MineralLogListFragment extends ContentFragment {
	ListView listView;
	View listEmptyView;
	View rootView;
	SimpleDateFormat tableFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat viewFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	MineralLogAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_minerallog_list,
				container, false);
		listView = (ListView) rootView
				.findViewById(R.id.fragment_minerallog_listview);
		listEmptyView = rootView
				.findViewById(R.id.fragment_minerallog_listview_nodata);
		listView.setEmptyView(listEmptyView);
		adapter = new MineralLogAdapter(context, null);
		adapter.setActivity(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapterItemClickListener);
		initList();
		return rootView;
	}

	@Override
	public void handle(Object o) {
		// TODO Auto-generated method stub
		super.handle(o);
		if (o!=null){
			int tag = (Integer) o;
			if (tag == 0) {// 保存日志

			} else if (tag == 1) {// 发送日志

			}
		}
	}

	void initList() {
		try {
			String columns[] = new String[] { MilPatrolTable.field_id,
					MilPatrolTable.field_ajzt,
					MilPatrolTable.field_wfztmc,
					MilPatrolTable.field_szcj,
					MilPatrolTable.field_ffckbh,
					MilPatrolTable.field_exception,
					MilPatrolTable.field_hasMining, MilPatrolTable.field_notes,
					MilPatrolTable.field_logTime };
			String selection = new StringBuffer(MilPatrolTable.field_user)
					.append("=?").toString();
			String args[] = new String[] { currentUser };
			String order = new StringBuffer(MilPatrolTable.field_logTime)
					.append(" desc").toString();
			ArrayList<ContentValues> casevValues = DBHelper
					.getDbHelper(context).doQuery(MilPatrolTable.name, columns,
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

	OnItemClickListener adapterItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> p, View v, int i, long l) {
			ContentValues cv = (ContentValues) adapter.getItem(i);
			String id = cv.getAsString(LocalCaseTable.field_id);
			Bundle bundle = new Bundle();
			bundle.putString("LOGID", id);
			activityHandle.replaceTitle("编辑巡查日志");
			activityHandle.replaceToolFragment(new ToolSaveSend(), null,
					R.anim.slide_in_top, R.anim.slide_out_bottom);
			activityHandle.replaceContentFragment(new MineralLogFragment(),
					bundle, R.anim.slide_in_right, R.anim.slide_out_left);
		}
	};
}
