package com.chinadci.mel.mleo.ui.adapters;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.TimeFormatFactory2;
import com.chinadci.mel.mleo.ldb.AnnexTable;
import com.chinadci.mel.mleo.ldb.CaseTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectTable;
import com.chinadci.mel.mleo.ldb.LocalCaseTable;
import com.chinadci.mel.mleo.ldb.ParmeterTable;
import com.chinadci.mel.mleo.ldb.PatrolTable;
import com.chinadci.mel.mleo.ldb.WebCaseTable;

/**
 * @ClassName WebCaseAdapter
 */
public class WebCaseAdapter extends BaseAdapter {
	ArrayList<ContentValues> items;
	private Activity activity;
	private Context context;
	private LayoutInflater inflater;
	private CaseAdapterHolder holder;
	AlertDialog alertDialog;
	String sourceTalbe;
	String urgencyTable;
	String parmeterColumns[];
	String parmeterSelection;

	String caseTable = "";
	String annexTable = "";
	String patrolTable = "";
	String inspectionTable = "";
	int delButtonVisibility = View.VISIBLE;
	DecimalFormat kmFormat = new DecimalFormat("#.####");

	public WebCaseAdapter(Context cxt, ArrayList<ContentValues> its) {
		context = cxt;
		items = its;
		inflater = LayoutInflater.from(context);
		sourceTalbe = context.getString(R.string.tb_casesource);
		urgencyTable = context.getString(R.string.tb_urgency);
		parmeterColumns = new String[] { ParmeterTable.field_id,
				ParmeterTable.field_name };
		parmeterSelection = new StringBuffer(ParmeterTable.field_id).append(
				"=?").toString();
	}

	public void setDelButtonVisibility(int visibility) {
		this.delButtonVisibility = visibility;
		notifyDataSetChanged();
	}

	public void setTableSource(String caseTable, String annexTalbe,
			String patrolTable, String inspectTable) {
		this.caseTable = caseTable;
		this.annexTable = annexTalbe;
		this.patrolTable = patrolTable;
		this.inspectionTable = inspectTable;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Boolean insertItem(int position, ContentValues item) {
		if (items != null && items.size() >= position) {
			items.add(position, item);
			return true;
		}
		return false;
	}

	public void setDataSet(ArrayList<ContentValues> its) {
		items = its;
	}

	public void addDataSet(ArrayList<ContentValues> its) {
		if (its != null && its.size() > 0) {
			if (items == null)
				items = new ArrayList<ContentValues>();
			for (int i = 0; i < its.size(); i++) {
				ContentValues cv = its.get(i);
				items.add(cv);
			}
		}
	}

	public int getCount() {
		if (items != null && items.size() > 0)
			return items.size();
		else
			return 0;
	}

	public Object getItem(int position) {
		if (items != null && items.size() > position)
			return items.get(position);
		else
			return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		String valueCreateTime;

		Double valueArea;
		String textAddress;
		String textCreateTime;
		String textParties;
		String textArea = "";
		String textStatus = "";

		if (items == null || items.size() < position)
			return convertView;

		try {
			ContentValues itemInfo = items.get(position);
			convertView = inflater.inflate(R.layout.adapter_case, null);
			holder = new CaseAdapterHolder();
			holder.keyValuesLayout = (LinearLayout) convertView
					.findViewById(R.id.adapter_case_keyvalues);

			holder.timeView = (TextView) convertView
					.findViewById(R.id.adapter_case_localtime);
			holder.deleteView = (TextView) convertView
					.findViewById(R.id.adapter_case_delete);
			holder.numView = (TextView) convertView
					.findViewById(R.id.adapter_case_numview);
			holder.deleteView.setTag(convertView);
			convertView.setTag(itemInfo);

			valueCreateTime = itemInfo.getAsString(CaseTable.field_mTime);

			valueArea = itemInfo.getAsDouble(CaseTable.field_illegalArea);
			if (valueArea != null)
				textArea = kmFormat.format(valueArea) + "m²";
			textStatus = itemInfo.getAsString(WebCaseTable.field_status_text);

			textAddress = itemInfo.getAsString(CaseTable.field_address);
			textParties = itemInfo.getAsString(CaseTable.field_parties);
			textCreateTime = TimeFormatFactory2
					.getDisplayTimeM(valueCreateTime);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			ElemTextView partiesView = new ElemTextView(context);
			if (textParties == null)
				textParties = "";
			partiesView.setText(String.format(context.getString(R.string.f_f1),
					textParties));

			ElemTextView addressView = new ElemTextView(context);
			if (textAddress == null)
				textAddress = "";
			addressView.setText(String.format(context.getString(R.string.f_f2),
					textAddress));

			ElemTextView areaView = new ElemTextView(context);
			areaView.setText(String.format(context.getString(R.string.f_f3),
					textArea));

			ElemTextView statusView = new ElemTextView(context);
			if (textStatus == null)
				textStatus = "";
			statusView.setText(String.format(context.getString(R.string.f_f4),
					textStatus));

			holder.numView.setText(String.valueOf(position + 1));
			holder.timeView.setText(textCreateTime);
			holder.keyValuesLayout.addView(partiesView, params);
			holder.keyValuesLayout.addView(addressView, params);
			holder.keyValuesLayout.addView(areaView, params);
			holder.keyValuesLayout.addView(statusView, params);
			holder.deleteView.setVisibility(delButtonVisibility);

			final int p = position;
			holder.deleteView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final View delView = v;
					View alertView = LayoutInflater.from(context).inflate(
							R.layout.view_alert, null);
					TextView notesView = (TextView) alertView
							.findViewById(R.id.view_alert_notes);
					Button cancelButton = (Button) alertView
							.findViewById(R.id.view_alert_cancel);
					Button doButton = (Button) alertView
							.findViewById(R.id.view_alert_do);
					notesView.setText("确定要删除此案件吗？");
					cancelButton.setText("暂不删除");
					doButton.setText("确定删除");

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
							new caseDeleteTask(delView).execute(p);
						}
					});

