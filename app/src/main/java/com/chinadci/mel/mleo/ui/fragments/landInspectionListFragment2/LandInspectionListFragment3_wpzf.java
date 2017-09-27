package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.chinadci.mel.mleo.ui.fragments.data.model.WpzfAjNum;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetPreSubNumTask_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetPreWpzfAjNumTask;
import com.chinadci.mel.mleo.ui.views.BadgeView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView.IXListViewListener;
import com.chinadci.mel.mleo.utils.NetWorkUtils;

/**
 * 土地执法》案件核查（快速处置）列表3
 */
public class LandInspectionListFragment3_wpzf extends BaseV4Fragment4Content implements IXListViewListener {
	private String aj_id;
	
	private View mView;
	private XListView mListView;
	private CommonAdapter<XZQHNum_Wpzf> mAdapter;
	private Handler mHandler;
	
	public ArrayList<String> patchList;
	
	private List<XZQHNum_Wpzf> mDatas = new ArrayList<XZQHNum_Wpzf>();

	private RadioGroup rg;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	
	private String myCurenntAjKey = "1";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fg_land3, container, false);
		initView();
		initData(null,null,null);
		return mView;
	}

	private void initView() {
		mListView = (XListView) mView.findViewById(R.id.land_xListView);
		rg = (RadioGroup) mView.findViewById(R.id.Rbtn2SelectDkType);
        rb1 = (RadioButton) mView.findViewById(R.id.Rbtn2SelectDkType1);
        rb2 = (RadioButton) mView.findViewById(R.id.Rbtn2SelectDkType2);
        rb3 = (RadioButton) mView.findViewById(R.id.Rbtn2SelectDkType3);
        rb1.setChecked(true);
        //delete teng.guo
        /*
        rb2.setChecked(false);
        rb3.setChecked(false);*/
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg0.getCheckedRadioButtonId()) {
				case R.id.Rbtn2SelectDkType1:
					//从Activity中拿值
					getList("1",((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode());			//modify teng.guo
					break;
				case R.id.Rbtn2SelectDkType2:
					getList("2",((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode());				//modify teng.guo
					break;
				case R.id.Rbtn2SelectDkType3:
					getList("3",((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode());			//modify teng.guo
					break;
				}
			}
		});
	}

	private void initData(final String patch,final String aj_year,final String aj_xzqbm) {
		mDatas.clear();
		aj_id = "401";
		activityHandle.replaceTitle("卫片执法");
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
			.show();
			rb1.setText("未办理("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",currentUser)+")");
			rb2.setText("待审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",currentUser)+")");
			rb3.setText("已审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",currentUser)+")");
			mDatas = DbUtil.getXZQHNumByUserAndAjAndAjKey(context, currentUser, aj_id,myCurenntAjKey);
			mAdapter = new CommonAdapter<XZQHNum_Wpzf>(mView.getContext(), mDatas,R.layout.list_item_clip_page1) {
				@Override
				public void convert(ViewHolder holder, XZQHNum_Wpzf item, int position) {
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
			//TODO 2017 03 15
			GetPreWpzfAjNumTask task11 = new GetPreWpzfAjNumTask(getActivity(), new TaskResultHandler<List<WpzfAjNum>>() {
				public void resultHander(List<WpzfAjNum> result) {
					if(result!=null&&result.size()>0){
					}else{
						Toast toast = Toast.makeText(context,"网络连接失败,读取缓存数据中...",
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					rb1.setText("未办理("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",currentUser)+")");
					rb2.setText("待审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",currentUser)+")");
					rb3.setText("已审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",currentUser)+")");
				}
			});
			task11.execute(currentUser,patch,aj_year,aj_xzqbm);		//modify teng.guo
			GetPreSubNumTask_Wpzf task1 = new GetPreSubNumTask_Wpzf(context, new TaskResultHandler<List<XZQHNum_Wpzf>>() {
				public void resultHander(List<XZQHNum_Wpzf> result) {
					if (alertDialog != null && alertDialog.isShowing()){
						alertDialog.dismiss();
					}
					if (result != null) {
						if (result.size() > 0) {
							mDatas = result;
							mAdapter = new CommonAdapter<XZQHNum_Wpzf>(mView.getContext(), mDatas,
									R.layout.list_item_clip_page1) {
								@Override
								public void convert(ViewHolder holder, XZQHNum_Wpzf item, int position) {
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
						rb1.setText("未办理("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",currentUser)+")");
						rb2.setText("待审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",currentUser)+")");
						rb3.setText("已审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",currentUser)+")");
						mDatas = DbUtil.getXZQHNumByUserAndAjAndAjKey(context, currentUser, aj_id,myCurenntAjKey);
						mAdapter = new CommonAdapter<XZQHNum_Wpzf>(mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, XZQHNum_Wpzf item, int position) {
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
			task1.execute(currentUser,aj_id,myCurenntAjKey,patch,aj_year,aj_xzqbm);		//modify teng.guo
		}
		mListView.setPullLoadEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
		mHandler = new Handler();
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Intent intent = new Intent(getActivity(), ModuleRealizeActivity.class);
				intent.putExtra(ModuleActivity.TAG_MODULE_ID, 210242);
				XZQHNum_Wpzf xm= (XZQHNum_Wpzf) mAdapter.getItem(position-1);
				intent.putExtra("aj_id", aj_id);
				intent.putExtra("xzqh_id", xm.getId());
				intent.putExtra("title", xm.getName());
				intent.putExtra("myCurenntAjKey", myCurenntAjKey);
				intent.putExtra("code", xm.getXzqudm());
				intent.putExtra("patch",((ModuleActivity)getActivity()).getPatch());	
				intent.putExtra("aj_year",((ModuleActivity)getActivity()).getAjYear());		//add teng.guo
				intent.putExtra("aj_xzqbm",((ModuleActivity)getActivity()).getXzqbmCode());
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
	
	@Override
	public void onStart(){
		super.onStart();
		Log.i("ydzf","now is start LandInspectionListFragment3_wpzf");
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	// 加载更多
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				getList(myCurenntAjKey,null,null,null);		//modify teng.guo	下拉刷新恢复原始结果
				onLoad();
			}
		}, 2000);
	}

	private CommonAdapter<XZQHNum_Wpzf> getList(final String ajKey,final String patch,String aj_year,String aj_xzqbm) {
		myCurenntAjKey = ajKey;
		mDatas.clear();
		if(mAdapter!=null){
			mAdapter.updateData(mDatas);
		}
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
			.show();
			rb1.setText("未办理("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",currentUser)+")");
			rb2.setText("待审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",currentUser)+")");
			rb3.setText("已审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",currentUser)+")");
			mDatas = DbUtil.getXZQHNumByUserAndAjAndAjKey(context, currentUser, aj_id,ajKey);
			mAdapter = new CommonAdapter<XZQHNum_Wpzf>(mView.getContext(), mDatas,
					R.layout.list_item_clip_page1) {
				@Override
				public void convert(ViewHolder holder, XZQHNum_Wpzf item, int position) {
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
			GetPreWpzfAjNumTask task11 = new GetPreWpzfAjNumTask(getActivity(), new TaskResultHandler<List<WpzfAjNum>>() {
				public void resultHander(List<WpzfAjNum> result) {
					if(result!=null&&result.size()>0){
					}else{
						Toast toast = Toast.makeText(context,"网络连接失败,读取缓存数据中...",
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					rb1.setText("未办理("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",currentUser)+")");
					rb2.setText("待审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",currentUser)+")");
					rb3.setText("已审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",currentUser)+")");
				}
			});
			task11.execute(currentUser,patch,aj_year,aj_xzqbm);		//modify teng.guo
			GetPreSubNumTask_Wpzf task1 = new GetPreSubNumTask_Wpzf(context, new TaskResultHandler<List<XZQHNum_Wpzf>>() {
				public void resultHander(List<XZQHNum_Wpzf> result) {
					if (alertDialog != null && alertDialog.isShowing()){
						alertDialog.dismiss();
					}
					if (result != null) {
						if (result.size() > 0) {
							mDatas = result;
							mAdapter = new CommonAdapter<XZQHNum_Wpzf>(mView.getContext(), mDatas,
									R.layout.list_item_clip_page1) {
								@Override
								public void convert(ViewHolder holder, XZQHNum_Wpzf item, int position) {
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
						rb1.setText("未办理("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",currentUser)+")");
						rb2.setText("待审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",currentUser)+")");
						rb3.setText("已审核("+DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",currentUser)+")");
						mDatas = DbUtil.getXZQHNumByUserAndAjAndAjKey(context, currentUser, aj_id,ajKey);
						mAdapter = new CommonAdapter<XZQHNum_Wpzf>(mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, XZQHNum_Wpzf item, int position) {
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
			task1.execute(currentUser,aj_id,ajKey,patch,aj_year,aj_xzqbm);		//modify teng.guo
		}
		return mAdapter;
	}
	
	private void showViewAnimation(View v) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
		v.startAnimation(anim);
	}
	
	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {
				getList(myCurenntAjKey,((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode());	//modify teng.guo
				onLoad();
			}
		}, 500);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		refreshUi();
	}
	
}
