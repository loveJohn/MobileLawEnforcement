package com.chinadci.mel.mleo.ui.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @ClassName TapeActivity
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:32:14
 * 
 */
public class TapeActivity extends Activity {

	public final static String AMRFILE = "amr";
	public final static int GETTAPE = 0x000001;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		ProgressBar progressBar = new ProgressBar(this);
		TextView textView = new TextView(this);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		textView.setText("请稍候...");
		layout.setBackgroundColor(Color.BLACK);
		textView.setTextColor(Color.WHITE);
		layout.addView(progressBar, new LayoutParams(
				((int) (24 * getResources().getDisplayMetrics().density)),
				(int) (24 * getResources().getDisplayMetrics().density)));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) (8 * getResources().getDisplayMetrics().density);
		layout.addView(textView, params);
		setContentView(layout);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == GETTAPE) {
				String oldPath = "";
				String newPath = "";
				String amrName = createAmrName();
				String exStorage = Environment.getExternalStorageDirectory()
						.getAbsolutePath();

				int dir_audios = getResources().getIdentifier(
						getPackageName() + ":string/" + "dir_audios", null,
						null);

				newPath = new StringBuffer(exStorage).append("/")
						.append(getString(dir_audios)).append("/")
						.append(amrName).toString();

				Uri uriRec = data.getData();
				Cursor cursor = this.getContentResolver().query(uriRec, null,
						null, null, null);

				if (cursor != null) {
					if (cursor.moveToNext())
						oldPath = cursor.getString(cursor
								.getColumnIndex("_data"));
				} else {
					oldPath = uriRec.getPath();
				}

				if (copyFile(oldPath, newPath)) {
					Bundle bundle = new Bundle();
					bundle.putString(AMRFILE, newPath);
					TapeActivity.this.setResult(RESULT_OK, this.getIntent()
							.putExtras(bundle));
				}
			}
			finish();
		} else {
			finish();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
	};

	/**
	 * 
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(Media.RECORD_SOUND_ACTION);
			startActivityForResult(intent, GETTAPE);
		};
	};

	private String createAmrName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'TAPE'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".amr";
	}

	public boolean copyFile(String oldPath, String newPath) {
		boolean isok = true;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // �ļ�����ʱ
				InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // �ֽ��� �ļ���С
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
				oldfile.delete();
			} else {
				isok = false;
			}
		} catch (Exception e) {
			isok = false;
		}
		return isok;

	}

}
