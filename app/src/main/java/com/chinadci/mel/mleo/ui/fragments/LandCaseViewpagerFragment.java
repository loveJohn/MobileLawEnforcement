package com.chinadci.mel.mleo.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinadci.mel.R;

/**
 * 
* @ClassName LandCaseViewpagerFragment 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:47:00 
*
 */
public class LandCaseViewpagerFragment extends ContentFragment {
	protected List<View> viewList = new ArrayList<View>();
	protected List<String> titleList = new ArrayList<String>();
	protected View contentView;
	protected ViewPager viewPager;
	protected ImageView image;
	protected TextView tabView1;
	protected TextView tabView2;
	protected TextView tabView3;
	protected int moveX; // 导航下面横线偏移宽度
	protected int width; // 导航下面比较粗的线的宽度
	protected int index; // 当前第一个view
	protected ViewPagerAdapter pagerAdapter;

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
				viewPager = (ViewPager) contentView
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
				viewPager.setOnPageChangeListener(new MyPageListener());

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

	protected OnClickListener tabClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
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

	protected class ViewPagerAdapter extends PagerAdapter {
		List<View> viewList;
		List<String> viewTitle;

		public ViewPagerAdapter(List<View> viewList, List<String> viewTitle) {
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

	protected class MyPageListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			int x = moveX * 2 + width;
			Animation animation = new TranslateAnimation(x * index, x * arg0,
					0, 0);
			index = arg0;
			animation.setFillAfter(true); // 设置动画停止在结束位置
			animation.setDuration(300); // 设置动画时间
			image.startAnimation(animation); // 启动动画
		}
	}
}
