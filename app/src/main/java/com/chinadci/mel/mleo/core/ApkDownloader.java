package com.chinadci.mel.mleo.core;

import java.io.File;
import java.util.UUID;

import android.content.Context;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.net.Uri;
import android.os.Environment;

public class ApkDownloader {
	static vDownloader downloader;

	public static long startDownload(Context context, String url, String tile,
			String desc, String folder) {
		if (downloader == null)
			downloader = new vDownloader();
		long did = downloader.startDownload(context, url, tile, desc, folder);
		return did;
	}

	static class vDownloader {

		public vDownloader() {
		}

		@SuppressLint({ "InlinedApi", "NewApi" })
		public long startDownload(Context context, String url, String tile,
				String desc, String folder) {
			try {
				String extSD = Environment.getExternalStorageDirectory()
						.toString();
				String apk = UUID.randomUUID().toString() + ".apk";
				String apkPath = new StringBuffer(extSD).append("/")
						.append(folder).append("/").append(apk).toString();
				File apkFile = new File(apkPath);
				DownloadManager downloadManager = (DownloadManager) context
						.getSystemService(android.content.Context.DOWNLOAD_SERVICE);
				Uri uri = Uri.parse(url);
				Request request = new Request(uri);
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
						| DownloadManager.Request.NETWORK_WIFI);
				request.setDestinationUri(Uri.fromFile(apkFile));
				request.setTitle(tile);
				request.setDescription(desc);
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setVisibleInDownloadsUi(true);
				request.setMimeType("application/vnd.android.package-archive");
				long id = downloadManager.enqueue(request);
				return id;
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
	}
}
