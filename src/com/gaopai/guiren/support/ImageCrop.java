package com.gaopai.guiren.support;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

public class ImageCrop {
	public static final int REQUEST_CROP_IMG = 199;
	public static final int MEETING_WIDTH = 640;
	public static final int MEETING_HEIGHT = 240;
	private Uri uri;

	private Context mContext;

	public ImageCrop(Context context) {
		mContext = context;
	}

	public Bitmap decodeWithIntent(Intent intent) {
		Bitmap photo = null;
		if (intent != null) {
			photo = intent.getParcelableExtra("data");
			if (photo != null) {
				return photo;
			}
		}
		return decodeUriAsBitmap();
	}

	public Bitmap decodeUriAsBitmap() {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		this.uri = uri;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		((Activity) mContext).startActivityForResult(intent, requestCode);
	}

	public static Uri creatUri(String path) {
		return Uri.fromFile(new File(path));
	}

}
