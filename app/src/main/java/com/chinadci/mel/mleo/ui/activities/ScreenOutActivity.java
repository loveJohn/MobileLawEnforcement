package com.chinadci.mel.mleo.ui.activities;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.HttpUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ui.activities.AdminXZQFilter.AdminXZQFilterActivity;

public class ScreenOutActivity extends CaptionActivity {
	
	Context mContext;
	
	public static final String PATCH="patch";
	public static final String AJYEAR="ajyear";
	public static final String XZQBMCODE="xzqbm";
	public static final String XZQNAME="xzqname";
	
	public ArrayList<String> patchList;
	private ArrayList<String> yearList;
	private ArrayList<String> monthList;
	Dialog da;	//对话框
	String dialogTitle;
	
	String userXzqbm;		//用户自身行政区编码
	String userXzqName;		//用户自身行政区名
	
	private String patch;
	private String ajyear;		//由于接口更换，需要改成年份加月份，如  201701
	private String month;		//暂时，需要集成到ajyear中
	private String xzqbm;		//行政区编码
	private String xzqName;		//行政区名
	
	private TextView patchChooser;
	private TextView yearChooser;
	private TextView monthChooser;
	private TextView districtChooser;
	private Button filterBn;
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	protected void onBackButtonClick(View v) {
		// TODO Auto-generated method stub
		super.onBackButtonClick(v);// 退出
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onBackButtonClick(null);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext=this;
		setTitle("条件筛选");
		setContent(R.layout.fg_land_inspection_list_find);
		init();
		//通过服务获取权限内行政区列表
	}
	
