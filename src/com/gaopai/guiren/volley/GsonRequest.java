package com.gaopai.guiren.volley;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonRequest<T> extends Request<T> {
	private final Gson gson = new Gson();
	private final Class<T> clazz;
	private final Map<String, String> params;
	private final Listener<T> listener;

	/**
	 * Make a GET request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 * @param clazz
	 *            Relevant class object, for Gson's reflection
	 * @param headers
	 *            Map of request headers
	 */
	public GsonRequest(String url, Class<T> clazz, Map<String, String> params,
			Listener<T> listener, ErrorListener errorListener, int type) {
		super(type, url, errorListener);
		this.clazz = clazz;
		this.params = params;
		this.listener = listener;
	}

	@Override
	public Map<String, String> getParams() throws AuthFailureError {
		return params != null ? params : super.getParams();
	}

//	@Override
//	public RetryPolicy getRetryPolicy() {
//		RetryPolicy retryPolicy = new DefaultRetryPolicy(10,
//				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//		return retryPolicy;
//	}

	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}

//	public Map<String, String> getHeaders() throws AuthFailureError {
//		Map<String, String> headers = new HashMap<String, String>();
//		headers.put("Charset", "UTF-8");
//		headers.put("Content-Type", "application/x-javascript");
//		headers.put("Accept-Encoding", "gzip,deflate");
//		return headers;
//	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			// String json = new String(response.data, "utf-8");
			String json = getRealString(response.data);
			
			
			
			
//			GsonObj gsonObj = null;
//			try {
//				gsonObj = (GsonObj) clazz.newInstance();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
			Log.d("Json", json);
			
			return (Response<T>) Response.success(
					JSON.parseObject(json, clazz),
					HttpHeaderParser.parseCacheHeaders(response));
//			return (Response<T>) Response.success(
//					gson.fromJson(json, gsonObj.getTypeToken()),
//					HttpHeaderParser.parseCacheHeaders(response));
		}
		// catch (UnsupportedEncodingException e) {
		// return Response.error(new ParseError(e));
		// }
		catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	// public static Cache.Entry cache(NetworkResponse response, long maxAge) {
	// long now = System.currentTimeMillis();
	// if (maxAge == 0)
	// maxAge = 60;
	// Map<String, String> headers = response.headers;
	//
	// long serverDate = 0;
	// long softExpire = 0;
	// String serverEtag = null;
	// String headerValue;
	//
	// headerValue = headers.get("Date");
	// if (headerValue != null) {
	// serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
	// }
	// softExpire = now + maxAge * 1000;
	// Cache.Entry entry = new Cache.Entry();
	// entry.data = response.data;
	// entry.etag = serverEtag;
	// entry.softTtl = softExpire;
	// entry.ttl = entry.softTtl;
	// entry.serverDate = serverDate;
	// entry.responseHeaders = headers;
	// return entry;
	// }

	private int getShort(byte[] data) {
		return ((data[0] << 8) | data[1] & 0xFF);
	}

	/**
	 * Gzip用
	 * 
	 * @param data
	 * @return
	 */
	private String getRealString(byte[] data) {
		byte[] h = new byte[2];
		h[0] = (data)[0];
		h[1] = (data)[1];
		int head = getShort(h);
		boolean t = head == 0x1f8b;
		InputStream in;
		StringBuilder sb = new StringBuilder();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if (t) {
				in = new GZIPInputStream(bis);
			} else {
				in = bis;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(in),
					1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}