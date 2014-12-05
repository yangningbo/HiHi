package com.gaopai.guiren.volley;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.gaopai.guiren.R;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class UIHelperUtil {

	public static Context cxt;
	public static String URL;
	private static Properties pro;

	protected static final int command_start = 1;
	protected static final int command_failure = 2;
	protected static final int command_success = 3;
	protected static final int command_finish = 4;
	protected static final int command_timeout = 5;

	protected static final int command_error = 10;
	private IResponseListener listener;
	private Object response;
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case command_start:
				UIHelperUtil.this.listener.onReqStart();
				break;
			case command_success:
				if (UIHelperUtil.this.getResponse() == null) {
					Toast.makeText(cxt, cxt.getString(R.string.data_error), Toast.LENGTH_SHORT).show();
					UIHelperUtil.this.listener.onFinish();
					return;
				}
				UIHelperUtil.this.listener.onSuccess(UIHelperUtil.this.getResponse());
				UIHelperUtil.this.listener.onFinish();
				break;
			case command_failure:
				UIHelperUtil.this.listener.onFailure(UIHelperUtil.this.getResponse());
				break;
			case command_finish:
				UIHelperUtil.this.listener.onFinish();
				break;
			case command_timeout:
				UIHelperUtil.this.listener.onTimeOut();
				break;
			case command_error:
				Toast.makeText(cxt, cxt.getString(R.string.data_error), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	// public UIHelperUtil() {
	// if (URL == null) {
	// URL = getUrl(cxt);
	// }
	// }

	public static String getUrl(Context cxt) {
		initPropertis(cxt);
		return pro.getProperty("com.dean.json.api.url");
	}

	private static void initPropertis(Context cxt) {
		InputStream fis = null;
		if (pro == null) {
			try {
				fis = cxt.getResources().getAssets().open("pro.properties");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			pro = new Properties();
			try {
				pro.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static UIHelperUtil getUIHelperUtil(IResponseListener listener) {
		UIHelperUtil uhu = new UIHelperUtil();
		uhu.listener = listener;
		return uhu;
	}

	protected void sendMessage(int state) {
		if (this.listener != null) {
			this.handler.sendEmptyMessage(state);
		}
	}

	public void sendStartMessage() {
		sendMessage(command_start);
	}

	public void sendSuccessMessage(Object object) {
		setResponse(object);
		sendMessage(command_success);
	}

	public void sendFailureMessage(Object object) {
		setResponse(object);
		sendMessage(command_failure);
	}

	public void sendFinishMessage() {
		sendMessage(command_finish);
	}

	public void sendTimeOutMessage() {
		sendMessage(command_timeout);
	}

	public void sendErrorMessage() {
		sendMessage(command_error);
	}

	public Object getResponse() {
		return this.response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
}