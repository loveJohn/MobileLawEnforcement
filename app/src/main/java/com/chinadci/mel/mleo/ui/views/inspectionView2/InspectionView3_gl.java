package com.chinadci.mel.mleo.ui.views.inspectionView2;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter2;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.Jsdt;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetAjljTask;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;

public class InspectionView3_gl extends FrameLayout {

	View rootView;
	ListView attriListView;
	View emptyView;
	String caseId = "";
	String sourceTable = "";
	AttriAdapter2 attriAdapter;

	TextView glxx_txt;
	LinearLayout glxx_edit_linear;
	EditText search_keyview;
	Button search_get;

	private String ajbh = null;

	Activity parentActivity;

	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	@SuppressLint("ResourceAsColor")
	public void setGLBH(String ajbh, String bh, String ajKey) {
		this.ajbh = ajbh;
		if (!TextUtils.isEmpty(bh)) {
			glxx_edit_linear.setVisibility(View.GONE);
			glxx_txt.setVisibility(View.VISIBLE);
			glxx_txt.setText(bh);
		} else {
			if (ajKey.equals("1")) {
				glxx_edit_linear.setVisibility(View.VISIBLE);
				glxx_txt.setVisibility(View.GONE);
				glxx_txt.setText("");
				search_keyview.setHint("若已处理，输入关联编号");
				search_keyview.setHintTextColor(R.color.gray);
			} else {
				glxx_edit_linear.setVisibility(View.GONE);
				glxx_txt.setVisibility(View.GONE);
				glxx_txt.setText("");
			}
		}
	}

