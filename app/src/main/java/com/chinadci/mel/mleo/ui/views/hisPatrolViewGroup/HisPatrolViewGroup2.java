package com.chinadci.mel.mleo.ui.views.hisPatrolViewGroup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.PatrolTable;
import com.chinadci.mel.mleo.ldb.TrackPatrolTable;
import com.chinadci.mel.mleo.ui.views.patrolEditeView2.PatrolView2;

/**
 */
public class HisPatrolViewGroup2 extends FrameLayout {
	View rootView;
	LinearLayout historyLayout;
	View emptyView;
	LinearLayout.LayoutParams layoutParams;
	String caseId = "";
	String user = "";
	String sourceTable = "";
	String annexTable = "";
	Activity parentActivity;

	public HisPatrolViewGroup2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public HisPatrolViewGroup2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public HisPatrolViewGroup2(Context context) {
		super(context);
		initView(context);
	}

	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	/**
	 * 
	 * @Title: setCaseSource
	 * @Description: TODO
	 * @param id
	 * @param table
	 * @throws
	 */
	public void setDataSource(String user, String id, String patrolTable,
			String annexTable) {
		this.caseId = id;
		this.sourceTable = patrolTable;
		this.annexTable = annexTable;
		this.user = user;
		// display case detail info
		viewPatrolInfo(this.caseId, this.sourceTable);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
		try {
			layoutParams = new LinearLayout.LayoutParams(
					layoutParams.MATCH_PARENT, layoutParams.WRAP_CONTENT);
			rootView = LayoutInflater.from(context).inflate(
					R.layout.view_hispatrol, null);
			rootView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			addView(rootView);
			historyLayout = (LinearLayout) rootView
					.findViewById(R.id.view_hispatrol_layout);
			emptyView = rootView.findViewById(R.id.view_hispatrol_nodata);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void viewPatrolInfo(String id, String table) {
		try {

			String columns[] = new String[] { PatrolTable.field_id,
					PatrolTable.field_caseId };
			String selection = new StringBuffer(PatrolTable.field_caseId)
					.append("=?").toString();
			String args[] = new String[] { id };
			String order = PatrolTable.field_mTime + " desc";
			ArrayList<ContentValues> patrolValues = DBHelper.getDbHelper(
					getContext()).doQuery(table, columns, selection, args,
					null, null, order, null);
			if (patrolValues != null && patrolValues.size() > 0) {
				emptyView.setVisibility(View.GONE);
				for (int i = 0; i < patrolValues.size(); i++) {
					ContentValues values = patrolValues.get(i);
					String titleText = new StringBuffer("第")
							.append(patrolValues.size() - i).append("次核查")
							.toString();
					PatrolView2 cview = new PatrolView2(getContext());
					cview.setParentActivity(parentActivity);
					cview.setTitle(titleText);
					cview.setDataSource(
							values.getAsString(TrackPatrolTable.field_id),
							sourceTable, annexTable);
					historyLayout.addView(cview, layoutParams);
				}
			} else {
				emptyView.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
