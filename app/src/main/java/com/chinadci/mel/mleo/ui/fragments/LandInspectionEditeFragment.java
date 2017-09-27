/*
 * 土地执法》案件核查（快速处置）编辑
 */
package com.chinadci.mel.mleo.ui.fragments;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.chinadci.mel.android.ui.views.CircleProgressBusyView;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import com.chinadci.mel.mleo.core.Parameters;
import com.chinadci.mel.mleo.ldb.CaseAnnexesTable;
import com.chinadci.mel.mleo.ldb.CaseInspectTable;
import com.chinadci.mel.mleo.ldb.CasePatrolTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.InspectionCaseTable;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.activities.PolyGatherActivity;
import com.chinadci.mel.mleo.ui.activities.TapeActivity;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.ui.views.CaseView;
import com.chinadci.mel.mleo.ui.views.InspectionEditeView;
import com.chinadci.mel.mleo.ui.views.InspectionView;
import com.chinadci.mel.mleo.ui.views.PatrolEditeView;

/**
 * 
 * @ClassName LandInspectionEditeFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:47:03
 * 
 */
@SuppressLint("SimpleDateFormat")
public class LandInspectionEditeFragment extends LandCaseViewpagerFragment {

	String caseId;

	CaseView caseInfoView;
	InspectionView inspectionView;
	InspectionEditeView inspectionEditeView;
	PatrolEditeView patrolEditeView;

