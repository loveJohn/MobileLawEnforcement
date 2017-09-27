package com.chinadci.mel.mleo.ui.fragments.landInspectionListFragment2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
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
import com.chinadci.mel.mleo.ui.fragments.ToolFragment;
import com.chinadci.mel.mleo.ui.fragments.base.BaseV4Fragment4Content;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionGetTask2;
import com.chinadci.mel.mleo.ui.fragments.data.model.SimpleAj_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.model.WpzfAjNum;
import com.chinadci.mel.mleo.ui.fragments.data.model.XZQHNum_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;
import com.chinadci.mel.mleo.ui.fragments.data.task.CheXiaoTask;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetSubNumTask_Wpzf;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetWpzfAjNumTask;
import com.chinadci.mel.mleo.ui.fragments.data.task.NewAddWpzfAjTask;
import com.chinadci.mel.mleo.ui.views.BadgeView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView;
import com.chinadci.mel.mleo.ui.views.xlistview.XListView.IXListViewListener;
import com.chinadci.mel.mleo.utils.CaseUtils;
import com.chinadci.mel.mleo.utils.NetWorkUtils;

/**
 * 土地执法》案件核查（快速处置）列表4
 */
public class LandInspectionListFragment4_wpzf extends BaseV4Fragment4Content
		implements IXListViewListener {
	private String aj_id;
	private String xzqh_id;
	private String code;		//获取跨乡镇图斑列表
	private View mView;
	private XListView mListView;
	private CommonAdapter mAdapter;
	private Handler mHandler;

	private List<Object> mDatas = new ArrayList<Object>();

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
		//判断从Activity拿值，如果为空，则从Intent中拿值，如果也为空，则默认为当前最新年份
		initData(((ModuleActivity)getActivity()).getPatch()==null?getActivity().getIntent().getStringExtra("patch"):((ModuleActivity)getActivity()).getPatch(),
					((ModuleActivity)getActivity()).getAjYear()==null?getActivity().getIntent().getStringExtra("aj_year"):((ModuleActivity)getActivity()).getAjYear(),
							((ModuleActivity)getActivity()).getXzqbmCode()==null?getActivity().getIntent().getStringExtra("aj_xzqbm"):((ModuleActivity)getActivity()).getXzqbmCode());	//add teng.guo
		return mView;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.i("ydzf","now is start LandInspectionListFragment4_wpzf");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView() {
		mListView = (XListView) mView.findViewById(R.id.land_xListView);
		Intent intent = getActivity().getIntent();
		aj_id = intent.getStringExtra("aj_id");
		xzqh_id = intent.getStringExtra("xzqh_id");
		myCurenntAjKey = intent.getStringExtra("myCurenntAjKey");
		code=intent.getStringExtra("code");
		if(9==xzqh_id.length()){
			Toast.makeText(getActivity(), "当前位于乡镇一级", Toast.LENGTH_SHORT).show();
			activityHandle.setToolButtonType(ToolFragment.TOOL_SEARCH);		//编号查询功能
		}else{
			activityHandle.setToolButtonType(ToolFragment.TOOL_FILTER);		//批量查找
		}
		activityHandle.replaceTitle(intent.getStringExtra("title"));
		rg = (RadioGroup) mView.findViewById(R.id.Rbtn2SelectDkType);
		rb1 = (RadioButton) mView.findViewById(R.id.Rbtn2SelectDkType1);
		rb2 = (RadioButton) mView.findViewById(R.id.Rbtn2SelectDkType2);
		rb3 = (RadioButton) mView.findViewById(R.id.Rbtn2SelectDkType3);
		switch (myCurenntAjKey) {
		case "1":
			rb1.setChecked(true);
			//rb2.setChecked(false);
			//rb3.setChecked(false);
			break;
		case "2":
			//rb1.setChecked(false);
			rb2.setChecked(true);
			//rb3.setChecked(false);
			break;
		case "3":
			//rb1.setChecked(false);
			//rb2.setChecked(false);		//delete teng.guo
			rb3.setChecked(true);
			break;
		}
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg0.getCheckedRadioButtonId()) {
				case R.id.Rbtn2SelectDkType1:		//由于市级列表到地区级列表跳转了一次activity，本次从activity中获取年份和区域，如果为空，从市级列表拿一遍。避免从市级列表跳转过来后为空，
					getList("1",((ModuleActivity)getActivity()).getPatch()==null?getActivity().getIntent().getStringExtra("patch"):((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear()==null?getActivity().getIntent().getStringExtra("aj_year"):((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode()==null?getActivity().getIntent().getStringExtra("aj_xzqbm"):((ModuleActivity)getActivity()).getXzqbmCode());		//modify teng.guo
					break;
				case R.id.Rbtn2SelectDkType2:
					getList("2",((ModuleActivity)getActivity()).getPatch()==null?getActivity().getIntent().getStringExtra("patch"):((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear()==null?getActivity().getIntent().getStringExtra("aj_year"):((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode()==null?getActivity().getIntent().getStringExtra("aj_xzqbm"):((ModuleActivity)getActivity()).getXzqbmCode());		//modify teng.guo
					break;
				case R.id.Rbtn2SelectDkType3:
					getList("3",((ModuleActivity)getActivity()).getPatch()==null?getActivity().getIntent().getStringExtra("patch"):((ModuleActivity)getActivity()).getPatch(),((ModuleActivity)getActivity()).getAjYear()==null?getActivity().getIntent().getStringExtra("aj_year"):((ModuleActivity)getActivity()).getAjYear(),((ModuleActivity)getActivity()).getXzqbmCode()==null?getActivity().getIntent().getStringExtra("aj_xzqbm"):((ModuleActivity)getActivity()).getXzqbmCode());		//modify teng.guo
					break;
				}
			}
		});
	}

	private void initData(String patch,String aj_year,String aj_xzqbm) {
		mDatas.clear();
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT).show();
			rb1.setText("未办理("
					+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",
							xzqh_id) + ")");
			rb2.setText("待审核("
					+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",
							xzqh_id) + ")");
			rb3.setText("已审核("
					+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",
							xzqh_id) + ")");
			mDatas = DbUtil.getSimpleAjByXzquAndAjAndAjKey(context, xzqh_id,
					aj_id, myCurenntAjKey);
			if (mDatas.size() > 0) {
				if (mDatas.get(0) instanceof SimpleAj_Wpzf) {
					activityHandle.setToolButtonType(ToolFragment.TOOL_SEARCH);		//编号查询功能	teng.guo
					mAdapter = new CommonAdapter<Object>(mView.getContext(),
							mDatas, R.layout.list_item_clip_page3) {
						@Override
						public void convert(ViewHolder holder,
								final Object item, int position) {
							if (item instanceof SimpleAj_Wpzf) {
								if (Boolean.parseBoolean(((SimpleAj_Wpzf) item)
										.isRevoke())) {
									((TextView) holder
											.getView(R.id.adapter_case_chexiao))
											.setVisibility(View.VISIBLE);
									((TextView) holder
											.getView(R.id.adapter_case_chexiao))
											.setOnClickListener(new OnClickListener() {
												public void onClick(View arg0) {
													AlertDialog.Builder builder = new Builder(
															getActivity());
													builder.setMessage("确认要撤销吗？");
													builder.setTitle("提示");
													builder.setPositiveButton(
															"确认",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.dismiss();
																	//2017 05 02
																	LayoutInflater factory = LayoutInflater.from(getActivity());
																	final View view = factory.inflate(R.layout.dialog_edit_view, null);
																	final EditText edit = (EditText) view.findViewById(R.id.editText);
																	new AlertDialog.Builder(getActivity())
																			.setTitle("请填写原因")
																			.setView(view)
																			.setPositiveButton(
																					"发送",
																					new android.content.DialogInterface.OnClickListener() {
																						@Override
																						public void onClick(final DialogInterface dialog,
																								int which) {
																							String input = edit.getText().toString();  
																				            if (input.equals("")) {  
																				                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																				            } else {
																				            	CheXiaoTask task = new CheXiaoTask(
																										context,
																										new TaskResultHandler<Boolean>() {
																											public void resultHander(
																													Boolean result) {
																												if (result) {
																													getList(myCurenntAjKey,null,null,null);		//modify teng.guo
																													dialog.dismiss();
																												}
																											}
																										});
																								task.execute(
																										((SimpleAj_Wpzf) item)
																										.getId(),
																								((SimpleAj_Wpzf) item)
																										.getAjKey(),
																												currentUser,
																												TimeFormatFactory2
																												.getSourceTime(new Date()),input);
																				            }  
																						}
																					}).setNegativeButton("取消", null).create().show();
																	
																}
															});
													builder.setNegativeButton(
															"取消",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.dismiss();
																}
															});
													builder.create().show();
												}
											});
								} else {
									if (isFirstPage()){
										((TextView) holder
												.getView(R.id.adapter_case_chexiao))
												.setVisibility(View.VISIBLE);
										((TextView) holder
												.getView(R.id.adapter_case_chexiao)).setText("新增");		//add teng.guo 20170620
										((TextView) holder
												.getView(R.id.adapter_case_chexiao)).setOnClickListener(new OnClickListener() {		//新增按钮
													public void onClick(View arg0) {
														if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
															Toast.makeText(getActivity(), "网络无连接，无法新增。", Toast.LENGTH_SHORT).show();
															return;
														}
														AlertDialog.Builder builder = new Builder(
																getActivity());
														builder.setMessage("是否新增案件？");
														builder.setTitle("提示");
														builder.setPositiveButton("新增",new DialogInterface.OnClickListener() {
																	public void onClick(DialogInterface dialog,int which) {
																		new NewAddWpzfAjTask(getActivity(),currentUser,xzqh_id,aj_id).execute(
																				((SimpleAj_Wpzf) item).getQybm(),// 0		//add teng.guo 20170626
																				((SimpleAj_Wpzf) item).getBh(),// 1
																				((SimpleAj_Wpzf) item).getJcbh(),//2
																				
																				((SimpleAj_Wpzf) item).getHx(),// 3
																				((SimpleAj_Wpzf) item).getHxfxjg(),// 4
																				((SimpleAj_Wpzf) item).getX(),// 5
																				((SimpleAj_Wpzf) item).getY(),// 6
																				((SimpleAj_Wpzf) item).getAjly(),// 7
																				((SimpleAj_Wpzf) item).getXxdz(),// 8
																				((SimpleAj_Wpzf) item).getAjKey(),// 9
																				((SimpleAj_Wpzf) item).isApprover(),// 10
																				((SimpleAj_Wpzf) item).isRevoke()// 11
																				);
																	}});
														builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
																	public void onClick(DialogInterface dialog,int which) {
																		dialog.dismiss();
																	}});
														builder.create().show();
													}
												});
									}else{
										((TextView) holder
												.getView(R.id.adapter_case_chexiao))
												.setVisibility(View.GONE);
									}
								}
								if (Boolean.parseBoolean(((SimpleAj_Wpzf) item)
										.getIsSave())) {
									// ((ViewGroup)
									// holder.getView(R.id.ll_root))
									// .setBackgroundColor(Color.parseColor("#51B0FB"));
									((TextView) holder
											.getView(R.id.textView_project_bh))
											.setTextColor(Color
													.parseColor("#51B0FB"));
								} else {
									// ((ViewGroup)
									// holder.getView(R.id.ll_root))
									// .setBackgroundColor(Color.parseColor("#FFFFFF"));
									((TextView) holder
											.getView(R.id.textView_project_bh))
											.setTextColor(Color
													.parseColor("#000000"));
								}
								BadgeView mBadge = ((BadgeView) holder
										.getView(R.id.bagde));
								//modify teng.guo 20170706
								if ((myCurenntAjKey
										.equals("1")||myCurenntAjKey
										.equals("3"))
										&& ((SimpleAj_Wpzf) item)
												.getLastState() != null) {
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setText(((SimpleAj_Wpzf) item)
											.getLastState());
								}else{
									mBadge.setVisibility(View.GONE);
								}
								((TextView) holder
										.getView(R.id.textView_project_bh))
										.setText(((SimpleAj_Wpzf) item).getBh());
								((TextView) holder
										.getView(R.id.textView_project_ajly))
										.setText(((SimpleAj_Wpzf) item)
												.getAjly());
								if (((SimpleAj_Wpzf) item).getJcbh() != null
										&& !((SimpleAj_Wpzf) item).getJcbh()
												.equals("")) {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.VISIBLE);
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setText("监测编号："
													+ ((SimpleAj_Wpzf) item)
															.getJcbh());
								} else {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.GONE);
								}
								((TextView) holder.getView(R.id.textView_xxdz))
										.setText(((SimpleAj_Wpzf) item)
												.getXxdz());
								((TextView) holder.getView(R.id.textView_sj))
										.setText(((SimpleAj_Wpzf) item).getSj());
							}
						}
					};
					mListView.setAdapter(mAdapter);
				}else{
					activityHandle.setToolButtonType(ToolFragment.TOOL_FILTER);		//批量查找  		teng.guo
				}
			} else {
				mDatas = DbUtil.getXZQHNumByXzquAndAjAndAjKey(context, xzqh_id,
						aj_id, myCurenntAjKey);
				if (mDatas.size() > 0) {
					if (mDatas.get(0) instanceof XZQHNum_Wpzf) {
						activityHandle.setToolButtonType(ToolFragment.TOOL_SEARCH);		//编号查询功能  teng.guo
						mAdapter = new CommonAdapter<Object>(
								mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, Object item,
									int position) {
								if (item instanceof XZQHNum_Wpzf) {
									((TextView) holder.getView(R.id.text_name))
											.setText(((XZQHNum_Wpzf) item)
													.getName());
									BadgeView mBadge = ((BadgeView) holder
											.getView(R.id.bagde));
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setBadgeCount(((XZQHNum_Wpzf) item)
											.getNum());
								}
							}
						};
						mListView.setAdapter(mAdapter);
					}else{
						activityHandle.setToolButtonType(ToolFragment.TOOL_FILTER);		//批量查找		teng.guo
					}
				} else {
					Toast.makeText(getActivity(), "网络无连接且无缓存数据...",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		} else {		//有网络连接
			final AlertDialog alertDialog;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在从服务器获取区域详情，请稍候...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			GetWpzfAjNumTask task11 = new GetWpzfAjNumTask(getActivity(),
					new TaskResultHandler<List<WpzfAjNum>>() {
						public void resultHander(List<WpzfAjNum> result) {
							if (result != null && result.size() > 0) {
							} else {
								Toast toast = Toast
										.makeText(context, "网络连接失败,读取缓存数据中...",
												Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
							rb1.setText("未办理("
									+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
											getActivity(), "1", xzqh_id) + ")");
							rb2.setText("待审核("
									+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
											getActivity(), "2", xzqh_id) + ")");
							rb3.setText("已审核("
									+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
											getActivity(), "3", xzqh_id) + ")");
						}
					});
			task11.execute(currentUser, xzqh_id,code,patch,aj_year,aj_xzqbm);		//modify teng.guo
			GetSubNumTask_Wpzf task1 = new GetSubNumTask_Wpzf(context,
					new TaskResultHandler<List<Object>>() {
						public void resultHander(List<Object> result) {
							if (alertDialog != null && alertDialog.isShowing()) {
								alertDialog.dismiss();
							}
							if (result != null) {
								if (result.size() > 0) {
									if (result.get(0) instanceof XZQHNum_Wpzf) {
										activityHandle.setToolButtonType(ToolFragment.TOOL_FILTER);		//批量查找
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page1) {
											@Override
											public void convert(
													ViewHolder holder,
													Object item, int position) {
												if (item instanceof XZQHNum_Wpzf) {
													((TextView) holder
															.getView(R.id.text_name))
															.setText(((XZQHNum_Wpzf) item)
																	.getName());
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													mBadge.setVisibility(View.VISIBLE);
													showViewAnimation(mBadge);
													mBadge.setBadgeCount(((XZQHNum_Wpzf) item)
															.getNum());
												}
											}
										};
										mListView.setAdapter(mAdapter);
									} else if (result.get(0) instanceof SimpleAj_Wpzf) {
										activityHandle.setToolButtonType(ToolFragment.TOOL_SEARCH);		//编号查询功能		teng.guo
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page3) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj_Wpzf) {
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.isRevoke())) {
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setOnClickListener(new OnClickListener() {
																	public void onClick(
																			View arg0) {
																		AlertDialog.Builder builder = new Builder(
																				getActivity());
																		builder.setMessage("确认要撤销吗？");
																		builder.setTitle("提示");
																		builder.setPositiveButton(
																				"确认",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																						//2017 05 02
																						LayoutInflater factory = LayoutInflater.from(getActivity());
																						final View view = factory.inflate(R.layout.dialog_edit_view, null);
																						final EditText edit = (EditText) view.findViewById(R.id.editText);
																						new AlertDialog.Builder(getActivity())
																								.setTitle("请填写原因")
																								.setView(view)
																								.setPositiveButton(
																										"发送",
																										new android.content.DialogInterface.OnClickListener() {
																											@Override
																											public void onClick(final DialogInterface dialog,
																													int which) {
																												String input = edit.getText().toString();  
																									            if (input.equals("")) {  
																									                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																									            } else {
																									            	CheXiaoTask task = new CheXiaoTask(
																															context,
																															new TaskResultHandler<Boolean>() {
																																public void resultHander(
																																		Boolean result) {
																																	if (result) {
																																		getList(myCurenntAjKey,null,null,null);		//modify teng.guo
																																		dialog.dismiss();
																																	}
																																}
																															});
																													task.execute(
																															((SimpleAj_Wpzf) item)
																															.getId(),
																													((SimpleAj_Wpzf) item)
																															.getAjKey(),
																																	currentUser,
																																	TimeFormatFactory2
																																	.getSourceTime(new Date()),input);
																									            }  
																											}
																										}).setNegativeButton("取消", null).create().show();
																					}
																				});
																		builder.setNegativeButton(
																				"取消",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																					}
																				});
																		builder.create()
																				.show();
																	}
																});
													} else {
														if (isFirstPage()){
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.VISIBLE);
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setText("新增");		//add teng.guo 20170620
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setOnClickListener(new OnClickListener() {		//新增按钮
																		public void onClick(View arg0) {
																			if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
																				Toast.makeText(getActivity(), "网络无连接，无法新增。", Toast.LENGTH_SHORT).show();
																				return;
																			}
																			AlertDialog.Builder builder = new Builder(
																					getActivity());
																			builder.setMessage("是否新增案件？");
																			builder.setTitle("提示");
																			builder.setPositiveButton("新增",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							new NewAddWpzfAjTask(getActivity(),currentUser,xzqh_id,aj_id).execute(
																									((SimpleAj_Wpzf) item).getQybm(),// 0		//add teng.guo 20170626
																									((SimpleAj_Wpzf) item).getBh(),// 1
																									((SimpleAj_Wpzf) item).getJcbh(),//2
																									
																									((SimpleAj_Wpzf) item).getHx(),// 3
																									((SimpleAj_Wpzf) item).getHxfxjg(),// 4
																									((SimpleAj_Wpzf) item).getX(),// 5
																									((SimpleAj_Wpzf) item).getY(),// 6
																									((SimpleAj_Wpzf) item).getAjly(),// 7
																									((SimpleAj_Wpzf) item).getXxdz(),// 8
																									((SimpleAj_Wpzf) item).getAjKey(),// 9
																									((SimpleAj_Wpzf) item).isApprover(),// 10
																									((SimpleAj_Wpzf) item).isRevoke()// 11
																									);
																						}});
																			builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							dialog.dismiss();
																						}});
																			builder.create().show();
																		}
																	});
														}else{
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.GONE);
														}
													}
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.getIsSave())) {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#51B0FB"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#51B0FB"));
													} else {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#FFFFFF"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#000000"));
													}
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													
													//展示标识	modify teng.guo 20170706
													if ((myCurenntAjKey
															.equals("1")||myCurenntAjKey
															.equals("3"))
															&& ((SimpleAj_Wpzf) item)
																	.getLastState() != null) {
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setText(((SimpleAj_Wpzf) item)
																.getLastState());
													}else{
														mBadge.setVisibility(View.GONE);
													}
													
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj_Wpzf) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj_Wpzf) item)
																	.getAjly());
													if (((SimpleAj_Wpzf) item)
															.getJcbh() != null
															&& !((SimpleAj_Wpzf) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj_Wpzf) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj_Wpzf) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj_Wpzf) item)
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
								rb1.setText("未办理("
										+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
												getActivity(), "1", xzqh_id)
										+ ")");
								rb2.setText("待审核("
										+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
												getActivity(), "2", xzqh_id)
										+ ")");
								rb3.setText("已审核("
										+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
												getActivity(), "3", xzqh_id)
										+ ")");
								mDatas = DbUtil
										.getSimpleAjByXzquAndAjAndAjKey(
												context, xzqh_id, aj_id,
												myCurenntAjKey);
								if (mDatas.size() > 0) {
									if (mDatas.get(0) instanceof SimpleAj_Wpzf) {
										activityHandle.setToolButtonType(ToolFragment.TOOL_SEARCH);		//编号查询功能
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), mDatas,
												R.layout.list_item_clip_page3) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj_Wpzf) {
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.isRevoke())) {
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setOnClickListener(new OnClickListener() {
																	public void onClick(
																			View arg0) {
																		AlertDialog.Builder builder = new Builder(
																				getActivity());
																		builder.setMessage("确认要撤销吗？");
																		builder.setTitle("提示");
																		builder.setPositiveButton(
																				"确认",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																						//2017 05 02
																						LayoutInflater factory = LayoutInflater.from(getActivity());
																						final View view = factory.inflate(R.layout.dialog_edit_view, null);
																						final EditText edit = (EditText) view.findViewById(R.id.editText);
																						new AlertDialog.Builder(getActivity())
																								.setTitle("请填写原因")
																								.setView(view)
																								.setPositiveButton(
																										"发送",
																										new android.content.DialogInterface.OnClickListener() {
																											@Override
																											public void onClick(final DialogInterface dialog,
																													int which) {
																												String input = edit.getText().toString();  
																									            if (input.equals("")) {  
																									                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																									            } else {
																									            	CheXiaoTask task = new CheXiaoTask(
																															context,
																															new TaskResultHandler<Boolean>() {
																																public void resultHander(
																																		Boolean result) {
																																	if (result) {
																																		getList(myCurenntAjKey,null,null,null);		//modify teng.guo
																																		dialog.dismiss();
																																	}
																																}
																															});
																													task.execute(
																															((SimpleAj_Wpzf) item)
																															.getId(),
																													((SimpleAj_Wpzf) item)
																															.getAjKey(),
																																	currentUser,
																																	TimeFormatFactory2
																																	.getSourceTime(new Date()),input);
																									            }  
																											}
																										}).setNegativeButton("取消", null).create().show();
																					}
																				});
																		builder.setNegativeButton(
																				"取消",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																					}
																				});
																		builder.create()
																				.show();
																	}
																});
													} else {
														if (isFirstPage()){
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.VISIBLE);
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setText("新增");		//add teng.guo 20170620
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setOnClickListener(new OnClickListener() {		//新增按钮
																		public void onClick(View arg0) {
																			if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
																				Toast.makeText(getActivity(), "网络无连接，无法新增。", Toast.LENGTH_SHORT).show();
																				return;
																			}
																			AlertDialog.Builder builder = new Builder(
																					getActivity());
																			builder.setMessage("是否新增案件？");
																			builder.setTitle("提示");
																			builder.setPositiveButton("新增",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							new NewAddWpzfAjTask(getActivity(),currentUser,xzqh_id,aj_id).execute(
																									((SimpleAj_Wpzf) item).getQybm(),// 0		//add teng.guo 20170626
																									((SimpleAj_Wpzf) item).getBh(),// 1
																									((SimpleAj_Wpzf) item).getJcbh(),//2
																									
																									((SimpleAj_Wpzf) item).getHx(),// 3
																									((SimpleAj_Wpzf) item).getHxfxjg(),// 4
																									((SimpleAj_Wpzf) item).getX(),// 5
																									((SimpleAj_Wpzf) item).getY(),// 6
																									((SimpleAj_Wpzf) item).getAjly(),// 7
																									((SimpleAj_Wpzf) item).getXxdz(),// 8
																									((SimpleAj_Wpzf) item).getAjKey(),// 9
																									((SimpleAj_Wpzf) item).isApprover(),// 10
																									((SimpleAj_Wpzf) item).isRevoke()// 11
																									);
																						}});
																			builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							dialog.dismiss();
																						}});
																			builder.create().show();
																		}
																	});
														}else{
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.GONE);
														}
													}
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.getIsSave())) {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#51B0FB"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#51B0FB"));
													} else {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#FFFFFF"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#000000"));
													}
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													//modify teng.guo 20170706
													if ((myCurenntAjKey
															.equals("1")||myCurenntAjKey
															.equals("3"))
															&& ((SimpleAj_Wpzf) item)
																	.getLastState() != null) {
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setText(((SimpleAj_Wpzf) item)
																.getLastState());
													}else{
														mBadge.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj_Wpzf) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj_Wpzf) item)
																	.getAjly());
													if (((SimpleAj_Wpzf) item)
															.getJcbh() != null
															&& !((SimpleAj_Wpzf) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj_Wpzf) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj_Wpzf) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj_Wpzf) item)
																	.getSj());
												}
											}
										};
										mListView.setAdapter(mAdapter);
									}else{
										activityHandle.setToolButtonType(ToolFragment.TOOL_FILTER);		//批量查找
									}
								} else {
									rb1.setText("未办理("
											+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
													getActivity(), "1", xzqh_id)
											+ ")");
									rb2.setText("待审核("
											+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
													getActivity(), "2", xzqh_id)
											+ ")");
									rb3.setText("已审核("
											+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
													getActivity(), "3", xzqh_id)
											+ ")");
									mDatas = DbUtil
											.getXZQHNumByXzquAndAjAndAjKey(
													context, xzqh_id, aj_id,
													myCurenntAjKey);
									if (mDatas.size() > 0) {
										if (mDatas.get(0) instanceof XZQHNum_Wpzf) {
											activityHandle.setToolButtonType(ToolFragment.TOOL_FILTER);		//批量查找
											mAdapter = new CommonAdapter<Object>(
													mView.getContext(),
													mDatas,
													R.layout.list_item_clip_page1) {
												@Override
												public void convert(
														ViewHolder holder,
														Object item,
														int position) {
													if (item instanceof XZQHNum_Wpzf) {
														((TextView) holder
																.getView(R.id.text_name))
																.setText(((XZQHNum_Wpzf) item)
																		.getName());
														BadgeView mBadge = ((BadgeView) holder
																.getView(R.id.bagde));
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setBadgeCount(((XZQHNum_Wpzf) item)
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
			task1.execute(xzqh_id, aj_id, myCurenntAjKey, currentUser,code,patch,aj_year,aj_xzqbm);		//modify teng.guo
		}
		mListView.setPullLoadEnable(false);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
		mHandler = new Handler();
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Object obj = (Object) mAdapter.getItem(position - 1);
				if (obj instanceof XZQHNum_Wpzf) {
					Intent intent = new Intent(getActivity(),
							ModuleRealizeActivity.class);
					intent.putExtra(ModuleActivity.TAG_MODULE_ID, 210242);
					intent.putExtra("aj_id", aj_id);
					intent.putExtra("xzqh_id", ((XZQHNum_Wpzf) obj).getId());
					intent.putExtra("title", ((XZQHNum_Wpzf) obj).getName());
					intent.putExtra("myCurenntAjKey", myCurenntAjKey);
					intent.putExtra("code", ((XZQHNum_Wpzf) obj).getXzqudm());		//获取跨乡镇列表
					//由于市级列表到地区级列表跳转了一次activity，需要将值塞入intent
					intent.putExtra("patch",((ModuleActivity)getActivity()).getPatch()==null?getActivity().getIntent().getStringExtra("patch"):((ModuleActivity)getActivity()).getPatch());
					intent.putExtra("aj_year",((ModuleActivity)getActivity()).getAjYear()==null?getActivity().getIntent().getStringExtra("aj_year"):((ModuleActivity)getActivity()).getAjYear());		//add teng.guo
					intent.putExtra("aj_xzqbm",((ModuleActivity)getActivity()).getXzqbmCode()==null?getActivity().getIntent().getStringExtra("aj_xzqbm"):((ModuleActivity)getActivity()).getXzqbmCode());
					
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else if (obj instanceof SimpleAj_Wpzf) {
					if (!((SimpleAj_Wpzf) obj).getAjly().equals("卫片执法")) {
						// new inspectionGetTask(context, ((SimpleAj_Wpzf) obj)
						// .getBh(), currentUser).execute(
						// ((SimpleAj_Wpzf) obj).getBh(),
						// ((SimpleAj_Wpzf) obj).getHx(),
						// ((SimpleAj_Wpzf) obj).getHxfxjg(),
						// ((SimpleAj_Wpzf) obj).getX(),
						// ((SimpleAj_Wpzf) obj).getY(),
						// ((SimpleAj_Wpzf) obj).getAjly(),
						// ((SimpleAj_Wpzf) obj).getXxdz());
					} else {
						Log.i("ydzf", "进入案件="+((SimpleAj_Wpzf) obj).getBh()+",aj_id="+aj_id);
						new inspectionGetTask2(context, ((SimpleAj_Wpzf) obj)
								.getBh(), currentUser).execute(
								((SimpleAj_Wpzf) obj).getBh(),// 1
								((SimpleAj_Wpzf) obj).getHx(),// 2
								((SimpleAj_Wpzf) obj).getHxfxjg(),// 3
								((SimpleAj_Wpzf) obj).getX(),// 4
								((SimpleAj_Wpzf) obj).getY(),// 5
								((SimpleAj_Wpzf) obj).getAjly(),// 6
								((SimpleAj_Wpzf) obj).getXxdz(),// 7
								((SimpleAj_Wpzf) obj).getAjKey(),// 8
								((SimpleAj_Wpzf) obj).isApprover(),// 9
								((SimpleAj_Wpzf) obj).isRevoke(),// 10
								((SimpleAj_Wpzf) obj).getLastState(),// 11
								((SimpleAj_Wpzf) obj).isChecker()// 12
								);
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
		refreshUi();
	}

	// 加载更多
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				getList(myCurenntAjKey,null,null,null);		//modify teng.guo
				onLoad();
			}
		}, 2000);
	}

	private CommonAdapter getList(String ajKey,String patch,String aj_year,String aj_xzqbm) {
		myCurenntAjKey = ajKey;
		mDatas.clear();
		if(mAdapter!=null){
			mAdapter.updateData(mDatas);
		}
		if (!NetWorkUtils.isNetworkAvailable(getActivity())) {// 网络无连接则加载缓存
			Toast.makeText(getActivity(), "网络无连接，请检查网络连接", Toast.LENGTH_SHORT)
					.show();
			rb1.setText("未办理("
					+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "1",
							xzqh_id) + ")");
			rb2.setText("待审核("
					+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "2",
							xzqh_id) + ")");
			rb3.setText("已审核("
					+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(getActivity(), "3",
							xzqh_id) + ")");
			mDatas = DbUtil.getSimpleAjByXzquAndAjAndAjKey(context, xzqh_id,
					aj_id, myCurenntAjKey);
			if (mDatas.size() > 0) {
				if (mDatas.get(0) instanceof SimpleAj_Wpzf) {
					mAdapter = new CommonAdapter<Object>(mView.getContext(),
							mDatas, R.layout.list_item_clip_page3) {
						@Override
						public void convert(ViewHolder holder,
								final Object item, int position) {
							if (item instanceof SimpleAj_Wpzf) {
								if (Boolean.parseBoolean(((SimpleAj_Wpzf) item)
										.isRevoke())) {
									((TextView) holder
											.getView(R.id.adapter_case_chexiao))
											.setVisibility(View.VISIBLE);
									((TextView) holder
											.getView(R.id.adapter_case_chexiao))
											.setOnClickListener(new OnClickListener() {
												public void onClick(View arg0) {
													AlertDialog.Builder builder = new Builder(
															getActivity());
													builder.setMessage("确认要撤销吗？");
													builder.setTitle("提示");
													builder.setPositiveButton(
															"确认",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.dismiss();
																	//2017 05 02
																	LayoutInflater factory = LayoutInflater.from(getActivity());
																	final View view = factory.inflate(R.layout.dialog_edit_view, null);
																	final EditText edit = (EditText) view.findViewById(R.id.editText);
																	new AlertDialog.Builder(getActivity())
																			.setTitle("请填写原因")
																			.setView(view)
																			.setPositiveButton(
																					"发送",
																					new android.content.DialogInterface.OnClickListener() {
																						@Override
																						public void onClick(final DialogInterface dialog,
																								int which) {
																							String input = edit.getText().toString();  
																				            if (input.equals("")) {  
																				                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																				            } else {
																				            	CheXiaoTask task = new CheXiaoTask(
																										context,
																										new TaskResultHandler<Boolean>() {
																											public void resultHander(
																													Boolean result) {
																												if (result) {
																													getList(myCurenntAjKey,null,null,null);		//modify teng.guo
																													dialog.dismiss();
																												}
																											}
																										});
																								task.execute(
																										((SimpleAj_Wpzf) item)
																										.getId(),
																								((SimpleAj_Wpzf) item)
																										.getAjKey(),
																												currentUser,
																												TimeFormatFactory2
																												.getSourceTime(new Date()),input);
																				            }  
																						}
																					}).setNegativeButton("取消", null).create().show();
																}
															});
													builder.setNegativeButton(
															"取消",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	dialog.dismiss();
																}
															});
													builder.create().show();
												}
											});
								} else {
									if (isFirstPage()){
										((TextView) holder
												.getView(R.id.adapter_case_chexiao))
												.setVisibility(View.VISIBLE);
										((TextView) holder
												.getView(R.id.adapter_case_chexiao)).setText("新增");		//add teng.guo 20170620
										((TextView) holder
												.getView(R.id.adapter_case_chexiao)).setOnClickListener(new OnClickListener() {		//新增按钮
													public void onClick(View arg0) {
														if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
															Toast.makeText(getActivity(), "网络无连接，无法新增。", Toast.LENGTH_SHORT).show();
															return;
														}
														AlertDialog.Builder builder = new Builder(
																getActivity());
														builder.setMessage("是否新增案件？");
														builder.setTitle("提示");
														builder.setPositiveButton("新增",new DialogInterface.OnClickListener() {
																	public void onClick(DialogInterface dialog,int which) {
																		new NewAddWpzfAjTask(getActivity(),currentUser,xzqh_id,aj_id).execute(
																				((SimpleAj_Wpzf) item).getQybm(),// 0		//add teng.guo 20170626
																				((SimpleAj_Wpzf) item).getBh(),// 1
																				((SimpleAj_Wpzf) item).getJcbh(),//2
																				
																				((SimpleAj_Wpzf) item).getHx(),// 3
																				((SimpleAj_Wpzf) item).getHxfxjg(),// 4
																				((SimpleAj_Wpzf) item).getX(),// 5
																				((SimpleAj_Wpzf) item).getY(),// 6
																				((SimpleAj_Wpzf) item).getAjly(),// 7
																				((SimpleAj_Wpzf) item).getXxdz(),// 8
																				((SimpleAj_Wpzf) item).getAjKey(),// 9
																				((SimpleAj_Wpzf) item).isApprover(),// 10
																				((SimpleAj_Wpzf) item).isRevoke()// 11
																				);
																	}});
														builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
																	public void onClick(DialogInterface dialog,int which) {
																		dialog.dismiss();
																	}});
														builder.create().show();
													}
												});
									}else{
										((TextView) holder
												.getView(R.id.adapter_case_chexiao))
												.setVisibility(View.GONE);
									}	
								}
								if (Boolean.parseBoolean(((SimpleAj_Wpzf) item)
										.getIsSave())) {
									// ((ViewGroup)
									// holder.getView(R.id.ll_root))
									// .setBackgroundColor(Color.parseColor("#51B0FB"));
									((TextView) holder
											.getView(R.id.textView_project_bh))
											.setTextColor(Color
													.parseColor("#51B0FB"));
								} else {
									// ((ViewGroup)
									// holder.getView(R.id.ll_root))
									// .setBackgroundColor(Color.parseColor("#FFFFFF"));
									((TextView) holder
											.getView(R.id.textView_project_bh))
											.setTextColor(Color
													.parseColor("#000000"));
								}
								BadgeView mBadge = ((BadgeView) holder
										.getView(R.id.bagde));
								//modify teng.guo 20170706
								if ((myCurenntAjKey
										.equals("1")||myCurenntAjKey
										.equals("3"))
										&& ((SimpleAj_Wpzf) item)
												.getLastState() != null) {
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setText(((SimpleAj_Wpzf) item)
											.getLastState());
								}else{
									mBadge.setVisibility(View.GONE);
								}
								((TextView) holder
										.getView(R.id.textView_project_bh))
										.setText(((SimpleAj_Wpzf) item).getBh());
								((TextView) holder
										.getView(R.id.textView_project_ajly))
										.setText(((SimpleAj_Wpzf) item)
												.getAjly());
								if (((SimpleAj_Wpzf) item).getJcbh() != null
										&& !((SimpleAj_Wpzf) item).getJcbh()
												.equals("")) {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.VISIBLE);
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setText("监测编号："
													+ ((SimpleAj_Wpzf) item)
															.getJcbh());
								} else {
									((TextView) holder
											.getView(R.id.textView_jcbh))
											.setVisibility(View.GONE);
								}
								((TextView) holder.getView(R.id.textView_xxdz))
										.setText(((SimpleAj_Wpzf) item)
												.getXxdz());
								((TextView) holder.getView(R.id.textView_sj))
										.setText(((SimpleAj_Wpzf) item).getSj());
							}
						}
					};
					mAdapter.notifyDataSetChanged();
					mListView.setAdapter(mAdapter);
				}
			} else {
				mDatas = DbUtil.getXZQHNumByXzquAndAjAndAjKey(context, xzqh_id,
						aj_id, myCurenntAjKey);
				if (mDatas.size() > 0) {
					if (mDatas.get(0) instanceof XZQHNum_Wpzf) {
						mAdapter = new CommonAdapter<Object>(
								mView.getContext(), mDatas,
								R.layout.list_item_clip_page1) {
							@Override
							public void convert(ViewHolder holder, Object item,
									int position) {
								if (item instanceof XZQHNum_Wpzf) {
									((TextView) holder.getView(R.id.text_name))
											.setText(((XZQHNum_Wpzf) item)
													.getName());
									BadgeView mBadge = ((BadgeView) holder
											.getView(R.id.bagde));
									mBadge.setVisibility(View.VISIBLE);
									showViewAnimation(mBadge);
									mBadge.setBadgeCount(((XZQHNum_Wpzf) item)
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
			GetWpzfAjNumTask task11 = new GetWpzfAjNumTask(getActivity(),
					new TaskResultHandler<List<WpzfAjNum>>() {
						public void resultHander(List<WpzfAjNum> result) {
							if (result != null && result.size() > 0) {
							} else {
								Toast toast = Toast
										.makeText(context, "网络连接失败,读取缓存数据中...",
												Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
							rb1.setText("未办理("
									+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
											getActivity(), "1", xzqh_id) + ")");
							rb2.setText("待审核("
									+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
											getActivity(), "2", xzqh_id) + ")");
							rb3.setText("已审核("
									+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
											getActivity(), "3", xzqh_id) + ")");
						}
					});
			task11.execute(currentUser, xzqh_id,code,patch,aj_year,aj_xzqbm);		//modify teng.guo
			GetSubNumTask_Wpzf task1 = new GetSubNumTask_Wpzf(context,
					new TaskResultHandler<List<Object>>() {
						public void resultHander(List<Object> result) {
							if (alertDialog != null && alertDialog.isShowing()) {
								alertDialog.dismiss();
							}
							if (result != null) {
								if (result.size() > 0) {
									if (result.get(0) instanceof XZQHNum_Wpzf) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page1) {
											@Override
											public void convert(
													ViewHolder holder,
													Object item, int position) {
												if (item instanceof XZQHNum_Wpzf) {
													((TextView) holder
															.getView(R.id.text_name))
															.setText(((XZQHNum_Wpzf) item)
																	.getName());
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													mBadge.setVisibility(View.VISIBLE);
													showViewAnimation(mBadge);
													mBadge.setBadgeCount(((XZQHNum_Wpzf) item)
															.getNum());
												}
											}
										};
										mAdapter.notifyDataSetChanged();
										mListView.setAdapter(mAdapter);
									} else if (result.get(0) instanceof SimpleAj_Wpzf) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), result,
												R.layout.list_item_clip_page3) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj_Wpzf) {
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.isRevoke())) {
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setOnClickListener(new OnClickListener() {
																	public void onClick(
																			View arg0) {
																		AlertDialog.Builder builder = new Builder(
																				getActivity());
																		builder.setMessage("确认要撤销吗？");
																		builder.setTitle("提示");
																		builder.setPositiveButton(
																				"确认",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																						//2017 05 02
																						LayoutInflater factory = LayoutInflater.from(getActivity());
																						final View view = factory.inflate(R.layout.dialog_edit_view, null);
																						final EditText edit = (EditText) view.findViewById(R.id.editText);
																						new AlertDialog.Builder(getActivity())
																								.setTitle("请填写原因")
																								.setView(view)
																								.setPositiveButton(
																										"发送",
																										new android.content.DialogInterface.OnClickListener() {
																											@Override
																											public void onClick(final DialogInterface dialog,
																													int which) {
																												String input = edit.getText().toString();  
																									            if (input.equals("")) {  
																									                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																									            } else {
																									            	CheXiaoTask task = new CheXiaoTask(
																															context,
																															new TaskResultHandler<Boolean>() {
																																public void resultHander(
																																		Boolean result) {
																																	if (result) {
																																		getList(myCurenntAjKey,null,null,null);		//modify teng.guo
																																		dialog.dismiss();
																																	}
																																}
																															});
																													task.execute(
																															((SimpleAj_Wpzf) item)
																															.getId(),
																													((SimpleAj_Wpzf) item)
																															.getAjKey(),
																																	currentUser,
																																	TimeFormatFactory2
																																	.getSourceTime(new Date()),input);
																									            }  
																											}
																										}).setNegativeButton("取消", null).create().show();
																					}
																				});
																		builder.setNegativeButton(
																				"取消",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																					}
																				});
																		builder.create()
																				.show();
																	}
																});
													} else {
														if (isFirstPage()){
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.VISIBLE);
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setText("新增");		//add teng.guo 20170620
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setOnClickListener(new OnClickListener() {		//新增按钮
																		public void onClick(View arg0) {
																			if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
																				Toast.makeText(getActivity(), "网络无连接，无法新增。", Toast.LENGTH_SHORT).show();
																				return;
																			}
																			AlertDialog.Builder builder = new Builder(
																					getActivity());
																			builder.setMessage("是否新增案件？");
																			builder.setTitle("提示");
																			builder.setPositiveButton("新增",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							new NewAddWpzfAjTask(getActivity(),currentUser,xzqh_id,aj_id).execute(
																									((SimpleAj_Wpzf) item).getQybm(),// 0		//add teng.guo 20170626
																									((SimpleAj_Wpzf) item).getBh(),// 1
																									((SimpleAj_Wpzf) item).getJcbh(),//2
																									
																									((SimpleAj_Wpzf) item).getHx(),// 3
																									((SimpleAj_Wpzf) item).getHxfxjg(),// 4
																									((SimpleAj_Wpzf) item).getX(),// 5
																									((SimpleAj_Wpzf) item).getY(),// 6
																									((SimpleAj_Wpzf) item).getAjly(),// 7
																									((SimpleAj_Wpzf) item).getXxdz(),// 8
																									((SimpleAj_Wpzf) item).getAjKey(),// 9
																									((SimpleAj_Wpzf) item).isApprover(),// 10
																									((SimpleAj_Wpzf) item).isRevoke()// 11
																									);
																						}});
																			builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							dialog.dismiss();
																						}});
																			builder.create().show();
																		}
																	});
														}else{
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.GONE);
														}	
													}
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.getIsSave())) {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#51B0FB"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#51B0FB"));
													} else {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#FFFFFF"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#000000"));
													}
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													//modify teng.guo 20170706
													if ((myCurenntAjKey
															.equals("1")||myCurenntAjKey
															.equals("3"))
															&& ((SimpleAj_Wpzf) item)
																	.getLastState() != null) {
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setText(((SimpleAj_Wpzf) item)
																.getLastState());
													}else{
														mBadge.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj_Wpzf) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj_Wpzf) item)
																	.getAjly());
													if (((SimpleAj_Wpzf) item)
															.getJcbh() != null
															&& !((SimpleAj_Wpzf) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj_Wpzf) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj_Wpzf) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj_Wpzf) item)
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
								rb1.setText("未办理("
										+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
												getActivity(), "1", xzqh_id)
										+ ")");
								rb2.setText("待审核("
										+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
												getActivity(), "2", xzqh_id)
										+ ")");
								rb3.setText("已审核("
										+ DbUtil.getWpzfAjNumCountByKeyAndXzqh(
												getActivity(), "3", xzqh_id)
										+ ")");
								mDatas = DbUtil
										.getSimpleAjByXzquAndAjAndAjKey(
												context, xzqh_id, aj_id,
												myCurenntAjKey);
								if (mDatas.size() > 0) {
									if (mDatas.get(0) instanceof SimpleAj_Wpzf) {
										mAdapter = new CommonAdapter<Object>(
												mView.getContext(), mDatas,
												R.layout.list_item_clip_page3) {
											@Override
											public void convert(
													ViewHolder holder,
													final Object item,
													int position) {
												if (item instanceof SimpleAj_Wpzf) {
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.isRevoke())) {
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.adapter_case_chexiao))
																.setOnClickListener(new OnClickListener() {
																	public void onClick(
																			View arg0) {
																		AlertDialog.Builder builder = new Builder(
																				getActivity());
																		builder.setMessage("确认要撤销吗？");
																		builder.setTitle("提示");
																		builder.setPositiveButton(
																				"确认",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																						//2017 05 02
																						LayoutInflater factory = LayoutInflater.from(getActivity());
																						final View view = factory.inflate(R.layout.dialog_edit_view, null);
																						final EditText edit = (EditText) view.findViewById(R.id.editText);
																						new AlertDialog.Builder(getActivity())
																								.setTitle("请填写原因")
																								.setView(view)
																								.setPositiveButton(
																										"发送",
																										new android.content.DialogInterface.OnClickListener() {
																											@Override
																											public void onClick(final DialogInterface dialog,
																													int which) {
																												String input = edit.getText().toString();  
																									            if (input.equals("")) {  
																									                Toast.makeText(getActivity(), "填写原因不能为空！" + input, Toast.LENGTH_LONG).show();  
																									            } else {
																									            	CheXiaoTask task = new CheXiaoTask(
																															context,
																															new TaskResultHandler<Boolean>() {
																																public void resultHander(
																																		Boolean result) {
																																	if (result) {
																																		getList(myCurenntAjKey,null,null,null);		//modify teng.guo
																																		dialog.dismiss();
																																	}
																																}
																															});
																													task.execute(
																															((SimpleAj_Wpzf) item)
																															.getId(),
																													((SimpleAj_Wpzf) item)
																															.getAjKey(),
																																	currentUser,
																																	TimeFormatFactory2
																																	.getSourceTime(new Date()),input);
																									            }  
																											}
																										}).setNegativeButton("取消", null).create().show();
																					}
																				});
																		builder.setNegativeButton(
																				"取消",
																				new DialogInterface.OnClickListener() {
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						dialog.dismiss();
																					}
																				});
																		builder.create()
																				.show();
																	}
																});
													} else {
														if (isFirstPage()){
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.VISIBLE);
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setText("新增");		//add teng.guo 20170620
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao)).setOnClickListener(new OnClickListener() {		//新增按钮
																		public void onClick(View arg0) {
																			if (!NetWorkUtils.isNetworkAvailable(getActivity())) {
																				Toast.makeText(getActivity(), "网络无连接，无法新增。", Toast.LENGTH_SHORT).show();
																				return;
																			}
																			AlertDialog.Builder builder = new Builder(
																					getActivity());
																			builder.setMessage("是否新增案件？");
																			builder.setTitle("提示");
																			builder.setPositiveButton("新增",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							new NewAddWpzfAjTask(getActivity(),currentUser,xzqh_id,aj_id).execute(
																									((SimpleAj_Wpzf) item).getQybm(),// 0		//add teng.guo 20170626
																									((SimpleAj_Wpzf) item).getBh(),// 1
																									((SimpleAj_Wpzf) item).getJcbh(),//2
																									
																									((SimpleAj_Wpzf) item).getHx(),// 3
																									((SimpleAj_Wpzf) item).getHxfxjg(),// 4
																									((SimpleAj_Wpzf) item).getX(),// 5
																									((SimpleAj_Wpzf) item).getY(),// 6
																									((SimpleAj_Wpzf) item).getAjly(),// 7
																									((SimpleAj_Wpzf) item).getXxdz(),// 8
																									((SimpleAj_Wpzf) item).getAjKey(),// 9
																									((SimpleAj_Wpzf) item).isApprover(),// 10
																									((SimpleAj_Wpzf) item).isRevoke()// 11
																									);
																						}});
																			builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
																						public void onClick(DialogInterface dialog,int which) {
																							dialog.dismiss();
																						}});
																			builder.create().show();
																		}
																	});
														}else{
															((TextView) holder
																	.getView(R.id.adapter_case_chexiao))
																	.setVisibility(View.GONE);
														}	
													}
													if (Boolean
															.parseBoolean(((SimpleAj_Wpzf) item)
																	.getIsSave())) {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#51B0FB"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#51B0FB"));
													} else {
														// ((ViewGroup)
														// holder.getView(R.id.ll_root))
														// .setBackgroundColor(Color.parseColor("#FFFFFF"));
														((TextView) holder
																.getView(R.id.textView_project_bh))
																.setTextColor(Color
																		.parseColor("#000000"));
													}
													BadgeView mBadge = ((BadgeView) holder
															.getView(R.id.bagde));
													//modify teng.guo 20170706
													if ((myCurenntAjKey
															.equals("1")||myCurenntAjKey
															.equals("3"))
															&& ((SimpleAj_Wpzf) item)
																	.getLastState() != null) {
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setText(((SimpleAj_Wpzf) item)
																.getLastState());
													}else{
														mBadge.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_project_bh))
															.setText(((SimpleAj_Wpzf) item)
																	.getBh());
													((TextView) holder
															.getView(R.id.textView_project_ajly))
															.setText(((SimpleAj_Wpzf) item)
																	.getAjly());
													if (((SimpleAj_Wpzf) item)
															.getJcbh() != null
															&& !((SimpleAj_Wpzf) item)
																	.getJcbh()
																	.equals("")) {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.VISIBLE);
														((TextView) holder
																.getView(R.id.textView_jcbh)).setText("监测编号："
																+ ((SimpleAj_Wpzf) item)
																		.getJcbh());
													} else {
														((TextView) holder
																.getView(R.id.textView_jcbh))
																.setVisibility(View.GONE);
													}
													((TextView) holder
															.getView(R.id.textView_xxdz))
															.setText(((SimpleAj_Wpzf) item)
																	.getXxdz());
													((TextView) holder
															.getView(R.id.textView_sj))
															.setText(((SimpleAj_Wpzf) item)
																	.getSj());
												}
											}
										};
										mAdapter.notifyDataSetChanged();
										mListView.setAdapter(mAdapter);
									}
								} else {
									mDatas = DbUtil
											.getXZQHNumByXzquAndAjAndAjKey(
													context, xzqh_id, aj_id,
													myCurenntAjKey);
									if (mDatas.size() > 0) {
										if (mDatas.get(0) instanceof XZQHNum_Wpzf) {
											mAdapter = new CommonAdapter<Object>(
													mView.getContext(),
													mDatas,
													R.layout.list_item_clip_page1) {
												@Override
												public void convert(
														ViewHolder holder,
														Object item,
														int position) {
													if (item instanceof XZQHNum_Wpzf) {
														((TextView) holder
																.getView(R.id.text_name))
																.setText(((XZQHNum_Wpzf) item)
																		.getName());
														BadgeView mBadge = ((BadgeView) holder
																.getView(R.id.bagde));
														mBadge.setVisibility(View.VISIBLE);
														showViewAnimation(mBadge);
														mBadge.setBadgeCount(((XZQHNum_Wpzf) item)
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
			task1.execute(xzqh_id, aj_id, ajKey, currentUser,code,patch,aj_year,aj_xzqbm);		//modify teng.guo
		}
		return mAdapter;
	}

	private void showViewAnimation(View v) {
		Animation anim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);
		v.startAnimation(anim);
	}

	// class inspectionGetTask extends AsyncTask<Object, Integer, Boolean> {
	// String msg = "";
	// Context context;
	// String caseId;
	// String user;
	// AlertDialog alertDialog;
	//
	// private String bh;
	// private String redline;
	// private String redline_result;
	// private double x;
	// private double y;
	// private String ajly;
	// private String dz;
	//
	// public inspectionGetTask(Context c, String caseId, String user) {
	// this.context = c;
	// this.caseId = caseId;
	// this.user = user;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// CircleProgressBusyView abv = new CircleProgressBusyView(context);
	// abv.setMsg("正在从服务器获取案件详情，请稍候...");
	// alertDialog = new AlertDialog.Builder(getActivity()).create();
	// alertDialog.show();
	// alertDialog.setCancelable(false);
	// alertDialog.getWindow().setContentView(abv);
	// }
	//
	// @SuppressWarnings("unused")
	// @Override
	// protected Boolean doInBackground(Object... params) {
	// try {
	// bh = (String) params[0];
	// redline = (String) params[1];
	// redline_result = (String) params[2];
	// x = (Double) params[3];
	// y = (Double) params[4];
	// ajly = (String) params[5];
	// dz = (String) params[6];
	// String uri = "";
	// String appUri = SharedPreferencesUtils.getInstance(context,
	// R.string.shared_preferences).getSharedPreferences(
	// R.string.sp_appuri, "");
	// uri = appUri.endsWith("/") ? new StringBuffer(appUri)
	// .append(context
	// .getString(R.string.uri_inspection_service))
	// .append("?user=").append(user).append("&caseId=")
	// .append(caseId).toString() : new StringBuffer(appUri)
	// .append("/")
	// .append(context
	// .getString(R.string.uri_inspection_service))
	// .append("?user=").append(user).append("&caseId=")
	// .append(caseId).toString();
	// HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
	// if (response.getStatusLine().getStatusCode() == 200) {
	// DbUtil.deleteINSPECTIONGETTASKDbDatasByBh(context, bh);
	// String entiryString = EntityUtils.toString(response
	// .getEntity());
	// Log.i("inspection_search_entiry", entiryString);
	// JSONObject backJson = new JSONObject(entiryString);
	// boolean succeed = backJson.getBoolean("succeed");
	// if (succeed) {
	// caseId = CaseUtils.getInstance().storeCaseFulldata(
	// context, currentUser, backJson,
	// InspectionCaseTable.name,
	// CaseAnnexesTable.name, CasePatrolTable.name,
	// CaseInspectTable.name);
	// // 2017 02 15
	// try {
	// JSONArray arraysss = backJson
	// .getJSONArray("patrols");
	// if (arraysss.length() > 0) {
	// for (int kk = 0; kk < arraysss.length(); kk++) {
	// JSONObject objjj = arraysss
	// .getJSONObject(kk);
	// String id = objjj.optString("id");
	// String fxjg = objjj.optString("fxjg");
	// DbUtil.deletePatrolsById(context, id);
	// DbUtil.insertPatrols(context, id, fxjg);
	// }
	// }
	// } catch (Exception e1) {
	// }
	//
	// // 保存核查信息
	// JSONObject insObject;
	// try {
	// insObject = backJson.getJSONObject("inspection");
	// } catch (Exception e) {
	// insObject = null;
	// }
	// try {
	// if (insObject == null) {
	// ContentValues inspectValues = CaseUtils
	// .getInstance()
	// .caseJson2ImspectionContentValues(
	// backJson.getJSONObject("case"));
	// int delInspectCount = DBHelper
	// .getDbHelper(context)
	// .delete(CaseInspectTable.name,
	// new StringBuffer(
	// InspectTable.field_caseId)
	// .append("=?")
	// .toString(),
	// new String[] { inspectValues
	// .getAsString(InspectTable.field_caseId) });
	// inspectValues.put(InspectTable.field_status, 0);
	// DBHelper.getDbHelper(context).insert(
	// CaseInspectTable.name, inspectValues);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// try {
	// JSONObject situationObject = backJson
	// .getJSONObject("situation");
	// if (situationObject != null
	// && situationObject != JSONObject.NULL) {
	// CaseUtils.getInstance().storeCaseSituation(
	// context, caseId, situationObject);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// publishProgress(-1);
	// msg = caseId;
	// DbUtil.insertINSPECTIONGETTASKDbDatas(context, bh,
	// redline, x + "", y + "", ajly, dz, entiryString);
	// return true;
	// } else {
	// publishProgress(-1);
	// msg = backJson.getString("msg");
	// return false;
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// publishProgress(-1);
	// msg = "获取案件详情发生异常";
	// return false;
	// }
	//
	// @Override
	// protected void onProgressUpdate(Integer... values) {
	// try {
	// int i = values[0];
	// if (i == -1) {
	// if (alertDialog != null && alertDialog.isShowing())
	// alertDialog.dismiss();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// super.onProgressUpdate(values);
	// }
	//
	// @SuppressWarnings("unused")
	// @Override
	// protected void onPostExecute(Boolean result) {
	// try {
	// if (alertDialog != null && alertDialog.isShowing())
	// alertDialog.dismiss();
	// if (result) {
	// Intent intent = new Intent(getActivity(),
	// ModuleRealizeActivity.class);
	// intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21025);
	// intent.putExtra(Parameters.CASE_ID, msg);
	// intent.putExtra("title", bh);
	// intent.putExtra("hx", redline);
	// intent.putExtra("hx_result", redline_result);
	// intent.putExtra("x", x);
	// intent.putExtra("y", y);
	// intent.putExtra("ajly", ajly);
	// intent.putExtra("dz", dz);
	// startActivity(intent);
	// getActivity().overridePendingTransition(
	// R.anim.slide_in_right, R.anim.slide_out_left);
	// } else {
	// Toast toast = Toast.makeText(context, msg + ",读取缓存数据中...",
	// Toast.LENGTH_SHORT);
	// toast.setGravity(Gravity.CENTER, 0, 0);
	// toast.show();
	// InspectionGetTask ist = DbUtil.getInspectionGetTaskByBh(
	// context, bh);
	// if (ist != null) {
	// try {
	// String entiryString = ist.getEntiryString();
	// JSONObject backJson = new JSONObject(entiryString);
	// boolean succeed = backJson.getBoolean("succeed");
	// if (succeed) {
	// caseId = CaseUtils.getInstance()
	// .storeCaseFulldata(context,
	// currentUser, backJson,
	// InspectionCaseTable.name,
	// CaseAnnexesTable.name,
	// CasePatrolTable.name,
	// CaseInspectTable.name);
	//
	// // 2017 02 15
	// try {
	// JSONArray arraysss = backJson
	// .getJSONArray("patrols");
	// if (arraysss.length() > 0) {
	// for (int kk = 0; kk < arraysss.length(); kk++) {
	// JSONObject objjj = arraysss
	// .getJSONObject(kk);
	// String id = objjj.optString("id");
	// String fxjg = objjj
	// .optString("fxjg");
	// DbUtil.deletePatrolsById(context,
	// id);
	// DbUtil.insertPatrols(context, id,
	// fxjg);
	// }
	// }
	// } catch (Exception e1) {
	// }
	//
	// // 保存核查信息
	// JSONObject insObject;
	// try {
	// insObject = backJson
	// .getJSONObject("inspection");
	// } catch (Exception e) {
	// insObject = null;
	// }
	// try {
	// if (insObject == null) {
	// ContentValues inspectValues = CaseUtils
	// .getInstance()
	// .caseJson2ImspectionContentValues(
	// backJson.getJSONObject("case"));
	// int delInspectCount = DBHelper
	// .getDbHelper(context)
	// .delete(CaseInspectTable.name,
	// new StringBuffer(
	// InspectTable.field_caseId)
	// .append("=?")
	// .toString(),
	// new String[] { inspectValues
	// .getAsString(InspectTable.field_caseId) });
	// inspectValues.put(
	// InspectTable.field_status, 0);
	// DBHelper.getDbHelper(context).insert(
	// CaseInspectTable.name,
	// inspectValues);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// try {
	// JSONObject situationObject = backJson
	// .getJSONObject("situation");
	// if (situationObject != null
	// && situationObject != JSONObject.NULL) {
	// CaseUtils
	// .getInstance()
	// .storeCaseSituation(context,
	// caseId, situationObject);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// } catch (Exception e) {
	// return;
	// }
	// Intent intent = new Intent(getActivity(),
	// ModuleRealizeActivity.class);
	// intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21025);
	// intent.putExtra(Parameters.CASE_ID, caseId);
	// intent.putExtra("title", bh);
	// intent.putExtra("hx", redline);
	// intent.putExtra("hx_result", redline_result);
	// intent.putExtra("x", x);
	// intent.putExtra("y", y);
	// intent.putExtra("ajly", ajly);
	// intent.putExtra("dz", dz);
	// startActivity(intent);
	// getActivity().overridePendingTransition(
	// R.anim.slide_in_right, R.anim.slide_out_left);
	// } else {
	// Toast toast2 = Toast.makeText(context,
	// "网络连接失败且缓存数据不存在", Toast.LENGTH_SHORT);
	// toast2.setGravity(Gravity.CENTER, 0, 0);
	// toast2.show();
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// TODO 大修改---2017 03 21
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
		private String zygdmj;		//占用耕地面积		//add teng.guo 20170815
		private String xfsj;
		
		private String clqk;

		private String ajKey;
		private String isApprover;
		private String isRevoke;
		private String lastState;
		private boolean isChecker;		//是否卫片核查人员

		private String glbh = null;
		

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
				ajKey = (String) params[7];
				isApprover = (String) params[8];
				isRevoke = (String) params[9];
				lastState=(String) params[10];		//0706
				isChecker=(boolean)params[11];
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
				Log.i("ydzf", "lastState="+lastState+",openAj-uri="+uri);
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					DbUtil.deleteINSPECTIONGETTASK2DbDatasByBh(context, bh);
					String entiryString = EntityUtils.toString(response
							.getEntity());
					Log.i("ydzf", "entiryString="+entiryString);
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						caseId = CaseUtils.getInstance()
								.storeCaseFulldata_Wpzf(context, currentUser,
										backJson, InspectionCaseTable.name,
										CaseAnnexesTable.name,
										CasePatrolTable.name,
										CaseInspectTable.name);

						jcbh = backJson.getJSONObject("case").getString("jcbh");
						jcmj = backJson.getJSONObject("case").getString("jcmj");
						if(backJson.getJSONObject("case").has("zygdmj")){
							zygdmj=backJson.getJSONObject("case").getString("zygdmj");
						}else{
							zygdmj="获取数据异常";
						}
						xfsj = backJson.getJSONObject("case").getString("xfsj");
						
						if(backJson!=null&&backJson.has("glbh")){
							glbh = backJson.optString("glbh");	
						}

						// 2017 02 15
						try {
							Log.i("ydzf", "patrols="+backJson.getJSONArray("patrols").toString());
							JSONArray arraysss = backJson.getJSONArray("patrols");
							if (arraysss.length() > 0) {
								for (int kk = 0; kk < arraysss.length(); kk++) {
									try{
										JSONObject objjj = arraysss.getJSONObject(kk);
										if(objjj.has("user")){	//处置记录
											String id = objjj.optString("id");// 其实是巡查编号不是案件id
											String fxjg = objjj.optString("fxjg");
											JSONObject clqkObj=objjj.getJSONObject("clqk");
											if(clqk==null){		//循环取值中，只取第一个处置情况。(最新一次处置)
												clqk=clqkObj.getString("key");		//add teng.guo  获取处理情况。
											}
											DbUtil.deletePatrolsById(context, id);
											DbUtil.insertPatrols(context, id, fxjg);
										}else{
											continue;
										}
									}catch (Exception e1) {
										continue;
									}
								}
							}
						} catch (Exception e1) {
						}

						// 2017 03 14
						try {
							JSONObject abbb = backJson.getJSONObject("situation");
							if (abbb != null && abbb != JSONObject.NULL) {
								JSONArray arraysss = abbb.getJSONArray("clqk_new");
								if (arraysss != null && arraysss.length() > 0) {
									for (int kk = 0; kk < arraysss.length(); kk++) {
										JSONObject objjj = arraysss
												.getJSONObject(kk);
										String key = objjj.optString("key");
										String value = objjj.optString("value");
										String parent = null;
										DbUtil.deleteclqk_nowByKey(context, key);
										DbUtil.insertclqk_now(context, key,
												value, parent);
										if (objjj.has("sub")) {
											JSONArray arraysss1 = objjj
													.getJSONArray("sub");
											if (arraysss1 != null
													&& arraysss1.length() > 0) {
												for (int kkk = 0; kkk < arraysss1
														.length(); kkk++) {
													JSONObject objjj1 = arraysss1
															.getJSONObject(kkk);
													String key1 = objjj1
															.optString("key");
													String value1 = objjj1
															.optString("value");
													String parent1 = key;
													DbUtil.deleteclqk_nowByKey(
															context, key1);
													DbUtil.insertclqk_now(
															context, key1,
															value1, parent1);
													if (objjj1.has("sub")) {
														JSONArray arraysss2 = objjj1
																.getJSONArray("sub");
														if (arraysss2 != null
																&& arraysss2
																		.length() > 0) {
															for (int kkkk = 0; kkkk < arraysss2
																	.length(); kkkk++) {
																JSONObject objjj2 = arraysss2
																		.getJSONObject(kkkk);
																String key2 = objjj2
																		.optString("key");
																String value2 = objjj2
																		.optString("value");
																String parent2 = key1;
																DbUtil.deleteclqk_nowByKey(
																		context,
																		key2);
																DbUtil.insertclqk_now(
																		context,
																		key2,
																		value2,
																		parent2);
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
				return false;
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
					if(ajKey.equals("1")){
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
					}else if (ajKey.equals("2")) {
						if(isApprover.equals("true")){
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21028);// 处理审批
						}else{
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21029);// 查看
						}
					} else if (ajKey.equals("3")) {
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21030);// 查看
					}else{
						intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
					}
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
					intent.putExtra("zygdmj", zygdmj);
					intent.putExtra("xfsj", xfsj);
					intent.putExtra("xzqhid", xzqh_id);
					intent.putExtra("ajid", aj_id);
					intent.putExtra("ajKey", ajKey);
					intent.putExtra("isChecker", isChecker);
					intent.putExtra("isApprover", isApprover);
					intent.putExtra("isRevoke", isRevoke);
					intent.putExtra("lastState", lastState);
					intent.putExtra("glbh", glbh);
					intent.putExtra("clqk", clqk);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				} else {
					Toast toast = Toast.makeText(context, msg + "读取缓存数据中...",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					InspectionGetTask2 ist2 = DbUtil.getInspectionGetTask2ByBh(context, bh);
					Log.i("ydzf", "LandInspectionListFragment4_wpzf ist2="+ist2);
					if (ist2 != null) {
						try {
							String entiryString = ist2.getEntiryString();
							Log.i("ydzf", "LandInspectionListFragment4_wpzf ist2 entiryString="+entiryString);
							JSONObject backJson = new JSONObject(entiryString);
							boolean succeed = backJson.getBoolean("succeed");
							if (succeed) {
								caseId = CaseUtils.getInstance()
										.storeCaseFulldata_Wpzf(context,
												currentUser, backJson,
												InspectionCaseTable.name,
												CaseAnnexesTable.name,
												CasePatrolTable.name,
												CaseInspectTable.name);
								jcbh = backJson.getJSONObject("case").getString("jcbh");
								jcmj = backJson.getJSONObject("case").getString("jcmj") + " ㎡";
								if(backJson.getJSONObject("case").has("zygdmj")){
									zygdmj=backJson.getJSONObject("case").getString("zygdmj");
								}else{
									zygdmj="获取数据异常";
								}
								xfsj = backJson.getJSONObject("case")
										.getString("xfsj");
								
								if(backJson!=null&&backJson.has("glbh")){
									glbh = backJson.optString("glbh");	
								}

								// 2017 02 15
								try {
									Log.i("ydzf", "patrols="+backJson.getJSONArray("patrols").toString());
									JSONArray arraysss = backJson.getJSONArray("patrols");
									if (arraysss.length() > 0) {
										for (int kk = 0; kk < arraysss.length(); kk++) {
											try{
											JSONObject objjj = arraysss.getJSONObject(kk);
												if(objjj.has("user")){	//处置记录
													String id = objjj.optString("id");// 其实是巡查编号不是案件id
													String fxjg = objjj.optString("fxjg");
													JSONObject clqkObj=objjj.getJSONObject("clqk");
													if(clqk==null){		//循环取值中，只取第一个处置情况。(最新一次处置)
														clqk=clqkObj.getString("key");		//add teng.guo  获取处理情况。
													}
													DbUtil.deletePatrolsById(context, id);
													DbUtil.insertPatrols(context, id, fxjg);
												}else{
													continue;
												}
											}catch (Exception e1) {
												continue;
											}
										}
									}
								} catch (Exception e1) {
								}

								// 2017 03 14
								try {
									JSONObject abbb = backJson
											.getJSONObject("situation");
									if (abbb != null && abbb != JSONObject.NULL) {
										JSONArray arraysss = abbb
												.getJSONArray("clqk_new");
										if (arraysss != null
												&& arraysss.length() > 0) {
											for (int kk = 0; kk < arraysss
													.length(); kk++) {
												JSONObject objjj = arraysss
														.getJSONObject(kk);
												String key = objjj
														.optString("key");
												String value = objjj
														.optString("value");
												String parent = null;
												DbUtil.deleteclqk_nowByKey(
														context, key);
												DbUtil.insertclqk_now(context,
														key, value, parent);
												if (objjj.has("sub")) {
													JSONArray arraysss1 = objjj
															.getJSONArray("sub");
													if (arraysss1 != null
															&& arraysss1
																	.length() > 0) {
														for (int kkk = 0; kkk < arraysss1
																.length(); kkk++) {
															JSONObject objjj1 = arraysss1
																	.getJSONObject(kkk);
															String key1 = objjj1
																	.optString("key");
															String value1 = objjj1
																	.optString("value");	
															String parent1 = key;
															DbUtil.deleteclqk_nowByKey(
																	context,
																	key1);
															DbUtil.insertclqk_now(
																	context,
																	key1,
																	value1,
																	parent1);
															if (objjj1
																	.has("sub")) {
																JSONArray arraysss2 = objjj1
																		.getJSONArray("sub");
																if (arraysss2 != null
																		&& arraysss2
																				.length() > 0) {
																	for (int kkkk = 0; kkkk < arraysss2
																			.length(); kkkk++) {
																		JSONObject objjj2 = arraysss2
																				.getJSONObject(kkkk);
																		String key2 = objjj2
																				.optString("key");
																		String value2 = objjj2
																				.optString("value");
																		String parent2 = key1;
																		DbUtil.deleteclqk_nowByKey(
																				context,
																				key2);
																		DbUtil.insertclqk_now(
																				context,
																				key2,
																				value2,
																				parent2);
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
						if(ajKey.equals("1")){
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
						}else if (ajKey.equals("2")) {
							if(isApprover.equals("true")){
								intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21028);// 处理审批
							}else{
								intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21029);// 查看
							}
						} else if (ajKey.equals("3")) {
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21030);// 查看
						}else{
							intent.putExtra(ModuleActivity.TAG_MODULE_ID, 21027);// 编辑
						}
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
						intent.putExtra("zygdmj", zygdmj);
						intent.putExtra("xfsj", xfsj);
						intent.putExtra("xzqhid", xzqh_id);
						intent.putExtra("ajid", aj_id);
						intent.putExtra("ajKey", ajKey);
						intent.putExtra("isChecker", isChecker);
						intent.putExtra("isApprover", isApprover);
						intent.putExtra("isRevoke", isRevoke);
						intent.putExtra("lastState", lastState);
						intent.putExtra("glbh", glbh);
						intent.putExtra("clqk", clqk);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.slide_in_right, R.anim.slide_out_left);
					} else {
						Log.i("ydzf", "LandInspectionListFragment4_wpzf else ist2=null ");
						Toast toast2 = Toast.makeText(context,
								"数据请求失败且缓存数据不存在", Toast.LENGTH_SHORT);
						toast2.setGravity(Gravity.CENTER, 0, 0);
						toast2.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	boolean isFirstPage(){
		if(myCurenntAjKey.equals("1")){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {
				//由于市级列表到地区级列表跳转了一次activity，本次从activity中获取年份和区域，如果为空，从市级列表的Intent中拿一遍。如果Intent拿值也为空，则默认为最新当前下发年份
				getList(myCurenntAjKey,((ModuleActivity)getActivity()).getPatch()==null?getActivity().getIntent().getStringExtra("patch"):((ModuleActivity)getActivity()).getPatch(),
						((ModuleActivity)getActivity()).getAjYear()==null?getActivity().getIntent().getStringExtra("aj_year"):((ModuleActivity)getActivity()).getAjYear(),
						((ModuleActivity)getActivity()).getXzqbmCode()==null?getActivity().getIntent().getStringExtra("aj_xzqbm"):((ModuleActivity)getActivity()).getXzqbmCode());	//modify teng.guo
				onLoad();
			}
		}, 500);
	}
	
}
