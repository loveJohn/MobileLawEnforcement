package com.chinadci.mel.mleo.ui.fragments;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.library.http.entity.MultipartEntity;
import com.andbase.library.http.entity.mine.content.ContentBody;
import com.andbase.library.http.entity.mine.content.FileBody;
import com.andbase.library.http.entity.mine.content.StringBody;
import com.chinadci.android.media.MIMEMapTable;
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
import com.chinadci.mel.mleo.ldb.AnnexTable;
import com.chinadci.mel.mleo.ldb.ClueSourceTable;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.IllegalStatusTable;
import com.chinadci.mel.mleo.ldb.IllegalSubjectTable;
import com.chinadci.mel.mleo.ldb.IllegalTypeTable;
import com.chinadci.mel.mleo.ldb.LandUsageTable;
import com.chinadci.mel.mleo.ldb.LocalAnnexTable;
import com.chinadci.mel.mleo.ldb.LocalCaseTable;
import com.chinadci.mel.mleo.ldb.ProjTypeTable;
import com.chinadci.mel.mleo.ui.activities.AdminChooserActivity;
import com.chinadci.mel.mleo.ui.activities.CameraPhotoActivity;
import com.chinadci.mel.mleo.ui.activities.PolyGatherActivity;
import com.chinadci.mel.mleo.ui.activities.TapeActivity;
import com.chinadci.mel.mleo.ui.adapters.AudioGridAdapter;
import com.chinadci.mel.mleo.ui.adapters.ImageGridAdapter;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.chinadci.mel.mleo.utils.VibratorUtil;

/**
 * 
 * @ClassName LandCaseEditeFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:46:38
 * 
 */
@SuppressLint("SimpleDateFormat")
public class LandCaseEditeFragment extends ContentFragment {
	View contentView;
	GridView photoGrid;
	GridView tapeGrid;
	GridView vedioGrid;

	LinearLayout photoLayout;
	LinearLayout tapeLayout;
	LinearLayout vedioLayout;
	DropDownSpinner illegalSubjectSpinner;
	DropDownSpinner sourceSpinner;
	DropDownSpinner landUsageSpinner;
	DropDownSpinner illegalStatusSpinner;
	DropDownSpinner illegalTypeSpinner;
	DropDownSpinner projTypeSpinner;
	EditText notesView;
	EditText partiesView;
	EditText addressView;
	EditText illegalAreaView;
	TextView redlineView;
	TextView adminView;
	TextView unitView;
	ImageButton photoButton;
	ImageButton audioButton;

	String keySource = "16";
	String keyIllegalType;
	String keyIllegalStatus;
	String keyProjType;
	String keyLandUsage;
	String keyIllegalSubject;

	String caseId;
	String textSource = "16";
	String textIllegalType;
	String textIllegalStatus;
	String textProjType;
	String textLandUsage;

	String adminCode = "350000";
	String adminName = "福建省";
	String parties = "";
	String address = "";
	String notes = "";
	String redlineJson = "";
	Double x, y;
	Double illegalArea;

