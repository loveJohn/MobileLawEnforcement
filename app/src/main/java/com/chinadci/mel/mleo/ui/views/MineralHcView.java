package com.chinadci.mel.mleo.ui.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.mel.R;
import com.chinadci.mel.android.core.interfaces.IClickListener;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ldb.MineralHcTable;
import com.chinadci.mel.mleo.ui.activities.MapViewActivity;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter2;
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

public class MineralHcView extends FrameLayout {

	Activity parentActivity;

	ListView attriListView;
	TextView redlineView;
	TextView titleView;
	LinearLayout imageLayout;
	LinearLayout audioLayout;
	LinearLayout redlineLayout;
	GridView imageGridView;
	GridView audioGridView;

	ImageGridAdapter imageGridAdapter;
	AudioGridAdapter audioGridAdapter;

	String patrolTable = "";
	String annexTable = "";
	String redlineJson;
	AttriAdapter2 attriAdapter;

	String patrolId = "";
	String caseId = "";
	String sourceTable = "";

	View rootView;

	public MineralHcView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void setParentActivity(Activity activity) {
		this.parentActivity = activity;
	}

	public void setDataSource(String id, String patrolTable, String annexTable) {
		this.patrolId = "HX" + TimeFormatFactory2.getIdFormatTime(new Date());
		this.caseId = id;
		this.sourceTable = patrolTable;
		this.annexTable = annexTable;
		// display case detail info
		viewMineralHcInfo(this.caseId, this.sourceTable, this.annexTable);
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

	void viewMineralHcInfo(String id, String pTable, String aTable) {
		try {
			String columns[] = new String[] { MineralHcTable.field_id,
					MineralHcTable.field_ffkcfs, MineralHcTable.field_fkckz,
					MineralHcTable.field_hccomment, MineralHcTable.field_hcrmc,
					MineralHcTable.field_hcsj, MineralHcTable.field_sfffckd,
					MineralHcTable.field_sftzffkc, MineralHcTable.field_wfztmc,
					MineralHcTable.field_wfztxz };
			String selection = MilPatrolTable.field_id + "=?";
			String args[] = new String[] { id };
			ContentValues patrolInfo = DBHelper.getDbHelper(getContext())
					.doQuery(pTable, columns, selection, args);
			if (patrolInfo != null) {
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();

				keys.add("核查时间");
				if (patrolInfo.getAsString(MineralHcTable.field_hcsj) != null
						&& !patrolInfo.getAsString(MineralHcTable.field_hcsj)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_hcsj));
				} else {
					values.add("");
				}
				keys.add("核查人员名称");
				if (patrolInfo.getAsString(MineralHcTable.field_hcrmc) != null
						&& !patrolInfo.getAsString(MineralHcTable.field_hcrmc)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_hcrmc));
				} else {
					values.add("");
				}

				keys.add("非法开采方式");
				if (patrolInfo.getAsString(MineralHcTable.field_ffkcfs) != null
						&& !patrolInfo.getAsString(MineralHcTable.field_ffkcfs)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_ffkcfs));
				} else {
					values.add("");
				}

				keys.add("非法开采矿种");
				if (patrolInfo.getAsString(MineralHcTable.field_fkckz) != null
						&& !patrolInfo.getAsString(MineralHcTable.field_fkckz)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_fkckz));
				} else {
					values.add("");
				}

				keys.add("是否非法采矿点");
				if (patrolInfo.getAsString(MineralHcTable.field_sfffckd) != null
						&& !patrolInfo
								.getAsString(MineralHcTable.field_sfffckd)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_sfffckd));
				} else {
					values.add("");
				}

				keys.add("是否停止非法开采");
				if (patrolInfo.getAsString(MineralHcTable.field_sftzffkc) != null
						&& !patrolInfo.getAsString(
								MineralHcTable.field_sftzffkc).equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_sftzffkc));
				} else {
					values.add("");
				}

				keys.add("违法主体名称");
				if (patrolInfo.getAsString(MineralHcTable.field_wfztmc) != null
						&& !patrolInfo.getAsString(MineralHcTable.field_wfztmc)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_wfztmc));
				} else {
					values.add("");
				}

				keys.add("违法主体性质");
				if (patrolInfo.getAsString(MineralHcTable.field_wfztxz) != null
						&& !patrolInfo.getAsString(MineralHcTable.field_wfztxz)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_wfztxz));
				} else {
					values.add("");
				}
				keys.add("备注");
				if (patrolInfo.getAsString(MineralHcTable.field_hccomment) != null
						&& !patrolInfo.getAsString(
								MineralHcTable.field_hccomment).equals("")) {
					values.add(patrolInfo
							.getAsString(MineralHcTable.field_hccomment));
				} else {
					values.add("");
				}

				attriAdapter = new AttriAdapter2(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();

				String annexFields[] = new String[] { CaseAnnexesTable.field_path };
				String annexSelection = new StringBuffer(
						CaseAnnexesTable.field_tagId).append("=? and ")
						.append(CaseAnnexesTable.field_tag).append("=?")
						.toString();
				String annexArgs[] = new String[] {
						patrolInfo.getAsString(CasePatrolTable.field_id),
						pTable };
				ArrayList<ContentValues> annexList = DBHelper.getDbHelper(
						getContext()).doQuery(aTable, annexFields,
						annexSelection, annexArgs, null, null, null, null);
				if (annexList != null && annexList.size() > 0) {
					for (ContentValues annex : annexList) {
						String path = annex
								.getAsString(CaseAnnexesTable.field_path);

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
							if (imageLayout.getVisibility() != View.VISIBLE)
								imageLayout.setVisibility(View.VISIBLE);
						}
					}
				}
				imageGridAdapter.notifyDataSetChanged();
				audioGridAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: TODO
	 * @param context
	 * @throws
	 */
	void initView(Context context) {
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

		rootView = LayoutInflater.from(context).inflate(
				R.layout.view_mineralhc, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);

		attriListView = (ListView) rootView
				.findViewById(R.id.view_mineralhc_listview);
		// redlineView = (TextView) rootView
		// .findViewById(R.id.view_patrol_redline);
		imageLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_photolayout);
		audioLayout = (LinearLayout) rootView
				.findViewById(R.id.view_media_audiolayout);
		redlineLayout = (LinearLayout) rootView
				.findViewById(R.id.view_patrol_redline_pad);
		imageGridView = (GridView) rootView
				.findViewById(R.id.view_media_photogrid);
		audioGridView = (GridView) rootView
				.findViewById(R.id.view_media_audiolist);
		titleView = (TextView) rootView.findViewById(R.id.view_patrol_title);
		// redlineView.setOnClickListener(redlineClickListener);
		imageGridView.setAdapter(imageGridAdapter);
		audioGridView.setAdapter(audioGridAdapter);

	}

	public void setTitle(String title) {
		titleView.setText(title);
	}
	public void noneTitle() {
		titleView.setVisibility(8);
	}
	// display case redline in map view
	OnClickListener redlineClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			showRedline();
		}
	};

	void showRedline() {
		if (redlineJson != null && !redlineJson.equals("")) {
			String geoArrayString = "[" + redlineJson + "]";
			Intent intent = new Intent(parentActivity, MapViewActivity.class);
			intent.putExtra(Parameters.GEOMETRY, geoArrayString);
			intent.putExtra(Parameters.ACTIVITY_TITLE, "核查红线");
			parentActivity.startActivity(intent);
			parentActivity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	}

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
