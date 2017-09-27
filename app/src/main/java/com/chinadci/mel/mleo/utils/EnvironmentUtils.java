package com.chinadci.mel.mleo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

import com.chinadci.android.map.dci.WGS84TiandituImageryLayer;
import com.chinadci.android.map.dci.WGS84TiandituMapLayer;
import com.chinadci.android.utils.IOUtils;
import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.mleo.core.OutsideManifestHandler;

/**
 * 
 * @ClassName EnvironmentUtils
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月11日 下午1:58:40
 * 
 */
public class EnvironmentUtils {

	/**
	 * 
	 * @Title prepareEnvir
	 * @Description TODO
	 * @param c
	 *            void
	 */
	public static void prepareEnvir(Context c) {
		CreateLSDir(c);
		CopyServiceUri(c);
		CopyManifest(c);
		GetOutsideManifest(c);
	}

	public static void GetOutsideManifest(Context context) {
		try {
			String extSD = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String dirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_manifest))
					.toString();
			String fileStr = new StringBuffer(dirStr).append("/manifest.xml")
					.append("").toString();
			File file = new File(fileStr);
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				OutsideManifestHandler.readHandler(context, fis);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void CopyManifest(Context context) {
		try {
			String extSD = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String dirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_manifest))
					.toString();
			String fileStr = new StringBuffer(dirStr).append("/manifest.xml")
					.append("").toString();
			File dir = new File(dirStr);
			if (!dir.exists())
				dir.mkdirs();

			File file = new File(fileStr);
			if (!file.exists()) {
				InputStream inputStream = context.getAssets().open(
						"manifest.xml");
				OutputStream outStream = new FileOutputStream(fileStr);
				byte[] buffer = new byte[1024];
				int readLen = 0;
				while ((readLen = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, readLen);
				}
				outStream.flush();
				inputStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title CreateLSDir
	 * @Description TODO
	 * @param context
	 *            void
	 */
	public static void CreateLSDir(Context context) {
		try {

			String extSD = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			File df = new File(new StringBuffer(extSD).append(
					"/FJ.GouTuYiDongZhiFa").toString());
			IOUtils.deleteFile(df);
			String photoDirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_photos)).toString();
			String audioDirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_audios)).toString();
			String videoDirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_videos)).toString();
			String annexDirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_annex)).toString();
			String tempDirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_temp)).toString();
			String apkDirStr = new StringBuffer(extSD).append("/")
					.append(context.getString(R.string.dir_apk)).toString();

			File photoDirFile = new File(photoDirStr);
			File audioDirFile = new File(audioDirStr);
			File videoDirFile = new File(videoDirStr);
			File annexDirFile = new File(annexDirStr);
			File tempDirFile = new File(tempDirStr);
			File apkDirFile = new File(apkDirStr);

			if (!photoDirFile.exists())
				photoDirFile.mkdirs();
			if (!audioDirFile.exists())
				audioDirFile.mkdirs();
			if (!videoDirFile.exists())
				videoDirFile.mkdirs();
			if (!annexDirFile.exists())
				annexDirFile.mkdirs();
			if (!tempDirFile.exists())
				tempDirFile.mkdirs();
			if (!apkDirFile.exists())
				apkDirFile.mkdirs();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Title CopyServiceUri
	 * @Description TODO
	 * @param context
	 *            void
	 */
	public static void CopyServiceUri(Context context) {
		try {
			String appUri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "");
			String bvUri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_bvmapuri, "");
			String rsiUri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_rsmapuri, "");

			if (bvUri.equalsIgnoreCase("")) {
				String defUri = WGS84TiandituMapLayer.URL;
				SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).writeSharedPreferences(
						R.string.sp_bvmapuri, defUri);
			}

			if (rsiUri.equalsIgnoreCase("")) {
				String defUri = WGS84TiandituImageryLayer.URL;
				SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).writeSharedPreferences(
						R.string.sp_rsmapuri, defUri);
			}
			String defUri = context.getString(R.string.uri_app);
			SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_appuri, defUri);
//			if (appUri.equalsIgnoreCase("")) {
//				String defUri = context.getString(R.string.uri_app);
//				SharedPreferencesUtils.getInstance(context,
//						R.string.shared_preferences).writeSharedPreferences(
//						R.string.sp_appuri, defUri);
//			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
