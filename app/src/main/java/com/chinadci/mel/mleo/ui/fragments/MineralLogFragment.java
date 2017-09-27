package com.chinadci.mel.mleo.ui.fragments;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.library.http.entity.MultipartEntity;
import com.andbase.library.http.entity.mine.content.ContentBody;
import com.andbase.library.http.entity.mine.content.FileBody;
import com.andbase.library.http.entity.mine.content.StringBody;
import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.LocationUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.KeyValue;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.android.core.interfaces.ISelectedChanged;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.DropDownSpinner;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.FFCKDTable;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ui.activities.AdminChooserActivity;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.activities.PolyGatherActivity;
import com.chinadci.mel.mleo.ui.activities.TapeActivity;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;

public class MineralLogFragment extends ContentFragment {
	View rootView;
	EditText lineView;
	EditText notesView;
	EditText exceptionView;
	TextView adminView;
	TextView dateView;
	TextView redlineView;
	ImageView photoView;
	// ImageView audioView;
	GridView photoGrid;
	GridView audioGrid;
	LinearLayout photoLayout;
	LinearLayout audioLayout;
	RadioButton noMiningView;
	RadioButton hasMiningView;

	String adminCode = "350000";
	String adminName = "福建省";

	RadioGroup radioGroup;

	DropDownSpinner ffckbhView;
	// 巡查类型
	DropDownSpinner xkTypeView;
	EditText zzwsbhView;
	RadioButton noZzView;
	RadioButton hasZzView;

	TableRow rowZZWS;
	TableRow rowSFZZ;

	String logId = "";
	String location = "";
	String redline = "";
	String line = "";
	String exception = "";
	String notes = "";
	String logTime = "";
	String cxType = "2";

	boolean hasMining = false;

	String ffckbh = "";
	String zzwsbh = "";
	boolean haszz = false;

	ImageGridAdapter photoGridAdapter;
	AudioGridAdapter audioGridAdapter;
	DatePickerDialog datePickerDialog;
	AlertDialog alertDialog;
	CircleProgressBusyView circleProgressView;
	DecimalFormat kmFormat = new DecimalFormat("##");

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Parameters.GET_REDLINE) {
			if (resultCode != Activity.RESULT_OK)
				return;
			Bundle bundle = data.getExtras();
			redline = bundle.getString(PolyGatherActivity.REDLINE).toString();
			if (redline != null && redline.length() > 0) {
				redlineView.setSelected(true);
				redlineView.setText(getString(R.string.cn_redlinedisplay));
			} else {
				redlineView.setSelected(false);
				redlineView.setText(R.string.cn_redlinegather);
			}
		} else if (requestCode == Parameters.GET_PHOTO) {
			Bundle bundle = data.getExtras();
			String photos[] = bundle
					.getStringArray(CameraPhotoActivity.PHOTOARRAY);
			if (photos != null && photos.length > 0) {
				for (int j = 0; j < photos.length; j++)
					photoGridAdapter.addItem(photos[j]);
				photoGridAdapter.notifyDataSetChanged();
				photoLayout.setVisibility(View.VISIBLE);
			}
		} else if (requestCode == Parameters.GET_AUDIO) {
			Bundle bundle = data.getExtras();
			String amrPath = bundle.getString(TapeActivity.AMRFILE);
			audioGridAdapter.addItem(amrPath);
			audioGridAdapter.notifyDataSetChanged();
			audioLayout.setVisibility(View.VISIBLE);
		} else if (requestCode == Parameters.GET_ADMIN) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();// 所在村居
				adminCode = bundle.getString(AdminChooserActivity.ADMIN_CODE);
				adminName = DBHelper.getDbHelper(context).queryAdminFullName(
						adminCode);
				adminView.setText(adminName);
				ffckbhView.setData(null);
				
//				if (adminCode.length() == 9)
					// getFFCKPar(adminCode);
					new GetFfckd().execute();
