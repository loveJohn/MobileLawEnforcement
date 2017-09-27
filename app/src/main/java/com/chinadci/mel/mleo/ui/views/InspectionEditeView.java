package com.chinadci.mel.mleo.ui.views;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chinadci.mel.R;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import com.chinadci.mel.android.ui.views.DropDownSpinner;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.IllegalStatusTable;
import com.chinadci.mel.mleo.ldb.IllegalSubjectTable;
import com.chinadci.mel.mleo.ldb.IllegalTypeTable;
import com.chinadci.mel.mleo.ldb.InspectResultTable;
import com.chinadci.mel.mleo.ldb.LandUsageTable;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter;

/**
 * 
 * @ClassName InspectionEditeView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:36:55
 * 
 */
public class InspectionEditeView extends FrameLayout {

	View rootView;
	EditText partiesView;
	EditText notesView;
	EditText telView;
	EditText areaView;
	TextView areaUnitView;

	DropDownSpinner inspectResultView;// 核查结果
	DropDownSpinner illegalSubjectView;// 违法主体
	DropDownSpinner landUsageView;// 地类
	DropDownSpinner illegalStatusView;// 违法现状
	DropDownSpinner illegalTypeView;// 违法类型

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
	Double illegalArea;
	boolean isCurrentUnitSqm = true;
	DecimalFormat kmFormat = new DecimalFormat("#.####");
	AttriAdapter attriAdapter;

	public InspectionEditeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public InspectionEditeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public InspectionEditeView(Context context, Activity activity) {
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
			if (illegalArea != null)
				values.put(CaseInspectTable.field_illegalArea, illegalArea);

			values.put(CaseInspectTable.field_status, 0);
			values.put(CaseInspectTable.field_user, user);
			values.put(CaseInspectTable.field_caseId, caseId);
			values.put(CaseInspectTable.field_parties, parties);
			values.put(CaseInspectTable.field_tel, tel);
			values.put(CaseInspectTable.field_notes, notes);
			values.put(CaseInspectTable.field_landUsage, keyLandUsage);
			values.put(CaseInspectTable.field_inspectResult, keyInspectResult);
			values.put(CaseInspectTable.field_illegalSubject, keyIllegalSubject);
			values.put(CaseInspectTable.field_illegalStatus, keyIllegalStatus);
			values.put(CaseInspectTable.field_illegalType, keyIllegalType);
			values.put(CaseInspectTable.field_mTime,
					TimeFormatFactory2.getSourceTime(new Date()));
			
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

	/**
	 * 
	 * @Title: setCaseSource
	 * @Description: TODO
	 * @param id
	 * @param table
	 * @throws
	 */
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
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
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

	void initView(Context context) {
		rootView = LayoutInflater.from(context).inflate(
				R.layout.view_inspection_edite, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);

		partiesView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_parties);
		telView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_tel);
		illegalStatusView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_illegalstatus);
		illegalTypeView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_illegaltype);
		illegalSubjectView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_illegalsubj);
		inspectResultView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_inspectres);
		landUsageView = (DropDownSpinner) rootView
				.findViewById(R.id.view_inspection_edite_landusage);
		notesView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_notes);
		areaView = (EditText) rootView
				.findViewById(R.id.view_inspection_edite_illegalarea);
		areaUnitView = (TextView) rootView
				.findViewById(R.id.view_inspection_edite_unit);

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
		areaUnitView.setOnClickListener(unitClickListener);

		try {
			ArrayList<KeyValue> inspectResult = DBHelper.getDbHelper(context)
					.getParameters(InspectResultTable.name);
			ArrayList<KeyValue> illegalStatus = DBHelper.getDbHelper(context)
					.getParameters(IllegalStatusTable.name);
			ArrayList<KeyValue> illegalType = DBHelper.getDbHelper(context)
					.getParameters(IllegalTypeTable.name);
			ArrayList<KeyValue> landUsage = DBHelper.getDbHelper(context)
					.getParameters(LandUsageTable.name);
			ArrayList<KeyValue> illegalSubject = DBHelper.getDbHelper(context)
					.getParameters(IllegalSubjectTable.name);

			illegalSubjectView.setData(illegalSubject);
			illegalTypeView.setData(illegalType);
			illegalStatusView.setData(illegalStatus);
			landUsageView.setData(landUsage);
			inspectResultView.setData(inspectResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
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

			default:
				break;
			}

			return null;
		}
	};

	/**
	 * 
	 * @Title: viewCaseInfo
	 * @Description: TODO
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
				}else {
					
					
					
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
