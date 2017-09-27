package com.chinadci.mel.mleo.ui.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.android.utils.SharedPreferencesUtils;
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
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.FfckfsTable;
import com.chinadci.mel.mleo.ldb.FkckzTable;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.MineralHcTable;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;

public class MineralHcEditeView extends FrameLayout {
	protected Context context;
	View rootView;
	String sourceTable = "";
	String caseId;

	String user;
	ImageGridAdapter photoGridAdapter;
	AudioGridAdapter audioGridAdapter;
	LinearLayout.LayoutParams layoutParams;
	DatePickerDialog datePickerDialog;

	ImageButton photoButton;
	// ImageButton audioButton;

	TextView dateView;
	DropDownSpinner illegalstatus;// 违法现状
	DropDownSpinner mineralXz;// 违法矿种性质
	DropDownSpinner mineralkz;// 违法现状
	EditText wfztName;// 违法主体名称
	EditText bzTxt;// 违法主体名称
	RadioButton hasffkz;// 是否非法矿;
	RadioButton hastz;// 是否停止非法采矿
	RadioButton noffkz;// 是否非法矿;
	RadioButton notz;// 是否停止非法采矿
	RadioButton hasljqd;//是否立即取缔
	RadioButton noljqd;//是否立即取缔

	LinearLayout historyLayout;
	View photoLayout;
	View audioLayout;
	GridView photoGridVeiw;
	GridView audioGridView;

	String HcId = "";

	TextView redlineView;
	TextView titleView;
	String field_id;// 编号
	String field_hcsj;// 核查时间，形如2014-05-01
	String field_hcrmc;// 核查人员名称
	String field_sfffckd;// 是否非法采矿点，0：否，1：是
	String field_wfztxz;// 违法主体性质，1：个人，2：企业；若无则为空
	String field_wfztmc;// 违法主体名称违法主体名称
	String field_fkckzdm;// 非法开采矿种代码
	String field_ffkcfsdm;// 非法开采方式代码
	String field_sftzffkc;// 是否停止非法开采，0：否，1：是
	String field_sfljqd;//是否立即取缔
	String field_hccomment;// 备注
	String logTime = "";
	String curTime = "";
	String currentUser = "";

	Activity parentActivity;

