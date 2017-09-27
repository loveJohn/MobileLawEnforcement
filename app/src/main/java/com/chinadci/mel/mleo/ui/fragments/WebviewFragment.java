package com.chinadci.mel.mleo.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * 
 * @ClassName LandStatisticsFragment
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年7月9日 下午4:47:15
 * 
 */
public class WebviewFragment extends ContentFragment {
	public final static String TAG_URL = "url";

	View rootView;
	WebView webView;
	ProgressBar progressBar;
	String url;

	int i_progress_res;
	int i_webview_res;
	int i_layout_res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			url = getArguments().getString(TAG_URL);

			initRes();
			rootView = inflater.inflate(i_layout_res, container, false);
			webView = (WebView) rootView.findViewById(i_webview_res);
			progressBar = (ProgressBar) rootView.findViewById(i_progress_res);

			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.setWebViewClient(new myWebViewClient());
			webView.setWebChromeClient(new myWebChromeViewClient());
			webView.loadUrl(url);
			Log.i("url", url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootView;
	}

	void initRes() {
		i_layout_res = context.getResources().getIdentifier(
				context.getPackageName() + ":layout/" + "fragment_webview",
				null, null);
		i_progress_res = context.getResources()
				.getIdentifier(
						context.getPackageName() + ":id/"
								+ "fragment_webview_progress", null, null);
		i_webview_res = context.getResources().getIdentifier(
				context.getPackageName() + ":id/" + "fragment_webview_webview",
				null, null);
	}

	class myWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	class myWebChromeViewClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressBar.setVisibility(view.GONE);
			} else {
				if (progressBar.getVisibility() == View.GONE)
					progressBar.setVisibility(view.VISIBLE);
				progressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	}
}
