package com.chinadci.mel.mleo.ui.views.patrolEditeView2;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.PatrolTable;
import com.chinadci.mel.mleo.ui.activities.MapViewActivity;
import com.chinadci.mel.mleo.ui.adapters.AttriAdapter2;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.utils.CaseUtils;

public class PatrolView2 extends FrameLayout {

	View rootView;
	ListView attriListView;
	TextView redlineView;
	TextView titleView;
	LinearLayout imageLayout;
	LinearLayout audioLayout;
	LinearLayout redlineLayout;
	GridView imageGridView;
	GridView audioGridView;
	
	TextView redline_fxjg;
	boolean isOpenFxjg = false;
	Drawable downDrawable;
	Drawable nextDrawable;
	TextView hx_fxjg;

	ImageGridAdapter imageGridAdapter;
	AudioGridAdapter audioGridAdapter;

	String patrolId = "";
	String patrolTable = "";
	String annexTable = "";
	String redlineJson;
	AttriAdapter2 attriAdapter;
	Activity parentActivity;

	public PatrolView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public PatrolView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PatrolView2(Context context) {
		super(context);
		initView(context);
	}

	public void setParentActivity(Activity activity) {
		parentActivity = activity;
	}

	/**
	 */
	public void setDataSource(String id, String patrolTable, String annexTable) {
		this.patrolId = id;
		this.patrolTable = patrolTable;
		this.annexTable = annexTable;
		// display case detail info
		viewPatrolInfo(this.patrolId, this.patrolTable, this.annexTable);
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

		rootView = LayoutInflater.from(context).inflate(R.layout.view_patrol2,
				null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);

		attriListView = (ListView) rootView
				.findViewById(R.id.view_patrol_attrilist);
		redlineView = (TextView) rootView
				.findViewById(R.id.view_patrol_redline);
		redline_fxjg = (TextView) rootView
				.findViewById(R.id.view_patrol_redline_fxjg);
		hx_fxjg	= (TextView) rootView
				.findViewById(R.id.hx_fxjg);
		hx_fxjg.setText("---没有分析结果---");
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
		redlineView.setOnClickListener(redlineClickListener);
		downDrawable=getResources().getDrawable(R.mipmap.ic_dropdown);
		nextDrawable=getResources().getDrawable(R.mipmap.ic_next);
		redline_fxjg.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(!isOpenFxjg){
					downDrawable.setBounds(0, 0, downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());  
					redline_fxjg.setCompoundDrawables(null, null, downDrawable, null);  
					hx_fxjg.setVisibility(View.VISIBLE);
					isOpenFxjg = true;
				}else{
					nextDrawable.setBounds(0, 0, nextDrawable.getMinimumWidth(), nextDrawable.getMinimumHeight());  
					redline_fxjg.setCompoundDrawables(null, null, nextDrawable, null); 
					hx_fxjg.setVisibility(View.GONE);
					isOpenFxjg = false;
				}
			}
		});
		imageGridView.setAdapter(imageGridAdapter);
		audioGridView.setAdapter(audioGridAdapter);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

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


	void viewPatrolInfo(String id, String pTable, String aTable) {
		try {
			try {
				hx_fxjg.setText(DbUtil.getPatrolsFxjgByid(getContext(), id));
			} catch (Exception e) {
			}
			String columns[] = new String[] { PatrolTable.field_id,
					PatrolTable.field_caseDocumentDate,
					PatrolTable.field_caseDocumentNo, PatrolTable.field_caseId,
					PatrolTable.field_deal, PatrolTable.field_mTime,
					PatrolTable.field_govDate, PatrolTable.field_notes,
					PatrolTable.field_pullInfo, PatrolTable.field_pullPlanDate,
					PatrolTable.field_pullPlan,
					PatrolTable.field_pullPlanPerson,
					PatrolTable.field_pullPlanNum, PatrolTable.field_redline,
					PatrolTable.field_regCase, PatrolTable.field_stopInfo,
					PatrolTable.field_stopNotice,
					PatrolTable.field_stopNoticeDate,
					PatrolTable.field_stopNoticeNo, PatrolTable.field_user };
			String selection = CaseInspectTable.field_id + "=?";
			String args[] = new String[] { id };
			ContentValues patrolInfo = DBHelper.getDbHelper(getContext())
					.doQuery(pTable, columns, selection, args);
			if (patrolInfo != null) {
				ArrayList<String> values = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();

				keys.add(getContext().getString(R.string.v_f26));
				if (patrolInfo.getAsString(PatrolTable.field_deal) != null
						&& !patrolInfo.getAsString(PatrolTable.field_deal)
								.equals("")) {
					values.add(patrolInfo.getAsString(PatrolTable.field_deal));
				} else {
					values.add("");
				}

				if (patrolInfo.getAsString(PatrolTable.field_stopNoticeNo) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_stopNoticeNo).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_stopNoticeNo));
					keys.add(getContext().getString(R.string.v_f27));
				}

				if (patrolInfo.getAsString(PatrolTable.field_stopNoticeDate) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_stopNoticeDate).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_stopNoticeDate));
					keys.add(getContext().getString(R.string.v_f28));
				}

				if (patrolInfo.getAsString(PatrolTable.field_pullPlanDate) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_pullPlanDate).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_pullPlanDate));
					keys.add(getContext().getString(R.string.v_f29));
				}

				if (patrolInfo.getAsString(PatrolTable.field_pullPlanNum) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_pullPlanNum).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_pullPlanNum));
					keys.add(getContext().getString(R.string.v_f30));
				}

				if (patrolInfo.getAsString(PatrolTable.field_pullPlanPerson) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_pullPlanPerson).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_pullPlanPerson));
					keys.add(getContext().getString(R.string.v_f31));
				}

				if (patrolInfo.getAsString(PatrolTable.field_caseDocumentNo) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_caseDocumentNo).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_caseDocumentNo));
					keys.add(getContext().getString(R.string.v_f32));
				}
				if (patrolInfo.getAsString(PatrolTable.field_caseDocumentDate) != null
						&& !patrolInfo.getAsString(
								PatrolTable.field_caseDocumentDate).equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_caseDocumentDate));
					keys.add(getContext().getString(R.string.v_f33));
				}

				if (patrolInfo.getAsString(PatrolTable.field_govDate) != null
						&& !patrolInfo.getAsString(PatrolTable.field_govDate)
								.equals("")) {
					values.add(patrolInfo
							.getAsString(PatrolTable.field_govDate));
					keys.add(getContext().getString(R.string.v_f42));
				}

				keys.add(getContext().getString(R.string.v_f11));
				if (patrolInfo.getAsString(PatrolTable.field_notes) != null
						&& !patrolInfo.getAsString(PatrolTable.field_notes)
								.equals("")) {
					values.add(patrolInfo.getAsString(PatrolTable.field_notes));
				} else {
					values.add("");
				}

				keys.add(getContext().getString(R.string.v_f34));
				if (patrolInfo.getAsString(PatrolTable.field_user) != null
						&& !patrolInfo.getAsString(PatrolTable.field_user)
								.equals("")) {
					values.add(patrolInfo.getAsString(PatrolTable.field_user));
				} else {
					values.add("");
				}

				keys.add(getContext().getString(R.string.v_f35));
				if (patrolInfo.getAsString(PatrolTable.field_mTime) != null
						&& !patrolInfo.getAsString(PatrolTable.field_mTime)
								.equals("")) {
					values.add(TimeFormatFactory2.getDisplayTimeM(patrolInfo
							.getAsString(PatrolTable.field_mTime)));
				} else {
					values.add("");
				}

				attriAdapter = new AttriAdapter2(getContext(), keys, values);
				attriListView.setAdapter(attriAdapter);
				attriAdapter.notifyDataSetChanged();

				redlineJson = patrolInfo
						.getAsString(CasePatrolTable.field_redline);
				if (redlineJson != null && !redlineJson.equals("")) {
					redlineLayout.setVisibility(View.VISIBLE);
				} else {
					redlineLayout.setVisibility(View.GONE);
				}
				// String annexFields[] = new String[] {
				// CaseAnnexesTable.field_path };
				// String annexSelection = new StringBuffer(
				// CaseAnnexesTable.field_tagId).append("=? and ")
				// .append(CaseAnnexesTable.field_tag).append("=?")
				// .toString();
				// String annexArgs[] = new String[] {
				// patrolInfo.getAsString(CasePatrolTable.field_id),
				// pTable };
				// ArrayList<ContentValues> annexList = DBHelper.getDbHelper(
				// getContext()).doQuery(aTable, annexFields,
				// annexSelection, annexArgs, null, null, null, null);
				// if (annexList != null && annexList.size() > 0) {
				// for (ContentValues annex : annexList) {
				// String path = annex
				// .getAsString(CaseAnnexesTable.field_path);
				// String ext = path.substring(path.lastIndexOf("."));
				// String type = MIMEMapTable.getInstance()
				// .getParentMIMEType(ext);
				// File f = new File(path);
				// if (f.exists())
				// if (type.equals("audio/*")) {
				// if (audioLayout.getVisibility() != View.VISIBLE)
				// audioLayout.setVisibility(View.VISIBLE);
				// audioGridAdapter.addItem(path);
				// } else if (type.equals("image/*")) {
				// imageGridAdapter.addItem(path);
				// if (imageLayout.getVisibility() != View.VISIBLE)
				// imageLayout.setVisibility(View.VISIBLE);
				// }
				// }
				// }
				// imageGridAdapter.notifyDataSetChanged();
				// audioGridAdapter.notifyDataSetChanged();
				new annexGetTask(getContext(),
						patrolInfo.getAsString(CasePatrolTable.field_id),
						pTable, aTable).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// display case redline in map view
	OnClickListener redlineClickListener = new OnClickListener() {

		public void onClick(View v) {
			showRedline();
		}
	};

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
		String id;
		String pTable;
		String aTable;
		ArrayList<ContentValues> annexList;

		public annexGetTask(Context c, String id, String pTable, String aTable) {
			this.context = c;
			this.id = id;
			this.pTable = pTable;
			this.aTable = aTable;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String annexFields[] = new String[] {
						CaseAnnexesTable.field_path, CaseAnnexesTable.field_uri };
				String annexSelection = new StringBuffer(
						CaseAnnexesTable.field_tagId).append("=? and ")
						.append(CaseAnnexesTable.field_tag).append("=?")
						.toString();
				String annexArgs[] = new String[] { id, pTable };
				annexList = DBHelper.getDbHelper(context).doQuery(aTable,
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
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			msg = "服务器返回异常";
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
				if (imageLayout.getVisibility() != View.VISIBLE)
					imageLayout.setVisibility(View.VISIBLE);
				imageGridAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
