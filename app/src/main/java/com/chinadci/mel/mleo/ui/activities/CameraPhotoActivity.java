package com.chinadci.mel.mleo.ui.activities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinadci.android.utils.DrawableUtils;
import com.chinadci.mel.android.core.DciActivityManager;
import com.chinadci.mel.mleo.core.utils.CommonUtil;
import com.chinadci.mel.mleo.ui.fragments.data.ldb.DbUtil;
import com.south.rotationvectorcompass.OrientationSensor;
import com.south.rotationvectorcompass.OrientationUpdateDelegate;

/**
 */
@SuppressLint("HandlerLeak")
public class CameraPhotoActivity extends Activity implements LocationListener {
	final static int PHOTOZOOM = 300;
	public final static String PHOTOARRAY = "photoarray";
	String photoPath;
	String sPath;
	String tPath;

	ArrayList<String> photos;
	String user;
	int type;

	// 姿态传感器
	private OrientationSensor mOrientationSensor;
	private LocationManager mLocationManager;

	float fwj = 0f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		TextView textView = new TextView(this);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		layout.setBackgroundColor(Color.BLACK);
		textView.setTextColor(Color.WHITE);
		textView.setText("请稍候...");
		layout.addView(progressBar, new LayoutParams(
				((int) (24 * getResources().getDisplayMetrics().density)),
				(int) (24 * getResources().getDisplayMetrics().density)));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) (8 * getResources().getDisplayMetrics().density);
		layout.addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		setContentView(layout);
		user = getIntent().getStringExtra("user");
		photos = new ArrayList<String>();

		try {
			mOrientationSensor = OrientationSensor.getOrientationSensor(this);
			mLocationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);// 初始化定位服务
			// 消息监听
			if (mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, this);
			}
		} catch (Exception e) {
		}

		new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	protected void onDestroy() {
		super.onDestroy();
	};

	protected void onPause() {
		try {
			mOrientationSensor.unregisterListener();
		} catch (Exception e) {
		}
		try {
			mLocationManager.removeUpdates(this);
		} catch (Exception e) {
		}
		super.onPause();
	};

	protected void onResume() {
		try {
			mOrientationSensor
					.registerListener(new OrientationUpdateDelegate() {
						@Override
						public void onRotationUpdate(float arg0, float arg1,
								float arg2) {
							fwj = arg0;
						}
					});
		} catch (Exception e) {
		}
		try {
			if (mLocationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, this);
			}
		} catch (Exception e) {
		}
		super.onResume();
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			photoFromCamera();
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// new ImageMatrixTask(540).execute(photoPath);
			DecimalFormat degreeFormat4Location = new DecimalFormat(
					"##0.0000000");
			DecimalFormat degreeFormat = new DecimalFormat("##0.0\u00B0");
			String xzb;
			String yzb;
			if(DciActivityManager.myLocationLng>0){
				xzb = degreeFormat4Location.format(DciActivityManager.myLocationLng);
				yzb = degreeFormat4Location.format(DciActivityManager.myLocationLat);
			}else{
				xzb = "请移步到GPS信号好的地方";
				yzb = "请移步到GPS信号好的地方";
			}
			String fwjStr;
			if (fwj > 0) {
				fwjStr = degreeFormat.format(fwj);
			}else{
				fwjStr = "设备不支持";
			}
			String pssj =CommonUtil.getCurrentTimeStr();
			try {
				String path = photoPath;
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 4;
				Bitmap sBitmap = BitmapFactory.decodeFile(path, opts);
				// if(fwj>0){
				// TODO 加水印
				// Matrix matrix = new Matrix();
				// sBitmap = Bitmap.createBitmap(sBitmap ,0,0, sBitmap
				// .getWidth(), sBitmap.getHeight(),matrix,true);//bitmap旋转
				String str1 = "经度："
						+ xzb;
				String str2 = "纬度："
						+ yzb;
				String str3 = "方位角：" + fwjStr;
				String str4 = pssj;
				int width = sBitmap.getWidth(), hight = sBitmap.getHeight();
				Bitmap bitmapWithText = Bitmap.createBitmap(width, hight,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmapWithText);
				Paint photoPaint = new Paint();
				photoPaint.setDither(true);
				photoPaint.setFilterBitmap(true);
				Rect src = new Rect(0, 0, sBitmap.getWidth(),
						sBitmap.getHeight());
				Rect dst = new Rect(0, 0, width, hight);
				canvas.drawBitmap(sBitmap, src, dst, photoPaint);
				Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
						| Paint.DEV_KERN_TEXT_FLAG);
				textPaint.setTextSize(26.0f);
				textPaint.setTypeface(Typeface.DEFAULT_BOLD);
				textPaint.setColor(Color.RED);
				canvas.drawText(str1, 26, 26, textPaint);
				canvas.drawText(str2, 26, 66, textPaint);
				canvas.drawText(str3, 26, 106, textPaint);
				canvas.drawText(str4, 26, 146, textPaint);
				canvas.save(Canvas.ALL_SAVE_FLAG);
				canvas.restore();
				try {
					BufferedOutputStream bos = new BufferedOutputStream(
							new FileOutputStream(path));
					bitmapWithText.compress(CompressFormat.PNG, 100, bos);// 将图片压缩的流里面
					bos.flush();// 刷新此缓冲区的输出流
					bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				sBitmap.recycle();
				bitmapWithText.recycle();
				// }else{
				// // Bitmap tBitmap = DrawableUtils.zoomBitmap(sBitmap, 480);
				// OutputStream outputStream = new FileOutputStream(path);
				// // tBitmap.compress(CompressFormat.PNG, 100,
				// outputStream);//80
				// sBitmap.compress(CompressFormat.PNG, 100, outputStream);
				// outputStream.flush();
				// outputStream.close();
				// }
			} catch (Exception e) {
			}
			DbUtil.deletePhotoByPath(this, photoPath);
			DbUtil.insertPhoto(this, photoPath, xzb, yzb, fwjStr, pssj);
			photos.add(photoPath);
			photoFromCamera();
		} else if (resultCode == RESULT_CANCELED) {
			Bundle bundle = new Bundle();
			String[] pics = null;
			if (photos.size() > 0) {
				pics = new String[photos.size()];
				for (int i = 0; i < photos.size(); i++) {
					pics[i] = photos.get(i);
				}
			}
			bundle.putStringArray(PHOTOARRAY, pics);
			CameraPhotoActivity.this.setResult(RESULT_OK, this.getIntent()
					.putExtras(bundle));
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void photoFromCamera() {
		String exStorage = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		int dir_photos = getResources().getIdentifier(
				getPackageName() + ":string/" + "dir_photos", null, null);
		photoPath = new StringBuffer(exStorage).append("/")
				.append(getString(dir_photos)).append("/")
				.append(getPhotoFileName()).toString();
		File photoFile = new File(photoPath);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		CameraPhotoActivity.this.startActivityForResult(intent, 1);
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'PHOTO'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	class ImageMatrixTask extends AsyncTask<String, Void, Void> {
		int size;

		public ImageMatrixTask(int width) {
			size = width;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				String path = params[0];
				Bitmap sBitmap = BitmapFactory.decodeFile(path);
				Bitmap tBitmap = DrawableUtils.zoomBitmap(sBitmap, 480);
				OutputStream outputStream = new FileOutputStream(path);
				tBitmap.compress(CompressFormat.PNG, 100, outputStream);// 80
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
			}
			return null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		DciActivityManager.myLocationLng = location.getLongitude();
		DciActivityManager.myLocationLat = location.getLatitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
