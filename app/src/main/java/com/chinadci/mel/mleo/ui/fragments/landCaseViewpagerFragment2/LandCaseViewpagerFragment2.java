package com.chinadci.mel.mleo.ui.fragments.landCaseViewpagerFragment2;

import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ui.fragments.ContentFragment;
import com.chinadci.mel.mleo.ui.views.ViewPagerCompat;
/**
 * 
* @ClassName LandCaseViewpagerFragment 
 */
public class LandCaseViewpagerFragment2 extends ContentFragment {
	protected List<View> viewList = new ArrayList<View>();
	protected List<String> titleList = new ArrayList<String>();
	protected View contentView;
	protected ViewPagerCompat viewPager;
	protected ImageView image;
	protected TextView tabView1;
	protected TextView tabView2;
	protected TextView tabView3;
	protected int moveX; // 导航下面横线偏移宽度
	protected int width; // 导航下面比较粗的线的宽度
	protected int index; // 当前第一个view
	protected PagerAdapter pagerAdapter;
	
	protected OnPageChangeListener myPageListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	/**
	 * 
	 * @Title initViewpager
	 * @Description 初始Viewpager内容 void
	 */
	protected void initViewpager() {
		try {
			if (contentView != null) {
				viewPager = (ViewPagerCompat) contentView
						.findViewById(R.id.fragment_case_viewpager_pager);
				tabView1 = (TextView) contentView
						.findViewById(R.id.fragment_case_viewpager_tab1);
				tabView2 = (TextView) contentView
						.findViewById(R.id.fragment_case_viewpager_tab2);
				tabView3 = (TextView) contentView
						.findViewById(R.id.fragment_case_viewpager_tab3);
				image = (ImageView) contentView
						.findViewById(R.id.fragment_case_viewpager_img);
				tabView1.setOnClickListener(tabClickListener);
				tabView2.setOnClickListener(tabClickListener);
				tabView3.setOnClickListener(tabClickListener);
				viewPager.setOnPageChangeListener(myPageListener);

				int screenW = getResources().getDisplayMetrics().widthPixels;
				width = BitmapFactory.decodeResource(getResources(),
						R.mipmap.mm).getWidth();
				moveX = (screenW / 3 - width) / 2;
				Matrix matrix = new Matrix();
				matrix.postTranslate(moveX, 0);
				image.setImageMatrix(matrix); // 设置动画初始位置
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnClickListener tabClickListener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.fragment_case_viewpager_tab1:
				viewPager.setCurrentItem(0);
				break;
			case R.id.fragment_case_viewpager_tab2:
				viewPager.setCurrentItem(1);
				break;
			case R.id.fragment_case_viewpager_tab3:
				viewPager.setCurrentItem(2);
				break;
			}
		}
	};
}
