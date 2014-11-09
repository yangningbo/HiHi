package com.gaopai.guiren.activity;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;

/**
 * 旋转图片界面
 * 
 */
public class RotateImageActivity extends BaseActivity implements OnClickListener {

	public static final String KEY_IMAGE_PATH = "image_path";
	private static final int MSG_SHOW_IMAGE = 11103;

	private LinearLayout mBottomToolBar;
	private ImageView mImageView;
	private ImageView mRotateLeft;
	private ImageView mRotateRight;
	private boolean isLoaded = false;
	private Bitmap mBitmap;
	private String mImageFilePath;
	private RelativeLayout mRelativeLayout;
	final GetterHandler mAnimHandler = new GetterHandler();
	private File mFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_rotateimage);
		initComponent();
	}

	private void initComponent() {
		mImageFilePath = getIntent().getStringExtra(KEY_IMAGE_PATH);
		mImageView = (ImageView) findViewById(R.id.imageview);
		mImageView.setOnClickListener(this);
		if (mImageFilePath != null && !mImageFilePath.equals("")) {
			mFile = new File(mImageFilePath);
			ShowBitmap();
		}
		mBottomToolBar = (LinearLayout) findViewById(R.id.imageviewer_toolbar);
		// mBottomToolBar.setVisibility(View.GONE);
		mRotateLeft = (ImageView) findViewById(R.id.imageviewer_imageview_rotateleft);
		mRotateRight = (ImageView) findViewById(R.id.imageviewer_imageview_rotateright);
		mRotateLeft.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRotateRight.setEnabled(true);
				if (mImageView == null || !isLoaded) {
					return;
				}
				// mRotation = mRotation - 90;
				mBitmap = rotate(mBitmap, -90);
				mImageView.setImageBitmap(mBitmap);
				mImageView.invalidate();
			}
		});

		mRotateRight.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRotateLeft.setEnabled(true);
				if (mImageView == null || !isLoaded) {
					return;
				}
				// mRotation = mRotation + 90;
				mBitmap = rotate(mBitmap, 90);
				mImageView.setImageBitmap(mBitmap);
				mImageView.invalidate();
			}
		});
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.rotate_image);
		View ivComplete = mTitleBar.addRightImageView(R.drawable.title_complete_btn);
		ivComplete.setId(R.id.ab_complete);
		ivComplete.setOnClickListener(this);

		mRelativeLayout = (RelativeLayout) findViewById(R.id.showZoomInOutLayout);
		mRelativeLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					showOnScreenControls();
					scheduleDismissOnScreenControls();
					break;

				case MotionEvent.ACTION_MOVE:
					break;

				case MotionEvent.ACTION_UP:
					break;

				default:
					break;
				}
				return false;
			}
		});

	}

	public Bitmap rotate(Bitmap bmp, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
		if (bmp != null && !bmp.isRecycled() && bmp != bm) {
			bmp.recycle();
		}
		bmp = bm;
		return bmp;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_SHOW_IMAGE:
				if (mBitmap != null) {
					mImageView.setImageBitmap(mBitmap);
					isLoaded = true;
				}
				removeProgressDialog();
				break;

			default:
				break;
			}

		}
	};

	class GetterHandler extends Handler {
		private static final int IMAGE_GETTER_CALLBACK = 1;

		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case IMAGE_GETTER_CALLBACK:
				((Runnable) message.obj).run();
				break;
			}
		}

		public void postGetterCallback(Runnable callback) {
			postDelayedGetterCallback(callback, 0);
		}

		public void postDelayedGetterCallback(Runnable callback, long delay) {
			if (callback == null) {
				throw new NullPointerException();
			}
			Message message = Message.obtain();
			message.what = IMAGE_GETTER_CALLBACK;
			message.obj = callback;
			sendMessageDelayed(message, delay);
		}

		public void removeAllGetterCallbacks() {
			removeMessages(IMAGE_GETTER_CALLBACK);
		}
	}

	private final Runnable mDismissOnScreenControlRunner = new Runnable() {
		@Override
		public void run() {
			hideOnScreenControls();
		}
	};

	@Override
	protected void onDestroy() {
		if (mImageView != null) {
			mImageView.setImageBitmap(null);
		}
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		super.onDestroy();
	}

	private void ShowBitmap() {

		new Thread() {
			@Override
			public void run() {
				Message message = new Message();
				showProgressDialog(R.string.add_more_loading);
				mBitmap = scalePicture(mImageFilePath);
				mHandler.sendEmptyMessage(MSG_SHOW_IMAGE);
			}
		}.start();
	}

	private Bitmap scalePicture(String filename) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(filename, opt);
			int picWidth = opt.outWidth;
			int picHeight = opt.outHeight;

			int width = 1024;

			// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
			opt.inSampleSize = 1;
			if (picWidth > picHeight) {
				if (picWidth > width) {
					opt.inSampleSize = picWidth / width;
				}
			} else {
				if (picHeight > width) {
					opt.inSampleSize = picHeight / width;
				}
			}

			// 这次再真正地生成一个有像素的，经过缩放了的bitmap
			opt.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filename, opt);
			int afterwidth = bitmap.getWidth();
			int afterheight = bitmap.getHeight();
			float con = 1.0f;
			int bigger = afterheight > afterwidth ? afterheight : afterwidth;
			if (bigger > width) {
				con = (float) width / bigger;
			}

			bitmap = Bitmap.createScaledBitmap(bitmap, (int) (con * afterwidth), (int) (con * afterheight), true);
			mImageFilePath = FeatureFunction.saveTempBitmap(bitmap, mFile.getName());
		} catch (Exception e) {
			// TODO: handle exception]
			e.printStackTrace();
		}
		return bitmap;
	}

	private void hideOnScreenControls() {

		if (mBottomToolBar.getVisibility() == View.VISIBLE) {
			Animation a = new AlphaAnimation(1, 0);
			;
			a.setDuration(500);
			mBottomToolBar.startAnimation(a);
			mBottomToolBar.setVisibility(View.INVISIBLE);
		}

	}

	private void showOnScreenControls() {
		// if (mPaused) return;
		// If the view has not been attached to the window yet, the
		// zoomButtonControls will not able to show up. So delay it until the
		// view has attached to window.
		if (mBottomToolBar.getVisibility() != View.VISIBLE) {
			Animation animation = new AlphaAnimation(0, 1);
			animation.setDuration(500);
			mBottomToolBar.startAnimation(animation);
			mBottomToolBar.setVisibility(View.VISIBLE);
		}
	}

	private void scheduleDismissOnScreenControls() {
		mHandler.removeCallbacks(mDismissOnScreenControlRunner);
		mHandler.postDelayed(mDismissOnScreenControlRunner, 2000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_complete:
			showProgressDialog(R.string.add_more_loading);
			mImageFilePath = FeatureFunction.saveTempBitmap(mBitmap, mFile.getName());
			removeProgressDialog();
			Intent intent = new Intent();
			intent.putExtra(KEY_IMAGE_PATH, mImageFilePath);
			setResult(RESULT_OK, intent);
			RotateImageActivity.this.finish();
			break;

		case R.id.imageview:
			showOnScreenControls();
			scheduleDismissOnScreenControls();
			break;
		default:
			break;
		}
	}

}