//				else if(adminCode.length()==12)
//				{
//					adminCode=adminCode.substring(0, 8);
//					new GetFfckd().execute();
//				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
		String admin[] = DBHelper.getDbHelper(context)
				.getUserAdmin(currentUser);
		if (admin != null) {
			if (!admin[0].equals("") && !admin[1].equals("")) {
				adminCode = admin[0];
				adminName = admin[1];
				
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		logId = getArguments().getString("LOGID", "");
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

		circleProgressView = new CircleProgressBusyView(context);
		alertDialog = new AlertDialog.Builder(getActivity()).create();
		rootView = LayoutInflater.from(context).inflate(
				R.layout.fragment_minerallog_edite, null);
		lineView = (EditText) rootView
				.findViewById(R.id.fragment_mineral_log_edite_line);
		notesView = (EditText) rootView
				.findViewById(R.id.fragment_mineral_log_edite_notes);
		exceptionView = (EditText) rootView
				.findViewById(R.id.fragment_mineral_log_edite_problem);
		noMiningView = (RadioButton) rootView
				.findViewById(R.id.fragment_mineral_log_edite_radio);
		hasMiningView = (RadioButton) rootView
				.findViewById(R.id.fragment_mineral_log_edite_radio_t);
		dateView = (TextView) rootView
				.findViewById(R.id.fragment_mineral_log_edite_date);

		adminView = (TextView) rootView
				.findViewById(R.id.fragment_case_edite_admin);
		adminView.setText(adminName);
		adminView.setOnClickListener(clickListener);

		photoGrid = (GridView) rootView.findViewById(R.id.view_media_photogrid);
		audioGrid = (GridView) rootView.findViewById(R.id.view_media_audiolist);
		photoLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_photolayout);
		audioLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_audiolayout);
		photoView = (ImageButton) rootView
				.findViewById(R.id.fragment_mineral_log_edite_camera);
		// audioView = (ImageButton) rootView
		// .findViewById(R.id.fragment_mineral_log_edite_tape);
		redlineView = (TextView) rootView
				.findViewById(R.id.fragment_mineral_log_edite_redline);

		radioGroup = (RadioGroup) rootView
				.findViewById(R.id.fragment_mineral_log_edite_radio_g);

		radioGroup.setOnCheckedChangeListener(rgOnCheckedChangeListener);
		// 非法采矿点编号
		ffckbhView = (DropDownSpinner) rootView
				.findViewById(R.id.fragment_mineral_log_edite_code);
		ffckbhView.setSelectedChangedListener(spinnerSelectedChangedListener);

		if (adminCode.length() == 9)
			// getFFCKPar(adminCode);
			new GetFfckd().execute();
		
		xkTypeView = (DropDownSpinner) rootView
				.findViewById(R.id.fragment_mineral_log_edite_type);
		xkTypeView.setSelectedChangedListener(spinnerSelectedChangedListener);

		ArrayList<KeyValue> xkList = new ArrayList<KeyValue>();
		xkList.add(new KeyValue("2", "区域巡查"));
		xkList.add(new KeyValue("1", "固定点巡查"));
		xkTypeView.setData(xkList);

		rowZZWS = (TableRow) rootView.findViewById(R.id.RowZZWS);
		rowSFZZ = (TableRow) rootView.findViewById(R.id.RowSFZZ);

		// try{
		//
		// ArrayList<KeyValue> ffckdSubject = DBHelper.getDbHelper(context)
		// .getParameters(FFCKDTable.name);
		// ffckbhView.setData(ffckdSubject);
		// }catch(Exception e){
		//
		// }

		// 是否制止非法行为
		noZzView = (RadioButton) rootView
				.findViewById(R.id.fragment_mineral_log_edite_radio_zz);
		hasZzView = (RadioButton) rootView
				.findViewById(R.id.fragment_mineral_log_edite_radio_zz_t);
		// 制止文书编号
		zzwsbhView = (EditText) rootView
				.findViewById(R.id.fragment_mineral_log_edite_zzws);

		// noMiningView.setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		//
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// hasMining = !isChecked;
		// }
		// });
		//
		// hasMiningView.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		//
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// hasMining = isChecked;
		// }
		// });
		//
		// noZzView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// haszz = !isChecked;
		// }
		// });
		//
		// hasZzView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// haszz = isChecked;
		// }
		// });

		redlineView.setOnClickListener(clickListener);
		photoView.setOnClickListener(clickListener);
		// audioView.setOnClickListener(clickListener);
		dateView.setOnClickListener(clickListener);
		photoGrid.setAdapter(photoGridAdapter);
		audioGrid.setAdapter(audioGridAdapter);
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		int year = calendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		int month = calendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		int day = calendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
		datePickerDialog = new DatePickerDialog(getActivity(), dateListener,
				year, month, day);
		initLog(logId);
		return rootView;
	}

	ISelectedChanged spinnerSelectedChangedListener = new ISelectedChanged() {

		public Object onSelectedChanged(View v, Object o) {
			int vid = v.getId();
			switch (vid) {
			case R.id.fragment_mineral_log_edite_code:
				ffckbh = o.toString();
				break;
			case R.id.fragment_mineral_log_edite_type:
				cxType = o.toString();
				break;
			default:
				break;
			}
			return null;
		}
	};

	@Override
	public void handle(Object o) {
		super.handle(o);
		if (lineView.getText().toString() != null
				&& !"".equals(lineView.getText().toString()))
			line = lineView.getText().toString();

		if (exceptionView.getText().toString() != null
				&& !"".equals(exceptionView.getText().toString()))
			exception = exceptionView.getText().toString();

		if (notesView.getText().toString() != null
				&& !"".equals(notesView.getText().toString()))
			notes = notesView.getText().toString();

//		if (ffckbhView.getText().toString() != null
//				&& !"".equals(ffckbhView.getText().toString()))
//			ffckbh = ffckbhView.getSelectedKey().toString();

		if (zzwsbhView.getText().toString() != null
				&& !"".equals(zzwsbhView.getText().toString()))
			zzwsbh = zzwsbhView.getText().toString();
		int tag = (Integer) o;
		if (tag == 0) {// 保存日志
			new SaveTask().execute();
		} else if (tag == 1) {// 发送日志
			new SendTask2().execute();
		}
	}

	private RadioGroup.OnCheckedChangeListener rgOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

		public void onCheckedChanged(RadioGroup group, int arg1) {
			if (arg1 == noMiningView.getId()) {
				rowZZWS.setVisibility(TableRow.GONE);
				rowSFZZ.setVisibility(TableRow.GONE);

				// // 非法采矿点编号
				// ffckbhView.setCursorVisible(false);
				// ffckbhView.setFocusable(false);
				// ffckbhView.setFocusableInTouchMode(false);
				// ffckbhView.setEnabled(false);
				// // 是否制止非法行为
				// noZzView.setEnabled(false);
				// hasZzView.setEnabled(false);
				// // 制止文书编号
				// zzwsbhView.setEnabled(false);
				// zzwsbhView.setCursorVisible(false);
				// zzwsbhView.setFocusable(false);
				// zzwsbhView.setFocusableInTouchMode(false);
			} else if (arg1 == hasMiningView.getId()) {
				rowZZWS.setVisibility(TableRow.VISIBLE);
				rowSFZZ.setVisibility(TableRow.VISIBLE);
				// ffckbhView.setEnabled(true);
				// ffckbhView.setCursorVisible(true);
				// ffckbhView.setFocusable(true);
				// ffckbhView.setFocusableInTouchMode(true);
				// // 是否制止非法行为
				// noZzView.setEnabled(true);
				// hasZzView.setEnabled(true);
				// // 制止文书编号
				// zzwsbhView.setEnabled(true);
				// zzwsbhView.setCursorVisible(true);
				// zzwsbhView.setFocusable(true);
				// zzwsbhView.setFocusableInTouchMode(true);
			}
		}
	};

	void initLog(String id) {
		if (id != null && !"".equals(id)) {
			try {
				String columns[] = new String[] {
						MilPatrolTable.field_exception,
						MilPatrolTable.field_hasMining,
						MilPatrolTable.field_haszz,
						MilPatrolTable.field_ffckbh,
						MilPatrolTable.field_zzwsbh, MilPatrolTable.field_id,
						MilPatrolTable.field_line, MilPatrolTable.field_xcrzxl,
						MilPatrolTable.field_logTime,
						MilPatrolTable.field_notes, MilPatrolTable.field_szcj,
						MilPatrolTable.field_redline };
				String selection = new StringBuffer(MilPatrolTable.field_id)
						.append("=?").toString();
				String args[] = new String[] { id };
				ContentValues values = DBHelper.getDbHelper(context).doQuery(
						MilPatrolTable.name, columns, selection, args);
				int mining = values
						.getAsInteger(MilPatrolTable.field_hasMining);
				hasMining = (mining == 1) ? true : false;

				exception = values.getAsString(MilPatrolTable.field_exception);
				line = values.getAsString(MilPatrolTable.field_line);
				notes = values.getAsString(MilPatrolTable.field_notes);
				redline = values.getAsString(MilPatrolTable.field_redline);
				logTime = values.getAsString(MilPatrolTable.field_logTime);
				cxType = values.getAsString(MilPatrolTable.field_xcrzxl);
				zzwsbh = values.getAsString(MilPatrolTable.field_zzwsbh);
				ffckbh = values.getAsString(MilPatrolTable.field_ffckbh);
				int haszzing = values.getAsInteger(MilPatrolTable.field_haszz);
				haszz = (haszzing == 1) ? true : false;
				if (haszz) {
					hasZzView.setChecked(true);
				} else {
					noZzView.setChecked(true);
				}
				dateView.setText(logTime);
				if (hasMining) {
					hasMiningView.setChecked(true);
				} else {
					noMiningView.setChecked(true);
				}
				exceptionView.setText(exception);
				lineView.setText(line);
				notesView.setText(notes);
				zzwsbhView.setText(zzwsbh);
				ffckbhView.setSelectedItem(ffckbh);
				xkTypeView.setSelectedItem(cxType);

				String admin = values.getAsString(MilPatrolTable.field_szcj);
				adminCode = admin;
				adminName = DBHelper.getDbHelper(context).queryAdminFullName(
						adminCode);
				adminView.setText(adminName);
				if (adminCode.length() == 9)
					// getFFCKPar(adminCode);
					new GetFfckd().execute();

				if (redline != null && redline.length() > 0) {
					redlineView.setSelected(true);
					redlineView.setText(getString(R.string.cn_redlinedisplay));
				} else {
					redlineView.setSelected(false);
					redlineView.setText(getString(R.string.cn_redlinegather));
				}

				ArrayList<ContentValues> annexes = DBHelper
						.getDbHelper(context).doQuery(
								MilPatrolAnnexesTable.name,
								new String[] {
										MilPatrolAnnexesTable.field_path,
										MilPatrolAnnexesTable.field_tag },
								MilPatrolAnnexesTable.field_tagId + "=?",
								new String[] { id }, null, null, null, null);
				if (annexes != null && annexes.size() > 0) {
					for (int i = 0; i < annexes.size(); i++) {
						String path = annexes.get(i).getAsString(
								MilPatrolAnnexesTable.field_path);
						String mtype = annexes.get(i).getAsString(
								MilPatrolAnnexesTable.field_tag);
						File f = new File(path);
						if (f.exists()) {
							if (mtype.equalsIgnoreCase("image/*")) {
								if (photoLayout.getVisibility() != View.VISIBLE)
									photoLayout.setVisibility(View.VISIBLE);
								photoGridAdapter.addItem(path);
							} else if (mtype.equalsIgnoreCase("audio/*")) {
								if (audioLayout.getVisibility() != View.VISIBLE)
									audioLayout.setVisibility(View.VISIBLE);
								audioGridAdapter.addItem(path);
							}
						}
					}
					photoGridAdapter.notifyDataSetChanged();
					audioGridAdapter.notifyDataSetChanged();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			logTime = TimeFormatFactory2.getDisplayTimeD(new Date());
			dateView.setText(logTime);
			logId = "LG" + TimeFormatFactory2.getIdFormatTime(new Date());
		}

	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vid = v.getId();
			Intent intent = null;
			switch (vid) {
			case R.id.fragment_mineral_log_edite_redline:
				Log.i("ydzf","MineralLogFragment_redline_Button_press");
				intent = new Intent(context, PolyGatherActivity.class);
				if (redline != null && redline.length() > 0) {
					intent.putExtra(PolyGatherActivity.REDLINE, redline);
				}
				getActivity().startActivityForResult(intent,
						Parameters.GET_REDLINE);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			case R.id.fragment_mineral_log_edite_camera:
				intent = new Intent(context, CameraPhotoActivity.class);
				getActivity().startActivityForResult(intent,
						Parameters.GET_PHOTO);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			// case R.id.fragment_mineral_log_edite_tape:
			// Intent tapeIntent = new Intent(context, TapeActivity.class);
			// getActivity().startActivityForResult(tapeIntent,
			// Parameters.GET_AUDIO);
			// break;

			case R.id.fragment_mineral_log_edite_date:
				datePickerDialog.show();
				break;
			case R.id.fragment_case_edite_admin:
				intent = new Intent(context, AdminChooserActivity.class);
				intent.putExtra(AdminChooserActivity.ADMIN_CODE, adminCode);
				getActivity().startActivityForResult(intent,
						Parameters.GET_ADMIN);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
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
			logTime = new StringBuffer("").append(year).append("年")
					.append(month).append("月").append(day).append("日")
					.toString();
			dateView.setText(logTime);
		}
	};

	IClickListener audioClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			final String path = o.toString();
			final View delView = v;
			if (v != null) {
				View alertView = LayoutInflater.from(context).inflate(
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
						// TODO Auto-generated method stub
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
					}
				});

				doButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
						audioGridAdapter.deleteItem(path);
						if (audioGridAdapter.getCount() < 1)
							audioLayout.setVisibility(View.GONE);
						Animation anim = AnimationUtils.loadAnimation(context,
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
						delView.startAnimation(anim);
						File f = new File(path);
						f.delete();
					}
				});

				alertDialog = new AlertDialog.Builder(getActivity()).create();
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
				context.startActivity(intent);
			}
			return null;
		}
	};

	IClickListener photoClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			final String path = o.toString();
			final View delView = v;
			if (v != null) {
				View alertView = LayoutInflater.from(context).inflate(
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
						// TODO Auto-generated method stub
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
					}
				});

				doButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (alertDialog != null && alertDialog.isShowing())
							alertDialog.dismiss();
						photoGridAdapter.deleteItem(path);
						Animation anim = AnimationUtils.loadAnimation(context,
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
						delView.startAnimation(anim);
						File f = new File(path);
						f.delete();
					}
				});

				alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.show();
				alertDialog.setCancelable(true);
				alertDialog.getWindow().setContentView(alertView);
			} else {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				Uri mUri = Uri.parse("file://" + path);
				intent.setDataAndType(mUri, "image/*");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				context.startActivity(intent);
			}
			return null;
		}
	};

	class SaveTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			circleProgressView.setMsg(getString(R.string.cn_saveing));
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(circleProgressView);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				DBHelper.getDbHelper(context).delete(
						MilPatrolAnnexesTable.name,
						MilPatrolAnnexesTable.field_tagId + "=?",
						new String[] { logId });
				ArrayList<String> audioPaths = audioGridAdapter.getPaths();
				ArrayList<String> imagePahts = photoGridAdapter.getPaths();
				if (imagePahts != null && imagePahts.size() > 0) {
					for (String s : imagePahts) {
						File file = new File(s);
						String fileName = file.getName();
						String prefix = fileName.substring(fileName
								.lastIndexOf("."));
						ContentValues aValues = new ContentValues();

						aValues.put(MilPatrolAnnexesTable.field_name, fileName);
						aValues.put(MilPatrolAnnexesTable.field_path, s);
						aValues.put(MilPatrolAnnexesTable.field_tagId, logId);
						aValues.put(
								MilPatrolAnnexesTable.field_tag,
								MIMEMapTable.getInstance().getParentMIMEType(
										prefix));
						DBHelper.getDbHelper(context).insert(
								MilPatrolAnnexesTable.name, aValues);
					}
				}

				if (audioPaths != null && audioPaths.size() > 0) {
					for (String s : audioPaths) {
						File file = new File(s);
						String fileName = file.getName();
						String prefix = fileName.substring(fileName
								.lastIndexOf("."));
						ContentValues aValues = new ContentValues();
						aValues.put(MilPatrolAnnexesTable.field_name, fileName);
						aValues.put(MilPatrolAnnexesTable.field_path, s);
						aValues.put(MilPatrolAnnexesTable.field_tagId, logId);
						aValues.put(
								MilPatrolAnnexesTable.field_tag,
								MIMEMapTable.getInstance().getParentMIMEType(
										prefix));
						DBHelper.getDbHelper(context).insert(
								MilPatrolAnnexesTable.name, aValues);
					}
				}

				ContentValues values = new ContentValues();
				values.put(MilPatrolTable.field_exception, exception);
				values.put(MilPatrolTable.field_hasMining,
						hasMiningView.isChecked() ? 1 : 0);
				values.put(MilPatrolTable.field_line, line);
				values.put(MilPatrolTable.field_logTime, logTime);
				values.put(MilPatrolTable.field_notes, notes);
				values.put(MilPatrolTable.field_redline, redline);
				values.put(MilPatrolTable.field_ffckbh, ffckbh);
				values.put(MilPatrolTable.field_szcj, adminCode);
				values.put(MilPatrolTable.field_xcrzxl, cxType);
				values.put(MilPatrolTable.field_zzwsbh, zzwsbh);
				values.put(MilPatrolTable.field_haszz,
						hasZzView.isChecked() ? 1 : 0);

				String where = new StringBuffer(MilPatrolTable.field_id)
						.append("=?").toString();
				String args[] = new String[] { logId };
				int count = DBHelper.getDbHelper(context).queryCount(
						MilPatrolTable.name, null, where, args);
				if (count > 0) {
					int rows = DBHelper.getDbHelper(context).update(
							MilPatrolTable.name, values, where, args);
					succeed = rows > 0 ? true : false;
				} else {
					values.put(MilPatrolTable.field_user, currentUser);
					values.put(MilPatrolTable.field_id, logId);
					long row = DBHelper.getDbHelper(context).insert(
							MilPatrolTable.name, values);
					succeed = row > -1 ? true : false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}

			if (result) {
				GravityCenterToast
						.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			} else {
				GravityCenterToast
						.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class GetFfckd extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// circleProgressView.setMsg(getString(R.string.cn_saveing));
			// alertDialog.show();
			// alertDialog.setCancelable(false);
			// alertDialog.getWindow().setContentView(circleProgressView);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeedReturn = false;
			String uri = "";
			String appUri = "";
			try {
				appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
			} catch (Exception e) {
				// TODO: handle exception
			}

			uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
					context.getString(R.string.uri_arguments_service))
					.toString() : new StringBuffer(appUri).append("/")
					.append(context.getString(R.string.uri_arguments_service))
					.toString();
			try {
				String table = FFCKDTable.name;
				String reUri = "FFCKD";
				String curAdminCode="";
				if(adminCode.length()==9)
				{
					curAdminCode=adminCode;
				}
				else if(adminCode.length()==12)
				{
					curAdminCode=adminCode.substring(0, 9);
				}
				String argumentUri = uri + "?type=" + reUri + "&pid="
						+ curAdminCode;
				HttpResponse response = HttpUtils
						.httpClientExcuteGet(argumentUri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					if (entityString != null && !entityString.equals("")) {
						JSONObject entiryJson = new JSONObject(entityString);
						boolean succeed = entiryJson.getBoolean("succeed");
						succeedReturn = succeed;
						if (succeed) {
							DBHelper.getDbHelper(context).delete(table, null,
									null);
							JSONArray dataSet = entiryJson
									.getJSONArray("dataset");
							if (dataSet != null && dataSet.length() > 0) {
								ArrayList<KeyValue> ffckdSubject = new ArrayList<KeyValue>();
								for (int n = 0; n < dataSet.length(); n++) {
									try {
										JSONObject data = dataSet
												.getJSONObject(n);
										ContentValues cv = new ContentValues();
										String key = data.getString("key");
										String value = data.getString("value");
										ffckdSubject.add(new KeyValue(key,
												value));

									} catch (Exception e) {
										continue;
									}
								}
								if(ffckbhView==null) 
								{
									ffckbhView = (DropDownSpinner) rootView
											.findViewById(R.id.fragment_mineral_log_edite_code);
								}
										
								ffckbhView.setData(ffckdSubject);
								ffckbh = ffckbhView.getSelectedKey().toString();
							}
						}
					}
				}
				// }
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return succeedReturn;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// if (alertDialog != null && alertDialog.isShowing()) {
			// alertDialog.dismiss();
			// }
			//
			// if (result) {
			// GravityCenterToast
			// .makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			// } else {
			// GravityCenterToast
			// .makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
			// }
		}
	}

	class SendTask extends AsyncTask<Void, Void, Boolean> {
		Location location;
		String msg = "";
		@Override
		protected void onPreExecute() {
			circleProgressView.setMsg(getString(R.string.cn_sendding));
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(circleProgressView);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = true;
			
			// boolean succeed = false;
			try {
				String uri = "";
				String annexUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_mineral_log)).toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_mineral_log))
								.toString();
				annexUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_annex_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_annex_service))
						.toString();
				JSONObject resObject = new JSONObject();
				resObject.put("xcry", currentUser);// 巡查人员
				resObject.put(
						"xcsj",
						logTime.replace('年', '-').replace('月', '-')
								.replace("日", ""));// 巡查时间
				resObject.put("xcxl", line);// 巡查线路
				resObject.put("fxffck", hasMiningView.isChecked() ? "1" : "0");// 发现非法采矿hasMiningView
				resObject.put("fxwt", exception);// 发现问题
				resObject.put("bz", notes);// 备注

				resObject.put("txr", currentUser);// 填写人
				resObject.put("szcj", adminCode);// 所在所村居
				resObject.put("tztzsbh", zzwsbh);// 制止文书编号
				resObject.put("ffckdbh", ffckbh);// 非法采矿点编号
				resObject.put("sfzzffxw", hasZzView.isChecked() ? "1" : "0");// 是否制止非法行为
				resObject.put("xcrzlx", cxType);// 巡查类型
				resObject.put("txsj",
						TimeFormatFactory2.getSourceTime(new Date()));// 填写时间
				JSONArray annexJsonArray = getAnnexJsonArray();
				if (annexJsonArray != null) {
					resObject.put("annexes", annexJsonArray);
				} else {
					resObject.put("annexes", JSONObject.NULL);
				}

				Thread.sleep(400);
				publishProgress();
				if (location != null) {
					resObject.put("x", location.getLongitude());// x坐标
					resObject.put("y", location.getLatitude());// y坐标
				} else {
					resObject.put("x", JSONObject.NULL);// x坐标
					resObject.put("y", JSONObject.NULL);// y坐标
				}
				if (redline != null && !"".equals(redline)) {
					resObject.put("hx", new JSONObject(redline));
				} else {
					resObject.put("hx", JSONObject.NULL);
				}

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(uri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(resObject.toString(),
						HTTP.UTF_8));
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {
						String backId = backJson.getString("patrolId");// 返回的巡查编号

						ArrayList<String> audioPaths = audioGridAdapter
								.getPaths();
						ArrayList<String> imagePahts = photoGridAdapter
								.getPaths();
						if (audioPaths != null && audioPaths.size() > 0)
							for (String apath : audioPaths) {
								File file = new File(apath);
								postAnnex(annexUri, file, backId);
								file.delete();
							}
						if (imagePahts != null && imagePahts.size() > 0)
							for (String ipath : imagePahts) {
								File file = new File(ipath);
								postAnnex(annexUri, file, backId);
								file.delete();
							}
						succeed = true;
						DBHelper.getDbHelper(context).delete(MilPatrolAnnexesTable.name,
								MilPatrolAnnexesTable.field_tagId + "=?",
								new String[] { logId });
						DBHelper.getDbHelper(context).delete(MilPatrolTable.name,
								MilPatrolTable.field_id + "=?", new String[] { logId });
						
						return true;
						// DBHelper.getDbHelper(context).delete(
						// MilPatrolAnnexesTable.name,
						// MilPatrolAnnexesTable.field_tagId + "=?",
						// new String[] { logId });
						// DBHelper.getDbHelper(context).delete(
						// MilPatrolTable.name,
						// MilPatrolTable.field_id + "=?",
						// new String[] { logId });
					}  else {
						msg = backJson.getString("msg");
						return false;
					}
				}
				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				msg = "任务被中断";
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			location = LocationUtils.getCurrentLocation(context);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (result) {
				GravityCenterToast
						.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
				getActivity().onBackPressed();
			} else {
				GravityCenterToast
						.makeText(context, "发送失败，" + msg, Toast.LENGTH_SHORT).show();
			}
		}

		void postAnnex(String uri, File file, String id) {
			try {
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(uri);

				String fileName = file.getName();
				String prefix = fileName.substring(fileName.lastIndexOf("."));
				String mime = MIMEMapTable.getInstance().getMIMEType(prefix);
				JSONObject attriJson = new JSONObject();
				attriJson.put("type", "kcxclj");
				attriJson.put("tagId", id);
				attriJson.put("name", fileName);
				attriJson.put("fjlx", mime.substring(0, mime.lastIndexOf("/")));

				MultipartEntity postEntity = new MultipartEntity();
				ContentBody cbAttri = new StringBody(attriJson.toString(),
						Charset.forName(HTTP.UTF_8));
				ContentBody cbFileData = new FileBody(file, mime);
				postEntity.addPart("attriData", cbAttri);
				postEntity.addPart("fileData", cbFileData);
				HttpPost annexPost = new HttpPost(uri);
				annexPost.setEntity(postEntity);
				client.execute(annexPost);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JSONArray getAnnexJsonArray() {
			try {
				JSONArray annexJsonArray = new JSONArray();
				ArrayList<String> audioPaths = audioGridAdapter.getPaths();
				ArrayList<String> imagePahts = photoGridAdapter.getPaths();
				if (imagePahts != null && imagePahts.size() > 0) {
					for (String s : imagePahts) {
						File file = new File(s);
						String fileName = file.getName();
						String prefix = fileName.substring(fileName
								.lastIndexOf("."));
						String mime = MIMEMapTable.getInstance()
								.getParentMIMEType(prefix);

						JSONObject annexJson = new JSONObject();
						annexJson.put("type", "kcxclj");
						annexJson.put("FileSize", file.length());
						annexJson.put("tagId", "");
						annexJson.put("name", fileName);
						annexJson.put("fjlx",
								mime.substring(0, mime.lastIndexOf("/")));
						annexJsonArray.put(annexJson);
					}
				}

				if (audioPaths != null && audioPaths.size() > 0) {
					for (String s : audioPaths) {
						File file = new File(s);
						String fileName = file.getName();
						String prefix = fileName.substring(fileName
								.lastIndexOf("."));
						String mime = MIMEMapTable.getInstance()
								.getParentMIMEType(prefix);

						JSONObject annexJson = new JSONObject();
						annexJson.put("type", "kcxclj");
						annexJson.put("FileSize", file.length());
						annexJson.put("tagId", "");
						annexJson.put("name", fileName);
						annexJson.put("fjlx",
								mime.substring(0, mime.lastIndexOf("/")));
						annexJsonArray.put(annexJson);
					}
				}
				return annexJsonArray;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
	class SendTask2 extends AsyncTask<Void, Void, Boolean>
	{
		Location location;
		String msg = "";
		CircleProgressBusyView abv;
		@Override
		protected void onPreExecute() {
			abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = true;
			
			// boolean succeed = false;
			try {
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_mineral_log)).toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_mineral_log))
								.toString();
//				annexUri = appUri.endsWith("/") ? new StringBuffer(appUri)
//						.append(context.getString(R.string.uri_annex_service))
//						.toString() : new StringBuffer(appUri).append("/")
//						.append(context.getString(R.string.uri_annex_service))
//						.toString();
						
				/**
				 * 上传附件,并返回附件的编号数组
				 */
				JSONArray annexArray = sendAnnexes();
						
				JSONObject resObject = new JSONObject();
				resObject.put("xcry", currentUser);// 巡查人员
				resObject.put(
						"xcsj",
						logTime.replace('年', '-').replace('月', '-')
								.replace("日", ""));// 巡查时间
				resObject.put("xcxl", line);// 巡查线路
				resObject.put("fxffck", hasMiningView.isChecked() ? "1" : "0");// 发现非法采矿hasMiningView
				resObject.put("fxwt", exception);// 发现问题
				resObject.put("bz", notes);// 备注

				resObject.put("txr", currentUser);// 填写人
				resObject.put("szcj", adminCode);// 所在所村居
				resObject.put("tztzsbh", zzwsbh);// 制止文书编号
				resObject.put("ffckdbh", ffckbh);// 非法采矿点编号
				resObject.put("sfzzffxw", hasZzView.isChecked() ? "1" : "0");// 是否制止非法行为
				resObject.put("xcrzlx", cxType);// 巡查类型
				resObject.put("txsj",
						TimeFormatFactory2.getSourceTime(new Date()));// 填写时间
				resObject.put("annexes", annexArray);

				Thread.sleep(400);
				publishProgress();
				if (location != null) {
					resObject.put("x", location.getLongitude());// x坐标
					resObject.put("y", location.getLatitude());// y坐标
				} else {
					resObject.put("x", JSONObject.NULL);// x坐标
					resObject.put("y", JSONObject.NULL);// y坐标
				}
				if (redline != null && !"".equals(redline)) {
					resObject.put("hx", new JSONObject(redline));
				} else {
					resObject.put("hx", JSONObject.NULL);
				}

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(uri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(resObject.toString(),
						HTTP.UTF_8));
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {
						String backId = backJson.getString("patrolId");// 返回的巡查编号

						ArrayList<String> audioPaths = audioGridAdapter
								.getPaths();
						ArrayList<String> imagePahts = photoGridAdapter
								.getPaths();
						if (audioPaths != null && audioPaths.size() > 0)
							for (String apath : audioPaths) {
								File file = new File(apath);
								//postAnnex(annexUri, file, backId);
								file.delete();
							}
						if (imagePahts != null && imagePahts.size() > 0)
							for (String ipath : imagePahts) {
								File file = new File(ipath);
								//postAnnex(annexUri, file, backId);
								file.delete();
							}
						succeed = true;
						DBHelper.getDbHelper(context).delete(MilPatrolAnnexesTable.name,
								MilPatrolAnnexesTable.field_tagId + "=?",
								new String[] { logId });
						DBHelper.getDbHelper(context).delete(MilPatrolTable.name,
								MilPatrolTable.field_id + "=?", new String[] { logId });
						
						return true;
						// DBHelper.getDbHelper(context).delete(
						// MilPatrolAnnexesTable.name,
						// MilPatrolAnnexesTable.field_tagId + "=?",
						// new String[] { logId });
						// DBHelper.getDbHelper(context).delete(
						// MilPatrolTable.name,
						// MilPatrolTable.field_id + "=?",
						// new String[] { logId });
					}  else {
						msg = backJson.getString("msg");
						return false;
					}
				}
				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				msg = "任务被中断";
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			location = LocationUtils.getCurrentLocation(context);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (result) {
				GravityCenterToast
						.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
				getActivity().onBackPressed();
			} else {
				GravityCenterToast
						.makeText(context, "发送失败，" + msg, Toast.LENGTH_SHORT).show();
			}
		}

		private String postAnnex(String path, String url) throws Exception {
			try {
				File file = new File(path);
				String fileName = file.getName();
				String prefix = fileName.substring(fileName.lastIndexOf("."));
				String mime = MIMEMapTable.getInstance().getMIMEType(prefix);
				JSONObject attriJson = new JSONObject();
				attriJson.put("type", "kcxclj");
				attriJson.put("fjlx", mime.substring(0, mime.lastIndexOf("/")));
				attriJson.put("name", fileName);
				try {
					attriJson.put("xzb", DbUtil.getPHOTO_XZB_ByPath(context, path));
					attriJson.put("yzb", DbUtil.getPHOTO_YZB_ByPath(context, path));
					attriJson.put("fwj", DbUtil.getPHOTO_FWJ_ByPath(context, path));
					attriJson.put("pssj", DbUtil.getPHOTO_PSSJ_ByPath(context, path));
				} catch (Exception e) {
				}
				MultipartEntity postEntity = new MultipartEntity();
				ContentBody cbAttri = new StringBody(attriJson.toString(),
						Charset.forName(HTTP.UTF_8));

				ContentBody cbFileData = new FileBody(file, mime);
				postEntity.addPart("attriData", cbAttri);
				postEntity.addPart("fileData", cbFileData);

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost annexPost = new HttpPost(url);
				annexPost.setEntity(postEntity);

				/**
				 * 上传发生异常时,重复操作三次,如果三次都没成功,提示用户上传附件发生异常
				 */
				int i = 0;
				while (i++ < 3) {
					HttpResponse response = client.execute(annexPost);
					if (response != null
							&& response.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(response
								.getEntity());
						Log.i("entity", entityString);
						JSONObject backJson = new JSONObject(entityString);
						if (backJson.getBoolean("succeed"))
							return backJson.getString("annexId");
						else
							throw new Exception(backJson.getString("msg"));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			throw new Exception("上传附件发生异常，请重试");
		}
		
		private JSONArray sendAnnexes() throws Exception {
			JSONArray annexArray = new JSONArray();
			try {
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				String annexUri = appUri.endsWith("/") ? new StringBuffer(
						appUri).append(
						context.getString(R.string.uri_annexupload_service))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_annexupload_service))
						.toString();

				ArrayList<String> imagePahts = photoGridAdapter.getPaths();
				ArrayList<String> tapePaths = audioGridAdapter.getPaths();
				if (imagePahts != null && imagePahts.size() > 0) {
					for (String s : imagePahts) {
						String annexId = postAnnex(s, annexUri);
						annexArray.put(annexId);
					}
				}

				if (tapePaths != null && tapePaths.size() > 0) {
					for (String s : tapePaths) {
						String annexId = postAnnex(s, annexUri);
						annexArray.put(annexId);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				throw e;
			}
			return annexArray;
		}


		JSONArray getAnnexJsonArray() {
			try {
				JSONArray annexJsonArray = new JSONArray();
				ArrayList<String> audioPaths = audioGridAdapter.getPaths();
				ArrayList<String> imagePahts = photoGridAdapter.getPaths();
				if (imagePahts != null && imagePahts.size() > 0) {
					for (String s : imagePahts) {
						File file = new File(s);
						String fileName = file.getName();
						String prefix = fileName.substring(fileName
								.lastIndexOf("."));
						
						String mime = MIMEMapTable.getInstance()
								.getParentMIMEType(prefix);

						JSONObject annexJson = new JSONObject();
						annexJson.put("type", "kcxclj");
						annexJson.put("FileSize", file.length());
						annexJson.put("tagId", "");
						annexJson.put("name", fileName);
						annexJson.put("fjlx",
								mime.substring(0, mime.lastIndexOf("/")));
						annexJsonArray.put(annexJson);
					}
				}

				if (audioPaths != null && audioPaths.size() > 0) {
					for (String s : audioPaths) {
						File file = new File(s);
						String fileName = file.getName();
						String prefix = fileName.substring(fileName
								.lastIndexOf("."));
						String mime = MIMEMapTable.getInstance()
								.getParentMIMEType(prefix);

						JSONObject annexJson = new JSONObject();
						annexJson.put("type", "kcxclj");
						annexJson.put("FileSize", file.length());
						annexJson.put("tagId", "");
						annexJson.put("name", fileName);
						annexJson.put("fjlx",
								mime.substring(0, mime.lastIndexOf("/")));
						annexJsonArray.put(annexJson);
					}
				}
				return annexJsonArray;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
