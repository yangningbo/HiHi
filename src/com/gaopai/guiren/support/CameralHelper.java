package com.gaopai.guiren.support;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.gaopai.guiren.utils.Logger;

public class CameralHelper {
	public static String TEMP_FILE_NAME = "header.jpg";
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	public static final int REQUEST_ROTATE_IMAGE = 1003;

	private Activity mContext;
	private Option option;
	private ImageCrop imageCrop;

	public CameralHelper(Activity context) {
		this.mContext = context;
		imageCrop = new ImageCrop(mContext);
		option = new Option();
	}

	public CameralHelper(Activity context, Option option) {
		this.mContext = context;
		imageCrop = new ImageCrop(mContext);
		this.option = option;
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
		if (!TextUtils.isEmpty(title)) {
			dialog = builder.setTitle(title).create();
		} else {
			dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void btnCameraAction() {
		cropPath = "";
		getImageFromCamera();
	}

	public void btnPhotoAction() {
		cropPath = "";
		getImageFromGallery();
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
		intent.putExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE, option.getPicNum());
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
					Logger.d(this, "pic=" + path);
					callback.receiveOriginPic(path);
					if (option.isCrop) {
						cropImage(path);
					}
				}
			}
			break;

		case REQUEST_GET_BITMAP_LIST:
			if (resultCode == Activity.RESULT_OK) {
				List<String> pathList = data.getStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST);
				if (pathList != null && pathList.size() > 0) {
					callback.receiveOriginPicList(pathList);
					if (option.isCrop) {
						cropImage(pathList.get(0));
					}
				}
			}
			break;
		case ImageCrop.REQUEST_CROP_IMG:
			Bitmap photo = imageCrop.decodeWithIntent(data);
			callback.receiveCropPic(cropPath);
			callback.receiveCropBitmap(photo);
			break;
		}
	}
	
	public void retriveUri(Uri uri) {
		imageCrop.retriveUri(uri);
	}
	
	public Uri getUri() {
		return imageCrop.getUri();
	}

	private String cropPath;

	private void cropImage(String path) {
		cropPath = path;
		imageCrop.cropImageUri(ImageCrop.creatUri(path), option.cropWidth, option.cropHeight,
				ImageCrop.REQUEST_CROP_IMG);
	}

	private GetImageCallback callback;

	public void setCallback(GetImageCallback callback) {
		if (this.callback != callback) {
			this.callback = callback;
		}
	}

	public void setOption(Option option) {
		if (this.option != option) {
			this.option = option;
		}
	}

	public static interface GetImageCallback {
		public void receiveOriginPic(String path);

		public void receiveCropPic(String path);

		public void receiveCropBitmap(Bitmap bitmap);

		public void receiveOriginPicList(List<String> pathList);
	}

	public static class SimpleCallback implements GetImageCallback {

		@Override
		public void receiveOriginPic(String path) {
		}

		@Override
		public void receiveCropPic(String path) {

		}

		@Override
		public void receiveCropBitmap(Bitmap bitmap) {

		}

		@Override
		public void receiveOriginPicList(List<String> pathList) {

		}

	}

	public static class Option {
		private int picNum;
		public boolean isCrop;
		public int cropWidth;
		public int cropHeight;

		public Option() {
			picNum = 9;
			isCrop = false;
			cropWidth = 100;
			cropHeight = 100;
		}

		public Option(int picNum, boolean isCrop, int cropWidth, int cropHeight) {
			this.picNum = picNum;
			this.isCrop = isCrop;
			this.cropWidth = cropWidth;
			this.cropHeight = cropHeight;
		}

		public int getPicNum() {
			if (isCrop) {
				return 1;
			}
			return picNum;

		}
	}
}
