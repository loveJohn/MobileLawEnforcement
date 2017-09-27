package com.chinadci.mel.mleo.ui.views;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.activities.MapViewActivity;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;
import com.chinadci.mel.mleo.utils.CaseUtils;

/**
 * 
 * @ClassName CaseView
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:33:42
 * 
 */
public class CaseView extends FrameLayout {

	View rootView;
	ListView attriListView;

	TextView redlineView;
	TextView locationView;
	LinearLayout photoLayout;
	LinearLayout audioLayout;
	LinearLayout redlineLayout;
	GridView photoGridView;
	GridView audioGridView;

	String redlineJson;
	String locationJson;
	String caseId = "";
	String caseTable = "";
	String annexTable = "";
	AttriAdapter attriAdapter;
	ImageGridAdapter imageGridAdapter;
	AudioGridAdapter audioGridAdapter;

	Activity parentActivity;

	public CaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public CaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

		// TODO Auto-generated constructor stub
	}

	public CaseView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @Title: setCaseSource
	 * @Description: TODO
	 * @param id
	 * @param table
	 * @throws
	 */
	public void setDataSource(String id, String caseTable, String annexTable) {
		this.caseId = id;
		this.caseTable = caseTable;
		this.annexTable = annexTable;
		// display case detail info
		viewCaseInfo(this.caseId, this.caseTable, this.annexTable);
	}

	/**
	 * 
	 * @Title: setParentActivity
	 * @Description: TODO
	 * @param activity
	 * @throws
	 */
	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
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

	/**
	 * 
	 * @Title: initView
	 * @Description: TODO
	 * @param context
	 * @throws
	 */
	void initView(Context context) {
		imageGridAdapter = new ImageGridAdapter(context,
				new ArrayList<String>(), imageClickListener, View.GONE,
				R.layout.view_photo, R.id.view_photo_photo,
				R.id.view_photo_delete);
		audioGridAdapter = new AudioGridAdapter(context,
				new ArrayList<String>(), audioClickListener, View.GONE,
				R.layout.view_audio, R.id.view_audio_index,
				R.id.view_audio_length, R.id.view_audio_delete);

		rootView = LayoutInflater.from(context).inflate(R.layout.view_case,
				null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		attriListView = (ListView) rootView
				.findViewById(R.id.view_case_listview);
		redlineView = (TextView) rootView.findViewById(R.id.view_case_redline);
		locationView = (TextView) rootView
				.findViewById(R.id.view_case_location);
		photoGridView = (GridView) rootView
				.findViewById(R.id.view_media_photogrid);
		audioGridView = (GridView) rootView
				.findViewById(R.id.view_media_audiolist);
		photoLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_photolayout);
		audioLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_audiolayout);
		redlineLayout = (LinearLayout) rootView
				.findViewById(R.id.view_case_redline_pad);
		locationView.setOnClickListener(commandClickListener);
		redlineView.setOnClickListener(commandClickListener);

		photoGridView.setAdapter(imageGridAdapter);
		audioGridView.setAdapter(audioGridAdapter);
	}

	/**
	 * 
	 * @Title: viewCaseInfo
	 * @Description: TODO
	 * @param id
	 * @param table
	 * @throws
	 */
	// void viewCaseInfo(String id, String cTable, String aTable) {
	// try {
	// String columns[] = new String[] { InspectionCaseTable.field_id,
	// InspectionCaseTable.field_bh, InspectionCaseTable.field_address,
	// InspectionCaseTable.field_illegalSubject,
	// InspectionCaseTable.field_illegalArea,
	// InspectionCaseTable.field_illegalStatus,
	// InspectionCaseTable.field_illegalType,
	// InspectionCaseTable.field_landUsage,
	// InspectionCaseTable.field_location, InspectionCaseTable.field_notes,
	// InspectionCaseTable.field_parties, InspectionCaseTable.field_redline,
	// InspectionCaseTable.field_mTime, InspectionCaseTable.field_source,
	// InspectionCaseTable.field_analysis,
	// InspectionCaseTable.field_user };
	// String selection = InspectionCaseTable.field_id + "=?";
	// String args[] = new String[] { id };
	// ContentValues caseInfo =
	// DBHelper.getDbHelper(getContext()).doQuery(cTable, columns,
	// selection, args);
	//
	// if (caseInfo != null) {
	// String values[] = new String[13];
	// String keys[] = new String[13];
	//
	// values[0] = caseInfo.getAsString(InspectionCaseTable.field_bh);
	// values[1] = caseInfo.getAsString(InspectionCaseTable.field_source);
	// values[2] =
	// caseInfo.getAsString(InspectionCaseTable.field_illegalSubject);
	// values[3] = caseInfo.getAsString(InspectionCaseTable.field_parties);
	// values[4] = caseInfo.getAsString(InspectionCaseTable.field_address);
	// values[5] = caseInfo.getAsString(InspectionCaseTable.field_landUsage);
	// values[6] = caseInfo.getAsString(InspectionCaseTable.field_illegalType);
	// values[7] =
	// caseInfo.getAsString(InspectionCaseTable.field_illegalStatus);
	// values[8] = caseInfo.getAsString(InspectionCaseTable.field_illegalArea);
	// values[9] = caseInfo.getAsString(InspectionCaseTable.field_notes);
	// values[10] = caseInfo.getAsString(InspectionCaseTable.field_user);
	// values[11] = caseInfo.getAsString(InspectionCaseTable.field_mTime);
	// values[12] = caseInfo.getAsString(InspectionCaseTable.field_analysis);
	//
	// keys[0] = getContext().getString(R.string.v_f23);
	// keys[1] = getContext().getString(R.string.v_f1);
	// keys[2] = getContext().getString(R.string.v_f2);
	// keys[3] = getContext().getString(R.string.v_f3);
	// keys[4] = getContext().getString(R.string.v_f5);
	// keys[5] = getContext().getString(R.string.v_f7);
	// keys[6] = getContext().getString(R.string.v_f8);
	// keys[7] = getContext().getString(R.string.v_f9);
	// keys[8] = getContext().getString(R.string.v_f10) + "(m²)";
	// keys[9] = getContext().getString(R.string.v_f11);
	// keys[10] = getContext().getString(R.string.v_f24);
	// keys[11] = getContext().getString(R.string.v_f25);
	// keys[12] = getContext().getString(R.string.v_f43);
	//
	// attriAdapter = new AttriAdapter(getContext(), keys, values);
	// attriListView.setAdapter(attriAdapter);
	// attriAdapter.notifyDataSetChanged();
	//
	// redlineJson = caseInfo.getAsString(InspectionCaseTable.field_redline);
	// locationJson = caseInfo.getAsString(InspectionCaseTable.field_location);
	// if (redlineJson != null && !redlineJson.equals(""))
	// redlineLayout.setVisibility(View.VISIBLE);
	// else
	// redlineLayout.setVisibility(View.GONE);
	//
	// String annexFields[] = new String[] { CaseAnnexesTable.field_path };
	// String annexSelection = new StringBuffer(CaseAnnexesTable.field_caseId)
	// .append("=? and ").append(CaseAnnexesTable.field_tag).append("=?")
	// .toString();
	// String annexArgs[] = new String[] { caseId, cTable };
	// ArrayList<ContentValues> annexList =
	// DBHelper.getDbHelper(getContext()).doQuery(
	// aTable, annexFields, annexSelection, annexArgs, null, null, null, null);
	// if (annexList != null && annexList.size() > 0) {
	// for (ContentValues annex : annexList) {
	// String path = annex.getAsString(CaseAnnexesTable.field_path);
	// String ext = path.substring(path.lastIndexOf("."));
	// String type = MIMEMapTable.getInstance().getParentMIMEType(ext);
	// if (type.equals("audio/*")) {
	// // load this annex as a image
	// audioGridAdapter.addItem(path);
	// if (audioLayout.getVisibility() != View.VISIBLE)
	// audioLayout.setVisibility(View.VISIBLE);
	// } else if (type.equals("image/*")) {
	// // load this annex as a audio
	// imageGridAdapter.addItem(path);
	// if (photoLayout.getVisibility() != View.VISIBLE)
	// photoLayout.setVisibility(View.VISIBLE);
	// }
	// }
	// }
	// imageGridAdapter.notifyDataSetChanged();
	// audioGridAdapter.notifyDataSetChanged();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

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
				String values[] = new String[13];
				String keys[] = new String[13];

				values[0] = caseInfo.getAsString(InspectionCaseTable.field_bh);
				values[1] = caseInfo
						.getAsString(InspectionCaseTable.field_source);
				values[2] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalSubject);
				values[3] = caseInfo
						.getAsString(InspectionCaseTable.field_parties);
				values[4] = caseInfo
						.getAsString(InspectionCaseTable.field_address);
				values[5] = caseInfo
						.getAsString(InspectionCaseTable.field_landUsage);
				values[6] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalType);
				values[7] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalStatus);
				values[8] = caseInfo
						.getAsString(InspectionCaseTable.field_illegalArea);
				values[9] = caseInfo
						.getAsString(InspectionCaseTable.field_notes);
				values[10] = caseInfo
						.getAsString(InspectionCaseTable.field_user);
				values[11] = caseInfo
						.getAsString(InspectionCaseTable.field_mTime);
				values[12] = caseInfo
						.getAsString(InspectionCaseTable.field_analysis);

				keys[0] = getContext().getString(R.string.v_f23);
				keys[1] = getContext().getString(R.string.v_f1);
				keys[2] = getContext().getString(R.string.v_f2);
				keys[3] = getContext().getString(R.string.v_f3);
				keys[4] = getContext().getString(R.string.v_f5);
				keys[5] = getContext().getString(R.string.v_f7);
				keys[6] = getContext().getString(R.string.v_f8);
				keys[7] = getContext().getString(R.string.v_f9);
				keys[8] = getContext().getString(R.string.v_f10) + "(m²)";
				keys[9] = getContext().getString(R.string.v_f11);
				keys[10] = getContext().getString(R.string.v_f24);
				keys[11] = getContext().getString(R.string.v_f25);
				keys[12] = getContext().getString(R.string.v_f43);

				attriAdapter = new AttriAdapter(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();

				redlineJson = caseInfo
						.getAsString(InspectionCaseTable.field_redline);
				locationJson = caseInfo
						.getAsString(InspectionCaseTable.field_location);
				if (redlineJson != null && !redlineJson.equals(""))
					redlineLayout.setVisibility(View.VISIBLE);
				else
					redlineLayout.setVisibility(View.GONE);
				new annexGetTask(getContext(), id, cTable, aTable).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void showRedline() {
		if (redlineJson != null && !redlineJson.equals("")) {
			String geoArrayString = "[" + redlineJson + "]";
			Intent intent = new Intent(parentActivity, MapViewActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Parameters.GEOMETRY, geoArrayString);
			intent.putExtra(Parameters.ACTIVITY_TITLE, "案件红线");
			parentActivity.startActivity(intent);
			parentActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	}

	/**
	 * 
	 */
	OnClickListener commandClickListener = new OnClickListener() {

		public void onClick(View v) {
			int vid = v.getId();
			switch (vid) {
			// show case's location in map view
			case R.id.view_case_redline:
				showRedline();
				break;

			// show redline in map view
			case R.id.view_case_location:

				break;

			default:
				break;
			}

		}
	};
	/**
	 * 
	 */
	IClickListener audioClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			String path = o.toString();

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			File f = new File(path);
			Uri mUri = Uri.fromFile(f);
			intent.setDataAndType(mUri, "audio/*");
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);

			return null;
		}
	};

	/**
	 * 
	 */
	IClickListener imageClickListener = new IClickListener() {

		public Object onClick(View v, Object o) {
			String path = o.toString();
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			Uri mUri = Uri.parse("file://" + path);
			intent.setDataAndType(mUri, "image/*");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			getContext().startActivity(intent);
			return null;
		}
	};

	class annexGetTask extends AsyncTask<Void, String, Boolean> {
		String msg = "";
		Context context;
		String caseId;
		String cTable;
		String aTable;
		ArrayList<ContentValues> annexList;

		public annexGetTask(Context c, String caseId, String cTable,
				String aTable) {
			this.context = c;
			this.caseId = caseId;
			this.cTable = cTable;
			this.aTable = aTable;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String annexFields[] = new String[] {
						CaseAnnexesTable.field_path, CaseAnnexesTable.field_uri };
				String annexSelection = new StringBuffer(
						CaseAnnexesTable.field_caseId).append("=? and ")
						.append(CaseAnnexesTable.field_tag).append("=?")
						.toString();
				String annexArgs[] = new String[] { caseId, cTable };
				annexList = DBHelper.getDbHelper(getContext()).doQuery(aTable,
						annexFields, annexSelection, annexArgs, null, null,
						null, null);
				if (annexList != null && annexList.size() > 0) {
					for (ContentValues annex : annexList) {
						String path = annex
								.getAsString(CaseAnnexesTable.field_path);
						String uri = annex
								.getAsString(CaseAnnexesTable.field_uri);
						File file = new File(path);
						if (file.exists()) {
							publishProgress(path);
						} else {
							CaseUtils.getInstance().downAnnex(path, uri);
							publishProgress(path);
						}
					}
					return true;
				} else {
					msg = "上报信息无附件";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//msg = "服务器返回异常";
			return false;
		}

		@Override
		protected void onProgressUpdate(String... paths) {
			String path = paths[0];
			String ext = path.substring(path.lastIndexOf("."));
			String type="";
			try {
				type = MIMEMapTable.getInstance().getParentMIMEType(ext);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (type.equals("audio/*")) {
				// load this annex as a image
				audioGridAdapter.addItem(path);
				if (audioLayout.getVisibility() != View.VISIBLE)
					audioLayout.setVisibility(View.VISIBLE);
				audioGridAdapter.notifyDataSetChanged();
			} else if (type.equals("image/*")) {
				// load this annex as a audio
				imageGridAdapter.addItem(path);
				if (photoLayout.getVisibility() != View.VISIBLE)
					photoLayout.setVisibility(View.VISIBLE);
				imageGridAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result&&!"".equals(msg)) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
