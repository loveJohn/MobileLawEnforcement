package com.chinadci.mel.mleo.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chinadci.android.utils.SharedPreferencesUtils;
import com.chinadci.mel.R;

/**
 * 
* @ClassName ServiceSettingActivity 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:32:08 
*
 */
public class ServiceSettingActivity extends Activity implements
		OnClickListener {
	EditText uriView;
	Button sureButton,resetButton;
	ImageButton backButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);
		backButton = (ImageButton) findViewById(R.id.activity_service_back);
		resetButton=(Button) findViewById(R.id.activity_service_reset_uri);
		sureButton = (Button) findViewById(R.id.activity_service_sure);
		uriView = (EditText) findViewById(R.id.activity_service_appuri);

		backButton.setOnClickListener(this);
		resetButton.setOnClickListener(this);
		sureButton.setOnClickListener(this);
		try {
			String appUri = SharedPreferencesUtils.getInstance(this,
					R.string.shared_preferences).getSharedPreferences(
					R.string.sp_appuri, "http://");
			uriView.setText(appUri);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int vid = v.getId();
		switch (vid) {
		case R.id.activity_service_back:
			finish();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			break;
		case R.id.activity_service_reset_uri:
			String defualtUri=this.getString(R.string.default_uri_app);
			if (saveServiceUri(defualtUri)) {
				uriView.setText(defualtUri);
				Toast.makeText(ServiceSettingActivity.this, "重置成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ServiceSettingActivity.this, "重置失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.activity_service_sure:
			String appUri = uriView.getText().toString();
			if (appUri == null || appUri.equalsIgnoreCase("")) {
				Toast.makeText(ServiceSettingActivity.this,
						"请输入新的服务器地址，包括ip与端口号", Toast.LENGTH_SHORT).show();
				return;
			} else {
				if (saveServiceUri(appUri)) {
					Toast.makeText(ServiceSettingActivity.this, "服务器地址更改成功",
							Toast.LENGTH_SHORT).show();
					// uriView.setText("");
				} else {
					Toast.makeText(ServiceSettingActivity.this, "用服务器地址更改失败",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private Boolean saveServiceUri(String uri) {
		try {
			String appUri = new StringBuffer(uri).append("/")
					.append("LandSupervisorServices").toString();
			SharedPreferencesUtils.getInstance(this,
					R.string.shared_preferences).writeSharedPreferences(
					R.string.sp_appuri, uri);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}
