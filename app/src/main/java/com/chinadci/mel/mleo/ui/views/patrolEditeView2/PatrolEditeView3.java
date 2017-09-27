package com.chinadci.mel.mleo.ui.views.patrolEditeView2;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import com.chinadci.mel.android.ui.views.DropDownSpinner;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.AnnexTable;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.CaseSituationTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ldb.PatrolTable;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.activities.polyGatherActivity2.PolyGatherActivity2;
import com.chinadci.mel.mleo.ui.activities.ShowPotosPagerActivity;
import com.chinadci.mel.mleo.ui.activities.TapeActivity;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.fragments.data.model.PatrolsType;
import com.chinadci.mel.mleo.ui.fragments.dialog.GetPhotoDialogFragment;

/**
 */
public class PatrolEditeView3 extends FrameLayout {

	TextView hxfxjg;

	View rootView;
	TextView situationView;//
	EditText notesText;// 备注
	DropDownSpinner dealSpinner;// 处理结果
	TextView redlineView;// 红线
	TextView titleView;// 标题，第n次处置
	ImageButton photoButton;// 拍照
	ImageButton audioButton;// 录音
	View photoLayout;// 照片显示区域
	View audioLayout;// 录音显示区域
	GridView photoGridVeiw;// 照片网格
	GridView audioGridView;// 录音网格
	LinearLayout historyLayout;// 历史处理显示区域
	DatePickerDialog datePickerDialog;// 时间选择框

	private RadioGroup rg;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;

	// 附加内容
	EditText stopNoticeNoText;// 制止通知书编号
	TextView stopNoticeDateText;// 下达时间
	TextView pullDateText;// 计划拆除时间
	EditText pullNumText;// 拆除组织人数
	EditText pullPersonText;// 拆除负责人
	EditText caseNoText;// 立案调查决定书文号
	TextView caseDateText;// 发文时间
	TextView govDateText;// 报告政府时间
	TextView sendDateText;// 转送时间
	TextView addAnnexTips;	//添加附件提示

	View rowStopNoticeNo;// 制止通知书
	View rowStopNoticDate;// 制止通知书下发时间
	View rowPullDate;// 计划拆除时间
	View rowPullNum;// 组织人数
	View rowPullPerson;// 拆除负责人
	View rowCaseNo;// 立案决定书文号
	View rowCaseDate;// 发文时间
	View rowGovDate;// 报告政府时间
	View rowSendDate;// 转送时间

	ImageGridAdapter photoGridAdapter;
	AudioGridAdapter audioGridAdapter;
	LinearLayout.LayoutParams layoutParams;

	String patrolId = "";// 处理编号
	String caseId = "";// 案件编号
	String user = "";// 用户

	String keyDeal = "";// 处理成果说明编号
	String stopNoticeNo = "";// 制止通知书编号
	String stopNoticeDate = "";// 制止通知书下达时间
	String pullPlanDate = "";// 计划拆除时间
	String pullNum = "";// 拆除人数
	String pullPerson = "";// 拆除负责人
	String caseDocumentNo = "";// 案件文号
	String caseDocumentDate = "";// 案件文号下达时间
	String govDate = "";// 报告政府时间
	String sendDate = "";// 转送时间
	String notes = "";// 备注
	String redlineJson = "";// 红线json

	String sourceTable = "";// 数据源
	AttriAdapter attriAdapter;
	Activity parentActivity;
	AlertDialog alertDialog;

	LinearLayout lll;
	
	private ArrayList<String> imageUrlsList= new ArrayList<>();

	public void setPatroEditLayoutVisiblity(int visibility) {
		lll.setVisibility(visibility);
	}

	GetPhotoDialogFragment frag;
	
