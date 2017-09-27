package com.chinadci.mel.mleo.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ldb.MineralHcTable;
import com.chinadci.mel.mleo.ldb.TrackMineralAnnexesTable;
import com.chinadci.mel.mleo.ldb.TrackMineralHcTable;
import com.chinadci.mel.mleo.ldb.TrackMineralTable;
import com.chinadci.mel.mleo.ui.activities.PolyGatherActivity;
import com.chinadci.mel.mleo.ui.views.MineralHcView;
import com.chinadci.mel.mleo.ui.views.MineralView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MineralHcFragment extends ContentFragment {
	List<View> viewList=new ArrayList<View>();
	List<String> titleList=new ArrayList<String>();
	
	View contentView;
	ViewPager viewPager;
	ImageView image;
	TextView tabView1;
	TextView tabView2;
	TextView tabView3;
	
	int moveX;  //导航下面偏移的宽度 
	int width;  //导航下面比较粗的宽度 
	int index;  //当前第一个View
	
	String caseId;
	
	MineralView mineralInfoView;
	MineralHcView mineralhcView;
	
	AlertDialog alertDialog;
	MineralHcPagerAdapter pagerAdapter;
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=Activity.RESULT_OK)
			return;
		switch(requestCode)
		{
//			case Parameters.GET_PHOTO:
//				Bundle photoBundle=data.getExtras();
//				String photos[]=photoBundle
//						.getStringArray(CameraPhotoActivity.PHOTOARRAY);
//				mineralhcEditView.addPhotoPaths(photos);
//				break;
//			case Parameters.GET_AUDIO:
//				Bundle audioBundle=data.getExtras();
//				String amrPath=audioBundle.getString(TapeActivity.AMRFILE);
//				mineralhcEditView.addAudioPath(amrPath);
//				break;
			case Parameters.GET_REDLINE:
				Bundle redlineBundle=data.getExtras();
				String relineString =redlineBundle.getString(
						PolyGatherActivity.REDLINE).toString();
				//mineralhcEditView.setRed
				break;
			default:
				break;
		}
		
	}
	
	//tool的方法
	@Override
	public void handle(Object o)
	{
		super.handle(o);
	}
	
	@Override
	public void refreshUi()
	{
		super.refreshUi();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context=getActivity().getApplicationContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState)
	{
		caseId=getArguments().getString(Parameters.CASE_ID);
		contentView=inflater.inflate(R.layout.fragment_mineralhc_edite, container,false);
		viewPager=(ViewPager)contentView
				.findViewById(R.id.fragment_inspection_edite_viewpager);
		tabView1=(TextView)contentView.findViewById(R.id.text1);
		tabView2=(TextView)contentView.findViewById(R.id.text2);
//		tabView3=(TextView)contentView.findViewById(R.id.text3);
		image=(ImageView)contentView.findViewById(R.id.iamge);
		tabView1.setOnClickListener(tabClickListener);
		tabView2.setOnClickListener(tabClickListener);
//		tabView3.setOnClickListener(tabClickListener);
		viewPager.setOnPageChangeListener(new MyPageListener());
		
		int screenW=getResources().getDisplayMetrics().widthPixels;
		width=BitmapFactory.decodeResource(getResources(), R.mipmap.mm)
				.getWidth();
		moveX=(screenW/2 -width)/2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(moveX, 0);
		image.setImageMatrix(matrix); // 设置动画初始位置
		initFragment();
		return contentView;
		
	}
	
	OnClickListener tabClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.text1:
				viewPager.setCurrentItem(0);
				break;
			case R.id.text2:
				viewPager.setCurrentItem(1);
				break;
//
//			case R.id.text3:
//				viewPager.setCurrentItem(2);
//				break;
			}
		}
	};
	void initFragment() 
	{
		try
		{
			mineralInfoView=new MineralView(context);
			mineralInfoView.setParentActivity(getActivity());
			mineralInfoView.setDataSource(currentUser, caseId, TrackMineralTable.name,TrackMineralAnnexesTable.name);
			
			
			ContentValues mineralValues=DBHelper.getDbHelper(context)
					.doQuery(TrackMineralTable.name, 
							new String[] { TrackMineralTable.field_id},
							MilPatrolTable.field_id + "=?",
						new String[] { caseId });
			
			ContentValues values=DBHelper.getDbHelper(context)
					.doQuery(TrackMineralHcTable.name, 
							new String[] { TrackMineralHcTable.field_id},
							TrackMineralHcTable.field_caseId + "=?",
						new String[] { caseId });
			
//			MineralHcView cview = new MineralHcView(getContext());
//			cview.setParentActivity(parentActivity);
//			cview.setTitle(titleText);
//			cview.setDataSource(
//					values.getAsString(MineralHcTable.field_id),
//					MineralHcTable.name, MilPatrolAnnexesTable.name);
//			historyLayout.addView(cview, layoutParams);
			
				mineralhcView = new MineralHcView(context);
				if(values!=null)
				{
					mineralhcView.noneTitle();
					mineralhcView.setParentActivity(getActivity());
					
					mineralhcView.setDataSource(values.getAsString(MineralHcTable.field_id),
							TrackMineralHcTable.name,TrackMineralAnnexesTable.name);
				}
				else
				{
					mineralhcView.setTitle("无核查记录！");
				}

			viewList.add(mineralhcView);
			titleList.add("处理结果1");
			
			viewList.add(mineralInfoView);
			titleList.add("上报信息");

			pagerAdapter = new MineralHcPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
			
			//mineralhcView
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	
	class MyPageListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			int x = moveX * 2 + width; // 从第一个到第二个view，粗的下划线的偏移量
			Log.v("index的值为:", index + "");
			Log.v("arg0的值为:", arg0 + "");
			Animation animation = new TranslateAnimation(x * index, x * arg0,
					0, 0);
			index = arg0;

			animation.setFillAfter(true); // 设置动画停止在结束位置
			animation.setDuration(300); // 设置动画时间
			image.startAnimation(animation); // 启动动画
		}
	}
	
	
	



	class MineralHcPagerAdapter extends PagerAdapter {
		List<View> viewList;
		List<String> viewTitle;

		public MineralHcPagerAdapter(List<View> viewList,
				List<String> viewTitle) {
			this.viewList = viewList;
			this.viewTitle = viewTitle;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			return viewList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return viewTitle.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
