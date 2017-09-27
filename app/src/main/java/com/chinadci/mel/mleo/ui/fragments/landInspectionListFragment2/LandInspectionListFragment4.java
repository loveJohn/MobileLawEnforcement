package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectTable;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.activities.ModuleActivity;
import com.chinadci.mel.mleo.ui.activities.ModuleRealizeActivity;
import com.chinadci.mel.mleo.ui.adapters.CommonAdapter;
import com.chinadci.mel.mleo.ui.adapters.ViewHolder;
import com.chinadci.mel.mleo.ui.fragments.base.BaseV4Fragment4Content;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask2;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetSubNumTask;
import com.chinadci.mel.mleo.ui.views.BadgeView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView.IXListViewListener;
import com.chinadci.mel.mleo.utils.CaseUtils;
import com.chinadci.mel.mleo.utils.NetWorkUtils;

/**
 * 土地执法》案件核查（快速处置）列表4
 */
public class LandInspectionListFragment4 extends BaseV4Fragment4Content
		implements IXListViewListener {
	private String aj_id;
	private String xzqh_id;
	private View mView;
	private XListView mListView;
	@SuppressWarnings("rawtypes")
	private CommonAdapter mAdapter;
	private Handler mHandler;

	private List<Object> mDatas = new ArrayList<Object>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fg_land2, container, false);
		initView();
		return mView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initView() {
		mListView = (XListView) mView.findViewById(R.id.land_xListView);
	}

	private void initData() {
		mDatas.clear();
		Intent intent = getActivity().getIntent();
		aj_id = intent.getStringExtra("aj_id");
		xzqh_id = intent.getStringExtra("xzqh_id");
		activityHandle.replaceTitle(intent.getStringExtra("title"));
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
					.show();
			mDatas = DbUtil.getSimpleAjByXzquAndAj(context, xzqh_id, aj_id);
			if (mDatas.size() > 0) {
				if (mDatas.get(0) instanceof SimpleAj) {
					mAdapter = new CommonAdapter<Object>(mView.getContext(),
							mDatas, R.layout.list_item_clip_page2) {
						@Override
						public void convert(ViewHolder holder,
								final Object item, int position) {
							if (item instanceof SimpleAj) {
								if(Boolean.parseBoolean(((SimpleAj) item).getIsSave())){
									((ViewGroup) holder
											.getView(R.id.ll_root))
											.setBackgroundResource(R.drawable.timeline_content2);
								}else{
									((ViewGroup) holder
											.getView(R.id.ll_root))
											.setBackgroundResource(R.drawable.timeline_content);
								}
								((TextView) holder
										.getView(R.id.textView_project_bh))
										.setText(((SimpleAj) item).getBh());
								((TextView) holder
										.getView(R.id.textView_project_ajly))
										.setText(((SimpleAj) item).getAjly());
								if (((SimpleAj) item).getJcbh() != null
										&& !((SimpleAj) item).getJcbh().equals(
												"")) {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.VISIBLE);
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setText("监测编号："
													+ ((SimpleAj) item)
															.getJcbh());
								} else {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.GONE);
								}
								((TextView) holder.getView(R.id.textView_xxdz))
										.setText(((SimpleAj) item).getXxdz());
								((TextView) holder.getView(R.id.textView_sj))
										.setText(((SimpleAj) item).getSj());
							}
						}
					};
					mListView.setAdapter(mAdapter);
				}
			} else {
				mDatas = DbUtil.getXZQHNumByXzquAndAj(context, xzqh_id, aj_id);
				if (mDatas.size() > 0) {
					if (mDatas.get(0) instanceof XZQHNum) {
						mAdapter = new CommonAdapter<Object>(
								mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, Object item,
									int position) {
								if (item instanceof XZQHNum) {
									((TextView) holder.getView(R.id.text_name))
											.setText(((XZQHNum) item).getName());
									BadgeView mBadge = ((BadgeView) holder
											.getView(R.id.bagde));
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setBadgeCount(((XZQHNum) item)
											.getNum());
								}
							}
						};
						mListView.setAdapter(mAdapter);
					}
				} else {
					Toast.makeText(getActivity(), "网络无连接且无缓存数据...",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		} else {
			final AlertDialog alertDialog;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取区域详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			GetSubNumTask task1 = new GetSubNumTask(context,
					new TaskResultHandler<List<Object>>() {
						public void resultHander(List<Object> result) {
							if (alertDialog != null && alertDialog.isShowing()) {
								alertDialog.dismiss();
							}
							if (result != null) {
								if (result.size() > 0) {
									if (result.get(0) instanceof XZQHNum) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page1) {
											@Override
											public void convert(
													ViewHolder holder,
													Object item, int position) {
												if (item instanceof XZQHNum) {
													((TextView) holder
															.getView(R.id.text_name))
															.setText(((XZQHNum) item)
																	.getName());
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													mBadge.setVisibility(View.VISIBLE);
													showViewAnimation(mBadge);
													mBadge.setBadgeCount(((XZQHNum) item)
															.getNum());
												}
											}
										};
										mListView.setAdapter(mAdapter);
									} else if (result.get(0) instanceof SimpleAj) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page2) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj) {
													if(Boolean.parseBoolean(((SimpleAj) item).getIsSave())){
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content2);
													}else{
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj) item)
																	.getAjly());
													if (((SimpleAj) item)
															.getJcbh() != null
															&& !((SimpleAj) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj) item)
																	.getSj());
												}
											}
										};
										mListView.setAdapter(mAdapter);
									} else {
									}
								}
							} else {
								showToastMsg(mView.getContext(),
										"网络连接失败，读取缓存中...");
								mDatas = DbUtil.getSimpleAjByXzquAndAj(context,
										xzqh_id, aj_id);
								if (mDatas.size() > 0) {
									if (mDatas.get(0) instanceof SimpleAj) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), mDatas,
												R.layout.list_item_clip_page2) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj) {
													if(Boolean.parseBoolean(((SimpleAj) item).getIsSave())){
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content2);
													}else{
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj) item)
																	.getAjly());
													if (((SimpleAj) item)
															.getJcbh() != null
															&& !((SimpleAj) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj) item)
																	.getSj());
												}
											}
										};
										mListView.setAdapter(mAdapter);
									}
								} else {
									mDatas = DbUtil.getXZQHNumByXzquAndAj(
											context, xzqh_id, aj_id);
									if (mDatas.size() > 0) {
										if (mDatas.get(0) instanceof XZQHNum) {
											mAdapter = new CommonAdapter<Object>(
													mView.getContext(),
													mDatas,
													R.layout.list_item_clip_page1) {
												@Override
												public void convert(
														ViewHolder holder,
														Object item,
														int position) {
													if (item instanceof XZQHNum) {
														((TextView) holder
																.getView(R.id.text_name))
																.setText(((XZQHNum) item)
																		.getName());
														BadgeView mBadge = ((BadgeView) holder
																.getView(R.id.bagde));
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setBadgeCount(((XZQHNum) item)
																.getNum());
													}
												}
											};
											mListView.setAdapter(mAdapter);
										}
									} else {
										Toast.makeText(getActivity(),
												"网络无连接且无缓存数据...",
												Toast.LENGTH_SHORT).show();
									}
								}
							}
						}
					});
			task1.execute(xzqh_id, aj_id);
		}
		mListView.setPullLoadEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
		mHandler = new Handler();
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Object obj = (Object) mAdapter.getItem(position - 1);
				if (obj instanceof XZQHNum) {
					Intent intent = new Intent(getActivity(),
							ModuleRealizeActivity.class);
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21024);
					intent.putExtra("aj_id", aj_id);
					intent.putExtra("xzqh_id", ((XZQHNum) obj).getId());
					intent.putExtra("title", ((XZQHNum) obj).getName());
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else if (obj instanceof SimpleAj) {
					if (!((SimpleAj) obj).getAjly().equals("卫片执法")) {
						new inspectionGetTask(context,
								((SimpleAj) obj).getBh(), currentUser).execute(
								((SimpleAj) obj).getBh(),
								((SimpleAj) obj).getHx(),
								((SimpleAj) obj).getHxfxjg(),
								((SimpleAj) obj).getX(),
								((SimpleAj) obj).getY(),
								((SimpleAj) obj).getAjly(),
								((SimpleAj) obj).getXxdz());
					} else {
						new inspectionGetTask2(context, ((SimpleAj) obj)
								.getBh(), currentUser).execute(
								((SimpleAj) obj).getBh(),
								((SimpleAj) obj).getHx(),
								((SimpleAj) obj).getHxfxjg(),
								((SimpleAj) obj).getX(),
								((SimpleAj) obj).getY(),
								((SimpleAj) obj).getAjly(),
								((SimpleAj) obj).getXxdz());
					}
				}
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
			mDatas = DbUtil.getSimpleAjByXzquAndAj(context, xzqh_id, aj_id);
			if (mDatas.size() > 0) {
				if (mDatas.get(0) instanceof SimpleAj) {
					mAdapter = new CommonAdapter<Object>(mView.getContext(),
							mDatas, R.layout.list_item_clip_page2) {
						@Override
						public void convert(ViewHolder holder,
								final Object item, int position) {
							if (item instanceof SimpleAj) {
								if(Boolean.parseBoolean(((SimpleAj) item).getIsSave())){
									((ViewGroup) holder
											.getView(R.id.ll_root))
											.setBackgroundResource(R.drawable.timeline_content2);
								}else{
									((ViewGroup) holder
											.getView(R.id.ll_root))
											.setBackgroundResource(R.drawable.timeline_content);
								}
								((TextView) holder
										.getView(R.id.textView_project_bh))
										.setText(((SimpleAj) item).getBh());
								((TextView) holder
										.getView(R.id.textView_project_ajly))
										.setText(((SimpleAj) item).getAjly());
								if (((SimpleAj) item).getJcbh() != null
										&& !((SimpleAj) item).getJcbh().equals(
												"")) {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.VISIBLE);
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setText("监测编号："
													+ ((SimpleAj) item)
															.getJcbh());
								} else {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.GONE);
								}
								((TextView) holder.getView(R.id.textView_xxdz))
										.setText(((SimpleAj) item).getXxdz());
								((TextView) holder.getView(R.id.textView_sj))
										.setText(((SimpleAj) item).getSj());
							}
						}
					};
					mAdapter.notifyDataSetChanged();
					mListView.setAdapter(mAdapter);
				}
			} else {
				mDatas = DbUtil.getXZQHNumByXzquAndAj(context, xzqh_id, aj_id);
				if (mDatas.size() > 0) {
					if (mDatas.get(0) instanceof XZQHNum) {
						mAdapter = new CommonAdapter<Object>(
								mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, Object item,
									int position) {
								if (item instanceof XZQHNum) {
									((TextView) holder.getView(R.id.text_name))
											.setText(((XZQHNum) item).getName());
									BadgeView mBadge = ((BadgeView) holder
											.getView(R.id.bagde));
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setBadgeCount(((XZQHNum) item)
											.getNum());
								}
							}
						};
						mAdapter.notifyDataSetChanged();
						mListView.setAdapter(mAdapter);
					}
				} else {
					Toast.makeText(getActivity(), "网络无连接且无缓存数据...",
							Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			final AlertDialog alertDialog;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取区域详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			GetSubNumTask task1 = new GetSubNumTask(context,
					new TaskResultHandler<List<Object>>() {
						public void resultHander(List<Object> result) {
							if (alertDialog != null && alertDialog.isShowing()) {
								alertDialog.dismiss();
							}
							if (result != null) {
								if (result.size() > 0) {
									if (result.get(0) instanceof XZQHNum) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page1) {
											@Override
											public void convert(
													ViewHolder holder,
													Object item, int position) {
												if (item instanceof XZQHNum) {
													((TextView) holder
															.getView(R.id.text_name))
															.setText(((XZQHNum) item)
																	.getName());
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													mBadge.setVisibility(View.VISIBLE);
													showViewAnimation(mBadge);
													mBadge.setBadgeCount(((XZQHNum) item)
															.getNum());
												}
											}
										};
										mAdapter.notifyDataSetChanged();
										mListView.setAdapter(mAdapter);
									} else if (result.get(0) instanceof SimpleAj) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page2) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj) {
													if(Boolean.parseBoolean(((SimpleAj) item).getIsSave())){
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content2);
													}else{
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj) item)
																	.getAjly());
													if (((SimpleAj) item)
															.getJcbh() != null
															&& !((SimpleAj) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj) item)
																	.getSj());
												}
											}
										};
										mAdapter.notifyDataSetChanged();
										mListView.setAdapter(mAdapter);
									} else {
									}
								}
							} else {
								showToastMsg(mView.getContext(),
										"网络连接失败，读取缓存中...");
								mDatas = DbUtil.getSimpleAjByXzquAndAj(context,
										xzqh_id, aj_id);
								if (mDatas.size() > 0) {
									if (mDatas.get(0) instanceof SimpleAj) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), mDatas,
												R.layout.list_item_clip_page2) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj) {
													if(Boolean.parseBoolean(((SimpleAj) item).getIsSave())){
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content2);
													}else{
														((ViewGroup) holder
																.getView(R.id.ll_root))
																.setBackgroundResource(R.drawable.timeline_content);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj) item)
																	.getAjly());
													if (((SimpleAj) item)
															.getJcbh() != null
															&& !((SimpleAj) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj) item)
																	.getSj());
												}
											}
										};
										mAdapter.notifyDataSetChanged();
										mListView.setAdapter(mAdapter);
									}
								} else {
									mDatas = DbUtil.getXZQHNumByXzquAndAj(
											context, xzqh_id, aj_id);
									if (mDatas.size() > 0) {
										if (mDatas.get(0) instanceof XZQHNum) {
											mAdapter = new CommonAdapter<Object>(
													mView.getContext(),
													mDatas,
													R.layout.list_item_clip_page1) {
												@Override
												public void convert(
														ViewHolder holder,
														Object item,
														int position) {
													if (item instanceof XZQHNum) {
														((TextView) holder
																.getView(R.id.text_name))
																.setText(((XZQHNum) item)
																		.getName());
														BadgeView mBadge = ((BadgeView) holder
																.getView(R.id.bagde));
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setBadgeCount(((XZQHNum) item)
																.getNum());
													}
												}
											};
											mAdapter.notifyDataSetChanged();
											mListView.setAdapter(mAdapter);
										}
									} else {
										Toast.makeText(getActivity(),
												"网络无连接且无缓存数据...",
												Toast.LENGTH_SHORT).show();
									}
								}
							}
						}
					});
			task1.execute(xzqh_id, aj_id);
		}
		return mAdapter;
	}

	private void showViewAnimation(View v) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);
		v.startAnimation(anim);
	}

	class inspectionGetTask extends AsyncTask<Object, Integer, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String user;
		AlertDialog alertDialog;

		private String bh;
		private String redline;
		private String redline_result;
		private double x;
		private double y;
		private String ajly;
		private String dz;

		public inspectionGetTask(Context c, String caseId, String user) {
			this.context = c;
			this.caseId = caseId;
			this.user = user;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取案件详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@SuppressWarnings("unused")
		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				bh = (String) params[0];
				redline = (String) params[1];
				redline_result = (String) params[2];
				x = (Double) params[3];
				y = (Double) params[4];
				ajly = (String) params[5];
				dz = (String) params[6];
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_inspection_service))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_inspection_service))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString();
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					DbUtil.deleteINSPECTIONGETTASKDbDatasByBh(context, bh);
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("inspection_search_entiry", entiryString);
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						caseId = CaseUtils.getInstance().storeCaseFulldata(
								context, currentUser, backJson,
								InspectionCaseTable.name,
								CaseAnnexesTable.name, CasePatrolTable.name,
								CaseInspectTable.name);
						//2017 02 15
						try {
							JSONArray arraysss = backJson.getJSONArray("patrols");
							if(arraysss.length()>0){
								for(int kk = 0;kk<arraysss.length();kk++){
									JSONObject objjj = arraysss.getJSONObject(kk);
									String id = objjj.optString("id");
									String fxjg = objjj.optString("fxjg");
									DbUtil.deletePatrolsById(context, id);
									DbUtil.insertPatrols(context, id, fxjg);
								}	
							}
						} catch (Exception e1) {
						}
						
						// 保存核查信息
						JSONObject insObject;
						try {
							insObject = backJson.getJSONObject("inspection");
						} catch (Exception e) {
							insObject = null;
						}
						try {
							if (insObject == null) {
								ContentValues inspectValues = CaseUtils
										.getInstance()
										.caseJson2ImspectionContentValues(
												backJson.getJSONObject("case"));
								int delInspectCount = DBHelper
										.getDbHelper(context)
										.delete(CaseInspectTable.name,
												new StringBuffer(
														InspectTable.field_caseId)
														.append("=?")
														.toString(),
												new String[] { inspectValues
														.getAsString(InspectTable.field_caseId) });
								inspectValues.put(InspectTable.field_status, 0);
								DBHelper.getDbHelper(context).insert(
										CaseInspectTable.name, inspectValues);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							JSONObject situationObject = backJson
									.getJSONObject("situation");
							if (situationObject != null
									&& situationObject != JSONObject.NULL) {
								CaseUtils.getInstance().storeCaseSituation(
										context, caseId, situationObject);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						publishProgress(-1);
						msg = caseId;
						DbUtil.insertINSPECTIONGETTASKDbDatas(context, bh,
								redline, x + "", y + "", ajly, dz, entiryString);
						return true;
					} else {
						publishProgress(-1);
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			publishProgress(-1);
			msg = "获取案件详情发生异常";
			return false;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			try {
				int i = values[0];
				if (i == -1) {
					if (alertDialog != null && alertDialog.isShowing())
						alertDialog.dismiss();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onProgressUpdate(values);
		}

		@SuppressWarnings("unused")
		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				if (result) {
					Intent intent = new Intent(getActivity(),
							ModuleRealizeActivity.class);
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21025);
					intent.putExtra(Parameters.CASE_ID, msg);
					intent.putExtra("title", bh);
					intent.putExtra("hx", redline);
					intent.putExtra("hx_result", redline_result);
					intent.putExtra("x", x);
					intent.putExtra("y", y);
					intent.putExtra("ajly", ajly);
					intent.putExtra("dz", dz);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else {
					Toast toast = Toast.makeText(context, msg + ",读取缓存数据中...",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					InspectionGetTask ist = DbUtil.getInspectionGetTaskByBh(
							context, bh);
					if (ist != null) {
						try {
							String entiryString = ist.getEntiryString();
							JSONObject backJson = new JSONObject(entiryString);
							boolean succeed = backJson.getBoolean("succeed");
							if (succeed) {
								caseId = CaseUtils.getInstance()
										.storeCaseFulldata(context,
												currentUser, backJson,
												InspectionCaseTable.name,
												CaseAnnexesTable.name,
												CasePatrolTable.name,
												CaseInspectTable.name);
								
								//2017 02 15
								try {
									JSONArray arraysss = backJson.getJSONArray("patrols");
									if(arraysss.length()>0){
										for(int kk = 0;kk<arraysss.length();kk++){
											JSONObject objjj = arraysss.getJSONObject(kk);
											String id = objjj.optString("id");
											String fxjg = objjj.optString("fxjg");
											DbUtil.deletePatrolsById(context, id);
											DbUtil.insertPatrols(context, id, fxjg);
										}	
									}
								} catch (Exception e1) {
								}
								
								// 保存核查信息
								JSONObject insObject;
								try {
									insObject = backJson
											.getJSONObject("inspection");
								} catch (Exception e) {
									insObject = null;
								}
								try {
									if (insObject == null) {
										ContentValues inspectValues = CaseUtils
												.getInstance()
												.caseJson2ImspectionContentValues(
														backJson.getJSONObject("case"));
										int delInspectCount = DBHelper
												.getDbHelper(context)
												.delete(CaseInspectTable.name,
														new StringBuffer(
																InspectTable.field_caseId)
																.append("=?")
																.toString(),
														new String[] { inspectValues
																.getAsString(InspectTable.field_caseId) });
										inspectValues.put(
												InspectTable.field_status, 0);
										DBHelper.getDbHelper(context).insert(
												CaseInspectTable.name,
												inspectValues);
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									JSONObject situationObject = backJson
											.getJSONObject("situation");
									if (situationObject != null
											&& situationObject != JSONObject.NULL) {
										CaseUtils
												.getInstance()
												.storeCaseSituation(context,
														caseId, situationObject);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							return;
						}
						Intent intent = new Intent(getActivity(),
								ModuleRealizeActivity.class);
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21025);
						intent.putExtra(Parameters.CASE_ID, caseId);
						intent.putExtra("title", bh);
						intent.putExtra("hx", redline);
						intent.putExtra("hx_result", redline_result);
						intent.putExtra("x", x);
						intent.putExtra("y", y);
						intent.putExtra("ajly", ajly);
						intent.putExtra("dz", dz);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.slide_in_right, R.anim.slide_out_left);
					} else {
						Toast toast2 = Toast.makeText(context,
								"网络连接失败且缓存数据不存在", Toast.LENGTH_SHORT);
						toast2.setGravity(Gravity.CENTER, 0, 0);
						toast2.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class inspectionGetTask2 extends AsyncTask<Object, Integer, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String user;
		AlertDialog alertDialog;

		private String bh;
		private String redline;
		private String redline_result;
		private double x;
		private double y;
		private String ajly;
		private String dz;

		private String jcbh;
		private String jcmj;
		private String zygdmj;
		private String xfsj;

		public inspectionGetTask2(Context c, String caseId, String user) {
			this.context = c;
			this.caseId = caseId;
			this.user = user;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取案件详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@SuppressWarnings("unused")
		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				bh = (String) params[0];
				redline = (String) params[1];
				redline_result = (String) params[2];
				x = (Double) params[3];
				y = (Double) params[4];
				ajly = (String) params[5];
				dz = (String) params[6];
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_inspection_service))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_inspection_service))
						.append("?user=").append(user).append("&caseId=")
						.append(caseId).toString();
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					DbUtil.deleteINSPECTIONGETTASK2DbDatasByBh(context, bh);
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("inspection_search_entiry", entiryString);
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						caseId = CaseUtils.getInstance().storeCaseFulldata(
								context, currentUser, backJson,
								InspectionCaseTable.name,
								CaseAnnexesTable.name, CasePatrolTable.name,
								CaseInspectTable.name);
						jcbh = backJson.getJSONObject("case").getString("jcbh");
						jcmj = backJson.getJSONObject("case").getString("jcmj")
								+ " ㎡";
						zygdmj=backJson.getJSONObject("case").getString("zygdmj")
								+ " ㎡";
						xfsj = backJson.getJSONObject("case").getString("xfsj");
						
						//2017 02 15
						try {
							JSONArray arraysss = backJson.getJSONArray("patrols");
							if(arraysss.length()>0){
								for(int kk = 0;kk<arraysss.length();kk++){
									JSONObject objjj = arraysss.getJSONObject(kk);
									String id = objjj.optString("id");
									String fxjg = objjj.optString("fxjg");
									DbUtil.deletePatrolsById(context, id);
									DbUtil.insertPatrols(context, id, fxjg);
								}	
							}
						} catch (Exception e1) {
						}
						
						//2017 03 14
						try {
							JSONObject abbb = backJson.getJSONObject("situation");
							if(abbb != null&& abbb != JSONObject.NULL){
								JSONArray arraysss = abbb.getJSONArray("clqk_new");
								if(arraysss!=null&&arraysss.length()>0){
									for(int kk = 0;kk<arraysss.length();kk++){
										JSONObject objjj = arraysss.getJSONObject(kk);
										String key = objjj.optString("key");
										String value = objjj.optString("value");
										String parent = null;
										DbUtil.deleteclqk_nowByKey(context, key);
										DbUtil.insertclqk_now(context, key, value, parent);
										if(objjj.has("sub")){
											JSONArray arraysss1 = objjj.getJSONArray("sub");
											if(arraysss1!=null&&arraysss1.length()>0){
												for(int kkk = 0;kkk<arraysss1.length();kkk++){
													JSONObject objjj1 = arraysss1.getJSONObject(kkk);
													String key1 = objjj1.optString("key");
													String value1 = objjj1.optString("value");
													String parent1 = key;
													DbUtil.deleteclqk_nowByKey(context, key1);
													DbUtil.insertclqk_now(context, key1, value1, parent1);
													if(objjj1.has("sub")){
														JSONArray arraysss2 = objjj1.getJSONArray("sub");
														if(arraysss2!=null&&arraysss2.length()>0){
															for(int kkkk = 0;kkkk<arraysss2.length();kkkk++){
																JSONObject objjj2 = arraysss2.getJSONObject(kkkk);
																String key2 = objjj2.optString("key");
																String value2 = objjj2.optString("value");
																String parent2 = key1;
																DbUtil.deleteclqk_nowByKey(context, key2);
																DbUtil.insertclqk_now(context, key2, value2, parent2);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						} catch (Exception e1) {
						}
						
						// 保存核查信息
						JSONObject insObject;
						try {
							insObject = backJson.getJSONObject("inspection");
						} catch (Exception e) {
							insObject = null;
						}
						try {
							if (insObject == null) {
								ContentValues inspectValues = CaseUtils
										.getInstance()
										.caseJson2ImspectionContentValues(
												backJson.getJSONObject("case"));
								int delInspectCount = DBHelper
										.getDbHelper(context)
										.delete(CaseInspectTable.name,
												new StringBuffer(
														InspectTable.field_caseId)
														.append("=?")
														.toString(),
												new String[] { inspectValues
														.getAsString(InspectTable.field_caseId) });
								inspectValues.put(InspectTable.field_status, 0);
								DBHelper.getDbHelper(context).insert(
										CaseInspectTable.name, inspectValues);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							JSONObject situationObject = backJson
									.getJSONObject("situation");
							if (situationObject != null
									&& situationObject != JSONObject.NULL) {
								CaseUtils.getInstance().storeCaseSituation(
										context, caseId, situationObject);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						publishProgress(-1);
						msg = caseId;
						DbUtil.insertINSPECTIONGETTASK2DbDatas(context, bh,
								redline, x + "", y + "", ajly, dz, jcbh, jcmj,zygdmj,
								xfsj, entiryString);
						return true;
					} else {
						publishProgress(-1);
						msg = backJson.getString("msg");
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			publishProgress(-1);
			msg = "获取案件详情发生异常";
			return false;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			try {
				int i = values[0];
				if (i == -1) {
					if (alertDialog != null && alertDialog.isShowing())
						alertDialog.dismiss();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onProgressUpdate(values);
		}

		@SuppressWarnings("unused")
		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				if (result) {
					Intent intent = new Intent(getActivity(),
							ModuleRealizeActivity.class);
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21026);
					intent.putExtra(Parameters.CASE_ID, msg);
					intent.putExtra("title", bh);
					intent.putExtra("hx", redline);
					intent.putExtra("hx_result", redline_result);
					intent.putExtra("x", x);
					intent.putExtra("y", y);
					intent.putExtra("ajly", ajly);
					intent.putExtra("dz", dz);
					intent.putExtra("jcbh", jcbh);
					intent.putExtra("jcmj", jcmj);
					intent.putExtra("xfsj", xfsj);
					intent.putExtra("xzqhid", xzqh_id);
					intent.putExtra("ajid", aj_id);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else {
					Toast toast = Toast.makeText(context, msg + ",读取缓存数据中...",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					InspectionGetTask2 ist2 = DbUtil.getInspectionGetTask2ByBh(
							context, bh);
					if (ist2 != null) {
						try {
							String entiryString = ist2.getEntiryString();
							JSONObject backJson = new JSONObject(entiryString);
							boolean succeed = backJson.getBoolean("succeed");
							if (succeed) {
								caseId = CaseUtils.getInstance()
										.storeCaseFulldata(context,
												currentUser, backJson,
												InspectionCaseTable.name,
												CaseAnnexesTable.name,
												CasePatrolTable.name,
												CaseInspectTable.name);
								jcbh = backJson.getJSONObject("case")
										.getString("jcbh");
								jcmj = backJson.getJSONObject("case")
										.getString("jcmj") + " ㎡";
								xfsj = backJson.getJSONObject("case")
										.getString("xfsj");
								
								//2017 02 15
								try {
									JSONArray arraysss = backJson.getJSONArray("patrols");
									if(arraysss!=null&&arraysss.length()>0){
										for(int kk = 0;kk<arraysss.length();kk++){
											JSONObject objjj = arraysss.getJSONObject(kk);
											String id = objjj.optString("id");
											String fxjg = objjj.optString("fxjg");
											DbUtil.deletePatrolsById(context, id);
											DbUtil.insertPatrols(context, id, fxjg);
										}	
									}
								} catch (Exception e1) {
								}
								
								//2017 03 14
								try {
									JSONObject abbb = backJson.getJSONObject("situation");
									if(abbb != null&& abbb != JSONObject.NULL){
										JSONArray arraysss = abbb.getJSONArray("clqk_new");
										if(arraysss!=null&&arraysss.length()>0){
											for(int kk = 0;kk<arraysss.length();kk++){
												JSONObject objjj = arraysss.getJSONObject(kk);
												String key = objjj.optString("key");
												String value = objjj.optString("value");
												String parent = null;
												DbUtil.deleteclqk_nowByKey(context, key);
												DbUtil.insertclqk_now(context, key, value, parent);
												if(objjj.has("sub")){
													JSONArray arraysss1 = objjj.getJSONArray("sub");
													if(arraysss1!=null&&arraysss1.length()>0){
														for(int kkk = 0;kkk<arraysss1.length();kkk++){
															JSONObject objjj1 = arraysss1.getJSONObject(kkk);
															String key1 = objjj1.optString("key");
															String value1 = objjj1.optString("value");
															String parent1 = key;
															DbUtil.deleteclqk_nowByKey(context, key1);
															DbUtil.insertclqk_now(context, key1, value1, parent1);
															if(objjj1.has("sub")){
																JSONArray arraysss2 = objjj1.getJSONArray("sub");
																if(arraysss2!=null&&arraysss2.length()>0){
																	for(int kkkk = 0;kkkk<arraysss2.length();kkkk++){
																		JSONObject objjj2 = arraysss2.getJSONObject(kkkk);
																		String key2 = objjj2.optString("key");
																		String value2 = objjj2.optString("value");
																		String parent2 = key1;
																		DbUtil.deleteclqk_nowByKey(context, key2);
																		DbUtil.insertclqk_now(context, key2, value2, parent2);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								} catch (Exception e1) {
								}
								
								// 保存核查信息
								JSONObject insObject;
								try {
									insObject = backJson
											.getJSONObject("inspection");
								} catch (Exception e) {
									insObject = null;
								}
								try {
									if (insObject == null) {
										ContentValues inspectValues = CaseUtils
												.getInstance()
												.caseJson2ImspectionContentValues(
														backJson.getJSONObject("case"));
										int delInspectCount = DBHelper
												.getDbHelper(context)
												.delete(CaseInspectTable.name,
														new StringBuffer(
																InspectTable.field_caseId)
																.append("=?")
																.toString(),
														new String[] { inspectValues
																.getAsString(InspectTable.field_caseId) });
										inspectValues.put(
												InspectTable.field_status, 0);
										DBHelper.getDbHelper(context).insert(
												CaseInspectTable.name,
												inspectValues);
									}

								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									JSONObject situationObject = backJson
											.getJSONObject("situation");
									if (situationObject != null
											&& situationObject != JSONObject.NULL) {
										CaseUtils
												.getInstance()
												.storeCaseSituation(context,
														caseId, situationObject);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							return;
						}
						Intent intent = new Intent(getActivity(),
								ModuleRealizeActivity.class);
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21026);
						intent.putExtra(Parameters.CASE_ID, caseId);
						intent.putExtra("title", bh);
						intent.putExtra("hx", redline);
						intent.putExtra("hx_result", redline_result);
						intent.putExtra("x", x);
						intent.putExtra("y", y);
						intent.putExtra("ajly", ajly);
						intent.putExtra("dz", dz);
						intent.putExtra("jcbh", jcbh);
						intent.putExtra("jcmj", jcmj);
						intent.putExtra("xfsj", xfsj);
						intent.putExtra("xzqhid", xzqh_id);
						intent.putExtra("ajid", aj_id);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.slide_in_right, R.anim.slide_out_left);
					} else {
						Toast toast2 = Toast.makeText(context,
								"网络连接失败且缓存数据不存在", Toast.LENGTH_SHORT);
						toast2.setGravity(Gravity.CENTER, 0, 0);
						toast2.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