	/**
	 * 
	 * @Title savePatrol
	 * @Description 保存处理结果数据
	 * @return boolean
	 */
	public boolean savePatrol() {
		boolean succeed = false;
		try {
			conrValues();// 将表单控件中的值赋值给对应变量
			ContentValues values = new ContentValues();

			values.put(PatrolTable.field_caseId, caseId);
			values.put(PatrolTable.field_deal, keyDeal);
			values.put(PatrolTable.field_notes, notes);
			values.put(PatrolTable.field_redline, redlineJson);

			values.put(PatrolTable.field_stopNoticeDate, stopNoticeDate);
			values.put(PatrolTable.field_stopNoticeNo, stopNoticeNo);

			values.put(PatrolTable.field_pullPlanDate, pullPlanDate);
			values.put(PatrolTable.field_pullPlanNum, pullNum);
			values.put(PatrolTable.field_pullPlanPerson, pullPerson);

			values.put(PatrolTable.field_caseDocumentDate, caseDocumentDate);
			values.put(PatrolTable.field_caseDocumentNo, caseDocumentNo);
			values.put(PatrolTable.field_govDate, govDate);
			values.put(PatrolTable.field_sendDate, sendDate);

			values.put(PatrolTable.field_user, user);
			values.put(CasePatrolTable.field_status, 0);

			int count = DBHelper.getDbHelper(getContext()).queryCount(
					CasePatrolTable.name, null, PatrolTable.field_id + "=?",
					new String[] { patrolId });
			if (count > 0) {
				int rows = DBHelper.getDbHelper(getContext()).update(
						CasePatrolTable.name, values,
						CasePatrolTable.field_id + "=?",
						new String[] { patrolId });
				succeed = (rows > 0);
			} else {
				values.put(CasePatrolTable.field_id, patrolId);
				long row = DBHelper.getDbHelper(getContext()).insert(
						CasePatrolTable.name, values);
				succeed = (row > -1);
			}

			DBHelper.getDbHelper(getContext()).delete(CaseAnnexesTable.name,
					CaseAnnexesTable.field_tagId + "=?",
					new String[] { patrolId });
			ArrayList<String> imagePahts = photoGridAdapter.getPaths();
			ArrayList<String> tapePaths = audioGridAdapter.getPaths();
			saveAnnexes(imagePahts, CaseAnnexesTable.name,
					CasePatrolTable.name, patrolId, caseId);
			saveAnnexes(tapePaths, CaseAnnexesTable.name, CasePatrolTable.name,
					patrolId, caseId);
			
			DbUtil.deletePatrolsTypeById(getContext(), patrolId);
			PatrolsType pType = new PatrolsType();
			pType.setId(patrolId);
			pType.setTitle("未发送(保存)");
			pType.setType("1");
			DbUtil.insertPatrolsType(getContext(), pType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return succeed;
	}

	/**
	 * 
	 * @Title getPatrolId
	 * @Description 获取处理情况编号
	 * @return String
	 */
	public String getPatrolId() {
		return this.patrolId;
	}

	/**
	 * 
	 * @Title checkInput
	 * @Description 判定必要项是否有录入
	 * @return String
	 */
	public String checkInput() {
		conrValues();
		if (keyDeal.equals("226") && photoGridAdapter.getCount() < 1) {
			return "请拍摄并上传书面报告";
		}

		if (keyDeal.equals("220")) {
			String sno = stopNoticeNoText.getText().toString();
			if (sno == null || "".equals(sno))
				return "制止通知书编号";

			if (photoGridAdapter.getCount() < 1)
				return "请拍摄通知书并上传";
		}
		return null;
	}

	/**
	 * 
	 * @Title getPatrolJson
	 * @return JSONObject
	 */
	public JSONObject getPatrolJson() {
		try {
			conrValues();

			JSONObject obj = new JSONObject();
			obj.put("user", user);// 处理上报人
			obj.put("caseId", caseId);// 案件编号
			obj.put("clqk", keyDeal);// 处理情况
			obj.put("bz", notes);// 备注

			obj.put("zztzsbh", stopNoticeNo);// 制止通知书编号

			obj.put("zzrs", pullNum);// 组织人数
			obj.put("ccfzr", pullPerson);// 拆除负责人
			obj.put("jhccsj", pullPlanDate);// 计划拆除时间

			obj.put("lajdswh", caseDocumentNo);// 立案决定书文号

			obj.put("xdsj", stopNoticeDate);// 下达时间

			obj.put("fwsj", caseDocumentDate);// 发文时间
			obj.put("bgzfsj", govDate);// 报告政府时间
			obj.put("zssj", sendDate);// 转送时间

			if (redlineJson != null && !redlineJson.equals("")
					&& !redlineJson.equals("null"))
				obj.put("redline", new JSONObject(redlineJson));
			else
				obj.put("redline", JSONObject.NULL);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public PatrolEditeView3(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public PatrolEditeView3(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PatrolEditeView3(Context context) {
		super(context);
		initView(context);
	}

	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	public void addPhotoPaths(String[] paths) {
		if (paths != null && paths.length > 0) {
			for (int i = 0; i < paths.length; i++) {
				File f = new File(paths[i]);
				if (f.exists()) {
					imageUrlsList.add(paths[i]);
					photoGridAdapter.addItem(paths[i]);
				}
			}
			if (photoLayout.getVisibility() != View.VISIBLE)
				photoLayout.setVisibility(View.VISIBLE);
			photoGridAdapter.notifyDataSetChanged();
			if(!hasAnnex()){
				addAnnexTips.setVisibility(VISIBLE);
				addAnnexTips.setText("请提供现场照片或相关证据材料");
			}else{
				addAnnexTips.setVisibility(GONE);
			}
		}
	}

	public void addAudioPath(String path) {
		File f = new File(path);
		if (f.exists()) {
			audioGridAdapter.addItem(path);
			audioGridAdapter.notifyDataSetChanged();
			if (audioLayout.getVisibility() != View.VISIBLE)
				audioLayout.setVisibility(View.VISIBLE);
			if(!hasAnnex()){
				addAnnexTips.setVisibility(VISIBLE);
				addAnnexTips.setText("请提供现场照片或相关证明材料");
			}else{
				addAnnexTips.setVisibility(GONE);
			}
		}
	}

	public void setRedline(String json) {
		if (json != null && json.length() > 0) {
			redlineJson = json;
			redlineView.setSelected(true);
			redlineView.setText(getContext().getString(
					R.string.cn_redlinedisplay));
		} else {
			redlineView.setSelected(false);
			redlineView.setText(R.string.cn_redlinegather);
			redlineJson = null;
		}
	}

	public ArrayList<String> getAnnexes() {
		ArrayList<String> audioPaths = audioGridAdapter.getPaths();
		ArrayList<String> photoPaths = photoGridAdapter.getPaths();
		audioPaths.addAll(photoPaths);
		return audioPaths;
	}

	public void setDataSource(String user, String id, String table) {
		this.patrolId = "ZZ" + TimeFormatFactory2.getIdFormatTime(new Date());
		this.caseId = id;
		this.sourceTable = table;
		this.user = user;
		// display case detail info
		viewPatrolInfo(this.caseId, this.sourceTable);
	}
	
	//add teng.guo start
	String aj_id;
	String xzqh_id;
	String jcbh;
	String redLinecaseId = "";
	String caseTable = "";
	String annexTable = "";
	String caseRedlineJson;
	String geoArrayString;
	
	public void setAj_id(String aj_id) {
		this.aj_id = aj_id;
	}
	
	public void setXzqh_id(String xzqh_id) {
		this.xzqh_id = xzqh_id;
	}
	
	public void setJcbh(String jcbh) {
		this.jcbh = jcbh;
	}
	
	
	public void setCaseDataSource(String id, String caseTable, String annexTable) {
		
		this.redLinecaseId = id;
		this.caseTable = caseTable;
		this.annexTable = annexTable;
		viewCaseInfo(this.redLinecaseId, this.caseTable, this.annexTable);
		
	}
	
	void viewCaseInfo(String id, String cTable, String aTable) {
		try {
			String columns[] = new String[] { InspectionCaseTable.field_id,
					InspectionCaseTable.field_bh,
					InspectionCaseTable.field_address,
					InspectionCaseTable.field_illegalSubject,
					InspectionCaseTable.field_illegalArea,
					InspectionCaseTable.field_illegalStatus,
					InspectionCaseTable.field_illegalType,
					InspectionCaseTable.field_landUsage,
					InspectionCaseTable.field_location,
					InspectionCaseTable.field_notes,
					InspectionCaseTable.field_parties,
					InspectionCaseTable.field_redline,
					InspectionCaseTable.field_mTime,
					InspectionCaseTable.field_source,
					InspectionCaseTable.field_analysis,
					InspectionCaseTable.field_user };
			String selection = InspectionCaseTable.field_id + "=?";
			String args[] = new String[] { id };
			ContentValues caseInfo = DBHelper.getDbHelper(getContext())
					.doQuery(cTable, columns, selection, args);

			if (caseInfo != null) {
				caseRedlineJson = caseInfo
						.getAsString(InspectionCaseTable.field_redline);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//add teng.guo end

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: 初始化视图
	 * @param context
	 */
	@SuppressWarnings("static-access")
	void initView(Context context) {
		imageUrlsList.clear();
		try {
			photoGridAdapter = new ImageGridAdapter(context,
					new ArrayList<String>(), photoClickListener,
					R.layout.view_photo, R.id.view_photo_photo,
					R.id.view_photo_delete);
			audioGridAdapter = new AudioGridAdapter(context,
					new ArrayList<String>(), audioClickListener,
					R.layout.view_audio, R.id.view_audio_index,
					R.id.view_audio_length, R.id.view_audio_delete);

			layoutParams = new LinearLayout.LayoutParams(
					layoutParams.MATCH_PARENT, layoutParams.WRAP_CONTENT);

			rootView = LayoutInflater.from(context).inflate(
					R.layout.view_patrol_edite3, null);
			rootView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			addView(rootView);

			hxfxjg = (TextView) rootView.findViewById(R.id.dkhx_fxjg);
			hxfxjg.setText("---没有分析结果---");
			hxfxjg.setVisibility(View.GONE);

			lll = (LinearLayout) rootView.findViewById(R.id.lll_patro_edit);

			rg = (RadioGroup) rootView.findViewById(R.id.Rbtn2SelectDkType);
			rb1 = (RadioButton) rootView.findViewById(R.id.Rbtn2SelectDkType1);
			rb2 = (RadioButton) rootView.findViewById(R.id.Rbtn2SelectDkType2);
			rb3 = (RadioButton) rootView.findViewById(R.id.Rbtn2SelectDkType3);
			rb1.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					switch (arg0.getCheckedRadioButtonId()) {
					case R.id.Rbtn2SelectDkType1:
						initDealSpinnerData("1");
						break;
					case R.id.Rbtn2SelectDkType2:
						initDealSpinnerData("2");
						break;
					case R.id.Rbtn2SelectDkType3:
						initDealSpinnerData("3");
						break;
					}
				}
			});

			situationView = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_status);
			stopNoticeDateText = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_stopnoticedate);
			stopNoticeNoText = (EditText) rootView
					.findViewById(R.id.view_patrol_edite_stopnoticeno);

			pullNumText = (EditText) rootView
					.findViewById(R.id.view_patrol_edite_pullnum);
			pullPersonText = (EditText) rootView
					.findViewById(R.id.view_patrol_edite_pullperson);
			pullDateText = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_pulldate);

			caseDateText = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_casedate);
			caseNoText = (EditText) rootView
					.findViewById(R.id.view_patrol_edite_caseno);
			dealSpinner = (DropDownSpinner) rootView
					.findViewById(R.id.view_patrol_edite_dealinfo);
			notesText = (EditText) rootView
					.findViewById(R.id.view_patrol_edite_notes);
			govDateText = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_govdate);
			sendDateText = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_sendnoticedate);

			redlineView = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_redline);
			photoButton = (ImageButton) rootView
					.findViewById(R.id.view_patrol_edite_photo);
			audioButton = (ImageButton) rootView
					.findViewById(R.id.view_patrol_edite_audio);
			photoGridVeiw = (GridView) rootView
					.findViewById(R.id.view_media_photogrid);
			audioGridView = (GridView) rootView
					.findViewById(R.id.view_media_audiolist);
			photoLayout = rootView.findViewById(R.id.view_media_photolayout);
			audioLayout = rootView.findViewById(R.id.view_media_audiolayout);
			historyLayout = (LinearLayout) rootView
					.findViewById(R.id.view_patrol_edite_historylayout);
			titleView = (TextView) rootView
					.findViewById(R.id.view_patrol_edite_title);
			addAnnexTips = (TextView) rootView.findViewById(R.id.add_annex_tip);
			rowCaseDate = rootView.findViewById(R.id.view_patrol_row_case_date);
			rowCaseNo = rootView.findViewById(R.id.view_patrol_row_case_no);
			rowStopNoticDate = rootView
					.findViewById(R.id.view_patrol_row_notice_date);
			rowPullDate = rootView.findViewById(R.id.view_patrol_row_pull_date);
			rowPullNum = rootView.findViewById(R.id.view_patrol_row_pull_num);
			rowPullPerson = rootView
					.findViewById(R.id.view_patrol_row_pull_person);
			rowStopNoticeNo = rootView
					.findViewById(R.id.view_patrol_row_notice_no);
			rowSendDate = rootView.findViewById(R.id.view_patrol_row_send_date);

			rowGovDate = rootView.findViewById(R.id.view_patrol_row_gov_date);
			stopNoticeDateText.setText(stopNoticeDate);
			pullDateText.setText(pullPlanDate);
			caseDateText.setText(caseDocumentDate);

			photoGridVeiw.setAdapter(photoGridAdapter);
			audioGridView.setAdapter(audioGridAdapter);
			redlineView.setOnClickListener(viewClickListener);
			photoButton.setOnClickListener(viewClickListener);
			audioButton.setOnClickListener(viewClickListener);
			caseDateText.setOnClickListener(viewClickListener);
			pullDateText.setOnClickListener(viewClickListener);
			govDateText.setOnClickListener(viewClickListener);
			stopNoticeDateText.setOnClickListener(viewClickListener);
			sendDateText.setOnClickListener(viewClickListener);

			dealSpinner
					.setSelectedChangedListener(spinnerSelectedChangedListener);

			// ArrayList<KeyValue> dealValues =
			// DBHelper.getDbHelper(getContext())
			// .getParameters(DealResultTable.name);
			// dealSpinner.setData(dealValues);
			initDealSpinnerData("1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setHxfxjg(String text) {
		hxfxjg.setText(text);
	}
	

	/**
	 * @Description 保存附件数据
	 */
	void saveAnnexes(ArrayList<String> paths, String tableName, String tag,
			String tagId, String caseId) {
		try {
			if (paths != null && paths.size() > 0) {
				for (String s : paths) {
					try {
						File file = new File(s);
						String fileName = file.getName();
						ContentValues aValues = new ContentValues();
						aValues.put(CaseAnnexesTable.field_tagId, tagId);
						aValues.put(CaseAnnexesTable.field_tag, tag);
						aValues.put(CaseAnnexesTable.field_caseId, caseId);
						aValues.put(CaseAnnexesTable.field_path, s);
						aValues.put(CaseAnnexesTable.field_name, fileName);
						DBHelper.getDbHelper(getContext()).insert(tableName,
								aValues);
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title conrValues
	 * @Description 将表单控件中的值赋值给对应变量
	 */
	void conrValues() {
		pullNum = "";
		pullPlanDate = "";
		pullPerson = "";
		caseDocumentDate = "";
		caseDocumentNo = "";
		stopNoticeDate = "";
		stopNoticeNo = "";
		govDate = "";
		sendDate = "";

		notes = notesText.getText().toString();
		if (keyDeal.equals("220")) {// 下达责令停止通知书
			stopNoticeNo = stopNoticeNoText.getText().toString();// 制止通知书编号
			stopNoticeDate = stopNoticeDateText.getText().toString();// 下达日期
		} else if (keyDeal.equals("225")) {// 制定拆除计划
			pullNum = pullNumText.getText().toString();// 组织人数
			pullPerson = pullPersonText.getText().toString();// 拆除负责人
			pullPlanDate = pullDateText.getText().toString();// 计划拆除时间
		} else if (keyDeal.equals("228")) {// 转立案查处
			caseDocumentNo = caseNoText.getText().toString();// 立案编号
			caseDocumentDate = caseDateText.getText().toString();// ...时间
		} else if (keyDeal.equals("226")) {// 报告政府
			govDate = govDateText.getText().toString();// 报告政府时间
		} else if (keyDeal.equals("230")) {// 报告政府
			sendDate = sendDateText.getText().toString();// 报告政府时间
		}
	}

	void viewCaseSituation(String id) {
		try {
			StringBuffer situation = new StringBuffer();
			String columns[] = new String[] {
					CaseSituationTable.field_arguments,
					CaseSituationTable.field_caseSurvey,
					CaseSituationTable.field_pullPlan,
					CaseSituationTable.field_pullSituation,
					CaseSituationTable.field_stopNotice,
					CaseSituationTable.field_stopSituation };
			String selection = new StringBuffer(CaseSituationTable.field_caseId)
					.append("=?").toString();
			String args[] = new String[] { id };
			ContentValues values = DBHelper.getDbHelper(getContext()).doQuery(
					CaseSituationTable.name, columns, selection, args);
			if (values != null) {
				String stopNotice = values
						.getAsString(CaseSituationTable.field_stopNotice);
				String stopSituation = values
						.getAsString(CaseSituationTable.field_stopSituation);
				String pullPlan = values
						.getAsString(CaseSituationTable.field_pullPlan);
				String pullSituation = values
						.getAsString(CaseSituationTable.field_pullSituation);
				String caseSurvey = values
						.getAsString(CaseSituationTable.field_caseSurvey);
				// String argsJson = values
				// .getAsString(CaseSituationTable.field_arguments);
				if (stopNotice != null && !stopNotice.equals(""))
					situation.append("制止通知书：").append(stopNotice).append("\n");
				if (stopSituation != null && !stopSituation.equals(""))
					situation.append("制止情况：").append(stopSituation)
							.append("\n");
				if (pullPlan != null && !pullPlan.equals(""))
					situation.append("拆除计划：").append(pullPlan).append("\n");
				if (pullSituation != null && !pullSituation.equals(""))
					situation.append("拆除情况：").append(pullSituation)
							.append("\n");
				if (caseSurvey != null && !caseSurvey.equals(""))
					situation.append("立案查处：").append(caseSurvey);

				String stext = situation.toString();
				if (stext != null && !stext.equals("")) {
					situationView.setVisibility(View.VISIBLE);
					situationView.setText(stext.endsWith("\n") ? stext
							.substring(0, stext.lastIndexOf("\n")) : stext);
				} else {
					situationView.setVisibility(View.GONE);
				}

				// JSONArray argsArray = new JSONArray(argsJson);
				// if (argsArray != null && args.length > 0) {
				// ArrayList<KeyValue> argsContentValues = new
				// ArrayList<KeyValue>();
				// for (int a = 0; a < argsArray.length(); a++) {
				// JSONObject argObj = argsArray.getJSONObject(a);
				// String key = argObj.getString("key");
				// String value = argObj.getString("value");
				// KeyValue argValues = new KeyValue(key, value);
				// argsContentValues.add(argValues);
				// }
				// dealSpinner.setData(argsContentValues);
				// }
				// 2017 03 14
				initDealSpinnerData("1");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDealSpinnerData(String parent) {
		Map<String, String> argsMap = DbUtil.getclqk_nowValueByParent(
				getContext(), parent);
		if (argsMap != null && argsMap.size() > 0) {
			ArrayList<KeyValue> argsContentValues = new ArrayList<KeyValue>();
			for (Map.Entry<String, String> entry : argsMap.entrySet()) {
				KeyValue argValues = new KeyValue(entry.getKey(),
						entry.getValue());
				argsContentValues.add(argValues);
				Map<String, String> argsMap1 = DbUtil.getclqk_nowValueByParent(
						getContext(), entry.getKey());
				if (argsMap1 != null && argsMap1.size() > 0) {
					for (Map.Entry<String, String> entry1 : argsMap1.entrySet()) {
						KeyValue argValues1 = new KeyValue(entry1.getKey(), "	"
								+ entry1.getValue());
						argsContentValues.add(argValues1);
					}
				}
			}
			dealSpinner.setData(argsContentValues);
		}
	}

	void viewPatrolInfo(String id, String table) {
		try {
			viewCaseSituation(id);
			String columns[] = new String[] { PatrolTable.field_id,
					CasePatrolTable.field_status };

			String selection = new StringBuffer(CasePatrolTable.field_caseId)
					.append("=?").toString();
			String args[] = new String[] { id };
			String order = CasePatrolTable.field_mTime + " desc";
			ArrayList<ContentValues> patrolValues = DBHelper.getDbHelper(
					getContext()).doQuery(table, columns, selection, args,
					null, null, order, null);
			if (patrolValues != null && patrolValues.size() > 0) {
				setTitle(new StringBuffer("第").append(patrolValues.size() + 1)
						.append("次处置").toString());
				for (int i = 0; i < patrolValues.size(); i++) {
					ContentValues values = patrolValues.get(i);
					String titleText = null;
					PatrolsType tPatrolsType = DbUtil.getPatrolsTypeByid(
							getContext(),
							values.getAsString(CasePatrolTable.field_id));
					PatrolsType tPatrolsType_jg = DbUtil.getPatrolsTypeByid(
							getContext(),
							values.getAsString(CasePatrolTable.field_id)
									+ "_jg");
					PatrolsType tPatrolsType_ch = DbUtil.getPatrolsTypeByid(
							getContext(),
							values.getAsString(CasePatrolTable.field_id)
									+ "_ch");
					PatrolsType tPatrolsType_th = DbUtil.getPatrolsTypeByid(
							getContext(),
							values.getAsString(CasePatrolTable.field_id)
									+ "_th");
					if (tPatrolsType != null
							&& tPatrolsType.getType().equals("1")) {
						titleText = tPatrolsType.getTitle();
						int status = values
								.getAsInteger(CasePatrolTable.field_status);
						if (status == 0) {
							setTitle(titleText);
							String columns2[] = new String[] {
									PatrolTable.field_id,
									PatrolTable.field_caseDocumentDate,
									PatrolTable.field_caseDocumentNo,
									PatrolTable.field_caseId,
									PatrolTable.field_deal,
									PatrolTable.field_mTime,
									PatrolTable.field_notes,
									PatrolTable.field_pullInfo,
									PatrolTable.field_pullPlan,
									PatrolTable.field_pullPlanDate,
									PatrolTable.field_pullPlanNum,
									PatrolTable.field_pullPlanPerson,
									PatrolTable.field_govDate,
									PatrolTable.field_redline,
									PatrolTable.field_regCase,
									PatrolTable.field_stopInfo,
									PatrolTable.field_stopNotice,
									PatrolTable.field_stopNoticeDate,
									PatrolTable.field_stopNoticeNo,
									PatrolTable.field_user,
									PatrolTable.field_sendDate };

							String selection2 = new StringBuffer(
									CasePatrolTable.field_id).append("=?")
									.toString();
							String args2[] = new String[] { values
									.getAsString(PatrolTable.field_id) };
							ContentValues values2 = DBHelper.getDbHelper(
									getContext()).doQuery(table, columns2,
									selection2, args2);
							patrolId = values2
									.getAsString(CasePatrolTable.field_id);
							keyDeal = values2
									.getAsString(CasePatrolTable.field_deal);
							if (keyDeal != null && !keyDeal.equals("")) {
								dealSpinner.setSelectedItem(keyDeal);
							}

							caseDocumentDate = values2
									.getAsString(CasePatrolTable.field_caseDocumentDate);
							caseDocumentNo = values2
									.getAsString(CasePatrolTable.field_caseDocumentNo);
							notes = values2
									.getAsString(CasePatrolTable.field_notes);
							govDate = values2
									.getAsString(CasePatrolTable.field_govDate);
							pullPlanDate = values2
									.getAsString(CasePatrolTable.field_pullPlanDate);
							pullNum = values2
									.getAsString(CasePatrolTable.field_pullPlanNum);
							pullPerson = values2
									.getAsString(CasePatrolTable.field_pullPlanPerson);
							redlineJson = values2
									.getAsString(CasePatrolTable.field_redline);

							stopNoticeDate = values2
									.getAsString(CasePatrolTable.field_stopNoticeDate);
							stopNoticeNo = values2
									.getAsString(CasePatrolTable.field_stopNoticeNo);

							sendDate = values2
									.getAsString(CasePatrolTable.field_sendDate);

							notesText.setText(notes);
							stopNoticeDateText.setText(stopNoticeDate);
							stopNoticeNoText.setText(stopNoticeNo);
							pullDateText.setText(pullPlanDate);
							pullNumText.setText(pullNum);
							pullPersonText.setText(pullPerson);
							caseDateText.setText(caseDocumentDate);
							caseNoText.setText(caseDocumentNo);
							govDateText.setText(govDate);
							sendDateText.setText(sendDate);

							if (redlineJson != null && redlineJson.length() > 0) {
								redlineView.setSelected(true);
								redlineView.setText(getContext().getString(
										R.string.cn_redlinedisplay));
							} else {
								redlineView.setSelected(false);
								redlineView.setText(getContext().getString(
										R.string.cn_redlinegather));
							}

							ArrayList<ContentValues> annexes = DBHelper
									.getDbHelper(getContext())
									.doQuery(
											CaseAnnexesTable.name,
											new String[] { CaseAnnexesTable.field_path },
											CaseAnnexesTable.field_tagId
													+ "=? and "
													+ CaseAnnexesTable.field_tag
													+ "=?",
											new String[] {
													values.getAsString(CasePatrolTable.field_id),
													CasePatrolTable.name },
											null, null, null, null);
							Log.i("ydzf", "PatrolEditeView3 annexes="+annexes);
							if (annexes != null && annexes.size() > 0) {
								Log.i("ydzf", "PatrolEditeView3 annexes size="+annexes.size());
								for (int k = 0; k < annexes.size(); k++) {
									String path = annexes.get(k).getAsString(
											AnnexTable.field_path);
									String ext = path.substring(path
											.lastIndexOf("."));
									String mtype = MIMEMapTable.getInstance()
											.getParentMIMEType(ext);
									File f = new File(path);
									if (f.exists()) {
										if (mtype.equalsIgnoreCase("image/*")) {
											if (photoLayout.getVisibility() != View.VISIBLE)
												photoLayout.setVisibility(View.VISIBLE);
											photoGridAdapter.addItem(path);
											
											imageUrlsList.add(path);
										} else if (mtype
												.equalsIgnoreCase("audio/*")) {
											if (audioLayout.getVisibility() != View.VISIBLE)
												audioLayout
														.setVisibility(View.VISIBLE);
											audioGridAdapter.addItem(path);
										}
									}
								}
								photoGridAdapter.notifyDataSetChanged();
								audioGridAdapter.notifyDataSetChanged();
							}
						} else {
							PatrolView3 cview = new PatrolView3(getContext());
							cview.setParentActivity(parentActivity);
							cview.setTitle(titleText);
							cview.setDataSource(values
									.getAsString(CasePatrolTable.field_id),
									CasePatrolTable.name, CaseAnnexesTable.name);
							historyLayout.addView(cview, layoutParams);
						}
						//add teng.guo start
						if(tPatrolsType_th!= null
								&& tPatrolsType_th.getType().equals("1")){
							PatrolView_TH cview = new PatrolView_TH(getContext());
							cview.setParentActivity(parentActivity);
							//cview.setTitle(tPatrolsType_th.getTitle());		//将标题设置放在内容放置内，根据有无内容控制标题显示。
							cview.setDataSource(tPatrolsType_th.getId(),tPatrolsType_th.getTitle());
							historyLayout.addView(cview, layoutParams);
							Log.i("ydzf", "equals");
						}
						//add teng.guo end
					} else {
					}
					if (tPatrolsType_jg != null
							&& tPatrolsType_jg.getType().equals("2")) {
						PatrolView_SP sview = new PatrolView_SP(getContext());
						sview.setParentActivity(parentActivity);
						sview.setTitle(tPatrolsType_jg.getTitle());
						sview.setDataSource(tPatrolsType_jg.getId());
						historyLayout.addView(sview, layoutParams);
					} else {
					}
					if(tPatrolsType_ch!= null
							&& tPatrolsType_ch.getType().equals("3")){
						PatrolView_CH cview = new PatrolView_CH(getContext());
						cview.setParentActivity(parentActivity);
						cview.setTitle(tPatrolsType_ch.getTitle());
						cview.setDataSource(tPatrolsType_ch.getId());
						historyLayout.addView(cview, layoutParams);
					}else{
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("ydzf", "exception="+e.getMessage());
		}
	}

	ISelectedChanged spinnerSelectedChangedListener = new ISelectedChanged() {

		public Object onSelectedChanged(View v, Object o) {
			keyDeal = o.toString();
			Log.i("ydzf","ISelectedChanged keyDeal="+keyDeal);
			rowStopNoticeNo.setVisibility(View.GONE);
			rowStopNoticDate.setVisibility(View.GONE);
			rowCaseDate.setVisibility(View.GONE);
			rowCaseNo.setVisibility(View.GONE);
			rowPullDate.setVisibility(View.GONE);
			rowPullNum.setVisibility(View.GONE);
			rowPullPerson.setVisibility(View.GONE);
			rowGovDate.setVisibility(View.GONE);
			rowSendDate.setVisibility(View.GONE);
			if (keyDeal.equals("000")) {
				initDealSpinnerData("2");
				Toast.makeText(getContext(), "请重新选择精确至其子类型。",Toast.LENGTH_SHORT).show();
				return null;
			}
			if(!hasAnnex()){
				addAnnexTips.setVisibility(VISIBLE);
				addAnnexTips.setText("请提供现场照片或相关证明材料");
			}else{
				addAnnexTips.setVisibility(GONE);
			}
			if (keyDeal.equals("220")) {// 下达责令停止通知书
				rowStopNoticeNo.setVisibility(View.VISIBLE);
				rowStopNoticDate.setVisibility(View.VISIBLE);
				if (stopNoticeDate.equals("")) {
					stopNoticeDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					stopNoticeDateText.setText(stopNoticeDate);
				}
				addAnnexTips.setText("请提供现场照片及制止通知书");
			} else if (keyDeal.equals("244")) {// 下达责令停止通知书并通知乡镇政府
				rowStopNoticeNo.setVisibility(View.VISIBLE);
				rowStopNoticDate.setVisibility(View.VISIBLE);
				if (stopNoticeDate.equals("")) {
					stopNoticeDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					stopNoticeDateText.setText(stopNoticeDate);
				}
				addAnnexTips.setText("请提供现场照片或其他证明材料");
			}else if (keyDeal.equals("225")) {// 制定拆除计划
				rowPullDate.setVisibility(View.VISIBLE);
				rowPullNum.setVisibility(View.VISIBLE);
				rowPullPerson.setVisibility(View.VISIBLE);
				if (pullPlanDate.equals("")) {
					pullPlanDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					pullDateText.setText(pullPlanDate);
				}
			} else if (keyDeal.equals("228")) {// 转立案查处
				rowCaseDate.setVisibility(View.VISIBLE);
				rowCaseNo.setVisibility(View.VISIBLE);
				if (caseDocumentDate.equals("")) {
					caseDocumentDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					caseDateText.setText(caseDocumentDate);
				}
				addAnnexTips.setText("请提供现场照片或其他证明材料");
			}else if (keyDeal.equals("238")) {// 建议转立案查处
				rowCaseDate.setVisibility(View.VISIBLE);
				rowCaseNo.setVisibility(View.VISIBLE);
				if (caseDocumentDate.equals("")) {
					caseDocumentDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					caseDateText.setText(caseDocumentDate);
				}
				addAnnexTips.setText("请提供现场照片或其他证明材料");
			} else if (keyDeal.equals("226")) {		//书面报告政府
				rowGovDate.setVisibility(View.VISIBLE);
				if (govDate.equals("")) {
					govDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					govDateText.setText(govDate);
				}
				addAnnexTips.setText("请提供现场照片或其他证明材料");
			} else if (keyDeal.equals("230")) {		//转相关部门处理
				rowSendDate.setVisibility(View.VISIBLE);
				if (sendDate.equals("")) {
					sendDate = TimeFormatFactory2.getDisplayTimeD(new Date());
					sendDateText.setText(sendDate);
				}
				addAnnexTips.setText("请提供现场照片或其他证明材料");
			}else if (keyDeal.equals("233")) {		//其他合法用地
				addAnnexTips.setText("请提供现场照片及土地使用权证书等相关文件");
			}else if (keyDeal.equals("232")) {		//当年审批
				addAnnexTips.setText("请提供现场照片及审批文件");
			}else if (keyDeal.equals("231")) {		//往年审批
				addAnnexTips.setText("请提供现场照片及审批文件");
			}else if (keyDeal.equals("229")) {		//其他用地
				addAnnexTips.setText("请提供现场照片及相关证据材料");
			}else if (keyDeal.equals("237")) {		//实地伪变化
				addAnnexTips.setText("请提供现场照片");
			}else if (keyDeal.equals("236")) {		//设施农用地
				addAnnexTips.setText("请提供现场照片及备案材料");
			}else if (keyDeal.equals("235")) {		//临时用地
				addAnnexTips.setText("请提供现场照片及临时用地批文");
			}
			return null;
		}
	};
	
	
	public boolean hasAnnex(){
		if(photoGridAdapter.getCount()>0){
			return true;
		}
		if(audioGridAdapter.getCount()>0){
			return true;
		}
		return false;
		
	}

	OnClickListener viewClickListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = null;
			int vid = v.getId();
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			int year = calendar.get(Calendar.YEAR); // 获取Calendar对象中的年
			int month = calendar.get(Calendar.MONTH);// 获取Calendar对象中的月
			int day = calendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天

			switch (vid) {
			case R.id.view_patrol_edite_audio:
				intent = new Intent(parentActivity, TapeActivity.class);
				parentActivity.startActivityForResult(intent,
						Parameters.GET_AUDIO);
				parentActivity.overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			case R.id.view_patrol_edite_photo:
				frag= new GetPhotoDialogFragment(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(parentActivity, CameraPhotoActivity.class);
						parentActivity.startActivityForResult(intent,
								Parameters.GET_PHOTO);
						parentActivity.overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						frag.dismiss();
					}
				},new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent pIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        parentActivity.startActivityForResult(pIntent, Parameters.PICK_IMAGE);
                        parentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        frag.dismiss();
						
						/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				        intent.addCategory(Intent.CATEGORY_OPENABLE);
				        intent.setType("image/*");
						parentActivity.startActivityForResult(intent, Parameters.PICK_IMAGE);
				        parentActivity.overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						frag.dismiss();*/
					}
				});
                frag.show(parentActivity.getFragmentManager(), "dialog");
				break;

			case R.id.view_patrol_edite_redline:
				//add teng.guo start
				Log.i("ydzf","PatrolEditeView3_redline_Button_press");
				if (caseRedlineJson != null && !caseRedlineJson.equals("")) {
					geoArrayString = "[" + caseRedlineJson + "]";
				}
				intent = new Intent(parentActivity, PolyGatherActivity2.class);
				if (redlineJson != null && redlineJson.length() > 0)
					intent.putExtra(PolyGatherActivity2.REDLINE, redlineJson);
				intent.putExtra(Parameters.GEOMETRY, geoArrayString);
				intent.putExtra("ajid", aj_id);
				intent.putExtra("xzqhid", xzqh_id);
				if(!TextUtils.isEmpty(jcbh)){
					intent.putExtra("jcbh", jcbh);
				}else{
					intent.putExtra("jcbh", "");
				}
				//add teng.guo end
				parentActivity.startActivityForResult(intent,
						Parameters.GET_REDLINE);
				parentActivity.overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			case R.id.view_patrol_edite_stopnoticedate:
				datePickerDialog = new DatePickerDialog(parentActivity,
						new OnDateSetListener() {

							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								String month = (monthOfYear + 1) > 9 ? String
										.valueOf(monthOfYear + 1) : "0"
										+ String.valueOf(monthOfYear + 1);
								String day = (dayOfMonth) > 9 ? String
										.valueOf(dayOfMonth) : "0"
										+ String.valueOf(dayOfMonth);
								stopNoticeDate = new StringBuffer("")
										.append(year).append("年").append(month)
										.append("月").append(day).append("日")
										.toString();
								stopNoticeDateText.setText(stopNoticeDate);
							}
						}, year, month, day);
				datePickerDialog.show();
				break;

			case R.id.view_patrol_edite_casedate:
				datePickerDialog = new DatePickerDialog(parentActivity,
						new OnDateSetListener() {

							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								String month = (monthOfYear + 1) > 9 ? String
										.valueOf(monthOfYear + 1) : "0"
										+ String.valueOf(monthOfYear + 1);
								String day = (dayOfMonth) > 9 ? String
										.valueOf(dayOfMonth) : "0"
										+ String.valueOf(dayOfMonth);
								caseDocumentDate = new StringBuffer("")
										.append(year).append("年").append(month)
										.append("月").append(day).append("日")
										.toString();
								caseDateText.setText(caseDocumentDate);
							}
						}, year, month, day);
				datePickerDialog.show();
				break;

			case R.id.view_patrol_edite_pulldate:
				datePickerDialog = new DatePickerDialog(parentActivity,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								String month = (monthOfYear + 1) > 9 ? String
										.valueOf(monthOfYear + 1) : "0"
										+ String.valueOf(monthOfYear + 1);
								String day = (dayOfMonth) > 9 ? String
										.valueOf(dayOfMonth) : "0"
										+ String.valueOf(dayOfMonth);
								pullPlanDate = new StringBuffer("")
										.append(year).append("年").append(month)
										.append("月").append(day).append("日")
										.toString();
								pullDateText.setText(pullPlanDate);
							}
						}, year, month, day);
				datePickerDialog.show();
				break;

			case R.id.view_patrol_edite_govdate:
				datePickerDialog = new DatePickerDialog(parentActivity,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								String month = (monthOfYear + 1) > 9 ? String
										.valueOf(monthOfYear + 1) : "0"
										+ String.valueOf(monthOfYear + 1);
								String day = (dayOfMonth) > 9 ? String
										.valueOf(dayOfMonth) : "0"
										+ String.valueOf(dayOfMonth);
								govDate = new StringBuffer("").append(year)
										.append("年").append(month).append("月")
										.append(day).append("日").toString();
								govDateText.setText(govDate);
							}
						}, year, month, day);
				datePickerDialog.show();
				break;
			case R.id.view_patrol_edite_sendnoticedate:
				datePickerDialog = new DatePickerDialog(parentActivity,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								String month = (monthOfYear + 1) > 9 ? String
										.valueOf(monthOfYear + 1) : "0"
										+ String.valueOf(monthOfYear + 1);
								String day = (dayOfMonth) > 9 ? String
										.valueOf(dayOfMonth) : "0"
										+ String.valueOf(dayOfMonth);
								sendDate = new StringBuffer("").append(year)
										.append("年").append(month).append("月")
										.append(day).append("日").toString();
								sendDateText.setText(sendDate);
							}
						}, year, month, day);
				datePickerDialog.show();
				break;
			default:
				break;
			}
		}
	};

	IClickListener audioClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			final String path = o.toString();
			final View delView = v;
			if (v != null) {

				View alertView = LayoutInflater.from(parentActivity).inflate(
						R.layout.view_alert, null);
				TextView notesView = (TextView) alertView
						.findViewById(R.id.view_alert_notes);
				Button cancelButton = (Button) alertView
						.findViewById(R.id.view_alert_cancel);
				Button doButton = (Button) alertView
						.findViewById(R.id.view_alert_do);
				notesView.setText("确定要删除此附件吗？");
				cancelButton.setText("暂不删除");
				doButton.setText("现在删除");
				cancelButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
					}
				});

				doButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
						audioGridAdapter.deleteItem(path);
						if (audioGridAdapter.getCount() < 1)
							audioLayout.setVisibility(View.GONE);
						Animation anim = AnimationUtils.loadAnimation(
								parentActivity, R.anim.scale_out_center);
						anim.setFillEnabled(true);
						anim.setAnimationListener(new AnimationListener() {
							public void onAnimationStart(Animation animation) {
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationEnd(Animation animation) {
								audioGridAdapter.notifyDataSetChanged();
							}
						});
						delView.startAnimation(anim);
						/*
						File f = new File(path);
						f.delete();	*/		//delete teng.guo
					}
				});

				alertDialog = new AlertDialog.Builder(parentActivity).create();
				alertDialog.show();
				alertDialog.setCancelable(true);
				alertDialog.getWindow().setContentView(alertView);

			} else {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				File f = new File(path);
				Uri mUri = Uri.fromFile(f);
				intent.setDataAndType(mUri, "audio/*");
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
			}
			return null;
		}
	};

	IClickListener photoClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			final String path = o.toString();
			final View delView = v;
			if (v != null) {

				View alertView = LayoutInflater.from(parentActivity).inflate(
						R.layout.view_alert, null);
				TextView notesView = (TextView) alertView
						.findViewById(R.id.view_alert_notes);
				Button cancelButton = (Button) alertView
						.findViewById(R.id.view_alert_cancel);
				Button doButton = (Button) alertView
						.findViewById(R.id.view_alert_do);
				notesView.setText("确定要删除此附件吗？");
				cancelButton.setText("暂不删除");
				doButton.setText("现在删除");
				cancelButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
					}
				});

				doButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
						photoGridAdapter.deleteItem(path);
						if (photoGridAdapter.getCount() < 1)
							photoLayout.setVisibility(View.GONE);
						Animation anim = AnimationUtils.loadAnimation(
								parentActivity, R.anim.scale_out_center);
						anim.setFillEnabled(true);
						anim.setAnimationListener(new AnimationListener() {
							public void onAnimationStart(Animation animation) {
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationEnd(Animation animation) {
								photoGridAdapter.notifyDataSetChanged();
							}
						});
						delView.startAnimation(anim);
						/*
						File f = new File(path);
						f.delete();	*/	//delete teng.guo
					}
				});

				alertDialog = new AlertDialog.Builder(parentActivity).create();
				alertDialog.show();
				alertDialog.setCancelable(true);
				alertDialog.getWindow().setContentView(alertView);

			} else {
				Intent intent = new Intent();
	            intent.setClass(parentActivity, ShowPotosPagerActivity.class);
	            intent.putStringArrayListExtra("imageUrlsList", (ArrayList<String>) imageUrlsList);
	            parentActivity.startActivity(intent);
				/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				Uri mUri = Uri.parse("file://" + path);
				intent.setDataAndType(mUri, "image/*");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				getContext().startActivity(intent);*/
			}
			return null;
		}
	};
}