	public InspectionView3_gl(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public InspectionView3_gl(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public InspectionView3_gl(Context context) {
		super(context);
		initView(context);
	}

	/**
	 */
	public void setDataSource(String id, String table) {
		this.caseId = id;
		this.sourceTable = table;
		viewInspectionInfo(this.caseId, this.sourceTable);
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

	/**
	 * 
	 * @Title: initView
	 * @param context
	 * @throws
	 */
	void initView(final Context context) {
		rootView = LayoutInflater.from(context).inflate(
				R.layout.view_inspection_gl, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		attriListView = (ListView) rootView
				.findViewById(R.id.view_inspection_listview);
		emptyView = rootView.findViewById(R.id.view_inspection_nodata);

		glxx_txt = (TextView) rootView.findViewById(R.id.glxx_txt);
		glxx_edit_linear = (LinearLayout) rootView
				.findViewById(R.id.glxx_edit_linear);
		search_keyview = (EditText) rootView.findViewById(R.id.search_keyview);
		search_get = (Button) rootView.findViewById(R.id.search_get);

		search_get.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!"".equals(search_keyview.getText().toString())) {
					getDatas(search_keyview.getText().toString());
				} else {
					Toast.makeText(context, "请输入案件编号！", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

	}

	protected void getDatas(String string) {
		final AlertDialog alertDialog;
		CircleProgressBusyView abv = new CircleProgressBusyView(parentActivity);
		abv.setMsg("正在关联案件信息，请稍候...");
		alertDialog = new AlertDialog.Builder(parentActivity).create();
		alertDialog.show();
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setContentView(abv);
		GetAjljTask task = new GetAjljTask(parentActivity,
				new TaskResultHandler<Boolean>() {
					@Override
					public void resultHander(Boolean result) {
						if (alertDialog != null && alertDialog.isShowing()) {
							alertDialog.dismiss();
						}
						if (result) {
							parentActivity.finish();
						}
					}
				});
		task.execute(ajbh, string);
	}

	/**
	 * 
	 * @Title: viewCaseInfo
	 * @param id
	 * @param table
	 * @throws
	 */
	void viewInspectionInfo(String id, String table) {
		try {
			String columns[] = new String[] { CaseInspectTable.field_id,
					CaseInspectTable.field_caseId,
					CaseInspectTable.field_illegalSubject,
					CaseInspectTable.field_illegalStatus,
					CaseInspectTable.field_illegalType,
					CaseInspectTable.field_illegalArea,
					CaseInspectTable.field_landUsage,
					CaseInspectTable.field_parties, 
					CaseInspectTable.field_tel,
					CaseInspectTable.field_notes, 
					CaseInspectTable.field_user,
					CaseInspectTable.field_mTime };
			String selection = CaseInspectTable.field_caseId + "=?";
			String args[] = new String[] { id };
			ContentValues caseInfo = DBHelper.getDbHelper(getContext())
					.doQuery(table, columns, selection, args);
			if (caseInfo != null) {
				emptyView.setVisibility(View.GONE);
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();

				// 违法主体
				keys.add(getContext().getString(R.string.v_f2));
				if (caseInfo.getAsString(CaseInspectTable.field_illegalSubject) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_illegalSubject).equals(
								"")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalSubject));
				} else {
					values.add("");
				}

				// 违法当事人
				keys.add(getContext().getString(R.string.v_f3));
				if (caseInfo.getAsString(CaseInspectTable.field_parties) != null
						&& !caseInfo
								.getAsString(CaseInspectTable.field_parties)
								.equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_parties));
				} else {
					values.add("");
				}

				// 电话
				keys.add(getContext().getString(R.string.v_f17));
				if (caseInfo.getAsString(CaseInspectTable.field_tel) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_tel)
								.equals("")) {
					values.add(caseInfo.getAsString(CaseInspectTable.field_tel));
				} else {
					values.add("");
				}

				// 违法类型
				keys.add(getContext().getString(R.string.v_f8));
				if (caseInfo.getAsString(CaseInspectTable.field_illegalType) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_illegalType).equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalType));
				} else {
					values.add(" ");
				}

				// 违法状态
				keys.add(getContext().getString(R.string.v_f9));
				if (caseInfo.getAsString(CaseInspectTable.field_illegalStatus) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_illegalStatus).equals(
								" ")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalStatus));
				} else {
					values.add(" ");
				}
				
				//2017 05 03
				keys.add("建设动态");
				Jsdt jsdt= DbUtil.getJsdtByBh(getContext(),id);
				if (jsdt!=null&&!TextUtils.isEmpty(jsdt.getKey())&&!TextUtils.isEmpty(jsdt.getValue())) {
					values.add(jsdt.getValue());
				} else {
					values.add("");
				}

				// 违法用地面积
				keys.add(getContext().getString(R.string.v_f10) + "(m²)");
				if (caseInfo.getAsDouble(CaseInspectTable.field_illegalArea) != null) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_illegalArea));
				} else {
					values.add("");
				}

				// 用地类型
				keys.add(getContext().getString(R.string.v_f18));
				if (caseInfo.getAsString(CaseInspectTable.field_landUsage) != null
						&& !caseInfo.getAsString(
								CaseInspectTable.field_landUsage).equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_landUsage));
				} else {
					values.add("");
				}

				// // 核查结果
				// keys.add(getContext().getString(R.string.v_f19));
				// if
				// (caseInfo.getAsString(CaseInspectTable.field_inspectResult)
				// != null
				// && !caseInfo.getAsString(
				// CaseInspectTable.field_inspectResult)
				// .equals("")) {
				// values.add(caseInfo
				// .getAsString(CaseInspectTable.field_inspectResult));
				// } else {
				// values.add("");
				// }

				// 成果说明
				keys.add(getContext().getString(R.string.v_f20));
				if (caseInfo.getAsString(CaseInspectTable.field_notes) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_notes)
								.equals("")) {

					values.add(caseInfo
							.getAsString(CaseInspectTable.field_notes));
				} else {
					values.add("");
				}

				// 核查人员
				keys.add(getContext().getString(R.string.v_f21));
				if (caseInfo.getAsString(CaseInspectTable.field_user) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_user)
								.equals("")) {
					values.add(caseInfo
							.getAsString(CaseInspectTable.field_user));
				} else {
					values.add("");
				}

				// 核查时间
				keys.add(getContext().getString(R.string.v_f22));
				if (caseInfo.getAsString(CaseInspectTable.field_mTime) != null
						&& !caseInfo.getAsString(CaseInspectTable.field_mTime)
								.equals("")) {
					values.add(TimeFormatFactory2.getDisplayTimeM(caseInfo
							.getAsString(CaseInspectTable.field_mTime)));
				} else {
					values.add("");
				}

				attriAdapter = new AttriAdapter2(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();
			} else {
				emptyView.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
