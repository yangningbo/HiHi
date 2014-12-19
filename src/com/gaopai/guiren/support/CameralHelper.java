package com.gaopai.guiren.support;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.view.Window;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.LocalPicActivity;
import com.gaopai.guiren.activity.LocalPicPathActivity;
import com.gaopai.guiren.activity.RotateImageActivity;

public class CameralHelper {
	public static String TEMP_FILE_NAME = "header.jpg";
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	public static final int REQUEST_ROTATE_IMAGE = 1003;

	private Activity mContext;

	public CameralHelper(Activity context) {
		this.mContext = context;
	}

	public void showDefaultSelectDialog(String title) {
		String[] array = new String[2];
		array[0] = mContext.getString(R.string.camera);
		array[1] = mContext.getString(R.string.gallery);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setItems(array,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							btnCameraAction();
						} else {
							btnPhotoAction();
						}
					}
				});
		Dialog dialog;
		if (TextUtils.isEmpty(title)) {
			dialog = builder.setTitle(title).create();
		} else {
			dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		dialog.show();
	}

	protected void btnCameraAction() {
		getImageFromCamera();
	}

	protected void btnPhotoAction() {
		getImageFromGallery();
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// TEMP_FILE_NAME = FeatureFunction.getPhotoFileName();

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					TEMP_FILE_NAME);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			mContext.startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	public static final int REQUEST_GET_BITMAP_LIST = 125;

	private void getImageFromGallery() {
		Intent intent = new Intent();
		intent.putExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE, LocalPicPathActivity.PIC_REQUIRE_MUTI);
		intent.setClass(mContext, LocalPicPathActivity.class);
		mContext.startActivityForResult(intent, REQUEST_GET_BITMAP_LIST);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					if (!TextUtils.isEmpty(uri.getAuthority())) {
						Cursor cursor = mContext.getContentResolver().query(uri, new String[] { MediaColumns.DATA },
								null, null, null);
						if (null == cursor) {
							return;
						}
						cursor.moveToFirst();
						String path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
						String extension = path.substring(path.lastIndexOf("."), path.length());
						if (FeatureFunction.isPic(extension)) {
						}
					}
					return;
				} else {
					String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
							+ TEMP_FILE_NAME;
					String extension = path.substring(path.indexOf("."), path.length());
					if (FeatureFunction.isPic(extension)) {
						Intent intent = new Intent();
						intent.putExtra(RotateImageActivity.KEY_IMAGE_PATH, path);
						intent.setClass(mContext, RotateImageActivity.class);
						mContext.startActivityForResult(intent, REQUEST_ROTATE_IMAGE);
					}
				}

			}
			break;
		case REQUEST_ROTATE_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				String path = data.getStringExtra(RotateImageActivity.KEY_IMAGE_PATH);
				if (!TextUtils.isEmpty(path)) {
					callback.receivePic(path);
				}
			}
			break;

		case REQUEST_GET_BITMAP_LIST:
			if (resultCode == Activity.RESULT_OK) {
				List<String> pathList = data.getStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST);
				callback.receivePicList(pathList);
			}
			break;
		}
	}
	
	private GetImageCallback callback;
	
	public void setCallback(GetImageCallback callback) {
		if (this.callback != callback) {
			this.callback = callback;
		}
		
	}
	
	public static interface GetImageCallback {
		public void receivePic(String path);
		public void receivePicList(List<String> pathList);
	}
}
