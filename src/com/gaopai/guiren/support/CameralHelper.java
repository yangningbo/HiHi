package com.gaopai.guiren.support;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.activity.RotateImageActivity;
import com.gaopai.guiren.utils.Logger;

public class CameralHelper {
	public static String TEMP_FILE_NAME = "header.jpg";
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	public static final int REQUEST_ROTATE_IMAGE = 1003;

	public static void getImageFromCamera(Activity activity) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					TEMP_FILE_NAME);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			activity.startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				Cursor cursor = activity.getContentResolver().query(uri, new String[] { MediaColumns.DATA }, null,
						null, null);
				if (null == cursor) {
					return;
				}
				cursor.moveToFirst();
				String path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
				String extension = path.substring(path.lastIndexOf("."), path.length());
				if (FeatureFunction.isPic(extension)) {
					// sendPicFile(MessageType.PICTURE, path);
				}
			}
			return;
		} else {
			String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
					+ TEMP_FILE_NAME;
			Logger.d(activity, "path=" + path);
			String extension = path.substring(path.indexOf("."), path.length());
			if (FeatureFunction.isPic(extension)) {

				Intent intent = new Intent();
				intent.putExtra(RotateImageActivity.KEY_IMAGE_PATH, path);
				intent.setClass(activity, RotateImageActivity.class);
				activity.startActivityForResult(intent, REQUEST_ROTATE_IMAGE);
			}
		}
	}
}
