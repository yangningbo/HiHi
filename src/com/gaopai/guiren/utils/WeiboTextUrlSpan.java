package com.gaopai.guiren.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;

public class WeiboTextUrlSpan extends ClickableSpan implements ParcelableSpan {

	public static final int TYPE_LINK = 0;
	public static final int TYPE_USER = 1;
	public static final int TYPE_CONNECTION = 2;

	private int mType = -1;

	private final String mUrl;

	public WeiboTextUrlSpan(String url) {
		mUrl = url;
	}

	public WeiboTextUrlSpan(String url, int type) {
		mUrl = url;
		mType = type;
	}

	@Override
	public int getSpanTypeId() {
		return 11;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mUrl);
	}

	@Override
	public void onClick(View widget) {
		Uri uri = Uri.parse(mUrl);
		String uirStr = uri.toString();
		if (uirStr.contains(MyTextUtils.USER_SCHEME) && uirStr.contains("-1")) {
			return;
		}
		Context context = widget.getContext();
		openUri(context, uri);
	}
	
	public String getUrl() {
		return mUrl;
	}

	public boolean isIntentAvailable(Intent intent) {
		PackageManager packageManager = DamiApp.getInstance().getPackageManager();
		List<ResolveInfo> resolveInfos = packageManager
				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return resolveInfos.size() > 0;
	}

	public void openUri(Context context, Uri uri) {
		Intent intent = new Intent("android.intent.action.VIEW", uri);
		intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
		if (isIntentAvailable(intent)) {
			context.startActivity(intent);
		}
	}

	public static int getColor(int colorId) {
		Context context = DamiApp.getInstance();
		int color = context.getResources().getColor(colorId);
		return color;
	}

	@Override
	public void updateDrawState(TextPaint tp) {
		switch (mType) {
		case TYPE_LINK:
			tp.setColor(getColor(R.color.blue_dongtai_names));
			break;
		case TYPE_USER:
			tp.setColor(Color.BLUE);
			break;
		case TYPE_CONNECTION:
			tp.setColor(getColor(R.color.connection_linkfy));
			break;
		default:
			break;
		}
	}

	public void onLongClick(View widget) {
	}

}
