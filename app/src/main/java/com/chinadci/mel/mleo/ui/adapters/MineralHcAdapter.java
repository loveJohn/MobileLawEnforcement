package com.chinadci.mel.mleo.ui.adapters;

import java.io.File;
import java.util.ArrayList;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.WebMinPatrolTable;
import com.chinadci.mel.mleo.ldb.ParmeterTable;

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
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MineralHcAdapter extends BaseAdapter {
	ArrayList<ContentValues> items;
	private Activity activity;
	private Context context;
	private LayoutInflater inflater;
	private AdapterHolder holder;
	AlertDialog alertDialog;

	String caseTable = "";
	String annexTable = "";
	String urgencyTable;
	String sourceTalbe;
	String parmeterColumns[];
	String parmeterSelection;

	public MineralHcAdapter(Context cxt, ArrayList<ContentValues> its) {
		context = cxt;
		items = its;
		inflater = LayoutInflater.from(context);
		sourceTalbe = context.getString(R.string.tb_casesource);
		urgencyTable = context.getString(R.string.tb_urgency);
		parmeterColumns = new String[] { ParmeterTable.field_id, ParmeterTable.field_name };
		parmeterSelection = new StringBuffer(ParmeterTable.field_id).append("=?").toString();
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public void setTableSource(String caseTable, String annexTalbe) {
		this.caseTable = caseTable;
		this.annexTable = annexTalbe;
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
		// TODO Auto-generated method stub
		if (items != null && items.size() > 0)
			return items.size();
		else
			return 0;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (items != null && items.size() > position)
			return items.get(position);
		else
			return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
//		String hasMining = "否";
//		String exception;
//		String notes;
		String ffztmc="";//违法主体名称 
		String szcj="";//所在村居
		String ffccdbh="";//非法采矿点编码
		String ajzt="";//案件状态
		String logTime;

		if (items == null || items.size() < position)
			return convertView;

		try {
			ContentValues itemInfo = items.get(position);
			convertView = inflater.inflate(R.layout.adapter_milhc, null);
			holder = new AdapterHolder();
			holder.keyValuesLayout = (LinearLayout) convertView
					.findViewById(R.id.adapter_case_keyvalues);

			holder.timeView = (TextView) convertView.findViewById(R.id.adapter_case_localtime);
			holder.deleteView = (TextView) convertView.findViewById(R.id.adapter_case_delete);
			holder.numView = (TextView) convertView.findViewById(R.id.adapter_case_numview);
			holder.deleteView.setTag(convertView);
			convertView.setTag(itemInfo);
//			hasMining = itemInfo.getAsString(WebMinPatrolTable.field_hasMining);
//			exception = itemInfo.getAsString(WebMinPatrolTable.field_exception);
//			notes = itemInfo.getAsString(WebMinPatrolTable.field_notes);
			ffztmc=itemInfo.getAsString(WebMinPatrolTable.field_wfztmc);
			szcj=DBHelper.getDbHelper(context).queryAdminFullName(itemInfo.getAsString(WebMinPatrolTable.field_szcj));
			ffccdbh=itemInfo.getAsString(WebMinPatrolTable.field_ffckbh);
			ajzt=itemInfo.getAsString(WebMinPatrolTable.field_ajzt);
			logTime = itemInfo.getAsString(WebMinPatrolTable.field_logTime);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			ElemTextView wfztmcView=new ElemTextView(context);
			wfztmcView.setText("违法主体名称："+ffztmc);
			ElemTextView szcjView=new ElemTextView(context);
			szcjView.setText("所在村居："+szcj);
			ElemTextView ffckdbhView=new ElemTextView(context);
			ffckdbhView.setText("非法采矿点编号："+ffccdbh);
			ElemTextView ajztView=new ElemTextView(context);
			ajztView.setText("案件状态："+ajzt);
			holder.keyValuesLayout.addView(wfztmcView, params);
			holder.keyValuesLayout.addView(szcjView, params);
			holder.keyValuesLayout.addView(ffckdbhView, params);
			holder.keyValuesLayout.addView(ajztView, params);
			
//			ElemTextView exceptionView = new ElemTextView(context);
//			exceptionView.setText("发现问题：" + exception);

//			ElemTextView miningView = new ElemTextView(context);
//			if (hasMining.equals("0")) {
//				miningView.setText("非法采矿点：没有发现非法采矿点");
//			} else if (hasMining.equals("1")) {
//				miningView.setText("非法采矿点：发现非法采矿点");
//			}

//			ElemTextView notesView = new ElemTextView(context);
//			notesView.setText("备注：" + notes);

			holder.numView.setText(String.valueOf(position + 1));
			holder.timeView.setText("巡查日期：" + logTime);
//			holder.keyValuesLayout.addView(miningView, params);
//			holder.keyValuesLayout.addView(exceptionView, params);
//			holder.keyValuesLayout.addView(notesView, params);

			final int p = position;
			holder.deleteView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final View delView = v;
					View alertView = LayoutInflater.from(context)
							.inflate(R.layout.view_alert, null);
					TextView notesView = (TextView) alertView.findViewById(R.id.view_alert_notes);
					Button cancelButton = (Button) alertView.findViewById(R.id.view_alert_cancel);
					Button doButton = (Button) alertView.findViewById(R.id.view_alert_do);
					notesView.setText("确定要删除此日志吗？");
					cancelButton.setText("暂不删除");
					doButton.setText("确定删除");

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
							new deleteTask(delView).execute(p);
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
			// TODO: handle exception
			e.printStackTrace();
			return convertView;
		}
	}

	class deleteTask extends AsyncTask<Integer, Integer, Void> {
		String id;
		View view;
		int index;

		public deleteTask(View v) {
			view = (View) v.getTag();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			if (params != null && params.length > 0) {
				try {

					index = params[0];
					ContentValues itemInfo = items.get(index);
					id = itemInfo.getAsString(WebMinPatrolTable.field_id);
					ArrayList<ContentValues> annexValues = DBHelper.getDbHelper(context).doQuery(
							MilPatrolAnnexesTable.name,
							new String[] { MilPatrolAnnexesTable.field_path },
							MilPatrolAnnexesTable.field_tagId + "=?", new String[] { id }, null,
							null, null, null);
					if (annexValues != null && annexValues.size() > 0) {
						for (ContentValues annex : annexValues) {
							String path = annex.getAsString("path");
							File file = new File(path);
							if (file.exists())
								file.delete();// 删除附件文件
						}
					}
					DBHelper.getDbHelper(context).delete(MilPatrolAnnexesTable.name,
							MilPatrolAnnexesTable.field_tagId + "=?", new String[] { id });// 删除附件表数据
					DBHelper.getDbHelper(context).delete(WebMinPatrolTable.name,
							WebMinPatrolTable.field_id + "=?", new String[] { id });// 删除本地案件表数据
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			Animation an = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);

			an.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					items.remove(index);
					notifyDataSetChanged();
				}
			});
			view.startAnimation(an);
		}
	}

	final class AdapterHolder {
		private LinearLayout keyValuesLayout;
		private TextView timeView;
		private TextView deleteView;
		private TextView numView;
	}

	class ElemTextView extends TextView {

		public ElemTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			setDefaultAttri();
			// TODO Auto-generated constructor stub
		}

		public ElemTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			setDefaultAttri();
			// TODO Auto-generated constructor stub
		}

		public ElemTextView(Context context) {
			super(context);
			setDefaultAttri();
			// TODO Auto-generated constructor stub
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