	private void init(){
		patch =getIntent().getStringExtra(PATCH);		//从前次查找中获取筛选年份
		ajyear=getIntent().getStringExtra(AJYEAR);		//从前次查找中获取筛选年份
		xzqbm=getIntent().getStringExtra(XZQBMCODE);	//从前次查找中获取行政区号
		xzqName=getIntent().getStringExtra(XZQNAME);	//从前次查找中获取行政区名
		String admin[] = DBHelper.getDbHelper(mContext).getUserAdmin(currentUser);
		if (admin != null) {
			if (!admin[0].equals("") && !admin[1].equals("")) {
				userXzqbm = admin[0];
				userXzqName = admin[1];
			}
		}
		//如果无前次查找值，对行政区名赋当前用户权限行政区名
		if(xzqbm==null||xzqbm.equals("")){
			xzqbm=userXzqbm;
		}
		
		if(xzqName==null||xzqName.equals("")){
			xzqName=userXzqName;
		}
		
		currentUser= SharedPreferencesUtils.getInstance(this,shared_preferences).getSharedPreferences(sp_actuser, "");
		int currentYear=Calendar.getInstance().get(Calendar.YEAR);
		if(ajyear==null||ajyear.equals("")){
			ajyear=String.valueOf(currentYear);		//如果无前次查找值，对年份赋当前年份默认值
		}
		int yearLen=getResources().getInteger(R.integer.str_default_year_list_length);
		patchList=new ArrayList<>();
		yearList=new ArrayList<>();
		monthList=new ArrayList<>();
		for(int i=0;i<=yearLen;i++){
			yearList.add(String.valueOf(currentYear-i));
		}
		//创建月份
		monthList.add("");
		for(int i=1;i<=12;i++){
			monthList.add(String.valueOf(i));
		}
		patchChooser=(TextView)findViewById(R.id.fg_land_inspection_list_find_patch);
		yearChooser=(TextView)findViewById(R.id.fg_land_inspection_list_find_year);
		monthChooser=(TextView)findViewById(R.id.fg_land_inspection_list_find_year_month);
		districtChooser=(TextView)findViewById(R.id.fg_land_inspection_list_find_district);
		filterBn=(Button)findViewById(R.id.fg_land_inspection_list_find_do_filter);
		
		patchChooser.setText("请选择批次");
		yearChooser.setText(ajyear+"年");
		monthChooser.setText(month);
		districtChooser.setText(xzqName);
		
		patchChooser.setOnClickListener(clickListener);
		yearChooser.setOnClickListener(clickListener);
		monthChooser.setOnClickListener(clickListener);
		districtChooser.setOnClickListener(clickListener);
		filterBn.setOnClickListener(clickListener);
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	public void refreshUI(){
		if(xzqbm!=null||!xzqbm.equals("")){
			xzqName = DBHelper.getDbHelper(mContext).queryAdminFullName(xzqbm);
			districtChooser.setText(xzqName);
		}
	}
	
	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			int vid = v.getId();
			switch (vid) {
				case R.id.fg_land_inspection_list_find_patch:
					patchList.clear();
					new PatchEnrnTask().execute();		//获取批次列表
					break;
				case R.id.fg_land_inspection_list_find_year:	//年份
					showYearListDialog();
					break;
				case R.id.fg_land_inspection_list_find_year_month:
					showMonthListDialog();
					break;
				case R.id.fg_land_inspection_list_find_district:	//行政区
					turnToChooseXZQ();
					break;
				case R.id.fg_land_inspection_list_find_do_filter:	//执行筛选
					
					doFilter();
					break;
				default:
					break;
			}
		}
	};
	
	//创建批次列表对话框，选择批次
	//创建年份列表对话框，选择年份
	private void showPatchListDialog() {
		// TODO Auto-generated method stub
		da=new Dialog(mContext);
        View mView=LayoutInflater.from(mContext).inflate(R.layout.list_dialog,null);
        ListView years= (ListView) mView.findViewById(R.id.list_dialog_list);
        years.setAdapter(new MyAdapter(mContext,patchList,MyAdapter.FLAG_PATCH));
        years.setOnItemClickListener(patchLis);
        da.setContentView(mView);
        da.setTitle(dialogTitle);
        da.show();
	}
	
	//创建年份列表对话框，选择年份
	private void showYearListDialog() {
		// TODO Auto-generated method stub
		da=new Dialog(mContext);
        View mView=LayoutInflater.from(mContext).inflate(R.layout.list_dialog,null);
        ListView years= (ListView) mView.findViewById(R.id.list_dialog_list);
        years.setAdapter(new MyAdapter(mContext,yearList,MyAdapter.FLAG_YEAR));
        years.setOnItemClickListener(yearLis);
        da.setContentView(mView);
        da.setTitle("请选择年份");
        da.show();
	}
	
	//创建月份列表对话框，选择月份
		private void showMonthListDialog() {
			// TODO Auto-generated method stub
			da=new Dialog(mContext);
	        View mView=LayoutInflater.from(mContext).inflate(R.layout.list_dialog,null);
	        ListView years= (ListView) mView.findViewById(R.id.list_dialog_list);
	        years.setAdapter(new MyAdapter(mContext,monthList,MyAdapter.FLAG_MONTH));
	        years.setOnItemClickListener(monthLis);
	        da.setContentView(mView);
	        da.setTitle("请选择月份");
	        da.show();
		}
	
	//创建fragment进行行政区选择
	private void turnToChooseXZQ(){
		/*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.activity_screen_on_district, new LandInspectionListConditionXZQFragment());
		ft.commit();*/
		if (userXzqName.length()>=12) {
			//padminCode = adminCode.substring(0, 9);
			Toast.makeText(this,"您暂无法选择其他区域",Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(mContext, AdminXZQFilterActivity.class);
		intent.putExtra(AdminXZQFilterActivity.XZQ_ADMIN_CODE, userXzqbm);
		startActivityForResult(intent,Parameters.GET_ADMIN);
		overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
	}
	
	//给onActivityResult塞入返回值，finish掉自身
	private void doFilter(){		
		Bundle bundle = new Bundle();
		bundle.putString(PATCH, patch);
		bundle.putString(AJYEAR, ajyear);
		bundle.putString(XZQBMCODE, xzqbm);
		bundle.putString(XZQNAME, xzqName);
		setResult(Activity.RESULT_OK, getIntent().putExtras(bundle));
		finish();
	}
	
	public String getUser(){
		return currentUser;
	}
	
	public String getAjYear(){
		return ajyear;
	}
	
	public void SetAjYear(String year){
		ajyear=year;
	}
	
	public String getXzqbmCode(){
		return xzqbm;
	}
	
	public void setXzqbmCode(String code){
		xzqbm=code;
	}
	
	public String getXzqName(){
		return xzqName;
	}
	
	public void setXzqName(String name){
		xzqName=name;
	}
	
	
	OnItemClickListener patchLis=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			String p=adapteItem(patchList.get(arg2),MyAdapter.FLAG_PATCH);
			da.dismiss();
            
            patchChooser.setText(p);
		}
    };
	
	OnItemClickListener yearLis=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			String p=adapteItem(patchList.get(arg2),MyAdapter.FLAG_YEAR);
            da.dismiss();
            yearChooser.setText(p);
		}
    };
    
    OnItemClickListener monthLis=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			String p=adapteItem(patchList.get(arg2),MyAdapter.FLAG_MONTH);
            da.dismiss();
            monthChooser.setText(p);
		}
    };
	
	
	class MyAdapter extends BaseAdapter{
		
		public static final int FLAG_PATCH=0x01;
		public static final int FLAG_YEAR=0x02;
		public static final int FLAG_MONTH=0x03;
        Context c;
        ArrayList<String> list;
        int flag;

        public MyAdapter(Context c,ArrayList<String> list,int flag){
            this.c=c;
            this.list=list;
            this.flag=flag;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (list != null && list.size() > position) {
            	String data=adapteItem(getItem(position),flag);
                AdminAdapterTextView view = new AdminAdapterTextView(c);
                view.setText(data);
                view.setTag(data);
                convertView = view;
            }
            return convertView;
        }
        
        

        class AdminAdapterTextView extends TextView {

            public AdminAdapterTextView(Context context, AttributeSet attrs,int defStyle) {
                super(context, attrs, defStyle);
                init(context);
                // TODO Auto-generated constructor stub
            }

            public AdminAdapterTextView(Context context, AttributeSet attrs) {
                super(context, attrs);
                init(context);
                // TODO Auto-generated constructor stub
            }

            public AdminAdapterTextView(Context context) {
                super(context);
                init(context);
                // TODO Auto-generated constructor stub
            }

            void init(Context context) {
                int padding = (int) (8 * context.getResources().getDisplayMetrics().density);
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                setBackgroundColor(Color.WHITE);
                setTextColor(Color.BLACK);
                setPadding(padding, padding, padding, padding);
            }
        }
    }
	
	String adapteItem(String value,int flag){
    	try{
        	if(flag==MyAdapter.FLAG_PATCH){
        		String[] strArr=value.split("-");
        		String reValue=strArr[0]+"年第"+strArr[1]+"批次";
        		return reValue;
        	}else if(flag==MyAdapter.FLAG_YEAR){
        		String reValue=value+"年";
        		return reValue;
        	}else if(flag==MyAdapter.FLAG_MONTH){
        		String reValue=value+"月";
        		return reValue;
        	}
    	}catch(Exception e){
    		Log.i("ydzf", "ScreenOutActivity e="+e.getMessage());
    	}
    	return null;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Parameters.GET_ADMIN) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				xzqbm = bundle.getString(AdminXZQFilterActivity.XZQ_ADMIN_CODE);
				refreshUI();
			}
		}
	}
	
	class PatchEnrnTask extends AsyncTask<Void, Integer, Boolean> {
		
		AlertDialog alertDialog;
		
		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(mContext);
			abv.setMsg("正在从服务器获取批次列表，请稍候...");
			alertDialog = new AlertDialog.Builder(mContext).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
		}
		

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(mContext,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? 
						new StringBuffer(appUri).append(mContext.getString(R.string.uri_get_patch)).
						append("?key=xf").toString() : 
						new StringBuffer(appUri).append("/").append(mContext.getString(R.string.uri_get_patch)).
						append("?key=xf").toString();
				Log.i("ydzf", "uri="+uri);
				HttpResponse response = HttpUtils.httpClientExcuteGet(uri);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entiryString = EntityUtils.toString(response.getEntity());
					JSONObject backJson = new JSONObject(entiryString);
					boolean succeed = backJson.getBoolean("succeed");
					if (succeed) {
						String patchArrStr=backJson.getString("xfStr");
						Log.i("ydzf", "patchArrStr="+patchArrStr);
						String[] patchArr=patchArrStr.split(",");
						for(String str:patchArr){
							if(!str.equals("")){
								patchList.add(str);
							}
						}
					}
					dialogTitle=backJson.getString("msg");
					if(patchList.size()>0){
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				if(result){
					showPatchListDialog();
				}else{
					Toast.makeText(mContext, "未获取到批次信息，请重新尝试！", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
	
