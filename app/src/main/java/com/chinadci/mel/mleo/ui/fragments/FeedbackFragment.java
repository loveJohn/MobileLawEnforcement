package com.chinadci.mel.mleo.ui.fragments;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chinadci.android.core.Feedback;
import com.chinadci.android.utils.NetworkUtils;
import com.chinadci.mel.R;

/**
 * 
* @ClassName FeedbackFragment 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年7月9日 下午4:46:35 
*
 */
public class FeedbackFragment extends ContentFragment implements
		OnClickListener {
	View rootView;
	Button buttonSend;
	EditText txtContactinfo;
	EditText txtDetailsinfo;
	LinearLayout waitLayout;

	String contactInfo;
	String detailsInfo;

	@Override
	public void handle(Object o) {
		// TODO Auto-generated method stub
		super.handle(o);
	}

	@Override
	public void refreshUi() {
		// TODO Auto-generated method stub
		super.refreshUi();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_feedback, container,
				false);
		buttonSend = (Button) rootView
				.findViewById(R.id.fragment_feedback_send);
		txtContactinfo = (EditText) rootView
				.findViewById(R.id.fragment_feedback_contactinfo);
		txtDetailsinfo = (EditText) rootView
				.findViewById(R.id.fragment_feedback_detailsinfo);
		waitLayout = (LinearLayout) rootView
				.findViewById(R.id.fragment_feedback_waitlayout);
		buttonSend.setOnClickListener(this);
		return rootView;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		sendFeedback();
	}

	private void sendFeedback() {
		try {
			contactInfo = txtContactinfo.getText().toString();
			detailsInfo = txtDetailsinfo.getText().toString();

			if (contactInfo == null || contactInfo.equals("")) {
				txtContactinfo.requestFocus();
				Toast.makeText(context, "请填写您的联系方式", Toast.LENGTH_SHORT).show();
				return;
			}

			if (detailsInfo == null || detailsInfo.equals("")) {
				txtDetailsinfo.requestFocus();
				Toast.makeText(context, "请填写您的建议", Toast.LENGTH_SHORT).show();
				return;
			}

			if (!NetworkUtils.checkNetwork(context)) {
				Toast.makeText(context, "没有可用的网络", Toast.LENGTH_SHORT).show();
				return;
			}

			buttonSend.setEnabled(false);
			waitLayout.setVisibility(View.VISIBLE);

			new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					try {
						HttpResponse response = Feedback.getInstance(context)
								.feedbackSuggestion(R.string.dci_appid,
										currentUser, contactInfo, detailsInfo);
						if (response.getStatusLine().getStatusCode() == 200) {
							String res = EntityUtils.toString(response
									.getEntity());
							Message msg = handler.obtainMessage();
							msg.obj = res;
							msg.what = 1;
							handler.sendMessage(msg);
							return;
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					handler.sendEmptyMessage(0);
				}

			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			buttonSend.setEnabled(true);
			waitLayout.setVisibility(View.GONE);
			switch (msg.what) {
			case 1:
				txtDetailsinfo.setText("");
				Toast.makeText(context, "意见发送成功", Toast.LENGTH_SHORT).show();
				break;

			case 0:
				Toast.makeText(context, "发送意见失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}

		};
	};

	/**
	 * 
	 * 
	 * @param context
	 * @return
	 */
	private String getDeviceSerial(Context context) {
		String serialId = android.os.Build.SERIAL;
		if (serialId == null || serialId.equals("")) {
			try {
				Class<?> c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class, String.class);
				serialId = (String) (get.invoke(c, "ro.serialno", "unknown"));
			} catch (Exception ignored) {
			}
		}
		return serialId;
	}
}
