package com.chinadci.mel.mleo.ui.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import com.chinadci.mel.R;
import com.chinadci.mel.mleo.ldb.DBHelper;
import com.chinadci.mel.mleo.ldb.UserFields;

/**
 * 
 * @ClassName PhotoCutActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:02
 * 
 */
public class PhotoCutActivity extends Activity {
	public final static int CAMERAWAY = 2;
	public final static int FILEWAY = 1;
	final static int PHOTOZOOM = 300;
	public final static String PHOTOFROM = "photofrom";
	String photoPath;
	String user;
	int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photocut);
		user = getIntent().getStringExtra("user");
		type = getIntent().getIntExtra(PHOTOFROM, 0);
		new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO: handle exception
				}
				handler.sendEmptyMessage(1);
			}
		}).start();

	}

	protected void onDestroy() {
		try {
			if (type == CAMERAWAY) {
				if (photoPath != null && !photoPath.equals("")) {
					File file = new File(photoPath);
					if (file.exists())
						file.delete();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	};

	/**
	 * 
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (type) {
			case CAMERAWAY:
				photoFromCamera();
				break;

			case FILEWAY:
				photoFromFile();
				break;

			default:
				finish();
				break;
			}

		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case FILEWAY:
				startPhotoZoom(data.getData());
				break;

			case CAMERAWAY:
				File temp = new File(photoPath);
				startPhotoZoom(Uri.fromFile(temp));
				break;

			case PHOTOZOOM:
				if (data != null) {
					savePhoto(data);
				}
				break;

			default:
				break;

			}
		} else if (resultCode == RESULT_CANCELED) {
			finish();
		}

	}

	private void photoFromCamera() {
		String exStorage = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		photoPath = new StringBuffer(exStorage).append("/")
				.append(getString(R.string.dir_photos)).append("/")
				.append(getPhotoFileName()).toString();
		File photoFile = new File(photoPath);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		PhotoCutActivity.this.startActivityForResult(intent, CAMERAWAY);
	}

	private void photoFromFile() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		PhotoCutActivity.this.startActivityForResult(intent, FILEWAY);
	}

	public void startPhotoZoom(Uri uri) {
		int outPut = (int) (80 * getResources().getDisplayMetrics().density);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outPut);
		intent.putExtra("outputY", outPut);
		intent.putExtra("return-data", true);
		PhotoCutActivity.this.startActivityForResult(intent, PHOTOZOOM);
	}

	private void savePhoto(Intent picdata) {
		try {

			Bundle extras = picdata.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				Drawable drawable = new BitmapDrawable(photo);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] b = stream.toByteArray();
				String table = getString(R.string.tb_users);
				String where = new StringBuffer(UserFields.name).append("=?")
						.toString();
				String args[] = new String[] { user };
				ContentValues updateValues = new ContentValues();
				updateValues.put(UserFields.photo, b);
				int rows = DBHelper.getDbHelper(this).update(table,
						updateValues, where, args);
				if (rows > 0) {
					Intent serviceIntent = new Intent();
					serviceIntent
							.setAction("com.chinadci.mel.mobilelam.action.UPDATEUSERPHOTO");
					sendBroadcast(serviceIntent);
					PhotoCutActivity.this.setResult(RESULT_OK, null);
					finish();

				} else {
					PhotoCutActivity.this.setResult(RESULT_OK, null);
					finish();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'PHOTO'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
}
