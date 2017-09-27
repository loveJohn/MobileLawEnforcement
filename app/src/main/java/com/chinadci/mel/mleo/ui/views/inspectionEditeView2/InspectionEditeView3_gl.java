package com.chinadci.mel.mleo.ui.views.inspectionEditeView2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.ExpKeyValue;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.DropDownExpandableSpinner;
import com.chinadci.mel.android.ui.views.DropDownSpinner;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.IllegalStatusTable;
import com.chinadci.mel.mleo.ldb.IllegalSubjectTable;
import com.chinadci.mel.mleo.ldb.IllegalTypeTable;
import com.chinadci.mel.mleo.ldb.InspectResultTable;
import com.chinadci.mel.mleo.ldb.LandUsageWpTable;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.InspectionEdit;
import com.chinadci.mel.mleo.ui.fragments.data.model.Jsdt;
import com.chinadci.mel.mleo.ui.fragments.data.task.GetAjljTask;
import com.chinadci.mel.mleo.ui.fragments.data.task.AbstractBaseTask.TaskResultHandler;

/**
 * 
 * @ClassName InspectionEditeView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:55
 * 
 */
public class InspectionEditeView3_gl extends FrameLayout {

	View rootView;
	EditText partiesView;
	EditText notesView;
	EditText telView;
	EditText areaView;
	TextView areaUnitView;

	DropDownSpinner inspectResultView;// 核查结果
	DropDownSpinner illegalSubjectView;// 违法主体
	//DropDownSpinner landUsageView;// 地类
	DropDownExpandableSpinner landUsageView;
	DropDownSpinner illegalStatusView;// 违法现状
	DropDownSpinner illegalTypeView;// 违法类型
	DropDownSpinner jsdt;

	String sourceTable = "";
	String caseId;

