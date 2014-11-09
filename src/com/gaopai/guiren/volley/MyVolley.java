/**
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gaopai.guiren.volley;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.gaopai.guiren.R;

/**
 * Helper class that is used to provide references to initialized
 * RequestQueue(s) and ImageLoader(s)
 * 
 * @author Ognyan Bankov
 * 
 */
public class MyVolley {
	private static RequestQueue mRequestQueue;
	private static AbstractHttpClient mHttpClient;
	private static ImageLoader mImageLoader;

	private MyVolley() {
		// no instances
	}

	public static void init(Context context) {
		mHttpClient = new DefaultHttpClient();
		mRequestQueue = Volley.newRequestQueue(context);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
				8 * 1024 * 1024));
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static void displayImage(String url, ImageView imageView,
			int errorResId) {
		ImageListener listener = ImageLoader.getImageListener(imageView,
				android.R.drawable.ic_menu_rotate, errorResId);
		mImageLoader.get(url, listener);
	}

	public static void displayImage(String url, ImageView imageView,
			int processId, int errorResId) {
		ImageListener listener = ImageLoader.getImageListener(imageView,
				processId, errorResId);
		mImageLoader.get(url, listener);
	}


}
