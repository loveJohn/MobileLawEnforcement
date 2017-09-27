package com.chinadci.mel.mleo.ui.views;

import org.json.JSONException;
import org.json.JSONObject;

import com.chinadci.mel.R;
import com.chinadci.mel.android.ui.views.GravityCenterToast;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * 
* @ClassName UserPasswordView 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:36:22 
*
 */
public class UserPasswordView extends LinearLayout {
	Context context;
	View rootView;
	Button alertButton;
	EditText oldPasswordView;
	EditText newPasswordView;
	String oldPassword;
	String newPassword;
	String currentUser;

	public UserPasswordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public UserPasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public UserPasswordView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public void setUser(String userName) {
		currentUser = userName;
	}

	void initView(Context c) {
		context = c;
		rootView = LayoutInflater.from(getContext()).inflate(
				R.layout.view_setting_password, null);
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(rootView);

		oldPasswordView = (EditText) findViewById(R.id.view_setting_password_oldpassword);
		newPasswordView = (EditText) findViewById(R.id.view_setting_password_newpassword);
		alertButton = (Button) findViewById(R.id.view_setting_password_sure);
		alertButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				modifyPassword();
			}
		});

	}

	
	private void modifyPassword() {
		oldPassword = oldPasswordView.getText().toString();
		newPassword = newPasswordView.getText().toString();
		if (oldPassword == null || oldPassword.equals("")) {
			GravityCenterToast
					.makeText(context, "请输入当前密码", Toast.LENGTH_SHORT).show();
			oldPasswordView.requestFocus();
		}

		if (newPassword == null || newPassword.equals("")) {
			GravityCenterToast.makeText(context, "请输入新密码", Toast.LENGTH_SHORT)
					.show();
			newPasswordView.requestFocus();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("name", currentUser);
			jsonObject.put("oldPassword", oldPassword);
			jsonObject.put("newPassword", newPassword);
			final String jsonString = jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
