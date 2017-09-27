package com.chinadci.mel.mleo.ui.fragments;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.andbase.library.http.entity.MultipartEntity;
import com.andbase.library.http.entity.mine.content.ContentBody;
import com.andbase.library.http.entity.mine.content.FileBody;
import com.andbase.library.http.entity.mine.content.StringBody;
import com.chinadci.android.media.MIMEMapTable;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.MilPatrolAnnexesTable;
import com.chinadci.mel.mleo.ldb.MilPatrolTable;
import com.chinadci.mel.mleo.ldb.MineralHcTable;
import com.chinadci.mel.mleo.ldb.WebMinPatrolTable;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.activities.PolyGatherActivity;
import com.chinadci.mel.mleo.ui.activities.TapeActivity;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.views.MineralHcEditeView;
import com.chinadci.mel.mleo.ui.views.MineralHcView;
import com.chinadci.mel.mleo.ui.views.MineralView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MineralHcEditeFragment extends ContentFragment {
	List<View> viewList=new ArrayList<View>();
	List<String> titleList=new ArrayList<String>();
	
	View contentView;
	ViewPager viewPager;
	ImageView image;
	TextView tabView1;
	TextView tabView2;
	TextView tabView3;
	
	int moveX;  //导航下面偏移的宽度 
	int width;  //导航下面比较粗的宽度 
	int index;  //当前第一个View
	
	String caseId;
	
	MineralView mineralInfoView;
	MineralHcView mineralhcView;
	MineralHcEditeView mineralhcEditView;
	
	AlertDialog alertDialog;
	MineralHcPagerAdapter pagerAdapter;
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=Activity.RESULT_OK)
			return;
		switch(requestCode)
		{
			case Parameters.GET_PHOTO:
				Bundle photoBundle=data.getExtras();
				String photos[]=photoBundle
						.getStringArray(CameraPhotoActivity.PHOTOARRAY);
				mineralhcEditView.addPhotoPaths(photos);
				break;
			case Parameters.PICK_IMAGE:
				try {
					Uri uri = data.getData();
					String[] proj = { MediaStore.Images.Media.DATA };
					@SuppressWarnings("deprecation")
					Cursor cursor = getActivity().managedQuery(uri, proj, null,
							null, null);
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(column_index);
					String photoss[] = {path};
					mineralhcEditView.addPhotoPaths(photoss);
				} catch (Exception e) {
				}
				break;
			case Parameters.GET_AUDIO:
				Bundle audioBundle=data.getExtras();
				String amrPath=audioBundle.getString(TapeActivity.AMRFILE);
				mineralhcEditView.addAudioPath(amrPath);
				break;
			case Parameters.GET_REDLINE:
				Bundle redlineBundle=data.getExtras();
				String relineString =redlineBundle.getString(
						PolyGatherActivity.REDLINE).toString();
				//mineralhcEditView.setRed
				break;
			default:
				break;
		}
		
	}
	
	//tool的方法
	@Override
	public void handle(Object o)
	{
		super.handle(o);
		int tag=(Integer)o;
		if(tag==0)
		{
			new MineralHcSaveTask().execute();
		}
		else if(tag==1)
		{
			String patrolCheckMsg=mineralhcEditView.checkInput();
			if(patrolCheckMsg==null){
				if(mineralhcEditView!=null)
				{
					String mineralhcCheckMsg=mineralhcEditView.checkInput();
					if(mineralhcCheckMsg==null)
					{
						new MineralHcSendTask2().execute();
					}
					else
					{
						Toast.makeText(context, patrolCheckMsg,
								Toast.LENGTH_SHORT).show();
					}
					
				}
				else
				{

					new MineralHcSendTask2().execute();
				}
			}
			else
			{
				Toast.makeText(context, patrolCheckMsg, Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	@Override
	public void refreshUi()
	{
		super.refreshUi();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context=getActivity().getApplicationContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,
			Bundle savedInstanceState)
	{
		caseId=getArguments().getString(Parameters.CASE_ID);
		contentView=inflater.inflate(R.layout.fragment_mineralhc_edite, container,false);
		viewPager=(ViewPager)contentView
				.findViewById(R.id.fragment_inspection_edite_viewpager);
		tabView1=(TextView)contentView.findViewById(R.id.text1);
		tabView2=(TextView)contentView.findViewById(R.id.text2);
//		tabView3=(TextView)contentView.findViewById(R.id.text3);
		image=(ImageView)contentView.findViewById(R.id.iamge);
		tabView1.setOnClickListener(tabClickListener);
		tabView2.setOnClickListener(tabClickListener);
//		tabView3.setOnClickListener(tabClickListener);
		viewPager.setOnPageChangeListener(new MyPageListener());
		
		int screenW=getResources().getDisplayMetrics().widthPixels;
		width=BitmapFactory.decodeResource(getResources(), R.mipmap.mm)
				.getWidth();
		moveX=(screenW/2 -width)/2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(moveX, 0);
		image.setImageMatrix(matrix); // 设置动画初始位置
		initFragment();
		return contentView;
		
	}
	
	OnClickListener tabClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.text1:
				viewPager.setCurrentItem(0);
				break;
			case R.id.text2:
				viewPager.setCurrentItem(1);
				break;
//
//			case R.id.text3:
//				viewPager.setCurrentItem(2);
//				break;
			}
		}
	};
	void initFragment() 
	{
		try
		{
			mineralInfoView=new MineralView(context);
			mineralInfoView.setParentActivity(getActivity());
			mineralInfoView.setDataSource(currentUser, caseId, WebMinPatrolTable.name,MilPatrolAnnexesTable.name);
			
			
			ContentValues mineralValues=DBHelper.getDbHelper(context)
					.doQuery(MilPatrolTable.name, 
							new String[] { MilPatrolTable.field_id},
							MilPatrolTable.field_id + "=?",
						new String[] { caseId });
			
//			if(mineralValues!=null)
//			{
//				int mineralStatus=mineralValues
//						.getAsInteger(MilPatrolTable.field_status);
//				if (mineralStatus == 2) {
//					mineralhcView = new MineralHcView(context);
//					mineralhcView.setDataSource(mineralValues
//							.getAsString(CaseInspectTable.field_caseId),
//							CaseInspectTable.name);
//					viewList.add(mineralhcView);
//					titleList.add("信息核查");
//				} else {
//					mineralhcEditView = new MineralHcEditeView(context,
//							getActivity());
//					mineralhcEditView.setDataSource(currentUser, caseId,
//							CaseInspectTable.name);
//					viewList.add(mineralhcEditView);
//					titleList.add("信息核查");
//				}
//			}
//			else {
				mineralhcEditView = new MineralHcEditeView(context,
						getActivity());
				mineralhcEditView.setParentActivity(getActivity());
				mineralhcEditView.setDataSource(currentUser, caseId,
						MineralHcTable.name);
//				viewList.add(mineralhcEditView);
//				titleList.add("信息核查");
//			}

			viewList.add(mineralhcEditView);
			titleList.add("处理结果");
			
			viewList.add(mineralInfoView);
			titleList.add("上报信息");

			pagerAdapter = new MineralHcPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
			
			//mineralhcView
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// save inspection in background thread
		class inspectionSaveTask extends AsyncTask<Void, Boolean, Boolean> {
			String msg = "";

			@Override
			protected void onPreExecute() {
				CircleProgressBusyView abv = new CircleProgressBusyView(context);
				abv.setMsg(getString(R.string.cn_saveing));
				alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.show();
				alertDialog.setCancelable(false);
				alertDialog.getWindow().setContentView(abv);
				if (mineralhcEditView != null)
					mineralhcEditView.getTextViewValues();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
//				boolean patrolSaved = patrolEditeView.savePatrol();
//				if (patrolSaved)
//					msg = "处理结果保存成功";
//				else
//					msg = "处理结果保存失败";
//
//				publishProgress(patrolSaved);
//
//				if (mineralhcEditView != null) {
//					boolean inspectionSaved = mineralhcEditView.saveInspection();
//					if (inspectionSaved)
//						msg = "信息核查保存成功";
//					else
//						msg = "信息核查保存失败";
//					publishProgress(inspectionSaved);
//				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Boolean... values) {
				// TODO Auto-generated method stub
				if (msg != null && !msg.equals("")) {
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
			}
		}

		// send inspection in background thread
		class MineralHcSendTask extends AsyncTask<Void, Void, Boolean> {
			String msg = "";

			@Override
			protected void onPreExecute() {
				CircleProgressBusyView abv = new CircleProgressBusyView(context);
				abv.setMsg(getString(R.string.cn_sendding));
				alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.show();
				alertDialog.setCancelable(false);
				alertDialog.getWindow().setContentView(abv);
				if (mineralhcEditView != null)
					mineralhcEditView.getTextViewValues();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean succeed = false;
				try {
					//patrolEditeView.savePatrol();
					String inspectionUri = "";
					String annexUri = "";
					String appUri = SharedPreferencesUtils.getInstance(context,
							R.string.shared_preferences).getSharedPreferences(
							R.string.sp_appuri, "");
					inspectionUri = appUri.endsWith("/") ? new StringBuffer(appUri)
							.append(context
									.getString(R.string.uri_mineralhc_log))
							.toString() : new StringBuffer(appUri)
							.append("/")
							.append(context
									.getString(R.string.uri_mineralhc_log))
							.toString();
					annexUri = appUri.endsWith("/") ? new StringBuffer(appUri)
							.append(context.getString(R.string.uri_annex_service))
							.toString() : new StringBuffer(appUri).append("/")
							.append(context.getString(R.string.uri_annex_service))
							.toString();
								
					JSONObject patrolJson = mineralhcEditView.getPatrolJson(); 

					HttpClient client = new DefaultHttpClient();
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, 10000);
					HttpPost post = new HttpPost(inspectionUri);
					post.setHeader(HTTP.CONTENT_TYPE, "application/json");
					post.setEntity(new StringEntity(patrolJson.toString(), HTTP.UTF_8));
					HttpResponse response = client.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(response
								.getEntity());
						JSONObject backJson = new JSONObject(entityString);
						if (backJson.getBoolean("succeed")) {
							String serverPatrolId = backJson
									.getString("patrolId");
							ArrayList<String> annexes = mineralhcEditView
									.getAnnexes();
							if (annexes != null && annexes.size() > 0) {
								for (String s : annexes) {
									File file = new File(s);
									String fileName = file.getName();
									String prefix = fileName.substring(fileName
											.lastIndexOf("."));
									String mime = MIMEMapTable.getInstance()
											.getMIMEType(prefix);
									JSONObject attriJson = new JSONObject();
									attriJson.put("type", "inspection");
									attriJson.put("tagId", serverPatrolId);
									attriJson.put("name", fileName);

									attriJson.put("fjlx", mime.substring(0,
											mime.lastIndexOf("/")));
									MultipartEntity postEntity = new MultipartEntity();
									ContentBody cbAttri = new StringBody(
											attriJson.toString(),
											Charset.forName(HTTP.UTF_8));

									ContentBody cbFileData = new FileBody(file,
											mime);
									postEntity.addPart("attriData", cbAttri);
									postEntity.addPart("fileData", cbFileData);

									HttpPost annexPost = new HttpPost(annexUri);
									annexPost.setEntity(postEntity);
									client.execute(annexPost);
								}
							}

							if (annexes != null && annexes.size() > 0) {
								for (String s : annexes) {
									File file = new File(s);
									file.delete();
								}
							}

							ArrayList<ContentValues> annexValues = DBHelper
									.getDbHelper(context)
									.doQuery(
											MilPatrolAnnexesTable.name,
											new String[] { MilPatrolAnnexesTable.field_path },
											new StringBuffer(
													MilPatrolAnnexesTable.field_tagId)
													.append("=?").toString(),
											new String[] { caseId }, null, null,
											null, null);
							if (annexValues != null && annexValues.size() > 0) {
								for (ContentValues contentValues : annexValues) {
									String path = contentValues
											.getAsString(MilPatrolAnnexesTable.field_path);
									File file = new File(path);
									if (file.exists())
										file.delete();
								}
							}
					
					
							DBHelper.getDbHelper(context).delete(
									MilPatrolTable.name,
									MilPatrolTable.field_id + "=?",
									new String[] { caseId });
							DBHelper.getDbHelper(context).delete(
									MineralHcTable.name,
									MineralHcTable.field_caseId + "=?",
									new String[] { caseId });
							DBHelper.getDbHelper(context).delete(
									MilPatrolAnnexesTable.name,
									MilPatrolAnnexesTable.field_tagId + "=?",
									new String[] { caseId });
							DBHelper.getDbHelper(context).delete(
									WebMinPatrolTable.name,
									WebMinPatrolTable.field_id + "=?",
									new String[] { caseId });
					
							
							succeed = true;
							msg = "核查处理发送成功";
						} else {
							succeed = false;
							msg = "核查处理发送失败："+backJson.getString("msg");
							//msg = "核查处理发送失败";
						}
					} else {
						succeed = false;
						msg = "核查处理发送失败";
					}
				} catch (Exception e) {
					e.printStackTrace();
					succeed = false;
					msg = "发送核查处理时发生异常";
				}
				return succeed;
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				GravityCenterToast.makeText(context, msg, Toast.LENGTH_SHORT)
						.show();

				if (result) {
					getActivity().onBackPressed();
				}
			}
		}
	
		
		// send inspection in background thread
		class MineralHcSendTask2 extends AsyncTask<Void, Void, Boolean> {
			String msg = "";
			@Override
			protected void onPreExecute() {
				CircleProgressBusyView abv = new CircleProgressBusyView(context);
				abv.setMsg(getString(R.string.cn_sendding));
				alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.show();
				alertDialog.setCancelable(false);
				alertDialog.getWindow().setContentView(abv);
				if (mineralhcEditView != null)
					mineralhcEditView.getTextViewValues();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean succeed = false;
				try {
					//patrolEditeView.savePatrol();
					String inspectionUri = "";
					String annexUri = "";
					String appUri = SharedPreferencesUtils.getInstance(context,
							R.string.shared_preferences).getSharedPreferences(
							R.string.sp_appuri, "");
					inspectionUri = appUri.endsWith("/") ? new StringBuffer(appUri)
							.append(context
									.getString(R.string.uri_mineralhc_log))
							.toString() : new StringBuffer(appUri)
							.append("/")
							.append(context
									.getString(R.string.uri_mineralhc_log))
							.toString();
					JSONArray annexArray = sendAnnexes();
								
					JSONObject patrolJson = mineralhcEditView.getPatrolJson(); 
					patrolJson.put("annexes", annexArray);
					HttpClient client = new DefaultHttpClient();
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, 10000);
					HttpPost post = new HttpPost(inspectionUri);
					post.setHeader(HTTP.CONTENT_TYPE, "application/json");
					post.setEntity(new StringEntity(patrolJson.toString(), HTTP.UTF_8));
					HttpResponse response = client.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						String entityString = EntityUtils.toString(response
								.getEntity());
						JSONObject backJson = new JSONObject(entityString);
						if (backJson.getBoolean("succeed")) {
//							String serverPatrolId = backJson
//									.getString("patrolId");
							ArrayList<String> annexes = mineralhcEditView
									.getAnnexes();
							

							if (annexes != null && annexes.size() > 0) {
								for (String s : annexes) {
									File file = new File(s);
									file.delete();
								}
							}

							ArrayList<ContentValues> annexValues = DBHelper
									.getDbHelper(context)
									.doQuery(
											MilPatrolAnnexesTable.name,
											new String[] { MilPatrolAnnexesTable.field_path },
											new StringBuffer(
													MilPatrolAnnexesTable.field_tagId)
													.append("=?").toString(),
											new String[] { caseId }, null, null,
											null, null);
							if (annexValues != null && annexValues.size() > 0) {
								for (ContentValues contentValues : annexValues) {
									String path = contentValues
											.getAsString(MilPatrolAnnexesTable.field_path);
									File file = new File(path);
									if (file.exists())
										file.delete();
								}
							}
					
					
							DBHelper.getDbHelper(context).delete(
									MilPatrolTable.name,
									MilPatrolTable.field_id + "=?",
									new String[] { caseId });
							DBHelper.getDbHelper(context).delete(
									MineralHcTable.name,
									MineralHcTable.field_caseId + "=?",
									new String[] { caseId });
							DBHelper.getDbHelper(context).delete(
									MilPatrolAnnexesTable.name,
									MilPatrolAnnexesTable.field_tagId + "=?",
									new String[] { caseId });
							DBHelper.getDbHelper(context).delete(
									WebMinPatrolTable.name,
									WebMinPatrolTable.field_id + "=?",
									new String[] { caseId });
					
							
							succeed = true;
							msg = "核查处理发送成功";
						} else {
							succeed = false;
							msg = "核查处理发送失败："+backJson.getString("msg");
							//msg = "核查处理发送失败";
						}
					} else {
						succeed = false;
						msg = "核查处理发送失败";
					}
				} catch (Exception e) {
					e.printStackTrace();
					succeed = false;
					msg = "发送核查处理时发生异常";
				}
				return succeed;
			}

			private String postAnnex(String path, String url) throws Exception {
				try {
					File file = new File(path);
					String fileName = file.getName();
					String prefix = fileName.substring(fileName.lastIndexOf("."));
					String mime = MIMEMapTable.getInstance().getMIMEType(prefix);
					JSONObject attriJson = new JSONObject();
					attriJson.put("type", "kchclj");
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
							.toString() : new StringBuffer(appUri).append("/")
							.append(context.getString(R.string.uri_annexupload_service))
							.toString();

					ArrayList<String> annexes = mineralhcEditView.getAnnexes();
					if (annexes != null && annexes.size() > 0) {
						for (String s : annexes) {
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
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.dismiss();
				}
				GravityCenterToast.makeText(context, msg, Toast.LENGTH_SHORT)
						.show();

				if (result) {
					getActivity().onBackPressed();
				}
			}
		}
	
	class MyPageListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			int x = moveX * 2 + width; // 从第一个到第二个view，粗的下划线的偏移量
			Log.v("index的值为:", index + "");
			Log.v("arg0的值为:", arg0 + "");
			Animation animation = new TranslateAnimation(x * index, x * arg0,
					0, 0);
			index = arg0;

			animation.setFillAfter(true); // 设置动画停止在结束位置
			animation.setDuration(300); // 设置动画时间
			image.startAnimation(animation); // 启动动画
		}
	}
	
	
	
	class MineralHcSaveTask extends AsyncTask<Void, Boolean, Boolean> 
	{

		String msg = "";

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_saveing));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (mineralhcEditView != null)
				mineralhcEditView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			boolean patrolSaved = mineralhcEditView.savePatrol();
			if (patrolSaved)
				msg = "处理结果保存成功";
			else
				msg = "处理结果保存失败";

			publishProgress(patrolSaved);

			return null;
		}

		@Override
		protected void onProgressUpdate(Boolean... values) {
			// TODO Auto-generated method stub
			if (msg != null && !msg.equals("")) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
		}
	}



	class MineralHcPagerAdapter extends PagerAdapter {
		List<View> viewList;
		List<String> viewTitle;

		public MineralHcPagerAdapter(List<View> viewList,
				List<String> viewTitle) {
			this.viewList = viewList;
			this.viewTitle = viewTitle;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			return viewList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return viewTitle.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	
}

