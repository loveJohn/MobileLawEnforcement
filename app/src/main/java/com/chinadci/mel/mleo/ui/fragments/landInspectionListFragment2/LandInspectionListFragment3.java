package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.adapters.CommonAdapter;
import com.chinadci.mel.mleo.ui.adapters.ViewHolder;
import com.chinadci.mel.mleo.ui.fragments.base.BaseV4Fragment4Content;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetPreSubNumTask;
import com.chinadci.mel.mleo.ui.views.BadgeView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView.IXListViewListener;
import com.chinadci.mel.mleo.utils.NetWorkUtils;

/**
 * 土地执法》案件核查（快速处置）列表3
 */
public class LandInspectionListFragment3 extends BaseV4Fragment4Content
		implements IXListViewListener {
	private String aj_id;
	
	private View mView;
	private XListView mListView;
	@SuppressWarnings("rawtypes")
	private CommonAdapter mAdapter;
	private Handler mHandler;
	
	private List<XZQHNum> mDatas = new ArrayList<XZQHNum>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fg_land2, container, false);
		initView();
		initData();
		return mView;
	}

	private void initView() {
		mListView = (XListView) mView.findViewById(R.id.land_xListView);
	}

	private void initData() {
		mDatas.clear();
		Intent intent = getActivity().getIntent();
		aj_id = intent.getStringExtra("aj_id");
		activityHandle.replaceTitle(intent.getStringExtra("title"));
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
			.show();
			mDatas = DbUtil.getXZQHNumByUserAndAj(context, currentUser, aj_id);
			mAdapter = new CommonAdapter<XZQHNum>(mView.getContext(), mDatas,
					R.layout.list_item_clip_page1) {
				@Override
				public void convert(ViewHolder holder, XZQHNum item, int position) {
					((TextView) holder.getView(R.id.text_name)).setText(item.getName());
					BadgeView mBadge = ((BadgeView) holder.getView(R.id.bagde));
					mBadge.setVisibility(View.VISIBLE);
					showViewAnimation(mBadge);
					mBadge.setBadgeCount(item.getNum());
				}
			};
			mListView.setAdapter(mAdapter);
		} else {
			final AlertDialog alertDialog;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取区域详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			GetPreSubNumTask task1 = new GetPreSubNumTask(context, new TaskResultHandler<List<XZQHNum>>() {
				public void resultHander(List<XZQHNum> result) {
					if (alertDialog != null && alertDialog.isShowing()){
						alertDialog.dismiss();
					}
					if (result != null) {
						if (result.size() > 0) {
							mDatas = result;
							mAdapter = new CommonAdapter<XZQHNum>(mView.getContext(), mDatas,
									R.layout.list_item_clip_page1) {
								@Override
								public void convert(ViewHolder holder, XZQHNum item, int position) {
									((TextView) holder.getView(R.id.text_name)).setText(item.getName());
									BadgeView mBadge = ((BadgeView) holder.getView(R.id.bagde));
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setBadgeCount(item.getNum());
								}
							};
							mListView.setAdapter(mAdapter);
						}
					}else{
						showToastMsg(mView.getContext(), "网络连接失败，读取缓存中...");
						mDatas = DbUtil.getXZQHNumByUserAndAj(context, currentUser, aj_id);
						mAdapter = new CommonAdapter<XZQHNum>(mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, XZQHNum item, int position) {
								((TextView) holder.getView(R.id.text_name)).setText(item.getName());
								BadgeView mBadge = ((BadgeView) holder.getView(R.id.bagde));
								mBadge.setVisibility(View.VISIBLE);
								showViewAnimation(mBadge);
								mBadge.setBadgeCount(item.getNum());
							}
						};
						mListView.setAdapter(mAdapter);
					}
				}
			});
			task1.execute(currentUser,aj_id);
		}
		mListView.setPullLoadEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
		mHandler = new Handler();
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Intent intent = new Intent(getActivity(), ModuleRealizeActivity.class);
				intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21024);
				XZQHNum xm= (XZQHNum) mAdapter.getItem(position-1);
				intent.putExtra("aj_id", aj_id);
				intent.putExtra("xzqh_id", xm.getId());
				intent.putExtra("title", xm.getName());
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
	}

	/** 停止刷新， */
	public void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}

	// 刷新
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				getList();
				onLoad();
			}
		}, 2000);
	}

	// 加载更多
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				getList();
				onLoad();
			}
		}, 2000);
	}

	@SuppressWarnings("rawtypes")
	private CommonAdapter getList() {
		mDatas.clear();
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
			.show();
			mDatas = DbUtil.getXZQHNumByUserAndAj(context, currentUser, aj_id);
			mAdapter = new CommonAdapter<XZQHNum>(mView.getContext(), mDatas,
					R.layout.list_item_clip_page1) {
				@Override
				public void convert(ViewHolder holder, XZQHNum item, int position) {
					((TextView) holder.getView(R.id.text_name)).setText(item.getName());
					BadgeView mBadge = ((BadgeView) holder.getView(R.id.bagde));
					mBadge.setVisibility(View.VISIBLE);
					showViewAnimation(mBadge);
					mBadge.setBadgeCount(item.getNum());
				}
			};
			mAdapter.notifyDataSetChanged();
			mListView.setAdapter(mAdapter);
		} else {
			final AlertDialog alertDialog;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取区域详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			GetPreSubNumTask task1 = new GetPreSubNumTask(context, new TaskResultHandler<List<XZQHNum>>() {
				public void resultHander(List<XZQHNum> result) {
					if (alertDialog != null && alertDialog.isShowing()){
						alertDialog.dismiss();
					}
					if (result != null) {
						if (result.size() > 0) {
							mDatas = result;
							mAdapter = new CommonAdapter<XZQHNum>(mView.getContext(), mDatas,
									R.layout.list_item_clip_page1) {
								@Override
								public void convert(ViewHolder holder, XZQHNum item, int position) {
									((TextView) holder.getView(R.id.text_name)).setText(item.getName());
									BadgeView mBadge = ((BadgeView) holder.getView(R.id.bagde));
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setBadgeCount(item.getNum());
								}
							};
							mAdapter.notifyDataSetChanged();
							mListView.setAdapter(mAdapter);
						}
					}else{
						showToastMsg(mView.getContext(), "网络连接失败，读取缓存中...");
						mDatas = DbUtil.getXZQHNumByUserAndAj(context, currentUser, aj_id);
						mAdapter = new CommonAdapter<XZQHNum>(mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, XZQHNum item, int position) {
								((TextView) holder.getView(R.id.text_name)).setText(item.getName());
								BadgeView mBadge = ((BadgeView) holder.getView(R.id.bagde));
								mBadge.setVisibility(View.VISIBLE);
								showViewAnimation(mBadge);
								mBadge.setBadgeCount(item.getNum());
							}
						};
						mAdapter.notifyDataSetChanged();
						mListView.setAdapter(mAdapter);
					}
				}
			});
			task1.execute(currentUser,aj_id);
		}
		return mAdapter;
	}
	
	private void showViewAnimation(View v) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
		v.startAnimation(anim);
	}
}