					alertDialog = new AlertDialog.Builder(activity).create();
					alertDialog.show();
					alertDialog.setCancelable(true);
					alertDialog.getWindow().setContentView(alertView);
				}
			});
			return convertView;
		} catch (Exception e) {
			return convertView;
		}
	}

	public class caseDeleteTask extends AsyncTask<Integer, Integer, Void> {
		String caseId;
		View view;
		int index;

		public caseDeleteTask(View v) {
			view = (View) v.getTag();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			if (params != null && params.length > 0) {
				try {
					index = params[0];
					ContentValues itemInfo = items.get(index);
					caseId = itemInfo.getAsString(LocalCaseTable.field_id);
					ArrayList<ContentValues> annexValues = DBHelper
							.getDbHelper(context).doQuery(annexTable,
									new String[] { AnnexTable.field_path },
									AnnexTable.field_caseId + "=?",
									new String[] { caseId }, null, null, null,
									null);
					if (annexValues != null && annexValues.size() > 0) {
						for (ContentValues annex : annexValues) {
							String path = annex.getAsString("path");
							File file = new File(path);
							if (file.exists())
								file.delete();// 删除附件文件
						}
					}
					DBHelper.getDbHelper(context).delete(annexTable,
							AnnexTable.field_caseId + "=?",
							new String[] { caseId });// 删除附件表数据

					DBHelper.getDbHelper(context).delete(caseTable,
							LocalCaseTable.field_id + "=?",
							new String[] { caseId });// 删除本地案件表数据

					DBHelper.getDbHelper(context).delete(inspectionTable,
							InspectTable.field_caseId + "=?",
							new String[] { caseId });

					DBHelper.getDbHelper(context).delete(patrolTable,
							PatrolTable.field_caseId + "=?",
							new String[] { caseId });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Animation an = AnimationUtils.loadAnimation(context,
					R.anim.slide_out_left);

			an.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					items.remove(index);
					notifyDataSetChanged();
				}
			});
			view.startAnimation(an);
		}
	}

	final class CaseAdapterHolder {
		private LinearLayout keyValuesLayout;
		private TextView timeView;
		private TextView deleteView;
		private TextView numView;
	}

	class ElemTextView extends TextView {

		public ElemTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			setDefaultAttri();
		}

		public ElemTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			setDefaultAttri();
		}

		public ElemTextView(Context context) {
			super(context);
			setDefaultAttri();
		}

		void setDefaultAttri() {
			int spacing = (int) (2 * context.getResources().getDisplayMetrics().density);
			setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			setTextColor(0xff646464);
			setPadding(spacing, spacing / 2, spacing, spacing / 2);
			setSingleLine(false);
		}
	}
}
