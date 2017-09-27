package com.chinadci.mel.mleo.ui.views.patrolEditeView2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter2;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_CH;

public class PatrolView_CH extends FrameLayout {

	View rootView;
	ListView attriListView;
	TextView titleView;
	
	String patrolId = "";
	AttriAdapter2 attriAdapter;
	Activity parentActivity;

	public PatrolView_CH(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public PatrolView_CH(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PatrolView_CH(Context context) {
		super(context);
		initView(context);
	}

	public void setParentActivity(Activity activity) {
		parentActivity = activity;
	}

	public void setDataSource(String id) {
		this.patrolId = id;
		viewPatrolInfo(this.patrolId);
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

	void initView(Context context) {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_patrol_sp,
				null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		attriListView = (ListView) rootView
				.findViewById(R.id.view_patrol_attrilist);
		titleView = (TextView) rootView.findViewById(R.id.view_patrol_title);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	void viewPatrolInfo(String id) {
		try {
			Patrols_CH patrols_CH = DbUtil.getPatrolsCHByid(getContext(), id);
			if (patrols_CH != null) {
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();
				keys.add("撤回人员");
				if ( patrols_CH.getChry()!= null
						&& !patrols_CH.getChry().equals("")) {
					values.add(patrols_CH.getChry());
				} else {
					values.add("");
				}

				if (patrols_CH.getChsj() != null
						&& !patrols_CH.getChsj().equals("")) {
					values.add(patrols_CH.getChsj());
					keys.add("撤回时间");
				}

				if (patrols_CH.getChyy() != null
						&& !patrols_CH.getChyy().equals("")) {
					values.add(patrols_CH.getChyy());
					keys.add("撤回原因");
				}
				attriAdapter = new AttriAdapter2(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