	String user;
	String parties = "";
	String tel = "";
	String notes = "";
	String keyIllegalSubject = "";
	String keyLandUsage = "";
	String keyIllegalStatus = "";
	String keyIllegalType = "";
	String keyInspectResult = "";
	String keyJsdt = "";
	Double illegalArea;
	boolean isCurrentUnitSqm = true;
	DecimalFormat kmFormat = new DecimalFormat("#.####");
	AttriAdapter attriAdapter;

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
				search_keyview.setHintTextColor(getResources().getColor(R.color.gray));
			} else {
				glxx_edit_linear.setVisibility(View.GONE);
				glxx_txt.setVisibility(View.GONE);
				glxx_txt.setText("");
			}
		}
	}

	public InspectionEditeView3_gl(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public InspectionEditeView3_gl(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public InspectionEditeView3_gl(Context context, Activity activity) {
		super(context);
		initView(activity);
	}

	/**
	 * 
	 * @Title: saveInspection
	 * @Description: save inspection information
	 * @return
	 * @throws
	 */
	public boolean saveInspection() {
		boolean succeed = false;
		try {
			getTextViewValues();
			ContentValues values = new ContentValues();
			if (illegalArea != null) {
				values.put(CaseInspectTable.field_illegalArea, illegalArea);
			}

			values.put(CaseInspectTable.field_status, 0);
			values.put(CaseInspectTable.field_user, user);
			values.put(CaseInspectTable.field_caseId, caseId);
			values.put(CaseInspectTable.field_parties, parties);
			values.put(CaseInspectTable.field_tel, tel);
			values.put(CaseInspectTable.field_notes, notes);
			values.put(CaseInspectTable.field_landUsage, keyLandUsage);
			values.put(CaseInspectTable.field_inspectResult, keyInspectResult);// 核查结果
			values.put(CaseInspectTable.field_illegalSubject, keyIllegalSubject);
			values.put(CaseInspectTable.field_illegalStatus, keyIllegalStatus);
			values.put(CaseInspectTable.field_illegalType, keyIllegalType);
			String ddte = TimeFormatFactory2.getSourceTime(new Date());
			values.put(CaseInspectTable.field_mTime, ddte);

			// 2017 02 25
			DbUtil.deleteINSPECTIONEDITDbDatasByBh(getContext(), caseId);
			String mj = null;
			if (illegalArea != null) {
				mj = String.valueOf(illegalArea);
			} else {
				mj = "";
			}
			DbUtil.insertINSPECTIONEDITDbDatas(getContext(), caseId,
					keyIllegalSubject, parties, tel, keyIllegalType,
					keyIllegalStatus, mj, keyLandUsage, notes, user, ddte);
			
			DbUtil.deleteINSPECTIONEDITJSDTDbDatasByBh(getContext(), caseId);
			if(!TextUtils.isEmpty(keyJsdt)){
				Jsdt jsy = DbUtil.getJsdtByKeyFromJsdts(getContext(), keyJsdt);
				DbUtil.insertINSPECTIONEDITJSDTDbDatas(getContext(), caseId, jsy.getKey(),jsy.getValue());
			}

			int count = DBHelper.getDbHelper(getContext()).queryCount(
					CaseInspectTable.name,
					null,
					CaseInspectTable.field_caseId + "=? and "
							+ CaseInspectTable.field_status + "=?",
					new String[] { caseId, String.valueOf(0) });
			if (count > 0) {
				int rows = DBHelper.getDbHelper(getContext()).update(
						CaseInspectTable.name,
						values,
						CaseInspectTable.field_caseId + "=? and "
								+ CaseInspectTable.field_status + "=?",
						new String[] { caseId, String.valueOf(0) });
				succeed = (rows > 0);
			} else {
				long row = DBHelper.getDbHelper(getContext()).insert(
						CaseInspectTable.name, values);
				succeed = (row > -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return succeed;
	}

	/**
	 * 
	 * @Title: getInspectionJson
	 * @Description: get ispection json object
	 * @return
	 * @throws
	 */
	public JSONObject getInspectionJson() {
		try {
			getTextViewValues();
			JSONObject obj = new JSONObject();
			obj.put("illegalArea", illegalArea);
			obj.put("user", user);
			obj.put("caseId", caseId);
			obj.put("parties", parties);
			obj.put("illegalSubject", keyIllegalSubject);
			obj.put("tel", tel);
			obj.put("illegalType", keyIllegalType);
			obj.put("illegalStatus", keyIllegalStatus);
			obj.put("landUsage", keyLandUsage);
			obj.put("inspectResult", keyInspectResult);
			obj.put("notes", notes);
			obj.put("jsdt",keyJsdt);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String checkInput() {
		getTextViewValues();
		if (parties == null || parties.trim().equals("")) {
			return "请填写违法主体名称";
		}
		return null;
	}
	public void setDataSource(String user, String caseId, String table) {
		this.sourceTable = table;
		this.caseId = caseId;
		this.user = user;

		// display case detail info
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

	public boolean getTextViewValues() {

		String areaText = areaView.getText().toString();
		if (areaText != null && !"".equals(areaText)) {
			if (isCurrentUnitSqm)
				illegalArea = Double.parseDouble(areaText);
			else
				illegalArea = mu2Sqm(Double.parseDouble(areaText));
		} else {
			illegalArea = null;
		}

		parties = partiesView.getText().toString();
		tel = telView.getText().toString();
		notes = notesView.getText().toString();
		return true;
	}

	void initView(final Context context) {
		rootView = LayoutInflater.from(context).inflate(
				R.layout.view_inspection_edite_gl, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		Log.i("ydzf", "InspectionEditView initView a");
		
		partiesView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_parties);
		telView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_tel);
		illegalStatusView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_illegalstatus);
		illegalTypeView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_illegaltype);
		jsdt = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_jsdt);
		illegalSubjectView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_illegalsubj);
		inspectResultView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_inspectres);
		/*landUsageView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_landusage);*/
		Log.i("ydzf", "InspectionEditView initView b");
		landUsageView = (DropDownExpandableSpinner) rootView
				.findViewById(R.id.view_inspection_edite_landusage);
		
		
		notesView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_notes);
		areaView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_illegalarea);
		areaUnitView = (TextView) rootView
				.findViewById(R.id.view_inspection_edite_unit);
		Log.i("ydzf", "InspectionEditView initView c");
		illegalSubjectView
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		landUsageView
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		inspectResultView
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		illegalStatusView
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		illegalTypeView
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		jsdt.setSelectedChangedListener(spinnerSelectedChangedListener);
		areaUnitView.setOnClickListener(unitClickListener);
		Log.i("ydzf", "InspectionEditView initView d");
		try {
			Log.i("ydzf", "InspectionEditView initView e");
			ArrayList<KeyValue> inspectResult = DBHelper.getDbHelper(context)
					.getParameters(InspectResultTable.name);
			ArrayList<KeyValue> illegalStatus = DBHelper.getDbHelper(context)
					.getParameters(IllegalStatusTable.name);
			ArrayList<KeyValue> illegalType = DBHelper.getDbHelper(context)
					.getParameters(IllegalTypeTable.name);
			
			ArrayList<ExpKeyValue> landUsage = DBHelper.getDbHelper(context).getWpParameters(LandUsageWpTable.name);
			
			/*ArrayList<KeyValue> landUsage = DBHelper.getDbHelper(context)
					.getParameters(LandUsageTable.name);*/
			
			ArrayList<KeyValue> illegalSubject = DBHelper.getDbHelper(context)
					.getParameters(IllegalSubjectTable.name);

			List<Jsdt> jsdts = DbUtil.getJsdts(context);
			ArrayList<KeyValue> jsdtValues = new ArrayList<>();
			if (jsdts != null && jsdts.size() > 0) {
				for (Jsdt js : jsdts) {
					KeyValue keyv = new KeyValue(js.getKey(), js.getValue());
					jsdtValues.add(keyv);
				}
			}
			jsdt.setData(jsdtValues);
			illegalSubjectView.setData(illegalSubject);
			illegalTypeView.setData(illegalType);
			illegalStatusView.setData(illegalStatus);
			inspectResultView.setData(inspectResult);
			
			Log.i("ydzf", "InspectionEditView initView f landUsage="+landUsage);
			landUsageView.setJsonData(landUsage);
		
			//landUsageView.setData(landUsage);
			

		} catch (Exception e) {
			Log.i("ydzf", "InspectionEditView initView e="+e.getMessage());
			e.printStackTrace();
		}

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
					Toast.makeText(context, "请输入案件编号！", Toast.LENGTH_SHORT).show();
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

	OnClickListener unitClickListener = new OnClickListener() {

		public void onClick(View v) {
			if (isCurrentUnitSqm) {// 当前单位为平方米
				areaUnitView.setText(R.string.mu);
				String text = areaView.getText().toString();
				if (text != null && !"".equals(text.trim())) {
					double numb = sqm2Mu(Double.parseDouble(text));
					areaView.setText(kmFormat.format(numb));
				}
			} else {
				areaUnitView.setText(R.string.sqm);
				String text = areaView.getText().toString();
				if (text != null && !"".equals(text.trim())) {
					double numb = mu2Sqm(Double.parseDouble(text));
					areaView.setText(kmFormat.format(numb));
				}
			}
			isCurrentUnitSqm = !isCurrentUnitSqm;

		}
	};

	ISelectedChanged spinnerSelectedChangedListener = new ISelectedChanged() {

		public Object onSelectedChanged(View v, Object o) {
			int vid = v.getId();
			switch (vid) {
			case R.id.view_inspection_edite_landusage:
				keyLandUsage = o.toString();
				break;

			case R.id.view_inspection_edite_inspectres:
				keyInspectResult = o.toString();
				break;

			case R.id.view_inspection_edite_illegalstatus:
				keyIllegalStatus = o.toString();
				break;

			case R.id.view_inspection_edite_illegaltype:
				keyIllegalType = o.toString();
				break;

			case R.id.view_inspection_edite_illegalsubj:
				keyIllegalSubject = o.toString();
				break;

			case R.id.view_inspection_edite_jsdt:
				keyJsdt = o.toString();
				break;

			default:
				break;
			}

			return null;
		}
	};

	/**
	 * 
	 * @Title: viewCaseInfo
	 * @param id
	 * @param table
	 * @throws
	 */
	void viewInspectionInfo(String id, String table) {
		if (id != null && !id.equals("")) {
			try {
				String columns[] = new String[] { CaseInspectTable.field_id,
						CaseInspectTable.field_caseId,
						CaseInspectTable.field_illegalSubject,
						CaseInspectTable.field_illegalStatus,
						CaseInspectTable.field_illegalArea,
						CaseInspectTable.field_illegalType,
						CaseInspectTable.field_landUsage,
						CaseInspectTable.field_inspectResult,
						CaseInspectTable.field_parties,
						CaseInspectTable.field_tel,
						CaseInspectTable.field_notes,
						CaseInspectTable.field_user };
				String selection = CaseInspectTable.field_caseId + "=? and "
						+ CaseInspectTable.field_status + "=?";
				String args[] = new String[] { id, String.valueOf(0) };
				ContentValues inspectionInfo = DBHelper.getDbHelper(
						getContext()).doQuery(table, columns, selection, args);
				if (inspectionInfo != null) {
					parties = inspectionInfo
							.getAsString(CaseInspectTable.field_parties);
					tel = inspectionInfo
							.getAsString(CaseInspectTable.field_tel);
					notes = inspectionInfo
							.getAsString(CaseInspectTable.field_notes);

					keyIllegalStatus = inspectionInfo
							.getAsString(CaseInspectTable.field_illegalStatus);
					keyIllegalType = inspectionInfo
							.getAsString(CaseInspectTable.field_illegalType);
					keyLandUsage = inspectionInfo
							.getAsString(CaseInspectTable.field_landUsage);
					keyInspectResult = inspectionInfo
							.getAsString(CaseInspectTable.field_inspectResult);
					keyIllegalSubject = inspectionInfo
							.getAsString(CaseInspectTable.field_illegalSubject);
					illegalArea = inspectionInfo
							.getAsDouble(CaseInspectTable.field_illegalArea);

					illegalSubjectView.setSelectedItem(keyIllegalSubject);
					illegalStatusView.setSelectedItem(keyIllegalStatus);
					illegalTypeView.setSelectedItem(keyIllegalType);
					landUsageView.setSelectedItem(keyLandUsage);
					inspectResultView.setSelectedItem(keyInspectResult);
					partiesView.setText(parties);
					telView.setText(tel);
					notesView.setText(notes);
					if (illegalArea != null)
						areaView.setText(kmFormat.format(illegalArea));

					InspectionEdit inEdit = DbUtil.getInspectionEditByBh(
							getContext(), caseId);
					if (inEdit != null) {
						if (!TextUtils.isEmpty(inEdit.getWfztlb())) {
							illegalSubjectView.setSelectedItem(inEdit
									.getWfztlb());
						}
						if (!TextUtils.isEmpty(inEdit.getWfxz())) {
							illegalStatusView.setSelectedItem(inEdit.getWfxz());
						}
						if (!TextUtils.isEmpty(inEdit.getWjlx())) {
							illegalTypeView.setSelectedItem(inEdit.getWjlx());
						}
						if (!TextUtils.isEmpty(inEdit.getDl())) {
							landUsageView.setSelectedItem(inEdit.getDl());
						}
						inspectResultView.setSelectedItem(keyInspectResult);
						partiesView.setText(inEdit.getWfztmc());
						telView.setText(inEdit.getPhone());
						notesView.setText(inEdit.getCgsm());
						areaView.setText(inEdit.getWfydmj());
						user = inEdit.getHcry();
						// time
					}
					Jsdt jjsdt = DbUtil.getJsdtByBh(getContext(), caseId);
					if (jjsdt != null) {
						if (!TextUtils.isEmpty(jjsdt.getKey())) {
							jsdt.setSelectedItem(jjsdt.getKey());
						}
					}
				} else {
					InspectionEdit inEdit = DbUtil.getInspectionEditByBh(
							getContext(), caseId);
					if (inEdit != null) {
						if (!TextUtils.isEmpty(inEdit.getWfztlb())) {
							illegalSubjectView.setSelectedItem(inEdit
									.getWfztlb());
						}
						if (!TextUtils.isEmpty(inEdit.getWfxz())) {
							illegalStatusView.setSelectedItem(inEdit.getWfxz());
						}
						if (!TextUtils.isEmpty(inEdit.getWjlx())) {
							illegalTypeView.setSelectedItem(inEdit.getWjlx());
						}
						if (!TextUtils.isEmpty(inEdit.getDl())) {
							landUsageView.setSelectedItem(inEdit.getDl());
						}
						inspectResultView.setSelectedItem(keyInspectResult);
						partiesView.setText(inEdit.getWfztmc());
						telView.setText(inEdit.getPhone());
						notesView.setText(inEdit.getCgsm());
						areaView.setText(inEdit.getWfydmj());
						user = inEdit.getHcry();
						// time
					}
					Jsdt jjsdt = DbUtil.getJsdtByBh(getContext(), caseId);
					if (jjsdt != null) {
						if (!TextUtils.isEmpty(jjsdt.getKey())) {
							jsdt.setSelectedItem(jjsdt.getKey());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	double sqm2Mu(double m) {
		return 0.0015d * m;
	}

	double mu2Sqm(double m) {
		return m / 0.0015d;
	}
}
