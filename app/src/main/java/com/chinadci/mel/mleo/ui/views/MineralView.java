package com.chinadci.mel.mleo.ui.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.WebMinPatrolTable;
import com.chinadci.mel.mleo.ui.activities.MapViewActivity;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MineralView extends FrameLayout {

	Activity parentActivity;

	String patrolId = "";
	String caseId = "";
	String user = "";
	String sourceTable = "";
	String annexTable = "";
	View rootView;
	ListView attriListView;
	TextView titleView;
	TextView redlineView;
	TextView locationView;
	LinearLayout photoLayout;
	LinearLayout audioLayout;
	LinearLayout redlineLayout;
	GridView photoGridView;
	GridView audioGridView;

	String redlineJson;
	String locationJson;
	String caseTable = "";
	AttriAdapter attriAdapter;
	ImageGridAdapter imageGridAdapter;
	AudioGridAdapter audioGridAdapter;

	Context context;

	public MineralView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: TODO
	 * @param context
	 * @throws
	 */
	void initView(Context _context) {
		context = _context;
		// imageGridAdapter = new ImageGridAdapter(context, null,
		// imageClickListener, View.GONE);
		// audioGridAdapter = new AudioGridAdapter(context, null,
		// audioClickListener, View.GONE);

		imageGridAdapter = new ImageGridAdapter(context,
				new ArrayList<String>(), imageClickListener, View.GONE,
				R.layout.view_photo, R.id.view_photo_photo,
				R.id.view_photo_delete);
		audioGridAdapter = new AudioGridAdapter(context,
				new ArrayList<String>(), audioClickListener, View.GONE,
				R.layout.view_audio, R.id.view_audio_index,
				R.id.view_audio_length, R.id.view_audio_delete);

		rootView = LayoutInflater.from(context).inflate(R.layout.view_mineral,
				null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);
		attriListView = (ListView) rootView
				.findViewById(R.id.view_case_listview);
		// titleView = (TextView) rootView.findViewById(R.id.view_patrol_title);
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
	 * @Title: setParentActivity
	 * @Description: TODO
	 * @param activity
	 * @throws
	 */
	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public void setDataSource(String user, String id, String table,String annexTable) {
		this.patrolId = "HX" + TimeFormatFactory2.getIdFormatTime(new Date());
		this.caseId = id;
		this.sourceTable = table;
		this.user = user;
		// display case detail info
		viewMineralInfo(this.caseId, this.sourceTable,annexTable);
	}

	void viewMineralInfo(String id, String cTable,String aTable) {
		try {
			String columns[] = new String[] { WebMinPatrolTable.field_id,
					WebMinPatrolTable.field_szcj,
					WebMinPatrolTable.field_exception,
					WebMinPatrolTable.field_ffckbh,
					WebMinPatrolTable.field_hasMining,
					WebMinPatrolTable.field_haszz,
					WebMinPatrolTable.field_line,
					WebMinPatrolTable.field_location,
					WebMinPatrolTable.field_logTime,
					WebMinPatrolTable.field_notes,
					WebMinPatrolTable.field_redline,
					WebMinPatrolTable.field_user,
					WebMinPatrolTable.field_zzwsbh,
					WebMinPatrolTable.field_xcrzxl };
			String selection = WebMinPatrolTable.field_id + "=?";
			String args[] = new String[] { id };
			ContentValues caseInfo = DBHelper.getDbHelper(getContext())
					.doQuery(cTable, columns, selection, args);

			if (caseInfo != null) {
				String values[] = new String[11];
				String keys[] = new String[11];
				// values[0] = caseInfo
				// .getAsString(WebMinPatrolTable.field_id);
				values[0] = caseInfo
						.getAsString(WebMinPatrolTable.field_xcrzxl); // 巡查类型
				values[1] = caseInfo
						.getAsString(WebMinPatrolTable.field_logTime);
				values[2] = caseInfo.getAsString(WebMinPatrolTable.field_line);
				values[3] = DBHelper.getDbHelper(context).queryAdminFullName(
						caseInfo.getAsString(WebMinPatrolTable.field_szcj));
				values[4] = caseInfo
						.getAsString(WebMinPatrolTable.field_ffckbh);
				values[5] = caseInfo.getAsString(
						WebMinPatrolTable.field_hasMining).equals("1") ? "发现"
						: "未发现";
				values[6] = caseInfo
						.getAsString(WebMinPatrolTable.field_zzwsbh);
				values[7] = caseInfo.getAsString(WebMinPatrolTable.field_haszz)
						.equals("1") ? "已制止" : "未制止";
				values[8] = caseInfo
						.getAsString(WebMinPatrolTable.field_exception);
				// values[7] = caseInfo
				// .getAsString(WebMinPatrolTable.field_user);
				values[9] = caseInfo.getAsString(WebMinPatrolTable.field_notes);
				values[10] = caseInfo.getAsString(WebMinPatrolTable.field_user);

				keys[0] = "巡查类型";
				keys[1] = "巡查日期 ";
				keys[2] = "巡查线路";
				keys[3] = "所在村居";
				keys[4] = "非法采矿点编号";
				keys[5] = "是否非法采矿";
				keys[6] = "制止文书编号";
				keys[7] = "是否制止非法行为";
				keys[8] = "发现问题";
				keys[9] = "备注";
				keys[10] = "上报人";
				// keys[0] = "案件编号";
				// keys[7] = "上报人";

				attriAdapter = new AttriAdapter(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();

				redlineJson = caseInfo
						.getAsString(WebMinPatrolTable.field_redline);
				locationJson = caseInfo
						.getAsString(WebMinPatrolTable.field_location);
				if (redlineJson != null && !redlineJson.equals(""))
					redlineLayout.setVisibility(View.VISIBLE);
				else
					redlineLayout.setVisibility(View.GONE);

				String annexFields[] = new String[] { MilPatrolAnnexesTable.field_path };
				String annexSelection = new StringBuffer(
						MilPatrolAnnexesTable.field_tagId).append("=?")
						.toString();
				String annexArgs[] = new String[] { caseId };
				ArrayList<ContentValues> annexList = DBHelper.getDbHelper(
						getContext()).doQuery(aTable,
						annexFields, annexSelection, annexArgs, null, null,
						null, null);
				if (annexList != null && annexList.size() > 0) {
					for (ContentValues annex : annexList) {
						String path = annex
								.getAsString(MilPatrolAnnexesTable.field_path);
						String ext = path.substring(path.lastIndexOf("."));
						String type = MIMEMapTable.getInstance()
								.getParentMIMEType(ext);
						if (type.equals("audio/*")) {
							// load this annex as a image
							audioGridAdapter.addItem(path);
							if (audioLayout.getVisibility() != View.VISIBLE)
								audioLayout.setVisibility(View.VISIBLE);
						} else if (type.equals("image/*")) {
							// load this annex as a audio
							imageGridAdapter.addItem(path);
							if (photoLayout.getVisibility() != View.VISIBLE)
								photoLayout.setVisibility(View.VISIBLE);
						}
					}
				}
				imageGridAdapter.notifyDataSetChanged();
				audioGridAdapter.notifyDataSetChanged();
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
}
