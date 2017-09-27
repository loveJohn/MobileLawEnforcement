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
import com.chinadci.mel.mleo.ui.fragments.data.model.Patrols_TH;

public class PatrolView_TH extends FrameLayout{		//退回处理
	
	View rootView;
	ListView attriListView;
	TextView titleView;
	
	String patrolId = "";
	AttriAdapter2 attriAdapter;
	Activity parentActivity;

	public PatrolView_TH(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public PatrolView_TH(Context context, AttributeSet attrs) {
		this(context, attrs,-1);
	}

	public PatrolView_TH(Context context) {
		this(context,null);
	}

	public void setParentActivity(Activity activity) {
		parentActivity = activity;
	}

	public void setDataSource(String id,String title) {
		this.patrolId = id;
		viewPatrolInfo(this.patrolId,title);
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

	void viewPatrolInfo(String id,String title) {
		try {
			Patrols_TH patrols_TH = DbUtil.getPatrolsTHByid(getContext(), id);
			if (patrols_TH != null) {
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();
				
				titleView.setText(title);
				
				if(patrols_TH.getSqry()!= null
						&& !patrols_TH.getSqry().equals("")){
					keys.add("申请人员");
					values.add(patrols_TH.getSqry());
				}
				if(patrols_TH.getSqsj()!= null
						&& !patrols_TH.getSqsj().equals("")){
					keys.add("申请时间");
					values.add(patrols_TH.getSqsj());
				}
				if(patrols_TH.getSqyy()!= null
						&& !patrols_TH.getSqyy().equals("")){
					keys.add("申请原因");
					values.add(patrols_TH.getSqyy());
				}
				if(patrols_TH.getThry()!= null
						&& !patrols_TH.getThry().equals("")){
					keys.add("处理人员");
					values.add(patrols_TH.getThry());
				}
				if (patrols_TH.getThsj() != null
						&& !patrols_TH.getThsj().equals("")) {
					keys.add("处理时间");
					values.add(patrols_TH.getThsj());
				}
				if (patrols_TH.getThyy() != null
						&& !patrols_TH.getThyy().equals("")) {
					keys.add("处理说明");
					values.add(patrols_TH.getThyy());
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