	/**
	 * 
	 * @Title savePatrol
	 * @Description TODO
	 * @return
	 * @return boolean
	 */
	public boolean savePatrol() {
		boolean succeed = false;
		try {
			conrTexts();
			ContentValues values = new ContentValues();
			values.put(MineralHcTable.field_status, 0);
			values.put(MineralHcTable.field_id, HcId);
			values.put(MineralHcTable.field_caseId, caseId);
			values.put(MineralHcTable.field_hcsj, field_hcsj);
			values.put(MineralHcTable.field_ffkcfs, field_ffkcfsdm);
			values.put(MineralHcTable.field_fkckz, field_fkckzdm);
			values.put(MineralHcTable.field_hccomment, field_hccomment);
			values.put(MineralHcTable.field_hcrmc, field_hcrmc);
			values.put(MineralHcTable.field_sfffckd, field_sfffckd);
			values.put(MineralHcTable.field_sftzffkc, field_sftzffkc);
			values.put(MineralHcTable.field_wfztmc, field_wfztmc);
			values.put(MineralHcTable.field_wfztxz, field_wfztxz);
			values.put(MineralHcTable.field_sfljqd, field_sfljqd);

			int count = DBHelper.getDbHelper(getContext()).queryCount(
					MineralHcTable.name, null, MineralHcTable.field_id + "=?",
					new String[] { HcId });

			if (count > 0) {
				int rows = DBHelper.getDbHelper(getContext()).update(
						MineralHcTable.name, values,
						MineralHcTable.field_id + "=?", new String[] { HcId });
				succeed = (rows > 0);
			} else {
				values.put(MineralHcTable.field_id, HcId);
				long row = DBHelper.getDbHelper(getContext()).insert(
						MineralHcTable.name, values);
				succeed = (row > -1);
			}

			DBHelper.getDbHelper(getContext()).delete(
					MilPatrolAnnexesTable.name,
					MilPatrolAnnexesTable.field_tagId + "=?",
					new String[] { HcId });
			ArrayList<String> imagePahts = photoGridAdapter.getPaths();
			ArrayList<String> tapePaths = audioGridAdapter.getPaths();
			saveAnnexes(imagePahts, MilPatrolAnnexesTable.name,
					MineralHcTable.name, HcId, caseId);
			saveAnnexes(tapePaths, MilPatrolAnnexesTable.name,
					MineralHcTable.name, HcId, caseId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return succeed;
	}

	/**
	 * 
	 * @Title saveAnnexes
	 * @Description TODO
	 * @param paths
	 * @param tableName
	 * @param tag
	 * @param tagId
	 * @param caseId
	 *            void
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
	 * @Title getPatrolId
	 * @Description TODO
	 * @return String
	 */
	public String getPatrolId() {
		return this.HcId;
	}

	/**
	 * 
	 * @Title getPatrolJson
	 * @Description TODO
	 * @return JSONObject
	 */
	public JSONObject getPatrolJson() {
		boolean succeed = false;
		try {
			conrValues();
			JSONObject obj = new JSONObject();
			obj.put("hcrmc", user);// 处理上报人
			obj.put("case_id", caseId);// 案件编号
			// obj.put("userArea", stopNotice);// 区域编码
			obj.put("sfffckd", field_sfffckd);// 是否非法采矿点
			obj.put("wfztxz", field_wfztxz);// 违法主体性质
			obj.put("wfztmc", field_wfztmc);// 违法主体名称
			obj.put("fkckz", field_fkckzdm);// 非法开采矿种
			obj.put("ffkcfs", field_ffkcfsdm);// 非法开采方式
			obj.put("sftzffkc", field_sftzffkc);// 谋划拆除时间
			obj.put("hccomment", field_hccomment);// 组织人数
			obj.put("sfljqd", field_sfljqd);// 组织人数
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String str = formatter.format(curDate);
			obj.put("hcsj", str);// 发文时间
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	void conrTexts() {

		field_hcsj = curTime;// dateView.getText().toString();

		field_sfffckd = hasffkz.isChecked() ? "1" : "0";// 是否非法采矿点，0：否，1：是
		// field_wfztxz="1";// 违法主体性质，1：个人，2：企业；若无则为空
		field_wfztmc = wfztName.getText().toString();// 违法主体名称违法主体名称
		// field_fkckzdm=mineralkz.getSelectedValue().toString();
		// field_ffkcfsdm=mineralXz.getSelectedValue().toString();// 非法开采方式代码
		field_sftzffkc = hastz.isChecked() ? "1" : "0";// "0";//
														// 是否停止非法开采，0：否，1：是
		field_hccomment = bzTxt.getText().toString();// 备注
		field_sfljqd=hasljqd.isChecked()?"1" : "0";// "0";//
	}

	void conrValues() {

		field_hcsj = curTime;// dateView.getText().toString();

		field_sfffckd = hasffkz.isChecked() ? "1" : "0";// 是否非法采矿点，0：否，1：是
		// field_wfztxz="1";// 违法主体性质，1：个人，2：企业；若无则为空
		field_wfztmc = wfztName.getText().toString();// 违法主体名称违法主体名称
		// field_fkckzdm=mineralkz.getSelectedKey().toString();
		// field_ffkcfsdm=mineralXz.getSelectedKey().toString();// 非法开采方式代码
		field_sftzffkc = hastz.isChecked() ? "1" : "0";// "0";//
		field_sfljqd=hasljqd.isChecked()?"1" : "0";
														// 是否停止非法开采，0：否，1：是
		field_hccomment = bzTxt.getText().toString();// 备注

	}

	public void addPhotoPaths(String[] paths) {
		if (paths != null && paths.length > 0) {
			for (int i = 0; i < paths.length; i++) {
				File f = new File(paths[i]);
				if (f.exists()) {
					photoGridAdapter.addItem(paths[i]);
					photoGridAdapter.notifyDataSetChanged();
					if (photoLayout.getVisibility() != View.VISIBLE)
						photoLayout.setVisibility(View.VISIBLE);
				}
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
		}
	}

	public ArrayList<String> getAnnexes() {
		ArrayList<String> audioPaths = audioGridAdapter.getPaths();
		ArrayList<String> photoPaths = photoGridAdapter.getPaths();
		audioPaths.addAll(photoPaths);
		return audioPaths;
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

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public MineralHcEditeView(Context context, Activity activity) {
		super(context);
		initView(activity);
		context = context;
		// TODO Auto-generated constructor stub
	}

	void initView(Context context) {
		try {
			field_hcrmc = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_actuser, "");
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			int year = calendar.get(Calendar.YEAR); // 获取Calendar对象中的年
			int month = calendar.get(Calendar.MONTH);// 获取Calendar对象中的月
			int day = calendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
			datePickerDialog = new DatePickerDialog(context, dateListener,
					year, month, day);

			// photoGridAdapter = new ImageGridAdapter(context, null,
			// photoClickListener);
			// audioGridAdapter = new AudioGridAdapter(context, null,
			// audioClickListener);

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
					R.layout.view_mineralhc_edite, null);
			rootView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			historyLayout = (LinearLayout) rootView
					.findViewById(R.id.view_mineral_hc_edite_historylayout);

			photoButton = (ImageButton) rootView
					.findViewById(R.id.view_mineral_hc_edite_photo);

			titleView = (TextView) rootView
					.findViewById(R.id.view_mineral_edite_title);

			wfztName = (EditText) rootView
					.findViewById(R.id.fragment_mineral_hc_edit_wfztn);

			bzTxt = (EditText) rootView
					.findViewById(R.id.fragment_mineral_log_edite_notes);

			dateView = (TextView) rootView
					.findViewById(R.id.fragment_mineral_hc_edit_date);
			dateView.setOnClickListener(clickListener);
			Date date = new Date();
			logTime = TimeFormatFactory2.getDisplayTimeD(date);
			dateView.setText(logTime);

			// curTime=TimeFormatFactory2.getIdFormatTimeS(date);

			illegalstatus = (DropDownSpinner) rootView
					.findViewById(R.id.fragment_hc_edite_illegalstatus);
			illegalstatus
					.setSelectedChangedListener(spinnerSelectedChangedListener);

			mineralXz = (DropDownSpinner) rootView
					.findViewById(R.id.fragment_hc_edite_mineralType);
			mineralXz
					.setSelectedChangedListener(spinnerSelectedChangedListener);

			mineralkz = (DropDownSpinner) rootView
					.findViewById(R.id.fragment_hc_edite_acType);
			mineralkz
					.setSelectedChangedListener(spinnerSelectedChangedListener);

			hasffkz = (RadioButton) rootView
					.findViewById(R.id.fragment_mineral_hc_edite_radio_t);
			hastz = (RadioButton) rootView
					.findViewById(R.id.fragment_mineral_hcStop_edite_radio_t);
			hasljqd= (RadioButton) rootView
					.findViewById(R.id.fragment_mineral_ljqdStop_edite_radio_t);

			noffkz = (RadioButton) rootView
					.findViewById(R.id.fragment_mineral_hc_edite_radio_f);
			notz = (RadioButton) rootView
					.findViewById(R.id.fragment_mineral_hcStop_edite_radio_f);
			noljqd = (RadioButton) rootView
					.findViewById(R.id.fragment_mineral_ljqdStop_edite_radio_f);
			

			photoButton.setOnClickListener(viewClickListener);
			// audioButton.setOnClickListener(viewClickListener);
			photoGridVeiw = (GridView) rootView
					.findViewById(R.id.view_media_photogrid);
			audioGridView = (GridView) rootView
					.findViewById(R.id.view_media_audiolist);
			photoLayout = rootView.findViewById(R.id.view_media_photolayout);
			audioLayout = rootView.findViewById(R.id.view_media_audiolayout);

			photoGridVeiw.setAdapter(photoGridAdapter);
			audioGridView.setAdapter(audioGridAdapter);
			try {

				// ArrayList<KeyValue> illegalSubject =
				// DBHelper.getDbHelper(context)
				// .getParameters(FfckfsTable.name);
				// 违法主体
				ArrayList<KeyValue> illegalSubject = new ArrayList<KeyValue>();
				illegalSubject.add(new KeyValue("1", "个人"));
				illegalSubject.add(new KeyValue("2", "企业"));
				illegalstatus.setData(illegalSubject);

				ArrayList<KeyValue> mineralkzSubject = DBHelper.getDbHelper(
						context).getParameters(FkckzTable.name);
				mineralkz.setData(mineralkzSubject);

				ArrayList<KeyValue> mineralTypeSubject = DBHelper.getDbHelper(
						context).getParameters(FfckfsTable.name);
				mineralXz.setData(mineralTypeSubject);
			} catch (Exception e) {

			}

			addView(rootView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ISelectedChanged spinnerSelectedChangedListener = new ISelectedChanged() {

		public Object onSelectedChanged(View v, Object o) {
			int vid = v.getId();
			switch (vid) {
			case R.id.fragment_hc_edite_illegalstatus:
				field_wfztxz = o.toString();
				break;

			case R.id.fragment_hc_edite_mineralType:
				field_ffkcfsdm = o.toString();
				break;

			case R.id.fragment_hc_edite_acType:
				field_fkckzdm = o.toString();
				break;

			default:
				break;
			}
			return null;
		}
	};

	OnClickListener viewClickListener = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = null;
			int vid = v.getId();

			switch (vid) {
			case R.id.view_mineral_hc_edite_photo:
				intent = new Intent(parentActivity, CameraPhotoActivity.class);
				parentActivity.startActivityForResult(intent,
						Parameters.GET_PHOTO);
				parentActivity.overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;
			default:
				break;
			}
		}
	};

	public String checkInput() {
		// getTextViewValues();
		// if (parties == null || parties.trim().equals("")) {
		// return "请填写个人/单位";
		// }
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
		this.HcId = "KC" + TimeFormatFactory2.getIdFormatTime(new Date());
		this.sourceTable = table;
		this.caseId = caseId;
		this.user = user;

		// display case detail info
		viewInspectionInfo(this.caseId, this.sourceTable);
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vid = v.getId();
			Intent intent = null;
			switch (vid) {
			case R.id.fragment_mineral_hc_edit_date:
				datePickerDialog.show();
				break;
			default:
				break;
			}
		}
	};

	DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String month = (monthOfYear + 1) > 9 ? String
					.valueOf(monthOfYear + 1) : "0"
					+ String.valueOf(monthOfYear + 1);
			String day = (dayOfMonth) > 9 ? String.valueOf(dayOfMonth) : "0"
					+ String.valueOf(dayOfMonth);
			curTime = new StringBuffer("").append(year).append("-")
					.append(month).append("-").append(day).toString();
			logTime = new StringBuffer("").append(year).append("年")
					.append(month).append("月").append(day).append("日")
					.toString();
			dateView.setText(logTime);
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
		try {
			String columns[] = new String[] { MineralHcTable.field_id,
					CasePatrolTable.field_status };

			String selection = new StringBuffer(MineralHcTable.field_caseId)
					.append("=?").toString();
			String args[] = new String[] { id };
			String order = MineralHcTable.field_hcsj + " desc";
			ArrayList<ContentValues> patrolValues = DBHelper.getDbHelper(
					getContext()).doQuery(table, columns, selection, args,
					null, null, order, null);
			if (patrolValues != null && patrolValues.size() > 0) {
				setTitle(new StringBuffer("第").append(patrolValues.size() + 1)
						.append("次处置").toString());
				for (int i = 0; i < patrolValues.size(); i++) {
					ContentValues values = patrolValues.get(i);
					String titleText = new StringBuffer("第")
							.append(patrolValues.size() - i).append("次处置")
							.toString();
					int status = values
							.getAsInteger(MineralHcTable.field_status);
					if (status == 0) {
						setTitle(titleText);
						String columns2[] = new String[] {
								MineralHcTable.field_id,
								MineralHcTable.field_ffkcfs,
								MineralHcTable.field_fkckz,
								MineralHcTable.field_caseId,
								MineralHcTable.field_hccomment,
								MineralHcTable.field_hcrmc,
								MineralHcTable.field_hcsj,
								MineralHcTable.field_sfffckd,
								MineralHcTable.field_sftzffkc,
								MineralHcTable.field_status,
								MineralHcTable.field_wfztmc,
								MineralHcTable.field_wfztxz,
								MineralHcTable.field_sfljqd};

						String selection2 = new StringBuffer(
								MineralHcTable.field_id).append("=?")
								.toString();
						String args2[] = new String[] { values
								.getAsString(MineralHcTable.field_id) };
						ContentValues values2 = DBHelper.getDbHelper(
								getContext()).doQuery(table, columns2,
								selection2, args2);

						field_id = values2.getAsString(MineralHcTable.field_id);
						field_hcsj = values2
								.getAsString(MineralHcTable.field_hcsj);
						field_sfffckd = values2
								.getAsString(MineralHcTable.field_sfffckd);
						field_wfztxz = values2
								.getAsString(MineralHcTable.field_wfztxz);
						field_wfztmc = values2
								.getAsString(MineralHcTable.field_wfztmc);
						field_fkckzdm = values2
								.getAsString(MineralHcTable.field_fkckz);
						field_ffkcfsdm = values2
								.getAsString(MineralHcTable.field_ffkcfs);
						field_sftzffkc = values2
								.getAsString(MineralHcTable.field_sftzffkc);
						field_hccomment = values2
								.getAsString(MineralHcTable.field_hccomment);
						field_sfljqd=values2
								.getAsString(MineralHcTable.field_sfljqd);

						// field_hcsj=curTime;//dateView.getText().toString();

						if (field_sfffckd.equals("1")) {
							hasffkz.setChecked(true);
							noffkz.setChecked(false);
						}else
						{
							noffkz.setChecked(true);
							hasffkz.setChecked(false);
						}// 是否非法采矿点，0：否，1：是
							// field_wfztxz="1";// 违法主体性质，1：个人，2：企业；若无则为空
						illegalstatus.setSelectedItem(field_wfztxz);
						wfztName.setText(field_wfztmc);// 违法主体名称违法主体名称
						mineralkz.setSelectedItem(field_fkckzdm);
						mineralXz.setSelectedItem(field_ffkcfsdm);// 非法开采方式代码

						if (field_sftzffkc.equals("1")) {
							hastz.setChecked(true);
							notz.setChecked(false);
						}else
						{
							hastz.setChecked(false);
							notz.setChecked(true);
						}
						if (field_sfljqd.equals("1")) {
							hasljqd.setChecked(true);
							noljqd.setChecked(false);
						}else
						{
							hasljqd.setChecked(false);
							noljqd.setChecked(true);
						}
						
						// 是否非法采矿点，0：否，1：是
							// field_sftzffkc=hastz.isChecked()?"1":"0";//"0";//
							// 是否停止非法开采，0：否，1：是
						bzTxt.setText(field_hccomment);// 备注

						dateView.setText(field_hcsj);

						ArrayList<ContentValues> annexes = DBHelper
								.getDbHelper(getContext())
								.doQuery(
										MilPatrolAnnexesTable.name,
										new String[] { MilPatrolAnnexesTable.field_path },
										MilPatrolAnnexesTable.field_tagId
												+ "=? and "
												+ MilPatrolAnnexesTable.field_tag
												+ "=?",
										new String[] {
												values.getAsString(MineralHcTable.field_id),
												MineralHcTable.name }, null,
										null, null, null);
						if (annexes != null && annexes.size() > 0) {
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
											photoLayout
													.setVisibility(View.VISIBLE);
										photoGridAdapter.addItem(path);
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

						MineralHcView cview = new MineralHcView(getContext());
						cview.setParentActivity(parentActivity);
						cview.setTitle(titleText);
						cview.setDataSource(
								values.getAsString(MineralHcTable.field_id),
								MineralHcTable.name, MilPatrolAnnexesTable.name);
						historyLayout.addView(cview, layoutParams);
					}
				}
			} else {
				setTitle("第1次核查");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	public boolean getTextViewValues() {

		return true;
	}

	IClickListener audioClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			String path = o.toString();
			if (v != null) {
				audioGridAdapter.deleteItem(path);
				if (audioGridAdapter.getCount() < 1)
					audioLayout.setVisibility(View.GONE);
				Animation anim = AnimationUtils.loadAnimation(getContext(),
						R.anim.scale_out_center);
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
				v.startAnimation(anim);
				File f = new File(path);
				f.delete();
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
			String path = o.toString();
			if (v != null) {
				photoGridAdapter.deleteItem(path);
				if (photoGridAdapter.getCount() < 1)
					photoLayout.setVisibility(View.GONE);
				Animation anim = AnimationUtils.loadAnimation(getContext(),
						R.anim.scale_out_center);
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
				v.startAnimation(anim);
				File f = new File(path);
				f.delete();
			} else {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				Uri mUri = Uri.parse("file://" + path);
				intent.setDataAndType(mUri, "image/*");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				getContext().startActivity(intent);
			}
			return null;
		}
	};

}
