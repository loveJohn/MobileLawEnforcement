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
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_SP;

public class PatrolView_SP extends FrameLayout {

	View rootView;
	ListView attriListView;
	TextView titleView;
	
	String patrolId = "";
	AttriAdapter2 attriAdapter;
	Activity parentActivity;

	public PatrolView_SP(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public PatrolView_SP(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PatrolView_SP(Context context) {
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
			Patrols_SP patrols_SP = DbUtil.getPatrolsSPByid(getContext(), id);
			if (patrols_SP != null) {
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();
				keys.add("审核人员");
				if ( patrols_SP.getSpry()!= null
						&& !patrols_SP.getSpry().equals("")) {
					values.add(patrols_SP.getSpry());
				} else {
					values.add("");
				}

				if (patrols_SP.getSpsj() != null
						&& !patrols_SP.getSpsj().equals("")) {
					values.add(patrols_SP.getSpsj());
					keys.add("审核时间");
				}

				if (patrols_SP.getSpsm() != null
						&& !patrols_SP.getSpsm().equals("")) {
					values.add(patrols_SP.getSpsm());
					keys.add("审核说明");
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
