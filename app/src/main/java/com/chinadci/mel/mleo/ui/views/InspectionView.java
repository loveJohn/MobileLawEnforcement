package com.chinadci.mel.mleo.ui.views;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter2;

/**
 * 
 * @ClassName InspectionView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:53
 * 
 */
public class InspectionView extends FrameLayout {

	View rootView;
	ListView attriListView;
	View emptyView;
	String caseId = "";
	String sourceTable = "";
	AttriAdapter2 attriAdapter;

	public InspectionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public InspectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public InspectionView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * 
	 * @Title: setCaseSource
	 * @Description: TODO
	 * @param id
	 * @param table
	 * @throws
	 */
	public void setDataSource(String id, String table) {
		this.caseId = id;
		this.sourceTable = table;
		// display case detail info
		viewInspectionInfo(this.caseId, this.sourceTable);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: TODO
	 * @param context
	 * @throws
	 */
	void initView(Context context) {
		rootView = LayoutInflater.from(context).inflate(
				R.layout.view_inspection, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		attriListView = (ListView) rootView
				.findViewById(R.id.view_inspection_listview);
		emptyView = rootView.findViewById(R.id.view_inspection_nodata);

	}

	/**
	 * 
	 * @Title: viewCaseInfo
	 * @Description: TODO
	 * @param id
	 * @param table
	 * @throws
	 */
	void viewInspectionInfo(String id, String table) {
		try {
			String columns[] = new String[] { CaseInspectTable.field_id,

			CaseInspectTable.field_caseId,
					CaseInspectTable.field_illegalSubject,
					CaseInspectTable.field_illegalStatus,
					CaseInspectTable.field_illegalType,
					CaseInspectTable.field_illegalArea,
					CaseInspectTable.field_landUsage,
					CaseInspectTable.field_parties, CaseInspectTable.field_tel,
					CaseInspectTable.field_notes, CaseInspectTable.field_user,
					CaseInspectTable.field_mTime };
			String selection = CaseInspectTable.field_caseId + "=?";
			String args[] = new String[] { id };
			ContentValues caseInfo = DBHelper.getDbHelper(getContext())
					.doQuery(table, columns, selection, args);
			if (caseInfo != null) {
				emptyView.setVisibility(View.GONE);
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();

				// 违法主体
				keys.add(getContext().getString(R.string.v_f2));
				if (caseInfo.getAsString(CaseInspectTable.field_illegalSubject) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_illegalSubject).equals(
								"")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalSubject));
				} else {
					values.add("");
				}

				// 违法当事人
				keys.add(getContext().getString(R.string.v_f3));
				if (caseInfo.getAsString(CaseInspectTable.field_parties) != null
						&& !caseInfo
								.getAsString(CaseInspectTable.field_parties)
								.equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_parties));
				} else {
					values.add("");
				}

				// 电话
				keys.add(getContext().getString(R.string.v_f17));
				if (caseInfo.getAsString(CaseInspectTable.field_tel) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_tel)
								.equals("")) {
					values.add(caseInfo.getAsString(CaseInspectTable.field_tel));
				} else {
					values.add("");
				}

				// 违法类型
				keys.add(getContext().getString(R.string.v_f8));
				if (caseInfo.getAsString(CaseInspectTable.field_illegalType) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_illegalType).equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalType));
				} else {
					values.add(" ");
				}

				// 违法状态
				keys.add(getContext().getString(R.string.v_f9));
				if (caseInfo.getAsString(CaseInspectTable.field_illegalStatus) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_illegalStatus).equals(
								" ")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalStatus));
				} else {
					values.add(" ");
				}

				// 违法用地面积
				keys.add(getContext().getString(R.string.v_f10) + "(m²)");
				if (caseInfo.getAsDouble(CaseInspectTable.field_illegalArea) != null) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalArea));
				} else {
					values.add("");
				}

				// 用地类型
				keys.add(getContext().getString(R.string.v_f18));
				if (caseInfo.getAsString(CaseInspectTable.field_landUsage) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_landUsage).equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_landUsage));
				} else {
					values.add("");
				}

				// // 核查结果
				// keys.add(getContext().getString(R.string.v_f19));
				// if
				// (caseInfo.getAsString(CaseInspectTable.field_inspectResult)
				// != null
				// && !caseInfo.getAsString(
				// CaseInspectTable.field_inspectResult)
				// .equals("")) {
				// values.add(caseInfo
				// .getAsString(CaseInspectTable.field_inspectResult));
				// } else {
				// values.add("");
				// }

				// 成果说明
				keys.add(getContext().getString(R.string.v_f20));
				if (caseInfo.getAsString(CaseInspectTable.field_notes) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_notes)
								.equals("")) {

					values.add(caseInfo
							.getAsString(CaseInspectTable.field_notes));
				} else {
					values.add("");
				}

				// 核查人员
				keys.add(getContext().getString(R.string.v_f21));
				if (caseInfo.getAsString(CaseInspectTable.field_user) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_user)
								.equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_user));
				} else {
					values.add("");
				}

				// 核查时间
				keys.add(getContext().getString(R.string.v_f22));
				if (caseInfo.getAsString(CaseInspectTable.field_mTime) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_mTime)
								.equals("")) {
					values.add(TimeFormatFactory2.getDisplayTimeM(caseInfo
							.getAsString(CaseInspectTable.field_mTime)));
				} else {
					values.add("");
				}

				attriAdapter = new AttriAdapter2(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();
			} else {
				emptyView.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
