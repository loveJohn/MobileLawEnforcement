package com.chinadci.mel.mleo.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
/**
 * 
* @ClassName ServiceUriAlertView 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:36:30 
*
 */
public class ServiceUriAlertView extends LinearLayout {
	Context context;
	View rootView;
	Button sureButton;
	EditText uriView;
	TextView titleView;
	String uri;
	String title;
	int spIndex;

	public ServiceUriAlertView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public ServiceUriAlertView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public ServiceUriAlertView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void setContent(String title, int spIndex) {
		this.title = title;
		this.spIndex = spIndex;
		titleView.setText(title);
		initUri();
	}

	void initView(Context c) {
		context = c;
		rootView = LayoutInflater.from(getContext()).inflate(
				R.layout.view_serviceuri, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		uriView = (EditText) rootView.findViewById(R.id.view_serviceuri_uri);
		titleView = (TextView) rootView
				.findViewById(R.id.view_serviceuri_title);
		sureButton = (Button) rootView.findViewById(R.id.view_serviceuri_sure);

		sureButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				modifyUri();
			}
		});
	}

	private void initUri() {
		String uri;
		try {
			uri = SharedPreferencesUtils.getInstance(context,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "");
			uriView.setText(uri);
		} catch (Exception e) {
			// TODO: handle exception
		}

		// switch (spIndex) {
		// case R.string.sp_appuri:
		// uri = SharedPreferencesUtils.getInstance(context,
		// R.string.shared_preferences).getSharedPreferences(
		// R.string.sp_appuri, "");
		// uriView.setText(uri);
		// break;
		//
		// case R.string.sp_bvmapuri:
		// uri = SharedPreferencesUtils.getInstance(context,
		// R.string.shared_preferences).getSharedPreferences(
		// R.string.sp_bvmapuri, "");
		// uriView.setText(uri);
		// break;
		// case R.string.sp_rsmapuri:
		// uri = SharedPreferencesUtils.getInstance(context,
		// R.string.shared_preferences).getSharedPreferences(
		// R.string.sp_bvmapuri, "");
		// uriView.setText(uri);
		// break;
		// default:
		// break;
		// }
	}

	/**
	 * �޸��������
	 */
	private void modifyUri() {
		uri = uriView.getText().toString();
		if (uri == null || uri.equals("")) {
			GravityCenterToast.makeText(context, "����������ַ", Toast.LENGTH_SHORT)
					.show();
			uriView.requestFocus();
		} else {
			try {
				SharedPreferencesUtils.getInstance(context,
						R.string.shared_preferences).writeSharedPreferences(
						spIndex, uri);
				GravityCenterToast.makeText(context, title + "�޸ĳɹ�",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				GravityCenterToast.makeText(context, title + "�޸�ʧ��",
						Toast.LENGTH_SHORT).show();
			}

			// switch (spIndex) {
			// case R.string.sp_appuri:
			// SharedPreferencesUtils.getInstance(context,
			// R.string.shared_preferences).writeSharedPreferences(
			// R.string.sp_appuri, uri);
			// GravityCenterToast.makeText(context, title + "�޸ĳɹ�",
			// Toast.LENGTH_SHORT).show();
			// break;
			//
			// case R.string.sp_bvmapuri:
			// SharedPreferencesUtils.getInstance(context,
			// R.string.shared_preferences).writeSharedPreferences(
			// R.string.sp_bvmapuri, uri);
			// GravityCenterToast.makeText(context, title + "�޸ĳɹ�",
			// Toast.LENGTH_SHORT).show();
			// break;
			//
			// case R.string.sp_rsmapuri:
			// SharedPreferencesUtils.getInstance(context,
			// R.string.shared_preferences).writeSharedPreferences(
			// R.string.sp_rsmapuri, uri);
			// GravityCenterToast.makeText(context, title + "�޸ĳɹ�",
			// Toast.LENGTH_SHORT).show();
			// break;
			//
			// default:
			// break;
			// }
		}
	}
}