	AlertDialog alertDialog;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		// photo
		case Parameters.GET_PHOTO:
			Bundle photoBundle = data.getExtras();
			String photos[] = photoBundle
					.getStringArray(CameraPhotoActivity.PHOTOARRAY);
			patrolEditeView.addPhotoPaths(photos);
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
				patrolEditeView.addPhotoPaths(photoss);
			} catch (Exception e) {
			}
			break;
		// audio
		case Parameters.GET_AUDIO:
			Bundle audioBundle = data.getExtras();
			String amrPath = audioBundle.getString(TapeActivity.AMRFILE);
			patrolEditeView.addAudioPath(amrPath);
			break;
		// redline
		case Parameters.GET_REDLINE:
			Bundle redlineBundle = data.getExtras();
			String redlineString = redlineBundle.getString(
					PolyGatherActivity.REDLINE).toString();
			patrolEditeView.setRedline(redlineString);
			break;
		default:
			break;
		}
	}

	@Override
	public void handle(Object o) {
		// TODO Auto-generated method stub
		super.handle(o);
		int tag = (Integer) o;
		if (tag == 0) {
			new inspectionSaveTask(false).execute();
		} else if (tag == 1) {
			String patrolCheckMsg = patrolEditeView.checkInput();
			if (patrolCheckMsg == null) {
				if (inspectionEditeView != null) {
					String inspectionCheckMsg = inspectionEditeView
							.checkInput();
					if (inspectionCheckMsg == null) {
						new inspectionSaveTask(true).execute();
						new inspectionSendTask2().execute();
					} else {
						Toast.makeText(context, inspectionCheckMsg,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					new inspectionSaveTask(true).execute();
					new inspectionSendTask2().execute();
				}
			} else {
				Toast.makeText(context, patrolCheckMsg, Toast.LENGTH_SHORT)
						.show();
			}
		} else if (tag == 2) {
			if (LocationUtils.isGPSSupport(context)) {// GPS模块已开启
				String uri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				uri = appUri.endsWith("/") ? new StringBuffer(appUri).append(
						context.getString(R.string.uri_response)).toString()
						: new StringBuffer(appUri)
								.append("/")
								.append(context
										.getString(R.string.uri_response))
								.toString();
				new ResponseTask(context).execute(currentUser, caseId, uri);
			} else {
				Toast.makeText(context, "请开启设备GPS", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		super.refreshUi();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		caseId = getArguments().getString(Parameters.CASE_ID);
		contentView = inflater.inflate(R.layout.fragment_case_viewpager,
				container, false);
		initViewpager();
		initFragment();
		return contentView;
	}

	void initFragment() {
		Log.i("ydzf", "LandInspectionEditeFragment_initFragment");
		try {

			caseInfoView = new CaseView(context);
			caseInfoView.setParentActivity(getActivity());
			caseInfoView.setDataSource(caseId, InspectionCaseTable.name,
					CaseAnnexesTable.name);

			patrolEditeView = new PatrolEditeView(context);
			patrolEditeView.setParentActivity(getActivity());
			patrolEditeView.setDataSource(currentUser, caseId,
					CasePatrolTable.name);

			ContentValues inspectionValues = DBHelper.getDbHelper(context)
					.doQuery(
							CaseInspectTable.name,
							new String[] { CaseInspectTable.field_caseId,
									CaseInspectTable.field_status },
							CaseInspectTable.field_caseId + "=?",
							new String[] { caseId });

			if (inspectionValues != null) {
				int inspectionStatus = inspectionValues
						.getAsInteger(CaseInspectTable.field_status);
				if (inspectionStatus == 2) {
					inspectionView = new InspectionView(context);
					inspectionView.setDataSource(inspectionValues
							.getAsString(CaseInspectTable.field_caseId),
							CaseInspectTable.name);
					viewList.add(inspectionView);

				} else {
					inspectionEditeView = new InspectionEditeView(context,
							getActivity());
					inspectionEditeView.setDataSource(currentUser, caseId,
							CaseInspectTable.name);
					viewList.add(inspectionEditeView);

				}
			} else {
				inspectionEditeView = new InspectionEditeView(context,
						getActivity());
				inspectionEditeView.setDataSource(currentUser, caseId,
						CaseInspectTable.name);
				viewList.add(inspectionEditeView);

			}
			viewList.add(patrolEditeView);

			viewList.add(caseInfoView);

			pagerAdapter = new ViewPagerAdapter(viewList, titleList);
			viewPager.setAdapter(pagerAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @ClassName ResponseTask
	 * @Description 开启响应
	 * @author leix@geo-k.cn
	 * @date 2014年7月29日 上午10:33:07
	 * 
	 */
	class ResponseTask extends AsyncTask<String, Integer, Boolean> {
		boolean succeed = false;
		String msg;
		Location location;
		Context rContext;
		String user;
		String caseId;
		String uri;

		public ResponseTask(Context context) {
			rContext = context;
		}

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg("正在提交响应...");
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			location = LocationUtils.getCurrentLocation2(getActivity());
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				user = params[0];
				caseId = params[1];
				uri = params[2];
				Thread.sleep(400);
				if (location != null) {
					JSONObject object = new JSONObject();
					object.put("user", user);
					object.put("caseId", caseId);
					object.put("x", location.getLongitude());
					object.put("y", location.getLatitude());
					object.put("accuracy", location.getAccuracy());
					Log.i("uri", uri);
					HttpResponse response = HttpUtils.httpClientExcutePost(uri,
							object);
					if (response.getStatusLine().getStatusCode() == 200) {
						String entity = EntityUtils.toString(response
								.getEntity());
						Log.i("entity", entity);
						JSONObject backJson = new JSONObject(entity);
						msg = backJson.getString("msg");
						if (backJson.getBoolean("succeed")) {
							succeed = backJson.getBoolean("succeed");
						}
					}
				} else {
					msg = "定位失败";
					succeed = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}

			if (msg == null) {
				if (result)
					msg = "响应成功";
				else
					msg = "响应失败";
			}
			Toast.makeText(rContext, msg, Toast.LENGTH_SHORT).show();
		}
	}

	// save inspection in background thread
	class inspectionSaveTask extends AsyncTask<Void, Boolean, Boolean> {
		String msg = "";
		boolean slient;

		public inspectionSaveTask(boolean slient) {
			this.slient = slient;
		}

		@Override
		protected void onPreExecute() {
			if (slient)
				return;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_saveing));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (inspectionEditeView != null)
				inspectionEditeView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			boolean patrolSaved = patrolEditeView.savePatrol();
			if (patrolSaved)
				msg = "处理结果保存成功";
			else
				msg = "处理结果保存失败";

			publishProgress(patrolSaved);

			if (inspectionEditeView != null) {
				boolean inspectionSaved = inspectionEditeView.saveInspection();
				if (inspectionSaved)
					msg = "信息核查保存成功";
				else
					msg = "信息核查保存失败";
				publishProgress(inspectionSaved);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Boolean... values) {
			if (slient)
				return;
			if (msg != null && !msg.equals("")) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (slient)
				return;
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
		}
	}

	// send inspection in background thread
	class inspectionSendTask2 extends AsyncTask<Void, Void, Boolean> {
		String msg = "";

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (inspectionEditeView != null)
				inspectionEditeView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				// patrolEditeView.savePatrol();
				String inspectionUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				inspectionUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_casecheck_service))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_casecheck_service))
						.toString();
						
				//判断是否有照片
				ArrayList<String> annexTem = patrolEditeView.getAnnexes();
				if(annexTem.size()==0)
				{
					succeed = false;
					msg = "请至少上传一张照片！";
					return succeed;
				}
						
				JSONArray annexArray = sendAnnexes();
				String patrolId = patrolEditeView.getPatrolId();

				JSONObject patrolJson = patrolEditeView.getPatrolJson();
				patrolJson.put("annexes", annexArray);
				JSONObject resJson = new JSONObject();
				resJson.put("patrol", patrolJson);
				if (inspectionEditeView != null) {
					inspectionEditeView.saveInspection();
					JSONObject inspectionJson = inspectionEditeView
							.getInspectionJson();
					resJson.put("inspection", inspectionJson);
				} else {
					resJson.put("inspection", JSONObject.NULL);
				}

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(inspectionUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(resJson.toString(), HTTP.UTF_8));
				Log.i("post-entity", resJson.toString());
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {

						ArrayList<String> annexes = patrolEditeView
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
										CaseAnnexesTable.name,
										new String[] { CaseAnnexesTable.field_path },
										new StringBuffer(
												CaseAnnexesTable.field_caseId)
												.append("=?").toString(),
										new String[] { caseId }, null, null,
										null, null);
						if (annexValues != null && annexValues.size() > 0) {
							for (ContentValues contentValues : annexValues) {
								String path = contentValues
										.getAsString(CaseAnnexesTable.field_path);
								File file = new File(path);
								if (file.exists())
									file.delete();
							}
						}

						DBHelper.getDbHelper(context).delete(
								InspectionCaseTable.name,
								InspectionCaseTable.field_id + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseAnnexesTable.name,
								CaseAnnexesTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CasePatrolTable.name,
								CasePatrolTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseInspectTable.name,
								CaseInspectTable.field_id + "=?",
								new String[] { caseId });
						succeed = true;
						msg = "核查处理发送成功";
					} else {
						succeed = false;
						msg = backJson.getString("msg");
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

		private String postAnnex(String path, String url) throws Exception {
			try {
				File file = new File(path);
				String fileName = file.getName();
				String prefix = fileName.substring(fileName.lastIndexOf("."));
				String mime = MIMEMapTable.getInstance().getMIMEType(prefix);
				JSONObject attriJson = new JSONObject();
				attriJson.put("type", "inspection");
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

				ArrayList<String> annexes = patrolEditeView.getAnnexes();
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
	}

	// send inspection in background thread
	class inspectionSendTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";

		@Override
		protected void onPreExecute() {
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			if (inspectionEditeView != null)
				inspectionEditeView.getTextViewValues();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {
				// patrolEditeView.savePatrol();
				String inspectionUri = "";
				String annexUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				inspectionUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context
								.getString(R.string.uri_inspection_service))
						.toString() : new StringBuffer(appUri)
						.append("/")
						.append(context
								.getString(R.string.uri_inspection_service))
						.toString();
				annexUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_annex_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_annex_service))
						.toString();

				String patrolId = patrolEditeView.getPatrolId();

				JSONObject patrolJson = patrolEditeView.getPatrolJson();
				JSONObject resJson = new JSONObject();
				resJson.put("patrol", patrolJson);
				if (inspectionEditeView != null) {
					inspectionEditeView.saveInspection();
					JSONObject inspectionJson = inspectionEditeView
							.getInspectionJson();
					resJson.put("inspection", inspectionJson);
				} else {
					resJson.put("inspection", JSONObject.NULL);
				}

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(inspectionUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(resJson.toString(), HTTP.UTF_8));
				Log.i("post-entity", resJson.toString());
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					JSONObject backJson = new JSONObject(entityString);
					if (backJson.getBoolean("succeed")) {
						String serverPatrolId = backJson
								.getString("inspectionId");
						ArrayList<String> annexes = patrolEditeView
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
										CaseAnnexesTable.name,
										new String[] { CaseAnnexesTable.field_path },
										new StringBuffer(
												CaseAnnexesTable.field_caseId)
												.append("=?").toString(),
										new String[] { caseId }, null, null,
										null, null);
						if (annexValues != null && annexValues.size() > 0) {
							for (ContentValues contentValues : annexValues) {
								String path = contentValues
										.getAsString(CaseAnnexesTable.field_path);
								File file = new File(path);
								if (file.exists())
									file.delete();
							}
						}

						DBHelper.getDbHelper(context).delete(
								InspectionCaseTable.name,
								InspectionCaseTable.field_id + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseAnnexesTable.name,
								CaseAnnexesTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CasePatrolTable.name,
								CasePatrolTable.field_caseId + "=?",
								new String[] { caseId });
						DBHelper.getDbHelper(context).delete(
								CaseInspectTable.name,
								CaseInspectTable.field_id + "=?",
								new String[] { caseId });
						succeed = true;
						msg = "核查处理发送成功";
					} else {
						succeed = false;
						msg = backJson.getString("msg");
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
}