	AlertDialog alertDialog;
	Thread minorThread;
	ImageGridAdapter imgGridAdapter;
	AudioGridAdapter tapeGridAdapter;
	Animation scaleInAnim;
	DecimalFormat kmFormat = new DecimalFormat("#.####");
	boolean isCurrentUnitSqm = true;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Parameters.GET_REDLINE) {
			if (resultCode != Activity.RESULT_OK)
				return;

			Bundle bundle = data.getExtras();
			double area = bundle.getDouble(PolyGatherActivity.AREA);
			redlineJson = bundle.getString(PolyGatherActivity.REDLINE)
					.toString();

			if (redlineJson != null && redlineJson.length() > 0) {
				redlineView.setSelected(true);
				redlineView.setText(getString(R.string.cn_redlinedisplay));
				if (area > 0) {
					illegalArea = area;
					if (isCurrentUnitSqm) {// 当前单位为平方米
						illegalAreaView.setText(kmFormat.format(illegalArea));
					} else {
						double numb = sqm2Mu(illegalArea);
						illegalAreaView.setText(kmFormat.format(numb));
					}
				}
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
					imgGridAdapter.addItem(photos[j]);
				imgGridAdapter.notifyDataSetChanged();
				photoLayout.setVisibility(View.VISIBLE);
			}
		}else if(requestCode == Parameters.PICK_IMAGE){
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
				imgGridAdapter.addItem(path);
			} catch (Exception e) {
			}
		} else if (requestCode == Parameters.GET_AUDIO) {
			Bundle bundle = data.getExtras();
			String amrPath = bundle.getString(TapeActivity.AMRFILE);
			tapeGridAdapter.addItem(amrPath);
			tapeGridAdapter.notifyDataSetChanged();
			tapeLayout.setVisibility(View.VISIBLE);
		} else if (requestCode == Parameters.GET_ADMIN) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				adminCode = bundle.getString(AdminChooserActivity.ADMIN_CODE);
				adminName = DBHelper.getDbHelper(context).queryAdminFullName(
						adminCode);
				adminView.setText(adminName);
			}
		}
	}

	@Override
	public void handle(Object o) {
		super.handle(o);
		int tag = (Integer) o;
		if (tag == 0) {
			// 保存案件信息
			new caseSaveTask(context, false).execute();

		} else if (tag == 1) {
			if (redlineJson == null || redlineJson.equals("")) {
				Toast.makeText(context, "请绘制红线", Toast.LENGTH_SHORT).show();
				return;
			}

			if (imgGridAdapter.getCount() < 1) {
				Toast.makeText(context, "请拍摄照片", Toast.LENGTH_SHORT).show();
				return;
			}
			parties = partiesView.getText().toString();
			// 静默保存案件信息
			new caseSaveTask(context, true).execute();
			new caseSendTask2(context).execute();
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
		caseId = getArguments().getString(Parameters.CASE_ID);
		contentView = inflater.inflate(R.layout.fragment_case_edite, container,
				false);

		imgGridAdapter = new ImageGridAdapter(context, new ArrayList<String>(),
				photoClickListener, R.layout.view_photo, R.id.view_photo_photo,
				R.id.view_photo_delete);

		tapeGridAdapter = new AudioGridAdapter(context,
				new ArrayList<String>(), tapeClickListener,
				R.layout.view_audio, R.id.view_audio_index,
				R.id.view_audio_length, R.id.view_audio_delete);

		photoGrid = (GridView) contentView
				.findViewById(R.id.view_media_photogrid);
		tapeGrid = (GridView) contentView
				.findViewById(R.id.view_media_audiolist);
		vedioGrid = (GridView) contentView
				.findViewById(R.id.view_media_vediogrid);

		photoLayout = (LinearLayout) contentView
				.findViewById(R.id.view_media_photolayout);
		tapeLayout = (LinearLayout) contentView
				.findViewById(R.id.view_media_audiolayout);
		vedioLayout = (LinearLayout) contentView
				.findViewById(R.id.view_media_vediolayout);

		photoButton = (ImageButton) contentView
				.findViewById(R.id.fragment_case_edite_camera);
		audioButton = (ImageButton) contentView
				.findViewById(R.id.fragment_case_edite_tape);
		redlineView = (TextView) contentView
				.findViewById(R.id.fragment_case_edite_redline);
		adminView = (TextView) contentView
				.findViewById(R.id.fragment_case_edite_admin);

		sourceSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_case_edite_source);
		projTypeSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_case_edite_projtype);
		illegalStatusSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_case_edite_illegalstatus);
		illegalTypeSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_case_edite_illegaltype);
		landUsageSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_case_edite_landusage);
		illegalSubjectSpinner = (DropDownSpinner) contentView
				.findViewById(R.id.fragment_case_edite_illegalsubj);
		addressView = (EditText) contentView
				.findViewById(R.id.fragment_case_edite_address);
		partiesView = (EditText) contentView
				.findViewById(R.id.fragment_case_edite_parties);
		illegalAreaView = (EditText) contentView
				.findViewById(R.id.fragment_case_edite_illegalarea);
		notesView = (EditText) contentView
				.findViewById(R.id.fragment_case_edite_notes);
		unitView = (TextView) contentView
				.findViewById(R.id.fragment_case_edite_unit);
		adminView.setText(adminName);
		photoGrid.setAdapter(imgGridAdapter);
		tapeGrid.setAdapter(tapeGridAdapter);

		sourceSpinner
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		projTypeSpinner
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		landUsageSpinner
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		illegalStatusSpinner
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		illegalTypeSpinner
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		illegalSubjectSpinner
				.setSelectedChangedListener(spinnerSelectedChangedListener);
		try {
			ArrayList<KeyValue> clueSource = DBHelper.getDbHelper(context)
					.getParameters(ClueSourceTable.name);
			ArrayList<KeyValue> projType = DBHelper.getDbHelper(context)
					.getParameters(ProjTypeTable.name);
			ArrayList<KeyValue> illegalStatus = DBHelper.getDbHelper(context)
					.getParameters(IllegalStatusTable.name);
			ArrayList<KeyValue> illegalType = DBHelper.getDbHelper(context)
					.getParameters(IllegalTypeTable.name);
			ArrayList<KeyValue> landUsage = DBHelper.getDbHelper(context)
					.getParameters(LandUsageTable.name);
			ArrayList<KeyValue> illegalSubject = DBHelper.getDbHelper(context)
					.getParameters(IllegalSubjectTable.name);

			projTypeSpinner.setData(projType);
			landUsageSpinner.setData(landUsage);
			illegalStatusSpinner.setData(illegalStatus);
			illegalTypeSpinner.setData(illegalType);
			sourceSpinner.setData(clueSource);
			illegalSubjectSpinner.setData(illegalSubject);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (caseId != null && !caseId.equals("")) {
			loadCaseInfo(caseId);
		} else {
			caseId = new StringBuffer("XC").append(
					TimeFormatFactory2.getDisplayTime(new Date(),
							"yyyyMMdd_hhmmss")).toString();
		}
		redlineView.setOnClickListener(clickListener);
		photoButton.setOnClickListener(clickListener);
		audioButton.setOnClickListener(clickListener);
		adminView.setOnClickListener(clickListener);
		unitView.setOnClickListener(clickListener);
		return contentView;
	}

	ISelectedChanged spinnerSelectedChangedListener = new ISelectedChanged() {

		public Object onSelectedChanged(View v, Object o) {
			int vid = v.getId();
			switch (vid) {
			case R.id.fragment_case_edite_illegalstatus:
				keyIllegalStatus = o.toString();
				break;

			case R.id.fragment_case_edite_illegaltype:
				keyIllegalType = o.toString();
				break;
			case R.id.fragment_case_edite_landusage:
				keyLandUsage = o.toString();
				break;
			case R.id.fragment_case_edite_projtype:
				keyProjType = o.toString();
				break;

			case R.id.fragment_case_edite_source:
				keySource = o.toString();
				break;

			case R.id.fragment_case_edite_illegalsubj:
				keyIllegalSubject = o.toString();
				break;

			default:
				break;
			}
			return null;
		}
	};

	void loadCaseInfo(String id) {
		try {
			String columns[] = new String[] { LocalCaseTable.field_address,
					LocalCaseTable.field_source, LocalCaseTable.field_admin,
					LocalCaseTable.field_id,
					LocalCaseTable.field_illegalSubject,
					LocalCaseTable.field_illegalArea,
					LocalCaseTable.field_illegalStatus,
					LocalCaseTable.field_illegalType,
					LocalCaseTable.field_landUsage, LocalCaseTable.field_notes,
					LocalCaseTable.field_parties, LocalCaseTable.field_redline,
					LocalCaseTable.field_projType, LocalCaseTable.field_user,
					LocalCaseTable.field_x, LocalCaseTable.field_y };
			String selection = new StringBuffer(LocalCaseTable.field_id)
					.append("=?").toString();
			String args[] = new String[] { id };
			ContentValues caseValues = DBHelper.getDbHelper(context).doQuery(
					LocalCaseTable.name, columns, selection, args);

			sourceSpinner.setSelectedItem(caseValues
					.getAsString(LocalCaseTable.field_source));
			projTypeSpinner.setSelectedItem(caseValues
					.getAsString(LocalCaseTable.field_projType));
			landUsageSpinner.setSelectedItem(caseValues
					.getAsString(LocalCaseTable.field_landUsage));
			illegalStatusSpinner.setSelectedItem(caseValues
					.getAsString(LocalCaseTable.field_illegalStatus));
			illegalTypeSpinner.setSelectedItem(caseValues
					.getAsString(LocalCaseTable.field_illegalType));
			illegalSubjectSpinner.setSelectedItem(caseValues
					.getAsString(LocalCaseTable.field_illegalSubject));

			try {
				adminCode = caseValues.getAsString(LocalCaseTable.field_admin);
				adminName = DBHelper.getDbHelper(context).queryAdminFullName(
						adminCode);
			} catch (Exception e) {
				// TODO: handle exception
			}

			address = caseValues.getAsString(LocalCaseTable.field_address);
			notes = caseValues.getAsString(LocalCaseTable.field_notes);
			illegalArea = caseValues
					.getAsDouble(LocalCaseTable.field_illegalArea);
			parties = caseValues.getAsString(LocalCaseTable.field_parties);
			x = caseValues.getAsDouble(LocalCaseTable.field_x);
			y = caseValues.getAsDouble(LocalCaseTable.field_y);
			redlineJson = caseValues.getAsString(LocalCaseTable.field_redline);

			partiesView.setText(parties);
			addressView.setText(address);
			if (illegalArea > 0)
				illegalAreaView.setText(kmFormat.format(illegalArea));
			notesView.setText(notes);

			ArrayList<ContentValues> annexes = DBHelper.getDbHelper(context)
					.doQuery(
							LocalAnnexTable.name,
							new String[] { AnnexTable.field_path },
							new StringBuffer(AnnexTable.field_tagId)
									.append("=? and ")
									.append(AnnexTable.field_tag).append("=?")
									.toString(),
							new String[] { caseId, LocalCaseTable.name }, null,
							null, null, null);

			if (annexes != null && annexes.size() > 0) {
				for (int i = 0; i < annexes.size(); i++) {
					String path = annexes.get(i).getAsString(
							AnnexTable.field_path);
					String prefix = path.substring(path.lastIndexOf("."));
					String mtype = MIMEMapTable.getInstance()
							.getParentMIMEType(prefix);
					File f = new File(path);
					if (f.exists()) {
						if (mtype.equalsIgnoreCase("image/*")) {
							if (photoLayout.getVisibility() != View.VISIBLE)
								photoLayout.setVisibility(View.VISIBLE);
							imgGridAdapter.addItem(path);
						} else if (mtype.equalsIgnoreCase("audio/*")) {
							if (tapeLayout.getVisibility() != View.VISIBLE)
								tapeLayout.setVisibility(View.VISIBLE);
							tapeGridAdapter.addItem(path);
						}
					}
				}
				imgGridAdapter.notifyDataSetChanged();
				tapeGridAdapter.notifyDataSetChanged();
			}

			if (redlineJson != null && redlineJson.length() > 0) {
				redlineView.setSelected(true);
				redlineView.setText(getString(R.string.cn_redlinedisplay));
			} else {
				redlineView.setSelected(false);
				redlineView.setText(getString(R.string.cn_redlinegather));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class caseSendTask2 extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		CircleProgressBusyView abv;

		public caseSendTask2(Context c) {
			this.context = c;
		}

		@Override
		protected void onPreExecute() {
			abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			address = addressView.getText().toString();
			notes = notesView.getText().toString();
			parties = partiesView.getText().toString();
			String areaText = illegalAreaView.getText().toString();
			if (areaText != null && !"".equals(areaText)) {
				if (isCurrentUnitSqm)
					illegalArea = Double.parseDouble(areaText);
				else
					illegalArea = mu2Sqm(Double.parseDouble(areaText));
			} else {
				illegalArea = 0d;
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			try {
				Location location = LocationUtils
						.getCurrentLocation2(getActivity());
				if (location != null) {
					x = location.getLongitude();
					y = location.getLatitude();
				} else {
					x = null;
					y = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String postAnnex(String path, String url) throws Exception {
			try {
				File file = new File(path);
				String fileName = file.getName();
				String prefix = fileName.substring(fileName.lastIndexOf("."));
				String mime = MIMEMapTable.getInstance().getMIMEType(prefix);
				JSONObject attriJson = new JSONObject();
				attriJson.put("type", "case");
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

				ArrayList<String> imagePahts = imgGridAdapter.getPaths();
				ArrayList<String> tapePaths = tapeGridAdapter.getPaths();
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

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String caseUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				caseUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_casesb_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_casesb_service))
						.toString();

				/**
				 * 上传附件,并返回附件的编号数组
				 */
				JSONArray annexArray = sendAnnexes();

				JSONObject caseJsonObject = new JSONObject();
				caseJsonObject.put("user", currentUser);
				Thread.sleep(500);
				publishProgress();
				if (x != null && y != null) {
					JSONObject locationJson = new JSONObject();
					locationJson.put("x", String.valueOf(x));
					locationJson.put("y", String.valueOf(y));
					JSONObject wkid = new JSONObject();
					wkid.put("wkid", 4326);
					locationJson.put("spatialReference", wkid);
					caseJsonObject.put("location", locationJson);
				} else {
					caseJsonObject.put("location", JSONObject.NULL);
				}

				if (redlineJson != null && !redlineJson.equals("")) {
					JSONObject redlineObject = new JSONObject(redlineJson);
					caseJsonObject.put("redline", redlineObject);
				} else {
					caseJsonObject.put("redline", JSONObject.NULL);
				}

				if (parties == null)
					parties = "";

				if (adminCode == null)
					adminCode = "";

				if (notes == null)
					notes = "";

				caseJsonObject.put("parties", parties);
				caseJsonObject.put("illegalSubject", keyIllegalSubject);
				caseJsonObject.put("admin", adminCode);
				caseJsonObject.put("address", address);
				caseJsonObject.put("illegalType", keyIllegalType);
				caseJsonObject.put("illegalStatus", keyIllegalStatus);
				caseJsonObject.put("landUsage", keyLandUsage);
				caseJsonObject.put("source", keySource);
				caseJsonObject.put("urgency", keyProjType);// 项目类型
				caseJsonObject.put("projType", keyProjType);
				caseJsonObject.put("source", keySource);
				caseJsonObject.put("notes", notes);
				caseJsonObject.put("annexes", annexArray);// 附件编号数组

				if (illegalArea > 0)
					caseJsonObject.put("illegalArea", illegalArea);
				else
					caseJsonObject.put("illegalArea", JSONObject.NULL);

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(caseUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(caseJsonObject.toString(),
						HTTP.UTF_8));
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					Log.i("entity", entityString);
					JSONObject caseBackJson = new JSONObject(entityString);

					if (caseBackJson.getBoolean("succeed")) {
						String serverCaseId = caseBackJson.getString("caseId");
						Log.i("caseId", serverCaseId);
						ArrayList<String> imagePahts = imgGridAdapter
								.getPaths();
						ArrayList<String> tapePaths = tapeGridAdapter
								.getPaths();

						if (imagePahts != null && imagePahts.size() > 0) {
							for (String s : imagePahts) {
								File file = new File(s);
								file.delete();
							}
						}

						if (tapePaths != null && tapePaths.size() > 0) {
							for (String s : tapePaths) {
								File file = new File(s);
								file.delete();
							}
						}

						// 删除附件记录
						DBHelper.getDbHelper(context).delete(
								LocalAnnexTable.name,
								new StringBuffer(AnnexTable.field_tagId)
										.append("=? and ")
										.append(AnnexTable.field_tag)
										.append("=?").toString(),
								new String[] { caseId, LocalCaseTable.name });

						// 删除案件记录
						DBHelper.getDbHelper(context).delete(
								LocalCaseTable.name,
								LocalCaseTable.field_id + "=?",
								new String[] { caseId });
						return true;
					} else {
						msg = caseBackJson.getString("msg");
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
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (result) {
				GravityCenterToast
						.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
				getActivity().onBackPressed();
			} else {
				GravityCenterToast.makeText(context, "发送失败，" + msg,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	class caseSendTask extends AsyncTask<Void, Void, Boolean> {
		String msg = "";
		Context context;
		CircleProgressBusyView abv;

		public caseSendTask(Context c) {
			this.context = c;
		}

		@Override
		protected void onPreExecute() {
			abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_sendding));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);
			address = addressView.getText().toString();
			notes = notesView.getText().toString();
			parties = partiesView.getText().toString();
			String areaText = illegalAreaView.getText().toString();
			if (areaText != null && !"".equals(areaText)) {
				if (isCurrentUnitSqm)
					illegalArea = Double.parseDouble(areaText);
				else
					illegalArea = mu2Sqm(Double.parseDouble(areaText));
			} else {
				illegalArea = 0d;
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			try {
				Location location = LocationUtils
						.getCurrentLocation2(getActivity());
				if (location != null) {
					x = location.getLongitude();
					y = location.getLatitude();
				} else {
					x = null;
					y = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String caseUri = "";
				String annexUri = "";
				String appUri = SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).getSharedPreferences(
						R.string.sp_appuri, "");
				caseUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_case_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_case_service))
						.toString();
				annexUri = appUri.endsWith("/") ? new StringBuffer(appUri)
						.append(context.getString(R.string.uri_annex_service))
						.toString() : new StringBuffer(appUri).append("/")
						.append(context.getString(R.string.uri_annex_service))
						.toString();

				JSONObject caseJsonObject = new JSONObject();
				caseJsonObject.put("user", currentUser);
				Thread.sleep(500);
				publishProgress();
				if (x != null && y != null) {
					JSONObject locationJson = new JSONObject();
					locationJson.put("x", String.valueOf(x));
					locationJson.put("y", String.valueOf(y));
					JSONObject wkid = new JSONObject();
					wkid.put("wkid", 4326);
					locationJson.put("spatialReference", wkid);
					caseJsonObject.put("location", locationJson);
				} else {
					// msg = "没有获取到案件当前位置!";
					// return false;
					caseJsonObject.put("location", JSONObject.NULL);
				}

				if (redlineJson != null && !redlineJson.equals("")) {
					JSONObject redlineObject = new JSONObject(redlineJson);
					caseJsonObject.put("redline", redlineObject);
				} else {
					caseJsonObject.put("redline", JSONObject.NULL);
				}

				if (parties == null)
					parties = "";

				if (adminCode == null)
					adminCode = "";

				if (notes == null)
					notes = "";

				caseJsonObject.put("parties", parties);
				caseJsonObject.put("illegalSubject", keyIllegalSubject);
				caseJsonObject.put("admin", adminCode);
				caseJsonObject.put("address", address);
				caseJsonObject.put("illegalType", keyIllegalType);
				caseJsonObject.put("illegalStatus", keyIllegalStatus);
				caseJsonObject.put("landUsage", keyLandUsage);
				caseJsonObject.put("source", keySource);
				caseJsonObject.put("urgency", keyProjType);// 项目类型
				caseJsonObject.put("projType", keyProjType);
				caseJsonObject.put("source", keySource);
				caseJsonObject.put("notes", notes);

				if (illegalArea > 0)
					caseJsonObject.put("illegalArea", illegalArea);
				else
					caseJsonObject.put("illegalArea", JSONObject.NULL);

				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost post = new HttpPost(caseUri);
				post.setHeader(HTTP.CONTENT_TYPE, "application/json");
				post.setEntity(new StringEntity(caseJsonObject.toString(),
						HTTP.UTF_8));
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					String entityString = EntityUtils.toString(response
							.getEntity());
					Log.i("entity", entityString);
					JSONObject caseBackJson = new JSONObject(entityString);

					if (caseBackJson.getBoolean("succeed")) {
						String serverCaseId = caseBackJson.getString("caseId");
						Log.i("caseId", serverCaseId);
						ArrayList<String> imagePahts = imgGridAdapter
								.getPaths();
						if (imagePahts != null && imagePahts.size() > 0) {
							for (String s : imagePahts) {
								File file = new File(s);
								String fileName = file.getName();
								String prefix = fileName.substring(fileName
										.lastIndexOf("."));
								String mime = MIMEMapTable.getInstance()
										.getMIMEType(prefix);
								JSONObject attriJson = new JSONObject();
								attriJson.put("type", "case");
								attriJson.put("tagId", serverCaseId);
								attriJson.put("fjlx", mime.substring(0,
										mime.lastIndexOf("/")));
								attriJson.put("name", fileName);
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

						ArrayList<String> tapePaths = tapeGridAdapter
								.getPaths();
						if (tapePaths != null && tapePaths.size() > 0) {
							for (String s : tapePaths) {
								File file = new File(s);
								String fileName = file.getName();
								String prefix = fileName.substring(fileName
										.lastIndexOf("."));

								JSONObject attriJson = new JSONObject();
								attriJson.put("type", "case");
								attriJson.put("tagId", serverCaseId);
								attriJson.put("name", fileName);
								attriJson.put("extention", prefix);

								MultipartEntity postEntity = new MultipartEntity();
								ContentBody cbAttri = new StringBody(
										attriJson.toString(),
										Charset.forName(HTTP.UTF_8));
								String mime = MIMEMapTable.getInstance()
										.getMIMEType(prefix);
								ContentBody cbFileData = new FileBody(file,
										mime);
								postEntity.addPart("attriData", cbAttri);
								postEntity.addPart("fileData", cbFileData);

								HttpPost annexPost = new HttpPost(annexUri);
								annexPost.setEntity(postEntity);
								client.execute(annexPost);
							}
						}

						if (imagePahts != null && imagePahts.size() > 0) {
							for (String s : imagePahts) {
								File file = new File(s);
								file.delete();
							}
						}

						if (tapePaths != null && tapePaths.size() > 0) {
							for (String s : tapePaths) {
								File file = new File(s);
								file.delete();
							}
						}

						// 删除附件记录
						DBHelper.getDbHelper(context).delete(
								LocalAnnexTable.name,
								new StringBuffer(AnnexTable.field_tagId)
										.append("=? and ")
										.append(AnnexTable.field_tag)
										.append("=?").toString(),
								new String[] { caseId, LocalCaseTable.name });

						// 删除案件记录
						DBHelper.getDbHelper(context).delete(
								LocalCaseTable.name,
								LocalCaseTable.field_id + "=?",
								new String[] { caseId });
						return true;
					} else {
						msg = caseBackJson.getString("msg");
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
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
			if (result) {
				GravityCenterToast
						.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
				getActivity().onBackPressed();
			} else {
				GravityCenterToast.makeText(context, "发送失败，" + msg,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 
	 * @ClassName: caseSaveTask
	 * @Description: TODO
	 * @author leix@geo-k.cn
	 * @date 2014年5月13日
	 * 
	 */
	class caseSaveTask extends AsyncTask<Void, Void, Boolean> {

		Context context;
		boolean slient;

		public caseSaveTask(Context c, boolean slient) {
			this.context = c;
			this.slient = slient;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (slient)
				return;
			CircleProgressBusyView abv = new CircleProgressBusyView(context);
			abv.setMsg(getString(R.string.cn_saveing));
			alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.show();
			alertDialog.setCancelable(false);
			alertDialog.getWindow().setContentView(abv);

			address = addressView.getText().toString();
			notes = notesView.getText().toString();
			parties = partiesView.getText().toString();
			String areaText = illegalAreaView.getText().toString();
			if (areaText != null && !"".equals(areaText)) {
				if (isCurrentUnitSqm)
					illegalArea = Double.parseDouble(areaText);
				else
					illegalArea = mu2Sqm(Double.parseDouble(areaText));
			} else {
				illegalArea = 0d;
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean succeed = false;
			try {

				ContentValues caseValues = new ContentValues();
				caseValues.put(LocalCaseTable.field_address, address);
				caseValues.put(LocalCaseTable.field_admin, adminCode);
				caseValues.put(LocalCaseTable.field_illegalArea, illegalArea);
				caseValues.put(LocalCaseTable.field_illegalStatus,
						keyIllegalStatus);
				caseValues
						.put(LocalCaseTable.field_illegalType, keyIllegalType);
				caseValues.put(LocalCaseTable.field_landUsage, keyLandUsage);
				caseValues.put(LocalCaseTable.field_source, keySource);
				caseValues.put(LocalCaseTable.field_projType, keyProjType);
				caseValues.put(LocalCaseTable.field_notes, notes);
				caseValues.put(LocalCaseTable.field_parties, parties);
				caseValues.put(LocalCaseTable.field_illegalSubject,
						keyIllegalSubject);
				caseValues.put(LocalCaseTable.field_redline, redlineJson);
				caseValues.put(LocalCaseTable.field_user, currentUser);
				if (x != null)
					caseValues.put(LocalCaseTable.field_x, x);
				if (y != null)
					caseValues.put(LocalCaseTable.field_y, y);

				// 删除已有附件记录
				DBHelper.getDbHelper(context).delete(
						LocalAnnexTable.name,
						AnnexTable.field_tagId + "=? and "
								+ AnnexTable.field_tag + "=?",
						new String[] { caseId, LocalCaseTable.name });

				ArrayList<String> imagePahts = imgGridAdapter.getPaths();
				if (imagePahts != null && imagePahts.size() > 0) {
					for (String s : imagePahts) {
						File file = new File(s);
						String fileName = file.getName();
						ContentValues aValues = new ContentValues();
						aValues.put(AnnexTable.field_caseId, caseId);
						aValues.put(AnnexTable.field_tag, LocalCaseTable.name);
						aValues.put(AnnexTable.field_tagId, caseId);
						aValues.put(AnnexTable.field_path, s);
						aValues.put(AnnexTable.field_name, fileName);

						DBHelper.getDbHelper(context).insert(
								LocalAnnexTable.name, aValues);
					}
				}

				ArrayList<String> tapePaths = tapeGridAdapter.getPaths();
				if (tapePaths != null && tapePaths.size() > 0) {
					for (String s : tapePaths) {
						File file = new File(s);
						String fileName = file.getName();
						ContentValues aValues = new ContentValues();
						aValues.put(AnnexTable.field_caseId, caseId);
						aValues.put(AnnexTable.field_tag, LocalCaseTable.name);
						aValues.put(AnnexTable.field_tagId, caseId);
						aValues.put(AnnexTable.field_path, s);
						aValues.put(AnnexTable.field_name, fileName);

						DBHelper.getDbHelper(context).insert(
								LocalAnnexTable.name, aValues);
					}
				}

				String where = new StringBuffer(LocalCaseTable.field_id)
						.append("=?").toString();
				String args[] = new String[] { caseId };
				int count = DBHelper.getDbHelper(context).queryCount(
						LocalCaseTable.name, null, where, args);
				if (count > 0) {
					int rows = DBHelper.getDbHelper(context).update(
							LocalCaseTable.name, caseValues, where, args);
					succeed = (rows > 0);

				} else {
					caseValues.put(LocalCaseTable.field_mTime,
							TimeFormatFactory2.getDateFormat(new Date()));
					caseValues.put(LocalCaseTable.field_id, caseId);
					long row = DBHelper.getDbHelper(context).insert(
							LocalCaseTable.name, caseValues);
					succeed = (row > -1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return succeed;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (slient)
				return;

			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}

			if (result) {
				GravityCenterToast
						.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
				VibratorUtil.Vibrate(getActivity(), 100);

			} else {
				GravityCenterToast
						.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
				VibratorUtil.Vibrate(getActivity(), 200);
			}
		}
	}

	IClickListener tapeClickListener = new IClickListener() {

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
						tapeGridAdapter.deleteItem(path);
						if (tapeGridAdapter.getCount() < 1)
							tapeLayout.setVisibility(View.GONE);
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.scale_out_center);
						anim.setFillEnabled(true);
						anim.setAnimationListener(new AnimationListener() {
							public void onAnimationStart(Animation animation) {
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationEnd(Animation animation) {
								tapeGridAdapter.notifyDataSetChanged();
							}
						});
						delView.startAnimation(anim);
						File f = new File(path);
						f.delete();
						try {
							DBHelper.getDbHelper(context).delete(
									LocalAnnexTable.name,
									new StringBuffer(AnnexTable.field_tagId)
											.append("=? and ")
											.append(AnnexTable.field_tag)
											.append("=? and ")
											.append(AnnexTable.field_path)
											.append("=?").toString(),
									new String[] { caseId, LocalCaseTable.name,
											path });
						} catch (Exception e) {
							e.printStackTrace();
						}
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
						imgGridAdapter.deleteItem(path);
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.scale_out_center);
						anim.setFillEnabled(true);
						anim.setAnimationListener(new AnimationListener() {
							public void onAnimationStart(Animation animation) {
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationEnd(Animation animation) {
								imgGridAdapter.notifyDataSetChanged();
							}
						});
						delView.startAnimation(anim);
						File f = new File(path);
						f.delete();

						try {
							DBHelper.getDbHelper(context).delete(
									LocalAnnexTable.name,
									new StringBuffer(AnnexTable.field_tagId)
											.append("=? and ")
											.append(AnnexTable.field_tag)
											.append("=? and ")
											.append(AnnexTable.field_path)
											.append("=?").toString(),
									new String[] { caseId, LocalCaseTable.name,
											path });
						} catch (Exception e) {
							e.printStackTrace();
						}
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

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			int vid = v.getId();
			Intent intent = null;
			switch (vid) {
			case R.id.fragment_case_edite_redline:
				Log.i("ydzf","LandCaseEditeFragment_redline_Button_press");
				intent = new Intent(context, PolyGatherActivity.class);
				//redlineJson="{\"x\":119.28653325838614,\"y\":26.059112172755427}";
				if (redlineJson != null && redlineJson.length() > 0) {
					intent.putExtra(PolyGatherActivity.REDLINE, redlineJson);
				}

				getActivity().startActivityForResult(intent,
						Parameters.GET_REDLINE);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			case R.id.fragment_case_edite_camera:
				intent = new Intent(context, CameraPhotoActivity.class);
				getActivity().startActivityForResult(intent,
						Parameters.GET_PHOTO);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;

			case R.id.fragment_case_edite_tape:
				Intent tapeIntent = new Intent(context, TapeActivity.class);
				getActivity().startActivityForResult(tapeIntent,
						Parameters.GET_AUDIO);
				break;

			case R.id.fragment_case_edite_admin:
				intent = new Intent(context, AdminChooserActivity.class);
				intent.putExtra(AdminChooserActivity.ADMIN_CODE, adminCode);
				getActivity().startActivityForResult(intent,
						Parameters.GET_ADMIN);
				getActivity().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				break;
			case R.id.fragment_case_edite_unit:
				if (isCurrentUnitSqm) {// 当前单位为平方米
					unitView.setText(R.string.mu);
					String text = illegalAreaView.getText().toString();
					if (text != null && !"".equals(text.trim())) {
						double numb = sqm2Mu(Double.parseDouble(text));
						illegalAreaView.setText(kmFormat.format(numb));
					}
				} else {
					unitView.setText(R.string.sqm);
					String text = illegalAreaView.getText().toString();
					if (text != null && !"".equals(text.trim())) {
						double numb = mu2Sqm(Double.parseDouble(text));
						illegalAreaView.setText(kmFormat.format(numb));
					}
				}
				isCurrentUnitSqm = !isCurrentUnitSqm;
				break;
			default:
				break;
			}
		}
	};

	double sqm2Mu(double m) {
		return 0.0015d * m;
	}

	double mu2Sqm(double m) {
		return m / 0.0015d;
	}
}
