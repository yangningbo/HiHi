package com.gaopai.guiren;

import java.lang.reflect.Field;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.volley.GsonObj;
import com.gaopai.guiren.volley.GsonRequest;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.volley.MyVolley;
import com.gaopai.guiren.volley.UIHelperUtil;

/**
 * 网络请求
 * 
 * @Description
 * @author Dean
 * 
 */
public class UIHelper {

	private static String outTimeError = "java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)";

	private static String port;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static GsonRequest reqData(int method, final Class cls, Map<String, Object> params, Object obj,
			IResponseListener listener) {

		final UIHelperUtil uhu = UIHelperUtil.getUIHelperUtil(listener);

		if (!MyUtils.isNetConnected(UIHelperUtil.cxt)) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(UIHelperUtil.cxt, R.string.network_wrong, Toast.LENGTH_SHORT).show();
					uhu.sendFailureMessage(null);
					uhu.sendFinishMessage();
				}
			});
			return null;
		}
		getPort(obj, cls);
		GsonRequest jr = new GsonRequest(DamiInfo.SERVER + port, cls, params, new Listener() {

			@Override
			public void onResponse(Object arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null) {
					uhu.sendSuccessMessage(arg0);
				} else
					uhu.sendFailureMessage(null);
				uhu.sendFinishMessage();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (arg0.getClass().equals(TimeoutError.class)) {
					uhu.sendTimeOutMessage();
				} else {
					uhu.sendFailureMessage(arg0);
				}
				uhu.sendFinishMessage();
				Log.e("Json", arg0 != null ? cls.getName() + " === " + arg0.getClass() + " === " + arg0.getMessage()
						: "error");
			}
		}, method);
		jr.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, 1.0f));
		uhu.sendStartMessage();
		MyVolley.getRequestQueue().add(jr);
		return jr;
	}

	private static void getPort(Object obj, @SuppressWarnings("rawtypes") Class cls) {
		GsonObj gsonObj = null;
		try {
			gsonObj = (GsonObj) cls.newInstance();
			if (obj != null) {
				try {
					Field field = cls.getDeclaredField("obj");
					if (field != null) {
						field.setAccessible(true);
						field.set(gsonObj, obj);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			}
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		port = gsonObj.getInterface();
	}

}
